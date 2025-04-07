// 
// Decompiled by Procyon v0.5.36
// 

package org.pitest.util;

import java.io.IOException;
import org.pitest.functional.SideEffect1;
import java.io.InputStream;
import java.util.logging.Logger;

public class StreamMonitor extends Thread implements Monitor
{
    private static final Logger LOG;
    private final byte[] buf;
    private final InputStream in;
    private final SideEffect1<String> inputHandler;
    
    public StreamMonitor(final InputStream in, final SideEffect1<String> inputHandler) {
        super("PIT Stream Monitor");
        this.buf = new byte[256];
        this.in = in;
        this.inputHandler = inputHandler;
        this.setDaemon(true);
    }
    
    @Override
    public void requestStart() {
        this.start();
    }
    
    @Override
    public void run() {
        while (!this.isInterrupted()) {
            this.readFromStream();
        }
    }
    
    private void readFromStream() {
        try {
            if (this.in.available() == 0) {
                Thread.sleep(100L);
                return;
            }
            int i;
            while ((i = this.in.read(this.buf, 0, this.buf.length)) != -1) {
                final String output = new String(this.buf, 0, i);
                this.inputHandler.apply(output);
            }
        }
        catch (IOException e) {
            this.requestStop();
            StreamMonitor.LOG.fine("No longer able to read stream.");
        }
        catch (InterruptedException e2) {
            Thread.currentThread().interrupt();
        }
    }
    
    @Override
    public void requestStop() {
        this.interrupt();
    }
    
    static {
        LOG = Log.getLogger();
    }
}
