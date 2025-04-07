// 
// Decompiled by Procyon v0.5.36
// 

package org.pitest.classinfo;

import org.pitest.reloc.asm.MethodVisitor;
import org.pitest.functional.F5;
import org.pitest.reloc.asm.ClassVisitor;

public abstract class MethodFilteringAdapter extends ClassVisitor
{
    private final F5<Integer, String, String, String, String[], Boolean> filter;
    
    public MethodFilteringAdapter(final ClassVisitor writer, final F5<Integer, String, String, String, String[], Boolean> filter) {
        super(393216, writer);
        this.filter = filter;
    }
    
    private boolean shouldInstrument(final int access, final String name, final String desc, final String signature, final String[] exceptions) {
        return this.filter.apply(access, name, desc, signature, exceptions);
    }
    
    @Override
    public final MethodVisitor visitMethod(final int access, final String name, final String desc, final String signature, final String[] exceptions) {
        final MethodVisitor methodVisitor = this.cv.visitMethod(access, name, desc, signature, exceptions);
        if (this.shouldInstrument(access, name, desc, signature, exceptions)) {
            return this.visitMethodIfRequired(access, name, desc, signature, exceptions, methodVisitor);
        }
        return methodVisitor;
    }
    
    public abstract MethodVisitor visitMethodIfRequired(final int p0, final String p1, final String p2, final String p3, final String[] p4, final MethodVisitor p5);
}
