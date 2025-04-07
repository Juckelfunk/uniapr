// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.maven.artifact.resolver.filter;

import java.util.Iterator;
import org.apache.maven.artifact.Artifact;
import java.util.List;

public class IncludesArtifactFilter implements ArtifactFilter
{
    private final List patterns;
    
    public IncludesArtifactFilter(final List patterns) {
        this.patterns = patterns;
    }
    
    public boolean include(final Artifact artifact) {
        final String id = artifact.getGroupId() + ":" + artifact.getArtifactId();
        boolean matched = false;
        for (Iterator i = this.patterns.iterator(); i.hasNext() & !matched; matched = true) {
            if (id.equals(i.next())) {}
        }
        return matched;
    }
}
