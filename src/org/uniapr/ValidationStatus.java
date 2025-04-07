// 
// Decompiled by Procyon v0.5.36
// 

package org.uniapr;

import org.pitest.util.ExitCode;

public enum ValidationStatus
{
    NOT_STARTED, 
    STARTED, 
    PLAUSIBLE, 
    NON_PLAUSIBLE, 
    TIMED_OUT, 
    RUN_ERROR, 
    INITIALIZER_ERROR, 
    MEMORY_ERROR;
    
    public static ValidationStatus forExitCode(final ExitCode exitCode) {
        if (exitCode.equals(ExitCode.OUT_OF_MEMORY)) {
            return ValidationStatus.MEMORY_ERROR;
        }
        if (exitCode.equals(ExitCode.TIMEOUT)) {
            return ValidationStatus.TIMED_OUT;
        }
        return ValidationStatus.RUN_ERROR;
    }
}
