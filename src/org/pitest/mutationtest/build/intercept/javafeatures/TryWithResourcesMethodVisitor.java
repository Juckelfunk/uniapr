// 
// Decompiled by Procyon v0.5.36
// 

package org.pitest.mutationtest.build.intercept.javafeatures;

import java.util.Arrays;
import org.objectweb.asm.Label;
import java.util.ArrayList;
import java.util.Set;
import java.util.List;
import org.objectweb.asm.MethodVisitor;

class TryWithResourcesMethodVisitor extends MethodVisitor
{
    private static final List<Integer> JAVAC_CLASS_INS_SEQUENCE;
    private static final List<Integer> JAVAC_INTERFACE_INS_SEQUENCE;
    private static final List<Integer> ECJ_INS_SEQUENCE;
    private final Set<Integer> lines;
    private final List<Integer> opcodesStack;
    private int currentLineNumber;
    
    TryWithResourcesMethodVisitor(final Set<Integer> lines) {
        super(393216);
        this.opcodesStack = new ArrayList<Integer>();
        this.lines = lines;
    }
    
    @Override
    public void visitLineNumber(final int line, final Label start) {
        this.prepareToStartTracking();
        super.visitLineNumber(this.currentLineNumber = line, start);
    }
    
    @Override
    public void visitVarInsn(final int opcode, final int var) {
        this.opcodesStack.add(opcode);
        super.visitVarInsn(opcode, var);
    }
    
    @Override
    public void visitJumpInsn(final int opcode, final Label label) {
        this.opcodesStack.add(opcode);
        super.visitJumpInsn(opcode, label);
    }
    
    @Override
    public void visitMethodInsn(final int opcode, final String owner, final String name, final String desc, final boolean itf) {
        this.opcodesStack.add(opcode);
        super.visitMethodInsn(opcode, owner, name, desc, itf);
    }
    
    @Override
    public void visitInsn(final int opcode) {
        if (opcode == 191) {
            this.opcodesStack.add(opcode);
            this.finishTracking();
        }
        super.visitInsn(opcode);
    }
    
    private void finishTracking() {
        if (TryWithResourcesMethodVisitor.JAVAC_CLASS_INS_SEQUENCE.equals(this.opcodesStack) || TryWithResourcesMethodVisitor.JAVAC_INTERFACE_INS_SEQUENCE.equals(this.opcodesStack) || TryWithResourcesMethodVisitor.ECJ_INS_SEQUENCE.equals(this.opcodesStack)) {
            this.lines.add(this.currentLineNumber);
        }
        this.prepareToStartTracking();
    }
    
    private void prepareToStartTracking() {
        if (!this.opcodesStack.isEmpty()) {
            this.opcodesStack.clear();
        }
    }
    
    static {
        JAVAC_CLASS_INS_SEQUENCE = Arrays.asList(58, 25, 198, 25, 198, 25, 182, 167, 58, 25, 25, 182, 167, 25, 182, 25, 191);
        JAVAC_INTERFACE_INS_SEQUENCE = Arrays.asList(58, 25, 198, 25, 198, 25, 185, 167, 58, 25, 25, 182, 167, 25, 185, 25, 191);
        ECJ_INS_SEQUENCE = Arrays.asList(58, 25, 199, 25, 58, 167, 25, 25, 165, 25, 25, 182, 25, 191);
    }
}
