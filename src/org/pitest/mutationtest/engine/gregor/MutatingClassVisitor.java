// 
// Decompiled by Procyon v0.5.36
// 

package org.pitest.mutationtest.engine.gregor;

import org.pitest.mutationtest.engine.gregor.blocks.BlockCounter;
import org.pitest.mutationtest.engine.gregor.blocks.BlockTrackingMethodDecorator;
import java.util.Iterator;
import org.pitest.mutationtest.engine.gregor.analysis.InstructionCounter;
import org.pitest.mutationtest.engine.gregor.analysis.InstructionTrackingMethodVisitor;
import org.pitest.mutationtest.engine.Location;
import org.pitest.mutationtest.engine.MethodName;
import org.pitest.classinfo.ClassName;
import org.pitest.reloc.asm.MethodVisitor;
import java.util.HashSet;
import java.util.Collection;
import java.util.Set;
import org.pitest.functional.F;
import org.pitest.reloc.asm.ClassVisitor;

class MutatingClassVisitor extends ClassVisitor
{
    private final F<MethodInfo, Boolean> filter;
    private final ClassContext context;
    private final Set<MethodMutatorFactory> methodMutators;
    
    MutatingClassVisitor(final ClassVisitor delegateClassVisitor, final ClassContext context, final F<MethodInfo, Boolean> filter, final Collection<MethodMutatorFactory> mutators) {
        super(393216, delegateClassVisitor);
        this.methodMutators = new HashSet<MethodMutatorFactory>();
        this.context = context;
        this.filter = filter;
        this.methodMutators.addAll(mutators);
    }
    
    @Override
    public void visit(final int version, final int access, final String name, final String signature, final String superName, final String[] interfaces) {
        super.visit(version, access, name, signature, superName, interfaces);
        this.context.registerClass(new ClassInfo(version, access, name, signature, superName, interfaces));
    }
    
    @Override
    public void visitSource(final String source, final String debug) {
        super.visitSource(source, debug);
        this.context.registerSourceFile(source);
    }
    
    @Override
    public MethodVisitor visitMethod(final int access, final String methodName, final String methodDescriptor, final String signature, final String[] exceptions) {
        final MethodMutationContext methodContext = new MethodMutationContext(this.context, Location.location(ClassName.fromString(this.context.getClassInfo().getName()), MethodName.fromString(methodName), methodDescriptor));
        final MethodVisitor methodVisitor = this.cv.visitMethod(access, methodName, methodDescriptor, signature, exceptions);
        final MethodInfo info = new MethodInfo().withOwner(this.context.getClassInfo()).withAccess(access).withMethodName(methodName).withMethodDescriptor(methodDescriptor);
        if (this.filter.apply(info)) {
            return this.visitMethodForMutation(methodContext, info, methodVisitor);
        }
        return methodVisitor;
    }
    
    private MethodVisitor visitMethodForMutation(final MethodMutationContext methodContext, final MethodInfo methodInfo, final MethodVisitor methodVisitor) {
        MethodVisitor next = methodVisitor;
        for (final MethodMutatorFactory each : this.methodMutators) {
            next = each.create(methodContext, methodInfo, next);
        }
        return new InstructionTrackingMethodVisitor(wrapWithDecorators(methodContext, this.wrapWithFilters(methodContext, next)), methodContext);
    }
    
    private static MethodVisitor wrapWithDecorators(final MethodMutationContext methodContext, final MethodVisitor mv) {
        return wrapWithBlockTracker(methodContext, wrapWithLineTracker(methodContext, mv));
    }
    
    private static MethodVisitor wrapWithBlockTracker(final MethodMutationContext methodContext, final MethodVisitor mv) {
        return new BlockTrackingMethodDecorator(methodContext, mv);
    }
    
    private static MethodVisitor wrapWithLineTracker(final MethodMutationContext methodContext, final MethodVisitor mv) {
        return new LineTrackingMethodVisitor(methodContext, mv);
    }
    
    private MethodVisitor wrapWithFilters(final MethodMutationContext methodContext, final MethodVisitor wrappedMethodVisitor) {
        return wrapWithStringSwitchFilter(methodContext, wrapWithAssertFilter(methodContext, wrappedMethodVisitor));
    }
    
    private static MethodVisitor wrapWithStringSwitchFilter(final MethodMutationContext methodContext, final MethodVisitor wrappedMethodVisitor) {
        return new AvoidStringSwitchedMethodAdapter(methodContext, wrappedMethodVisitor);
    }
    
    private static MethodVisitor wrapWithAssertFilter(final MethodMutationContext methodContext, final MethodVisitor wrappedMethodVisitor) {
        return new AvoidAssertsMethodAdapter(methodContext, wrappedMethodVisitor);
    }
}
