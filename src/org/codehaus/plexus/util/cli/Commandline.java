// 
// Decompiled by Procyon v0.5.36
// 

package org.codehaus.plexus.util.cli;

import java.io.IOException;
import org.codehaus.plexus.util.StringUtils;
import java.util.Iterator;
import java.util.Properties;
import org.codehaus.plexus.util.cli.shell.BourneShell;
import org.codehaus.plexus.util.cli.shell.CmdShell;
import org.codehaus.plexus.util.cli.shell.CommandShell;
import org.codehaus.plexus.util.Os;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.io.File;
import org.codehaus.plexus.util.cli.shell.Shell;
import java.util.Map;
import java.util.Vector;

public class Commandline implements Cloneable
{
    protected static final String OS_NAME = "os.name";
    protected static final String WINDOWS = "Windows";
    protected Vector arguments;
    protected Map envVars;
    private long pid;
    private Shell shell;
    protected String executable;
    private File workingDir;
    
    public Commandline(final String toProcess, final Shell shell) {
        this.arguments = new Vector();
        this.envVars = Collections.synchronizedMap(new LinkedHashMap<Object, Object>());
        this.pid = -1L;
        this.shell = shell;
        String[] tmp = new String[0];
        try {
            tmp = CommandLineUtils.translateCommandline(toProcess);
        }
        catch (Exception e) {
            System.err.println("Error translating Commandline.");
        }
        if (tmp != null && tmp.length > 0) {
            this.setExecutable(tmp[0]);
            for (int i = 1; i < tmp.length; ++i) {
                this.createArgument().setValue(tmp[i]);
            }
        }
    }
    
    public Commandline(final Shell shell) {
        this.arguments = new Vector();
        this.envVars = Collections.synchronizedMap(new LinkedHashMap<Object, Object>());
        this.pid = -1L;
        this.shell = shell;
    }
    
    public Commandline(final String toProcess) {
        this.arguments = new Vector();
        this.envVars = Collections.synchronizedMap(new LinkedHashMap<Object, Object>());
        this.pid = -1L;
        this.setDefaultShell();
        String[] tmp = new String[0];
        try {
            tmp = CommandLineUtils.translateCommandline(toProcess);
        }
        catch (Exception e) {
            System.err.println("Error translating Commandline.");
        }
        if (tmp != null && tmp.length > 0) {
            this.setExecutable(tmp[0]);
            for (int i = 1; i < tmp.length; ++i) {
                this.createArgument().setValue(tmp[i]);
            }
        }
    }
    
    public Commandline() {
        this.arguments = new Vector();
        this.envVars = Collections.synchronizedMap(new LinkedHashMap<Object, Object>());
        this.pid = -1L;
        this.setDefaultShell();
    }
    
    public long getPid() {
        if (this.pid == -1L) {
            this.pid = Long.parseLong(String.valueOf(System.currentTimeMillis()));
        }
        return this.pid;
    }
    
    public void setPid(final long pid) {
        this.pid = pid;
    }
    
    private void setDefaultShell() {
        if (Os.isFamily("windows")) {
            if (Os.isFamily("win9x")) {
                this.setShell(new CommandShell());
            }
            else {
                this.setShell(new CmdShell());
            }
        }
        else {
            this.setShell(new BourneShell());
        }
    }
    
    public Argument createArgument() {
        return this.createArgument(false);
    }
    
    public Argument createArgument(final boolean insertAtStart) {
        final Argument argument = new Argument();
        if (insertAtStart) {
            this.arguments.insertElementAt(argument, 0);
        }
        else {
            this.arguments.addElement(argument);
        }
        return argument;
    }
    
    public Arg createArg() {
        return this.createArg(false);
    }
    
    public Arg createArg(final boolean insertAtStart) {
        final Arg argument = new Argument();
        if (insertAtStart) {
            this.arguments.insertElementAt(argument, 0);
        }
        else {
            this.arguments.addElement(argument);
        }
        return argument;
    }
    
    public void addArg(final Arg argument) {
        this.addArg(argument, false);
    }
    
    public void addArg(final Arg argument, final boolean insertAtStart) {
        if (insertAtStart) {
            this.arguments.insertElementAt(argument, 0);
        }
        else {
            this.arguments.addElement(argument);
        }
    }
    
    public void setExecutable(final String executable) {
        this.shell.setExecutable(executable);
        this.executable = executable;
    }
    
    public String getExecutable() {
        String exec = this.shell.getExecutable();
        if (exec == null) {
            exec = this.executable;
        }
        return exec;
    }
    
    public void addArguments(final String[] line) {
        for (int i = 0; i < line.length; ++i) {
            this.createArgument().setValue(line[i]);
        }
    }
    
    public void addEnvironment(final String name, final String value) {
        this.envVars.put(name, value);
    }
    
    public void addSystemEnvironment() throws Exception {
        final Properties systemEnvVars = CommandLineUtils.getSystemEnvVars();
        for (final String key : systemEnvVars.keySet()) {
            if (!this.envVars.containsKey(key)) {
                this.addEnvironment(key, systemEnvVars.getProperty(key));
            }
        }
    }
    
