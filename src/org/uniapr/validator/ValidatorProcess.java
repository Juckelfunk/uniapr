// 
// Decompiled by Procyon v0.5.36
// 

package org.uniapr.validator;

import org.uniapr.ValidationStatusTable;
import org.uniapr.Patch;
import java.util.Collection;
import org.pitest.util.ExitCode;
import java.io.IOException;
import java.net.ServerSocket;
import org.pitest.util.SocketFinder;
import org.pitest.process.ProcessArgs;
import org.pitest.process.WrappingProcess;

class ValidatorProcess
{
    private final WrappingProcess process;
    private final ValidatorCommunicationThread communicationThread;
    
    public ValidatorProcess(final ProcessArgs processArgs, final ValidatorArguments arguments) {
        this(new SocketFinder().getNextAvailableServerSocket(), processArgs, arguments);
    }
    
    private ValidatorProcess(final ServerSocket socket, final ProcessArgs processArgs, final ValidatorArguments arguments) {
        this.process = new WrappingProcess(socket.getLocalPort(), processArgs, PatchValidator.class);
        this.communicationThread = new ValidatorCommunicationThread(socket, arguments);
    }
    
    public void start() throws IOException, InterruptedException {
        this.communicationThread.start();
        this.process.start();
    }
    
    public ExitCode waitToDie() {
        try {
            return this.communicationThread.waitToFinish();
        }
        finally {
            this.process.destroy();
        }
    }
    
    public void checkInPatches(final Collection<Patch> patches) {
        this.communicationThread.checkInPatches(patches);
    }
    
    public ValidationStatusTable getStatusTable() {
        return this.communicationThread.getStatusTable();
    }
}
