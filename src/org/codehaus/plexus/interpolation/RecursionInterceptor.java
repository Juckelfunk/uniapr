// 
// Decompiled by Procyon v0.5.36
// 

package org.codehaus.plexus.interpolation;

import java.util.List;

public interface RecursionInterceptor
{
    void expressionResolutionStarted(final String p0);
    
    void expressionResolutionFinished(final String p0);
    
    boolean hasRecursiveExpression(final String p0);
    
    List getExpressionCycle(final String p0);
}
