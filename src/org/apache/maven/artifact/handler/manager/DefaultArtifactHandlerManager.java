// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.maven.artifact.handler.manager;

import java.util.Set;
import org.apache.maven.artifact.handler.DefaultArtifactHandler;
import org.apache.maven.artifact.handler.ArtifactHandler;
import java.util.Map;

public class DefaultArtifactHandlerManager implements ArtifactHandlerManager
{
    private Map artifactHandlers;
    
    public ArtifactHandler getArtifactHandler(final String type) {
        ArtifactHandler handler = this.artifactHandlers.get(type);
        if (handler == null) {
            handler = new DefaultArtifactHandler(type);
        }
        return handler;
    }
    
    public void addHandlers(final Map handlers) {
        this.artifactHandlers.putAll(handlers);
    }
    
    public Set getHandlerTypes() {
        return this.artifactHandlers.keySet();
    }
}
