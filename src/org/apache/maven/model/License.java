// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.maven.model;

import java.io.Serializable;

public class License implements Serializable
{
    private String name;
    private String url;
    private String distribution;
    private String comments;
    
    public String getComments() {
        return this.comments;
    }
    
    public String getDistribution() {
        return this.distribution;
    }
    
    public String getName() {
        return this.name;
    }
    
    public String getUrl() {
        return this.url;
    }
    
    public void setComments(final String comments) {
        this.comments = comments;
    }
    
    public void setDistribution(final String distribution) {
        this.distribution = distribution;
    }
    
    public void setName(final String name) {
        this.name = name;
    }
    
    public void setUrl(final String url) {
        this.url = url;
    }
}
