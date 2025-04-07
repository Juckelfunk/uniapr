// 
// Decompiled by Procyon v0.5.36
// 

package org.pitest.reloc.asm.commons;

import org.pitest.reloc.asm.Label;
import org.pitest.reloc.asm.Handle;
import org.pitest.reloc.asm.Opcodes;
import org.pitest.reloc.asm.MethodVisitor;

public class CodeSizeEvaluator extends MethodVisitor implements Opcodes
{
    private int minSize;
    private int maxSize;
    
    public CodeSizeEvaluator(final MethodVisitor mv) {
        this(393216, mv);
    }
    
    protected CodeSizeEvaluator(final int api, final MethodVisitor mv) {
        super(api, mv);
    }
    
    public int getMinSize() {
        return this.minSize;
    }
    
    public int getMaxSize() {
        return this.maxSize;
    }
    
    @Override
    public void visitInsn(final int opcode) {
        ++this.minSize;
        ++this.maxSize;
        if (this.mv != null) {
            this.mv.visitInsn(opcode);
        }
    }
    
    @Override
    public void visitIntInsn(final int opcode, final int operand) {
        if (opcode == 17) {
            this.minSize += 3;
            this.maxSize += 3;
        }
        else {
            this.minSize += 2;
            this.maxSize += 2;
        }
        if (this.mv != null) {
            this.mv.visitIntInsn(opcode, operand);
        }
    }
    
    @Override
    public void visitVarInsn(final int opcode, final int var) {
        if (var < 4 && opcode != 169) {
            ++this.minSize;
            ++this.maxSize;
        }
        else if (var >= 256) {
            this.minSize += 4;
            this.maxSize += 4;
        }
        else {
            this.minSize += 2;
            this.maxSize += 2;
        }
        if (this.mv != null) {
            this.mv.visitVarInsn(opcode, var);
        }
    }
    
    @Override
    public void visitTypeInsn(final int opcode, final String type) {
        this.minSize += 3;
        this.maxSize += 3;
        if (this.mv != null) {
            this.mv.visitTypeInsn(opcode, type);
        }
    }
    
    @Override
    public void visitFieldInsn(final int opcode, final String owner, final String name, final String desc) {
        this.minSize += 3;
        this.maxSize += 3;
        if (this.mv != null) {
            this.mv.visitFieldInsn(opcode, owner, name, desc);
        }
    }
    
    @Deprecated
    @Override
    public void visitMethodInsn(final int opcode, final String owner, final String name, final String desc) {
        if (this.api >= 327680) {
            super.visitMethodInsn(opcode, owner, name, desc);
            return;
        }
        this.doVisitMethodInsn(opcode, owner, name, desc, opcode == 185);
    }
    
    @Override
    public void visitMethodInsn(final int opcode, final String owner, final String name, final String desc, final boolean itf) {
        if (this.api < 327680) {
            super.visitMethodInsn(opcode, owner, name, desc, itf);
            return;
        }
        this.doVisitMethodInsn(opcode, owner, name, desc, itf);
    }
    
    private void doVisitMethodInsn(final int opcode, final String owner, final String name, final String desc, final boolean itf) {
        if (opcode == 185) {
            this.minSize += 5;
            this.maxSize += 5;
        }
        else {
            this.minSize += 3;
            this.maxSize += 3;
        }
        if (this.mv != null) {
            this.mv.visitMethodInsn(opcode, owner, name, desc, itf);
        }
    }
    
    @Override
    public void visitInvokeDynamicInsn(final String name, final String desc, final Handle bsm, final Object... bsmArgs) {
        this.minSize += 5;
        this.maxSize += 5;
        if (this.mv != null) {
            this.mv.visitInvokeDynamicInsn(name, desc, bsm, bsmArgs);
        }
    }
    
    @Override
    public void visitJumpInsn(final int opcode, final Label label) {
        this.minSize += 3;
        if (opcode == 167 || opcode == 168) {
            this.maxSize += 5;
        }
        else {
            this.maxSize += 8;
        }
        if (this.mv != null) {
            this.mv.visitJumpInsn(opcode, label);
        }
    }
    
    @Override
    public void visitLdcInsn(final Object cst) {
        if (cst instanceof Long || cst instanceof Double) {
            this.minSize += 3;
            this.maxSize += 3;
        }
        else {
            this.minSize += 2;
            this.maxSize += 3;
        }
        if (this.mv != null) {
            this.mv.visitLdcInsn(cst);
        }
    }
    
    @Override
    public void visitIincInsn(final int var, final int increment) {
        if (var > 255 || increment > 127 || increment < -128) {
            this.minSize += 6;
            this.maxSize += 6;
        }
        else {
            this.minSize += 3;
            this.maxSize += 3;
        }
        if (this.mv != null) {
            this.mv.visitIincInsn(var, increment);
        }
    }
    
    @Override
    public void visitTableSwitchInsn(final int min, final int max, final Label dflt, final Label... labels) {
        this.minSize += 13 + labels.length * 4;
        this.maxSize += 16 + labels.length * 4;
        if (this.mv != null) {
            this.mv.visitTableSwitchInsn(min, max, dflt, labels);
        }
    }
    
    @Override
    public void visitLookupSwitchInsn(final Label dflt, final int[] keys, final Label[] labels) {
        this.minSize += 9 + keys.length * 8;
        this.maxSize += 12 + keys.length * 8;
        if (this.mv != null) {
            this.mv.visitLookupSwitchInsn(dflt, keys, labels);
        }
    }
    
    @Override
    public void visitMultiANewArrayInsn(final String desc, final int dims) {
        this.minSize += 4;
        this.maxSize += 4;
        if (this.mv != null) {
            this.mv.visitMultiANewArrayInsn(desc, dims);
        }
    }
}
