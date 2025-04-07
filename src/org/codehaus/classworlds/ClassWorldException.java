// 
// Decompiled by Procyon v0.5.36
// 

package org.codehaus.classworlds;

public class ClassWorldException extends Exception
{
    private ClassWorld world;
    
    public ClassWorldException(final ClassWorld world) {
        this.world = world;
    }
    
    public ClassWorldException(final ClassWorld world, final String msg) {
        super(msg);
        this.world = world;
    }
    
    public ClassWorld getWorld() {
        return this.world;
    }
}
