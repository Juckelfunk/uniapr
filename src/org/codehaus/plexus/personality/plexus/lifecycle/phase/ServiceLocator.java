// 
// Decompiled by Procyon v0.5.36
// 

package org.codehaus.plexus.personality.plexus.lifecycle.phase;

import org.codehaus.plexus.component.repository.exception.ComponentLifecycleException;
import java.util.List;
import java.util.Map;
import org.codehaus.plexus.component.repository.exception.ComponentLookupException;

public interface ServiceLocator
{
    Object lookup(final String p0) throws ComponentLookupException;
    
    Object lookup(final String p0, final String p1) throws ComponentLookupException;
    
    Map lookupMap(final String p0) throws ComponentLookupException;
    
    List lookupList(final String p0) throws ComponentLookupException;
    
    void release(final Object p0) throws ComponentLifecycleException;
    
    void releaseAll(final Map p0) throws ComponentLifecycleException;
    
    void releaseAll(final List p0) throws ComponentLifecycleException;
    
    boolean hasComponent(final String p0);
    
    boolean hasComponent(final String p0, final String p1);
}
