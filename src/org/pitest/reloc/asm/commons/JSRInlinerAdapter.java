// 
// Decompiled by Procyon v0.5.36
// 

package org.pitest.reloc.asm.commons;

import java.util.Set;
import java.util.AbstractMap;
import org.pitest.reloc.asm.tree.InsnNode;
import java.util.List;
import org.pitest.reloc.asm.tree.LocalVariableNode;
import java.util.ArrayList;
import org.pitest.reloc.asm.tree.InsnList;
import java.util.LinkedList;
import org.pitest.reloc.asm.tree.LookupSwitchInsnNode;
import org.pitest.reloc.asm.tree.TableSwitchInsnNode;
import org.pitest.reloc.asm.tree.TryCatchBlockNode;
import java.util.Iterator;
import org.pitest.reloc.asm.tree.AbstractInsnNode;
import org.pitest.reloc.asm.tree.JumpInsnNode;
import org.pitest.reloc.asm.Label;
import java.util.HashMap;
import org.pitest.reloc.asm.MethodVisitor;
import java.util.BitSet;
import org.pitest.reloc.asm.tree.LabelNode;
import java.util.Map;
import org.pitest.reloc.asm.Opcodes;
import org.pitest.reloc.asm.tree.MethodNode;

public class JSRInlinerAdapter extends MethodNode implements Opcodes
{
    private static final boolean LOGGING = false;
    private final Map<LabelNode, BitSet> subroutineHeads;
    private final BitSet mainSubroutine;
    final BitSet dualCitizens;
    
    public JSRInlinerAdapter(final MethodVisitor mv, final int access, final String name, final String desc, final String signature, final String[] exceptions) {
        this(393216, mv, access, name, desc, signature, exceptions);
        if (this.getClass() != JSRInlinerAdapter.class) {
            throw new IllegalStateException();
        }
    }
    
    protected JSRInlinerAdapter(final int api, final MethodVisitor mv, final int access, final String name, final String desc, final String signature, final String[] exceptions) {
        super(api, access, name, desc, signature, exceptions);
        this.subroutineHeads = new HashMap<LabelNode, BitSet>();
        this.mainSubroutine = new BitSet();
        this.dualCitizens = new BitSet();
        this.mv = mv;
    }
    
    @Override
    public void visitJumpInsn(final int opcode, final Label lbl) {
        super.visitJumpInsn(opcode, lbl);
        final LabelNode ln = ((JumpInsnNode)this.instructions.getLast()).label;
        if (opcode == 168 && !this.subroutineHeads.containsKey(ln)) {
            this.subroutineHeads.put(ln, new BitSet());
        }
    }
    
    @Override
    public void visitEnd() {
        if (!this.subroutineHeads.isEmpty()) {
            this.markSubroutines();
            this.emitCode();
        }
        if (this.mv != null) {
            this.accept(this.mv);
        }
    }
    
    private void markSubroutines() {
        final BitSet anyvisited = new BitSet();
        this.markSubroutineWalk(this.mainSubroutine, 0, anyvisited);
        for (final Map.Entry<LabelNode, BitSet> entry : this.subroutineHeads.entrySet()) {
            final LabelNode lab = entry.getKey();
            final BitSet sub = entry.getValue();
            final int index = this.instructions.indexOf(lab);
            this.markSubroutineWalk(sub, index, anyvisited);
        }
    }
    
    private void markSubroutineWalk(final BitSet sub, final int index, final BitSet anyvisited) {
        this.markSubroutineWalkDFS(sub, index, anyvisited);
        boolean loop = true;
        while (loop) {
            loop = false;
            for (final TryCatchBlockNode trycatch : this.tryCatchBlocks) {
                final int handlerindex = this.instructions.indexOf(trycatch.handler);
                if (sub.get(handlerindex)) {
                    continue;
                }
                final int startindex = this.instructions.indexOf(trycatch.start);
                final int endindex = this.instructions.indexOf(trycatch.end);
                final int nextbit = sub.nextSetBit(startindex);
                if (nextbit == -1 || nextbit >= endindex) {
                    continue;
                }
                this.markSubroutineWalkDFS(sub, handlerindex, anyvisited);
                loop = true;
            }
        }
    }
    
