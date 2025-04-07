// 
// Decompiled by Procyon v0.5.36
// 

package org.codehaus.plexus.util.cli;

import java.io.Writer;
import org.codehaus.plexus.util.IOUtil;
import java.io.Reader;
import java.io.InputStreamReader;
import java.io.InputStream;
import java.io.PrintWriter;
import java.io.BufferedReader;

public class StreamPumper extends Thread
{
    private BufferedReader in;
    private StreamConsumer consumer;
    private PrintWriter out;
    private static final int SIZE = 1024;
    boolean done;
    
    public StreamPumper(final InputStream in) {
        this.consumer = null;
        this.out = null;
        this.in = new BufferedReader(new InputStreamReader(in), 1024);
    }
    
    public StreamPumper(final InputStream in, final StreamConsumer consumer) {
        this(in);
        this.consumer = consumer;
    }
    
    public StreamPumper(final InputStream in, final PrintWriter writer) {
        this(in);
        this.out = writer;
    }
    
    public StreamPumper(final InputStream in, final PrintWriter writer, final StreamConsumer consumer) {
        this(in);
        this.out = writer;
        this.consumer = consumer;
    }
    
    public void run() {
        try {
            for (String s = this.in.readLine(); s != null; s = this.in.readLine()) {
                this.consumeLine(s);
                if (this.out != null) {
                    this.out.println(s);
                    this.out.flush();
                }
            }
        }
        catch (Throwable e) {
            IOUtil.close(this.in);
            this.done = true;
            synchronized (this) {
                this.notifyAll();
            }
        }
        finally {
            IOUtil.close(this.in);
            this.done = true;
            synchronized (this) {
                this.notifyAll();
            }
        }
    }
    
    public void flush() {
        if (this.out != null) {
            this.out.flush();
        }
    }
    
    public void close() {
        IOUtil.close(this.out);
    }
    
    public boolean isDone() {
        return this.done;
    }
    
    private void consumeLine(final String line) {
        if (this.consumer != null) {
            this.consumer.consumeLine(line);
        }
    }
}
