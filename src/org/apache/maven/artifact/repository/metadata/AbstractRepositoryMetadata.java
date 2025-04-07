// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.maven.artifact.repository.metadata;

import org.apache.maven.artifact.Artifact;
import java.io.Writer;
import java.io.Reader;
import org.apache.maven.artifact.repository.metadata.io.xpp3.MetadataXpp3Writer;
import org.codehaus.plexus.util.WriterFactory;
import org.codehaus.plexus.util.IOUtil;
import org.codehaus.plexus.util.ReaderFactory;
import java.io.File;
import org.apache.maven.artifact.metadata.ArtifactMetadata;
import org.apache.maven.artifact.repository.metadata.io.xpp3.MetadataXpp3Reader;
import org.codehaus.plexus.util.xml.pull.XmlPullParserException;
import java.io.IOException;
import org.apache.maven.artifact.repository.ArtifactRepository;

public abstract class AbstractRepositoryMetadata implements RepositoryMetadata
{
    private Metadata metadata;
    
    protected AbstractRepositoryMetadata(final Metadata metadata) {
        this.metadata = metadata;
    }
    
    public String getRemoteFilename() {
        return "maven-metadata.xml";
    }
    
    public String getLocalFilename(final ArtifactRepository repository) {
        return "maven-metadata-" + repository.getKey() + ".xml";
    }
    
    public void storeInLocalRepository(final ArtifactRepository localRepository, final ArtifactRepository remoteRepository) throws RepositoryMetadataStoreException {
        try {
            this.updateRepositoryMetadata(localRepository, remoteRepository);
        }
        catch (IOException e) {
            throw new RepositoryMetadataStoreException("Error updating group repository metadata", e);
        }
        catch (XmlPullParserException e2) {
            throw new RepositoryMetadataStoreException("Error updating group repository metadata", e2);
        }
    }
    
    protected void updateRepositoryMetadata(final ArtifactRepository localRepository, final ArtifactRepository remoteRepository) throws IOException, XmlPullParserException {
        final MetadataXpp3Reader mappingReader = new MetadataXpp3Reader();
        Metadata metadata = null;
        final File metadataFile = new File(localRepository.getBasedir(), localRepository.pathOfLocalRepositoryMetadata(this, remoteRepository));
        if (metadataFile.exists()) {
            Reader reader = null;
            try {
                reader = ReaderFactory.newXmlReader(metadataFile);
                metadata = mappingReader.read(reader, false);
            }
            finally {
                IOUtil.close(reader);
            }
        }
        boolean changed;
        if (metadata == null) {
            metadata = this.metadata;
            changed = true;
        }
        else {
            changed = metadata.merge(this.metadata);
        }
        final String version = metadata.getVersion();
        if (version != null && ("LATEST".equals(version) || "RELEASE".equals(version))) {
            metadata.setVersion(null);
        }
        if (changed || !metadataFile.exists()) {
            Writer writer = null;
            try {
                metadataFile.getParentFile().mkdirs();
                writer = WriterFactory.newXmlWriter(metadataFile);
                final MetadataXpp3Writer mappingWriter = new MetadataXpp3Writer();
                mappingWriter.write(writer, metadata);
            }
            finally {
                IOUtil.close(writer);
            }
        }
        else {
            metadataFile.setLastModified(System.currentTimeMillis());
        }
    }
    
    @Override
    public String toString() {
        return "repository metadata for: '" + this.getKey() + "'";
    }
    
    protected static Metadata createMetadata(final Artifact artifact, final Versioning versioning) {
        final Metadata metadata = new Metadata();
        metadata.setGroupId(artifact.getGroupId());
        metadata.setArtifactId(artifact.getArtifactId());
        metadata.setVersion(artifact.getVersion());
        metadata.setVersioning(versioning);
        return metadata;
    }
    
    protected static Versioning createVersioning(final Snapshot snapshot) {
        final Versioning versioning = new Versioning();
        versioning.setSnapshot(snapshot);
        versioning.updateTimestamp();
        return versioning;
    }
    
    public void setMetadata(final Metadata metadata) {
        this.metadata = metadata;
    }
    
    public Metadata getMetadata() {
        return this.metadata;
    }
    
    public void merge(final ArtifactMetadata metadata) {
        final AbstractRepositoryMetadata repoMetadata = (AbstractRepositoryMetadata)metadata;
        this.metadata.merge(repoMetadata.getMetadata());
    }
    
    public String extendedToString() {
        final StringBuffer buffer = new StringBuffer();
        buffer.append("\nRepository Metadata\n--------------------------");
        buffer.append("\nGroupId: ").append(this.getGroupId());
        buffer.append("\nArtifactId: ").append(this.getArtifactId());
        buffer.append("\nMetadata Type: ").append(this.getClass().getName());
        return buffer.toString();
    }
}
