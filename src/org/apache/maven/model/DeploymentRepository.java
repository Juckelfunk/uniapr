// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.maven.model;

import java.io.Serializable;

public class DeploymentRepository extends RepositoryBase implements Serializable
{
    private boolean uniqueVersion;
    
    public DeploymentRepository() {
        this.uniqueVersion = true;
    }
    
    public boolean isUniqueVersion() {
        return this.uniqueVersion;
    }
    
    public void setUniqueVersion(final boolean uniqueVersion) {
        this.uniqueVersion = uniqueVersion;
    }
    
    @Override
    public boolean equals(final Object obj) {
        return super.equals(obj);
    }
}
