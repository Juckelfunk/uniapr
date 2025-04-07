// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.maven.profiles.activation;

import org.apache.maven.model.Profile;

public interface ProfileActivator
{
    public static final String ROLE = ProfileActivator.class.getName();
    
    boolean canDetermineActivation(final Profile p0);
    
    boolean isActive(final Profile p0) throws ProfileActivationException;
}
