// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.maven.plugin.registry;

import java.io.Serializable;

public class TrackableBase implements Serializable
{
    public static final String USER_LEVEL = "user-level";
    public static final String GLOBAL_LEVEL = "global-level";
    private String sourceLevel;
    private boolean sourceLevelSet;
    
    public TrackableBase() {
        this.sourceLevel = "user-level";
        this.sourceLevelSet = false;
    }
    
    public void setSourceLevel(final String sourceLevel) {
        if (this.sourceLevelSet) {
            throw new IllegalStateException("Cannot reset sourceLevel attribute; it is already set to: " + sourceLevel);
        }
        if (!"user-level".equals(sourceLevel) && !"global-level".equals(sourceLevel)) {
            throw new IllegalArgumentException("sourceLevel must be one of: {user-level,global-level}");
        }
        this.sourceLevel = sourceLevel;
        this.sourceLevelSet = true;
    }
    
    public String getSourceLevel() {
        return this.sourceLevel;
    }
}
