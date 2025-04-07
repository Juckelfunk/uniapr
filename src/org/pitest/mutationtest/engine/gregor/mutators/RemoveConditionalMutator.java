// 
// Decompiled by Procyon v0.5.36
// 

package org.pitest.mutationtest.engine.gregor.mutators;

import org.pitest.mutationtest.engine.MutationIdentifier;
import org.pitest.reloc.asm.Label;
import org.pitest.reloc.asm.MethodVisitor;
import org.pitest.mutationtest.engine.gregor.MethodInfo;
import org.pitest.mutationtest.engine.gregor.MutationContext;
import java.util.List;
import java.util.ArrayList;
import org.pitest.mutationtest.engine.gregor.MethodMutatorFactory;

public class RemoveConditionalMutator implements MethodMutatorFactory
{
    private final Choice kind;
    private final boolean replaceWith;
    
    public RemoveConditionalMutator(final Choice c, final boolean rc) {
        this.kind = c;
        this.replaceWith = rc;
    }
    
    public static Iterable<MethodMutatorFactory> makeMutators() {
        final List<MethodMutatorFactory> variations = new ArrayList<MethodMutatorFactory>();
        final Choice[] allChoices = { Choice.EQUAL, Choice.ORDER };
        final boolean[] arrWith = { true, false };
        for (final Choice c : allChoices) {
            for (final boolean b : arrWith) {
                variations.add(new RemoveConditionalMutator(c, b));
            }
        }
        return variations;
    }
    
    @Override
    public MethodVisitor create(final MutationContext context, final MethodInfo methodInfo, final MethodVisitor methodVisitor) {
        return new RemoveConditionalMethodVisitor(this, context, methodVisitor, "removed conditional - replaced " + this.kind.description() + " check with " + this.replaceWith);
    }
    
    @Override
    public String getGloballyUniqueId() {
        return this.getClass().getName() + "_" + this.kind + "_" + (this.replaceWith ? "IF" : "ELSE");
    }
    
    @Override
    public String getName() {
        return "REMOVE_CONDITIONALS_" + this.kind + "_" + (this.replaceWith ? "IF" : "ELSE") + "_MUTATOR";
    }
    
    public enum Choice
    {
        EQUAL("equality"), 
        ORDER("comparison");
        
        private String desc;
        
        private Choice(final String desc) {
            this.desc = desc;
        }
        
        String description() {
            return this.desc;
        }
    }
    
    private final class RemoveConditionalMethodVisitor extends MethodVisitor
    {
        private final String description;
        private final MutationContext context;
        private final MethodMutatorFactory factory;
        
        RemoveConditionalMethodVisitor(final MethodMutatorFactory factory, final MutationContext context, final MethodVisitor delegateMethodVisitor, final String description) {
            super(393216, delegateMethodVisitor);
            this.context = context;
            this.factory = factory;
            this.description = description;
        }
        
        @Override
        public void visitJumpInsn(final int opcode, final Label label) {
            if (this.canMutate(opcode)) {
                final MutationIdentifier newId = this.context.registerMutation(this.factory, this.description);
                if (this.context.shouldMutate(newId)) {
                    this.emptyStack(opcode);
                    if (!RemoveConditionalMutator.this.replaceWith) {
                        super.visitJumpInsn(167, label);
                    }
                }
                else {
                    this.mv.visitJumpInsn(opcode, label);
                }
            }
            else {
                this.mv.visitJumpInsn(opcode, label);
            }
        }
        
        private void emptyStack(final int opcode) {
            switch (opcode) {
                case 159:
                case 160:
                case 161:
                case 162:
                case 163:
                case 164:
                case 165:
                case 166: {
                    super.visitInsn(88);
                    break;
                }
                default: {
                    super.visitInsn(87);
                    break;
                }
            }
        }
        
        private boolean canMutate(final int opcode) {
            switch (opcode) {
                case 155:
                case 156:
                case 157:
                case 158:
                case 161:
                case 162:
                case 163:
                case 164: {
                    return RemoveConditionalMutator.this.kind == Choice.ORDER;
                }
                case 153:
                case 154:
                case 159:
                case 160:
                case 165:
                case 166:
                case 198:
                case 199: {
                    return RemoveConditionalMutator.this.kind == Choice.EQUAL;
                }
                default: {
                    return false;
                }
            }
        }
    }
}
