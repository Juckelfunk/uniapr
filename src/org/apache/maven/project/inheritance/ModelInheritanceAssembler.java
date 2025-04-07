// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.maven.project.inheritance;

import org.apache.maven.model.Build;
import org.apache.maven.model.Model;

public interface ModelInheritanceAssembler
{
    public static final String ROLE = ModelInheritanceAssembler.class.getName();
    
    void assembleModelInheritance(final Model p0, final Model p1, final String p2);
    
    void assembleModelInheritance(final Model p0, final Model p1);
    
    void assembleBuildInheritance(final Build p0, final Build p1, final boolean p2);
    
    void copyModel(final Model p0, final Model p1);
}
