// 
// Decompiled by Procyon v0.5.36
// 

package org.codehaus.plexus.util.cli;

import java.util.Iterator;
import java.util.Collections;
import java.util.HashMap;
import org.codehaus.plexus.util.StringUtils;
import java.util.Vector;
import java.util.StringTokenizer;
import java.util.Locale;
import java.io.Reader;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.IOException;
import org.codehaus.plexus.util.Os;
import java.util.Properties;
import java.io.InputStream;
import java.util.Map;

public abstract class CommandLineUtils
{
    private static Map processes;
    private static Thread shutdownHook;
    
    public static void addShutdownHook() {
        Runtime.getRuntime().addShutdownHook(CommandLineUtils.shutdownHook);
    }
    
    public static void removeShutdownHook(final boolean execute) {
        Runtime.getRuntime().removeShutdownHook(CommandLineUtils.shutdownHook);
        if (execute) {
            CommandLineUtils.shutdownHook.run();
        }
    }
    
    public static int executeCommandLine(final Commandline cl, final StreamConsumer systemOut, final StreamConsumer systemErr) throws CommandLineException {
        return executeCommandLine(cl, null, systemOut, systemErr, 0);
    }
    
    public static int executeCommandLine(final Commandline cl, final StreamConsumer systemOut, final StreamConsumer systemErr, final int timeoutInSeconds) throws CommandLineException {
        return executeCommandLine(cl, null, systemOut, systemErr, timeoutInSeconds);
    }
    
    public static int executeCommandLine(final Commandline cl, final InputStream systemIn, final StreamConsumer systemOut, final StreamConsumer systemErr) throws CommandLineException {
        return executeCommandLine(cl, systemIn, systemOut, systemErr, 0);
    }
    
    public static int executeCommandLine(final Commandline cl, final InputStream systemIn, final StreamConsumer systemOut, final StreamConsumer systemErr, final int timeoutInSeconds) throws CommandLineException {
        if (cl == null) {
            throw new IllegalArgumentException("cl cannot be null.");
        }
        final Process p = cl.execute();
        CommandLineUtils.processes.put(new Long(cl.getPid()), p);
        StreamFeeder inputFeeder = null;
        if (systemIn != null) {
            inputFeeder = new StreamFeeder(systemIn, p.getOutputStream());
        }
        final StreamPumper outputPumper = new StreamPumper(p.getInputStream(), systemOut);
        final StreamPumper errorPumper = new StreamPumper(p.getErrorStream(), systemErr);
        if (inputFeeder != null) {
            inputFeeder.start();
        }
        outputPumper.start();
        errorPumper.start();
        try {
            int returnValue;
            if (timeoutInSeconds <= 0) {
                returnValue = p.waitFor();
            }
            else {
                final long now = System.currentTimeMillis();
                final long timeoutInMillis = 1000L * timeoutInSeconds;
                final long finish = now + timeoutInMillis;
                while (isAlive(p) && System.currentTimeMillis() < finish) {
                    Thread.sleep(10L);
                }
                if (isAlive(p)) {
                    throw new InterruptedException("Process timeout out after " + timeoutInSeconds + " seconds");
                }
                returnValue = p.exitValue();
            }
            if (inputFeeder != null) {
                synchronized (inputFeeder) {
                    while (!inputFeeder.isDone()) {
                        inputFeeder.wait();
                    }
                }
            }
            synchronized (outputPumper) {
                while (!outputPumper.isDone()) {
                    outputPumper.wait();
                }
            }
            synchronized (errorPumper) {
                while (!errorPumper.isDone()) {
                    errorPumper.wait();
                }
            }
            CommandLineUtils.processes.remove(new Long(cl.getPid()));
            return returnValue;
        }
        catch (InterruptedException ex) {
            killProcess(cl.getPid());
            throw new CommandLineTimeOutException("Error while executing external command, process killed.", ex);
        }
        finally {
            if (inputFeeder != null) {
                inputFeeder.close();
            }
            outputPumper.close();
            errorPumper.close();
            p.destroy();
            if (CommandLineUtils.processes.get(new Long(cl.getPid())) != null) {
                CommandLineUtils.processes.remove(new Long(cl.getPid()));
            }
        }
    }
    
    public static Properties getSystemEnvVars() throws IOException {
        return getSystemEnvVars(!Os.isFamily("windows"));
    }
    
    public static Properties getSystemEnvVars(final boolean caseSensitive) throws IOException {
        Process p = null;
        try {
            final Properties envVars = new Properties();
            final Runtime r = Runtime.getRuntime();
            if (Os.isFamily("windows")) {
                if (Os.isFamily("win9x")) {
                    p = r.exec("command.com /c set");
                }
                else {
                    p = r.exec("cmd.exe /c set");
                }
            }
            else {
                p = r.exec("env");
            }
            final BufferedReader br = new BufferedReader(new InputStreamReader(p.getInputStream()));
            String lastKey = null;
            String lastVal = null;
            String line;
            while ((line = br.readLine()) != null) {
                final int idx = line.indexOf(61);
                if (idx > 0) {
                    lastKey = line.substring(0, idx);
                    if (!caseSensitive) {
                        lastKey = lastKey.toUpperCase(Locale.ENGLISH);
                    }
                    lastVal = line.substring(idx + 1);
                    envVars.setProperty(lastKey, lastVal);
                }
                else {
                    if (lastKey == null) {
                        continue;
                    }
                    lastVal = lastVal + "\n" + line;
                    envVars.setProperty(lastKey, lastVal);
                }
            }
            return envVars;
        }
        finally {
            if (p != null) {
                p.destroy();
            }
        }
    }
    
