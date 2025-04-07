// 
// Decompiled by Procyon v0.5.36
// 

package org.pitest.coverage.execute;

import org.pitest.util.ReceiveStrategy;
import org.pitest.util.SafeDataOutputStream;
import org.pitest.coverage.CoverageResult;
import org.pitest.functional.SideEffect1;
import java.util.List;
import java.net.ServerSocket;
import org.pitest.util.CommunicationThread;

public class CoverageCommunicationThread extends CommunicationThread
{
    public CoverageCommunicationThread(final ServerSocket socket, final CoverageOptions arguments, final List<String> tus, final SideEffect1<CoverageResult> handler) {
        super(socket, new SendData(arguments, tus), new Receive(handler));
    }
}
