// 
// Decompiled by Procyon v0.5.36
// 

package org.pitest.dependency;

import org.pitest.reloc.asm.MethodVisitor;
import org.pitest.functional.SideEffect1;
import org.pitest.reloc.asm.ClassVisitor;

class DependencyClassVisitor extends ClassVisitor
{
    private final SideEffect1<DependencyAccess> typeReceiver;
    private String className;
    
    protected DependencyClassVisitor(final ClassVisitor visitor, final SideEffect1<DependencyAccess> typeReceiver) {
        super(393216, visitor);
        this.typeReceiver = this.filterOutJavaLangObject(typeReceiver);
    }
    
    private SideEffect1<DependencyAccess> filterOutJavaLangObject(final SideEffect1<DependencyAccess> child) {
        return new SideEffect1<DependencyAccess>() {
            @Override
            public void apply(final DependencyAccess a) {
                if (!a.getDest().getOwner().equals("java/lang/Object")) {
                    child.apply(a);
                }
            }
        };
    }
    
    @Override
    public void visit(final int version, final int access, final String name, final String signature, final String superName, final String[] interfaces) {
        this.className = name;
    }
    
    @Override
    public MethodVisitor visitMethod(final int access, final String name, final String desc, final String signature, final String[] exceptions) {
        final MethodVisitor methodVisitor = this.cv.visitMethod(access, name, desc, signature, exceptions);
        final DependencyAccess.Member me = new DependencyAccess.Member(this.className, name);
        return new DependencyAnalysisMethodVisitor(me, methodVisitor, this.typeReceiver);
    }
    
    private static class DependencyAnalysisMethodVisitor extends MethodVisitor
    {
        private final DependencyAccess.Member member;
        private final SideEffect1<DependencyAccess> typeReceiver;
        
        DependencyAnalysisMethodVisitor(final DependencyAccess.Member member, final MethodVisitor methodVisitor, final SideEffect1<DependencyAccess> typeReceiver) {
            super(393216, methodVisitor);
            this.typeReceiver = typeReceiver;
            this.member = member;
        }
        
        @Override
        public void visitMethodInsn(final int opcode, final String owner, final String name, final String desc, final boolean itf) {
            this.typeReceiver.apply(new DependencyAccess(this.member, new DependencyAccess.Member(owner, name)));
            this.mv.visitMethodInsn(opcode, owner, name, desc, itf);
        }
        
        @Override
        public void visitFieldInsn(final int opcode, final String owner, final String name, final String desc) {
            this.typeReceiver.apply(new DependencyAccess(this.member, new DependencyAccess.Member(owner, name)));
            this.mv.visitFieldInsn(opcode, owner, name, desc);
        }
    }
}
