// 
// Decompiled by Procyon v0.5.36
// 

package org.pitest.mutationtest.execute;

import org.pitest.util.ExitCode;
import org.pitest.mutationtest.MutationStatusTestPair;
import java.io.IOException;
import org.pitest.mutationtest.engine.MutationIdentifier;
import java.io.OutputStream;
import org.pitest.util.SafeDataOutputStream;

public class DefaultReporter implements Reporter
{
    private final SafeDataOutputStream w;
    
    DefaultReporter(final OutputStream w) {
        this.w = new SafeDataOutputStream(w);
    }
    
    @Override
    public synchronized void describe(final MutationIdentifier i) throws IOException {
        this.w.writeByte((byte)1);
        this.w.write(i);
        this.w.flush();
    }
    
    @Override
    public synchronized void report(final MutationIdentifier i, final MutationStatusTestPair mutationDetected) throws IOException {
        this.w.writeByte((byte)2);
        this.w.write(i);
        this.w.write(mutationDetected);
        this.w.flush();
    }
    
    @Override
    public synchronized void done(final ExitCode exitCode) {
        this.w.writeByte((byte)64);
        this.w.writeInt(exitCode.getCode());
        this.w.flush();
    }
}
