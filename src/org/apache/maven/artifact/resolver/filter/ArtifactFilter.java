// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.maven.artifact.resolver.filter;

import org.apache.maven.artifact.Artifact;

public interface ArtifactFilter
{
    boolean include(final Artifact p0);
}
