// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.maven.model;

import java.io.Serializable;

public class ActivationProperty implements Serializable
{
    private String name;
    private String value;
    
    public String getName() {
        return this.name;
    }
    
    public String getValue() {
        return this.value;
    }
    
    public void setName(final String name) {
        this.name = name;
    }
    
    public void setValue(final String value) {
        this.value = value;
    }
}
