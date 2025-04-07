// 
// Decompiled by Procyon v0.5.36
// 

package org.codehaus.plexus.interpolation;

import java.util.List;

public abstract class AbstractDelegatingValueSource implements ValueSource
{
    private final ValueSource delegate;
    
    protected AbstractDelegatingValueSource(final ValueSource delegate) {
        if (delegate == null) {
            throw new NullPointerException("Delegate ValueSource cannot be null.");
        }
        this.delegate = delegate;
    }
    
    protected ValueSource getDelegate() {
        return this.delegate;
    }
    
    public Object getValue(final String expression) {
        return this.getDelegate().getValue(expression);
    }
    
    public void clearFeedback() {
        this.delegate.clearFeedback();
    }
    
    public List getFeedback() {
        return this.delegate.getFeedback();
    }
}
