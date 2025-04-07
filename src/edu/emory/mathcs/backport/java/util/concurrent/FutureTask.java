// 
// Decompiled by Procyon v0.5.36
// 

package edu.emory.mathcs.backport.java.util.concurrent;

import edu.emory.mathcs.backport.java.util.concurrent.helpers.Utils;

public class FutureTask implements RunnableFuture
{
    private static final int READY = 0;
    private static final int RUNNING = 1;
    private static final int RAN = 2;
    private static final int CANCELLED = 4;
    private final Callable callable;
    private Object result;
    private Throwable exception;
    private int state;
    private volatile Thread runner;
    
    public FutureTask(final Callable callable) {
        if (callable == null) {
            throw new NullPointerException();
        }
        this.callable = callable;
    }
    
    public FutureTask(final Runnable runnable, final Object result) {
        this(Executors.callable(runnable, result));
    }
    
    public synchronized boolean isCancelled() {
        return this.state == 4;
    }
    
    public synchronized boolean isDone() {
        return this.ranOrCancelled() && this.runner == null;
    }
    
    public boolean cancel(final boolean mayInterruptIfRunning) {
        synchronized (this) {
            if (this.ranOrCancelled()) {
                return false;
            }
            this.state = 4;
            if (mayInterruptIfRunning) {
                final Thread r = this.runner;
                if (r != null) {
                    r.interrupt();
                }
            }
            this.runner = null;
            this.notifyAll();
        }
        this.done();
        return true;
    }
    
    public synchronized Object get() throws InterruptedException, ExecutionException {
        this.waitFor();
        return this.getResult();
    }
    
    public synchronized Object get(final long timeout, final TimeUnit unit) throws InterruptedException, ExecutionException, TimeoutException {
        this.waitFor(unit.toNanos(timeout));
        return this.getResult();
    }
    
    protected void done() {
    }
    
    protected void set(final Object v) {
        this.setCompleted(v);
    }
    
    protected void setException(final Throwable t) {
        this.setFailed(t);
    }
    
    public void run() {
        synchronized (this) {
            if (this.state != 0) {
                return;
            }
            this.state = 1;
            this.runner = Thread.currentThread();
        }
        try {
            this.set(this.callable.call());
        }
        catch (Throwable ex) {
            this.setException(ex);
        }
    }
    
    protected boolean runAndReset() {
        synchronized (this) {
            if (this.state != 0) {
                return false;
            }
            this.state = 1;
            this.runner = Thread.currentThread();
        }
        try {
            this.callable.call();
            synchronized (this) {
                this.runner = null;
                if (this.state == 1) {
                    this.state = 0;
                    return true;
                }
                return false;
            }
        }
        catch (Throwable ex) {
            this.setException(ex);
            return false;
        }
    }
    
    private boolean ranOrCancelled() {
        return (this.state & 0x6) != 0x0;
    }
    
    private void setCompleted(final Object result) {
        synchronized (this) {
            if (this.ranOrCancelled()) {
                return;
            }
            this.state = 2;
            this.result = result;
            this.runner = null;
            this.notifyAll();
        }
        this.done();
    }
    
    private void setFailed(final Throwable exception) {
        synchronized (this) {
            if (this.ranOrCancelled()) {
                return;
            }
            this.state = 2;
            this.exception = exception;
            this.runner = null;
            this.notifyAll();
        }
        this.done();
    }
    
    private void waitFor() throws InterruptedException {
        while (!this.isDone()) {
            this.wait();
        }
    }
    
    private void waitFor(long nanos) throws InterruptedException, TimeoutException {
        if (nanos < 0L) {
            throw new IllegalArgumentException();
        }
        if (this.isDone()) {
            return;
        }
        for (long deadline = Utils.nanoTime() + nanos; nanos > 0L; nanos = deadline - Utils.nanoTime()) {
            TimeUnit.NANOSECONDS.timedWait(this, nanos);
            if (this.isDone()) {
                return;
            }
        }
        throw new TimeoutException();
    }
    
    private Object getResult() throws ExecutionException {
        if (this.state == 4) {
            throw new CancellationException();
        }
        if (this.exception != null) {
            throw new ExecutionException(this.exception);
        }
        return this.result;
    }
}
