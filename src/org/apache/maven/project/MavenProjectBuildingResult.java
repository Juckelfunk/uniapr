// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.maven.project;

import org.apache.maven.project.validation.ModelValidationResult;

public class MavenProjectBuildingResult
{
    private MavenProject project;
    private ModelValidationResult modelValidationResult;
    private boolean successful;
    
    public MavenProjectBuildingResult(final MavenProject project) {
        this.project = project;
        this.successful = true;
    }
    
    public MavenProjectBuildingResult(final ModelValidationResult modelValidationResult) {
        this.modelValidationResult = modelValidationResult;
        this.successful = (modelValidationResult.getMessageCount() == 0);
    }
    
    public ModelValidationResult getModelValidationResult() {
        return this.modelValidationResult;
    }
    
    public MavenProject getProject() {
        return this.project;
    }
    
    public boolean isSuccessful() {
        return this.successful;
    }
}
