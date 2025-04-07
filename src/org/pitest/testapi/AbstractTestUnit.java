// 
// Decompiled by Procyon v0.5.36
// 

package org.pitest.testapi;

public abstract class AbstractTestUnit implements TestUnit
{
    private final Description description;
    
    public AbstractTestUnit(final Description description) {
        this.description = description;
    }
    
    @Override
    public abstract void execute(final ResultCollector p0);
    
    @Override
    public final Description getDescription() {
        return this.description;
    }
}
