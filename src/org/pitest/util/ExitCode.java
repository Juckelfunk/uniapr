// 
// Decompiled by Procyon v0.5.36
// 

package org.pitest.util;

public enum ExitCode
{
    OK(0), 
    OUT_OF_MEMORY(11), 
    UNKNOWN_ERROR(13), 
    TIMEOUT(14), 
    JUNIT_ISSUE(15);
    
    private final int code;
    
    private ExitCode(final int code) {
        this.code = code;
    }
    
    public int getCode() {
        return this.code;
    }
    
    public static ExitCode fromCode(final int code) {
        for (final ExitCode each : values()) {
            if (each.getCode() == code) {
                return each;
            }
        }
        return ExitCode.UNKNOWN_ERROR;
    }
    
    public boolean isOk() {
        return this.equals(ExitCode.OK);
    }
}
