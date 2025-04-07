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

public class Reporting implements Serializable
{
    private Boolean excludeDefaultsValue;
    private String outputDirectory;
    private List<ReportPlugin> plugins;
    Map reportPluginMap;
    
    public void addPlugin(final ReportPlugin reportPlugin) {
        if (!(reportPlugin instanceof ReportPlugin)) {
            throw new ClassCastException("Reporting.addPlugins(reportPlugin) parameter must be instanceof " + ReportPlugin.class.getName());
        }
        this.getPlugins().add(reportPlugin);
    }
    
    public String getOutputDirectory() {
        return this.outputDirectory;
    }
    
    public List<ReportPlugin> getPlugins() {
        if (this.plugins == null) {
            this.plugins = new ArrayList<ReportPlugin>();
        }
        return this.plugins;
    }
    
    public Boolean isExcludeDefaultsValue() {
        return this.excludeDefaultsValue;
    }
    
    public void removePlugin(final ReportPlugin reportPlugin) {
        if (!(reportPlugin instanceof ReportPlugin)) {
            throw new ClassCastException("Reporting.removePlugins(reportPlugin) parameter must be instanceof " + ReportPlugin.class.getName());
        }
        this.getPlugins().remove(reportPlugin);
    }
    
    public void setExcludeDefaultsValue(final Boolean excludeDefaultsValue) {
        this.excludeDefaultsValue = excludeDefaultsValue;
    }
    
    public void setOutputDirectory(final String outputDirectory) {
        this.outputDirectory = outputDirectory;
    }
    
    public void setPlugins(final List<ReportPlugin> plugins) {
        this.plugins = plugins;
    }
    
    public void flushReportPluginMap() {
        this.reportPluginMap = null;
    }
    
    public Map getReportPluginsAsMap() {
        if (this.reportPluginMap == null) {
            this.reportPluginMap = new LinkedHashMap();
            if (this.getPlugins() != null) {
                for (final ReportPlugin reportPlugin : this.getPlugins()) {
                    this.reportPluginMap.put(reportPlugin.getKey(), reportPlugin);
                }
            }
        }
        return this.reportPluginMap;
    }
    
    public boolean isExcludeDefaults() {
        return this.excludeDefaultsValue != null && this.excludeDefaultsValue;
    }
    
    public void setExcludeDefaults(final boolean excludeDefaults) {
        this.excludeDefaultsValue = (excludeDefaults ? Boolean.TRUE : Boolean.FALSE);
    }
    
    public void setExcludeDefaultsValue(final String excludeDefaults) {
        this.excludeDefaultsValue = ((excludeDefaults != null) ? Boolean.valueOf(excludeDefaults) : null);
    }
}
