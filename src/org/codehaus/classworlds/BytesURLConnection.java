// 
// Decompiled by Procyon v0.5.36
// 

package org.codehaus.classworlds;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;

public class BytesURLConnection extends URLConnection
{
    protected byte[] content;
    protected int offset;
    protected int length;
    
    public BytesURLConnection(final URL url, final byte[] content) {
        super(url);
        this.content = content;
    }
    
    public void connect() {
    }
    
    public InputStream getInputStream() {
        return new ByteArrayInputStream(this.content);
    }
}
