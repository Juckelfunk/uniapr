// 
// Decompiled by Procyon v0.5.36
// 

package org.codehaus.plexus.component.composition;

import java.util.List;
import org.codehaus.plexus.component.repository.ComponentDescriptor;

public interface CompositionResolver
{
    void addComponentDescriptor(final ComponentDescriptor p0) throws CompositionException;
    
    List getRequirements(final String p0);
    
    List findRequirements(final String p0);
}
