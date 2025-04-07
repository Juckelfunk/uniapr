// 
// Decompiled by Procyon v0.5.36
// 

package org.pitest.mutationtest.engine.gregor;

import org.pitest.reloc.asm.Label;
import org.pitest.reloc.asm.MethodVisitor;

public class LineTrackingMethodVisitor extends MethodVisitor
{
    private final MutationContext context;
    
    public LineTrackingMethodVisitor(final MutationContext context, final MethodVisitor delegateMethodVisitor) {
        super(393216, delegateMethodVisitor);
        this.context = context;
    }
    
    @Override
    public void visitLineNumber(final int line, final Label start) {
        this.context.registerCurrentLine(line);
        this.mv.visitLineNumber(line, start);
    }
}
