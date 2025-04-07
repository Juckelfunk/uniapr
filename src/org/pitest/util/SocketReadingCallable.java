// 
// Decompiled by Procyon v0.5.36
// 

package org.pitest.util;

import java.io.OutputStream;
import java.net.Socket;
import java.io.IOException;
import java.io.InputStream;
import java.io.BufferedInputStream;
import java.net.ServerSocket;
import org.pitest.functional.SideEffect1;
import java.util.concurrent.Callable;

class SocketReadingCallable implements Callable<ExitCode>
{
    private final SideEffect1<SafeDataOutputStream> sendInitialData;
    private final ReceiveStrategy receive;
    private final ServerSocket socket;
    
    SocketReadingCallable(final ServerSocket socket, final SideEffect1<SafeDataOutputStream> sendInitialData, final ReceiveStrategy receive) {
        this.socket = socket;
        this.sendInitialData = sendInitialData;
        this.receive = receive;
    }
    
    @Override
    public ExitCode call() throws Exception {
        try (final Socket clientSocket = this.socket.accept()) {
            try (final BufferedInputStream bif = new BufferedInputStream(clientSocket.getInputStream())) {
                this.sendDataToMinion(clientSocket);
                final SafeDataInputStream is = new SafeDataInputStream(bif);
                return this.receiveResults(is);
            }
            catch (IOException e) {
                throw Unchecked.translateCheckedException(e);
            }
        }
        finally {
            try {
                this.socket.close();
            }
            catch (IOException e2) {
                throw Unchecked.translateCheckedException(e2);
            }
        }
    }
    
    private void sendDataToMinion(final Socket clientSocket) throws IOException {
        final OutputStream os = clientSocket.getOutputStream();
        final SafeDataOutputStream dos = new SafeDataOutputStream(os);
        this.sendInitialData.apply(dos);
    }
    
    private ExitCode receiveResults(final SafeDataInputStream is) {
        for (byte control = is.readByte(); control != 64; control = is.readByte()) {
            this.receive.apply(control, is);
        }
        return ExitCode.fromCode(is.readInt());
    }
}
