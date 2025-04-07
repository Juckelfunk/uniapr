// 
// Decompiled by Procyon v0.5.36
// 

package org.pitest.junit.adapter;

import org.junit.runner.notification.StoppedByUserException;
import org.junit.runner.notification.Failure;
import org.pitest.testapi.ResultCollector;
import org.pitest.testapi.Description;
import org.junit.runner.notification.RunListener;

class AdaptingRunListener extends RunListener
{
    private final Description description;
    private final ResultCollector rc;
    private boolean failed;
    
    AdaptingRunListener(final Description description, final ResultCollector rc) {
        this.failed = false;
        this.description = description;
        this.rc = rc;
    }
    
    @Override
    public void testFailure(final Failure failure) throws Exception {
        this.rc.notifyEnd(this.description, failure.getException());
        this.failed = true;
    }
    
    @Override
    public void testAssumptionFailure(final Failure failure) {
    }
    
    @Override
    public void testIgnored(final org.junit.runner.Description description) throws Exception {
        this.rc.notifySkipped(this.description);
    }
    
    @Override
    public void testStarted(final org.junit.runner.Description description) throws Exception {
        if (this.failed) {
            throw new StoppedByUserException();
        }
        this.rc.notifyStart(this.description);
    }
    
    @Override
    public void testFinished(final org.junit.runner.Description description) throws Exception {
        if (!this.failed) {
            this.rc.notifyEnd(this.description);
        }
    }
}
