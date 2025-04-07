// 
// Decompiled by Procyon v0.5.36
// 

package org.codehaus.plexus.component.composition;

import java.util.Iterator;
import java.util.List;
import org.codehaus.plexus.util.dag.CycleDetectedException;
import org.codehaus.plexus.component.repository.ComponentRequirement;
import org.codehaus.plexus.component.repository.ComponentDescriptor;
import org.codehaus.plexus.util.dag.DAG;

public class DefaultCompositionResolver implements CompositionResolver
{
    private DAG dag;
    
    public DefaultCompositionResolver() {
        this.dag = new DAG();
    }
    
    public void addComponentDescriptor(final ComponentDescriptor componentDescriptor) throws CompositionException {
        final String componentKey = componentDescriptor.getComponentKey();
        final List requirements = componentDescriptor.getRequirements();
        for (final ComponentRequirement requirement : requirements) {
            try {
                this.dag.addEdge(componentKey, requirement.getRole());
            }
            catch (CycleDetectedException e) {
                throw new CompositionException("Cyclic requirement detected", e);
            }
        }
    }
    
    public List getRequirements(final String componentKey) {
        return this.dag.getChildLabels(componentKey);
    }
    
    public List findRequirements(final String componentKey) {
        return this.dag.getParentLabels(componentKey);
    }
}
