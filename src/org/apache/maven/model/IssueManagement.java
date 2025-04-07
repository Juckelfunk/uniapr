// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.maven.model;

import java.io.Serializable;

public class IssueManagement implements Serializable
{
    private String system;
    private String url;
    
    public String getSystem() {
        return this.system;
    }
    
    public String getUrl() {
        return this.url;
    }
    
    public void setSystem(final String system) {
        this.system = system;
    }
    
    public void setUrl(final String url) {
        this.url = url;
    }
}
