// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.maven.artifact.resolver.filter;

import org.apache.maven.artifact.Artifact;
import java.util.Collection;
import java.util.HashSet;
import java.util.Arrays;
import java.util.Set;

public class ExclusionSetFilter implements ArtifactFilter
{
    private Set excludes;
    
    public ExclusionSetFilter(final String[] excludes) {
        this.excludes = new HashSet(Arrays.asList(excludes));
    }
    
    public ExclusionSetFilter(final Set excludes) {
        this.excludes = excludes;
    }
    
    public boolean include(final Artifact artifact) {
        return !this.excludes.contains(artifact.getArtifactId());
    }
}
