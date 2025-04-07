// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.maven.wagon.repository;

public class RepositoryPermissions
{
    private String group;
    private String directoryMode;
    private String fileMode;
    
    public String getDirectoryMode() {
        return this.directoryMode;
    }
    
    public void setDirectoryMode(final String directoryMode) {
        this.directoryMode = directoryMode;
    }
    
    public String getFileMode() {
        return this.fileMode;
    }
    
    public void setFileMode(final String fileMode) {
        this.fileMode = fileMode;
    }
    
    public String getGroup() {
        return this.group;
    }
    
    public void setGroup(final String group) {
        this.group = group;
    }
}
