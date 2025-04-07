// 
// Decompiled by Procyon v0.5.36
// 

package org.pitest.coverage.analysis;

import java.util.Iterator;
import sun.pitest.CodeCoverageStore;
import org.pitest.reloc.asm.Label;
import org.pitest.reloc.asm.Handle;
import java.util.List;
import org.pitest.mutationtest.engine.gregor.analysis.InstructionCounter;
import org.pitest.reloc.asm.MethodVisitor;

public class SimpleBlockCoverageVisitor extends MethodVisitor
{
    private final MethodVisitor methodVisitor;
    private final int classId;
    private final int probeOffset;
    private final InstructionCounter counter;
    private final List<Block> blocks;
    private int probeCount;
    
    public SimpleBlockCoverageVisitor(final List<Block> blocks, final InstructionCounter counter, final int classId, final MethodVisitor writer, final int access, final String name, final String desc, final int probeOffset) {
        super(393216, writer);
        this.probeCount = 0;
        this.counter = counter;
        this.methodVisitor = writer;
        this.classId = classId;
        this.blocks = blocks;
        this.probeOffset = probeOffset;
    }
    
    @Override
    public void visitFrame(final int type, final int nLocal, final Object[] local, final int nStack, final Object[] stack) {
        this.insertProbeIfAppropriate();
        super.visitFrame(type, nLocal, local, nStack, stack);
    }
    
    @Override
    public void visitInsn(final int opcode) {
        this.insertProbeIfAppropriate();
        super.visitInsn(opcode);
    }
    
    @Override
    public void visitIntInsn(final int opcode, final int operand) {
        this.insertProbeIfAppropriate();
        super.visitIntInsn(opcode, operand);
    }
    
    @Override
    public void visitVarInsn(final int opcode, final int var) {
        this.insertProbeIfAppropriate();
        super.visitVarInsn(opcode, var);
    }
    
    @Override
    public void visitTypeInsn(final int opcode, final String type) {
        this.insertProbeIfAppropriate();
        super.visitTypeInsn(opcode, type);
    }
    
    @Override
    public void visitFieldInsn(final int opcode, final String owner, final String name, final String desc) {
        this.insertProbeIfAppropriate();
        super.visitFieldInsn(opcode, owner, name, desc);
    }
    
    @Override
    public void visitMethodInsn(final int opcode, final String owner, final String name, final String desc, final boolean itf) {
        this.insertProbeIfAppropriate();
        super.visitMethodInsn(opcode, owner, name, desc, itf);
    }
    
    @Override
    public void visitInvokeDynamicInsn(final String name, final String desc, final Handle bsm, final Object... bsmArgs) {
        this.insertProbeIfAppropriate();
        super.visitInvokeDynamicInsn(name, desc, bsm, bsmArgs);
    }
    
    @Override
    public void visitJumpInsn(final int opcode, final Label label) {
        this.insertProbeIfAppropriate();
        super.visitJumpInsn(opcode, label);
    }
    
    @Override
    public void visitLabel(final Label label) {
        super.visitLabel(label);
        this.insertProbeIfAppropriate();
    }
    
    @Override
    public void visitLdcInsn(final Object cst) {
        this.insertProbeIfAppropriate();
        super.visitLdcInsn(cst);
    }
    
    @Override
    public void visitIincInsn(final int var, final int increment) {
        this.insertProbeIfAppropriate();
        super.visitIincInsn(var, increment);
    }
    
    @Override
    public void visitTableSwitchInsn(final int min, final int max, final Label dflt, final Label... labels) {
        this.insertProbeIfAppropriate();
        super.visitTableSwitchInsn(min, max, dflt, labels);
    }
    
    @Override
    public void visitLookupSwitchInsn(final Label dflt, final int[] keys, final Label[] labels) {
        this.insertProbeIfAppropriate();
        super.visitLookupSwitchInsn(dflt, keys, labels);
    }
    
    @Override
    public void visitMultiANewArrayInsn(final String desc, final int dims) {
        this.insertProbeIfAppropriate();
        super.visitMultiANewArrayInsn(desc, dims);
    }
    
    @Override
    public void visitLineNumber(final int line, final Label start) {
        this.insertProbeIfAppropriate();
        super.visitLineNumber(line, start);
    }
    
    private void insertProbeIfAppropriate() {
        if (this.needsProbe(this.counter.currentInstructionCount())) {
            this.methodVisitor.visitLdcInsn(this.classId);
            this.methodVisitor.visitLdcInsn(this.probeCount + this.probeOffset);
            this.methodVisitor.visitMethodInsn(184, CodeCoverageStore.CLASS_NAME, "visitSingleProbe", "(II)V", false);
            ++this.probeCount;
        }
    }
    
    private boolean needsProbe(final int currentInstructionCount) {
        for (final Block each : this.blocks) {
            if (each.firstInstructionIs(currentInstructionCount - 1)) {
                return true;
            }
        }
        return false;
    }
}
