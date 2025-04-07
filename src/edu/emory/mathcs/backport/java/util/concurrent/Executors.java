// 
// Decompiled by Procyon v0.5.36
// 

package edu.emory.mathcs.backport.java.util.concurrent;

import java.util.Collection;
import java.util.List;
import edu.emory.mathcs.backport.java.util.concurrent.atomic.AtomicInteger;
import java.security.Permission;
import java.security.AccessController;
import java.security.AccessControlContext;
import java.security.PrivilegedExceptionAction;
import java.security.PrivilegedAction;

public class Executors
{
    public static ExecutorService newFixedThreadPool(final int nThreads) {
        return new ThreadPoolExecutor(nThreads, nThreads, 0L, TimeUnit.MILLISECONDS, new LinkedBlockingQueue());
    }
    
    public static ExecutorService newFixedThreadPool(final int nThreads, final ThreadFactory threadFactory) {
        return new ThreadPoolExecutor(nThreads, nThreads, 0L, TimeUnit.MILLISECONDS, new LinkedBlockingQueue(), threadFactory);
    }
    
    public static ExecutorService newSingleThreadExecutor() {
        return new FinalizableDelegatedExecutorService(new ThreadPoolExecutor(1, 1, 0L, TimeUnit.MILLISECONDS, new LinkedBlockingQueue()));
    }
    
    public static ExecutorService newSingleThreadExecutor(final ThreadFactory threadFactory) {
        return new FinalizableDelegatedExecutorService(new ThreadPoolExecutor(1, 1, 0L, TimeUnit.MILLISECONDS, new LinkedBlockingQueue(), threadFactory));
    }
    
    public static ExecutorService newCachedThreadPool() {
        return new ThreadPoolExecutor(0, Integer.MAX_VALUE, 60L, TimeUnit.SECONDS, new SynchronousQueue());
    }
    
    public static ExecutorService newCachedThreadPool(final ThreadFactory threadFactory) {
        return new ThreadPoolExecutor(0, Integer.MAX_VALUE, 60L, TimeUnit.SECONDS, new SynchronousQueue(), threadFactory);
    }
    
    public static ScheduledExecutorService newSingleThreadScheduledExecutor() {
        return new DelegatedScheduledExecutorService(new ScheduledThreadPoolExecutor(1));
    }
    
    public static ScheduledExecutorService newSingleThreadScheduledExecutor(final ThreadFactory threadFactory) {
        return new DelegatedScheduledExecutorService(new ScheduledThreadPoolExecutor(1, threadFactory));
    }
    
    public static ScheduledExecutorService newScheduledThreadPool(final int corePoolSize) {
        return new ScheduledThreadPoolExecutor(corePoolSize);
    }
    
    public static ScheduledExecutorService newScheduledThreadPool(final int corePoolSize, final ThreadFactory threadFactory) {
        return new ScheduledThreadPoolExecutor(corePoolSize, threadFactory);
    }
    
    public static ExecutorService unconfigurableExecutorService(final ExecutorService executor) {
        if (executor == null) {
            throw new NullPointerException();
        }
        return new DelegatedExecutorService(executor);
    }
    
    public static ScheduledExecutorService unconfigurableScheduledExecutorService(final ScheduledExecutorService executor) {
        if (executor == null) {
            throw new NullPointerException();
        }
        return new DelegatedScheduledExecutorService(executor);
    }
    
    public static ThreadFactory defaultThreadFactory() {
        return new DefaultThreadFactory();
    }
    
    public static ThreadFactory privilegedThreadFactory() {
        return new PrivilegedThreadFactory();
    }
    
    public static Callable callable(final Runnable task, final Object result) {
        if (task == null) {
            throw new NullPointerException();
        }
        return new RunnableAdapter(task, result);
    }
    
    public static Callable callable(final Runnable task) {
        if (task == null) {
            throw new NullPointerException();
        }
        return new RunnableAdapter(task, null);
    }
    
    public static Callable callable(final PrivilegedAction action) {
        if (action == null) {
            throw new NullPointerException();
        }
        return new Callable() {
            public Object call() {
                return action.run();
            }
        };
    }
    
