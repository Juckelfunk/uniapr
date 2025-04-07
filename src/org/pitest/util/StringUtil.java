// 
// Decompiled by Procyon v0.5.36
// 

package org.pitest.util;

import java.util.Iterator;

public class StringUtil
{
    public static String join(final Iterable<String> strings, final String separator) {
        final StringBuilder sb = new StringBuilder();
        String sep = "";
        for (final String s : strings) {
            sb.append(sep).append(s);
            sep = separator;
        }
        return sb.toString();
    }
    
    public static String newLine() {
        return System.getProperty("line.separator");
    }
    
    public static String separatorLine(final char c) {
        return repeat(c, 80);
    }
    
    public static String separatorLine() {
        return repeat('-', 80);
    }
    
    public static String repeat(final char c, final int n) {
        return new String(new char[n]).replace('\0', c);
    }
    
    public static String escapeBasicHtmlChars(final String s) {
        final StringBuilder sb = new StringBuilder();
        escapeBasicHtmlChars(s, sb);
        return sb.toString();
    }
    
    public static void escapeBasicHtmlChars(final String s, final StringBuilder out) {
        for (int i = 0; i < s.length(); ++i) {
            final int v;
            final char c = (char)(v = s.charAt(i));
            if (v < 32 || v > 127 || v == 38 || v == 39 || v == 60 || v == 62 || v == 34) {
                out.append('&');
                out.append('#');
                out.append(v);
                out.append(';');
            }
            else {
                out.append(c);
            }
        }
    }
    
    public static boolean isNullOrEmpty(final String s) {
        return s == null || s.isEmpty();
    }
}
