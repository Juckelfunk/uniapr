// 
// Decompiled by Procyon v0.5.36
// 

package org.pitest.testapi;

public final class TestResult
{
    private final Description description;
    private final Throwable throwable;
    private final TestUnitState state;
    
    public TestResult(final Description description, final Throwable t) {
        this(description, t, TestUnitState.FINISHED);
    }
    
    public TestResult(final Description description, final Throwable t, final TestUnitState state) {
        this.description = description;
        this.throwable = t;
        this.state = state;
    }
    
    public Throwable getThrowable() {
        return this.throwable;
    }
    
    public TestUnitState getState() {
        return this.state;
    }
    
    public Description getDescription() {
        return this.description;
    }
    
    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = 31 * result + ((this.description == null) ? 0 : this.description.hashCode());
        result = 31 * result + ((this.state == null) ? 0 : this.state.hashCode());
        result = 31 * result + ((this.throwable == null) ? 0 : this.throwable.hashCode());
        return result;
    }
    
    @Override
    public boolean equals(final Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (this.getClass() != obj.getClass()) {
            return false;
        }
        final TestResult other = (TestResult)obj;
        if (this.description == null) {
            if (other.description != null) {
                return false;
            }
        }
        else if (!this.description.equals(other.description)) {
            return false;
        }
        if (this.state == null) {
            if (other.state != null) {
                return false;
            }
        }
        else if (!this.state.equals(other.state)) {
            return false;
        }
        if (this.throwable == null) {
            if (other.throwable != null) {
                return false;
            }
        }
        else if (!this.throwable.equals(other.throwable)) {
            return false;
        }
        return true;
    }
    
    @Override
    public String toString() {
        return "TestResult [description=" + this.description + ", state=" + this.state + ", throwable=" + this.throwable + "]";
    }
}
