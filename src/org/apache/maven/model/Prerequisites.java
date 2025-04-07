// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.maven.model;

import java.io.Serializable;

public class Prerequisites implements Serializable
{
    private String maven;
    
    public Prerequisites() {
        this.maven = "2.0";
    }
    
    public String getMaven() {
        return this.maven;
    }
    
    public void setMaven(final String maven) {
        this.maven = maven;
    }
}
