// 
// Decompiled by Procyon v0.5.36
// 

package org.codehaus.plexus.util.cli.shell;

import java.io.File;
import org.codehaus.plexus.util.StringUtils;
import java.util.Collection;
import java.util.Arrays;
import java.util.ArrayList;
import java.util.List;

public class Shell implements Cloneable
{
    private static final char[] DEFAULT_QUOTING_TRIGGER_CHARS;
    private String shellCommand;
    private List shellArgs;
    private boolean quotedArgumentsEnabled;
    private String executable;
    private String workingDir;
    private boolean quotedExecutableEnabled;
    private boolean doubleQuotedArgumentEscaped;
    private boolean singleQuotedArgumentEscaped;
    private boolean doubleQuotedExecutableEscaped;
    private boolean singleQuotedExecutableEscaped;
    private char argQuoteDelimiter;
    private char exeQuoteDelimiter;
    
    public Shell() {
        this.shellArgs = new ArrayList();
        this.quotedArgumentsEnabled = true;
        this.quotedExecutableEnabled = true;
        this.doubleQuotedArgumentEscaped = false;
        this.singleQuotedArgumentEscaped = false;
        this.doubleQuotedExecutableEscaped = false;
        this.singleQuotedExecutableEscaped = false;
        this.argQuoteDelimiter = '\"';
        this.exeQuoteDelimiter = '\"';
    }
    
    public void setShellCommand(final String shellCommand) {
        this.shellCommand = shellCommand;
    }
    
    public String getShellCommand() {
        return this.shellCommand;
    }
    
    public void setShellArgs(final String[] shellArgs) {
        this.shellArgs.clear();
        this.shellArgs.addAll(Arrays.asList(shellArgs));
    }
    
    public String[] getShellArgs() {
        if (this.shellArgs == null || this.shellArgs.isEmpty()) {
            return null;
        }
        return this.shellArgs.toArray(new String[this.shellArgs.size()]);
    }
    
    public List getCommandLine(final String executable, final String[] arguments) {
        return this.getRawCommandLine(executable, arguments);
    }
    
    protected List getRawCommandLine(final String executable, final String[] arguments) {
        final List commandLine = new ArrayList();
        final StringBuffer sb = new StringBuffer();
        if (executable != null) {
            final String preamble = this.getExecutionPreamble();
            if (preamble != null) {
                sb.append(preamble);
            }
            if (this.isQuotedExecutableEnabled()) {
                final char[] escapeChars = this.getEscapeChars(this.isSingleQuotedExecutableEscaped(), this.isDoubleQuotedExecutableEscaped());
                sb.append(StringUtils.quoteAndEscape(this.getExecutable(), this.getExecutableQuoteDelimiter(), escapeChars, this.getQuotingTriggerChars(), '\\', false));
            }
            else {
                sb.append(this.getExecutable());
            }
        }
        for (int i = 0; i < arguments.length; ++i) {
            if (sb.length() > 0) {
                sb.append(" ");
            }
            if (this.isQuotedArgumentsEnabled()) {
                final char[] escapeChars = this.getEscapeChars(this.isSingleQuotedExecutableEscaped(), this.isDoubleQuotedExecutableEscaped());
                sb.append(StringUtils.quoteAndEscape(arguments[i], this.getArgumentQuoteDelimiter(), escapeChars, this.getQuotingTriggerChars(), '\\', false));
            }
            else {
                sb.append(arguments[i]);
            }
        }
        commandLine.add(sb.toString());
        return commandLine;
    }
    
    protected char[] getQuotingTriggerChars() {
        return Shell.DEFAULT_QUOTING_TRIGGER_CHARS;
    }
    
    protected String getExecutionPreamble() {
        return null;
    }
    
    protected char[] getEscapeChars(final boolean includeSingleQuote, final boolean includeDoubleQuote) {
        final StringBuffer buf = new StringBuffer(2);
        if (includeSingleQuote) {
            buf.append('\'');
        }
        if (includeDoubleQuote) {
            buf.append('\"');
        }
        final char[] result = new char[buf.length()];
        buf.getChars(0, buf.length(), result, 0);
        return result;
    }
    
    protected boolean isDoubleQuotedArgumentEscaped() {
        return this.doubleQuotedArgumentEscaped;
    }
    
