// 
// Decompiled by Procyon v0.5.36
// 

package org.pitest.mutationtest.engine.gregor.config;

import org.pitest.mutationtest.engine.gregor.mutators.experimental.RemoveSwitchMutator;
import org.pitest.mutationtest.engine.gregor.mutators.experimental.NakedReceiverMutator;
import org.pitest.mutationtest.engine.gregor.mutators.ArgumentPropagationMutator;
import org.pitest.mutationtest.engine.gregor.mutators.experimental.MemberVariableMutator;
import org.pitest.mutationtest.engine.gregor.mutators.ConstructorCallMutator;
import org.pitest.mutationtest.engine.gregor.mutators.NonVoidMethodCallMutator;
import org.pitest.mutationtest.engine.gregor.mutators.experimental.RemoveIncrementsMutator;
import org.pitest.mutationtest.engine.gregor.mutators.InlineConstantMutator;
import java.util.LinkedHashMap;
import org.pitest.help.PitHelpError;
import org.pitest.help.Help;
import java.util.Comparator;
import java.util.Set;
import org.pitest.functional.F;
import java.util.TreeSet;
import java.util.Collections;
import org.pitest.functional.FCollection;
import org.pitest.functional.prelude.Prelude;
import java.util.Arrays;
import org.pitest.mutationtest.engine.gregor.mutators.NullReturnValsMutator;
import org.pitest.mutationtest.engine.gregor.mutators.EmptyObjectReturnValsMutator;
import org.pitest.mutationtest.engine.gregor.mutators.PrimitiveReturnsMutator;
import org.pitest.mutationtest.engine.gregor.mutators.BooleanFalseReturnValsMutator;
import org.pitest.mutationtest.engine.gregor.mutators.BooleanTrueReturnValsMutator;
import org.pitest.mutationtest.engine.gregor.mutators.IncrementsMutator;
import org.pitest.mutationtest.engine.gregor.mutators.ConditionalsBoundaryMutator;
import org.pitest.mutationtest.engine.gregor.mutators.NegateConditionalsMutator;
import org.pitest.mutationtest.engine.gregor.mutators.VoidMethodCallMutator;
import org.pitest.mutationtest.engine.gregor.mutators.MathMutator;
import org.pitest.mutationtest.engine.gregor.mutators.ReturnValsMutator;
import org.pitest.mutationtest.engine.gregor.mutators.InvertNegsMutator;
import java.util.List;
import java.util.ArrayList;
import org.pitest.mutationtest.engine.gregor.mutators.experimental.SwitchMutator;
import org.pitest.mutationtest.engine.gregor.mutators.RemoveConditionalMutator;
import java.util.Collection;
import org.pitest.mutationtest.engine.gregor.MethodMutatorFactory;
import java.util.Map;

public final class Mutator
{
    private static final Map<String, Iterable<MethodMutatorFactory>> MUTATORS;
    
    public static Collection<MethodMutatorFactory> all() {
        return fromStrings(Mutator.MUTATORS.keySet());
    }
    
    private static Collection<MethodMutatorFactory> stronger() {
        return combine(defaults(), group(new RemoveConditionalMutator(RemoveConditionalMutator.Choice.EQUAL, false), new SwitchMutator()));
    }
    
    private static Collection<MethodMutatorFactory> combine(final Collection<MethodMutatorFactory> a, final Collection<MethodMutatorFactory> b) {
        final List<MethodMutatorFactory> l = new ArrayList<MethodMutatorFactory>(a);
        l.addAll(b);
        return l;
    }
    
    public static Collection<MethodMutatorFactory> defaults() {
        return group(InvertNegsMutator.INVERT_NEGS_MUTATOR, ReturnValsMutator.RETURN_VALS_MUTATOR, MathMutator.MATH_MUTATOR, VoidMethodCallMutator.VOID_METHOD_CALL_MUTATOR, NegateConditionalsMutator.NEGATE_CONDITIONALS_MUTATOR, ConditionalsBoundaryMutator.CONDITIONALS_BOUNDARY_MUTATOR, IncrementsMutator.INCREMENTS_MUTATOR);
    }
    
    public static Collection<MethodMutatorFactory> newDefaults() {
        return combine(group(InvertNegsMutator.INVERT_NEGS_MUTATOR, MathMutator.MATH_MUTATOR, VoidMethodCallMutator.VOID_METHOD_CALL_MUTATOR, NegateConditionalsMutator.NEGATE_CONDITIONALS_MUTATOR, ConditionalsBoundaryMutator.CONDITIONALS_BOUNDARY_MUTATOR, IncrementsMutator.INCREMENTS_MUTATOR), betterReturns());
    }
    
    public static Collection<MethodMutatorFactory> betterReturns() {
        return group(BooleanTrueReturnValsMutator.BOOLEAN_TRUE_RETURN, BooleanFalseReturnValsMutator.BOOLEAN_FALSE_RETURN, PrimitiveReturnsMutator.PRIMITIVE_RETURN_VALS_MUTATOR, EmptyObjectReturnValsMutator.EMPTY_RETURN_VALUES, NullReturnValsMutator.NULL_RETURN_VALUES);
    }
    
    private static Collection<MethodMutatorFactory> group(final MethodMutatorFactory... ms) {
        return Arrays.asList(ms);
    }
    