    private void markSubroutineWalkDFS(final BitSet sub, int index, final BitSet anyvisited) {
        while (true) {
            final AbstractInsnNode node = this.instructions.get(index);
            if (sub.get(index)) {
                return;
            }
            sub.set(index);
            if (anyvisited.get(index)) {
                this.dualCitizens.set(index);
            }
            anyvisited.set(index);
            if (node.getType() == 7 && node.getOpcode() != 168) {
                final JumpInsnNode jnode = (JumpInsnNode)node;
                final int destidx = this.instructions.indexOf(jnode.label);
                this.markSubroutineWalkDFS(sub, destidx, anyvisited);
            }
            if (node.getType() == 11) {
                final TableSwitchInsnNode tsnode = (TableSwitchInsnNode)node;
                int destidx = this.instructions.indexOf(tsnode.dflt);
                this.markSubroutineWalkDFS(sub, destidx, anyvisited);
                for (int i = tsnode.labels.size() - 1; i >= 0; --i) {
                    final LabelNode l = tsnode.labels.get(i);
                    destidx = this.instructions.indexOf(l);
                    this.markSubroutineWalkDFS(sub, destidx, anyvisited);
                }
            }
            if (node.getType() == 12) {
                final LookupSwitchInsnNode lsnode = (LookupSwitchInsnNode)node;
                int destidx = this.instructions.indexOf(lsnode.dflt);
                this.markSubroutineWalkDFS(sub, destidx, anyvisited);
                for (int i = lsnode.labels.size() - 1; i >= 0; --i) {
                    final LabelNode l = lsnode.labels.get(i);
                    destidx = this.instructions.indexOf(l);
                    this.markSubroutineWalkDFS(sub, destidx, anyvisited);
                }
            }
            switch (this.instructions.get(index).getOpcode()) {
                case 167:
                case 169:
                case 170:
                case 171:
                case 172:
                case 173:
                case 174:
                case 175:
                case 176:
                case 177:
                case 191: {}
                default: {
                    if (++index >= this.instructions.size()) {
                        return;
                    }
                    continue;
                }
            }
        }
    }
    
    private void emitCode() {
        final LinkedList<Instantiation> worklist = new LinkedList<Instantiation>();
        worklist.add(new Instantiation(null, this.mainSubroutine));
        final InsnList newInstructions = new InsnList();
        final List<TryCatchBlockNode> newTryCatchBlocks = new ArrayList<TryCatchBlockNode>();
        final List<LocalVariableNode> newLocalVariables = new ArrayList<LocalVariableNode>();
        while (!worklist.isEmpty()) {
            final Instantiation inst = worklist.removeFirst();
            this.emitSubroutine(inst, worklist, newInstructions, newTryCatchBlocks, newLocalVariables);
        }
        this.instructions = newInstructions;
        this.tryCatchBlocks = newTryCatchBlocks;
        this.localVariables = newLocalVariables;
    }
    
