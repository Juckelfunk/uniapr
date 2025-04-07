// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.maven.settings;

import java.io.Serializable;

public class Activation implements Serializable
{
    private boolean activeByDefault;
    private String jdk;
    private ActivationOS os;
    private ActivationProperty property;
    private ActivationFile file;
    
    public Activation() {
        this.activeByDefault = false;
    }
    
    public ActivationFile getFile() {
        return this.file;
    }
    
    public String getJdk() {
        return this.jdk;
    }
    
    public ActivationOS getOs() {
        return this.os;
    }
    
    public ActivationProperty getProperty() {
        return this.property;
    }
    
    public boolean isActiveByDefault() {
        return this.activeByDefault;
    }
    
    public void setActiveByDefault(final boolean activeByDefault) {
        this.activeByDefault = activeByDefault;
    }
    
    public void setFile(final ActivationFile file) {
        this.file = file;
    }
    
    public void setJdk(final String jdk) {
        this.jdk = jdk;
    }
    
    public void setOs(final ActivationOS os) {
        this.os = os;
    }
    
    public void setProperty(final ActivationProperty property) {
        this.property = property;
    }
}
