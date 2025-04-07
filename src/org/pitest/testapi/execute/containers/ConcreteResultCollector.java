// 
// Decompiled by Procyon v0.5.36
// 

package org.pitest.testapi.execute.containers;

import org.pitest.testapi.TestUnitState;
import org.pitest.testapi.Description;
import org.pitest.testapi.TestResult;
import java.util.Collection;
import org.pitest.testapi.ResultCollector;

public final class ConcreteResultCollector implements ResultCollector
{
    private final Collection<TestResult> feedback;
    
    public ConcreteResultCollector(final Collection<TestResult> feedback) {
        this.feedback = feedback;
    }
    
    @Override
    public void notifyStart(final Description tu) {
        this.put(new TestResult(tu, null, TestUnitState.STARTED));
    }
    
    @Override
    public void notifySkipped(final Description tu) {
        this.put(new TestResult(tu, null, TestUnitState.NOT_RUN));
    }
    
    @Override
    public void notifyEnd(final Description description, final Throwable t) {
        this.put(new TestResult(description, t));
    }
    
    @Override
    public void notifyEnd(final Description description) {
        this.put(new TestResult(description, null));
    }
    
    private void put(final TestResult tr) {
        this.feedback.add(tr);
    }
    
    @Override
    public boolean shouldExit() {
        return false;
    }
}
