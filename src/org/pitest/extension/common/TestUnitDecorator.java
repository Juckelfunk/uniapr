// 
// Decompiled by Procyon v0.5.36
// 

package org.pitest.extension.common;

import org.pitest.testapi.ResultCollector;
import org.pitest.testapi.Description;
import org.pitest.testapi.TestUnit;

public abstract class TestUnitDecorator implements TestUnit
{
    private final TestUnit child;
    
    protected TestUnitDecorator(final TestUnit child) {
        this.child = child;
    }
    
    @Override
    public Description getDescription() {
        return this.child.getDescription();
    }
    
    public TestUnit child() {
        return this.child;
    }
    
    @Override
    public abstract void execute(final ResultCollector p0);
}
