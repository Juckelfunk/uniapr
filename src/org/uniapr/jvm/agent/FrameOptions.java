// 
// Decompiled by Procyon v0.5.36
// 

package org.uniapr.jvm.agent;

public class FrameOptions
{
    private static final int JAVA_7 = 51;
    
    public static int pickFlags(final byte[] bs) {
        if (needsFrames(bs)) {
            return 2;
        }
        return 1;
    }
    
    public static boolean needsFrames(final byte[] bs) {
        final short majorVersion = (short)((bs[6] & 0xFF) << 8 | (bs[7] & 0xFF));
        return majorVersion >= 51;
    }
}
