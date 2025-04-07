// 
// Decompiled by Procyon v0.5.36
// 

package org.codehaus.plexus.util;

import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.Arrays;
import java.util.Locale;
import java.util.Map;
import java.util.Iterator;
import java.util.StringTokenizer;

public class StringUtils
{
    public static String clean(final String str) {
        return (str == null) ? "" : str.trim();
    }
    
    public static String trim(final String str) {
        return (str == null) ? null : str.trim();
    }
    
    public static String deleteWhitespace(final String str) {
        final StringBuffer buffer = new StringBuffer();
        for (int sz = str.length(), i = 0; i < sz; ++i) {
            if (!Character.isWhitespace(str.charAt(i))) {
                buffer.append(str.charAt(i));
            }
        }
        return buffer.toString();
    }
    
    public static boolean isNotEmpty(final String str) {
        return str != null && str.length() > 0;
    }
    
    public static boolean isEmpty(final String str) {
        return str == null || str.trim().length() == 0;
    }
    
    public static boolean isBlank(final String str) {
        final int strLen;
        if (str == null || (strLen = str.length()) == 0) {
            return true;
        }
        for (int i = 0; i < strLen; ++i) {
            if (!Character.isWhitespace(str.charAt(i))) {
                return false;
            }
        }
        return true;
    }
    
    public static boolean isNotBlank(final String str) {
        return !isBlank(str);
    }
    
    public static boolean equals(final String str1, final String str2) {
        return (str1 == null) ? (str2 == null) : str1.equals(str2);
    }
    
    public static boolean equalsIgnoreCase(final String str1, final String str2) {
        return (str1 == null) ? (str2 == null) : str1.equalsIgnoreCase(str2);
    }
    
    public static int indexOfAny(final String str, final String[] searchStrs) {
        if (str == null || searchStrs == null) {
            return -1;
        }
        final int sz = searchStrs.length;
        int ret = Integer.MAX_VALUE;
        int tmp = 0;
        for (int i = 0; i < sz; ++i) {
            tmp = str.indexOf(searchStrs[i]);
            if (tmp != -1) {
                if (tmp < ret) {
                    ret = tmp;
                }
            }
        }
        return (ret == Integer.MAX_VALUE) ? -1 : ret;
    }
    
    public static int lastIndexOfAny(final String str, final String[] searchStrs) {
        if (str == null || searchStrs == null) {
            return -1;
        }
        final int sz = searchStrs.length;
        int ret = -1;
        int tmp = 0;
        for (int i = 0; i < sz; ++i) {
            tmp = str.lastIndexOf(searchStrs[i]);
            if (tmp > ret) {
                ret = tmp;
            }
        }
        return ret;
    }
    
    public static String substring(final String str, int start) {
        if (str == null) {
            return null;
        }
        if (start < 0) {
            start += str.length();
        }
        if (start < 0) {
            start = 0;
        }
        if (start > str.length()) {
            return "";
        }
        return str.substring(start);
    }
    
    public static String substring(final String str, int start, int end) {
        if (str == null) {
            return null;
        }
        if (end < 0) {
            end += str.length();
        }
        if (start < 0) {
            start += str.length();
        }
        if (end > str.length()) {
            end = str.length();
        }
        if (start > end) {
            return "";
        }
        if (start < 0) {
            start = 0;
        }
        if (end < 0) {
            end = 0;
        }
        return str.substring(start, end);
    }
    
    public static String left(final String str, final int len) {
        if (len < 0) {
            throw new IllegalArgumentException("Requested String length " + len + " is less than zero");
        }
        if (str == null || str.length() <= len) {
            return str;
        }
        return str.substring(0, len);
    }
    
    public static String right(final String str, final int len) {
        if (len < 0) {
            throw new IllegalArgumentException("Requested String length " + len + " is less than zero");
        }
        if (str == null || str.length() <= len) {
            return str;
        }
        return str.substring(str.length() - len);
    }
    
