// 
// Decompiled by Procyon v0.5.36
// 

package org.codehaus.plexus.util;

import java.util.StringTokenizer;
import java.util.Vector;
import java.io.File;

public final class SelectorUtils
{
    public static final String PATTERN_HANDLER_PREFIX = "[";
    public static final String PATTERN_HANDLER_SUFFIX = "]";
    public static final String REGEX_HANDLER_PREFIX = "%regex[";
    public static final String ANT_HANDLER_PREFIX = "%ant[";
    private static SelectorUtils instance;
    
    private SelectorUtils() {
    }
    
    public static SelectorUtils getInstance() {
        return SelectorUtils.instance;
    }
    
    public static boolean matchPatternStart(final String pattern, final String str) {
        return matchPatternStart(pattern, str, true);
    }
    
    public static boolean matchPatternStart(String pattern, final String str, final boolean isCaseSensitive) {
        if (pattern.length() > "%regex[".length() + "]".length() + 1 && pattern.startsWith("%regex[") && pattern.endsWith("]")) {
            return true;
        }
        if (pattern.length() > "%ant[".length() + "]".length() + 1 && pattern.startsWith("%ant[") && pattern.endsWith("]")) {
            pattern = pattern.substring("%ant[".length(), pattern.length() - "]".length());
        }
        final String altStr = str.replace('\\', '/');
        return matchAntPathPatternStart(pattern, str, File.separator, isCaseSensitive) || matchAntPathPatternStart(pattern, altStr, "/", isCaseSensitive);
    }
    
    private static boolean matchAntPathPatternStart(final String pattern, final String str, final String separator, final boolean isCaseSensitive) {
        if (str.startsWith(separator) != pattern.startsWith(separator)) {
            return false;
        }
        final Vector patDirs = tokenizePath(pattern, separator);
        final Vector strDirs = tokenizePath(str, separator);
        int patIdxStart;
        int patIdxEnd;
        int strIdxStart;
        int strIdxEnd;
        for (patIdxStart = 0, patIdxEnd = patDirs.size() - 1, strIdxStart = 0, strIdxEnd = strDirs.size() - 1; patIdxStart <= patIdxEnd && strIdxStart <= strIdxEnd; ++patIdxStart, ++strIdxStart) {
            final String patDir = patDirs.elementAt(patIdxStart);
            if (patDir.equals("**")) {
                break;
            }
            if (!match(patDir, strDirs.elementAt(strIdxStart), isCaseSensitive)) {
                return false;
            }
        }
        return strIdxStart > strIdxEnd || patIdxStart <= patIdxEnd;
    }
    
    public static boolean matchPath(final String pattern, final String str) {
        return matchPath(pattern, str, true);
    }
    
    public static boolean matchPath(String pattern, final String str, final boolean isCaseSensitive) {
        if (pattern.length() > "%regex[".length() + "]".length() + 1 && pattern.startsWith("%regex[") && pattern.endsWith("]")) {
            String pat;
            pattern = (pat = pattern.substring("%regex[".length(), pattern.length() - "]".length()));
            pat = pat.replaceAll("/", "[\\\\\\\\/]");
            return str.matches(pat);
        }
        if (pattern.length() > "%ant[".length() + "]".length() + 1 && pattern.startsWith("%ant[") && pattern.endsWith("]")) {
            pattern = pattern.substring("%ant[".length(), pattern.length() - "]".length());
        }
        final String altStr = str.replace('\\', '/');
        return matchAntPathPattern(pattern, str, File.separator, isCaseSensitive) || matchAntPathPattern(pattern, altStr, "/", isCaseSensitive);
    }
    
