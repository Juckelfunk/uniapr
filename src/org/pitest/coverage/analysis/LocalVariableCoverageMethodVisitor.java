// 
// Decompiled by Procyon v0.5.36
// 

package org.pitest.coverage.analysis;

import sun.pitest.CodeCoverageStore;
import org.pitest.reloc.asm.Type;
import org.pitest.reloc.asm.MethodVisitor;
import org.pitest.mutationtest.engine.gregor.analysis.InstructionCounter;
import java.util.List;

class LocalVariableCoverageMethodVisitor extends AbstractCoverageStrategy
{
    private int[] locals;
    
    LocalVariableCoverageMethodVisitor(final List<Block> blocks, final InstructionCounter counter, final int classId, final MethodVisitor writer, final int access, final String name, final String desc, final int probeOffset) {
        super(blocks, counter, classId, writer, access, name, desc, probeOffset);
    }
    
    @Override
    void prepare() {
        this.locals = new int[this.blocks.size()];
        for (int i = 0; i != this.blocks.size(); ++i) {
            this.locals[i] = this.newLocal(Type.getType("Z"));
            this.pushConstant(0);
            this.mv.visitVarInsn(54, this.locals[i]);
        }
    }
    
    @Override
    void insertProbe() {
        this.pushConstant(1);
        this.mv.visitVarInsn(54, this.locals[this.probeCount]);
    }
    
    protected void generateProbeReportCode() {
        this.pushConstant(this.classId);
        this.pushConstant(this.probeOffset);
        for (final int i : this.locals) {
            this.mv.visitVarInsn(21, i);
        }
        this.methodVisitor.visitMethodInsn(184, CodeCoverageStore.CLASS_NAME, "visitProbes", "(II" + String.format(String.format("%%0%dd", this.blocks.size()), 0).replace("0", "Z") + ")V", false);
    }
}
