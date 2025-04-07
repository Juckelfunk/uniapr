// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.maven.settings;

import java.io.Serializable;

public class Proxy extends IdentifiableBase implements Serializable
{
    private boolean active;
    private String protocol;
    private String username;
    private String password;
    private int port;
    private String host;
    private String nonProxyHosts;
    
    public Proxy() {
        this.active = true;
        this.protocol = "http";
        this.port = 8080;
    }
    
    public String getHost() {
        return this.host;
    }
    
    public String getNonProxyHosts() {
        return this.nonProxyHosts;
    }
    
    public String getPassword() {
        return this.password;
    }
    
    public int getPort() {
        return this.port;
    }
    
    public String getProtocol() {
        return this.protocol;
    }
    
    public String getUsername() {
        return this.username;
    }
    
    public boolean isActive() {
        return this.active;
    }
    
    public void setActive(final boolean active) {
        this.active = active;
    }
    
    public void setHost(final String host) {
        this.host = host;
    }
    
    public void setNonProxyHosts(final String nonProxyHosts) {
        this.nonProxyHosts = nonProxyHosts;
    }
    
    public void setPassword(final String password) {
        this.password = password;
    }
    
    public void setPort(final int port) {
        this.port = port;
    }
    
    public void setProtocol(final String protocol) {
        this.protocol = protocol;
    }
    
    public void setUsername(final String username) {
        this.username = username;
    }
}