    public String[] getEnvironmentVariables() throws CommandLineException {
        try {
            this.addSystemEnvironment();
        }
        catch (Exception e) {
            throw new CommandLineException("Error setting up environmental variables", e);
        }
        final String[] environmentVars = new String[this.envVars.size()];
        int i = 0;
        for (final String name : this.envVars.keySet()) {
            final String value = this.envVars.get(name);
            environmentVars[i] = name + "=" + value;
            ++i;
        }
        return environmentVars;
    }
    
    public String[] getCommandline() {
        final String[] args = this.getArguments();
        final String executable = this.getExecutable();
        if (executable == null) {
            return args;
        }
        final String[] result = new String[args.length + 1];
        result[0] = executable;
        System.arraycopy(args, 0, result, 1, args.length);
        return result;
    }
    
    public String[] getShellCommandline() {
        this.verifyShellState();
        return this.getShell().getShellCommandLine(this.getArguments()).toArray(new String[0]);
    }
    
    public String[] getArguments() {
        final Vector result = new Vector(this.arguments.size() * 2);
        for (int i = 0; i < this.arguments.size(); ++i) {
            final Argument arg = this.arguments.elementAt(i);
            final String[] s = arg.getParts();
            if (s != null) {
                for (int j = 0; j < s.length; ++j) {
                    result.addElement(s[j]);
                }
            }
        }
        final String[] res = new String[result.size()];
        result.copyInto(res);
        return res;
    }
    
    public String toString() {
        return StringUtils.join(this.getShellCommandline(), " ");
    }
    
    public int size() {
        return this.getCommandline().length;
    }
    
    public Object clone() {
        final Commandline c = new Commandline((Shell)this.shell.clone());
        c.executable = this.executable;
        c.workingDir = this.workingDir;
        c.addArguments(this.getArguments());
        return c;
    }
    
    public void clear() {
        this.executable = null;
        this.workingDir = null;
        this.shell.setExecutable(null);
        this.shell.clearArguments();
        this.arguments.removeAllElements();
    }
    
    public void clearArgs() {
        this.arguments.removeAllElements();
    }
    
    public Marker createMarker() {
        return new Marker(this.arguments.size());
    }
    
    public void setWorkingDirectory(final String path) {
        this.shell.setWorkingDirectory(path);
        this.workingDir = new File(path);
    }
    
    public void setWorkingDirectory(final File workingDirectory) {
        this.shell.setWorkingDirectory(workingDirectory);
        this.workingDir = workingDirectory;
    }
    
    public File getWorkingDirectory() {
        File workDir = this.shell.getWorkingDirectory();
        if (workDir == null) {
            workDir = this.workingDir;
        }
        return workDir;
    }
    
    public Process execute() throws CommandLineException {
        this.verifyShellState();
        final String[] environment = this.getEnvironmentVariables();
        final File workingDir = this.shell.getWorkingDirectory();
        Process process;
        try {
            if (workingDir == null) {
                process = Runtime.getRuntime().exec(this.getShellCommandline(), environment);
            }
            else {
                if (!workingDir.exists()) {
                    throw new CommandLineException("Working directory \"" + workingDir.getPath() + "\" does not exist!");
                }
                if (!workingDir.isDirectory()) {
                    throw new CommandLineException("Path \"" + workingDir.getPath() + "\" does not specify a directory.");
                }
                process = Runtime.getRuntime().exec(this.getShellCommandline(), environment, workingDir);
            }
        }
        catch (IOException ex) {
            throw new CommandLineException("Error while executing process.", ex);
        }
        return process;
    }
    
    private void verifyShellState() {
        if (this.shell.getWorkingDirectory() == null) {
            this.shell.setWorkingDirectory(this.workingDir);
        }
        if (this.shell.getExecutable() == null) {
            this.shell.setExecutable(this.executable);
        }
    }
    
    public Properties getSystemEnvVars() throws Exception {
        return CommandLineUtils.getSystemEnvVars();
    }
    
    public void setShell(final Shell shell) {
        this.shell = shell;
    }
    
    public Shell getShell() {
        return this.shell;
    }
    
    public static String[] translateCommandline(final String toProcess) throws Exception {
        return CommandLineUtils.translateCommandline(toProcess);
    }
    
    public static String quoteArgument(final String argument) throws CommandLineException {
        return CommandLineUtils.quote(argument);
    }
    
    public static String toString(final String[] line) {
        return CommandLineUtils.toString(line);
    }
    
    public class Marker
    {
        private int position;
        private int realPos;
        
        Marker(final int position) {
            this.realPos = -1;
            this.position = position;
        }
        
        public int getPosition() {
            if (this.realPos == -1) {
                this.realPos = ((Commandline.this.getExecutable() != null) ? 1 : 0);
                for (int i = 0; i < this.position; ++i) {
                    final Arg arg = Commandline.this.arguments.elementAt(i);
                    this.realPos += arg.getParts().length;
                }
            }
            return this.realPos;
        }
    }
    
    public static class Argument implements Arg
    {
        private String[] parts;
        
        public void setValue(final String value) {
            if (value != null) {
                this.parts = new String[] { value };
            }
        }
        
        public void setLine(final String line) {
            if (line == null) {
                return;
            }
            try {
                this.parts = CommandLineUtils.translateCommandline(line);
            }
            catch (Exception e) {
                System.err.println("Error translating Commandline.");
            }
        }
        
        public void setFile(final File value) {
            this.parts = new String[] { value.getAbsolutePath() };
        }
        
        public String[] getParts() {
            return this.parts;
        }
    }
}