    public static String mid(final String str, final int pos, final int len) {
        if (pos < 0 || (str != null && pos > str.length())) {
            throw new StringIndexOutOfBoundsException("String index " + pos + " is out of bounds");
        }
        if (len < 0) {
            throw new IllegalArgumentException("Requested String length " + len + " is less than zero");
        }
        if (str == null) {
            return null;
        }
        if (str.length() <= pos + len) {
            return str.substring(pos);
        }
        return str.substring(pos, pos + len);
    }
    
    public static String[] split(final String str) {
        return split(str, null, -1);
    }
    
    public static String[] split(final String text, final String separator) {
        return split(text, separator, -1);
    }
    
    public static String[] split(final String str, final String separator, final int max) {
        StringTokenizer tok = null;
        if (separator == null) {
            tok = new StringTokenizer(str);
        }
        else {
            tok = new StringTokenizer(str, separator);
        }
        int listSize = tok.countTokens();
        if (max > 0 && listSize > max) {
            listSize = max;
        }
        final String[] list = new String[listSize];
        int i = 0;
        int lastTokenBegin = 0;
        int lastTokenEnd = 0;
        while (tok.hasMoreTokens()) {
            if (max > 0 && i == listSize - 1) {
                final String endToken = tok.nextToken();
                lastTokenBegin = str.indexOf(endToken, lastTokenEnd);
                list[i] = str.substring(lastTokenBegin);
                break;
            }
            list[i] = tok.nextToken();
            lastTokenBegin = str.indexOf(list[i], lastTokenEnd);
            lastTokenEnd = lastTokenBegin + list[i].length();
            ++i;
        }
        return list;
    }
    
    public static String concatenate(final Object[] array) {
        return join(array, "");
    }
    
    public static String join(final Object[] array, String separator) {
        if (separator == null) {
            separator = "";
        }
        final int arraySize = array.length;
        final int bufSize = (arraySize == 0) ? 0 : ((array[0].toString().length() + separator.length()) * arraySize);
        final StringBuffer buf = new StringBuffer(bufSize);
        for (int i = 0; i < arraySize; ++i) {
            if (i > 0) {
                buf.append(separator);
            }
            buf.append(array[i]);
        }
        return buf.toString();
    }
    
    public static String join(final Iterator iterator, String separator) {
        if (separator == null) {
            separator = "";
        }
        final StringBuffer buf = new StringBuffer(256);
        while (iterator.hasNext()) {
            buf.append(iterator.next());
            if (iterator.hasNext()) {
                buf.append(separator);
            }
        }
        return buf.toString();
    }
    
    public static String replaceOnce(final String text, final char repl, final char with) {
        return replace(text, repl, with, 1);
    }
    
    public static String replace(final String text, final char repl, final char with) {
        return replace(text, repl, with, -1);
    }
    
    public static String replace(final String text, final char repl, final char with, final int max) {
        return replace(text, String.valueOf(repl), String.valueOf(with), max);
    }
    
    public static String replaceOnce(final String text, final String repl, final String with) {
        return replace(text, repl, with, 1);
    }
    
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
    
    public static String overlayString(final String text, final String overlay, final int start, final int end) {
        return new StringBuffer(start + overlay.length() + text.length() - end + 1).append(text.substring(0, start)).append(overlay).append(text.substring(end)).toString();
    }
    
    public static String center(final String str, final int size) {
        return center(str, size, " ");
    }
    
    public static String center(String str, final int size, final String delim) {
        final int sz = str.length();
        final int p = size - sz;
        if (p < 1) {
            return str;
        }
        str = leftPad(str, sz + p / 2, delim);
        str = rightPad(str, size, delim);
        return str;
    }
    
    public static String chomp(final String str) {
        return chomp(str, "\n");
    }
    
    public static String chomp(final String str, final String sep) {
        final int idx = str.lastIndexOf(sep);
        if (idx != -1) {
            return str.substring(0, idx);
        }
        return str;
    }
    
