// 
// Decompiled by Procyon v0.5.36
// 

package org.pitest.mutationtest;

import org.pitest.functional.Option;
import org.pitest.mutationtest.engine.MutationDetails;

public final class MutationResult
{
    private final MutationDetails details;
    private final MutationStatusTestPair status;
    
    public MutationResult(final MutationDetails md, final MutationStatusTestPair status) {
        this.details = md;
        this.status = status;
    }
    
    public MutationDetails getDetails() {
        return this.details;
    }
    
    public Option<String> getKillingTest() {
        return this.status.getKillingTest();
    }
    
    public DetectionStatus getStatus() {
        return this.status.getStatus();
    }
    
    public int getNumberOfTestsRun() {
        return this.status.getNumberOfTestsRun();
    }
    
    public MutationStatusTestPair getStatusTestPair() {
        return this.status;
    }
    
    public String getStatusDescription() {
        return this.getStatus().name();
    }
    
    public String getKillingTestDescription() {
        return this.getKillingTest().getOrElse("none");
    }
    
    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = 31 * result + ((this.details == null) ? 0 : this.details.hashCode());
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
        final MutationResult other = (MutationResult)obj;
        if (this.details == null) {
            if (other.details != null) {
                return false;
            }
        }
        else if (!this.details.equals(other.details)) {
            return false;
        }
        if (this.status == null) {
            if (other.status != null) {
                return false;
            }
        }
        else if (!this.status.equals(other.status)) {
            return false;
        }
        return true;
    }
    
    @Override
    public String toString() {
        return "MutationResult [details=" + this.details + ", status=" + this.status + "]";
    }
}
