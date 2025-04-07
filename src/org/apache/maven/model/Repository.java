// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.maven.model;

import java.io.Serializable;

public class Repository extends RepositoryBase implements Serializable
{
    private RepositoryPolicy releases;
    private RepositoryPolicy snapshots;
    
    public RepositoryPolicy getReleases() {
        return this.releases;
    }
    
    public RepositoryPolicy getSnapshots() {
        return this.snapshots;
    }
    
    public void setReleases(final RepositoryPolicy releases) {
        this.releases = releases;
    }
    
    public void setSnapshots(final RepositoryPolicy snapshots) {
        this.snapshots = snapshots;
    }
    
    @Override
    public boolean equals(final Object obj) {
        return super.equals(obj);
    }
}
