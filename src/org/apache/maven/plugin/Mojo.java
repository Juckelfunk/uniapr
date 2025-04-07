// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.maven.plugin;

import org.apache.maven.plugin.logging.Log;

public interface Mojo
{
    public static final String ROLE = Mojo.class.getName();
    
    void execute() throws MojoExecutionException, MojoFailureException;
    
    void setLog(final Log p0);
    
    Log getLog();
}
