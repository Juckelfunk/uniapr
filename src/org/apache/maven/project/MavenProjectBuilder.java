// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.maven.project;

import org.apache.maven.project.interpolation.ModelInterpolationException;
import java.util.List;
import org.apache.maven.artifact.Artifact;
import org.apache.maven.artifact.resolver.ArtifactNotFoundException;
import org.apache.maven.artifact.resolver.ArtifactResolutionException;
import org.apache.maven.wagon.events.TransferListener;
import org.apache.maven.profiles.ProfileManager;
import org.apache.maven.artifact.repository.ArtifactRepository;
import java.io.File;

public interface MavenProjectBuilder
{
    public static final String ROLE = MavenProjectBuilder.class.getName();
    public static final String STANDALONE_SUPERPOM_GROUPID = "org.apache.maven";
    public static final String STANDALONE_SUPERPOM_ARTIFACTID = "super-pom";
    public static final String STANDALONE_SUPERPOM_VERSION = "2.0";
    
    MavenProject build(final File p0, final ArtifactRepository p1, final ProfileManager p2) throws ProjectBuildingException;
    
    MavenProject build(final File p0, final ArtifactRepository p1, final ProfileManager p2, final boolean p3) throws ProjectBuildingException;
    
    MavenProject buildWithDependencies(final File p0, final ArtifactRepository p1, final ProfileManager p2, final TransferListener p3) throws ProjectBuildingException, ArtifactResolutionException, ArtifactNotFoundException;
    
    MavenProject buildWithDependencies(final File p0, final ArtifactRepository p1, final ProfileManager p2) throws ProjectBuildingException, ArtifactResolutionException, ArtifactNotFoundException;
    
    MavenProject buildFromRepository(final Artifact p0, final List p1, final ArtifactRepository p2) throws ProjectBuildingException;
    
    MavenProject buildFromRepository(final Artifact p0, final List p1, final ArtifactRepository p2, final boolean p3) throws ProjectBuildingException;
    
    @Deprecated
    MavenProject buildStandaloneSuperProject(final ArtifactRepository p0) throws ProjectBuildingException;
    
    @Deprecated
    MavenProject buildStandaloneSuperProject(final ArtifactRepository p0, final ProfileManager p1) throws ProjectBuildingException;
    
    MavenProject buildStandaloneSuperProject(final ProjectBuilderConfiguration p0) throws ProjectBuildingException;
    
    MavenProject build(final File p0, final ProjectBuilderConfiguration p1) throws ProjectBuildingException;
    
    MavenProject build(final File p0, final ProjectBuilderConfiguration p1, final boolean p2) throws ProjectBuildingException;
    
    void calculateConcreteState(final MavenProject p0, final ProjectBuilderConfiguration p1) throws ModelInterpolationException;
    
    void calculateConcreteState(final MavenProject p0, final ProjectBuilderConfiguration p1, final boolean p2) throws ModelInterpolationException;
}
