// 
// Decompiled by Procyon v0.5.36
// 

package org.uniapr.jvm.offline;

import org.uniapr.jvm.core.JVMStatus;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.FieldVisitor;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.ClassVisitor;

class LeakingFieldsVisitor extends ClassVisitor implements Opcodes
{
    String cName;
    boolean interfaze;
    boolean hasClinit;
    boolean isEnum;
    boolean isAbstract;
    boolean hasUnsafeField;
    String superClass;
    
    public LeakingFieldsVisitor() {
        super(327680);
        this.interfaze = false;
        this.hasClinit = false;
        this.isEnum = false;
        this.isAbstract = false;
        this.hasUnsafeField = false;
    }
    
    @Override
    public void visit(final int version, final int access, final String name, final String signature, final String superName, final String[] interfaces) {
        this.cName = name;
        this.interfaze = isInterface(access);
        this.isEnum = isEnum(access);
        this.isAbstract = isAbstract(access);
        ++LeakingFieldMain.counter;
        this.superClass = superName;
    }
    
    @Override
    public FieldVisitor visitField(final int access, final String name, final String descriptor, final String signature, final Object value) {
        if (!this.isSafe(access, value)) {
            this.hasUnsafeField = true;
        }
        return null;
    }
    
    @Override
    public MethodVisitor visitMethod(final int access, final String name, final String desc, final String signature, final String[] exceptions) {
        final boolean isClinit = name.startsWith("<clinit>");
        if (isClinit) {
            this.hasClinit = true;
        }
        return null;
    }
    
    @Override
    public void visitEnd() {
        if (!this.interfaze) {
            LeakingFieldMain.completeParentMap.put(this.cName, this.superClass);
        }
        if (this.hasClinit && this.hasUnsafeField && !this.isEnum && !this.isAbstract && !JVMStatus.isExcluded(this.cName)) {
            if (!LeakingFieldMain.visitedAndSafe.contains(this.cName)) {
                LeakingFieldMain.classes.put(this.cName, this.interfaze);
            }
        }
        else {
            LeakingFieldMain.visitedAndSafe.add(this.cName);
        }
    }
    
    public boolean isSafe(final int access, final Object value) {
        return (access & 0x8) == 0x0 || ((access & 0x10) != 0x0 && value != null);
    }
    
    public static boolean isInterface(final int access) {
        return (access & 0x200) != 0x0;
    }
    
    public static boolean isAbstract(final int access) {
        return (access & 0x400) != 0x0;
    }
    
    public static boolean isEnum(final int access) {
        return (access & 0x4000) != 0x0;
    }
}
