// 
// Decompiled by Procyon v0.5.36
// 

package org.codehaus.plexus.interpolation;

import java.util.Iterator;
import java.util.regex.Matcher;
import org.codehaus.plexus.interpolation.util.StringUtils;
import java.util.Collection;
import java.util.regex.Pattern;
import java.util.WeakHashMap;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class RegexBasedInterpolator implements Interpolator
{
    private String startRegex;
    private String endRegex;
    private Map existingAnswers;
    private List valueSources;
    private List postProcessors;
    private boolean reusePatterns;
    private boolean cacheAnswers;
    public static final String DEFAULT_REGEXP = "\\$\\{(.+?)\\}";
    private Map compiledPatterns;
    
    public RegexBasedInterpolator() {
        this.existingAnswers = new HashMap();
        this.valueSources = new ArrayList();
        this.postProcessors = new ArrayList();
        this.reusePatterns = false;
        this.cacheAnswers = false;
        (this.compiledPatterns = new WeakHashMap()).put("\\$\\{(.+?)\\}", Pattern.compile("\\$\\{(.+?)\\}"));
    }
    
    public RegexBasedInterpolator(final boolean reusePatterns) {
        this();
        this.reusePatterns = reusePatterns;
    }
    
    public RegexBasedInterpolator(final String startRegex, final String endRegex) {
        this();
        this.startRegex = startRegex;
        this.endRegex = endRegex;
    }
    
    public RegexBasedInterpolator(final List valueSources) {
        this();
        this.valueSources.addAll(valueSources);
    }
    
    public RegexBasedInterpolator(final String startRegex, final String endRegex, final List valueSources) {
        this();
        this.startRegex = startRegex;
        this.endRegex = endRegex;
        this.valueSources.addAll(valueSources);
    }
    
    public void addValueSource(final ValueSource valueSource) {
        this.valueSources.add(valueSource);
    }
    
    public void removeValuesSource(final ValueSource valueSource) {
        this.valueSources.remove(valueSource);
    }
    
    public void addPostProcessor(final InterpolationPostProcessor postProcessor) {
        this.postProcessors.add(postProcessor);
    }
    
    public void removePostProcessor(final InterpolationPostProcessor postProcessor) {
        this.postProcessors.remove(postProcessor);
    }
    
    public String interpolate(final String input, String thisPrefixPattern, RecursionInterceptor recursionInterceptor) throws InterpolationException {
        if (input == null) {
            return "";
        }
        if (recursionInterceptor == null) {
            recursionInterceptor = new SimpleRecursionInterceptor();
        }
        if (thisPrefixPattern != null && thisPrefixPattern.length() == 0) {
            thisPrefixPattern = null;
        }
        int realExprGroup = 2;
        Pattern expressionPattern = null;
        if (this.startRegex != null || this.endRegex != null) {
            if (thisPrefixPattern == null) {
                expressionPattern = this.getPattern(this.startRegex + this.endRegex);
                realExprGroup = 1;
            }
            else {
                expressionPattern = this.getPattern(this.startRegex + thisPrefixPattern + this.endRegex);
            }
        }
        else if (thisPrefixPattern != null) {
            expressionPattern = this.getPattern("\\$\\{(" + thisPrefixPattern + ")?(.+?)\\}");
        }
        else {
            expressionPattern = this.getPattern("\\$\\{(.+?)\\}");
            realExprGroup = 1;
        }
        try {
            return this.interpolate(input, recursionInterceptor, expressionPattern, realExprGroup);
        }
        finally {
            if (!this.cacheAnswers) {
                this.clearAnswers();
            }
        }
    }
    
    private Pattern getPattern(final String regExp) {
        if (!this.reusePatterns) {
            return Pattern.compile(regExp);
        }
        Pattern pattern = null;
        synchronized (this) {
            pattern = this.compiledPatterns.get(regExp);
            if (pattern != null) {
                return pattern;
            }
            pattern = Pattern.compile(regExp);
            this.compiledPatterns.put(regExp, pattern);
        }
        return pattern;
    }
    
    private String interpolate(final String input, final RecursionInterceptor recursionInterceptor, final Pattern expressionPattern, final int realExprGroup) throws InterpolationException {
        if (input == null) {
            return "";
        }
        String result = input;
        final Matcher matcher = expressionPattern.matcher(result);
        while (matcher.find()) {
            final String wholeExpr = matcher.group(0);
            String realExpr = matcher.group(realExprGroup);
            if (realExpr.startsWith(".")) {
                realExpr = realExpr.substring(1);
            }
            if (recursionInterceptor.hasRecursiveExpression(realExpr)) {
                throw new InterpolationException("Detected the following recursive expression cycle: " + recursionInterceptor.getExpressionCycle(realExpr), wholeExpr);
            }
            recursionInterceptor.expressionResolutionStarted(realExpr);
            Object value = this.existingAnswers.get(realExpr);
            ValueSource vs;
            for (Iterator it = this.valueSources.iterator(); it.hasNext() && value == null; value = vs.getValue(realExpr)) {
                vs = it.next();
            }
            if (value != null) {
                value = this.interpolate(String.valueOf(value), recursionInterceptor, expressionPattern, realExprGroup);
                if (this.postProcessors != null && !this.postProcessors.isEmpty()) {
                    for (final InterpolationPostProcessor postProcessor : this.postProcessors) {
                        final Object newVal = postProcessor.execute(realExpr, value);
                        if (newVal != null) {
                            value = newVal;
                            break;
                        }
                    }
                }
                result = StringUtils.replace(result, wholeExpr, String.valueOf(value));
                matcher.reset(result);
            }
            recursionInterceptor.expressionResolutionFinished(realExpr);
        }
        return result;
    }
    
    public List getFeedback() {
        final List messages = new ArrayList();
        for (final ValueSource vs : this.valueSources) {
            final List feedback = vs.getFeedback();
            if (feedback != null && !feedback.isEmpty()) {
                messages.addAll(feedback);
            }
        }
        return messages;
    }
    
    public void clearFeedback() {
        for (final ValueSource vs : this.valueSources) {
            vs.clearFeedback();
        }
    }
    
    public String interpolate(final String input, final String thisPrefixPattern) throws InterpolationException {
        return this.interpolate(input, thisPrefixPattern, null);
    }
    
    public String interpolate(final String input) throws InterpolationException {
        return this.interpolate(input, null, null);
    }
    
    public String interpolate(final String input, final RecursionInterceptor recursionInterceptor) throws InterpolationException {
        return this.interpolate(input, null, recursionInterceptor);
    }
    
    public boolean isReusePatterns() {
        return this.reusePatterns;
    }
    
    public void setReusePatterns(final boolean reusePatterns) {
        this.reusePatterns = reusePatterns;
    }
    
    public boolean isCacheAnswers() {
        return this.cacheAnswers;
    }
    
    public void setCacheAnswers(final boolean cacheAnswers) {
        this.cacheAnswers = cacheAnswers;
    }
    
    public void clearAnswers() {
        this.existingAnswers.clear();
    }
}
