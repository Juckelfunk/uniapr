// 
// Decompiled by Procyon v0.5.36
// 

package org.pitest.coverage.analysis;

import org.pitest.reloc.asm.Handle;
import org.pitest.reloc.asm.Label;
import sun.pitest.CodeCoverageStore;
import org.pitest.reloc.asm.Type;
import org.pitest.reloc.asm.MethodVisitor;
import org.pitest.mutationtest.engine.gregor.analysis.InstructionCounter;
import java.util.List;

public class ArrayProbeCoverageMethodVisitor extends AbstractCoverageStrategy
{
    private int probeHitArrayLocal;
    
    public ArrayProbeCoverageMethodVisitor(final List<Block> blocks, final InstructionCounter counter, final int classId, final MethodVisitor writer, final int access, final String name, final String desc, final int probeOffset) {
        super(blocks, counter, classId, writer, access, name, desc, probeOffset);
    }
    
    @Override
    void prepare() {
        this.probeHitArrayLocal = this.newLocal(Type.getType("[Z"));
        this.pushConstant(this.blocks.size());
        this.mv.visitIntInsn(188, 4);
        this.mv.visitVarInsn(58, this.probeHitArrayLocal);
    }
    
    @Override
    void generateProbeReportCode() {
        this.pushConstant(this.classId);
        this.pushConstant(this.probeOffset);
        this.mv.visitVarInsn(25, this.probeHitArrayLocal);
        this.methodVisitor.visitMethodInsn(184, CodeCoverageStore.CLASS_NAME, "visitProbes", "(II[Z)V", false);
    }
    
    @Override
    void insertProbe() {
        this.mv.visitVarInsn(25, this.probeHitArrayLocal);
        this.pushConstant(this.probeCount);
        this.pushConstant(1);
        this.mv.visitInsn(84);
    }
}
