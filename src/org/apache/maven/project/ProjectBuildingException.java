// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.maven.project;

public class ProjectBuildingException extends Exception
{
    private final String projectId;
    
    public ProjectBuildingException(final String projectId, final String message) {
        super(message);
        this.projectId = projectId;
    }
    
    public ProjectBuildingException(final String projectId, final String message, final Throwable cause) {
        super(message, cause);
        this.projectId = projectId;
    }
    
    public String getProjectId() {
        return this.projectId;
    }
    
    @Override
    public String getMessage() {
        return super.getMessage() + " for project " + this.projectId;
    }
}
