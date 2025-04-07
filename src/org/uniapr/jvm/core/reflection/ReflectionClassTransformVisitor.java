// 
// Decompiled by Procyon v0.5.36
// 

package org.uniapr.jvm.core.reflection;

import java.util.Collection;
import java.util.HashSet;
import java.util.Arrays;
import java.util.HashMap;
import org.objectweb.asm.MethodVisitor;
import java.util.Set;
import java.util.Map;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.ClassVisitor;

public class ReflectionClassTransformVisitor extends ClassVisitor implements Opcodes
{
    String slashClazzName;
    boolean clinit;
    boolean isInterface;
    public static final Map<String, Set<String>> reflectionSites;
    
    public ReflectionClassTransformVisitor(final String clazzName, final ClassVisitor cv, final boolean isInterface) {
        super(327680, cv);
        this.clinit = false;
        this.isInterface = false;
        this.slashClazzName = clazzName;
        this.isInterface = isInterface;
        cv.visitSource("prapr_gen", "prapr_gen");
    }
    
    @Override
    public void visitSource(final String s1, final String debug) {
        this.cv.visitSource("prapr_gen-" + s1, "prapr_gen");
    }
    
    @Override
    public MethodVisitor visitMethod(final int access, final String name, final String desc, final String signature, final String[] exceptions) {
        final MethodVisitor mv = this.cv.visitMethod(access, name, desc, signature, exceptions);
        if (ReflectionClassTransformVisitor.reflectionSites.get(this.slashClazzName).contains(name)) {
            return (mv == null) ? null : new ReflectionRewriterMethodVisitor(mv, this.slashClazzName);
        }
        return mv;
    }
    
    static {
        (reflectionSites = new HashMap<String, Set<String>>()).put("java/lang/reflect/Field", new HashSet<String>(Arrays.asList("set", "get")));
        ReflectionClassTransformVisitor.reflectionSites.put("java/lang/reflect/Method", new HashSet<String>(Arrays.asList("invoke")));
        ReflectionClassTransformVisitor.reflectionSites.put("java/lang/reflect/Constructor", new HashSet<String>(Arrays.asList("newInstance")));
    }
}