    private void emitSubroutine(final Instantiation instant, final List<Instantiation> worklist, final InsnList newInstructions, final List<TryCatchBlockNode> newTryCatchBlocks, final List<LocalVariableNode> newLocalVariables) {
        LabelNode duplbl = null;
        for (int i = 0, c = this.instructions.size(); i < c; ++i) {
            final AbstractInsnNode insn = this.instructions.get(i);
            final Instantiation owner = instant.findOwner(i);
            if (insn.getType() == 8) {
                final LabelNode ilbl = (LabelNode)insn;
                final LabelNode remap = instant.rangeLabel(ilbl);
                if (remap != duplbl) {
                    newInstructions.add(remap);
                    duplbl = remap;
                }
            }
            else if (owner == instant) {
                if (insn.getOpcode() == 169) {
                    LabelNode retlabel = null;
                    for (Instantiation p = instant; p != null; p = p.previous) {
                        if (p.subroutine.get(i)) {
                            retlabel = p.returnLabel;
                        }
                    }
                    if (retlabel == null) {
                        throw new RuntimeException("Instruction #" + i + " is a RET not owned by any subroutine");
                    }
                    newInstructions.add(new JumpInsnNode(167, retlabel));
                }
                else if (insn.getOpcode() == 168) {
                    final LabelNode lbl = ((JumpInsnNode)insn).label;
                    final BitSet sub = this.subroutineHeads.get(lbl);
                    final Instantiation newinst = new Instantiation(instant, sub);
                    final LabelNode startlbl = newinst.gotoLabel(lbl);
                    newInstructions.add(new InsnNode(1));
                    newInstructions.add(new JumpInsnNode(167, startlbl));
                    newInstructions.add(newinst.returnLabel);
                    worklist.add(newinst);
                }
                else {
                    newInstructions.add(insn.clone(instant));
                }
            }
        }
        for (final TryCatchBlockNode trycatch : this.tryCatchBlocks) {
            final LabelNode start = instant.rangeLabel(trycatch.start);
            final LabelNode end = instant.rangeLabel(trycatch.end);
            if (start == end) {
                continue;
            }
            final LabelNode handler = instant.gotoLabel(trycatch.handler);
            if (start == null || end == null || handler == null) {
                throw new RuntimeException("Internal error!");
            }
            newTryCatchBlocks.add(new TryCatchBlockNode(start, end, handler, trycatch.type));
        }
        for (final LocalVariableNode lvnode : this.localVariables) {
            final LabelNode start = instant.rangeLabel(lvnode.start);
            final LabelNode end = instant.rangeLabel(lvnode.end);
            if (start == end) {
                continue;
            }
            newLocalVariables.add(new LocalVariableNode(lvnode.name, lvnode.desc, lvnode.signature, start, end, lvnode.index));
        }
    }
    
    private static void log(final String str) {
        System.err.println(str);
    }
    
    private class Instantiation extends AbstractMap<LabelNode, LabelNode>
    {
        final Instantiation previous;
        public final BitSet subroutine;
        public final Map<LabelNode, LabelNode> rangeTable;
        public final LabelNode returnLabel;
        
        Instantiation(final Instantiation prev, final BitSet sub) {
            this.rangeTable = new HashMap<LabelNode, LabelNode>();
            this.previous = prev;
            this.subroutine = sub;
            for (Instantiation p = prev; p != null; p = p.previous) {
                if (p.subroutine == sub) {
                    throw new RuntimeException("Recursive invocation of " + sub);
                }
            }
            if (prev != null) {
                this.returnLabel = new LabelNode();
            }
            else {
                this.returnLabel = null;
            }
            LabelNode duplbl = null;
            for (int i = 0, c = JSRInlinerAdapter.this.instructions.size(); i < c; ++i) {
                final AbstractInsnNode insn = JSRInlinerAdapter.this.instructions.get(i);
                if (insn.getType() == 8) {
                    final LabelNode ilbl = (LabelNode)insn;
                    if (duplbl == null) {
                        duplbl = new LabelNode();
                    }
                    this.rangeTable.put(ilbl, duplbl);
                }
                else if (this.findOwner(i) == this) {
                    duplbl = null;
                }
            }
        }
        
        public Instantiation findOwner(final int i) {
            if (!this.subroutine.get(i)) {
                return null;
            }
            if (!JSRInlinerAdapter.this.dualCitizens.get(i)) {
                return this;
            }
            Instantiation own = this;
            for (Instantiation p = this.previous; p != null; p = p.previous) {
                if (p.subroutine.get(i)) {
                    own = p;
                }
            }
            return own;
        }
        
        public LabelNode gotoLabel(final LabelNode l) {
            final Instantiation owner = this.findOwner(JSRInlinerAdapter.this.instructions.indexOf(l));
            return owner.rangeTable.get(l);
        }
        
        public LabelNode rangeLabel(final LabelNode l) {
            return this.rangeTable.get(l);
        }
        
        @Override
        public Set<Map.Entry<LabelNode, LabelNode>> entrySet() {
            return null;
        }
        
        @Override
        public LabelNode get(final Object o) {
            return this.gotoLabel((LabelNode)o);
        }
    }
}
