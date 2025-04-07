// 
// Decompiled by Procyon v0.5.36
// 

package org.uniapr.commons.process;

import org.pitest.functional.SideEffect1;

public final class LoggerUtils
{
    private static final Object LOCK;
    
    private LoggerUtils() {
    }
    
    public static SideEffect1<String> out() {
        return new SideEffect1<String>() {
            @Override
            public void apply(final String msg) {
                synchronized (LoggerUtils.LOCK) {
                    System.out.print(msg);
                    System.out.flush();
                }
            }
        };
    }
    
    public static SideEffect1<String> err() {
        return new SideEffect1<String>() {
            @Override
            public void apply(final String msg) {
                synchronized (LoggerUtils.LOCK) {
                    System.out.print(msg);
                    System.out.flush();
                }
            }
        };
    }
    
    static {
        LOCK = new Object();
    }
}
