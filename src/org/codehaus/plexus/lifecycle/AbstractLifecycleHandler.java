// 
// Decompiled by Procyon v0.5.36
// 

package org.codehaus.plexus.lifecycle;

import org.codehaus.plexus.personality.plexus.lifecycle.phase.PhaseExecutionException;
import java.util.Iterator;
import org.codehaus.plexus.lifecycle.phase.Phase;
import org.codehaus.plexus.component.manager.ComponentManager;
import java.util.ArrayList;
import java.util.List;

public abstract class AbstractLifecycleHandler implements LifecycleHandler
{
    private String id;
    private String name;
    private List beginSegment;
    private List suspendSegment;
    private List resumeSegment;
    private List endSegment;
    
    public AbstractLifecycleHandler() {
        this.id = null;
        this.name = null;
        this.beginSegment = new ArrayList();
        this.suspendSegment = new ArrayList();
        this.resumeSegment = new ArrayList();
        this.endSegment = new ArrayList();
    }
    
    public String getId() {
        return this.id;
    }
    
    public String getName() {
        return this.name;
    }
    
    public List getBeginSegment() {
        return this.beginSegment;
    }
    
    public List getSuspendSegment() {
        return this.suspendSegment;
    }
    
    public List getResumeSegment() {
        return this.resumeSegment;
    }
    
    public List getEndSegment() {
        return this.endSegment;
    }
    
    public void start(final Object component, final ComponentManager manager) throws PhaseExecutionException {
        if (this.segmentIsEmpty(this.getBeginSegment())) {
            return;
        }
        for (final Phase phase : this.getBeginSegment()) {
            phase.execute(component, manager);
        }
    }
    
    public void suspend(final Object component, final ComponentManager manager) throws PhaseExecutionException {
        if (this.segmentIsEmpty(this.getSuspendSegment())) {
            return;
        }
        for (final Phase phase : this.getSuspendSegment()) {
            phase.execute(component, manager);
        }
    }
    
    public void resume(final Object component, final ComponentManager manager) throws PhaseExecutionException {
        if (this.segmentIsEmpty(this.getResumeSegment())) {
            return;
        }
        for (final Phase phase : this.getResumeSegment()) {
            phase.execute(component, manager);
        }
    }
    
    public void end(final Object component, final ComponentManager manager) throws PhaseExecutionException {
        if (this.segmentIsEmpty(this.getEndSegment())) {
            return;
        }
        for (final Phase phase : this.getEndSegment()) {
            phase.execute(component, manager);
        }
    }
    
    private boolean segmentIsEmpty(final List segment) {
        return segment == null || segment.size() == 0;
    }
}
