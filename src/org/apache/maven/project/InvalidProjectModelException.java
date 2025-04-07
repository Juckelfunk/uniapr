// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.maven.project;

import org.apache.maven.project.validation.ModelValidationResult;

public class InvalidProjectModelException extends ProjectBuildingException
{
    private final String pomLocation;
    private ModelValidationResult validationResult;
    
    public InvalidProjectModelException(final String projectId, final String pomLocation, final String message, final Throwable cause) {
        super(projectId, message, cause);
        this.pomLocation = pomLocation;
    }
    
    public InvalidProjectModelException(final String projectId, final String pomLocation, final String message, final ModelValidationResult validationResult) {
        super(projectId, message);
        this.pomLocation = pomLocation;
        this.validationResult = validationResult;
    }
    
    public InvalidProjectModelException(final String projectId, final String pomLocation, final String message) {
        super(projectId, message);
        this.pomLocation = pomLocation;
    }
    
    public final String getPomLocation() {
        return this.pomLocation;
    }
    
    public final ModelValidationResult getValidationResult() {
        return this.validationResult;
    }
    
    @Override
    public String getMessage() {
        return super.getMessage() + " at " + this.pomLocation;
    }
}
