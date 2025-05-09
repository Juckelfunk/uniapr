// 
// Decompiled by Procyon v0.5.36
// 

package edu.utdallas.prf.reloc.apache.commons.io.output;

import java.io.IOException;
import java.io.OutputStream;

public class DemuxOutputStream extends OutputStream
{
    private final InheritableThreadLocal<OutputStream> outputStreamThreadLocal;
    
    public DemuxOutputStream() {
        this.outputStreamThreadLocal = new InheritableThreadLocal<OutputStream>();
    }
    
    public OutputStream bindStream(final OutputStream output) {
        final OutputStream stream = this.outputStreamThreadLocal.get();
        this.outputStreamThreadLocal.set(output);
        return stream;
    }
    
    @Override
    public void close() throws IOException {
        final OutputStream output = this.outputStreamThreadLocal.get();
        if (null != output) {
            output.close();
        }
    }
    
    @Override
    public void flush() throws IOException {
        final OutputStream output = this.outputStreamThreadLocal.get();
        if (null != output) {
            output.flush();
        }
    }
    
    @Override
    public void write(final int ch) throws IOException {
        final OutputStream output = this.outputStreamThreadLocal.get();
        if (null != output) {
            output.write(ch);
        }
    }
}