    public static Callable callable(final PrivilegedExceptionAction action) {
        if (action == null) {
            throw new NullPointerException();
        }
        return new Callable() {
            public Object call() throws Exception {
                return action.run();
            }
        };
    }
    
    public static Callable privilegedCallable(final Callable callable) {
        if (callable == null) {
            throw new NullPointerException();
        }
        return new PrivilegedCallable(callable);
    }
    
    public static Callable privilegedCallableUsingCurrentClassLoader(final Callable callable) {
        if (callable == null) {
            throw new NullPointerException();
        }
        return new PrivilegedCallableUsingCurrentClassLoader(callable);
    }
    
    private Executors() {
    }
    
    static final class RunnableAdapter implements Callable
    {
        final Runnable task;
        final Object result;
        
        RunnableAdapter(final Runnable task, final Object result) {
            this.task = task;
            this.result = result;
        }
        
        public Object call() {
            this.task.run();
            return this.result;
        }
    }
    
    static final class PrivilegedCallable implements Callable
    {
        private final AccessControlContext acc;
        private final Callable task;
        private Object result;
        private Exception exception;
        
        PrivilegedCallable(final Callable task) {
            this.task = task;
            this.acc = AccessController.getContext();
        }
        
        public Object call() throws Exception {
            AccessController.doPrivileged((PrivilegedAction<Object>)new PrivilegedAction() {
                private final /* synthetic */ PrivilegedCallable this$0;
                
                public Object run() {
                    try {
                        this.this$0.result = this.this$0.task.call();
                    }
                    catch (Exception ex) {
                        this.this$0.exception = ex;
                    }
                    return null;
                }
            }, this.acc);
            if (this.exception != null) {
                throw this.exception;
            }
            return this.result;
        }
    }
    
    static final class PrivilegedCallableUsingCurrentClassLoader implements Callable
    {
        private final ClassLoader ccl;
        private final AccessControlContext acc;
        private final Callable task;
        private Object result;
        private Exception exception;
        
        PrivilegedCallableUsingCurrentClassLoader(final Callable task) {
            this.task = task;
            this.ccl = Thread.currentThread().getContextClassLoader();
            (this.acc = AccessController.getContext()).checkPermission(new RuntimePermission("getContextClassLoader"));
            this.acc.checkPermission(new RuntimePermission("setContextClassLoader"));
        }
        
        public Object call() throws Exception {
            AccessController.doPrivileged((PrivilegedAction<Object>)new PrivilegedAction() {
                private final /* synthetic */ PrivilegedCallableUsingCurrentClassLoader this$0;
                
                public Object run() {
                    ClassLoader savedcl = null;
                    final Thread t = Thread.currentThread();
                    try {
                        final ClassLoader cl = t.getContextClassLoader();
                        if (this.this$0.ccl != cl) {
                            t.setContextClassLoader(this.this$0.ccl);
                            savedcl = cl;
                        }
                        this.this$0.result = this.this$0.task.call();
                    }
                    catch (Exception ex) {
                        this.this$0.exception = ex;
                    }
                    finally {
                        if (savedcl != null) {
                            t.setContextClassLoader(savedcl);
                        }
                    }
                    return null;
                }
            }, this.acc);
            if (this.exception != null) {
                throw this.exception;
            }
            return this.result;
        }
    }
    
    static class DefaultThreadFactory implements ThreadFactory
    {
        static final AtomicInteger poolNumber;
        final ThreadGroup group;
        final AtomicInteger threadNumber;
        final String namePrefix;
        
        DefaultThreadFactory() {
            this.threadNumber = new AtomicInteger(1);
            final SecurityManager s = System.getSecurityManager();
            this.group = ((s != null) ? s.getThreadGroup() : Thread.currentThread().getThreadGroup());
            this.namePrefix = "pool-" + DefaultThreadFactory.poolNumber.getAndIncrement() + "-thread-";
        }
        
        public Thread newThread(final Runnable r) {
            final Thread t = new Thread(this.group, r, this.namePrefix + this.threadNumber.getAndIncrement(), 0L);
            if (t.isDaemon()) {
                t.setDaemon(false);
            }
            if (t.getPriority() != 5) {
                t.setPriority(5);
            }
            return t;
        }
        
