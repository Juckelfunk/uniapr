// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.maven.project.injection;

import org.apache.maven.model.Model;
import org.apache.maven.model.Profile;

public interface ProfileInjector
{
    public static final String ROLE = ProfileInjector.class.getName();
    
    void inject(final Profile p0, final Model p1);
}
