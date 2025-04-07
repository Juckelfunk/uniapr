// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.maven.profiles;

import java.io.Serializable;

public class ActivationOS implements Serializable
{
    private String name;
    private String family;
    private String arch;
    private String version;
    
    public String getArch() {
        return this.arch;
    }
    
    public String getFamily() {
        return this.family;
    }
    
    public String getName() {
        return this.name;
    }
    
    public String getVersion() {
        return this.version;
    }
    
    public void setArch(final String arch) {
        this.arch = arch;
    }
    
    public void setFamily(final String family) {
        this.family = family;
    }
    
    public void setName(final String name) {
        this.name = name;
    }
    
    public void setVersion(final String version) {
        this.version = version;
    }
}
