// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.maven.model;

import java.io.Serializable;

public class DistributionManagement implements Serializable
{
    private DeploymentRepository repository;
    private DeploymentRepository snapshotRepository;
    private Site site;
    private String downloadUrl;
    private Relocation relocation;
    private String status;
    
    public String getDownloadUrl() {
        return this.downloadUrl;
    }
    
    public Relocation getRelocation() {
        return this.relocation;
    }
    
    public DeploymentRepository getRepository() {
        return this.repository;
    }
    
    public Site getSite() {
        return this.site;
    }
    
    public DeploymentRepository getSnapshotRepository() {
        return this.snapshotRepository;
    }
    
    public String getStatus() {
        return this.status;
    }
    
    public void setDownloadUrl(final String downloadUrl) {
        this.downloadUrl = downloadUrl;
    }
    
    public void setRelocation(final Relocation relocation) {
        this.relocation = relocation;
    }
    
    public void setRepository(final DeploymentRepository repository) {
        this.repository = repository;
    }
    
    public void setSite(final Site site) {
        this.site = site;
    }
    
    public void setSnapshotRepository(final DeploymentRepository snapshotRepository) {
        this.snapshotRepository = snapshotRepository;
    }
    
    public void setStatus(final String status) {
        this.status = status;
    }
}