    public static String chompLast(final String str) {
        return chompLast(str, "\n");
    }
    
    public static String chompLast(final String str, final String sep) {
        if (str.length() == 0) {
            return str;
        }
        final String sub = str.substring(str.length() - sep.length());
        if (sep.equals(sub)) {
            return str.substring(0, str.length() - sep.length());
        }
        return str;
    }
    
    public static String getChomp(final String str, final String sep) {
        final int idx = str.lastIndexOf(sep);
        if (idx == str.length() - sep.length()) {
            return sep;
        }
        if (idx != -1) {
            return str.substring(idx);
        }
        return "";
    }
    
    public static String prechomp(final String str, final String sep) {
        final int idx = str.indexOf(sep);
        if (idx != -1) {
            return str.substring(idx + sep.length());
        }
        return str;
    }
    
    public static String getPrechomp(final String str, final String sep) {
        final int idx = str.indexOf(sep);
        if (idx != -1) {
            return str.substring(0, idx + sep.length());
        }
        return "";
    }
    
    public static String chop(final String str) {
        if ("".equals(str)) {
            return "";
        }
        if (str.length() == 1) {
            return "";
        }
        final int lastIdx = str.length() - 1;
        final String ret = str.substring(0, lastIdx);
        final char last = str.charAt(lastIdx);
        if (last == '\n' && ret.charAt(lastIdx - 1) == '\r') {
            return ret.substring(0, lastIdx - 1);
        }
        return ret;
    }
    
    public static String chopNewline(final String str) {
        int lastIdx = str.length() - 1;
        final char last = str.charAt(lastIdx);
        if (last == '\n') {
            if (str.charAt(lastIdx - 1) == '\r') {
                --lastIdx;
            }
        }
        else {
            ++lastIdx;
        }
        return str.substring(0, lastIdx);
    }
    
    public static String escape(final String str) {
        final int sz = str.length();
        final StringBuffer buffer = new StringBuffer(2 * sz);
        for (int i = 0; i < sz; ++i) {
            final char ch = str.charAt(i);
            if (ch > '\u0fff') {
                buffer.append("\\u" + Integer.toHexString(ch));
            }
            else if (ch > '\u00ff') {
                buffer.append("\\u0" + Integer.toHexString(ch));
            }
            else if (ch > '\u007f') {
                buffer.append("\\u00" + Integer.toHexString(ch));
            }
            else if (ch < ' ') {
                switch (ch) {
                    case '\b': {
                        buffer.append('\\');
                        buffer.append('b');
                        break;
                    }
                    case '\n': {
                        buffer.append('\\');
                        buffer.append('n');
                        break;
                    }
                    case '\t': {
                        buffer.append('\\');
                        buffer.append('t');
                        break;
                    }
                    case '\f': {
                        buffer.append('\\');
                        buffer.append('f');
                        break;
                    }
                    case '\r': {
                        buffer.append('\\');
                        buffer.append('r');
                        break;
                    }
                    default: {
                        if (ch > '\u000f') {
                            buffer.append("\\u00" + Integer.toHexString(ch));
                            break;
                        }
                        buffer.append("\\u000" + Integer.toHexString(ch));
                        break;
                    }
                }
            }
            else {
                switch (ch) {
                    case '\'': {
                        buffer.append('\\');
                        buffer.append('\'');
                        break;
                    }
                    case '\"': {
                        buffer.append('\\');
                        buffer.append('\"');
                        break;
                    }
                    case '\\': {
                        buffer.append('\\');
                        buffer.append('\\');
                        break;
                    }
                    default: {
                        buffer.append(ch);
                        break;
                    }
                }
            }
        }
        return buffer.toString();
    }
    
    public static String repeat(final String str, final int repeat) {
        final StringBuffer buffer = new StringBuffer(repeat * str.length());
        for (int i = 0; i < repeat; ++i) {
            buffer.append(str);
        }
        return buffer.toString();
    }
    
