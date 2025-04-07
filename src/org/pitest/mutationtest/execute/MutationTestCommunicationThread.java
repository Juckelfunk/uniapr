// 
// Decompiled by Procyon v0.5.36
// 

package org.pitest.mutationtest.execute;

import org.pitest.mutationtest.DetectionStatus;
import org.pitest.util.SafeDataInputStream;
import org.pitest.util.Log;
import org.pitest.util.ReceiveStrategy;
import org.pitest.util.SafeDataOutputStream;
import org.pitest.functional.SideEffect1;
import java.net.ServerSocket;
import org.pitest.mutationtest.MutationStatusTestPair;
import org.pitest.mutationtest.engine.MutationIdentifier;
import java.util.Map;
import java.util.logging.Logger;
import org.pitest.util.CommunicationThread;

public class MutationTestCommunicationThread extends CommunicationThread
{
    private static final Logger LOG;
    private final Map<MutationIdentifier, MutationStatusTestPair> idMap;
    
    public MutationTestCommunicationThread(final ServerSocket socket, final MinionArguments arguments, final Map<MutationIdentifier, MutationStatusTestPair> idMap) {
        super(socket, new SendData(arguments), new Receive(idMap));
        this.idMap = idMap;
    }
    
    public MutationStatusTestPair getStatus(final MutationIdentifier id) {
        return this.idMap.get(id);
    }
    
    static {
        LOG = Log.getLogger();
    }
    
    private static class SendData implements SideEffect1<SafeDataOutputStream>
    {
        private final MinionArguments arguments;
        
        SendData(final MinionArguments arguments) {
            this.arguments = arguments;
        }
        
        @Override
        public void apply(final SafeDataOutputStream dos) {
            dos.write(this.arguments);
            dos.flush();
        }
    }
    
    private static class Receive implements ReceiveStrategy
    {
        private final Map<MutationIdentifier, MutationStatusTestPair> idMap;
        
        Receive(final Map<MutationIdentifier, MutationStatusTestPair> idMap) {
            this.idMap = idMap;
        }
        
        @Override
        public void apply(final byte control, final SafeDataInputStream is) {
            switch (control) {
                case 1: {
                    this.handleDescribe(is);
                    break;
                }
                case 2: {
                    this.handleReport(is);
                    break;
                }
            }
        }
        
        private void handleReport(final SafeDataInputStream is) {
            final MutationIdentifier mutation = is.read(MutationIdentifier.class);
            final MutationStatusTestPair value = is.read(MutationStatusTestPair.class);
            this.idMap.put(mutation, value);
            MutationTestCommunicationThread.LOG.fine(mutation + " " + value);
        }
        
        private void handleDescribe(final SafeDataInputStream is) {
            final MutationIdentifier mutation = is.read(MutationIdentifier.class);
            this.idMap.put(mutation, new MutationStatusTestPair(1, DetectionStatus.STARTED));
        }
    }
}
