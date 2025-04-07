// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.maven.model;

import java.io.Serializable;

public class Scm implements Serializable
{
    private String connection;
    private String developerConnection;
    private String tag;
    private String url;
    
    public Scm() {
        this.tag = "HEAD";
    }
    
    public String getConnection() {
        return this.connection;
    }
    
    public String getDeveloperConnection() {
        return this.developerConnection;
    }
    
    public String getTag() {
        return this.tag;
    }
    
    public String getUrl() {
        return this.url;
    }
    
    public void setConnection(final String connection) {
        this.connection = connection;
    }
    
    public void setDeveloperConnection(final String developerConnection) {
        this.developerConnection = developerConnection;
    }
    
    public void setTag(final String tag) {
        this.tag = tag;
    }
    
    public void setUrl(final String url) {
        this.url = url;
    }
}
