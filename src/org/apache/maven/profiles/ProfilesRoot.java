// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.maven.profiles;

import java.util.ArrayList;
import java.util.List;
import java.io.Serializable;

public class ProfilesRoot implements Serializable
{
    private List<Profile> profiles;
    private List<String> activeProfiles;
    private String modelEncoding;
    
    public ProfilesRoot() {
        this.modelEncoding = "UTF-8";
    }
    
    public void addActiveProfile(final String string) {
        if (!(string instanceof String)) {
            throw new ClassCastException("ProfilesRoot.addActiveProfiles(string) parameter must be instanceof " + String.class.getName());
        }
        this.getActiveProfiles().add(string);
    }
    
    public void addProfile(final Profile profile) {
        if (!(profile instanceof Profile)) {
            throw new ClassCastException("ProfilesRoot.addProfiles(profile) parameter must be instanceof " + Profile.class.getName());
        }
        this.getProfiles().add(profile);
    }
    
    public List<String> getActiveProfiles() {
        if (this.activeProfiles == null) {
            this.activeProfiles = new ArrayList<String>();
        }
        return this.activeProfiles;
    }
    
    public String getModelEncoding() {
        return this.modelEncoding;
    }
    
    public List<Profile> getProfiles() {
        if (this.profiles == null) {
            this.profiles = new ArrayList<Profile>();
        }
        return this.profiles;
    }
    
    public void removeActiveProfile(final String string) {
        if (!(string instanceof String)) {
            throw new ClassCastException("ProfilesRoot.removeActiveProfiles(string) parameter must be instanceof " + String.class.getName());
        }
        this.getActiveProfiles().remove(string);
    }
    
    public void removeProfile(final Profile profile) {
        if (!(profile instanceof Profile)) {
            throw new ClassCastException("ProfilesRoot.removeProfiles(profile) parameter must be instanceof " + Profile.class.getName());
        }
        this.getProfiles().remove(profile);
    }
    
    public void setActiveProfiles(final List<String> activeProfiles) {
        this.activeProfiles = activeProfiles;
    }
    
    public void setModelEncoding(final String modelEncoding) {
        this.modelEncoding = modelEncoding;
    }
    
    public void setProfiles(final List<Profile> profiles) {
        this.profiles = profiles;
    }
}
