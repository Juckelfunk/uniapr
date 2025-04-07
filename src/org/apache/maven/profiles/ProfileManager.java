// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.maven.profiles;

import java.util.Properties;
import org.apache.maven.settings.Settings;
import java.util.Map;
import org.apache.maven.profiles.activation.ProfileActivationException;
import java.util.List;
import org.apache.maven.model.Profile;

public interface ProfileManager
{
    void addProfile(final Profile p0);
    
    void explicitlyActivate(final String p0);
    
    void explicitlyActivate(final List p0);
    
    void explicitlyDeactivate(final String p0);
    
    void explicitlyDeactivate(final List p0);
    
    void activateAsDefault(final String p0);
    
    List getActiveProfiles() throws ProfileActivationException;
    
    void addProfiles(final List p0);
    
    Map getProfilesById();
    
    List getExplicitlyActivatedIds();
    
    List getExplicitlyDeactivatedIds();
    
    List getIdsActivatedByDefault();
    
    void loadSettingsProfiles(final Settings p0);
    
    Properties getRequestProperties();
}
