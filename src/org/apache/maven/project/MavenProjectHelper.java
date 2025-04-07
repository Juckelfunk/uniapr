// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.maven.project;

import java.util.List;
import java.io.File;

public interface MavenProjectHelper
{
    public static final String ROLE = MavenProjectHelper.class.getName();
    
    void attachArtifact(final MavenProject p0, final File p1, final String p2);
    
    void attachArtifact(final MavenProject p0, final String p1, final File p2);
    
    void attachArtifact(final MavenProject p0, final String p1, final String p2, final File p3);
    
    void addResource(final MavenProject p0, final String p1, final List p2, final List p3);
    
    void addTestResource(final MavenProject p0, final String p1, final List p2, final List p3);
}
