// 
// Decompiled by Procyon v0.5.36
// 

package org.pitest.bytecode.analysis;

import java.util.ListIterator;
import java.util.List;
import java.util.LinkedList;
import java.util.Collection;
import org.pitest.functional.MutableList;
import org.objectweb.asm.tree.AnnotationNode;
import org.pitest.mutationtest.engine.MethodName;
import org.pitest.mutationtest.engine.Location;
import org.objectweb.asm.tree.AbstractInsnNode;
import org.pitest.functional.FunctionalList;
import org.objectweb.asm.tree.MethodNode;
import org.pitest.classinfo.ClassName;

public class MethodTree
{
    private final ClassName owner;
    private final MethodNode rawNode;
    private FunctionalList<AbstractInsnNode> lazyInstructions;
    
    public MethodTree(final ClassName owner, final MethodNode rawNode) {
        this.owner = owner;
        this.rawNode = rawNode;
    }
    
    public MethodNode rawNode() {
        return this.rawNode;
    }
    
    public Location asLocation() {
        return Location.location(this.owner, MethodName.fromString(this.rawNode.name), this.rawNode.desc);
    }
    
    public FunctionalList<AbstractInsnNode> instructions() {
        if (this.lazyInstructions != null) {
            return this.lazyInstructions;
        }
        return this.createInstructionList();
    }
    
    public boolean isSynthetic() {
        return (this.rawNode.access & 0x1000) != 0x0;
    }
    
    public FunctionalList<AnnotationNode> annotations() {
        final FunctionalList<AnnotationNode> annotaions = new MutableList<AnnotationNode>();
        if (this.rawNode.invisibleAnnotations != null) {
            annotaions.addAll((Collection<?>)this.rawNode.invisibleAnnotations);
        }
        if (this.rawNode.visibleAnnotations != null) {
            annotaions.addAll((Collection<?>)this.rawNode.visibleAnnotations);
        }
        return annotaions;
    }
    
    private FunctionalList<AbstractInsnNode> createInstructionList() {
        final List<AbstractInsnNode> list = new LinkedList<AbstractInsnNode>();
        final ListIterator<AbstractInsnNode> it = (ListIterator<AbstractInsnNode>)this.rawNode.instructions.iterator();
        while (it.hasNext()) {
            list.add(it.next());
        }
        return this.lazyInstructions = new MutableList<AbstractInsnNode>(list);
    }
}
