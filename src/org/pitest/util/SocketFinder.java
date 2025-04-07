// 
// Decompiled by Procyon v0.5.36
// 

package org.pitest.util;

import java.io.IOException;
import java.net.ServerSocket;

public class SocketFinder
{
    public synchronized ServerSocket getNextAvailableServerSocket() {
        try {
            return new ServerSocket(0);
        }
        catch (IOException e) {
            throw Unchecked.translateCheckedException(e);
        }
    }
}
