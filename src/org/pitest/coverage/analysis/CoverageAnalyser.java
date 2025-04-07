// 
// Decompiled by Procyon v0.5.36
// 

package org.pitest.coverage.analysis;

import java.util.List;
import org.pitest.mutationtest.engine.gregor.analysis.InstructionTrackingMethodVisitor;
import org.pitest.mutationtest.engine.gregor.analysis.InstructionCounter;
import org.pitest.mutationtest.engine.gregor.analysis.DefaultInstructionCounter;
import sun.pitest.CodeCoverageStore;
import org.pitest.reloc.asm.MethodVisitor;
import org.pitest.coverage.CoverageClassVisitor;
import org.pitest.reloc.asm.tree.MethodNode;

public class CoverageAnalyser extends MethodNode
{
    private static final int MAX_SUPPORTED_LOCAL_PROBES = 15;
    private final CoverageClassVisitor parent;
    private final int classId;
    private final MethodVisitor mv;
    private final int probeOffset;
    
    public CoverageAnalyser(final CoverageClassVisitor parent, final int classId, final int probeOffset, final MethodVisitor mv, final int access, final String name, final String desc, final String signature, final String[] exceptions) {
        super(393216, access, name, desc, signature, exceptions);
        this.mv = mv;
        this.parent = parent;
        this.classId = classId;
        this.probeOffset = probeOffset;
    }
    
    @Override
    public void visitEnd() {
        final List<Block> blocks = this.findRequriedProbeLocations();
        this.parent.registerProbes(blocks.size());
        final int blockCount = blocks.size();
        CodeCoverageStore.registerMethod(this.classId, this.name, this.desc, this.probeOffset, this.probeOffset + blocks.size() - 1);
        final DefaultInstructionCounter counter = new DefaultInstructionCounter();
        if (blockCount == 1 || this.name.equals("<init>")) {
            this.accept(new InstructionTrackingMethodVisitor(new SimpleBlockCoverageVisitor(blocks, counter, this.classId, this.mv, this.access, this.name, this.desc, this.probeOffset), counter));
        }
        else if (blockCount <= 15 && blockCount >= 1) {
            this.accept(new InstructionTrackingMethodVisitor(new LocalVariableCoverageMethodVisitor(blocks, counter, this.classId, this.mv, this.access, this.name, this.desc, this.probeOffset), counter));
        }
        else {
            this.accept(new InstructionTrackingMethodVisitor(new ArrayProbeCoverageMethodVisitor(blocks, counter, this.classId, this.mv, this.access, this.name, this.desc, this.probeOffset), counter));
        }
    }
    
    private List<Block> findRequriedProbeLocations() {
        return ControlFlowAnalyser.analyze(this);
    }
}
