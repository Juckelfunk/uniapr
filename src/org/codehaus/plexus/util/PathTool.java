// 
// Decompiled by Procyon v0.5.36
// 

package org.codehaus.plexus.util;

import java.util.StringTokenizer;
import java.io.File;

public class PathTool
{
    public static final String getRelativePath(String basedir, String filename) {
        basedir = uppercaseDrive(basedir);
        filename = uppercaseDrive(filename);
        if (basedir == null || basedir.length() == 0 || filename == null || filename.length() == 0 || !filename.startsWith(basedir)) {
            return "";
        }
        final String separator = determineSeparator(filename);
        basedir = StringUtils.chompLast(basedir, separator);
        filename = StringUtils.chompLast(filename, separator);
        final String relativeFilename = filename.substring(basedir.length());
        return determineRelativePath(relativeFilename, separator);
    }
    
    public static final String getRelativePath(String filename) {
        filename = uppercaseDrive(filename);
        if (filename == null || filename.length() == 0) {
            return "";
        }
        final String separator = determineSeparator(filename);
        filename = StringUtils.chompLast(filename, separator);
        if (!filename.startsWith(separator)) {
            filename = separator + filename;
        }
        return determineRelativePath(filename, separator);
    }
    
    public static final String getDirectoryComponent(final String filename) {
        if (filename == null || filename.length() == 0) {
            return "";
        }
        final String separator = determineSeparator(filename);
        final String directory = StringUtils.chomp(filename, separator);
        if (filename.equals(directory)) {
            return ".";
        }
        return directory;
    }
    
    public static final String calculateLink(final String link, final String relativePath) {
        if (link.startsWith("/site/")) {
            return link.substring(5);
        }
        if (link.startsWith("/absolute/")) {
            return link.substring(10);
        }
        if (link.indexOf(":") >= 0) {
            return link;
        }
        if (relativePath.equals(".")) {
            if (link.startsWith("/")) {
                return link.substring(1);
            }
            return link;
        }
        else {
            if (relativePath.endsWith("/") && link.startsWith("/")) {
                return relativePath + "." + link.substring(1);
            }
            if (relativePath.endsWith("/") || link.startsWith("/")) {
                return relativePath + link;
            }
            return relativePath + "/" + link;
        }
    }
    
    public static final String getRelativeWebPath(final String oldPath, final String newPath) {
        if (StringUtils.isEmpty(oldPath) || StringUtils.isEmpty(newPath)) {
            return "";
        }
        final String resultPath = buildRelativePath(newPath, oldPath, '/');
        if (newPath.endsWith("/") && !resultPath.endsWith("/")) {
            return resultPath + "/";
        }
        return resultPath;
    }
    
    public static final String getRelativeFilePath(final String oldPath, final String newPath) {
        if (StringUtils.isEmpty(oldPath) || StringUtils.isEmpty(newPath)) {
            return "";
        }
        String fromPath = new File(oldPath).getPath();
        String toPath = new File(newPath).getPath();
        if (toPath.matches("^\\[a-zA-Z]:")) {
            toPath = toPath.substring(1);
        }
        if (fromPath.matches("^\\[a-zA-Z]:")) {
            fromPath = fromPath.substring(1);
        }
        if (fromPath.startsWith(":", 1)) {
            fromPath = Character.toLowerCase(fromPath.charAt(0)) + fromPath.substring(1);
        }
        if (toPath.startsWith(":", 1)) {
            toPath = Character.toLowerCase(toPath.charAt(0)) + toPath.substring(1);
        }
        if (toPath.startsWith(":", 1) && fromPath.startsWith(":", 1) && !toPath.substring(0, 1).equals(fromPath.substring(0, 1))) {
            return null;
        }
        if ((toPath.startsWith(":", 1) && !fromPath.startsWith(":", 1)) || (!toPath.startsWith(":", 1) && fromPath.startsWith(":", 1))) {
            return null;
        }
        final String resultPath = buildRelativePath(toPath, fromPath, File.separatorChar);
        if (newPath.endsWith(File.separator) && !resultPath.endsWith(File.separator)) {
            return resultPath + File.separator;
        }
        return resultPath;
    }
    
    private static final String determineRelativePath(final String filename, final String separator) {
        if (filename.length() == 0) {
            return "";
        }
        final int slashCount = StringUtils.countMatches(filename, separator) - 1;
        if (slashCount <= 0) {
            return ".";
        }
        final StringBuffer sb = new StringBuffer();
        for (int i = 0; i < slashCount; ++i) {
            sb.append("../");
        }
        return StringUtils.chop(sb.toString());
    }
    
    private static final String determineSeparator(final String filename) {
        final int forwardCount = StringUtils.countMatches(filename, "/");
        final int backwardCount = StringUtils.countMatches(filename, "\\");
        return (forwardCount >= backwardCount) ? "/" : "\\";
    }
    
    static final String uppercaseDrive(String path) {
        if (path == null) {
            return null;
        }
        if (path.length() >= 2 && path.charAt(1) == ':') {
            path = Character.toUpperCase(path.charAt(0)) + path.substring(1);
        }
        return path;
    }
    
    private static final String buildRelativePath(final String toPath, final String fromPath, final char separatorChar) {
        StringTokenizer toTokeniser = new StringTokenizer(toPath, String.valueOf(separatorChar));
        StringTokenizer fromTokeniser = new StringTokenizer(fromPath, String.valueOf(separatorChar));
        int count = 0;
        while (toTokeniser.hasMoreTokens() && fromTokeniser.hasMoreTokens()) {
            if (separatorChar == '\\') {
                if (!fromTokeniser.nextToken().equalsIgnoreCase(toTokeniser.nextToken())) {
                    break;
                }
            }
            else if (!fromTokeniser.nextToken().equals(toTokeniser.nextToken())) {
                break;
            }
            ++count;
        }
        toTokeniser = new StringTokenizer(toPath, String.valueOf(separatorChar));
        fromTokeniser = new StringTokenizer(fromPath, String.valueOf(separatorChar));
        while (count-- > 0) {
            fromTokeniser.nextToken();
            toTokeniser.nextToken();
        }
        String relativePath = "";
        while (fromTokeniser.hasMoreTokens()) {
            fromTokeniser.nextToken();
            relativePath += "..";
            if (fromTokeniser.hasMoreTokens()) {
                relativePath += separatorChar;
            }
        }
        if (relativePath.length() != 0 && toTokeniser.hasMoreTokens()) {
            relativePath += separatorChar;
        }
        while (toTokeniser.hasMoreTokens()) {
            relativePath += toTokeniser.nextToken();
            if (toTokeniser.hasMoreTokens()) {
                relativePath += separatorChar;
            }
        }
        return relativePath;
    }
}
