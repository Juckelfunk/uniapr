// 
// Decompiled by Procyon v0.5.36
// 

package edu.emory.mathcs.backport.java.util.concurrent;

import java.util.Iterator;
import java.util.List;
import edu.emory.mathcs.backport.java.util.concurrent.helpers.Utils;
import java.util.ArrayList;
import java.util.Collection;

public abstract class AbstractExecutorService implements ExecutorService
{
    protected RunnableFuture newTaskFor(final Runnable runnable, final Object value) {
        return new FutureTask(runnable, value);
    }
    
    protected RunnableFuture newTaskFor(final Callable callable) {
        return new FutureTask(callable);
    }
    
    public Future submit(final Runnable task) {
        if (task == null) {
            throw new NullPointerException();
        }
        final RunnableFuture ftask = this.newTaskFor(task, null);
        this.execute(ftask);
        return ftask;
    }
    
    public Future submit(final Runnable task, final Object result) {
        if (task == null) {
            throw new NullPointerException();
        }
        final RunnableFuture ftask = this.newTaskFor(task, result);
        this.execute(ftask);
        return ftask;
    }
    
    public Future submit(final Callable task) {
        if (task == null) {
            throw new NullPointerException();
        }
        final RunnableFuture ftask = this.newTaskFor(task);
        this.execute(ftask);
        return ftask;
    }
    
    private Object doInvokeAny(final Collection tasks, final boolean timed, long nanos) throws InterruptedException, ExecutionException, TimeoutException {
        if (tasks == null) {
            throw new NullPointerException();
        }
        int ntasks = tasks.size();
        if (ntasks == 0) {
            throw new IllegalArgumentException();
        }
        final List futures = new ArrayList(ntasks);
        final ExecutorCompletionService ecs = new ExecutorCompletionService(this);
        try {
            ExecutionException ee = null;
            long lastTime = timed ? Utils.nanoTime() : 0L;
            final Iterator it = tasks.iterator();
            futures.add(ecs.submit(it.next()));
            --ntasks;
            int active = 1;
            while (true) {
                Future f = ecs.poll();
                if (f == null) {
                    if (ntasks > 0) {
                        --ntasks;
                        futures.add(ecs.submit(it.next()));
                        ++active;
                    }
                    else {
                        if (active == 0) {
                            if (ee == null) {
                                ee = new ExecutionException();
                            }
                            throw ee;
                        }
                        if (timed) {
                            f = ecs.poll(nanos, TimeUnit.NANOSECONDS);
                            if (f == null) {
                                throw new TimeoutException();
                            }
                            final long now = Utils.nanoTime();
                            nanos -= now - lastTime;
                            lastTime = now;
                        }
                        else {
                            f = ecs.take();
                        }
                    }
                }
                if (f != null) {
                    --active;
                    try {
                        return f.get();
                    }
                    catch (InterruptedException ie) {
                        throw ie;
                    }
                    catch (ExecutionException eex) {
                        ee = eex;
                    }
                    catch (RuntimeException rex) {
                        ee = new ExecutionException(rex);
                    }
                }
            }
        }
        finally {
            final Iterator f2 = futures.iterator();
            while (f2.hasNext()) {
                f2.next().cancel(true);
            }
        }
    }
    
    public Object invokeAny(final Collection tasks) throws InterruptedException, ExecutionException {
        try {
            return this.doInvokeAny(tasks, false, 0L);
        }
        catch (TimeoutException cannotHappen) {
            assert false;
            return null;
        }
    }
    
    public Object invokeAny(final Collection tasks, final long timeout, final TimeUnit unit) throws InterruptedException, ExecutionException, TimeoutException {
        return this.doInvokeAny(tasks, true, unit.toNanos(timeout));
    }
    
    public List invokeAll(final Collection tasks) throws InterruptedException {
        if (tasks == null) {
            throw new NullPointerException();
        }
        final List futures = new ArrayList(tasks.size());
        boolean done = false;
        try {
            final Iterator t = tasks.iterator();
            while (t.hasNext()) {
                final RunnableFuture f = this.newTaskFor(t.next());
                futures.add(f);
                this.execute(f);
            }
            for (final Future f2 : futures) {
                if (!f2.isDone()) {
                    try {
                        f2.get();
                    }
                    catch (CancellationException ignore) {}
                    catch (ExecutionException ex) {}
                }
            }
            done = true;
            return futures;
        }
        finally {
            if (!done) {
                for (final Future f3 : futures) {
                    f3.cancel(true);
                }
            }
        }
    }
    
    public List invokeAll(final Collection tasks, final long timeout, final TimeUnit unit) throws InterruptedException {
        if (tasks == null || unit == null) {
            throw new NullPointerException();
        }
        long nanos = unit.toNanos(timeout);
        final List futures = new ArrayList(tasks.size());
        boolean done = false;
        try {
            final Iterator t = tasks.iterator();
            while (t.hasNext()) {
                futures.add(this.newTaskFor(t.next()));
            }
            long lastTime = Utils.nanoTime();
            final Iterator it = futures.iterator();
            while (it.hasNext()) {
                this.execute(it.next());
                final long now = Utils.nanoTime();
                nanos -= now - lastTime;
                lastTime = now;
                if (nanos <= 0L) {
                    return futures;
                }
            }
            for (final Future f : futures) {
                if (!f.isDone()) {
                    if (nanos <= 0L) {
                        return futures;
                    }
                    try {
                        f.get(nanos, TimeUnit.NANOSECONDS);
                    }
                    catch (CancellationException ignore) {}
                    catch (ExecutionException ignore2) {}
                    catch (TimeoutException toe) {
                        return futures;
                    }
                    final long now2 = Utils.nanoTime();
                    nanos -= now2 - lastTime;
                    lastTime = now2;
                }
            }
            done = true;
            return futures;
        }
        finally {
            if (!done) {
                for (final Future f2 : futures) {
                    f2.cancel(true);
                }
            }
        }
    }
}
