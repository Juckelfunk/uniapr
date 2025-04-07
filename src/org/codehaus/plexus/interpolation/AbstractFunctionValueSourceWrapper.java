// 
// Decompiled by Procyon v0.5.36
// 

package org.codehaus.plexus.interpolation;

public abstract class AbstractFunctionValueSourceWrapper implements ValueSource
{
    private final ValueSource valueSource;
    
    protected AbstractFunctionValueSourceWrapper(final ValueSource valueSource) {
        this.valueSource = valueSource;
    }
    
    public Object getValue(final String expression) {
        final Object value = this.valueSource.getValue(expression);
        String expr = expression;
        if (this.valueSource instanceof QueryEnabledValueSource) {
            expr = ((QueryEnabledValueSource)this.valueSource).getLastExpression();
        }
        return this.executeFunction(expr, value);
    }
    
    protected ValueSource getValueSource() {
        return this.valueSource;
    }
    
    protected abstract Object executeFunction(final String p0, final Object p1);
}
