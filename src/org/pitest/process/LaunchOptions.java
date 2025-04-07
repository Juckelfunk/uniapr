// 
// Decompiled by Procyon v0.5.36
// 

package org.pitest.process;

import java.util.HashMap;
import java.util.Collections;
import java.util.Map;
import java.util.List;

public class LaunchOptions
{
    private final JavaAgent javaAgentFinder;
    private final List<String> childJVMArgs;
    private final JavaExecutableLocator javaExecutable;
    private final Map<String, String> environmentVariables;
    
    public LaunchOptions(final JavaAgent javaAgentFinder) {
        this(javaAgentFinder, new DefaultJavaExecutableLocator(), Collections.emptyList(), new HashMap<String, String>());
    }
    
    public LaunchOptions(final JavaAgent javaAgentFinder, final JavaExecutableLocator javaExecutable, final List<String> childJVMArgs, final Map<String, String> environmentVariables) {
        this.javaAgentFinder = javaAgentFinder;
        this.childJVMArgs = childJVMArgs;
        this.javaExecutable = javaExecutable;
        this.environmentVariables = environmentVariables;
    }
    
    public JavaAgent getJavaAgentFinder() {
        return this.javaAgentFinder;
    }
    
    public List<String> getChildJVMArgs() {
        return this.childJVMArgs;
    }
    
    public String getJavaExecutable() {
        return this.javaExecutable.javaExecutable();
    }
    
    public Map<String, String> getEnvironmentVariables() {
        return this.environmentVariables;
    }
}
