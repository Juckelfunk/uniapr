// 
// Decompiled by Procyon v0.5.36
// 

package org.pitest.mutationtest.engine.gregor;

import org.pitest.reloc.asm.Label;
import org.pitest.reloc.asm.MethodVisitor;

public class AvoidStringSwitchedMethodAdapter extends MethodVisitor
{
    private static final String DISABLE_REASON = "STRING_SWITCH";
    private final MutationContext context;
    private boolean hashCodeWasLastCall;
    private Label end;
    
    public AvoidStringSwitchedMethodAdapter(final MutationContext context, final MethodVisitor delegateMethodVisitor) {
        super(393216, delegateMethodVisitor);
        this.context = context;
    }
    
    @Override
    public void visitMethodInsn(final int opcode, final String owner, final String name, final String desc, final boolean itf) {
        if (opcode == 182 && "java/lang/String".equals(owner) && "hashCode".equals(name)) {
            this.hashCodeWasLastCall = true;
        }
        else {
            this.hashCodeWasLastCall = false;
        }
        super.visitMethodInsn(opcode, owner, name, desc, itf);
    }
    
    @Override
    public void visitLineNumber(final int line, final Label start) {
        super.visitLineNumber(line, start);
        this.enableMutation();
    }
    
    @Override
    public void visitTableSwitchInsn(final int min, final int max, final Label dflt, final Label... labels) {
        super.visitTableSwitchInsn(min, max, dflt, labels);
        if (this.hashCodeWasLastCall) {
            this.context.disableMutations("STRING_SWITCH");
            this.end = dflt;
        }
    }
    
    @Override
    public void visitLookupSwitchInsn(final Label dflt, final int[] keys, final Label[] labels) {
        super.visitLookupSwitchInsn(dflt, keys, labels);
        if (this.hashCodeWasLastCall) {
            this.context.disableMutations("STRING_SWITCH");
            this.end = dflt;
        }
    }
    
    @Override
    public void visitLabel(final Label label) {
        super.visitLabel(label);
        if (this.end == label) {
            this.enableMutation();
        }
    }
    
    @Override
    public void visitJumpInsn(final int opcode, final Label label) {
        this.hashCodeWasLastCall = false;
        super.visitJumpInsn(opcode, label);
    }
    
    @Override
    public void visitIntInsn(final int opcode, final int operand) {
        this.hashCodeWasLastCall = false;
        super.visitIntInsn(opcode, operand);
    }
    
    @Override
    public void visitInsn(final int opcode) {
        this.hashCodeWasLastCall = false;
        super.visitInsn(opcode);
    }
    
    @Override
    public void visitEnd() {
        super.visitEnd();
        this.enableMutation();
    }
    
    private void enableMutation() {
        this.context.enableMutatations("STRING_SWITCH");
        this.end = null;
        this.hashCodeWasLastCall = false;
    }
}
