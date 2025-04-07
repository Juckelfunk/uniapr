// 
// Decompiled by Procyon v0.5.36
// 

package org.codehaus.plexus.component.configurator.expression;

import java.io.File;

public interface ExpressionEvaluator
{
    Object evaluate(final String p0) throws ExpressionEvaluationException;
    
    File alignToBaseDirectory(final File p0);
}
