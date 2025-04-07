// 
// Decompiled by Procyon v0.5.36
// 

package org.codehaus.classworlds;

import java.net.URLConnection;
import java.net.URL;
import java.net.URLStreamHandler;

public class BytesURLStreamHandler extends URLStreamHandler
{
    byte[] content;
    int offset;
    int length;
    
    public BytesURLStreamHandler(final byte[] content) {
        this.content = content;
    }
    
    public URLConnection openConnection(final URL url) {
        return new BytesURLConnection(url, this.content);
    }
}
