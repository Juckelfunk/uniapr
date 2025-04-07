// 
// Decompiled by Procyon v0.5.36
// 

package org.pitest.classinfo;

import org.pitest.reloc.asm.AnnotationVisitor;
import org.pitest.reloc.asm.Label;
import org.pitest.reloc.asm.MethodVisitor;

class InfoMethodVisitor extends MethodVisitor
{
    private final ClassInfoBuilder classInfo;
    
    InfoMethodVisitor(final ClassInfoBuilder classInfo, final MethodVisitor writer) {
        super(393216, writer);
        this.classInfo = classInfo;
    }
    
    @Override
    public void visitLineNumber(final int line, final Label start) {
        this.classInfo.registerCodeLine(line);
    }
    
    @Override
    public AnnotationVisitor visitAnnotation(final String desc, final boolean visible) {
        final String type = desc.substring(1, desc.length() - 1);
        this.classInfo.registerAnnotation(type);
        return super.visitAnnotation(desc, visible);
    }
}
