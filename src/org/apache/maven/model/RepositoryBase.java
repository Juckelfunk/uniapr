// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.maven.model;

import java.io.Serializable;

public class RepositoryBase implements Serializable
{
    private String id;
    private String name;
    private String url;
    private String layout;
    
    public RepositoryBase() {
        this.layout = "default";
    }
    
    public String getId() {
        return this.id;
    }
    
    public String getLayout() {
        return this.layout;
    }
    
    public String getName() {
        return this.name;
    }
    
    public String getUrl() {
        return this.url;
    }
    
    public void setId(final String id) {
        this.id = id;
    }
    
    public void setLayout(final String layout) {
        this.layout = layout;
    }
    
    public void setName(final String name) {
        this.name = name;
    }
    
    public void setUrl(final String url) {
        this.url = url;
    }
    
    @Override
    public boolean equals(final Object obj) {
        if (!(obj instanceof RepositoryBase)) {
            return false;
        }
        final RepositoryBase other = (RepositoryBase)obj;
        if (this.id != null) {
            return this.id.equals(other.id);
        }
        return super.equals(obj);
    }
}
