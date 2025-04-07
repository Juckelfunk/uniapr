// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.maven.plugin.registry.io.xpp3;

import org.apache.maven.plugin.registry.TrackableBase;
import java.util.Iterator;
import org.apache.maven.plugin.registry.Plugin;
import java.io.IOException;
import org.codehaus.plexus.util.xml.pull.XmlSerializer;
import org.codehaus.plexus.util.xml.pull.MXSerializer;
import org.apache.maven.plugin.registry.PluginRegistry;
import java.io.Writer;

public class PluginRegistryXpp3Writer
{
    private static final String NAMESPACE;
    
    public void write(final Writer writer, final PluginRegistry pluginRegistry) throws IOException {
        final XmlSerializer serializer = new MXSerializer();
        serializer.setProperty("http://xmlpull.org/v1/doc/properties.html#serializer-indentation", "  ");
        serializer.setProperty("http://xmlpull.org/v1/doc/properties.html#serializer-line-separator", "\n");
        serializer.setOutput(writer);
        serializer.startDocument(pluginRegistry.getModelEncoding(), null);
        this.writePluginRegistry(pluginRegistry, "pluginRegistry", serializer);
        serializer.endDocument();
    }
    
    private void writePlugin(final Plugin plugin, final String tagName, final XmlSerializer serializer) throws IOException {
        if (plugin != null) {
            serializer.startTag(PluginRegistryXpp3Writer.NAMESPACE, tagName);
            if (plugin.getGroupId() != null) {
                serializer.startTag(PluginRegistryXpp3Writer.NAMESPACE, "groupId").text(plugin.getGroupId()).endTag(PluginRegistryXpp3Writer.NAMESPACE, "groupId");
            }
            if (plugin.getArtifactId() != null) {
                serializer.startTag(PluginRegistryXpp3Writer.NAMESPACE, "artifactId").text(plugin.getArtifactId()).endTag(PluginRegistryXpp3Writer.NAMESPACE, "artifactId");
            }
            if (plugin.getLastChecked() != null) {
                serializer.startTag(PluginRegistryXpp3Writer.NAMESPACE, "lastChecked").text(plugin.getLastChecked()).endTag(PluginRegistryXpp3Writer.NAMESPACE, "lastChecked");
            }
            if (plugin.getUseVersion() != null) {
                serializer.startTag(PluginRegistryXpp3Writer.NAMESPACE, "useVersion").text(plugin.getUseVersion()).endTag(PluginRegistryXpp3Writer.NAMESPACE, "useVersion");
            }
            if (plugin.getRejectedVersions() != null && plugin.getRejectedVersions().size() > 0) {
                serializer.startTag(PluginRegistryXpp3Writer.NAMESPACE, "rejectedVersions");
                for (final String rejectedVersion : plugin.getRejectedVersions()) {
                    serializer.startTag(PluginRegistryXpp3Writer.NAMESPACE, "rejectedVersion").text(rejectedVersion).endTag(PluginRegistryXpp3Writer.NAMESPACE, "rejectedVersion");
                }
                serializer.endTag(PluginRegistryXpp3Writer.NAMESPACE, "rejectedVersions");
            }
            serializer.endTag(PluginRegistryXpp3Writer.NAMESPACE, tagName);
        }
    }
    
    private void writePluginRegistry(final PluginRegistry pluginRegistry, final String tagName, final XmlSerializer serializer) throws IOException {
        if (pluginRegistry != null) {
            serializer.setPrefix("", "http://maven.apache.org/PLUGIN_REGISTRY/1.0.0");
            serializer.setPrefix("xsi", "http://www.w3.org/2001/XMLSchema-instance");
            serializer.startTag(PluginRegistryXpp3Writer.NAMESPACE, tagName);
            serializer.attribute("", "xsi:schemaLocation", "http://maven.apache.org/PLUGIN_REGISTRY/1.0.0 http://maven.apache.org/xsd/plugin-registry-1.0.0.xsd");
            if (pluginRegistry.getUpdateInterval() != null && !pluginRegistry.getUpdateInterval().equals("never")) {
                serializer.startTag(PluginRegistryXpp3Writer.NAMESPACE, "updateInterval").text(pluginRegistry.getUpdateInterval()).endTag(PluginRegistryXpp3Writer.NAMESPACE, "updateInterval");
            }
            if (pluginRegistry.getAutoUpdate() != null) {
                serializer.startTag(PluginRegistryXpp3Writer.NAMESPACE, "autoUpdate").text(pluginRegistry.getAutoUpdate()).endTag(PluginRegistryXpp3Writer.NAMESPACE, "autoUpdate");
            }
            if (pluginRegistry.getCheckLatest() != null) {
                serializer.startTag(PluginRegistryXpp3Writer.NAMESPACE, "checkLatest").text(pluginRegistry.getCheckLatest()).endTag(PluginRegistryXpp3Writer.NAMESPACE, "checkLatest");
            }
            if (pluginRegistry.getPlugins() != null && pluginRegistry.getPlugins().size() > 0) {
                serializer.startTag(PluginRegistryXpp3Writer.NAMESPACE, "plugins");
                for (final Plugin o : pluginRegistry.getPlugins()) {
                    this.writePlugin(o, "plugin", serializer);
                }
                serializer.endTag(PluginRegistryXpp3Writer.NAMESPACE, "plugins");
            }
            serializer.endTag(PluginRegistryXpp3Writer.NAMESPACE, tagName);
        }
    }
    
    private void writeTrackableBase(final TrackableBase trackableBase, final String tagName, final XmlSerializer serializer) throws IOException {
        if (trackableBase != null) {
            serializer.startTag(PluginRegistryXpp3Writer.NAMESPACE, tagName);
            serializer.endTag(PluginRegistryXpp3Writer.NAMESPACE, tagName);
        }
    }
    
    static {
        NAMESPACE = null;
    }
}
