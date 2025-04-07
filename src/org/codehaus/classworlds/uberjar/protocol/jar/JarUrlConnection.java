// 
// Decompiled by Procyon v0.5.36
// 

package org.codehaus.classworlds.uberjar.protocol.jar;

import java.net.URLDecoder;
import java.util.jar.JarFile;
import java.util.jar.JarEntry;
import java.util.jar.JarInputStream;
import org.codehaus.classworlds.UrlUtils;
import java.io.IOException;
import java.util.List;
import java.net.MalformedURLException;
import java.util.StringTokenizer;
import java.util.ArrayList;
import java.io.InputStream;
import java.net.URL;
import java.net.JarURLConnection;

public class JarUrlConnection extends JarURLConnection
{
    private URL baseResource;
    private String[] segments;
    private InputStream in;
    
    JarUrlConnection(URL url) throws IOException {
        super(url = normaliseURL(url));
        final String baseText = url.getPath();
        final int bangLoc = baseText.indexOf("!");
        final String baseResourceText = baseText.substring(0, bangLoc);
        String extraText = "";
        if (bangLoc <= baseText.length() - 2 && baseText.charAt(bangLoc + 1) == '/') {
            if (bangLoc + 2 == baseText.length()) {
                extraText = "";
            }
            else {
                extraText = baseText.substring(bangLoc + 1);
            }
            final List segments = new ArrayList();
            final StringTokenizer tokens = new StringTokenizer(extraText, "!");
            while (tokens.hasMoreTokens()) {
                segments.add(tokens.nextToken());
            }
            this.segments = segments.toArray(new String[segments.size()]);
            this.baseResource = new URL(baseResourceText);
            return;
        }
        throw new MalformedURLException("No !/ in url: " + url.toExternalForm());
    }
    
    protected static URL normaliseURL(final URL url) throws MalformedURLException {
        String text = UrlUtils.normalizeUrlPath(url.toString());
        if (!text.startsWith("jar:")) {
            text = "jar:" + text;
        }
        if (text.indexOf(33) < 0) {
            text += "!/";
        }
        return new URL(text);
    }
    
    protected String[] getSegments() {
        return this.segments;
    }
    
    protected URL getBaseResource() {
        return this.baseResource;
    }
    
    public void connect() throws IOException {
        if (this.segments.length == 0) {
            this.setupBaseResourceInputStream();
        }
        else {
            this.setupPathedInputStream();
        }
    }
    
    protected void setupBaseResourceInputStream() throws IOException {
        this.in = this.getBaseResource().openStream();
    }
    
    protected void setupPathedInputStream() throws IOException {
        InputStream curIn = this.getBaseResource().openStream();
        for (int i = 0; i < this.segments.length; ++i) {
            curIn = this.getSegmentInputStream(curIn, this.segments[i]);
        }
        this.in = curIn;
    }
    
    protected InputStream getSegmentInputStream(final InputStream baseIn, final String segment) throws IOException {
        final JarInputStream jarIn = new JarInputStream(baseIn);
        JarEntry entry = null;
        while (jarIn.available() != 0) {
            entry = jarIn.getNextJarEntry();
            if (entry == null) {
                break;
            }
            if (("/" + entry.getName()).equals(segment)) {
                return jarIn;
            }
        }
        throw new IOException("unable to locate segment: " + segment);
    }
    
    public InputStream getInputStream() throws IOException {
        if (this.in == null) {
            this.connect();
        }
        return this.in;
    }
    
    public JarFile getJarFile() throws IOException {
        String url = this.baseResource.toExternalForm();
        if (url.startsWith("file:/")) {
            url = url.substring(6);
        }
        return new JarFile(URLDecoder.decode(url, "UTF-8"));
    }
}
