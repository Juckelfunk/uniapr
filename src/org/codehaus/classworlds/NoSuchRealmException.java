// 
// Decompiled by Procyon v0.5.36
// 

package org.codehaus.classworlds;

public class NoSuchRealmException extends ClassWorldException
{
    private String id;
    
    public NoSuchRealmException(final ClassWorld world, final String id) {
        super(world, id);
        this.id = id;
    }
    
    public String getId() {
        return this.id;
    }
}
