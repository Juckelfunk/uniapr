// 
// Decompiled by Procyon v0.5.36
// 

package org.codehaus.plexus.interpolation;

public class InterpolationCycleException extends InterpolationException
{
    private static final long serialVersionUID = 1L;
    
    public InterpolationCycleException(final RecursionInterceptor recursionInterceptor, final String realExpr, final String wholeExpr) {
        super("Detected the following recursive expression cycle: " + recursionInterceptor.getExpressionCycle(realExpr), wholeExpr);
    }
}
