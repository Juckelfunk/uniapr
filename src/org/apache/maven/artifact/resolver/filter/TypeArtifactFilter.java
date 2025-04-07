// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.maven.artifact.resolver.filter;

import org.apache.maven.artifact.Artifact;

public class TypeArtifactFilter implements ArtifactFilter
{
    private String type;
    
    public TypeArtifactFilter(final String type) {
        this.type = "jar";
        this.type = type;
    }
    
    public boolean include(final Artifact artifact) {
        return this.type.equals(artifact.getType());
    }
}
