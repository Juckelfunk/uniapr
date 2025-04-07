// 
// Decompiled by Procyon v0.5.36
// 

package org.codehaus.plexus.interpolation;

import java.util.ArrayList;
import java.util.List;

public abstract class AbstractValueSource implements ValueSource
{
    private final List feedback;
    
    protected AbstractValueSource(final boolean usesFeedback) {
        if (usesFeedback) {
            this.feedback = new ArrayList();
        }
        else {
            this.feedback = null;
        }
    }
    
    public void clearFeedback() {
        if (this.feedback != null) {
            this.feedback.clear();
        }
    }
    
    public List getFeedback() {
        return this.feedback;
    }
    
    protected void addFeedback(final String message) {
        this.feedback.add(message);
    }
    
    protected void addFeedback(final String message, final Throwable cause) {
        this.feedback.add(message);
        this.feedback.add(cause);
    }
}
