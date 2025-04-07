// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.maven.wagon;

import java.io.FileNotFoundException;
import java.io.FileDescriptor;
import java.nio.channels.FileChannel;
import java.io.IOException;
import java.io.FileOutputStream;
import java.io.File;
import java.io.OutputStream;

public class LazyFileOutputStream extends OutputStream
{
    private File file;
    private FileOutputStream delegee;
    
    public LazyFileOutputStream(final String filename) {
        this.file = new File(filename);
    }
    
    public LazyFileOutputStream(final File file) {
        this.file = file;
    }
    
    public void close() throws IOException {
        if (this.delegee != null) {
            this.delegee.close();
        }
    }
    
    public boolean equals(final Object obj) {
        return this.delegee.equals(obj);
    }
    
    public void flush() throws IOException {
        if (this.delegee != null) {
            this.delegee.flush();
        }
    }
    
    public FileChannel getChannel() {
        return this.delegee.getChannel();
    }
    
    public FileDescriptor getFD() throws IOException {
        return this.delegee.getFD();
    }
    
    public int hashCode() {
        return this.delegee.hashCode();
    }
    
    public String toString() {
        return this.delegee.toString();
    }
    
    public void write(final byte[] b) throws IOException {
        if (this.delegee == null) {
            this.initialize();
        }
        this.delegee.write(b);
    }
    
    public void write(final byte[] b, final int off, final int len) throws IOException {
        if (this.delegee == null) {
            this.initialize();
        }
        this.delegee.write(b, off, len);
    }
    
    public void write(final int b) throws IOException {
        if (this.delegee == null) {
            this.initialize();
        }
        this.delegee.write(b);
    }
    
    private void initialize() throws FileNotFoundException {
        this.delegee = new FileOutputStream(this.file);
    }
}
