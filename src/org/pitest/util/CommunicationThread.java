// 
// Decompiled by Procyon v0.5.36
// 

package org.pitest.util;

import java.util.concurrent.ExecutionException;
import java.util.logging.Level;
import java.util.concurrent.Callable;
import java.io.IOException;
import java.util.concurrent.FutureTask;
import java.net.ServerSocket;
import org.pitest.functional.SideEffect1;
import java.util.logging.Logger;

public class CommunicationThread
{
    private static final Logger LOG;
    private final SideEffect1<SafeDataOutputStream> sendInitialData;
    private final ReceiveStrategy receive;
    private final ServerSocket socket;
    private FutureTask<ExitCode> future;
    
    public CommunicationThread(final ServerSocket socket, final SideEffect1<SafeDataOutputStream> sendInitialData, final ReceiveStrategy receive) {
        this.socket = socket;
        this.sendInitialData = sendInitialData;
        this.receive = receive;
    }
    
    public void start() throws IOException, InterruptedException {
        this.future = this.createFuture();
    }
    
    private FutureTask<ExitCode> createFuture() {
        final FutureTask<ExitCode> newFuture = new FutureTask<ExitCode>(new SocketReadingCallable(this.socket, this.sendInitialData, this.receive));
        final Thread thread = new Thread(newFuture);
        thread.setDaemon(true);
        thread.setName("pit communication");
        thread.start();
        return newFuture;
    }
    
    public ExitCode waitToFinish() {
        try {
            return this.future.get();
        }
        catch (ExecutionException e) {
            CommunicationThread.LOG.log(Level.WARNING, "Error while watching child process", e);
            return ExitCode.UNKNOWN_ERROR;
        }
        catch (InterruptedException e2) {
            CommunicationThread.LOG.log(Level.WARNING, "interrupted while waiting for child process", e2);
            return ExitCode.UNKNOWN_ERROR;
        }
    }
    
    static {
        LOG = Log.getLogger();
    }
}
