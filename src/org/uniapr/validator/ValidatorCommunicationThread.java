// 
// Decompiled by Procyon v0.5.36
// 

package org.uniapr.validator;

import java.util.Iterator;
import org.uniapr.ValidationStatus;
import org.uniapr.PatchId;
import org.pitest.util.SafeDataInputStream;
import org.uniapr.ValidationStatusTable;
import org.uniapr.Patch;
import java.util.Collection;
import org.pitest.util.ReceiveStrategy;
import org.pitest.util.SafeDataOutputStream;
import org.pitest.functional.SideEffect1;
import java.net.ServerSocket;
import org.pitest.util.CommunicationThread;

class ValidatorCommunicationThread extends CommunicationThread
{
    private final DataReceiver receiver;
    
    public ValidatorCommunicationThread(final ServerSocket socket, final ValidatorArguments arguments) {
        this(socket, new DataSender(arguments), new DataReceiver());
    }
    
    public ValidatorCommunicationThread(final ServerSocket socket, final DataSender sender, final DataReceiver receiver) {
        super(socket, sender, receiver);
        this.receiver = receiver;
    }
    
    public void checkInPatches(final Collection<Patch> patches) {
        this.receiver.checkInPatches(patches);
    }
    
    public ValidationStatusTable getStatusTable() {
        return this.receiver.statusTable;
    }
    
    protected static class DataSender implements SideEffect1<SafeDataOutputStream>
    {
        private final ValidatorArguments arguments;
        
        DataSender(final ValidatorArguments arguments) {
            this.arguments = arguments;
        }
        
        @Override
        public void apply(final SafeDataOutputStream dos) {
            dos.write(this.arguments);
        }
    }
    
    protected static class DataReceiver implements ReceiveStrategy
    {
        final ValidationStatusTable statusTable;
        
        public DataReceiver() {
            this.statusTable = new ValidationStatusTable();
        }
        
        @Override
        public void apply(final byte control, final SafeDataInputStream dis) {
            final PatchId id = dis.read(PatchId.class);
            switch (control) {
                case 1: {
                    this.statusTable.put(id, ValidationStatus.STARTED);
                    break;
                }
                case 2: {
                    final ValidationStatus status = dis.read(ValidationStatus.class);
                    this.statusTable.put(id, status);
                    break;
                }
            }
        }
        
        public void checkInPatches(final Collection<Patch> patches) {
            for (final Patch patch : patches) {
                this.statusTable.put(patch.getId(), ValidationStatus.NOT_STARTED);
            }
        }
    }
}