    public static String rightPad(final String str, final int size) {
        return rightPad(str, size, " ");
    }
    
    public static String rightPad(String str, int size, final String delim) {
        size = (size - str.length()) / delim.length();
        if (size > 0) {
            str += repeat(delim, size);
        }
        return str;
    }
    
    public static String leftPad(final String str, final int size) {
        return leftPad(str, size, " ");
    }
    
    public static String leftPad(String str, int size, final String delim) {
        size = (size - str.length()) / delim.length();
        if (size > 0) {
            str = repeat(delim, size) + str;
        }
        return str;
    }
    
    public static String strip(final String str) {
        return strip(str, null);
    }
    
    public static String strip(String str, final String delim) {
        str = stripStart(str, delim);
        return stripEnd(str, delim);
    }
    
    public static String[] stripAll(final String[] strs) {
        return stripAll(strs, null);
    }
    
    public static String[] stripAll(final String[] strs, final String delimiter) {
        if (strs == null || strs.length == 0) {
            return strs;
        }
        final int sz = strs.length;
        final String[] newArr = new String[sz];
        for (int i = 0; i < sz; ++i) {
            newArr[i] = strip(strs[i], delimiter);
        }
        return newArr;
    }
    
    public static String stripEnd(final String str, final String strip) {
        if (str == null) {
            return null;
        }
        int end = str.length();
        if (strip == null) {
            while (end != 0 && Character.isWhitespace(str.charAt(end - 1))) {
                --end;
            }
        }
        else {
            while (end != 0 && strip.indexOf(str.charAt(end - 1)) != -1) {
                --end;
            }
        }
        return str.substring(0, end);
    }
    
    public static String stripStart(final String str, final String strip) {
        if (str == null) {
            return null;
        }
        int start = 0;
        final int sz = str.length();
        if (strip == null) {
            while (start != sz && Character.isWhitespace(str.charAt(start))) {
                ++start;
            }
        }
        else {
            while (start != sz && strip.indexOf(str.charAt(start)) != -1) {
                ++start;
            }
        }
        return str.substring(start);
    }
    
    public static String upperCase(final String str) {
        if (str == null) {
            return null;
        }
        return str.toUpperCase();
    }
    
    public static String lowerCase(final String str) {
        if (str == null) {
            return null;
        }
        return str.toLowerCase();
    }
    
    public static String uncapitalise(final String str) {
        if (str == null) {
            return null;
        }
        if (str.length() == 0) {
            return "";
        }
        return new StringBuffer(str.length()).append(Character.toLowerCase(str.charAt(0))).append(str.substring(1)).toString();
    }
    
    public static String capitalise(final String str) {
        if (str == null) {
            return null;
        }
        if (str.length() == 0) {
            return "";
        }
        return new StringBuffer(str.length()).append(Character.toTitleCase(str.charAt(0))).append(str.substring(1)).toString();
    }
    
    public static String swapCase(final String str) {
        if (str == null) {
            return null;
        }
        final int sz = str.length();
        final StringBuffer buffer = new StringBuffer(sz);
        boolean whitespace = false;
        char ch = '\0';
        char tmp = '\0';
        for (int i = 0; i < sz; ++i) {
            ch = str.charAt(i);
            if (Character.isUpperCase(ch)) {
                tmp = Character.toLowerCase(ch);
            }
            else if (Character.isTitleCase(ch)) {
                tmp = Character.toLowerCase(ch);
            }
            else if (Character.isLowerCase(ch)) {
                if (whitespace) {
                    tmp = Character.toTitleCase(ch);
                }
                else {
                    tmp = Character.toUpperCase(ch);
                }
            }
            else {
                tmp = ch;
            }
            buffer.append(tmp);
            whitespace = Character.isWhitespace(ch);
        }
        return buffer.toString();
    }
    
