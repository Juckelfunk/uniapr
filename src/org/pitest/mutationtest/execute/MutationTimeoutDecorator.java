// 
// Decompiled by Procyon v0.5.36
// 

package org.pitest.mutationtest.execute;

import java.util.concurrent.ExecutionException;
import org.pitest.util.Unchecked;
import java.util.concurrent.TimeoutException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.FutureTask;
import org.pitest.testapi.ResultCollector;
import org.pitest.testapi.TestUnit;
import org.pitest.functional.SideEffect;
import org.pitest.mutationtest.TimeoutLengthStrategy;
import org.pitest.extension.common.TestUnitDecorator;

public final class MutationTimeoutDecorator extends TestUnitDecorator
{
    private final TimeoutLengthStrategy timeOutStrategy;
    private final SideEffect timeOutSideEffect;
    private final long executionTime;
    
    public MutationTimeoutDecorator(final TestUnit child, final SideEffect timeOutSideEffect, final TimeoutLengthStrategy timeStrategy, final long executionTime) {
        super(child);
        this.timeOutSideEffect = timeOutSideEffect;
        this.executionTime = executionTime;
        this.timeOutStrategy = timeStrategy;
    }
    
    @Override
    public void execute(final ResultCollector rc) {
        final long maxTime = this.timeOutStrategy.getAllowedTime(this.executionTime);
        final FutureTask<?> future = this.createFutureForChildTestUnit(rc);
        this.executeFutureWithTimeOut(maxTime, future, rc);
        if (!future.isDone()) {
            this.timeOutSideEffect.apply();
        }
    }
    
    private void executeFutureWithTimeOut(final long maxTime, final FutureTask<?> future, final ResultCollector rc) {
        try {
            future.get(maxTime, TimeUnit.MILLISECONDS);
        }
        catch (TimeoutException ex) {}
        catch (InterruptedException ex2) {}
        catch (ExecutionException e) {
            throw Unchecked.translateCheckedException(e);
        }
    }
    
    private FutureTask<?> createFutureForChildTestUnit(final ResultCollector rc) {
        final FutureTask<?> future = new FutureTask<Object>(this.createRunnable(rc), null);
        final Thread thread = new Thread(future);
        thread.setDaemon(true);
        thread.setName("mutationTestThread");
        thread.start();
        return future;
    }
    
    private Runnable createRunnable(final ResultCollector rc) {
        return new Runnable() {
            @Override
            public void run() {
                try {
                    MutationTimeoutDecorator.this.child().execute(rc);
                }
                catch (Throwable ex) {
                    rc.notifyEnd(MutationTimeoutDecorator.this.child().getDescription(), ex);
                }
            }
        };
    }
}
