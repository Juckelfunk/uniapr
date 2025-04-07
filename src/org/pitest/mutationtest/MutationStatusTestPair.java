// 
// Decompiled by Procyon v0.5.36
// 

package org.pitest.mutationtest;

import org.pitest.functional.Option;
import java.io.Serializable;

public final class MutationStatusTestPair implements Serializable
{
    private static final long serialVersionUID = 1L;
    private final int numberOfTestsRun;
    private final DetectionStatus status;
    private final Option<String> killingTest;
    
    public MutationStatusTestPair(final int numberOfTestsRun, final DetectionStatus status) {
        this(numberOfTestsRun, status, null);
    }
    
    public MutationStatusTestPair(final int numberOfTestsRun, final DetectionStatus status, final String killingTest) {
        this.status = status;
        this.killingTest = Option.some(killingTest);
        this.numberOfTestsRun = numberOfTestsRun;
    }
    
    public DetectionStatus getStatus() {
        return this.status;
    }
    
    public Option<String> getKillingTest() {
        return this.killingTest;
    }
    
    public int getNumberOfTestsRun() {
        return this.numberOfTestsRun;
    }
    
    @Override
    public String toString() {
        if (this.killingTest.hasNone()) {
            return this.status.name();
        }
        return this.status.name() + " by " + this.killingTest.value();
    }
    
    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = 31 * result + ((this.killingTest == null) ? 0 : this.killingTest.hashCode());
        result = 31 * result + this.numberOfTestsRun;
        result = 31 * result + ((this.status == null) ? 0 : this.status.hashCode());
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
        final MutationStatusTestPair other = (MutationStatusTestPair)obj;
        if (this.killingTest == null) {
            if (other.killingTest != null) {
                return false;
            }
        }
        else if (!this.killingTest.equals(other.killingTest)) {
            return false;
        }
        return this.numberOfTestsRun == other.numberOfTestsRun && this.status == other.status;
    }
}