    private static boolean matchAntPathPattern(final String pattern, final String str, final String separator, final boolean isCaseSensitive) {
        if (str.startsWith(separator) != pattern.startsWith(separator)) {
            return false;
        }
        Vector patDirs = tokenizePath(pattern, separator);
        Vector strDirs = tokenizePath(str, separator);
        int patIdxStart;
        int patIdxEnd;
        int strIdxStart;
        int strIdxEnd;
        for (patIdxStart = 0, patIdxEnd = patDirs.size() - 1, strIdxStart = 0, strIdxEnd = strDirs.size() - 1; patIdxStart <= patIdxEnd && strIdxStart <= strIdxEnd; ++patIdxStart, ++strIdxStart) {
            final String patDir = patDirs.elementAt(patIdxStart);
            if (patDir.equals("**")) {
                break;
            }
            if (!match(patDir, strDirs.elementAt(strIdxStart), isCaseSensitive)) {
                patDirs = null;
                strDirs = null;
                return false;
            }
        }
        if (strIdxStart > strIdxEnd) {
            for (int i = patIdxStart; i <= patIdxEnd; ++i) {
                if (!patDirs.elementAt(i).equals("**")) {
                    patDirs = null;
                    strDirs = null;
                    return false;
                }
            }
            return true;
        }
        if (patIdxStart > patIdxEnd) {
            patDirs = null;
            strDirs = null;
            return false;
        }
        while (patIdxStart <= patIdxEnd && strIdxStart <= strIdxEnd) {
            final String patDir = patDirs.elementAt(patIdxEnd);
            if (patDir.equals("**")) {
                break;
            }
            if (!match(patDir, strDirs.elementAt(strIdxEnd), isCaseSensitive)) {
                patDirs = null;
                strDirs = null;
                return false;
            }
            --patIdxEnd;
            --strIdxEnd;
        }
        if (strIdxStart > strIdxEnd) {
            for (int i = patIdxStart; i <= patIdxEnd; ++i) {
                if (!patDirs.elementAt(i).equals("**")) {
                    patDirs = null;
                    strDirs = null;
                    return false;
                }
            }
            return true;
        }
        while (patIdxStart != patIdxEnd && strIdxStart <= strIdxEnd) {
            int patIdxTmp = -1;
            for (int j = patIdxStart + 1; j <= patIdxEnd; ++j) {
                if (patDirs.elementAt(j).equals("**")) {
                    patIdxTmp = j;
                    break;
                }
            }
            if (patIdxTmp == patIdxStart + 1) {
                ++patIdxStart;
            }
            else {
                final int patLength = patIdxTmp - patIdxStart - 1;
                final int strLength = strIdxEnd - strIdxStart + 1;
                int foundIdx = -1;
                int k = 0;
            Label_0413:
                while (k <= strLength - patLength) {
                    for (int l = 0; l < patLength; ++l) {
                        final String subPat = patDirs.elementAt(patIdxStart + l + 1);
                        final String subStr = strDirs.elementAt(strIdxStart + k + l);
                        if (!match(subPat, subStr, isCaseSensitive)) {
                            ++k;
                            continue Label_0413;
                        }
                    }
                    foundIdx = strIdxStart + k;
                    break;
                }
                if (foundIdx == -1) {
                    patDirs = null;
                    strDirs = null;
                    return false;
                }
                patIdxStart = patIdxTmp;
                strIdxStart = foundIdx + patLength;
            }
        }
        for (int i = patIdxStart; i <= patIdxEnd; ++i) {
            if (!patDirs.elementAt(i).equals("**")) {
                patDirs = null;
                strDirs = null;
                return false;
            }
        }
        return true;
    }
    
    public static boolean match(final String pattern, final String str) {
        return match(pattern, str, true);
    }
    
