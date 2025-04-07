// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.maven.artifact.versioning;

public interface ArtifactVersion extends Comparable
{
    int getMajorVersion();
    
    int getMinorVersion();
    
    int getIncrementalVersion();
    
    int getBuildNumber();
    
    String getQualifier();
    
    void parseVersion(final String p0);
}
