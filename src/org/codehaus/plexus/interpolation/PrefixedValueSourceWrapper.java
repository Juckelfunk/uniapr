// 
// Decompiled by Procyon v0.5.36
// 

package org.codehaus.plexus.interpolation;

import java.util.Collection;
import org.codehaus.plexus.interpolation.util.ValueSourceUtils;
import java.util.Collections;
import java.util.List;

public class PrefixedValueSourceWrapper implements FeedbackEnabledValueSource, QueryEnabledValueSource
{
    private final ValueSource valueSource;
    private final List possiblePrefixes;
    private boolean allowUnprefixedExpressions;
    private String lastExpression;
    
    public PrefixedValueSourceWrapper(final ValueSource valueSource, final String prefix) {
        this.valueSource = valueSource;
        this.possiblePrefixes = Collections.singletonList(prefix);
    }
    
    public PrefixedValueSourceWrapper(final ValueSource valueSource, final String prefix, final boolean allowUnprefixedExpressions) {
        this.valueSource = valueSource;
        this.possiblePrefixes = Collections.singletonList(prefix);
        this.allowUnprefixedExpressions = allowUnprefixedExpressions;
    }
    
    public PrefixedValueSourceWrapper(final ValueSource valueSource, final List possiblePrefixes) {
        this.valueSource = valueSource;
        this.possiblePrefixes = possiblePrefixes;
    }
    
    public PrefixedValueSourceWrapper(final ValueSource valueSource, final List possiblePrefixes, final boolean allowUnprefixedExpressions) {
        this.valueSource = valueSource;
        this.possiblePrefixes = possiblePrefixes;
        this.allowUnprefixedExpressions = allowUnprefixedExpressions;
    }
    
    public Object getValue(final String expression) {
        this.lastExpression = ValueSourceUtils.trimPrefix(expression, this.possiblePrefixes, this.allowUnprefixedExpressions);
        if (this.lastExpression == null) {
            return null;
        }
        return this.valueSource.getValue(this.lastExpression);
    }
    
    public List getFeedback() {
        return (this.valueSource instanceof FeedbackEnabledValueSource) ? ((FeedbackEnabledValueSource)this.valueSource).getFeedback() : Collections.EMPTY_LIST;
    }
    
    public String getLastExpression() {
        return (this.valueSource instanceof QueryEnabledValueSource) ? ((QueryEnabledValueSource)this.valueSource).getLastExpression() : this.lastExpression;
    }
    
    public void clearFeedback() {
        this.valueSource.clearFeedback();
    }
}
