// 
// Decompiled by Procyon v0.5.36
// 

package org.uniapr.validator;

import org.uniapr.ValidationStatus;
import org.uniapr.PatchId;
import java.io.OutputStream;
import org.uniapr.commons.process.AbstractReporter;

class ValidatorReporter extends AbstractReporter
{
    public ValidatorReporter(final OutputStream os) {
        super(os);
    }
    
    public synchronized void reportStatus(final PatchId id, final ValidationStatus status) {
        this.dos.writeByte((byte)2);
        this.dos.write(id);
        this.dos.write(status);
        this.dos.flush();
    }
    
    public synchronized void notifyStarted(final PatchId id) {
        this.dos.writeByte((byte)1);
        this.dos.write(id);
        this.dos.flush();
    }
}
