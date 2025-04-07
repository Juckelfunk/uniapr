// 
// Decompiled by Procyon v0.5.36
// 

package org.codehaus.plexus.interpolation;

public class InterpolationException extends Exception
{
    private static final long serialVersionUID = 1L;
    private final String expression;
    
    public InterpolationException(final String message, final String expression, final Throwable cause) {
        super(buildMessage(message, expression), cause);
        this.expression = expression;
    }
    
    public InterpolationException(final String message, final String expression) {
        super(buildMessage(message, expression));
        this.expression = expression;
    }
    
    private static String buildMessage(final String message, final String expression) {
        return "Resolving expression: '" + expression + "': " + message;
    }
    
    public String getExpression() {
        return this.expression;
    }
}
