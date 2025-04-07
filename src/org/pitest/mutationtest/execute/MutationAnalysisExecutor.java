// 
// Decompiled by Procyon v0.5.36
// 

package org.pitest.mutationtest.execute;

import org.pitest.util.Log;
import org.pitest.functional.FCollection;
import org.pitest.functional.SideEffect1;
import org.pitest.mutationtest.ClassMutationResults;
import java.util.Iterator;
import java.util.concurrent.ExecutionException;
import org.pitest.util.Unchecked;
import java.util.concurrent.Callable;
import org.pitest.mutationtest.MutationMetaData;
import java.util.concurrent.Future;
import java.util.ArrayList;
import org.pitest.mutationtest.build.MutationAnalysisUnit;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.Executors;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.ThreadPoolExecutor;
import org.pitest.mutationtest.MutationResultListener;
import java.util.List;
import java.util.logging.Logger;

public class MutationAnalysisExecutor
{
    private static final Logger LOG;
    private final List<MutationResultListener> listeners;
    private final ThreadPoolExecutor executor;
    
    public MutationAnalysisExecutor(final int numberOfThreads, final List<MutationResultListener> listeners) {
        this.listeners = listeners;
        this.executor = new ThreadPoolExecutor(numberOfThreads, numberOfThreads, 10L, TimeUnit.SECONDS, new LinkedBlockingQueue<Runnable>(), Executors.defaultThreadFactory());
    }
    
    public void run(final List<MutationAnalysisUnit> testUnits) {
        MutationAnalysisExecutor.LOG.fine("Running " + testUnits.size() + " units");
        this.signalRunStartToAllListeners();
        final List<Future<MutationMetaData>> results = new ArrayList<Future<MutationMetaData>>(testUnits.size());
        for (final MutationAnalysisUnit unit : testUnits) {
            results.add(this.executor.submit((Callable<MutationMetaData>)unit));
        }
        this.executor.shutdown();
        try {
            this.processResult(results);
        }
        catch (InterruptedException e) {
            throw Unchecked.translateCheckedException(e);
        }
        catch (ExecutionException e2) {
            throw Unchecked.translateCheckedException(e2);
        }
        this.signalRunEndToAllListeners();
    }
    
    private void processResult(final List<Future<MutationMetaData>> results) throws InterruptedException, ExecutionException {
        for (final Future<MutationMetaData> f : results) {
            final MutationMetaData r = f.get();
            for (final MutationResultListener l : this.listeners) {
                for (final ClassMutationResults cr : r.toClassResults()) {
                    l.handleMutationResult(cr);
                }
            }
        }
    }
    
    private void signalRunStartToAllListeners() {
        FCollection.forEach(this.listeners, (SideEffect1<Object>)new SideEffect1<MutationResultListener>() {
            @Override
            public void apply(final MutationResultListener a) {
                a.runStart();
            }
        });
    }
    
    private void signalRunEndToAllListeners() {
        FCollection.forEach(this.listeners, (SideEffect1<Object>)new SideEffect1<MutationResultListener>() {
            @Override
            public void apply(final MutationResultListener a) {
                a.runEnd();
            }
        });
    }
    
    static {
        LOG = Log.getLogger();
    }
}
