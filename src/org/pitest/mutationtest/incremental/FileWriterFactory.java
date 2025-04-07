// 
// Decompiled by Procyon v0.5.36
// 

package org.pitest.mutationtest.incremental;

import java.io.IOException;
import org.pitest.util.Unchecked;
import java.io.Writer;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.FileOutputStream;
import java.io.PrintWriter;
import java.io.File;

public class FileWriterFactory implements WriterFactory
{
    private final File file;
    private PrintWriter writer;
    
    public FileWriterFactory(final File file) {
        this.file = file;
    }
    
    @Override
    public PrintWriter create() {
        this.file.getParentFile().mkdirs();
        try {
            if (this.writer == null) {
                this.writer = new PrintWriter(new OutputStreamWriter(new FileOutputStream(this.file), "UTF-8"));
            }
            return this.writer;
        }
        catch (IOException e) {
            throw Unchecked.translateCheckedException(e);
        }
    }
    
    @Override
    public void close() {
        if (this.writer != null) {
            this.writer.close();
        }
    }
}
