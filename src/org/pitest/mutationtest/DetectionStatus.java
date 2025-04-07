// 
// Decompiled by Procyon v0.5.36
// 

package org.pitest.mutationtest;

import org.pitest.util.ExitCode;

public enum DetectionStatus
{
    KILLED(true), 
    SURVIVED(false), 
    TIMED_OUT(true), 
    NON_VIABLE(true), 
    MEMORY_ERROR(true), 
    NOT_STARTED(false), 
    STARTED(false), 
    RUN_ERROR(true), 
    NO_COVERAGE(false);
    
    private final boolean detected;
    
    private DetectionStatus(final boolean detected) {
        this.detected = detected;
    }
    
    public static DetectionStatus getForErrorExitCode(final ExitCode exitCode) {
        if (exitCode.equals(ExitCode.OUT_OF_MEMORY)) {
            return DetectionStatus.MEMORY_ERROR;
        }
        if (exitCode.equals(ExitCode.TIMEOUT)) {
            return DetectionStatus.TIMED_OUT;
        }
        return DetectionStatus.RUN_ERROR;
    }
    
    public boolean isDetected() {
        return this.detected;
    }
}
