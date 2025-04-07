// 
// Decompiled by Procyon v0.5.36
// 

package org.pitest.process;

import org.pitest.util.StreamMonitor;
import org.pitest.functional.SideEffect1;
import org.pitest.util.Monitor;

public class JavaProcess
{
    private final Process process;
    private final Monitor out;
    private final Monitor err;
    
    public JavaProcess(final Process process, final SideEffect1<String> sysoutHandler, final SideEffect1<String> syserrHandler) {
        this.process = process;
        this.out = new StreamMonitor(process.getInputStream(), sysoutHandler);
        this.err = new StreamMonitor(process.getErrorStream(), syserrHandler);
        this.out.requestStart();
        this.err.requestStart();
    }
    
    public void destroy() {
        this.out.requestStop();
        this.err.requestStop();
        this.process.destroy();
    }
    
    public int waitToDie() throws InterruptedException {
        final int exitVal = this.process.waitFor();
        this.out.requestStop();
        this.err.requestStop();
        return exitVal;
    }
    
    public boolean isAlive() {
        try {
            this.process.exitValue();
            return false;
        }
        catch (IllegalThreadStateException e) {
            return true;
        }
    }
}