    public static void killProcess(final long pid) {
        final Process p = CommandLineUtils.processes.get(new Long(pid));
        if (p != null) {
            p.destroy();
            System.out.println("Process " + pid + " is killed.");
            CommandLineUtils.processes.remove(new Long(pid));
        }
        else {
            System.out.println("don't exist.");
        }
    }
    
    public static boolean isAlive(final long pid) {
        return CommandLineUtils.processes.get(new Long(pid)) != null;
    }
    
    public static boolean isAlive(final Process p) {
        if (p == null) {
            return false;
        }
        try {
            p.exitValue();
            return false;
        }
        catch (IllegalThreadStateException e) {
            return true;
        }
    }
    
    public static String[] translateCommandline(final String toProcess) throws Exception {
        if (toProcess == null || toProcess.length() == 0) {
            return new String[0];
        }
        final int normal = 0;
        final int inQuote = 1;
        final int inDoubleQuote = 2;
        int state = 0;
        final StringTokenizer tok = new StringTokenizer(toProcess, "\"' ", true);
        final Vector v = new Vector();
        final StringBuffer current = new StringBuffer();
        while (tok.hasMoreTokens()) {
            final String nextTok = tok.nextToken();
            switch (state) {
                case 1: {
                    if ("'".equals(nextTok)) {
                        state = 0;
                        continue;
                    }
                    current.append(nextTok);
                    continue;
                }
                case 2: {
                    if ("\"".equals(nextTok)) {
                        state = 0;
                        continue;
                    }
                    current.append(nextTok);
                    continue;
                }
                default: {
                    if ("'".equals(nextTok)) {
                        state = 1;
                        continue;
                    }
                    if ("\"".equals(nextTok)) {
                        state = 2;
                        continue;
                    }
                    if (!" ".equals(nextTok)) {
                        current.append(nextTok);
                        continue;
                    }
                    if (current.length() != 0) {
                        v.addElement(current.toString());
                        current.setLength(0);
                        continue;
                    }
                    continue;
                }
            }
        }
        if (current.length() != 0) {
            v.addElement(current.toString());
        }
        if (state == 1 || state == 2) {
            throw new CommandLineException("unbalanced quotes in " + toProcess);
        }
        final String[] args = new String[v.size()];
        v.copyInto(args);
        return args;
    }
    
    public static String quote(final String argument) throws CommandLineException {
        return quote(argument, false, false, true);
    }
    
    public static String quote(final String argument, final boolean wrapExistingQuotes) throws CommandLineException {
        return quote(argument, false, false, wrapExistingQuotes);
    }
    
    public static String quote(final String argument, final boolean escapeSingleQuotes, final boolean escapeDoubleQuotes, final boolean wrapExistingQuotes) throws CommandLineException {
        if (argument.indexOf("\"") > -1) {
            if (argument.indexOf("'") > -1) {
                throw new CommandLineException("Can't handle single and double quotes in same argument");
            }
            if (escapeSingleQuotes) {
                return "\\'" + argument + "\\'";
            }
            if (wrapExistingQuotes) {
                return '\'' + argument + '\'';
            }
        }
        else if (argument.indexOf("'") > -1) {
            if (escapeDoubleQuotes) {
                return "\\\"" + argument + "\\\"";
            }
            if (wrapExistingQuotes) {
                return '\"' + argument + '\"';
            }
        }
        else if (argument.indexOf(" ") > -1) {
            if (escapeDoubleQuotes) {
                return "\\\"" + argument + "\\\"";
            }
            return '\"' + argument + '\"';
        }
        return argument;
    }
    
    public static String toString(final String[] line) {
        if (line == null || line.length == 0) {
            return "";
        }
        final StringBuffer result = new StringBuffer();
        for (int i = 0; i < line.length; ++i) {
            if (i > 0) {
                result.append(' ');
            }
            try {
                result.append(StringUtils.quoteAndEscape(line[i], '\"'));
            }
            catch (Exception e) {
                System.err.println("Error quoting argument: " + e.getMessage());
            }
        }
        return result.toString();
    }
    
    static {
        CommandLineUtils.processes = Collections.synchronizedMap(new HashMap<Object, Object>());
        (CommandLineUtils.shutdownHook = new Thread("CommandlineUtil shutdown") {
            public void run() {
                if (CommandLineUtils.processes != null && CommandLineUtils.processes.size() > 0) {
                    System.err.println("Destroying " + CommandLineUtils.processes.size() + " processes");
                    final Iterator it = CommandLineUtils.processes.values().iterator();
                    while (it.hasNext()) {
                        System.err.println("Destroying process..");
                        it.next().destroy();
                    }
                    System.err.println("Destroyed " + CommandLineUtils.processes.size() + " processes");
                }
            }
        }).setContextClassLoader(null);
        addShutdownHook();
    }
    
    public static class StringStreamConsumer implements StreamConsumer
    {
        private StringBuffer string;
        private String ls;
        
        public StringStreamConsumer() {
            this.string = new StringBuffer();
            this.ls = System.getProperty("line.separator");
        }
        
        public void consumeLine(final String line) {
            this.string.append(line).append(this.ls);
        }
        
        public String getOutput() {
            return this.string.toString();
        }
    }
}
