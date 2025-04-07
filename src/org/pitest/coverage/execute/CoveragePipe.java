// 
// Decompiled by Procyon v0.5.36
// 

package org.pitest.coverage.execute;

import org.pitest.util.ExitCode;
import java.util.Iterator;
import java.util.Collection;
import org.pitest.testapi.Description;
import sun.pitest.CodeCoverageStore;
import java.io.OutputStream;
import org.pitest.util.SafeDataOutputStream;
import org.pitest.coverage.CoverageReceiver;

public class CoveragePipe implements CoverageReceiver
{
    private final SafeDataOutputStream dos;
    
    public CoveragePipe(final OutputStream dos) {
        this.dos = new SafeDataOutputStream(dos);
    }
    
    @Override
    public synchronized void newTest() {
        CodeCoverageStore.reset();
    }
    
    @Override
    public synchronized void recordTestOutcome(final Description description, final boolean wasGreen, final int executionTime) {
        final Collection<Long> hits = CodeCoverageStore.getHits();
        this.dos.writeByte((byte)16);
        this.dos.write(description);
        this.dos.writeInt(hits.size());
        for (final Long each : hits) {
            this.dos.writeLong(each);
        }
        this.dos.writeBoolean(wasGreen);
        this.dos.writeInt(executionTime);
    }
    
    public synchronized void end(final ExitCode exitCode) {
        this.dos.writeByte((byte)64);
        this.dos.writeInt(exitCode.getCode());
        this.dos.flush();
    }
    
    @Override
    public synchronized void registerClass(final int id, final String className) {
        this.dos.writeByte((byte)32);
        this.dos.writeInt(id);
        this.dos.writeString(className);
    }
    
    @Override
    public synchronized void registerProbes(final int classId, final String methodName, final String methodDesc, final int firstProbe, final int lastProbe) {
        this.dos.writeByte((byte)4);
        this.dos.writeInt(classId);
        this.dos.writeString(methodName);
        this.dos.writeString(methodDesc);
        this.dos.writeInt(firstProbe);
        this.dos.writeInt(lastProbe);
    }
}
