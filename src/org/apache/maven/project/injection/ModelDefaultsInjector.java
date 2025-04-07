// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.maven.project.injection;

import org.apache.maven.model.Plugin;
import org.apache.maven.model.Model;

public interface ModelDefaultsInjector
{
    public static final String ROLE = ModelDefaultsInjector.class.getName();
    
    void injectDefaults(final Model p0);
    
    void mergePluginWithDefaults(final Plugin p0, final Plugin p1);
}
