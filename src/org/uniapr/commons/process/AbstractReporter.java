// 
// Decompiled by Procyon v0.5.36
// 

package org.uniapr.commons.process;

import org.pitest.util.ExitCode;
import java.io.OutputStream;
import org.pitest.util.SafeDataOutputStream;

public abstract class AbstractReporter
{
    protected final SafeDataOutputStream dos;
    
    protected AbstractReporter(final OutputStream os) {
        this.dos = new SafeDataOutputStream(os);
    }
    
    public synchronized void done(final ExitCode exitCode) {
        this.dos.writeByte((byte)64);
        this.dos.writeInt(exitCode.getCode());
        this.dos.flush();
    }
}
