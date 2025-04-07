// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.maven.artifact.resolver.filter;

import org.apache.maven.artifact.Artifact;
import java.util.List;

public class ExcludesArtifactFilter extends IncludesArtifactFilter
{
    public ExcludesArtifactFilter(final List patterns) {
        super(patterns);
    }
    
    @Override
    public boolean include(final Artifact artifact) {
        return !super.include(artifact);
    }
}
