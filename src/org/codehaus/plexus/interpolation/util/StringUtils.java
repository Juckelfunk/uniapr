// 
// Decompiled by Procyon v0.5.36
// 

package org.codehaus.plexus.interpolation.util;

public class StringUtils
{
    public static String replace(final String text, final String repl, final String with) {
        return replace(text, repl, with, -1);
    }
    
    public static String replace(final String text, final String repl, final String with, int max) {
        if (text == null || repl == null || with == null || repl.length() == 0) {
            return text;
        }
        final StringBuffer buf = new StringBuffer(text.length());
        int start = 0;
        int end = 0;
        while ((end = text.indexOf(repl, start)) != -1) {
            buf.append(text.substring(start, end)).append(with);
            start = end + repl.length();
            if (--max == 0) {
                break;
            }
        }
        buf.append(text.substring(start));
        return buf.toString();
    }
    
    public static String capitalizeFirstLetter(final String data) {
        final char firstLetter = Character.toTitleCase(data.substring(0, 1).charAt(0));
        final String restLetters = data.substring(1);
        return firstLetter + restLetters;
    }
}
