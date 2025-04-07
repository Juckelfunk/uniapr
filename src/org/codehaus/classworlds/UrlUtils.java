// 
// Decompiled by Procyon v0.5.36
// 

package org.codehaus.classworlds;

public class UrlUtils
{
    public static String normalizeUrlPath(String name) {
        if (name.startsWith("/")) {
            name = name.substring(1);
        }
        final int i = name.indexOf("/..");
        if (i > 0) {
            final int j = name.lastIndexOf("/", i - 1);
            name = name.substring(0, j) + name.substring(i + 3);
        }
        return name;
    }
}
