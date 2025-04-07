// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.maven.model;

import java.util.Properties;
import java.io.Serializable;

public class Notifier implements Serializable
{
    private String type;
    private boolean sendOnError;
    private boolean sendOnFailure;
    private boolean sendOnSuccess;
    private boolean sendOnWarning;
    private String address;
    private Properties configuration;
    
    public Notifier() {
        this.type = "mail";
        this.sendOnError = true;
        this.sendOnFailure = true;
        this.sendOnSuccess = true;
        this.sendOnWarning = true;
    }
    
    public void addConfiguration(final String key, final String value) {
        this.getConfiguration().put(key, value);
    }
    
    public String getAddress() {
        return this.address;
    }
    
    public Properties getConfiguration() {
        if (this.configuration == null) {
            this.configuration = new Properties();
        }
        return this.configuration;
    }
    
    public String getType() {
        return this.type;
    }
    
    public boolean isSendOnError() {
        return this.sendOnError;
    }
    
    public boolean isSendOnFailure() {
        return this.sendOnFailure;
    }
    
    public boolean isSendOnSuccess() {
        return this.sendOnSuccess;
    }
    
    public boolean isSendOnWarning() {
        return this.sendOnWarning;
    }
    
    public void setAddress(final String address) {
        this.address = address;
    }
    
    public void setConfiguration(final Properties configuration) {
        this.configuration = configuration;
    }
    
    public void setSendOnError(final boolean sendOnError) {
        this.sendOnError = sendOnError;
    }
    
    public void setSendOnFailure(final boolean sendOnFailure) {
        this.sendOnFailure = sendOnFailure;
    }
    
    public void setSendOnSuccess(final boolean sendOnSuccess) {
        this.sendOnSuccess = sendOnSuccess;
    }
    
    public void setSendOnWarning(final boolean sendOnWarning) {
        this.sendOnWarning = sendOnWarning;
    }
    
    public void setType(final String type) {
        this.type = type;
    }
}
