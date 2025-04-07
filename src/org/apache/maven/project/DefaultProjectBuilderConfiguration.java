// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.maven.project;

import java.util.Date;
import java.util.Properties;
import org.apache.maven.artifact.repository.ArtifactRepository;
import org.apache.maven.profiles.ProfileManager;

public class DefaultProjectBuilderConfiguration implements ProjectBuilderConfiguration
{
    private ProfileManager globalProfileManager;
    private ArtifactRepository localRepository;
    private Properties userProperties;
    private Properties executionProperties;
    private Date buildStartTime;
    
    public DefaultProjectBuilderConfiguration() {
        this.executionProperties = System.getProperties();
    }
    
    public ProjectBuilderConfiguration setGlobalProfileManager(final ProfileManager globalProfileManager) {
        this.globalProfileManager = globalProfileManager;
        return this;
    }
    
    public ProfileManager getGlobalProfileManager() {
        return this.globalProfileManager;
    }
    
    public ProjectBuilderConfiguration setLocalRepository(final ArtifactRepository localRepository) {
        this.localRepository = localRepository;
        return this;
    }
    
    public ArtifactRepository getLocalRepository() {
        return this.localRepository;
    }
    
    public ProjectBuilderConfiguration setUserProperties(final Properties userProperties) {
        this.userProperties = userProperties;
        return this;
    }
    
    public Properties getUserProperties() {
        if (this.userProperties == null) {
            this.userProperties = new Properties();
        }
        return this.userProperties;
    }
    
    public Properties getExecutionProperties() {
        return this.executionProperties;
    }
    
    public ProjectBuilderConfiguration setExecutionProperties(final Properties executionProperties) {
        this.executionProperties = executionProperties;
        return this;
    }
    
    public Date getBuildStartTime() {
        return this.buildStartTime;
    }
    
    public ProjectBuilderConfiguration setBuildStartTime(final Date buildStartTime) {
        this.buildStartTime = buildStartTime;
        return this;
    }
}
