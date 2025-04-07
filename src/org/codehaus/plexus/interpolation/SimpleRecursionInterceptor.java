// 
// Decompiled by Procyon v0.5.36
// 

package org.codehaus.plexus.interpolation;

import java.util.Collections;
import java.util.List;
import java.util.Stack;

public class SimpleRecursionInterceptor implements RecursionInterceptor
{
    private Stack expressions;
    
    public SimpleRecursionInterceptor() {
        this.expressions = new Stack();
    }
    
    public void expressionResolutionFinished(final String expression) {
        this.expressions.pop();
    }
    
    public void expressionResolutionStarted(final String expression) {
        this.expressions.push(expression);
    }
    
    public boolean hasRecursiveExpression(final String expression) {
        return this.expressions.contains(expression);
    }
    
    public List getExpressionCycle(final String expression) {
        final int idx = this.expressions.indexOf(expression);
        if (idx < 0) {
            return Collections.EMPTY_LIST;
        }
        return this.expressions.subList(idx, this.expressions.size());
    }
}
