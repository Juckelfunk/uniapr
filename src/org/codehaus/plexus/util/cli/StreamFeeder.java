// 
// Decompiled by Procyon v0.5.36
// 

package org.codehaus.plexus.util.cli;

import java.io.IOException;
import java.io.OutputStream;
import java.io.InputStream;

public class StreamFeeder extends Thread
{
    private InputStream input;
    private OutputStream output;
    private boolean done;
    
    public StreamFeeder(final InputStream input, final OutputStream output) {
        this.input = input;
        this.output = output;
    }
    
    public void run() {
        try {
            this.feed();
        }
        catch (Throwable ex) {
            this.close();
            this.done = true;
            synchronized (this) {
                this.notifyAll();
            }
        }
        finally {
            this.close();
            this.done = true;
            synchronized (this) {
                this.notifyAll();
            }
        }
    }
    
    public void close() {
        if (this.input != null) {
            synchronized (this.input) {
                try {
                    this.input.close();
                }
                catch (IOException ex) {}
                this.input = null;
            }
        }
        if (this.output != null) {
            synchronized (this.output) {
                try {
                    this.output.close();
                }
                catch (IOException ex2) {}
                this.output = null;
            }
        }
    }
    
    public boolean isDone() {
        return this.done;
    }
    
    private void feed() throws IOException {
        for (int data = this.input.read(); !this.done && data != -1; data = this.input.read()) {
            synchronized (this.output) {
                this.output.write(data);
            }
        }
    }
}
