// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.maven.model;

import java.io.Serializable;

public class Resource extends FileSet implements Serializable
{
    private String targetPath;
    private boolean filtering;
    private String mergeId;
    private static int mergeIdCounter;
    
    public Resource() {
        this.filtering = false;
    }
    
    public String getMergeId() {
        return this.mergeId;
    }
    
    public String getTargetPath() {
        return this.targetPath;
    }
    
    public boolean isFiltering() {
        return this.filtering;
    }
    
    public void setFiltering(final boolean filtering) {
        this.filtering = filtering;
    }
    
    public void setMergeId(final String mergeId) {
        this.mergeId = mergeId;
    }
    
    public void setTargetPath(final String targetPath) {
        this.targetPath = targetPath;
    }
    
    public void initMergeId() {
        if (this.getMergeId() == null) {
            this.setMergeId("resource-" + Resource.mergeIdCounter++);
        }
    }
    
    @Override
    public String toString() {
        return "Resource {targetPath: " + this.getTargetPath() + ", filtering: " + this.isFiltering() + ", " + super.toString() + "}";
    }
    
    static {
        Resource.mergeIdCounter = 0;
    }
}
