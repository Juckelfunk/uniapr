// 
// Decompiled by Procyon v0.5.36
// 

package org.pitest.coverage;

import sun.pitest.CodeCoverageStore;
import org.pitest.coverage.analysis.CoverageAnalyser;
import org.pitest.reloc.asm.MethodVisitor;
import org.pitest.functional.F5;
import org.pitest.reloc.asm.ClassVisitor;
import org.pitest.classinfo.BridgeMethodFilter;
import org.pitest.reloc.asm.ClassWriter;
import org.pitest.classinfo.MethodFilteringAdapter;

public class CoverageClassVisitor extends MethodFilteringAdapter
{
    private final int classId;
    private int probeCount;
    
    public CoverageClassVisitor(final int classId, final ClassWriter writer) {
        super(writer, BridgeMethodFilter.INSTANCE);
        this.probeCount = 0;
        this.classId = classId;
    }
    
    public void registerProbes(final int number) {
        this.probeCount += number;
    }
    
    @Override
    public MethodVisitor visitMethodIfRequired(final int access, final String name, final String desc, final String signature, final String[] exceptions, final MethodVisitor methodVisitor) {
        return new CoverageAnalyser(this, this.classId, this.probeCount, methodVisitor, access, name, desc, signature, exceptions);
    }
    
    @Override
    public void visitEnd() {
        CodeCoverageStore.registerClassProbes(this.classId, this.probeCount);
    }
}
