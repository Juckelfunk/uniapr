// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.maven.artifact.handler;

public class DefaultArtifactHandler implements ArtifactHandler
{
    private String extension;
    private String type;
    private String classifier;
    private String directory;
    private String packaging;
    private boolean includesDependencies;
    private String language;
    private boolean addedToClasspath;
    
    public DefaultArtifactHandler() {
    }
    
    public DefaultArtifactHandler(final String type) {
        this.type = type;
    }
    
    public String getExtension() {
        if (this.extension == null) {
            this.extension = this.type;
        }
        return this.extension;
    }
    
    public String getType() {
        return this.type;
    }
    
    public String getClassifier() {
        return this.classifier;
    }
    
    public String getDirectory() {
        if (this.directory == null) {
            this.directory = this.getPackaging() + "s";
        }
        return this.directory;
    }
    
    public String getPackaging() {
        if (this.packaging == null) {
            this.packaging = this.type;
        }
        return this.packaging;
    }
    
    public boolean isIncludesDependencies() {
        return this.includesDependencies;
    }
    
    public String getLanguage() {
        if (this.language == null) {
            this.language = "none";
        }
        return this.language;
    }
    
    public boolean isAddedToClasspath() {
        return this.addedToClasspath;
    }
}
