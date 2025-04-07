// 
// Decompiled by Procyon v0.5.36
// 

package org.pitest.mutationtest.build;

import org.pitest.util.Log;
import java.util.List;
import java.util.Iterator;
import org.pitest.coverage.TestInfo;
import org.pitest.mutationtest.engine.Mutater;
import org.pitest.bytecode.analysis.ClassTree;
import org.pitest.mutationtest.engine.MutationDetails;
import java.util.Collection;
import org.pitest.classinfo.ClassName;
import org.pitest.classinfo.CachingByteArraySource;
import org.pitest.classinfo.ClassByteArraySource;
import org.pitest.mutationtest.MutationConfig;
import java.util.logging.Logger;

public class MutationSource
{
    private static final Logger LOG;
    private final MutationConfig mutationConfig;
    private final TestPrioritiser testPrioritiser;
    private final ClassByteArraySource source;
    private final MutationInterceptor interceptor;
    
    public MutationSource(final MutationConfig mutationConfig, final TestPrioritiser testPrioritiser, final ClassByteArraySource source, final MutationInterceptor interceptor) {
        this.mutationConfig = mutationConfig;
        this.testPrioritiser = testPrioritiser;
        this.source = new CachingByteArraySource(source, 200);
        this.interceptor = interceptor;
    }
    
    public Collection<MutationDetails> createMutations(final ClassName clazz) {
        final Mutater m = this.mutationConfig.createMutator(this.source);
        final Collection<MutationDetails> availableMutations = m.findMutations(clazz);
        if (availableMutations.isEmpty()) {
            return availableMutations;
        }
        final ClassTree tree = ClassTree.fromBytes(this.source.getBytes(clazz.asJavaName()).value());
        this.interceptor.begin(tree);
        final Collection<MutationDetails> updatedMutations = this.interceptor.intercept(availableMutations, m);
        this.interceptor.end();
        this.assignTestsToMutations(updatedMutations);
        return updatedMutations;
    }
    
    private void assignTestsToMutations(final Collection<MutationDetails> availableMutations) {
        for (final MutationDetails mutation : availableMutations) {
            final List<TestInfo> testDetails = this.testPrioritiser.assignTests(mutation);
            if (testDetails.isEmpty()) {
                MutationSource.LOG.fine("According to coverage no tests hit the mutation " + mutation);
            }
            mutation.addTestsInOrder(testDetails);
        }
    }
    
    static {
        LOG = Log.getLogger();
    }
}
