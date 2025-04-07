// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.maven.settings;

import java.io.Serializable;

public class Server extends IdentifiableBase implements Serializable
{
    private String username;
    private String password;
    private String privateKey;
    private String passphrase;
    private String filePermissions;
    private String directoryPermissions;
    private Object configuration;
    
    public Object getConfiguration() {
        return this.configuration;
    }
    
    public String getDirectoryPermissions() {
        return this.directoryPermissions;
    }
    
    public String getFilePermissions() {
        return this.filePermissions;
    }
    
    public String getPassphrase() {
        return this.passphrase;
    }
    
    public String getPassword() {
        return this.password;
    }
    
    public String getPrivateKey() {
        return this.privateKey;
    }
    
    public String getUsername() {
        return this.username;
    }
    
    public void setConfiguration(final Object configuration) {
        this.configuration = configuration;
    }
    
    public void setDirectoryPermissions(final String directoryPermissions) {
        this.directoryPermissions = directoryPermissions;
    }
    
    public void setFilePermissions(final String filePermissions) {
        this.filePermissions = filePermissions;
    }
    
    public void setPassphrase(final String passphrase) {
        this.passphrase = passphrase;
    }
    
    public void setPassword(final String password) {
        this.password = password;
    }
    
    public void setPrivateKey(final String privateKey) {
        this.privateKey = privateKey;
    }
    
    public void setUsername(final String username) {
        this.username = username;
    }
}
