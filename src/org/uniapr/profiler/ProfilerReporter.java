// 
// Decompiled by Procyon v0.5.36
// 

package org.uniapr.profiler;

import java.util.Collection;
import java.io.OutputStream;
import org.uniapr.commons.process.AbstractReporter;

class ProfilerReporter extends AbstractReporter
{
    public ProfilerReporter(final OutputStream os) {
        super(os);
    }
    
    public synchronized void reportTestTime(final String testName, final long timeElapsed) {
        this.dos.writeByte((byte)2);
        this.dos.writeString(testName);
        this.dos.writeLong(timeElapsed);
        this.dos.flush();
    }
    
    public synchronized void reportFailingTestNames(final Collection<String> failingTestNames) {
        this.dos.writeByte((byte)1);
        this.dos.write(failingTestNames.toArray(new String[0]));
    }
}
