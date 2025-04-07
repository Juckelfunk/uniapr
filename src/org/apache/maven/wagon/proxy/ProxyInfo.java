// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.maven.wagon.proxy;

import java.io.Serializable;

public class ProxyInfo implements Serializable
{
    public static final String PROXY_SOCKS5 = "SOCKS_5";
    public static final String PROXY_SOCKS4 = "SOCKS4";
    public static final String PROXY_HTTP = "HTTP";
    private String host;
    private String userName;
    private String password;
    private int port;
    private String type;
    private String nonProxyHosts;
    private String ntlmHost;
    private String ntlmDomain;
    
    public ProxyInfo() {
        this.host = null;
        this.userName = null;
        this.password = null;
        this.port = -1;
        this.type = null;
    }
    
    public String getHost() {
        return this.host;
    }
    
    public void setHost(final String host) {
        this.host = host;
    }
    
    public String getPassword() {
        return this.password;
    }
    
    public void setPassword(final String password) {
        this.password = password;
    }
    
    public int getPort() {
        return this.port;
    }
    
    public void setPort(final int port) {
        this.port = port;
    }
    
    public String getUserName() {
        return this.userName;
    }
    
    public void setUserName(final String userName) {
        this.userName = userName;
    }
    
    public String getType() {
        return this.type;
    }
    
    public void setType(final String type) {
        this.type = type;
    }
    
    public String getNonProxyHosts() {
        return this.nonProxyHosts;
    }
    
    public void setNonProxyHosts(final String nonProxyHosts) {
        this.nonProxyHosts = nonProxyHosts;
    }
    
    public String getNtlmHost() {
        return this.ntlmHost;
    }
    
    public void setNtlmHost(final String ntlmHost) {
        this.ntlmHost = ntlmHost;
    }
    
    public void setNtlmDomain(final String ntlmDomain) {
        this.ntlmDomain = ntlmDomain;
    }
    
    public String getNtlmDomain() {
        return this.ntlmDomain;
    }
}
