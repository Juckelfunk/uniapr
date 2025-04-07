// 
// Decompiled by Procyon v0.5.36
// 

package org.pitest.reloc.asm.tree;

import org.pitest.reloc.asm.MethodVisitor;

public class ParameterNode
{
    public String name;
    public int access;
    
    public ParameterNode(final String name, final int access) {
        this.name = name;
        this.access = access;
    }
    
    public void accept(final MethodVisitor mv) {
        mv.visitParameter(this.name, this.access);
    }
}
