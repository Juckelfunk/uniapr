// 
// Decompiled by Procyon v0.5.36
// 

package org.codehaus.plexus.interpolation;

import org.codehaus.plexus.interpolation.reflection.ReflectionValueExtractor;

public class ObjectBasedValueSource extends AbstractValueSource
{
    private final Object root;
    
    public ObjectBasedValueSource(final Object root) {
        super(true);
        this.root = root;
    }
    
    public Object getValue(final String expression) {
        try {
            return ReflectionValueExtractor.evaluate(expression, this.root, false);
        }
        catch (Exception e) {
            this.addFeedback("Failed to extract '" + expression + "' from: " + this.root, e);
            return null;
        }
    }
}
