// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.maven.artifact.installer;

import java.io.File;
import org.apache.maven.artifact.repository.ArtifactRepository;
import org.apache.maven.artifact.Artifact;

public interface ArtifactInstaller
{
    public static final String ROLE = ArtifactInstaller.class.getName();
    
    @Deprecated
    void install(final String p0, final String p1, final Artifact p2, final ArtifactRepository p3) throws ArtifactInstallationException;
    
    void install(final File p0, final Artifact p1, final ArtifactRepository p2) throws ArtifactInstallationException;
}