        static {
            poolNumber = new AtomicInteger(1);
        }
    }
    
    static class PrivilegedThreadFactory extends DefaultThreadFactory
    {
        private final ClassLoader ccl;
        private final AccessControlContext acc;
        
        PrivilegedThreadFactory() {
            this.ccl = Thread.currentThread().getContextClassLoader();
            (this.acc = AccessController.getContext()).checkPermission(new RuntimePermission("setContextClassLoader"));
        }
        
        public Thread newThread(final Runnable r) {
            return super.newThread(new Runnable() {
                private final /* synthetic */ PrivilegedThreadFactory this$0;
                
                public void run() {
                    AccessController.doPrivileged((PrivilegedAction<Object>)new PrivilegedAction() {
                        private final /* synthetic */ Executors$5 this$1;
                        
                        public Object run() {
                            Thread.currentThread().setContextClassLoader(this.this$1.this$0.ccl);
                            r.run();
                            return null;
                        }
                    }, this.this$0.acc);
                }
            });
        }
    }
    
    static class DelegatedExecutorService extends AbstractExecutorService
    {
        private final ExecutorService e;
        
        DelegatedExecutorService(final ExecutorService executor) {
            this.e = executor;
        }
        
        public void execute(final Runnable command) {
            this.e.execute(command);
        }
        
        public void shutdown() {
            this.e.shutdown();
        }
        
        public List shutdownNow() {
            return this.e.shutdownNow();
        }
        
        public boolean isShutdown() {
            return this.e.isShutdown();
        }
        
        public boolean isTerminated() {
            return this.e.isTerminated();
        }
        
        public boolean awaitTermination(final long timeout, final TimeUnit unit) throws InterruptedException {
            return this.e.awaitTermination(timeout, unit);
        }
        
        public Future submit(final Runnable task) {
            return this.e.submit(task);
        }
        
        public Future submit(final Callable task) {
            return this.e.submit(task);
        }
        
        public Future submit(final Runnable task, final Object result) {
            return this.e.submit(task, result);
        }
        
        public List invokeAll(final Collection tasks) throws InterruptedException {
            return this.e.invokeAll(tasks);
        }
        
        public List invokeAll(final Collection tasks, final long timeout, final TimeUnit unit) throws InterruptedException {
            return this.e.invokeAll(tasks, timeout, unit);
        }
        
        public Object invokeAny(final Collection tasks) throws InterruptedException, ExecutionException {
            return this.e.invokeAny(tasks);
        }
        
        public Object invokeAny(final Collection tasks, final long timeout, final TimeUnit unit) throws InterruptedException, ExecutionException, TimeoutException {
            return this.e.invokeAny(tasks, timeout, unit);
        }
    }
    
    static class FinalizableDelegatedExecutorService extends DelegatedExecutorService
    {
        FinalizableDelegatedExecutorService(final ExecutorService executor) {
            super(executor);
        }
        
        protected void finalize() {
            super.shutdown();
        }
    }
    
    static class DelegatedScheduledExecutorService extends DelegatedExecutorService implements ScheduledExecutorService
    {
        private final ScheduledExecutorService e;
        
        DelegatedScheduledExecutorService(final ScheduledExecutorService executor) {
            super(executor);
            this.e = executor;
        }
        
        public ScheduledFuture schedule(final Runnable command, final long delay, final TimeUnit unit) {
            return this.e.schedule(command, delay, unit);
        }
        
        public ScheduledFuture schedule(final Callable callable, final long delay, final TimeUnit unit) {
            return this.e.schedule(callable, delay, unit);
        }
        
        public ScheduledFuture scheduleAtFixedRate(final Runnable command, final long initialDelay, final long period, final TimeUnit unit) {
            return this.e.scheduleAtFixedRate(command, initialDelay, period, unit);
        }
        
        public ScheduledFuture scheduleWithFixedDelay(final Runnable command, final long initialDelay, final long delay, final TimeUnit unit) {
            return this.e.scheduleWithFixedDelay(command, initialDelay, delay, unit);
        }
    }
}
