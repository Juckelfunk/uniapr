// 
// Decompiled by Procyon v0.5.36
// 

package org.pitest.mutationtest.engine.gregor;

import org.pitest.reloc.asm.MethodVisitor;

public class InsnSubstitution implements ZeroOperandMutation
{
    private final int replacementOpcode;
    private final String message;
    
    public InsnSubstitution(final int replacementOpcode, final String message) {
        this.replacementOpcode = replacementOpcode;
        this.message = message;
    }
    
    @Override
    public void apply(final int opCode, final MethodVisitor mv) {
        mv.visitInsn(this.replacementOpcode);
    }
    
    @Override
    public String decribe(final int opCode, final MethodInfo methodInfo) {
        return this.message;
    }
}
