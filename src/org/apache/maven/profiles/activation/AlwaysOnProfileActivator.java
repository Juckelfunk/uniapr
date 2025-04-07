// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.maven.profiles.activation;

import org.apache.maven.profiles.AlwaysOnActivation;
import org.apache.maven.model.Profile;

public class AlwaysOnProfileActivator implements ProfileActivator
{
    public boolean canDetermineActivation(final Profile profile) {
        return profile.getActivation() != null && profile.getActivation() instanceof AlwaysOnActivation;
    }
    
    public boolean isActive(final Profile profile) {
        return true;
    }
}
