// 
// Decompiled by Procyon v0.5.36
// 

package org.pitest.mutationtest.build.intercept.staticinitializers;

import org.pitest.mutationtest.build.InterceptorType;
import org.pitest.mutationtest.engine.PoisonStatus;
import org.pitest.classinfo.ClassName;
import org.pitest.functional.Option;
import org.pitest.bytecode.analysis.AnalysisFunctions;
import org.objectweb.asm.tree.AbstractInsnNode;
import org.objectweb.asm.tree.MethodInsnNode;
import org.pitest.bytecode.analysis.MethodTree;
import org.pitest.functional.FunctionalList;
import org.pitest.functional.prelude.Prelude;
import org.pitest.functional.F;
import org.pitest.functional.FCollection;
import org.pitest.mutationtest.engine.Mutater;
import java.util.Collection;
import org.pitest.bytecode.analysis.ClassTree;
import org.pitest.mutationtest.engine.MutationDetails;
import org.pitest.functional.predicate.Predicate;
import org.pitest.mutationtest.engine.MethodName;
import org.pitest.mutationtest.build.MutationInterceptor;

class StaticInitializerInterceptor implements MutationInterceptor
{
    private static final MethodName CLINIT;
    private Predicate<MutationDetails> isStaticInitCode;
    
    @Override
    public void begin(final ClassTree clazz) {
        this.analyseClass(clazz);
    }
    
    @Override
    public Collection<MutationDetails> intercept(final Collection<MutationDetails> mutations, final Mutater m) {
        if (this.isStaticInitCode != null) {
            final FunctionalList<MutationDetails> altered = FCollection.filter(mutations, (F<MutationDetails, Boolean>)this.isStaticInitCode).map(this.setStaticInitializerFlag());
            final FunctionalList<MutationDetails> notAltered = FCollection.filter(mutations, (F<MutationDetails, Boolean>)Prelude.not((F<Object, Boolean>)this.isStaticInitCode));
            notAltered.addAll((Collection<?>)altered);
            return notAltered;
        }
        return mutations;
    }
    
    @Override
    public void end() {
        this.isStaticInitCode = null;
    }
    
    private void analyseClass(final ClassTree tree) {
        final Option<MethodTree> clinit = tree.methods().findFirst(this.nameEquals(StaticInitializerInterceptor.CLINIT.name()));
        if (clinit.hasSome()) {
            final FunctionalList<MethodInsnNode> selfCalls = clinit.value().instructions().flatMap((F<AbstractInsnNode, ? extends Iterable<MethodInsnNode>>)this.is(MethodInsnNode.class)).filter(this.calls(tree.name()));
            final Predicate<MethodTree> matchingCalls = Prelude.or(selfCalls.map(toPredicate()));
            final Predicate<MutationDetails> initOnlyMethods = Prelude.or(tree.methods().filter(isPrivateStatic()).filter((F<MethodTree, Boolean>)matchingCalls).map(AnalysisFunctions.matchMutationsInMethod()));
            this.isStaticInitCode = (Predicate<MutationDetails>)Prelude.or(isInStaticInitializer(), initOnlyMethods);
        }
    }
    
    private static Predicate<MutationDetails> isInStaticInitializer() {
        return new Predicate<MutationDetails>() {
            @Override
            public Boolean apply(final MutationDetails a) {
                return a.getId().getLocation().getMethodName().equals(StaticInitializerInterceptor.CLINIT);
            }
        };
    }
    
    private static F<MethodTree, Boolean> isPrivateStatic() {
        return new F<MethodTree, Boolean>() {
            @Override
            public Boolean apply(final MethodTree a) {
                return (a.rawNode().access & 0x8) != 0x0 && (a.rawNode().access & 0x2) != 0x0;
            }
        };
    }
    
    private static F<MethodInsnNode, Predicate<MethodTree>> toPredicate() {
        return new F<MethodInsnNode, Predicate<MethodTree>>() {
            @Override
            public Predicate<MethodTree> apply(final MethodInsnNode a) {
                return matchesCall(a);
            }
        };
    }
    
    private static Predicate<MethodTree> matchesCall(final MethodInsnNode call) {
        return new Predicate<MethodTree>() {
            @Override
            public Boolean apply(final MethodTree a) {
                return a.rawNode().name.equals(call.name) && a.rawNode().desc.equals(call.desc);
            }
        };
    }
    
    private F<MethodInsnNode, Boolean> calls(final ClassName self) {
        return new F<MethodInsnNode, Boolean>() {
            @Override
            public Boolean apply(final MethodInsnNode a) {
                return a.owner.equals(self.asInternalName());
            }
        };
    }
    
    private <T extends AbstractInsnNode> F<AbstractInsnNode, Option<T>> is(final Class<T> clazz) {
        return new F<AbstractInsnNode, Option<T>>() {
            @Override
            public Option<T> apply(final AbstractInsnNode a) {
                if (a.getClass().isAssignableFrom(clazz)) {
                    return Option.some(a);
                }
                return (Option<T>)Option.none();
            }
        };
    }
    
    private Predicate<MethodTree> nameEquals(final String name) {
        return new Predicate<MethodTree>() {
            @Override
            public Boolean apply(final MethodTree a) {
                return a.rawNode().name.equals(name);
            }
        };
    }
    
    private F<MutationDetails, MutationDetails> setStaticInitializerFlag() {
        return new F<MutationDetails, MutationDetails>() {
            @Override
            public MutationDetails apply(final MutationDetails a) {
                return a.withPoisonStatus(PoisonStatus.IS_STATIC_INITIALIZER_CODE);
            }
        };
    }
    
    @Override
    public InterceptorType type() {
        return InterceptorType.MODIFY;
    }
    
    static {
        CLINIT = MethodName.fromString("<clinit>");
    }
}
