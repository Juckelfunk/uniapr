// 
// Decompiled by Procyon v0.5.36
// 

package org.codehaus.plexus.interpolation;

import java.util.Collections;
import java.util.List;
import org.codehaus.plexus.interpolation.util.ValueSourceUtils;
import java.util.Collection;
import java.util.Stack;

public class PrefixAwareRecursionInterceptor implements RecursionInterceptor
{
    public static final String DEFAULT_START_TOKEN = "\\$\\{";
    public static final String DEFAULT_END_TOKEN = "\\}";
    private Stack nakedExpressions;
    private final Collection possiblePrefixes;
    private String endToken;
    private String startToken;
    private boolean watchUnprefixedExpressions;
    
    public PrefixAwareRecursionInterceptor(final Collection possiblePrefixes, final boolean watchUnprefixedExpressions) {
        this.nakedExpressions = new Stack();
        this.endToken = "\\}";
        this.startToken = "\\$\\{";
        this.watchUnprefixedExpressions = true;
        this.possiblePrefixes = possiblePrefixes;
        this.watchUnprefixedExpressions = watchUnprefixedExpressions;
    }
    
    public PrefixAwareRecursionInterceptor(final Collection possiblePrefixes) {
        this.nakedExpressions = new Stack();
        this.endToken = "\\}";
        this.startToken = "\\$\\{";
        this.watchUnprefixedExpressions = true;
        this.possiblePrefixes = possiblePrefixes;
    }
    
    public boolean hasRecursiveExpression(final String expression) {
        final String realExpr = ValueSourceUtils.trimPrefix(expression, this.possiblePrefixes, this.watchUnprefixedExpressions);
        return realExpr != null && this.nakedExpressions.contains(realExpr);
    }
    
    public void expressionResolutionFinished(final String expression) {
        this.nakedExpressions.pop();
    }
    
    public void expressionResolutionStarted(final String expression) {
        final String realExpr = ValueSourceUtils.trimPrefix(expression, this.possiblePrefixes, this.watchUnprefixedExpressions);
        this.nakedExpressions.push(realExpr);
    }
    
    public List getExpressionCycle(final String expression) {
        final String expr = ValueSourceUtils.trimPrefix(expression, this.possiblePrefixes, this.watchUnprefixedExpressions);
        if (expr == null) {
            return Collections.EMPTY_LIST;
        }
        final int idx = this.nakedExpressions.indexOf(expression);
        if (idx < 0) {
            return Collections.EMPTY_LIST;
        }
        return this.nakedExpressions.subList(idx, this.nakedExpressions.size());
    }
}
