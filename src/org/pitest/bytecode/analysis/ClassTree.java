// 
// Decompiled by Procyon v0.5.36
// 

package org.pitest.bytecode.analysis;

import org.objectweb.asm.util.Printer;
import org.objectweb.asm.util.TraceClassVisitor;
import java.io.Writer;
import java.io.PrintWriter;
import org.objectweb.asm.util.Textifier;
import java.io.StringWriter;
import org.pitest.classinfo.ClassName;
import java.util.Collection;
import org.pitest.functional.MutableList;
import org.objectweb.asm.tree.AnnotationNode;
import org.pitest.functional.F;
import org.pitest.functional.Option;
import org.pitest.mutationtest.engine.Location;
import org.objectweb.asm.tree.MethodNode;
import org.pitest.functional.FCollection;
import org.objectweb.asm.ClassVisitor;
import org.objectweb.asm.ClassReader;
import org.pitest.functional.FunctionalList;
import org.objectweb.asm.tree.ClassNode;

public class ClassTree
{
    private final ClassNode rawNode;
    private FunctionalList<MethodTree> lazyMethods;
    
    public ClassTree(final ClassNode rawNode) {
        this.rawNode = rawNode;
    }
    
    public static ClassTree fromBytes(final byte[] bytes) {
        final ClassReader cr = new ClassReader(bytes);
        final ClassNode classNode = new ClassNode();
        cr.accept((ClassVisitor)classNode, 8);
        return new ClassTree(classNode);
    }
    
    public FunctionalList<MethodTree> methods() {
        if (this.lazyMethods != null) {
            return this.lazyMethods;
        }
        return this.lazyMethods = FCollection.map(this.rawNode.methods, toTree(this.name()));
    }
    
    public Option<MethodTree> method(final Location loc) {
        return this.methods().findFirst(MethodMatchers.forLocation(loc));
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
    
    private static F<MethodNode, MethodTree> toTree(final ClassName name) {
        return new F<MethodNode, MethodTree>() {
            @Override
            public MethodTree apply(final MethodNode a) {
                return new MethodTree(name, a);
            }
        };
    }
    
    public ClassName name() {
        return ClassName.fromString(this.rawNode.name);
    }
    
    public ClassNode rawNode() {
        return this.rawNode;
    }
    
    @Override
    public String toString() {
        final StringWriter writer = new StringWriter();
        this.rawNode.accept((ClassVisitor)new TraceClassVisitor((ClassVisitor)null, (Printer)new Textifier(), new PrintWriter(writer)));
        return writer.toString();
    }
}
