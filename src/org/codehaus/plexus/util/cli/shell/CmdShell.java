// 
// Decompiled by Procyon v0.5.36
// 

package org.codehaus.plexus.util.cli.shell;

import java.util.Arrays;
import java.util.List;

public class CmdShell extends Shell
{
    public CmdShell() {
        this.setShellCommand("cmd.exe");
        this.setQuotedExecutableEnabled(true);
        this.setShellArgs(new String[] { "/X", "/C" });
    }
    
    public List getCommandLine(final String executable, final String[] arguments) {
        final StringBuffer sb = new StringBuffer();
        sb.append("\"");
        sb.append(super.getCommandLine(executable, arguments).get(0));
        sb.append("\"");
        return Arrays.asList(sb.toString());
    }
}
