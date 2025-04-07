// 
// Decompiled by Procyon v0.5.36
// 

package org.pitest.testapi.execute;

import org.pitest.testapi.Description;
import org.pitest.testapi.ResultCollector;

public class ExitingResultCollector implements ResultCollector
{
    private final ResultCollector child;
    private boolean hadFailure;
    
    public ExitingResultCollector(final ResultCollector child) {
        this.hadFailure = false;
        this.child = child;
    }
    
    @Override
    public void notifySkipped(final Description description) {
        this.child.notifySkipped(description);
    }
    
    @Override
    public void notifyStart(final Description description) {
        this.child.notifyStart(description);
    }
    
    @Override
    public boolean shouldExit() {
        return this.hadFailure;
    }
    
    @Override
    public void notifyEnd(final Description description, final Throwable t) {
        this.child.notifyEnd(description, t);
        if (t != null) {
            this.hadFailure = true;
        }
    }
    
    @Override
    public void notifyEnd(final Description description) {
        this.child.notifyEnd(description);
    }
}
