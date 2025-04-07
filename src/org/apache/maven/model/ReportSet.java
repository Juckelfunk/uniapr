// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.maven.model;

import java.util.ArrayList;
import java.util.List;
import java.io.Serializable;

public class ReportSet implements Serializable
{
    private String id;
    private Object configuration;
    private String inherited;
    private List<String> reports;
    private boolean inheritanceApplied;
    
    public ReportSet() {
        this.id = "default";
        this.inheritanceApplied = true;
    }
    
    public void addReport(final String string) {
        if (!(string instanceof String)) {
            throw new ClassCastException("ReportSet.addReports(string) parameter must be instanceof " + String.class.getName());
        }
        this.getReports().add(string);
    }
    
    public Object getConfiguration() {
        return this.configuration;
    }
    
    public String getId() {
        return this.id;
    }
    
    public String getInherited() {
        return this.inherited;
    }
    
    public List<String> getReports() {
        if (this.reports == null) {
            this.reports = new ArrayList<String>();
        }
        return this.reports;
    }
    
    public void removeReport(final String string) {
        if (!(string instanceof String)) {
            throw new ClassCastException("ReportSet.removeReports(string) parameter must be instanceof " + String.class.getName());
        }
        this.getReports().remove(string);
    }
    
    public void setConfiguration(final Object configuration) {
        this.configuration = configuration;
    }
    
    public void setId(final String id) {
        this.id = id;
    }
    
    public void setInherited(final String inherited) {
        this.inherited = inherited;
    }
    
    public void setReports(final List<String> reports) {
        this.reports = reports;
    }
    
    public void unsetInheritanceApplied() {
        this.inheritanceApplied = false;
    }
    
    public boolean isInheritanceApplied() {
        return this.inheritanceApplied;
    }
}