    public static String capitaliseAllWords(final String str) {
        if (str == null) {
            return null;
        }
        final int sz = str.length();
        final StringBuffer buffer = new StringBuffer(sz);
        boolean space = true;
        for (int i = 0; i < sz; ++i) {
            final char ch = str.charAt(i);
            if (Character.isWhitespace(ch)) {
                buffer.append(ch);
                space = true;
            }
            else if (space) {
                buffer.append(Character.toTitleCase(ch));
                space = false;
            }
            else {
                buffer.append(ch);
            }
        }
        return buffer.toString();
    }
    
    public static String uncapitaliseAllWords(final String str) {
        if (str == null) {
            return null;
        }
        final int sz = str.length();
        final StringBuffer buffer = new StringBuffer(sz);
        boolean space = true;
        for (int i = 0; i < sz; ++i) {
            final char ch = str.charAt(i);
            if (Character.isWhitespace(ch)) {
                buffer.append(ch);
                space = true;
            }
            else if (space) {
                buffer.append(Character.toLowerCase(ch));
                space = false;
            }
            else {
                buffer.append(ch);
            }
        }
        return buffer.toString();
    }
    
    public static String getNestedString(final String str, final String tag) {
        return getNestedString(str, tag, tag);
    }
    
    public static String getNestedString(final String str, final String open, final String close) {
        if (str == null) {
            return null;
        }
        final int start = str.indexOf(open);
        if (start != -1) {
            final int end = str.indexOf(close, start + open.length());
            if (end != -1) {
                return str.substring(start + open.length(), end);
            }
        }
        return null;
    }
    
    public static int countMatches(final String str, final String sub) {
        if (sub.equals("")) {
            return 0;
        }
        if (str == null) {
            return 0;
        }
        int count = 0;
        for (int idx = 0; (idx = str.indexOf(sub, idx)) != -1; idx += sub.length()) {
            ++count;
        }
        return count;
    }
    
    public static boolean isAlpha(final String str) {
        if (str == null) {
            return false;
        }
        for (int sz = str.length(), i = 0; i < sz; ++i) {
            if (!Character.isLetter(str.charAt(i))) {
                return false;
            }
        }
        return true;
    }
    
    public static boolean isWhitespace(final String str) {
        if (str == null) {
            return false;
        }
        for (int sz = str.length(), i = 0; i < sz; ++i) {
            if (!Character.isWhitespace(str.charAt(i))) {
                return false;
            }
        }
        return true;
    }
    
    public static boolean isAlphaSpace(final String str) {
        if (str == null) {
            return false;
        }
        for (int sz = str.length(), i = 0; i < sz; ++i) {
            if (!Character.isLetter(str.charAt(i)) && str.charAt(i) != ' ') {
                return false;
            }
        }
        return true;
    }
    
    public static boolean isAlphanumeric(final String str) {
        if (str == null) {
            return false;
        }
        for (int sz = str.length(), i = 0; i < sz; ++i) {
            if (!Character.isLetterOrDigit(str.charAt(i))) {
                return false;
            }
        }
        return true;
    }
    
    public static boolean isAlphanumericSpace(final String str) {
        if (str == null) {
            return false;
        }
        for (int sz = str.length(), i = 0; i < sz; ++i) {
            if (!Character.isLetterOrDigit(str.charAt(i)) && str.charAt(i) != ' ') {
                return false;
            }
        }
        return true;
    }
    
    public static boolean isNumeric(final String str) {
        if (str == null) {
            return false;
        }
        for (int sz = str.length(), i = 0; i < sz; ++i) {
            if (!Character.isDigit(str.charAt(i))) {
                return false;
            }
        }
        return true;
    }
    
    public static boolean isNumericSpace(final String str) {
        if (str == null) {
            return false;
        }
        for (int sz = str.length(), i = 0; i < sz; ++i) {
            if (!Character.isDigit(str.charAt(i)) && str.charAt(i) != ' ') {
                return false;
            }
        }
        return true;
    }
    
