// 
// Decompiled by Procyon v0.5.36
// 

package org.pitest.mutationtest.engine.gregor;

import java.util.Collection;
import org.pitest.functional.F;
import org.pitest.functional.FCollection;
import java.util.ArrayList;
import org.pitest.mutationtest.engine.gregor.blocks.ConcreteBlockCounter;
import org.pitest.mutationtest.engine.MutationDetails;
import java.util.List;
import org.pitest.mutationtest.engine.MutationIdentifier;
import org.pitest.functional.Option;
import org.pitest.mutationtest.engine.gregor.blocks.BlockCounter;

class ClassContext implements BlockCounter
{
    private ClassInfo classInfo;
    private String sourceFile;
    private Option<MutationIdentifier> target;
    private final List<MutationDetails> mutations;
    private final ConcreteBlockCounter blockCounter;
    
    ClassContext() {
        this.target = (Option<MutationIdentifier>)Option.none();
        this.mutations = new ArrayList<MutationDetails>();
        this.blockCounter = new ConcreteBlockCounter();
    }
    
    public Option<MutationIdentifier> getTargetMutation() {
        return this.target;
    }
    
    public ClassInfo getClassInfo() {
        return this.classInfo;
    }
    
    public String getJavaClassName() {
        return this.classInfo.getName().replace("/", ".");
    }
    
    public String getFileName() {
        return this.sourceFile;
    }
    
    public void setTargetMutation(final Option<MutationIdentifier> target) {
        this.target = target;
    }
    
    public List<MutationDetails> getMutationDetails(final MutationIdentifier id) {
        return FCollection.filter(this.mutations, hasId(id));
    }
    
    private static F<MutationDetails, Boolean> hasId(final MutationIdentifier id) {
        return new F<MutationDetails, Boolean>() {
            @Override
            public Boolean apply(final MutationDetails a) {
                return a.matchesId(id);
            }
        };
    }
    
    public void registerClass(final ClassInfo classInfo) {
        this.classInfo = classInfo;
    }
    
    public void registerSourceFile(final String source) {
        this.sourceFile = source;
    }
    
    public boolean shouldMutate(final MutationIdentifier newId) {
        return this.getTargetMutation().contains(idMatches(newId));
    }
    
    private static F<MutationIdentifier, Boolean> idMatches(final MutationIdentifier newId) {
        return new F<MutationIdentifier, Boolean>() {
            @Override
            public Boolean apply(final MutationIdentifier a) {
                return a.matches(newId);
            }
        };
    }
    
    public Collection<MutationDetails> getCollectedMutations() {
        return this.mutations;
    }
    
    public void addMutation(final MutationDetails details) {
        this.mutations.add(details);
    }
    
    @Override
    public void registerNewBlock() {
        this.blockCounter.registerNewBlock();
    }
    
    @Override
    public void registerFinallyBlockStart() {
        this.blockCounter.registerFinallyBlockStart();
    }
    
    @Override
    public void registerFinallyBlockEnd() {
        this.blockCounter.registerFinallyBlockEnd();
    }
    
    public int getCurrentBlock() {
        return this.blockCounter.getCurrentBlock();
    }
    
    public boolean isWithinFinallyBlock() {
        return this.blockCounter.isWithinFinallyBlock();
    }
}
