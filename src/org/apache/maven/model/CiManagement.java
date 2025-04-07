// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.maven.model;

import java.util.ArrayList;
import java.util.List;
import java.io.Serializable;

public class CiManagement implements Serializable
{
    private String system;
    private String url;
    private List<Notifier> notifiers;
    
    public void addNotifier(final Notifier notifier) {
        if (!(notifier instanceof Notifier)) {
            throw new ClassCastException("CiManagement.addNotifiers(notifier) parameter must be instanceof " + Notifier.class.getName());
        }
        this.getNotifiers().add(notifier);
    }
    
    public List<Notifier> getNotifiers() {
        if (this.notifiers == null) {
            this.notifiers = new ArrayList<Notifier>();
        }
        return this.notifiers;
    }
    
    public String getSystem() {
        return this.system;
    }
    
    public String getUrl() {
        return this.url;
    }
    
    public void removeNotifier(final Notifier notifier) {
        if (!(notifier instanceof Notifier)) {
            throw new ClassCastException("CiManagement.removeNotifiers(notifier) parameter must be instanceof " + Notifier.class.getName());
        }
        this.getNotifiers().remove(notifier);
    }
    
    public void setNotifiers(final List<Notifier> notifiers) {
        this.notifiers = notifiers;
    }
    
    public void setSystem(final String system) {
        this.system = system;
    }
    
    public void setUrl(final String url) {
        this.url = url;
    }
}
