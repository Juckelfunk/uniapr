// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.maven.model;

import java.util.ArrayList;
import java.util.Properties;
import java.util.List;
import java.io.Serializable;

public class Contributor implements Serializable
{
    private String name;
    private String email;
    private String url;
    private String organization;
    private String organizationUrl;
    private List<String> roles;
    private String timezone;
    private Properties properties;
    
    public void addProperty(final String key, final String value) {
        this.getProperties().put(key, value);
    }
    
    public void addRole(final String string) {
        if (!(string instanceof String)) {
            throw new ClassCastException("Contributor.addRoles(string) parameter must be instanceof " + String.class.getName());
        }
        this.getRoles().add(string);
    }
    
    public String getEmail() {
        return this.email;
    }
    
    public String getName() {
        return this.name;
    }
    
    public String getOrganization() {
        return this.organization;
    }
    
    public String getOrganizationUrl() {
        return this.organizationUrl;
    }
    
    public Properties getProperties() {
        if (this.properties == null) {
            this.properties = new Properties();
        }
        return this.properties;
    }
    
    public List<String> getRoles() {
        if (this.roles == null) {
            this.roles = new ArrayList<String>();
        }
        return this.roles;
    }
    
    public String getTimezone() {
        return this.timezone;
    }
    
    public String getUrl() {
        return this.url;
    }
    
    public void removeRole(final String string) {
        if (!(string instanceof String)) {
            throw new ClassCastException("Contributor.removeRoles(string) parameter must be instanceof " + String.class.getName());
        }
        this.getRoles().remove(string);
    }
    
    public void setEmail(final String email) {
        this.email = email;
    }
    
    public void setName(final String name) {
        this.name = name;
    }
    
    public void setOrganization(final String organization) {
        this.organization = organization;
    }
    
    public void setOrganizationUrl(final String organizationUrl) {
        this.organizationUrl = organizationUrl;
    }
    
    public void setProperties(final Properties properties) {
        this.properties = properties;
    }
    
    public void setRoles(final List<String> roles) {
        this.roles = roles;
    }
    
    public void setTimezone(final String timezone) {
        this.timezone = timezone;
    }
    
    public void setUrl(final String url) {
        this.url = url;
    }
}
