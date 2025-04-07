// 
// Decompiled by Procyon v0.5.36
// 

package org.pitest.coverage.execute;

import org.pitest.util.Log;
import java.util.Iterator;
import java.util.List;
import java.util.logging.Logger;
import org.pitest.util.SafeDataOutputStream;
import org.pitest.functional.SideEffect1;

final class SendData implements SideEffect1<SafeDataOutputStream>
{
    private static final Logger LOG;
    private final CoverageOptions arguments;
    private final List<String> testClasses;
    
    SendData(final CoverageOptions arguments, final List<String> testClasses) {
        this.arguments = arguments;
        this.testClasses = testClasses;
    }
    
    @Override
    public void apply(final SafeDataOutputStream dos) {
        this.sendArguments(dos);
        this.sendTests(dos);
    }
    
    private void sendArguments(final SafeDataOutputStream dos) {
        dos.write(this.arguments);
        dos.flush();
    }
    
    private void sendTests(final SafeDataOutputStream dos) {
        SendData.LOG.info("Sending " + this.testClasses.size() + " test classes to minion");
        dos.writeInt(this.testClasses.size());
        for (final String tc : this.testClasses) {
            dos.writeString(tc);
        }
        dos.flush();
        SendData.LOG.info("Sent tests to minion");
    }
    
    static {
        LOG = Log.getLogger();
    }
}
