// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.maven.model;

import java.util.ArrayList;
import java.util.List;
import java.io.Serializable;

public class PluginExecution extends ConfigurationContainer implements Serializable
{
    private String id;
    private String phase;
    private List<String> goals;
    public static final String DEFAULT_EXECUTION_ID = "default";
    
    public PluginExecution() {
        this.id = "default";
    }
    
    public void addGoal(final String string) {
        if (!(string instanceof String)) {
            throw new ClassCastException("PluginExecution.addGoals(string) parameter must be instanceof " + String.class.getName());
        }
        this.getGoals().add(string);
    }
    
    public List<String> getGoals() {
        if (this.goals == null) {
            this.goals = new ArrayList<String>();
        }
        return this.goals;
    }
    
    public String getId() {
        return this.id;
    }
    
    public String getPhase() {
        return this.phase;
    }
    
    public void removeGoal(final String string) {
        if (!(string instanceof String)) {
            throw new ClassCastException("PluginExecution.removeGoals(string) parameter must be instanceof " + String.class.getName());
        }
        this.getGoals().remove(string);
    }
    
    public void setGoals(final List<String> goals) {
        this.goals = goals;
    }
    
    public void setId(final String id) {
        this.id = id;
    }
    
    public void setPhase(final String phase) {
        this.phase = phase;
    }
}
