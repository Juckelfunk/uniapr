// 
// Decompiled by Procyon v0.5.36
// 

package edu.emory.mathcs.backport.java.util.concurrent;

public class ExecutorCompletionService implements CompletionService
{
    private final Executor executor;
    private final AbstractExecutorService aes;
    private final BlockingQueue completionQueue;
    
    private RunnableFuture newTaskFor(final Callable task) {
        if (this.aes == null) {
            return new FutureTask(task);
        }
        return this.aes.newTaskFor(task);
    }
    
    private RunnableFuture newTaskFor(final Runnable task, final Object result) {
        if (this.aes == null) {
            return new FutureTask(task, result);
        }
        return this.aes.newTaskFor(task, result);
    }
    
    public ExecutorCompletionService(final Executor executor) {
        if (executor == null) {
            throw new NullPointerException();
        }
        this.executor = executor;
        this.aes = ((executor instanceof AbstractExecutorService) ? ((AbstractExecutorService)executor) : null);
        this.completionQueue = new LinkedBlockingQueue();
    }
    
    public ExecutorCompletionService(final Executor executor, final BlockingQueue completionQueue) {
        if (executor == null || completionQueue == null) {
            throw new NullPointerException();
        }
        this.executor = executor;
        this.aes = ((executor instanceof AbstractExecutorService) ? ((AbstractExecutorService)executor) : null);
        this.completionQueue = completionQueue;
    }
    
    public Future submit(final Callable task) {
        if (task == null) {
            throw new NullPointerException();
        }
        final RunnableFuture f = this.newTaskFor(task);
        this.executor.execute(new QueueingFuture(f));
        return f;
    }
    
    public Future submit(final Runnable task, final Object result) {
        if (task == null) {
            throw new NullPointerException();
        }
        final RunnableFuture f = this.newTaskFor(task, result);
        this.executor.execute(new QueueingFuture(f));
        return f;
    }
    
    public Future take() throws InterruptedException {
        return (Future)this.completionQueue.take();
    }
    
    public Future poll() {
        return (Future)this.completionQueue.poll();
    }
    
    public Future poll(final long timeout, final TimeUnit unit) throws InterruptedException {
        return (Future)this.completionQueue.poll(timeout, unit);
    }
    
    private class QueueingFuture extends FutureTask
    {
        private final Future task;
        
        QueueingFuture(final RunnableFuture task) {
            super(task, null);
            this.task = task;
        }
        
        protected void done() {
            ExecutorCompletionService.this.completionQueue.add(this.task);
        }
    }
}
