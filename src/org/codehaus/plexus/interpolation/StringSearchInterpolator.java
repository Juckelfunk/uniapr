// 
// Decompiled by Procyon v0.5.36
// 

package org.codehaus.plexus.interpolation;

import java.util.Collection;
import java.util.Iterator;
import java.util.Set;
import java.util.HashSet;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class StringSearchInterpolator implements Interpolator
{
    private Map existingAnswers;
    private List valueSources;
    private List postProcessors;
    private boolean cacheAnswers;
    public static final String DEFAULT_START_EXPR = "${";
    public static final String DEFAULT_END_EXPR = "}";
    private String startExpr;
    private String endExpr;
    private String escapeString;
    
    public StringSearchInterpolator() {
        this.existingAnswers = new HashMap();
        this.valueSources = new ArrayList();
        this.postProcessors = new ArrayList();
        this.cacheAnswers = false;
        this.startExpr = "${";
        this.endExpr = "}";
    }
    
    public StringSearchInterpolator(final String startExpr, final String endExpr) {
        this.existingAnswers = new HashMap();
        this.valueSources = new ArrayList();
        this.postProcessors = new ArrayList();
        this.cacheAnswers = false;
        this.startExpr = startExpr;
        this.endExpr = endExpr;
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
    
    public String interpolate(final String input, final String thisPrefixPattern) throws InterpolationException {
        throw new UnsupportedOperationException("Regular expressions are not supported in this Interpolator implementation.");
    }
    
    public String interpolate(final String input, final String thisPrefixPattern, final RecursionInterceptor recursionInterceptor) throws InterpolationException {
        throw new UnsupportedOperationException("Regular expressions are not supported in this Interpolator implementation.");
    }
    
    public String interpolate(final String input) throws InterpolationException {
        return this.interpolate(input, new SimpleRecursionInterceptor());
    }
    
    public String interpolate(final String input, final RecursionInterceptor recursionInterceptor) throws InterpolationException {
        try {
            return this.interpolate(input, recursionInterceptor, new HashSet());
        }
        finally {
            if (!this.cacheAnswers) {
                this.existingAnswers.clear();
            }
        }
    }
    
    private String interpolate(final String input, final RecursionInterceptor recursionInterceptor, final Set unresolvable) throws InterpolationException {
        if (input == null) {
            return "";
        }
        final StringBuffer result = new StringBuffer(input.length() * 2);
        int startIdx = -1;
        int endIdx = -1;
        while ((startIdx = input.indexOf(this.startExpr, endIdx + 1)) > -1) {
            result.append(input.substring(endIdx + 1, startIdx));
            endIdx = input.indexOf(this.endExpr, startIdx + 1);
            if (endIdx < 0) {
                break;
            }
            final String wholeExpr = input.substring(startIdx, endIdx + this.endExpr.length());
            String realExpr = wholeExpr.substring(this.startExpr.length(), wholeExpr.length() - this.endExpr.length());
            if (startIdx >= 0 && this.escapeString != null && this.escapeString.length() > 0) {
                final int startEscapeIdx = (startIdx == 0) ? 0 : (startIdx - this.escapeString.length());
                if (startEscapeIdx >= 0) {
                    final String escape = input.substring(startEscapeIdx, startIdx);
                    if (escape != null && this.escapeString.equals(escape)) {
                        result.append(wholeExpr);
                        result.replace(startEscapeIdx, startEscapeIdx + this.escapeString.length(), "");
                        continue;
                    }
                }
            }
            boolean resolved = false;
            if (!unresolvable.contains(wholeExpr)) {
                if (realExpr.startsWith(".")) {
                    realExpr = realExpr.substring(1);
                }
                if (recursionInterceptor.hasRecursiveExpression(realExpr)) {
                    throw new InterpolationCycleException(recursionInterceptor, realExpr, wholeExpr);
                }
                recursionInterceptor.expressionResolutionStarted(realExpr);
                Object value = this.existingAnswers.get(realExpr);
                Object bestAnswer = null;
                for (Iterator it = this.valueSources.iterator(); it.hasNext() && value == null; value = null) {
                    final ValueSource vs = it.next();
                    value = vs.getValue(realExpr);
                    if (value != null && value.toString().indexOf(wholeExpr) > -1) {
                        bestAnswer = value;
                    }
                }
                if (value == null && bestAnswer != null) {
                    throw new InterpolationCycleException(recursionInterceptor, realExpr, wholeExpr);
                }
                if (value != null) {
                    value = this.interpolate(String.valueOf(value), recursionInterceptor, unresolvable);
                    if (this.postProcessors != null && !this.postProcessors.isEmpty()) {
                        for (final InterpolationPostProcessor postProcessor : this.postProcessors) {
                            final Object newVal = postProcessor.execute(realExpr, value);
                            if (newVal != null) {
                                value = newVal;
                                break;
                            }
                        }
                    }
                    result.append(String.valueOf(value));
                    resolved = true;
                }
                else {
                    unresolvable.add(wholeExpr);
                }
                recursionInterceptor.expressionResolutionFinished(realExpr);
            }
            if (!resolved) {
                result.append(wholeExpr);
            }
            if (endIdx <= -1) {
                continue;
            }
            endIdx += this.endExpr.length() - 1;
        }
        if (endIdx == -1 && startIdx > -1) {
            result.append(input.substring(startIdx, input.length()));
        }
        else if (endIdx < input.length()) {
            result.append(input.substring(endIdx + 1, input.length()));
        }
        return result.toString();
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
    
    public boolean isCacheAnswers() {
        return this.cacheAnswers;
    }
    
    public void setCacheAnswers(final boolean cacheAnswers) {
        this.cacheAnswers = cacheAnswers;
    }
    
    public void clearAnswers() {
        this.existingAnswers.clear();
    }
    
    public String getEscapeString() {
        return this.escapeString;
    }
    
    public void setEscapeString(final String escapeString) {
        this.escapeString = escapeString;
    }
}
