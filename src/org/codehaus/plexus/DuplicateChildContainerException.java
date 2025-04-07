// 
// Decompiled by Procyon v0.5.36
// 

package org.codehaus.plexus;

public class DuplicateChildContainerException extends PlexusContainerException
{
    private final String parent;
    private final String child;
    
    public DuplicateChildContainerException(final String parent, final String child) {
        super("Cannot create child container, because child named '" + child + "' already exists in parent '" + parent + "'.");
        this.parent = parent;
        this.child = child;
    }
    
    public String getParent() {
        return this.parent;
    }
    
    public String getChild() {
        return this.child;
    }
}
