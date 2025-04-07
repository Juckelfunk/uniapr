// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.maven.model;

import java.util.ArrayList;
import java.util.List;
import java.io.Serializable;

public class Build extends BuildBase implements Serializable
{
    private String sourceDirectory;
    private String scriptSourceDirectory;
    private String testSourceDirectory;
    private String outputDirectory;
    private String testOutputDirectory;
    private List<Extension> extensions;
    
    public void addExtension(final Extension extension) {
        if (!(extension instanceof Extension)) {
            throw new ClassCastException("Build.addExtensions(extension) parameter must be instanceof " + Extension.class.getName());
        }
        this.getExtensions().add(extension);
    }
    
    public List<Extension> getExtensions() {
        if (this.extensions == null) {
            this.extensions = new ArrayList<Extension>();
        }
        return this.extensions;
    }
    
    public String getOutputDirectory() {
        return this.outputDirectory;
    }
    
    public String getScriptSourceDirectory() {
        return this.scriptSourceDirectory;
    }
    
    public String getSourceDirectory() {
        return this.sourceDirectory;
    }
    
    public String getTestOutputDirectory() {
        return this.testOutputDirectory;
    }
    
    public String getTestSourceDirectory() {
        return this.testSourceDirectory;
    }
    
    public void removeExtension(final Extension extension) {
        if (!(extension instanceof Extension)) {
            throw new ClassCastException("Build.removeExtensions(extension) parameter must be instanceof " + Extension.class.getName());
        }
        this.getExtensions().remove(extension);
    }
    
    public void setExtensions(final List<Extension> extensions) {
        this.extensions = extensions;
    }
    
    public void setOutputDirectory(final String outputDirectory) {
        this.outputDirectory = outputDirectory;
    }
    
    public void setScriptSourceDirectory(final String scriptSourceDirectory) {
        this.scriptSourceDirectory = scriptSourceDirectory;
    }
    
    public void setSourceDirectory(final String sourceDirectory) {
        this.sourceDirectory = sourceDirectory;
    }
    
    public void setTestOutputDirectory(final String testOutputDirectory) {
        this.testOutputDirectory = testOutputDirectory;
    }
    
    public void setTestSourceDirectory(final String testSourceDirectory) {
        this.testSourceDirectory = testSourceDirectory;
    }
}
