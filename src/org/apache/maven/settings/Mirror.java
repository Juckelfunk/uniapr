// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.maven.settings;

import java.io.Serializable;

public class Mirror extends IdentifiableBase implements Serializable
{
    private String mirrorOf;
    private String name;
    private String url;
    
    public String getMirrorOf() {
        return this.mirrorOf;
    }
    
    public String getName() {
        return this.name;
    }
    
    public String getUrl() {
        return this.url;
    }
    
    public void setMirrorOf(final String mirrorOf) {
        this.mirrorOf = mirrorOf;
    }
    
    public void setName(final String name) {
        this.name = name;
    }
    
    public void setUrl(final String url) {
        this.url = url;
    }
}
