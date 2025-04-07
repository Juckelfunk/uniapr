// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.maven.artifact.repository.metadata.io.xpp3;

import org.apache.maven.artifact.repository.metadata.Versioning;
import org.apache.maven.artifact.repository.metadata.Snapshot;
import java.util.Iterator;
import org.apache.maven.artifact.repository.metadata.Plugin;
import java.io.IOException;
import org.codehaus.plexus.util.xml.pull.XmlSerializer;
import org.codehaus.plexus.util.xml.pull.MXSerializer;
import org.apache.maven.artifact.repository.metadata.Metadata;
import java.io.Writer;

public class MetadataXpp3Writer
{
    private static final String NAMESPACE;
    
    public void write(final Writer writer, final Metadata metadata) throws IOException {
        final XmlSerializer serializer = new MXSerializer();
        serializer.setProperty("http://xmlpull.org/v1/doc/properties.html#serializer-indentation", "  ");
        serializer.setProperty("http://xmlpull.org/v1/doc/properties.html#serializer-line-separator", "\n");
        serializer.setOutput(writer);
        serializer.startDocument(metadata.getModelEncoding(), null);
        this.writeMetadata(metadata, "metadata", serializer);
        serializer.endDocument();
    }
    
    private void writeMetadata(final Metadata metadata, final String tagName, final XmlSerializer serializer) throws IOException {
        if (metadata != null) {
            serializer.startTag(MetadataXpp3Writer.NAMESPACE, tagName);
            if (metadata.getGroupId() != null) {
                serializer.startTag(MetadataXpp3Writer.NAMESPACE, "groupId").text(metadata.getGroupId()).endTag(MetadataXpp3Writer.NAMESPACE, "groupId");
            }
            if (metadata.getArtifactId() != null) {
                serializer.startTag(MetadataXpp3Writer.NAMESPACE, "artifactId").text(metadata.getArtifactId()).endTag(MetadataXpp3Writer.NAMESPACE, "artifactId");
            }
            if (metadata.getVersion() != null) {
                serializer.startTag(MetadataXpp3Writer.NAMESPACE, "version").text(metadata.getVersion()).endTag(MetadataXpp3Writer.NAMESPACE, "version");
            }
            if (metadata.getVersioning() != null) {
                this.writeVersioning(metadata.getVersioning(), "versioning", serializer);
            }
            if (metadata.getPlugins() != null && metadata.getPlugins().size() > 0) {
                serializer.startTag(MetadataXpp3Writer.NAMESPACE, "plugins");
                for (final Plugin o : metadata.getPlugins()) {
                    this.writePlugin(o, "plugin", serializer);
                }
                serializer.endTag(MetadataXpp3Writer.NAMESPACE, "plugins");
            }
            serializer.endTag(MetadataXpp3Writer.NAMESPACE, tagName);
        }
    }
    
    private void writePlugin(final Plugin plugin, final String tagName, final XmlSerializer serializer) throws IOException {
        if (plugin != null) {
            serializer.startTag(MetadataXpp3Writer.NAMESPACE, tagName);
            if (plugin.getName() != null) {
                serializer.startTag(MetadataXpp3Writer.NAMESPACE, "name").text(plugin.getName()).endTag(MetadataXpp3Writer.NAMESPACE, "name");
            }
            if (plugin.getPrefix() != null) {
                serializer.startTag(MetadataXpp3Writer.NAMESPACE, "prefix").text(plugin.getPrefix()).endTag(MetadataXpp3Writer.NAMESPACE, "prefix");
            }
            if (plugin.getArtifactId() != null) {
                serializer.startTag(MetadataXpp3Writer.NAMESPACE, "artifactId").text(plugin.getArtifactId()).endTag(MetadataXpp3Writer.NAMESPACE, "artifactId");
            }
            serializer.endTag(MetadataXpp3Writer.NAMESPACE, tagName);
        }
    }
    
    private void writeSnapshot(final Snapshot snapshot, final String tagName, final XmlSerializer serializer) throws IOException {
        if (snapshot != null) {
            serializer.startTag(MetadataXpp3Writer.NAMESPACE, tagName);
            if (snapshot.getTimestamp() != null) {
                serializer.startTag(MetadataXpp3Writer.NAMESPACE, "timestamp").text(snapshot.getTimestamp()).endTag(MetadataXpp3Writer.NAMESPACE, "timestamp");
            }
            if (snapshot.getBuildNumber() != 0) {
                serializer.startTag(MetadataXpp3Writer.NAMESPACE, "buildNumber").text(String.valueOf(snapshot.getBuildNumber())).endTag(MetadataXpp3Writer.NAMESPACE, "buildNumber");
            }
            if (snapshot.isLocalCopy()) {
                serializer.startTag(MetadataXpp3Writer.NAMESPACE, "localCopy").text(String.valueOf(snapshot.isLocalCopy())).endTag(MetadataXpp3Writer.NAMESPACE, "localCopy");
            }
            serializer.endTag(MetadataXpp3Writer.NAMESPACE, tagName);
        }
    }
    
    private void writeVersioning(final Versioning versioning, final String tagName, final XmlSerializer serializer) throws IOException {
        if (versioning != null) {
            serializer.startTag(MetadataXpp3Writer.NAMESPACE, tagName);
            if (versioning.getLatest() != null) {
                serializer.startTag(MetadataXpp3Writer.NAMESPACE, "latest").text(versioning.getLatest()).endTag(MetadataXpp3Writer.NAMESPACE, "latest");
            }
            if (versioning.getRelease() != null) {
                serializer.startTag(MetadataXpp3Writer.NAMESPACE, "release").text(versioning.getRelease()).endTag(MetadataXpp3Writer.NAMESPACE, "release");
            }
            if (versioning.getSnapshot() != null) {
                this.writeSnapshot(versioning.getSnapshot(), "snapshot", serializer);
            }
            if (versioning.getVersions() != null && versioning.getVersions().size() > 0) {
                serializer.startTag(MetadataXpp3Writer.NAMESPACE, "versions");
                for (final String version : versioning.getVersions()) {
                    serializer.startTag(MetadataXpp3Writer.NAMESPACE, "version").text(version).endTag(MetadataXpp3Writer.NAMESPACE, "version");
                }
                serializer.endTag(MetadataXpp3Writer.NAMESPACE, "versions");
            }
            if (versioning.getLastUpdated() != null) {
                serializer.startTag(MetadataXpp3Writer.NAMESPACE, "lastUpdated").text(versioning.getLastUpdated()).endTag(MetadataXpp3Writer.NAMESPACE, "lastUpdated");
            }
            serializer.endTag(MetadataXpp3Writer.NAMESPACE, tagName);
        }
    }
    
    static {
        NAMESPACE = null;
    }
}
