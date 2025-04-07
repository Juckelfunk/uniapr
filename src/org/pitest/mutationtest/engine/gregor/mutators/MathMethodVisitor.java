// 
// Decompiled by Procyon v0.5.36
// 

package org.pitest.mutationtest.engine.gregor.mutators;

import org.pitest.mutationtest.engine.gregor.InsnSubstitution;
import java.util.HashMap;
import org.pitest.reloc.asm.MethodVisitor;
import org.pitest.mutationtest.engine.gregor.MutationContext;
import org.pitest.mutationtest.engine.gregor.MethodInfo;
import org.pitest.mutationtest.engine.gregor.MethodMutatorFactory;
import org.pitest.mutationtest.engine.gregor.ZeroOperandMutation;
import java.util.Map;
import org.pitest.mutationtest.engine.gregor.AbstractInsnMutator;

class MathMethodVisitor extends AbstractInsnMutator
{
    private static final Map<Integer, ZeroOperandMutation> MUTATIONS;
    
    MathMethodVisitor(final MethodMutatorFactory factory, final MethodInfo methodInfo, final MutationContext context, final MethodVisitor writer) {
        super(factory, methodInfo, context, writer);
    }
    
    @Override
    protected Map<Integer, ZeroOperandMutation> getMutations() {
        return MathMethodVisitor.MUTATIONS;
    }
    
    static {
        (MUTATIONS = new HashMap<Integer, ZeroOperandMutation>()).put(96, new InsnSubstitution(100, "Replaced integer addition with subtraction"));
        MathMethodVisitor.MUTATIONS.put(100, new InsnSubstitution(96, "Replaced integer subtraction with addition"));
        MathMethodVisitor.MUTATIONS.put(104, new InsnSubstitution(108, "Replaced integer multiplication with division"));
        MathMethodVisitor.MUTATIONS.put(108, new InsnSubstitution(104, "Replaced integer division with multiplication"));
        MathMethodVisitor.MUTATIONS.put(128, new InsnSubstitution(126, "Replaced bitwise OR with AND"));
        MathMethodVisitor.MUTATIONS.put(126, new InsnSubstitution(128, "Replaced bitwise AND with OR"));
        MathMethodVisitor.MUTATIONS.put(112, new InsnSubstitution(104, "Replaced integer modulus with multiplication"));
        MathMethodVisitor.MUTATIONS.put(130, new InsnSubstitution(126, "Replaced XOR with AND"));
        MathMethodVisitor.MUTATIONS.put(120, new InsnSubstitution(122, "Replaced Shift Left with Shift Right"));
        MathMethodVisitor.MUTATIONS.put(122, new InsnSubstitution(120, "Replaced Shift Right with Shift Left"));
        MathMethodVisitor.MUTATIONS.put(124, new InsnSubstitution(120, "Replaced Unsigned Shift Right with Shift Left"));
        MathMethodVisitor.MUTATIONS.put(97, new InsnSubstitution(101, "Replaced long addition with subtraction"));
        MathMethodVisitor.MUTATIONS.put(101, new InsnSubstitution(97, "Replaced long subtraction with addition"));
        MathMethodVisitor.MUTATIONS.put(105, new InsnSubstitution(109, "Replaced long multiplication with division"));
        MathMethodVisitor.MUTATIONS.put(109, new InsnSubstitution(105, "Replaced long division with multiplication"));
        MathMethodVisitor.MUTATIONS.put(129, new InsnSubstitution(127, "Replaced bitwise OR with AND"));
        MathMethodVisitor.MUTATIONS.put(127, new InsnSubstitution(129, "Replaced bitwise AND with OR"));
        MathMethodVisitor.MUTATIONS.put(113, new InsnSubstitution(105, "Replaced long modulus with multiplication"));
        MathMethodVisitor.MUTATIONS.put(131, new InsnSubstitution(127, "Replaced XOR with AND"));
        MathMethodVisitor.MUTATIONS.put(121, new InsnSubstitution(123, "Replaced Shift Left with Shift Right"));
        MathMethodVisitor.MUTATIONS.put(123, new InsnSubstitution(121, "Replaced Shift Right with Shift Left"));
        MathMethodVisitor.MUTATIONS.put(125, new InsnSubstitution(121, "Replaced Unsigned Shift Right with Shift Left"));
        MathMethodVisitor.MUTATIONS.put(98, new InsnSubstitution(102, "Replaced float addition with subtraction"));
        MathMethodVisitor.MUTATIONS.put(102, new InsnSubstitution(98, "Replaced float subtraction with addition"));
        MathMethodVisitor.MUTATIONS.put(106, new InsnSubstitution(110, "Replaced float multiplication with division"));
        MathMethodVisitor.MUTATIONS.put(110, new InsnSubstitution(106, "Replaced float division with multiplication"));
        MathMethodVisitor.MUTATIONS.put(114, new InsnSubstitution(106, "Replaced float modulus with multiplication"));
        MathMethodVisitor.MUTATIONS.put(99, new InsnSubstitution(103, "Replaced double addition with subtraction"));
        MathMethodVisitor.MUTATIONS.put(103, new InsnSubstitution(99, "Replaced double subtraction with addition"));
        MathMethodVisitor.MUTATIONS.put(107, new InsnSubstitution(111, "Replaced double multiplication with division"));
        MathMethodVisitor.MUTATIONS.put(111, new InsnSubstitution(107, "Replaced double division with multiplication"));
        MathMethodVisitor.MUTATIONS.put(115, new InsnSubstitution(107, "Replaced double modulus with multiplication"));
    }
}
