// 
// Decompiled by Procyon v0.5.36
// 

package org.pitest.classinfo;

import org.pitest.functional.F5;

public enum BridgeMethodFilter implements F5<Integer, String, String, String, String[], Boolean>
{
    INSTANCE;
    
    @Override
    public Boolean apply(final Integer access, final String name, final String desc, final String signature, final String[] exceptions) {
        return isSynthetic(access);
    }
    
    private static boolean isSynthetic(final Integer access) {
        return (access & 0x40) == 0x0;
    }
}
