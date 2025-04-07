// 
// Decompiled by Procyon v0.5.36
// 

package org.codehaus.plexus.component.configurator.expression;

import java.io.File;

public class DefaultExpressionEvaluator implements ExpressionEvaluator
{
    public Object evaluate(final String expression) {
        return expression;
    }
    
    public File alignToBaseDirectory(final File file) {
        return file;
    }
}
