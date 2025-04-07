// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.maven.model;

import java.io.Serializable;

public class Organization implements Serializable
{
    private String name;
    private String url;
    
    public String getName() {
        return this.name;
    }
    
    public String getUrl() {
        return this.url;
    }
    
    public void setName(final String name) {
        this.name = name;
    }
    
    public void setUrl(final String url) {
        this.url = url;
    }
}
