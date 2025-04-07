// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.maven.profiles.activation;

import org.apache.maven.model.Profile;

public abstract class DetectedProfileActivator implements ProfileActivator
{
    public boolean canDetermineActivation(final Profile profile) {
        return this.canDetectActivation(profile);
    }
    
    protected abstract boolean canDetectActivation(final Profile p0);
}
