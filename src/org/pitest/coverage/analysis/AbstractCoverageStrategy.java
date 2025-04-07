// 
// Decompiled by Procyon v0.5.36
// 

package org.pitest.coverage.analysis;

import java.util.Iterator;
import org.pitest.reloc.asm.Handle;
import org.pitest.reloc.asm.Label;
import org.pitest.mutationtest.engine.gregor.analysis.InstructionCounter;
import java.util.List;
import org.pitest.reloc.asm.MethodVisitor;
import org.pitest.reloc.asm.commons.AdviceAdapter;

abstract class AbstractCoverageStrategy extends AdviceAdapter
{
    protected final MethodVisitor methodVisitor;
    protected final int classId;
    protected final int probeOffset;
    protected final List<Block> blocks;
    private final InstructionCounter counter;
    private final Label before;
    private final Label handler;
    protected int probeCount;
    
    AbstractCoverageStrategy(final List<Block> blocks, final InstructionCounter counter, final int classId, final MethodVisitor writer, final int access, final String name, final String desc, final int probeOffset) {
        super(393216, writer, access, name, desc);
        this.before = new Label();
        this.handler = new Label();
        this.probeCount = 0;
        this.methodVisitor = writer;
        this.classId = classId;
        this.counter = counter;
        this.blocks = blocks;
        this.probeOffset = probeOffset;
    }
    
    abstract void prepare();
    
    abstract void generateProbeReportCode();
    
    abstract void insertProbe();
    
    @Override
    public void visitCode() {
        super.visitCode();
        this.prepare();
        this.mv.visitLabel(this.before);
    }
    
    @Override
    public void visitMaxs(final int maxStack, final int maxLocals) {
        this.mv.visitTryCatchBlock(this.before, this.handler, this.handler, null);
        this.mv.visitLabel(this.handler);
        this.generateProbeReportCode();
        this.mv.visitInsn(191);
        this.mv.visitMaxs(maxStack, this.nextLocal);
    }
    
    @Override
    protected void onMethodExit(final int opcode) {
        if (opcode != 191) {
            this.generateProbeReportCode();
        }
    }
    
    protected void pushConstant(final int value) {
        switch (value) {
            case 0: {
                this.mv.visitInsn(3);
                break;
            }
            case 1: {
                this.mv.visitInsn(4);
                break;
            }
            case 2: {
                this.mv.visitInsn(5);
                break;
            }
            case 3: {
                this.mv.visitInsn(6);
                break;
            }
            case 4: {
                this.mv.visitInsn(7);
                break;
            }
            case 5: {
                this.mv.visitInsn(8);
                break;
            }
            default: {
                if (value <= 127) {
                    this.mv.visitIntInsn(16, value);
                    break;
                }
                if (value <= 32767) {
                    this.mv.visitIntInsn(17, value);
                    break;
                }
                this.mv.visitLdcInsn(value);
                break;
            }
        }
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
            this.insertProbe();
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