    public static String defaultString(final Object obj) {
        return defaultString(obj, "");
    }
    
    public static String defaultString(final Object obj, final String defaultString) {
        return (obj == null) ? defaultString : obj.toString();
    }
    
    public static String reverse(final String str) {
        if (str == null) {
            return null;
        }
        return new StringBuffer(str).reverse().toString();
    }
    
    public static String reverseDelimitedString(final String str, final String delimiter) {
        final String[] strs = split(str, delimiter);
        reverseArray(strs);
        return join(strs, delimiter);
    }
    
    private static void reverseArray(final Object[] array) {
        for (int i = 0, j = array.length - 1; j > i; --j, ++i) {
            final Object tmp = array[j];
            array[j] = array[i];
            array[i] = tmp;
        }
    }
    
    public static String abbreviate(final String s, final int maxWidth) {
        return abbreviate(s, 0, maxWidth);
    }
    
    public static String abbreviate(final String s, int offset, final int maxWidth) {
        if (maxWidth < 4) {
            throw new IllegalArgumentException("Minimum abbreviation width is 4");
        }
        if (s.length() <= maxWidth) {
            return s;
        }
        if (offset > s.length()) {
            offset = s.length();
        }
        if (s.length() - offset < maxWidth - 3) {
            offset = s.length() - (maxWidth - 3);
        }
        if (offset <= 4) {
            return s.substring(0, maxWidth - 3) + "...";
        }
        if (maxWidth < 7) {
            throw new IllegalArgumentException("Minimum abbreviation width with offset is 7");
        }
        if (offset + (maxWidth - 3) < s.length()) {
            return "..." + abbreviate(s.substring(offset), maxWidth - 3);
        }
        return "..." + s.substring(s.length() - (maxWidth - 3));
    }
    
    public static String difference(final String s1, final String s2) {
        final int at = differenceAt(s1, s2);
        if (at == -1) {
            return "";
        }
        return s2.substring(at);
    }
    
    public static int differenceAt(final String s1, final String s2) {
        int i;
        for (i = 0; i < s1.length() && i < s2.length() && s1.charAt(i) == s2.charAt(i); ++i) {}
        if (i < s2.length() || i < s1.length()) {
            return i;
        }
        return -1;
    }
    
    public static String interpolate(String text, final Map namespace) {
        final Iterator keys = namespace.keySet().iterator();
        while (keys.hasNext()) {
            final String key = keys.next().toString();
            final Object obj = namespace.get(key);
            if (obj == null) {
                throw new NullPointerException("The value of the key '" + key + "' is null.");
            }
            final String value = obj.toString();
            text = replace(text, "${" + key + "}", value);
            if (key.indexOf(" ") != -1) {
                continue;
            }
            text = replace(text, "$" + key, value);
        }
        return text;
    }
    
    public static String removeAndHump(final String data, final String replaceThis) {
        final StringBuffer out = new StringBuffer();
        final String temp = data;
        final StringTokenizer st = new StringTokenizer(temp, replaceThis);
        while (st.hasMoreTokens()) {
            final String element = (String)st.nextElement();
            out.append(capitalizeFirstLetter(element));
        }
        return out.toString();
    }
    
    public static String capitalizeFirstLetter(final String data) {
        final char firstLetter = Character.toTitleCase(data.substring(0, 1).charAt(0));
        final String restLetters = data.substring(1);
        return firstLetter + restLetters;
    }
    
    public static String lowercaseFirstLetter(final String data) {
        final char firstLetter = Character.toLowerCase(data.substring(0, 1).charAt(0));
        final String restLetters = data.substring(1);
        return firstLetter + restLetters;
    }
    
    public static String addAndDeHump(final String view) {
        final StringBuffer sb = new StringBuffer();
        for (int i = 0; i < view.length(); ++i) {
            if (i != 0 && Character.isUpperCase(view.charAt(i))) {
                sb.append('-');
            }
            sb.append(view.charAt(i));
        }
        return sb.toString().trim().toLowerCase(Locale.ENGLISH);
    }
    
