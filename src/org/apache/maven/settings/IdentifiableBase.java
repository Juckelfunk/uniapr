// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.maven.settings;

import java.io.Serializable;

public class IdentifiableBase extends TrackableBase implements Serializable
{
    private String id;
    
    public IdentifiableBase() {
        this.id = "default";
    }
    
    public String getId() {
        return this.id;
    }
    
    public void setId(final String id) {
        this.id = id;
    }
}
