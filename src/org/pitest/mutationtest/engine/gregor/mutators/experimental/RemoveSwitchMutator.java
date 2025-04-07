// 
// Decompiled by Procyon v0.5.36
// 

package org.pitest.mutationtest.engine.gregor.mutators.experimental;

import org.pitest.mutationtest.engine.MutationIdentifier;
import org.pitest.reloc.asm.Label;
import org.pitest.reloc.asm.MethodVisitor;
import org.pitest.mutationtest.engine.gregor.MethodInfo;
import org.pitest.mutationtest.engine.gregor.MutationContext;
import java.util.List;
import java.util.ArrayList;
import org.pitest.mutationtest.engine.gregor.MethodMutatorFactory;

public class RemoveSwitchMutator implements MethodMutatorFactory
{
    private final int key;
    
    public RemoveSwitchMutator(final int i) {
        this.key = i;
    }
    
    public static Iterable<MethodMutatorFactory> makeMutators() {
        final List<MethodMutatorFactory> variations = new ArrayList<MethodMutatorFactory>();
        for (int i = 0; i != 100; ++i) {
            variations.add(new RemoveSwitchMutator(i));
        }
        return variations;
    }
    
    @Override
    public MethodVisitor create(final MutationContext context, final MethodInfo methodInfo, final MethodVisitor methodVisitor) {
        return new RemoveSwitchMethodVisitor(context, methodVisitor);
    }
    
    @Override
    public String getGloballyUniqueId() {
        return this.getClass().getName() + "_" + this.key;
    }
    
    @Override
    public String getName() {
        return this.toString();
    }
    
    @Override
    public String toString() {
        return "EXPERIMENTAL_REMOVE_SWITCH_MUTATOR_" + this.key;
    }
    
    private final class RemoveSwitchMethodVisitor extends MethodVisitor
    {
        private final MutationContext context;
        
        RemoveSwitchMethodVisitor(final MutationContext context, final MethodVisitor methodVisitor) {
            super(393216, methodVisitor);
            this.context = context;
        }
        
        @Override
        public void visitTableSwitchInsn(final int i, final int i1, final Label defaultLabel, final Label... labels) {
            if (labels.length > RemoveSwitchMutator.this.key && this.shouldMutate()) {
                final Label[] newLabels = labels.clone();
                super.visitTableSwitchInsn(i, i1, newLabels[RemoveSwitchMutator.this.key] = defaultLabel, newLabels);
            }
            else {
                super.visitTableSwitchInsn(i, i1, defaultLabel, labels);
            }
        }
        
        @Override
        public void visitLookupSwitchInsn(final Label defaultLabel, final int[] ints, final Label[] labels) {
            if (labels.length > RemoveSwitchMutator.this.key && this.shouldMutate()) {
                final Label[] newLabels = labels.clone();
                super.visitLookupSwitchInsn(newLabels[RemoveSwitchMutator.this.key] = defaultLabel, ints, newLabels);
            }
            else {
                super.visitLookupSwitchInsn(defaultLabel, ints, labels);
            }
        }
        
        private boolean shouldMutate() {
            final MutationIdentifier mutationId = this.context.registerMutation(RemoveSwitchMutator.this, "RemoveSwitch " + RemoveSwitchMutator.this.key + " mutation");
            return this.context.shouldMutate(mutationId);
        }
    }
}
