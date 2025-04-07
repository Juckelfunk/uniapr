// 
// Decompiled by Procyon v0.5.36
// 

package org.codehaus.classworlds.uberjar.protocol.jar;

import java.io.IOException;
import java.net.URLConnection;
import java.net.URL;
import java.net.URLStreamHandler;

public class Handler extends URLStreamHandler
{
    private static final Handler INSTANCE;
    
    public static Handler getInstance() {
        return Handler.INSTANCE;
    }
    
    public URLConnection openConnection(final URL url) throws IOException {
        return new JarUrlConnection(url);
    }
    
    public void parseURL(final URL url, final String spec, final int start, final int limit) {
        final String specPath = spec.substring(start, limit);
        String urlPath = null;
        if (specPath.charAt(0) == '/') {
            urlPath = specPath;
        }
        else if (specPath.charAt(0) == '!') {
            final String relPath = url.getFile();
            final int bangLoc = relPath.lastIndexOf("!");
            if (bangLoc < 0) {
                urlPath = relPath + specPath;
            }
            else {
                urlPath = relPath.substring(0, bangLoc) + specPath;
            }
        }
        else {
            final String relPath = url.getFile();
            if (relPath != null) {
                final int lastSlashLoc = relPath.lastIndexOf("/");
                if (lastSlashLoc < 0) {
                    urlPath = "/" + specPath;
                }
                else {
                    urlPath = relPath.substring(0, lastSlashLoc + 1) + specPath;
                }
            }
            else {
                urlPath = specPath;
            }
        }
        this.setURL(url, "jar", "", 0, null, null, urlPath, null, null);
    }
    
    static {
        INSTANCE = new Handler();
    }
}
