// 
// Decompiled by Procyon v0.5.36
// 

package org.pitest.mutationtest.engine.gregor.blocks;

import java.util.HashSet;
import org.pitest.reloc.asm.Label;
import java.util.Set;
import org.pitest.reloc.asm.MethodVisitor;

public class BlockTrackingMethodDecorator extends MethodVisitor
{
    private final BlockCounter blockCounter;
    private final Set<Label> handlers;
    
    public BlockTrackingMethodDecorator(final BlockCounter blockCounter, final MethodVisitor mv) {
        super(393216, mv);
        this.handlers = new HashSet<Label>();
        this.blockCounter = blockCounter;
    }
    
    @Override
    public void visitInsn(final int opcode) {
        this.mv.visitInsn(opcode);
        if (this.endsBlock(opcode)) {
            this.blockCounter.registerFinallyBlockEnd();
            this.blockCounter.registerNewBlock();
        }
    }
    
    @Override
    public void visitJumpInsn(final int arg0, final Label arg1) {
        this.mv.visitJumpInsn(arg0, arg1);
        this.blockCounter.registerNewBlock();
    }
    
    @Override
    public void visitTryCatchBlock(final Label start, final Label end, final Label handler, final String type) {
        super.visitTryCatchBlock(start, end, handler, type);
        if (type == null) {
            this.handlers.add(handler);
        }
    }
    
    @Override
    public void visitLabel(final Label label) {
        super.visitLabel(label);
        if (this.handlers.contains(label)) {
            this.blockCounter.registerFinallyBlockStart();
        }
    }
    
    private boolean endsBlock(final int opcode) {
        switch (opcode) {
            case 172:
            case 173:
            case 174:
            case 175:
            case 176:
            case 177:
            case 191: {
                return true;
            }
            default: {
                return false;
            }
        }
    }
}
