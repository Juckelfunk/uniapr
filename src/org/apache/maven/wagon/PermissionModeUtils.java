// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.maven.wagon;

public final class PermissionModeUtils
{
    private PermissionModeUtils() {
    }
    
    public static String getUserMaskFor(final String modeStr) {
        String ret = null;
        try {
            int mode = Integer.valueOf(modeStr, 8);
            mode = mode % 8 + mode / 8 % 8 * 8 + mode / 64 % 8 * 64;
            ret = Integer.toOctalString(511 - mode);
        }
        catch (NumberFormatException e) {
            try {
                Integer.parseInt(modeStr);
            }
            catch (NumberFormatException e2) {
                ret = modeStr;
            }
        }
        if (ret == null) {
            throw new IllegalArgumentException("The mode is a number but is not octal");
        }
        return ret;
    }
}