    public static String quoteAndEscape(final String source, final char quoteChar) {
        return quoteAndEscape(source, quoteChar, new char[] { quoteChar }, new char[] { ' ' }, '\\', false);
    }
    
    public static String quoteAndEscape(final String source, final char quoteChar, final char[] quotingTriggers) {
        return quoteAndEscape(source, quoteChar, new char[] { quoteChar }, quotingTriggers, '\\', false);
    }
    
    public static String quoteAndEscape(final String source, final char quoteChar, final char[] escapedChars, final char escapeChar, final boolean force) {
        return quoteAndEscape(source, quoteChar, escapedChars, new char[] { ' ' }, escapeChar, force);
    }
    
    public static String quoteAndEscape(final String source, final char quoteChar, final char[] escapedChars, final char[] quotingTriggers, final char escapeChar, final boolean force) {
        if (source == null) {
            return null;
        }
        if (!force && source.startsWith(Character.toString(quoteChar)) && source.endsWith(Character.toString(quoteChar))) {
            return source;
        }
        final String escaped = escape(source, escapedChars, escapeChar);
        boolean quote = false;
        if (force) {
            quote = true;
        }
        else if (!escaped.equals(source)) {
            quote = true;
        }
        else {
            for (int i = 0; i < quotingTriggers.length; ++i) {
                if (escaped.indexOf(quotingTriggers[i]) > -1) {
                    quote = true;
                    break;
                }
            }
        }
        if (quote) {
            return quoteChar + escaped + quoteChar;
        }
        return escaped;
    }
    
    public static String escape(final String source, final char[] escapedChars, final char escapeChar) {
        if (source == null) {
            return null;
        }
        final char[] eqc = new char[escapedChars.length];
        System.arraycopy(escapedChars, 0, eqc, 0, escapedChars.length);
        Arrays.sort(eqc);
        final StringBuffer buffer = new StringBuffer(source.length());
        int escapeCount = 0;
        for (int i = 0; i < source.length(); ++i) {
            final char c = source.charAt(i);
            final int result = Arrays.binarySearch(eqc, c);
            if (result > -1) {
                buffer.append(escapeChar);
                ++escapeCount;
            }
            buffer.append(c);
        }
        return buffer.toString();
    }
    
    public static String removeDuplicateWhitespace(final String s) {
        final String patternStr = "\\s+";
        final String replaceStr = " ";
        final Pattern pattern = Pattern.compile(patternStr);
        final Matcher matcher = pattern.matcher(s);
        return matcher.replaceAll(replaceStr);
    }
    
    public static String unifyLineSeparators(final String s) {
        return unifyLineSeparators(s, System.getProperty("line.separator"));
    }
    
    public static String unifyLineSeparators(final String s, String ls) {
        if (s == null) {
            return null;
        }
        if (ls == null) {
            ls = System.getProperty("line.separator");
        }
        if (!ls.equals("\n") && !ls.equals("\r") && !ls.equals("\r\n")) {
            throw new IllegalArgumentException("Requested line separator is invalid.");
        }
        final int length = s.length();
        final StringBuffer buffer = new StringBuffer(length);
        for (int i = 0; i < length; ++i) {
            if (s.charAt(i) == '\r') {
                if (i + 1 < length && s.charAt(i + 1) == '\n') {
                    ++i;
                }
                buffer.append(ls);
            }
            else if (s.charAt(i) == '\n') {
                buffer.append(ls);
            }
            else {
                buffer.append(s.charAt(i));
            }
        }
        return buffer.toString();
    }
    
    public static boolean contains(final String str, final char searchChar) {
        return !isEmpty(str) && str.indexOf(searchChar) >= 0;
    }
    
    public static boolean contains(final String str, final String searchStr) {
        return str != null && searchStr != null && str.indexOf(searchStr) >= 0;
    }
}
