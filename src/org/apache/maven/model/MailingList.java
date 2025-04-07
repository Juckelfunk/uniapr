// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.maven.model;

import java.util.ArrayList;
import java.util.List;
import java.io.Serializable;

public class MailingList implements Serializable
{
    private String name;
    private String subscribe;
    private String unsubscribe;
    private String post;
    private String archive;
    private List<String> otherArchives;
    
    public void addOtherArchive(final String string) {
        if (!(string instanceof String)) {
            throw new ClassCastException("MailingList.addOtherArchives(string) parameter must be instanceof " + String.class.getName());
        }
        this.getOtherArchives().add(string);
    }
    
    public String getArchive() {
        return this.archive;
    }
    
    public String getName() {
        return this.name;
    }
    
    public List<String> getOtherArchives() {
        if (this.otherArchives == null) {
            this.otherArchives = new ArrayList<String>();
        }
        return this.otherArchives;
    }
    
    public String getPost() {
        return this.post;
    }
    
    public String getSubscribe() {
        return this.subscribe;
    }
    
    public String getUnsubscribe() {
        return this.unsubscribe;
    }
    
    public void removeOtherArchive(final String string) {
        if (!(string instanceof String)) {
            throw new ClassCastException("MailingList.removeOtherArchives(string) parameter must be instanceof " + String.class.getName());
        }
        this.getOtherArchives().remove(string);
    }
    
    public void setArchive(final String archive) {
        this.archive = archive;
    }
    
    public void setName(final String name) {
        this.name = name;
    }
    
    public void setOtherArchives(final List<String> otherArchives) {
        this.otherArchives = otherArchives;
    }
    
    public void setPost(final String post) {
        this.post = post;
    }
    
    public void setSubscribe(final String subscribe) {
        this.subscribe = subscribe;
    }
    
    public void setUnsubscribe(final String unsubscribe) {
        this.unsubscribe = unsubscribe;
    }
}
