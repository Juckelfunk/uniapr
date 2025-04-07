// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.maven.artifact.resolver;

import org.apache.maven.artifact.repository.ArtifactRepository;
import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import org.apache.maven.artifact.Artifact;

public class AbstractArtifactResolutionException extends Exception
{
    private String groupId;
    private String artifactId;
    private String version;
    private String type;
    private String classifier;
    private Artifact artifact;
    private List remoteRepositories;
    private final String originalMessage;
    private final String path;
    static final String LS;
    
    protected AbstractArtifactResolutionException(final String message, final String groupId, final String artifactId, final String version, final String type, final String classifier, final List remoteRepositories, final List path) {
        this(message, groupId, artifactId, version, type, classifier, remoteRepositories, path, null);
    }
    
    protected AbstractArtifactResolutionException(final String message, final String groupId, final String artifactId, final String version, final String type, final String classifier, final List remoteRepositories, final List path, final Throwable t) {
        super(constructMessageBase(message, groupId, artifactId, version, type, remoteRepositories, path), t);
        this.originalMessage = message;
        this.groupId = groupId;
        this.artifactId = artifactId;
        this.type = type;
        this.classifier = classifier;
        this.version = version;
        this.remoteRepositories = remoteRepositories;
        this.path = constructArtifactPath(path, "");
    }
    
    protected AbstractArtifactResolutionException(final String message, final Artifact artifact) {
        this(message, artifact, null);
    }
    
    protected AbstractArtifactResolutionException(final String message, final Artifact artifact, final List remoteRepositories) {
        this(message, artifact, remoteRepositories, null);
    }
    
    protected AbstractArtifactResolutionException(final String message, final Artifact artifact, final List remoteRepositories, final Throwable t) {
        this(message, artifact.getGroupId(), artifact.getArtifactId(), artifact.getVersion(), artifact.getType(), artifact.getClassifier(), remoteRepositories, artifact.getDependencyTrail(), t);
        this.artifact = artifact;
    }
    
    public Artifact getArtifact() {
        return this.artifact;
    }
    
    public String getGroupId() {
        return this.groupId;
    }
    
    public String getArtifactId() {
        return this.artifactId;
    }
    
    public String getVersion() {
        return this.version;
    }
    
    public String getType() {
        return this.type;
    }
    
    public String getClassifier() {
        return this.classifier;
    }
    
    public String getPath() {
        return this.path;
    }
    
    public List getRemoteRepositories() {
        return this.remoteRepositories;
    }
    
    public String getOriginalMessage() {
        return this.originalMessage;
    }
    
    protected static String constructArtifactPath(final List path, final String indentation) {
        final StringBuffer sb = new StringBuffer();
        if (path != null) {
            sb.append(AbstractArtifactResolutionException.LS);
            sb.append(indentation);
            sb.append("Path to dependency: ");
            sb.append(AbstractArtifactResolutionException.LS);
            int num = 1;
            final Iterator i = path.iterator();
            while (i.hasNext()) {
                sb.append(indentation);
                sb.append("\t");
                sb.append(num);
                sb.append(") ");
                sb.append(i.next());
                sb.append(AbstractArtifactResolutionException.LS);
                ++num;
            }
        }
        return sb.toString();
    }
    
    private static String constructMessageBase(final String message, final String groupId, final String artifactId, final String version, final String type, final List remoteRepositories, final List path) {
        final StringBuffer sb = new StringBuffer();
        sb.append(message);
        sb.append(AbstractArtifactResolutionException.LS);
        sb.append("  " + groupId + ":" + artifactId + ":" + type + ":" + version);
        sb.append(AbstractArtifactResolutionException.LS);
        if (remoteRepositories != null && !remoteRepositories.isEmpty()) {
            sb.append(AbstractArtifactResolutionException.LS);
            sb.append("from the specified remote repositories:");
            sb.append(AbstractArtifactResolutionException.LS + "  ");
            final Iterator i = new HashSet(remoteRepositories).iterator();
            while (i.hasNext()) {
                final ArtifactRepository remoteRepository = i.next();
                sb.append(remoteRepository.getId());
                sb.append(" (");
                sb.append(remoteRepository.getUrl());
                sb.append(")");
                if (i.hasNext()) {
                    sb.append(",\n  ");
                }
            }
        }
        sb.append(AbstractArtifactResolutionException.LS);
        sb.append(constructArtifactPath(path, ""));
        sb.append(AbstractArtifactResolutionException.LS);
        return sb.toString();
    }
    
    protected static String constructMissingArtifactMessage(final String message, final String indentation, final String groupId, final String artifactId, final String version, final String type, final String classifier, final String downloadUrl, final List path) {
        final StringBuffer sb = new StringBuffer(message);
        if (!"pom".equals(type)) {
            if (downloadUrl != null) {
                sb.append(AbstractArtifactResolutionException.LS);
                sb.append(AbstractArtifactResolutionException.LS);
                sb.append(indentation);
                sb.append("Try downloading the file manually from: ");
                sb.append(AbstractArtifactResolutionException.LS);
                sb.append(indentation);
                sb.append("    ");
                sb.append(downloadUrl);
            }
            else {
                sb.append(AbstractArtifactResolutionException.LS);
                sb.append(AbstractArtifactResolutionException.LS);
                sb.append(indentation);
                sb.append("Try downloading the file manually from the project website.");
            }
            sb.append(AbstractArtifactResolutionException.LS);
            sb.append(AbstractArtifactResolutionException.LS);
            sb.append(indentation);
            sb.append("Then, install it using the command: ");
            sb.append(AbstractArtifactResolutionException.LS);
            sb.append(indentation);
            sb.append("    mvn install:install-file -DgroupId=");
            sb.append(groupId);
            sb.append(" -DartifactId=");
            sb.append(artifactId);
            sb.append(" -Dversion=");
            sb.append(version);
            if (classifier != null && !classifier.equals("")) {
                sb.append(" -Dclassifier=");
                sb.append(classifier);
            }
            sb.append(" -Dpackaging=");
            sb.append(type);
            sb.append(" -Dfile=/path/to/file");
            sb.append(AbstractArtifactResolutionException.LS);
            sb.append(AbstractArtifactResolutionException.LS);
            sb.append(indentation);
            sb.append("Alternatively, if you host your own repository you can deploy the file there: ");
            sb.append(AbstractArtifactResolutionException.LS);
            sb.append(indentation);
            sb.append("    mvn deploy:deploy-file -DgroupId=");
            sb.append(groupId);
            sb.append(" -DartifactId=");
            sb.append(artifactId);
            sb.append(" -Dversion=");
            sb.append(version);
            if (classifier != null && !classifier.equals("")) {
                sb.append(" -Dclassifier=");
                sb.append(classifier);
            }
            sb.append(" -Dpackaging=");
            sb.append(type);
            sb.append(" -Dfile=/path/to/file");
            sb.append(" -Durl=[url] -DrepositoryId=[id]");
            sb.append(AbstractArtifactResolutionException.LS);
        }
        sb.append(constructArtifactPath(path, indentation));
        sb.append(AbstractArtifactResolutionException.LS);
        return sb.toString();
    }
    
    public String getArtifactPath() {
        return this.path;
    }
    
    static {
        LS = System.getProperty("line.separator");
    }
}
