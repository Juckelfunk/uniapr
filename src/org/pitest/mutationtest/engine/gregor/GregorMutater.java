// 
// Decompiled by Procyon v0.5.36
// 

package org.pitest.mutationtest.engine.gregor;

import org.pitest.functional.prelude.Prelude;
import java.util.List;
import org.pitest.reloc.asm.ClassWriter;
import org.pitest.functional.FCollection;
import org.pitest.classinfo.ComputeClassWriter;
import org.pitest.bytecode.FrameOptions;
import org.pitest.mutationtest.engine.Mutant;
import org.pitest.reloc.asm.ClassVisitor;
import org.pitest.bytecode.NullVisitor;
import org.pitest.reloc.asm.ClassReader;
import org.pitest.functional.F;
import org.pitest.mutationtest.engine.MutationIdentifier;
import org.pitest.functional.Option;
import org.pitest.mutationtest.engine.MutationDetails;
import org.pitest.functional.FunctionalList;
import org.pitest.classinfo.ClassName;
import java.util.HashSet;
import java.util.HashMap;
import java.util.Collection;
import java.util.Set;
import org.pitest.classinfo.ClassByteArraySource;
import org.pitest.functional.predicate.Predicate;
import java.util.Map;
import org.pitest.mutationtest.engine.Mutater;

public class GregorMutater implements Mutater
{
    private final Map<String, String> computeCache;
    private final Predicate<MethodInfo> filter;
    private final ClassByteArraySource byteSource;
    private final Set<MethodMutatorFactory> mutators;
    
    public GregorMutater(final ClassByteArraySource byteSource, final Predicate<MethodInfo> filter, final Collection<MethodMutatorFactory> mutators) {
        this.computeCache = new HashMap<String, String>();
        this.mutators = new HashSet<MethodMutatorFactory>();
        this.filter = filter;
        this.mutators.addAll(mutators);
        this.byteSource = byteSource;
    }
    
    @Override
    public FunctionalList<MutationDetails> findMutations(final ClassName classToMutate) {
        final ClassContext context = new ClassContext();
        context.setTargetMutation((Option<MutationIdentifier>)Option.none());
        return this.byteSource.getBytes(classToMutate.asInternalName()).flatMap((F<byte[], ? extends Iterable<MutationDetails>>)this.findMutations(context));
    }
    
    private F<byte[], Iterable<MutationDetails>> findMutations(final ClassContext context) {
        return new F<byte[], Iterable<MutationDetails>>() {
            @Override
            public Iterable<MutationDetails> apply(final byte[] bytes) {
                return GregorMutater.this.findMutationsForBytes(context, bytes);
            }
        };
    }
    
    private Collection<MutationDetails> findMutationsForBytes(final ClassContext context, final byte[] classToMutate) {
        final ClassReader first = new ClassReader(classToMutate);
        final NullVisitor nv = new NullVisitor();
        final MutatingClassVisitor mca = new MutatingClassVisitor(nv, context, this.filterMethods(), this.mutators);
        first.accept(mca, 8);
        return context.getCollectedMutations();
    }
    
    @Override
    public Mutant getMutation(final MutationIdentifier id) {
        final ClassContext context = new ClassContext();
        context.setTargetMutation(Option.some(id));
        final Option<byte[]> bytes = this.byteSource.getBytes(id.getClassName().asJavaName());
        final ClassReader reader = new ClassReader(bytes.value());
        final ClassWriter w = new ComputeClassWriter(this.byteSource, this.computeCache, FrameOptions.pickFlags(bytes.value()));
        final MutatingClassVisitor mca = new MutatingClassVisitor(w, context, this.filterMethods(), (Collection<MethodMutatorFactory>)FCollection.filter(this.mutators, (F<Object, Boolean>)isMutatorFor(id)));
        reader.accept(mca, 8);
        final List<MutationDetails> details = context.getMutationDetails(context.getTargetMutation().value());
        return new Mutant(details.get(0), w.toByteArray());
    }
    
    private static Predicate<MethodMutatorFactory> isMutatorFor(final MutationIdentifier id) {
        return new Predicate<MethodMutatorFactory>() {
            @Override
            public Boolean apply(final MethodMutatorFactory a) {
                return id.getMutator().equals(a.getGloballyUniqueId());
            }
        };
    }
    
    private Predicate<MethodInfo> filterMethods() {
        return (Predicate<MethodInfo>)Prelude.and(this.filter, filterSyntheticMethods(), Prelude.not((F<Object, Boolean>)isGeneratedEnumMethod()), Prelude.not(isGroovyClass()));
    }
    
    private static F<MethodInfo, Boolean> isGroovyClass() {
        return new Predicate<MethodInfo>() {
            @Override
            public Boolean apply(final MethodInfo a) {
                return a.isInGroovyClass();
            }
        };
    }
    
    private static Predicate<MethodInfo> filterSyntheticMethods() {
        return new Predicate<MethodInfo>() {
            @Override
            public Boolean apply(final MethodInfo a) {
                return !a.isSynthetic() || a.getName().startsWith("lambda$");
            }
        };
    }
    
    private static Predicate<MethodInfo> isGeneratedEnumMethod() {
        return new Predicate<MethodInfo>() {
            @Override
            public Boolean apply(final MethodInfo a) {
                return a.isGeneratedEnumMethod();
            }
        };
    }
}
