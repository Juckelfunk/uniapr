// 
// Decompiled by Procyon v0.5.36
// 

package org.uniapr.jvm.core;

import org.objectweb.asm.Opcodes;
import org.objectweb.asm.ClassVisitor;

public class CheckPraPRGenVisitor extends ClassVisitor implements Opcodes
{
    public boolean isPraPRGen;
    
    public CheckPraPRGenVisitor() {
        super(327680);
        this.isPraPRGen = false;
    }
    
    @Override
    public void visitSource(final String source, final String debug) {
        if (source.startsWith("prapr_gen")) {
            this.isPraPRGen = true;
        }
    }
}
