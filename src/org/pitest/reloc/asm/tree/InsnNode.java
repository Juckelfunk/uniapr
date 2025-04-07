// 
// Decompiled by Procyon v0.5.36
// 

package org.pitest.reloc.asm.tree;

import java.util.Map;
import org.pitest.reloc.asm.MethodVisitor;

public class InsnNode extends AbstractInsnNode
{
    public InsnNode(final int opcode) {
        super(opcode);
    }
    
    @Override
    public int getType() {
        return 0;
    }
    
    @Override
    public void accept(final MethodVisitor mv) {
        mv.visitInsn(this.opcode);
        this.acceptAnnotations(mv);
    }
    
    @Override
    public AbstractInsnNode clone(final Map<LabelNode, LabelNode> labels) {
        return new InsnNode(this.opcode).cloneAnnotations(this);
    }
}
