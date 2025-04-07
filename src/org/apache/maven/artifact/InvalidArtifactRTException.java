// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.maven.artifact;

public class InvalidArtifactRTException extends RuntimeException
{
    private final String groupId;
    private final String artifactId;
    private final String version;
    private final String type;
    private final String baseMessage;
    
    public InvalidArtifactRTException(final String groupId, final String artifactId, final String version, final String type, final String message) {
        this.groupId = groupId;
        this.artifactId = artifactId;
        this.version = version;
        this.type = type;
        this.baseMessage = message;
    }
    
    public InvalidArtifactRTException(final String groupId, final String artifactId, final String version, final String type, final String message, final Throwable cause) {
        super(cause);
        this.groupId = groupId;
        this.artifactId = artifactId;
        this.version = version;
        this.type = type;
        this.baseMessage = message;
    }
    
    @Override
    public String getMessage() {
        return "For artifact {" + this.getArtifactKey() + "}: " + this.getBaseMessage();
    }
    
    public String getBaseMessage() {
        return this.baseMessage;
    }
    
    public String getArtifactId() {
        return this.artifactId;
    }
    
    public String getGroupId() {
        return this.groupId;
    }
    
    public String getType() {
        return this.type;
    }
    
    public String getVersion() {
        return this.version;
    }
    
    public String getArtifactKey() {
        return this.groupId + ":" + this.artifactId + ":" + this.version + ":" + this.type;
    }
}
