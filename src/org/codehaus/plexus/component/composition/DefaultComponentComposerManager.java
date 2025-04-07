// 
// Decompiled by Procyon v0.5.36
// 

package org.codehaus.plexus.component.composition;

import java.util.Iterator;
import org.codehaus.plexus.PlexusContainer;
import org.codehaus.plexus.component.repository.ComponentDescriptor;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class DefaultComponentComposerManager implements ComponentComposerManager
{
    private Map composerMap;
    private List componentComposers;
    private String defaultComponentComposerId;
    
    public DefaultComponentComposerManager() {
        this.composerMap = new HashMap();
        this.defaultComponentComposerId = "field";
    }
    
    public void assembleComponent(final Object component, final ComponentDescriptor componentDescriptor, final PlexusContainer container) throws UndefinedComponentComposerException, CompositionException {
        if (componentDescriptor.getRequirements().size() == 0) {
            return;
        }
        String componentComposerId = componentDescriptor.getComponentComposer();
        if (componentComposerId == null || componentComposerId.trim().length() == 0) {
            componentComposerId = this.defaultComponentComposerId;
        }
        final ComponentComposer componentComposer = this.getComponentComposer(componentComposerId);
        componentComposer.assembleComponent(component, componentDescriptor, container);
    }
    
    protected ComponentComposer getComponentComposer(final String id) throws UndefinedComponentComposerException {
        ComponentComposer retValue = null;
        if (this.composerMap.containsKey(id)) {
            retValue = this.composerMap.get(id);
        }
        else {
            retValue = this.findComponentComposer(id);
        }
        if (retValue == null) {
            throw new UndefinedComponentComposerException("Specified component composer cannot be found: " + id);
        }
        return retValue;
    }
    
    private ComponentComposer findComponentComposer(final String id) {
        ComponentComposer retValue = null;
        for (final ComponentComposer componentComposer : this.componentComposers) {
            if (componentComposer.getId().equals(id)) {
                retValue = componentComposer;
                break;
            }
        }
        return retValue;
    }
}
