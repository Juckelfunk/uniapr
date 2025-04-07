// 
// Decompiled by Procyon v0.5.36
// 

package org.codehaus.plexus.util.cli;

import java.io.Writer;
import java.io.PrintWriter;

public class WriterStreamConsumer implements StreamConsumer
{
    private PrintWriter writer;
    
    public WriterStreamConsumer(final Writer writer) {
        this.writer = new PrintWriter(writer);
    }
    
    public void consumeLine(final String line) {
        this.writer.println(line);
        this.writer.flush();
    }
}
