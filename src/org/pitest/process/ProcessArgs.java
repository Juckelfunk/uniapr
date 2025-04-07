// 
// Decompiled by Procyon v0.5.36
// 

package org.pitest.process;

import org.pitest.classpath.ClassPath;
import java.util.Collections;
import org.pitest.functional.prelude.Prelude;
import java.util.Map;
import java.io.File;
import java.util.List;
import org.pitest.functional.SideEffect1;

public final class ProcessArgs
{
    private final String launchClassPath;
    private SideEffect1<String> stdout;
    private SideEffect1<String> stdErr;
    private List<String> jvmArgs;
    private JavaAgent javaAgentFinder;
    private File workingDir;
    private String javaExecutable;
    private Map<String, String> environmentVariables;
    
    private ProcessArgs(final String launchClassPath) {
        this.stdout = Prelude.print(String.class);
        this.stdErr = Prelude.printTo(String.class, System.err);
        this.jvmArgs = Collections.emptyList();
        this.workingDir = null;
        this.launchClassPath = launchClassPath;
    }
    
    public static ProcessArgs withClassPath(final String cp) {
        return new ProcessArgs(cp);
    }
    
    public static ProcessArgs withClassPath(final ClassPath cp) {
        return new ProcessArgs(cp.getLocalClassPath());
    }
    
    public ProcessArgs andBaseDir(final File baseDir) {
        this.workingDir = baseDir;
        return this;
    }
    
    public ProcessArgs andStdout(final SideEffect1<String> stdout) {
        this.stdout = stdout;
        return this;
    }
    
    public ProcessArgs andStderr(final SideEffect1<String> stderr) {
        this.stdErr = stderr;
        return this;
    }
    
    public String getLaunchClassPath() {
        return this.launchClassPath;
    }
    
    public SideEffect1<String> getStdout() {
        return this.stdout;
    }
    
    public SideEffect1<String> getStdErr() {
        return this.stdErr;
    }
    
    public List<String> getJvmArgs() {
        return this.jvmArgs;
    }
    
    public JavaAgent getJavaAgentFinder() {
        return this.javaAgentFinder;
    }
    
    public void setStdout(final SideEffect1<String> stdout) {
        this.stdout = stdout;
    }
    
    public void setStdErr(final SideEffect1<String> stdErr) {
        this.stdErr = stdErr;
    }
    
    public void setJvmArgs(final List<String> jvmArgs) {
        this.jvmArgs = jvmArgs;
    }
    
    public File getWorkingDir() {
        return this.workingDir;
    }
    
    public String getJavaExecutable() {
        return this.javaExecutable;
    }
    
    public ProcessArgs andLaunchOptions(final LaunchOptions launchOptions) {
        this.jvmArgs = launchOptions.getChildJVMArgs();
        this.javaAgentFinder = launchOptions.getJavaAgentFinder();
        this.javaExecutable = launchOptions.getJavaExecutable();
        this.environmentVariables = launchOptions.getEnvironmentVariables();
        return this;
    }
    
    public Map<String, String> getEnvironmentVariables() {
        return this.environmentVariables;
    }
}
