// 
// Decompiled by Procyon v0.5.36
// 

package org.codehaus.plexus.util;

import java.io.IOException;
import java.io.OutputStream;

public class StringOutputStream extends OutputStream
{
    private StringBuffer buf;
    
    public StringOutputStream() {
        this.buf = new StringBuffer();
    }
    
    public void write(final byte[] b) throws IOException {
        this.buf.append(new String(b));
    }
    
    public void write(final byte[] b, final int off, final int len) throws IOException {
        this.buf.append(new String(b, off, len));
    }
    
    public void write(final int b) throws IOException {
        final byte[] bytes = { (byte)b };
        this.buf.append(new String(bytes));
    }
    
    public String toString() {
        return this.buf.toString();
    }
}
