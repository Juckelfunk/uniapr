// 
// Decompiled by Procyon v0.5.36
// 

package org.pitest.reloc.asm.commons;

import java.util.Collections;
import org.pitest.reloc.asm.tree.AbstractInsnNode;
import org.pitest.reloc.asm.tree.TryCatchBlockNode;
import java.util.Comparator;
import org.pitest.reloc.asm.MethodVisitor;
import org.pitest.reloc.asm.tree.MethodNode;

public class TryCatchBlockSorter extends MethodNode
{
    public TryCatchBlockSorter(final MethodVisitor mv, final int access, final String name, final String desc, final String signature, final String[] exceptions) {
        this(393216, mv, access, name, desc, signature, exceptions);
    }
    
    protected TryCatchBlockSorter(final int api, final MethodVisitor mv, final int access, final String name, final String desc, final String signature, final String[] exceptions) {
        super(api, access, name, desc, signature, exceptions);
        this.mv = mv;
    }
    
    @Override
    public void visitEnd() {
        final Comparator<TryCatchBlockNode> comp = new Comparator<TryCatchBlockNode>() {
            public int compare(final TryCatchBlockNode t1, final TryCatchBlockNode t2) {
                final int len1 = this.blockLength(t1);
                final int len2 = this.blockLength(t2);
                return len1 - len2;
            }
            
            private int blockLength(final TryCatchBlockNode block) {
                final int startidx = TryCatchBlockSorter.this.instructions.indexOf(block.start);
                final int endidx = TryCatchBlockSorter.this.instructions.indexOf(block.end);
                return endidx - startidx;
            }
        };
        Collections.sort(this.tryCatchBlocks, comp);
        for (int i = 0; i < this.tryCatchBlocks.size(); ++i) {
            this.tryCatchBlocks.get(i).updateIndex(i);
        }
        if (this.mv != null) {
            this.accept(this.mv);
        }
    }
}
