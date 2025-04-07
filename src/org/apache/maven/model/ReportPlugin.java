// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.maven.model;

import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.ArrayList;
import java.util.Map;
import java.util.List;
import java.io.Serializable;

public class ReportPlugin implements Serializable
{
    private String groupId;
    private String artifactId;
    private String version;
    private String inherited;
    private Object configuration;
    private List<ReportSet> reportSets;
    private Map reportSetMap;
    private boolean inheritanceApplied;
    
    public ReportPlugin() {
        this.groupId = "org.apache.maven.plugins";
        this.reportSetMap = null;
        this.inheritanceApplied = true;
    }
    
    public void addReportSet(final ReportSet reportSet) {
        if (!(reportSet instanceof ReportSet)) {
            throw new ClassCastException("ReportPlugin.addReportSets(reportSet) parameter must be instanceof " + ReportSet.class.getName());
        }
        this.getReportSets().add(reportSet);
    }
    
    public String getArtifactId() {
        return this.artifactId;
    }
    
    public Object getConfiguration() {
        return this.configuration;
    }
    
    public String getGroupId() {
        return this.groupId;
    }
    
    public String getInherited() {
        return this.inherited;
    }
    
    public List<ReportSet> getReportSets() {
        if (this.reportSets == null) {
            this.reportSets = new ArrayList<ReportSet>();
        }
        return this.reportSets;
    }
    
    public String getVersion() {
        return this.version;
    }
    
    public void removeReportSet(final ReportSet reportSet) {
        if (!(reportSet instanceof ReportSet)) {
            throw new ClassCastException("ReportPlugin.removeReportSets(reportSet) parameter must be instanceof " + ReportSet.class.getName());
        }
        this.getReportSets().remove(reportSet);
    }
    
    public void setArtifactId(final String artifactId) {
        this.artifactId = artifactId;
    }
    
    public void setConfiguration(final Object configuration) {
        this.configuration = configuration;
    }
    
    public void setGroupId(final String groupId) {
        this.groupId = groupId;
    }
    
    public void setInherited(final String inherited) {
        this.inherited = inherited;
    }
    
    public void setReportSets(final List<ReportSet> reportSets) {
        this.reportSets = reportSets;
    }
    
    public void setVersion(final String version) {
        this.version = version;
    }
    
    public void flushReportSetMap() {
        this.reportSetMap = null;
    }
    
    public Map getReportSetsAsMap() {
        if (this.reportSetMap == null) {
            this.reportSetMap = new LinkedHashMap();
            if (this.getReportSets() != null) {
                for (final ReportSet reportSet : this.getReportSets()) {
                    this.reportSetMap.put(reportSet.getId(), reportSet);
                }
            }
        }
        return this.reportSetMap;
    }
    
    public String getKey() {
        return constructKey(this.groupId, this.artifactId);
    }
    
    public static String constructKey(final String groupId, final String artifactId) {
        return groupId + ":" + artifactId;
    }
    
    public void unsetInheritanceApplied() {
        this.inheritanceApplied = false;
    }
    
    public boolean isInheritanceApplied() {
        return this.inheritanceApplied;
    }
    
    @Override
    public boolean equals(final Object other) {
        if (other instanceof ReportPlugin) {
            final ReportPlugin otherPlugin = (ReportPlugin)other;
            return this.getKey().equals(otherPlugin.getKey());
        }
        return false;
    }
    
    @Override
    public int hashCode() {
        return this.getKey().hashCode();
    }
    
    @Override
    public String toString() {
        return "ReportPlugin [" + this.getKey() + "]";
    }
}
