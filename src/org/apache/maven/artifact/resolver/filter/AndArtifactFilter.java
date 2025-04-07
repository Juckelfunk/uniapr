// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.maven.artifact.resolver.filter;

import java.util.Iterator;
import org.apache.maven.artifact.Artifact;
import java.util.ArrayList;
import java.util.List;

public class AndArtifactFilter implements ArtifactFilter
{
    private final List filters;
    
    public AndArtifactFilter() {
        this.filters = new ArrayList();
    }
    
    public boolean include(final Artifact artifact) {
        boolean include = true;
        for (Iterator i = this.filters.iterator(); i.hasNext() && include; include = false) {
            final ArtifactFilter filter = i.next();
            if (!filter.include(artifact)) {}
        }
        return include;
    }
    
    public void add(final ArtifactFilter artifactFilter) {
        this.filters.add(artifactFilter);
    }
}
