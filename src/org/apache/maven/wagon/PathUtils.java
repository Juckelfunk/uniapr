// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.maven.wagon;

import java.io.File;
import java.util.StringTokenizer;

public final class PathUtils
{
    private PathUtils() {
    }
    
    public static String dirname(final String path) {
        final int i = path.lastIndexOf("/");
        return (i >= 0) ? path.substring(0, i) : "";
    }
    
    public static String filename(final String path) {
        final int i = path.lastIndexOf("/");
        return (i >= 0) ? path.substring(i + 1) : path;
    }
    
    public static String[] dirnames(final String path) {
        final String dirname = dirname(path);
        return split(dirname, "/", -1);
    }
    
    private static String[] split(final String str, final String separator, final int max) {
        StringTokenizer tok;
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
        int lastTokenEnd = 0;
        while (tok.hasMoreTokens()) {
            if (max > 0 && i == listSize - 1) {
                final String endToken = tok.nextToken();
                final int lastTokenBegin = str.indexOf(endToken, lastTokenEnd);
                list[i] = str.substring(lastTokenBegin);
                break;
            }
            list[i] = tok.nextToken();
            final int lastTokenBegin = str.indexOf(list[i], lastTokenEnd);
            lastTokenEnd = lastTokenBegin + list[i].length();
            ++i;
        }
        return list;
    }
    
    public static String host(final String url) {
        final String authorization = authorization(url);
        final int index = authorization.indexOf(64);
        if (index >= 0) {
            return authorization.substring(index + 1);
        }
        return authorization;
    }
    
    private static String authorization(final String url) {
        if (url == null) {
            return "localhost";
        }
        final String protocol = protocol(url);
        if (protocol == null || protocol.equalsIgnoreCase("file")) {
            return "localhost";
        }
        String host = url;
        if (protocol.equalsIgnoreCase("scm")) {
            host = host.substring(host.indexOf(":", 4) + 1).trim();
        }
        host = host.substring(host.indexOf(":") + 1).trim();
        if (host.startsWith("//")) {
            host = host.substring(2);
        }
        int pos = host.indexOf("/");
        if (pos > 0) {
            host = host.substring(0, pos);
        }
        pos = host.indexOf(64);
        if (pos > 0) {
            pos = host.indexOf(58, pos);
        }
        else {
            pos = host.indexOf(":");
        }
        if (pos > 0) {
            host = host.substring(0, pos);
        }
        return host;
    }
    
    public static String protocol(final String url) {
        final int pos = url.indexOf(":");
        if (pos == -1) {
            return "";
        }
        return url.substring(0, pos).trim();
    }
    
    public static int port(String url) {
        final String protocol = protocol(url);
        if (protocol == null || protocol.equalsIgnoreCase("file")) {
            return -1;
        }
        final String authorization = authorization(url);
        if (authorization == null) {
            return -1;
        }
        if (protocol.equalsIgnoreCase("scm")) {
            url = url.substring(url.indexOf(":", 4) + 1).trim();
        }
        if (url.regionMatches(true, 0, "file:", 0, 5) || url.regionMatches(true, 0, "local:", 0, 6)) {
            return -1;
        }
        url = url.substring(url.indexOf(":") + 1).trim();
        if (url.startsWith("//")) {
            url = url.substring(2);
        }
        final int start = authorization.length();
        if (url.length() <= start || url.charAt(start) != ':') {
            return -1;
        }
        int end = url.indexOf(47, start);
        if (end == start + 1) {
            return -1;
        }
        if (end == -1) {
            end = url.length();
        }
        return Integer.parseInt(url.substring(start + 1, end));
    }
    
    public static String basedir(String url) {
        String protocol = protocol(url);
        String retValue = null;
        if (protocol.equalsIgnoreCase("scm") && url.regionMatches(true, 0, "scm:svn:", 0, 8)) {
            url = url.substring(url.indexOf(":", 4) + 1);
            protocol = protocol(url);
        }
        if (protocol.equalsIgnoreCase("file")) {
            retValue = url.substring(protocol.length() + 1);
            retValue = decode(retValue);
            if (retValue.startsWith("//")) {
                retValue = retValue.substring(2);
                if (retValue.length() >= 2 && (retValue.charAt(1) == '|' || retValue.charAt(1) == ':')) {
                    retValue = retValue.charAt(0) + ":" + retValue.substring(2);
                }
                else {
                    final int index = retValue.indexOf("/");
                    if (index >= 0) {
                        retValue = retValue.substring(index + 1);
                    }
                    if (retValue.length() >= 2 && (retValue.charAt(1) == '|' || retValue.charAt(1) == ':')) {
                        retValue = retValue.charAt(0) + ":" + retValue.substring(2);
                    }
                    else if (index >= 0) {
                        retValue = "/" + retValue;
                    }
                }
            }
            if (retValue.length() >= 2 && retValue.charAt(1) == '|') {
                retValue = retValue.charAt(0) + ":" + retValue.substring(2);
            }
        }
        else {
            final String authorization = authorization(url);
            final int port = port(url);
            int pos;
            if (protocol.equalsIgnoreCase("scm")) {
                pos = url.indexOf(":", 4) + 1;
                pos = url.indexOf(":", pos) + 1;
            }
            else {
                pos = url.indexOf("://") + 3;
            }
            pos += authorization.length();
            if (port != -1) {
                pos = pos + Integer.toString(port).length() + 1;
            }
            if (url.length() > pos) {
                retValue = url.substring(pos);
                if (retValue.startsWith(":")) {
                    retValue = retValue.substring(1);
                }
                retValue = retValue.replace(':', '/');
            }
        }
        if (retValue == null) {
            retValue = "/";
        }
        return retValue.trim();
    }
    
    private static String decode(final String url) {
        String decoded = url;
        if (url != null) {
            char ch = '\0';
            for (int pos = -1; (pos = decoded.indexOf(37, pos + 1)) >= 0; decoded = decoded.substring(0, pos) + ch + decoded.substring(pos + 3)) {
                if (pos + 2 < decoded.length()) {
                    final String hexStr = decoded.substring(pos + 1, pos + 3);
                    ch = (char)Integer.parseInt(hexStr, 16);
                }
            }
        }
        return decoded;
    }
    
    public static String user(final String url) {
        final String host = authorization(url);
        int index = host.indexOf(64);
        if (index > 0) {
            final String userInfo = host.substring(0, index);
            index = userInfo.indexOf(58);
            if (index > 0) {
                return userInfo.substring(0, index);
            }
            if (index < 0) {
                return userInfo;
            }
        }
        return null;
    }
    
    public static String password(final String url) {
        final String host = authorization(url);
        int index = host.indexOf(64);
        if (index > 0) {
            final String userInfo = host.substring(0, index);
            index = userInfo.indexOf(58);
            if (index >= 0) {
                return userInfo.substring(index + 1);
            }
        }
        return null;
    }
    
    public static String toRelative(final File basedir, String absolutePath) {
        absolutePath = absolutePath.replace('\\', '/');
        final String basedirPath = basedir.getAbsolutePath().replace('\\', '/');
        String relative;
        if (absolutePath.startsWith(basedirPath)) {
            relative = absolutePath.substring(basedirPath.length());
            if (relative.startsWith("/")) {
                relative = relative.substring(1);
            }
            if (relative.length() <= 0) {
                relative = ".";
            }
        }
        else {
            relative = absolutePath;
        }
        return relative;
    }
}