    public static Collection<MethodMutatorFactory> byName(final String name) {
        return FCollection.map(Mutator.MUTATORS.get(name), Prelude.id(MethodMutatorFactory.class));
    }
    
    private static void add(final String key, final MethodMutatorFactory value) {
        Mutator.MUTATORS.put(key, Collections.singleton(value));
    }
    
    private static void addGroup(final String key, final Iterable<MethodMutatorFactory> value) {
        Mutator.MUTATORS.put(key, value);
    }
    
    public static Collection<MethodMutatorFactory> fromStrings(final Collection<String> names) {
        final Set<MethodMutatorFactory> unique = new TreeSet<MethodMutatorFactory>(compareId());
        FCollection.flatMapTo((Iterable<? extends String>)names, (F<String, ? extends Iterable<Object>>)fromString(), (Collection<? super Object>)unique);
        return unique;
    }
    
    private static Comparator<? super MethodMutatorFactory> compareId() {
        return new Comparator<MethodMutatorFactory>() {
            @Override
            public int compare(final MethodMutatorFactory o1, final MethodMutatorFactory o2) {
                return o1.getGloballyUniqueId().compareTo(o2.getGloballyUniqueId());
            }
        };
    }
    
    private static F<String, Iterable<MethodMutatorFactory>> fromString() {
        return new F<String, Iterable<MethodMutatorFactory>>() {
            @Override
            public Iterable<MethodMutatorFactory> apply(final String a) {
                final Iterable<MethodMutatorFactory> i = Mutator.MUTATORS.get(a);
                if (i == null) {
                    throw new PitHelpError(Help.UNKNOWN_MUTATOR, new Object[] { a });
                }
                return i;
            }
        };
    }
    
    static {
        MUTATORS = new LinkedHashMap<String, Iterable<MethodMutatorFactory>>();
        add("INVERT_NEGS", InvertNegsMutator.INVERT_NEGS_MUTATOR);
        add("RETURN_VALS", ReturnValsMutator.RETURN_VALS_MUTATOR);
        add("INLINE_CONSTS", new InlineConstantMutator());
        add("MATH", MathMutator.MATH_MUTATOR);
        add("VOID_METHOD_CALLS", VoidMethodCallMutator.VOID_METHOD_CALL_MUTATOR);
        add("NEGATE_CONDITIONALS", NegateConditionalsMutator.NEGATE_CONDITIONALS_MUTATOR);
        add("CONDITIONALS_BOUNDARY", ConditionalsBoundaryMutator.CONDITIONALS_BOUNDARY_MUTATOR);
        add("INCREMENTS", IncrementsMutator.INCREMENTS_MUTATOR);
        add("REMOVE_INCREMENTS", RemoveIncrementsMutator.REMOVE_INCREMENTS_MUTATOR);
        add("NON_VOID_METHOD_CALLS", NonVoidMethodCallMutator.NON_VOID_METHOD_CALL_MUTATOR);
        add("CONSTRUCTOR_CALLS", ConstructorCallMutator.CONSTRUCTOR_CALL_MUTATOR);
        add("REMOVE_CONDITIONALS_EQ_IF", new RemoveConditionalMutator(RemoveConditionalMutator.Choice.EQUAL, true));
        add("REMOVE_CONDITIONALS_EQ_ELSE", new RemoveConditionalMutator(RemoveConditionalMutator.Choice.EQUAL, false));
        add("REMOVE_CONDITIONALS_ORD_IF", new RemoveConditionalMutator(RemoveConditionalMutator.Choice.ORDER, true));
        add("REMOVE_CONDITIONALS_ORD_ELSE", new RemoveConditionalMutator(RemoveConditionalMutator.Choice.ORDER, false));
        addGroup("REMOVE_CONDITIONALS", RemoveConditionalMutator.makeMutators());
        add("TRUE_RETURNS", BooleanTrueReturnValsMutator.BOOLEAN_TRUE_RETURN);
        add("FALSE_RETURNS", BooleanFalseReturnValsMutator.BOOLEAN_FALSE_RETURN);
        add("PRIMITIVE_RETURNS", PrimitiveReturnsMutator.PRIMITIVE_RETURN_VALS_MUTATOR);
        add("EMPTY_RETURNS", EmptyObjectReturnValsMutator.EMPTY_RETURN_VALUES);
        add("NULL_RETURNS", NullReturnValsMutator.NULL_RETURN_VALUES);
        addGroup("RETURNS", betterReturns());
        add("EXPERIMENTAL_MEMBER_VARIABLE", new MemberVariableMutator());
        add("EXPERIMENTAL_SWITCH", new SwitchMutator());
        add("EXPERIMENTAL_ARGUMENT_PROPAGATION", ArgumentPropagationMutator.ARGUMENT_PROPAGATION_MUTATOR);
        add("EXPERIMENTAL_NAKED_RECEIVER", NakedReceiverMutator.NAKED_RECEIVER);
        addGroup("REMOVE_SWITCH", RemoveSwitchMutator.makeMutators());
        addGroup("DEFAULTS", defaults());
        addGroup("STRONGER", stronger());
        addGroup("ALL", all());
        addGroup("NEW_DEFAULTS", newDefaults());
    }
}