    public static boolean match(final String pattern, final String str, final boolean isCaseSensitive) {
        final char[] patArr = pattern.toCharArray();
        final char[] strArr = str.toCharArray();
        int patIdxStart = 0;
        int patIdxEnd = patArr.length - 1;
        int strIdxStart = 0;
        int strIdxEnd = strArr.length - 1;
        boolean containsStar = false;
        for (int i = 0; i < patArr.length; ++i) {
            if (patArr[i] == '*') {
                containsStar = true;
                break;
            }
        }
        if (!containsStar) {
            if (patIdxEnd != strIdxEnd) {
                return false;
            }
            for (int i = 0; i <= patIdxEnd; ++i) {
                final char ch = patArr[i];
                if (ch != '?' && !equals(ch, strArr[i], isCaseSensitive)) {
                    return false;
                }
            }
            return true;
        }
        else {
            if (patIdxEnd == 0) {
                return true;
            }
            char ch;
            while ((ch = patArr[patIdxStart]) != '*' && strIdxStart <= strIdxEnd) {
                if (ch != '?' && !equals(ch, strArr[strIdxStart], isCaseSensitive)) {
                    return false;
                }
                ++patIdxStart;
                ++strIdxStart;
            }
            if (strIdxStart > strIdxEnd) {
                for (int i = patIdxStart; i <= patIdxEnd; ++i) {
                    if (patArr[i] != '*') {
                        return false;
                    }
                }
                return true;
            }
            while ((ch = patArr[patIdxEnd]) != '*' && strIdxStart <= strIdxEnd) {
                if (ch != '?' && !equals(ch, strArr[strIdxEnd], isCaseSensitive)) {
                    return false;
                }
                --patIdxEnd;
                --strIdxEnd;
            }
            if (strIdxStart > strIdxEnd) {
                for (int i = patIdxStart; i <= patIdxEnd; ++i) {
                    if (patArr[i] != '*') {
                        return false;
                    }
                }
                return true;
            }
            while (patIdxStart != patIdxEnd && strIdxStart <= strIdxEnd) {
                int patIdxTmp = -1;
                for (int j = patIdxStart + 1; j <= patIdxEnd; ++j) {
                    if (patArr[j] == '*') {
                        patIdxTmp = j;
                        break;
                    }
                }
                if (patIdxTmp == patIdxStart + 1) {
                    ++patIdxStart;
                }
                else {
                    final int patLength = patIdxTmp - patIdxStart - 1;
                    final int strLength = strIdxEnd - strIdxStart + 1;
                    int foundIdx = -1;
                    int k = 0;
                Label_0399:
                    while (k <= strLength - patLength) {
                        for (int l = 0; l < patLength; ++l) {
                            ch = patArr[patIdxStart + l + 1];
                            if (ch != '?' && !equals(ch, strArr[strIdxStart + k + l], isCaseSensitive)) {
                                ++k;
                                continue Label_0399;
                            }
                        }
                        foundIdx = strIdxStart + k;
                        break;
                    }
                    if (foundIdx == -1) {
                        return false;
                    }
                    patIdxStart = patIdxTmp;
                    strIdxStart = foundIdx + patLength;
                }
            }
            for (int i = patIdxStart; i <= patIdxEnd; ++i) {
                if (patArr[i] != '*') {
                    return false;
                }
            }
            return true;
        }
    }
    
    private static boolean equals(final char c1, final char c2, final boolean isCaseSensitive) {
        return c1 == c2 || (!isCaseSensitive && (Character.toUpperCase(c1) == Character.toUpperCase(c2) || Character.toLowerCase(c1) == Character.toLowerCase(c2)));
    }
    
    public static Vector tokenizePath(final String path) {
        return tokenizePath(path, File.separator);
    }
    
    public static Vector tokenizePath(final String path, final String separator) {
        final Vector ret = new Vector();
        final StringTokenizer st = new StringTokenizer(path, separator);
        while (st.hasMoreTokens()) {
            ret.addElement(st.nextToken());
        }
        return ret;
    }
    
    public static boolean isOutOfDate(final File src, final File target, final int granularity) {
        return src.exists() && (!target.exists() || src.lastModified() - granularity > target.lastModified());
    }
    
    public static String removeWhitespace(final String input) {
        final StringBuffer result = new StringBuffer();
        if (input != null) {
            final StringTokenizer st = new StringTokenizer(input);
            while (st.hasMoreTokens()) {
                result.append(st.nextToken());
            }
        }
        return result.toString();
    }
    
    static {
        SelectorUtils.instance = new SelectorUtils();
    }
}
