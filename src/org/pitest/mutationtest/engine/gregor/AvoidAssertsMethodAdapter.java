// 
// Decompiled by Procyon v0.5.36
// 

package org.pitest.mutationtest.engine.gregor;

import org.pitest.reloc.asm.Label;
import org.pitest.reloc.asm.MethodVisitor;

public class AvoidAssertsMethodAdapter extends MethodVisitor
{
    private static final String DISABLE_REASON = "ASSERTS";
    private final MutationContext context;
    private boolean assertBlockStarted;
    private Label destination;
    
    public AvoidAssertsMethodAdapter(final MutationContext context, final MethodVisitor delegateMethodVisitor) {
        super(393216, delegateMethodVisitor);
        this.context = context;
    }
    
    @Override
    public void visitMethodInsn(final int opcode, final String owner, final String name, final String desc, final boolean itf) {
        if (opcode == 182 && "java/lang/Class".equals(owner) && "desiredAssertionStatus".equals(name)) {
            this.context.disableMutations("ASSERTS");
        }
        super.visitMethodInsn(opcode, owner, name, desc, itf);
    }
    
    @Override
    public void visitFieldInsn(final int opcode, final String owner, final String name, final String desc) {
        if ("$assertionsDisabled".equals(name)) {
            if (opcode == 178) {
                this.context.disableMutations("ASSERTS");
                this.assertBlockStarted = true;
            }
            else if (opcode == 179) {
                this.context.enableMutatations("ASSERTS");
            }
        }
        super.visitFieldInsn(opcode, owner, name, desc);
    }
    
    @Override
    public void visitJumpInsn(final int opcode, final Label destination) {
        if (opcode == 154 && this.assertBlockStarted) {
            this.destination = destination;
            this.assertBlockStarted = false;
        }
        super.visitJumpInsn(opcode, destination);
    }
    
    @Override
    public void visitLabel(final Label label) {
        super.visitLabel(label);
        if (this.destination == label) {
            this.context.enableMutatations("ASSERTS");
            this.destination = null;
        }
    }
}
