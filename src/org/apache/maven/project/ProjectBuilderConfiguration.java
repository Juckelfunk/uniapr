// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.maven.project;

import java.util.Date;
import java.util.Properties;
import org.apache.maven.profiles.ProfileManager;
import org.apache.maven.artifact.repository.ArtifactRepository;

public interface ProjectBuilderConfiguration
{
    ArtifactRepository getLocalRepository();
    
    ProfileManager getGlobalProfileManager();
    
    Properties getUserProperties();
    
    Properties getExecutionProperties();
    
    ProjectBuilderConfiguration setGlobalProfileManager(final ProfileManager p0);
    
    ProjectBuilderConfiguration setLocalRepository(final ArtifactRepository p0);
    
    ProjectBuilderConfiguration setUserProperties(final Properties p0);
    
    ProjectBuilderConfiguration setExecutionProperties(final Properties p0);
    
    Date getBuildStartTime();
    
    ProjectBuilderConfiguration setBuildStartTime(final Date p0);
}
