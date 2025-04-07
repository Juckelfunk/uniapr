// 
// Decompiled by Procyon v0.5.36
// 

package org.pitest.reloc.asm.commons;

import org.pitest.reloc.asm.MethodVisitor;
import org.pitest.reloc.asm.ClassVisitor;

public class StaticInitMerger extends ClassVisitor
{
    private String name;
    private MethodVisitor clinit;
    private final String prefix;
    private int counter;
    
    public StaticInitMerger(final String prefix, final ClassVisitor cv) {
        this(393216, prefix, cv);
    }
    
    protected StaticInitMerger(final int api, final String prefix, final ClassVisitor cv) {
        super(api, cv);
        this.prefix = prefix;
    }
    
    @Override
    public void visit(final int version, final int access, final String name, final String signature, final String superName, final String[] interfaces) {
        this.cv.visit(version, access, name, signature, superName, interfaces);
        this.name = name;
    }
    
    @Override
    public MethodVisitor visitMethod(final int access, final String name, final String desc, final String signature, final String[] exceptions) {
        MethodVisitor mv;
        if ("<clinit>".equals(name)) {
            final int a = 10;
            final String n = this.prefix + this.counter++;
            mv = this.cv.visitMethod(a, n, desc, signature, exceptions);
            if (this.clinit == null) {
                this.clinit = this.cv.visitMethod(a, name, desc, null, null);
            }
            this.clinit.visitMethodInsn(184, this.name, n, desc, false);
        }
        else {
            mv = this.cv.visitMethod(access, name, desc, signature, exceptions);
        }
        return mv;
    }
    
    @Override
    public void visitEnd() {
        if (this.clinit != null) {
            this.clinit.visitInsn(177);
            this.clinit.visitMaxs(0, 0);
        }
        this.cv.visitEnd();
    }
}
