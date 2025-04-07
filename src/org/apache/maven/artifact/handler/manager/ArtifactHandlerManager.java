// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.maven.artifact.handler.manager;

import java.util.Map;
import org.apache.maven.artifact.handler.ArtifactHandler;

public interface ArtifactHandlerManager
{
    public static final String ROLE = ArtifactHandlerManager.class.getName();
    
    ArtifactHandler getArtifactHandler(final String p0);
    
    void addHandlers(final Map p0);
}
