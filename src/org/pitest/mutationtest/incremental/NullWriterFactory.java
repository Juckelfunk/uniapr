// 
// Decompiled by Procyon v0.5.36
// 

package org.pitest.mutationtest.incremental;

import java.io.IOException;
import java.io.OutputStream;
import java.io.Writer;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;

public class NullWriterFactory implements WriterFactory
{
    private final PrintWriter pw;
    
    public NullWriterFactory() {
        this.pw = new PrintWriter(new OutputStreamWriter(this.nullOutputStream()));
    }
    
    private OutputStream nullOutputStream() {
        return new OutputStream() {
            @Override
            public void write(final int b) throws IOException {
            }
        };
    }
    
    @Override
    public PrintWriter create() {
        return this.pw;
    }
    
    @Override
    public void close() {
        this.pw.close();
    }
}
