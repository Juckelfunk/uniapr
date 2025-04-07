// 
// Decompiled by Procyon v0.5.36
// 

package org.pitest.mutationtest.statistics;

import org.pitest.mutationtest.DetectionStatus;

public class StatusCount
{
    private final DetectionStatus status;
    private long count;
    
    StatusCount(final DetectionStatus status, final long count) {
        this.status = status;
        this.count = count;
    }
    
    void increment() {
        ++this.count;
    }
    
    @Override
    public String toString() {
        return "" + this.status + " " + this.count;
    }
    
    public long getCount() {
        return this.count;
    }
    
    public DetectionStatus getStatus() {
        return this.status;
    }
}