    protected boolean isSingleQuotedArgumentEscaped() {
        return this.singleQuotedArgumentEscaped;
    }
    
    protected boolean isDoubleQuotedExecutableEscaped() {
        return this.doubleQuotedExecutableEscaped;
    }
    
    protected boolean isSingleQuotedExecutableEscaped() {
        return this.singleQuotedExecutableEscaped;
    }
    
    protected void setArgumentQuoteDelimiter(final char argQuoteDelimiter) {
        this.argQuoteDelimiter = argQuoteDelimiter;
    }
    
    protected char getArgumentQuoteDelimiter() {
        return this.argQuoteDelimiter;
    }
    
    protected void setExecutableQuoteDelimiter(final char exeQuoteDelimiter) {
        this.exeQuoteDelimiter = exeQuoteDelimiter;
    }
    
    protected char getExecutableQuoteDelimiter() {
        return this.exeQuoteDelimiter;
    }
    
    public List getShellCommandLine(final String[] arguments) {
        final List commandLine = new ArrayList();
        if (this.getShellCommand() != null) {
            commandLine.add(this.getShellCommand());
        }
        if (this.getShellArgs() != null) {
            commandLine.addAll(this.getShellArgsList());
        }
        commandLine.addAll(this.getCommandLine(this.getExecutable(), arguments));
        return commandLine;
    }
    
    public List getShellArgsList() {
        return this.shellArgs;
    }
    
    public void addShellArg(final String arg) {
        this.shellArgs.add(arg);
    }
    
    public void setQuotedArgumentsEnabled(final boolean quotedArgumentsEnabled) {
        this.quotedArgumentsEnabled = quotedArgumentsEnabled;
    }
    
    public boolean isQuotedArgumentsEnabled() {
        return this.quotedArgumentsEnabled;
    }
    
    public void setQuotedExecutableEnabled(final boolean quotedExecutableEnabled) {
        this.quotedExecutableEnabled = quotedExecutableEnabled;
    }
    
    public boolean isQuotedExecutableEnabled() {
        return this.quotedExecutableEnabled;
    }
    
    public void setExecutable(final String executable) {
        if (executable == null || executable.length() == 0) {
            return;
        }
        this.executable = executable.replace('/', File.separatorChar).replace('\\', File.separatorChar);
    }
    
    public String getExecutable() {
        return this.executable;
    }
    
    public void setWorkingDirectory(final String path) {
        if (path != null) {
            this.workingDir = path;
        }
    }
    
    public void setWorkingDirectory(final File workingDir) {
        if (workingDir != null) {
            this.workingDir = workingDir.getAbsolutePath();
        }
    }
    
    public File getWorkingDirectory() {
        return (this.workingDir == null) ? null : new File(this.workingDir);
    }
    
    public String getWorkingDirectoryAsString() {
        return this.workingDir;
    }
    
    public void clearArguments() {
        this.shellArgs.clear();
    }
    
    public Object clone() {
        final Shell shell = new Shell();
        shell.setExecutable(this.getExecutable());
        shell.setWorkingDirectory(this.getWorkingDirectory());
        shell.setShellArgs(this.getShellArgs());
        return shell;
    }
    
    public String getOriginalExecutable() {
        return this.executable;
    }
    
    public List getOriginalCommandLine(final String executable, final String[] arguments) {
        return this.getRawCommandLine(executable, arguments);
    }
    
    protected void setDoubleQuotedArgumentEscaped(final boolean doubleQuotedArgumentEscaped) {
        this.doubleQuotedArgumentEscaped = doubleQuotedArgumentEscaped;
    }
    
    protected void setDoubleQuotedExecutableEscaped(final boolean doubleQuotedExecutableEscaped) {
        this.doubleQuotedExecutableEscaped = doubleQuotedExecutableEscaped;
    }
    
    protected void setSingleQuotedArgumentEscaped(final boolean singleQuotedArgumentEscaped) {
        this.singleQuotedArgumentEscaped = singleQuotedArgumentEscaped;
    }
    
    protected void setSingleQuotedExecutableEscaped(final boolean singleQuotedExecutableEscaped) {
        this.singleQuotedExecutableEscaped = singleQuotedExecutableEscaped;
    }
    
    static {
        DEFAULT_QUOTING_TRIGGER_CHARS = new char[] { ' ' };
    }
}
