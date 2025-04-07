// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.maven.wagon.authentication;

import java.io.Serializable;

public class AuthenticationInfo implements Serializable
{
    private String userName;
    private String password;
    private String passphrase;
    private String privateKey;
    
    public String getPassphrase() {
        return this.passphrase;
    }
    
    public void setPassphrase(final String passphrase) {
        this.passphrase = passphrase;
    }
    
    public String getPrivateKey() {
        return this.privateKey;
    }
    
    public void setPrivateKey(final String privateKey) {
        this.privateKey = privateKey;
    }
    
    public String getPassword() {
        return this.password;
    }
    
    public void setPassword(final String password) {
        this.password = password;
    }
    
    public String getUserName() {
        return this.userName;
    }
    
    public void setUserName(final String userName) {
        this.userName = userName;
    }
}
