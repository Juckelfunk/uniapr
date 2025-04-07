// 
// Decompiled by Procyon v0.5.36
// 

package edu.emory.mathcs.backport.java.util.concurrent;

import java.util.ConcurrentModificationException;
import edu.emory.mathcs.backport.java.util.concurrent.helpers.Utils;
import java.util.Collection;
import java.util.ArrayList;
import java.util.List;
import java.util.Iterator;
import java.security.Permission;
import edu.emory.mathcs.backport.java.util.concurrent.locks.Condition;
import java.util.HashSet;
import edu.emory.mathcs.backport.java.util.concurrent.locks.ReentrantLock;
import edu.emory.mathcs.backport.java.util.concurrent.atomic.AtomicInteger;

public class ThreadPoolExecutor extends AbstractExecutorService
{
    private final AtomicInteger ctl;
    private static final int COUNT_BITS = 29;
    private static final int CAPACITY = 536870911;
    private static final int RUNNING = -536870912;
    private static final int SHUTDOWN = 0;
    private static final int STOP = 536870912;
    private static final int TIDYING = 1073741824;
    private static final int TERMINATED = 1610612736;
    private final BlockingQueue workQueue;
    private final ReentrantLock mainLock;
    private final HashSet workers;
    private final Condition termination;
    private int largestPoolSize;
    private long completedTaskCount;
    private volatile ThreadFactory threadFactory;
    private volatile RejectedExecutionHandler handler;
    private volatile long keepAliveTime;
    private volatile boolean allowCoreThreadTimeOut;
    private volatile int corePoolSize;
    private volatile int maximumPoolSize;
    private static final RejectedExecutionHandler defaultHandler;
    private static final RuntimePermission shutdownPerm;
    private static final boolean ONLY_ONE = true;
    
    private static int runStateOf(final int c) {
        return c & 0xE0000000;
    }
    
    private static int workerCountOf(final int c) {
        return c & 0x1FFFFFFF;
    }
    
    private static int ctlOf(final int rs, final int wc) {
        return rs | wc;
    }
    
    private static boolean runStateLessThan(final int c, final int s) {
        return c < s;
    }
    
    private static boolean runStateAtLeast(final int c, final int s) {
        return c >= s;
    }
    
    private static boolean isRunning(final int c) {
        return c < 0;
    }
    
    private boolean compareAndIncrementWorkerCount(final int expect) {
        return this.ctl.compareAndSet(expect, expect + 1);
    }
    
    private boolean compareAndDecrementWorkerCount(final int expect) {
        return this.ctl.compareAndSet(expect, expect - 1);
    }
    
    private void decrementWorkerCount() {
        while (!this.compareAndDecrementWorkerCount(this.ctl.get())) {}
    }
    
    private void advanceRunState(final int targetState) {
        int c;
        do {
            c = this.ctl.get();
        } while (!runStateAtLeast(c, targetState) && !this.ctl.compareAndSet(c, ctlOf(targetState, workerCountOf(c))));
    }
    
    final void tryTerminate() {
        while (true) {
            final int c = this.ctl.get();
            if (isRunning(c) || runStateAtLeast(c, 1073741824) || (runStateOf(c) == 0 && !this.workQueue.isEmpty())) {
                return;
            }
            if (workerCountOf(c) != 0) {
                this.interruptIdleWorkers(true);
                return;
            }
            final ReentrantLock mainLock = this.mainLock;
            mainLock.lock();
            try {
                if (this.ctl.compareAndSet(c, ctlOf(1073741824, 0))) {
                    try {
                        this.terminated();
                    }
                    finally {
                        this.ctl.set(ctlOf(1610612736, 0));
                        this.termination.signalAll();
                    }
                    return;
                }
                continue;
            }
            finally {
                mainLock.unlock();
            }
        }
    }
    
    private void checkShutdownAccess() {
        final SecurityManager security = System.getSecurityManager();
        if (security != null) {
            security.checkPermission(ThreadPoolExecutor.shutdownPerm);
            final ReentrantLock mainLock = this.mainLock;
            mainLock.lock();
            try {
                for (final Worker w : this.workers) {
                    security.checkAccess(w.thread);
                }
            }
            finally {
                mainLock.unlock();
            }
        }
    }
    
    private void interruptWorkers() {
        final ReentrantLock mainLock = this.mainLock;
        mainLock.lock();
        try {
            for (final Worker w : this.workers) {
                try {
                    w.thread.interrupt();
                }
                catch (SecurityException ex) {}
            }
        }
        finally {
            mainLock.unlock();
        }
    }
    
    private void interruptIdleWorkers(final boolean onlyOne) {
        final ReentrantLock mainLock = this.mainLock;
        mainLock.lock();
        try {
            for (final Worker w : this.workers) {
                final Thread t = w.thread;
                if (!t.isInterrupted() && w.tryLock()) {
                    try {
                        t.interrupt();
                    }
                    catch (SecurityException ignore) {}
                    finally {
                        w.unlock();
                    }
                }
                if (onlyOne) {
                    break;
                }
            }
        }
        finally {
            mainLock.unlock();
        }
    }
    
    private void interruptIdleWorkers() {
        this.interruptIdleWorkers(false);
    }
    
    private void clearInterruptsForTaskRun() {
        if (runStateLessThan(this.ctl.get(), 536870912) && Thread.interrupted() && runStateAtLeast(this.ctl.get(), 536870912)) {
            Thread.currentThread().interrupt();
        }
    }
    
    final void reject(final Runnable command) {
        this.handler.rejectedExecution(command, this);
    }
    
    void onShutdown() {
    }
    
    final boolean isRunningOrShutdown(final boolean shutdownOK) {
        final int rs = runStateOf(this.ctl.get());
        return rs == -536870912 || (rs == 0 && shutdownOK);
    }
    
    private List drainQueue() {
        final BlockingQueue q = this.workQueue;
        final List taskList = new ArrayList();
        q.drainTo(taskList);
        if (!q.isEmpty()) {
            final Runnable[] arr = q.toArray(new Runnable[0]);
            for (int i = 0; i < arr.length; ++i) {
                final Runnable r = arr[i];
                if (q.remove(r)) {
                    taskList.add(r);
                }
            }
        }
        return taskList;
    }
    
    private boolean addWorker(final Runnable firstTask, final boolean core) {
        while (true) {
            int n = this.ctl.get();
            final int rs = runStateOf(n);
            if (rs >= 0 && (rs != 0 || firstTask != null || this.workQueue.isEmpty())) {
                return false;
            }
            do {
                final int wc = workerCountOf(n);
                if (wc >= 536870911 || wc >= (core ? this.corePoolSize : this.maximumPoolSize)) {
                    return false;
                }
                if (this.compareAndIncrementWorkerCount(n)) {
                    final Worker w = new Worker(firstTask);
                    final Thread t = w.thread;
                    final ReentrantLock mainLock = this.mainLock;
                    mainLock.lock();
                    try {
                        final int c = this.ctl.get();
                        final int rs2 = runStateOf(c);
                        if (t == null || (rs2 >= 0 && (rs2 != 0 || firstTask != null))) {
                            this.decrementWorkerCount();
                            this.tryTerminate();
                            return false;
                        }
                        this.workers.add(w);
                        final int s = this.workers.size();
                        if (s > this.largestPoolSize) {
                            this.largestPoolSize = s;
                        }
                    }
                    finally {
                        mainLock.unlock();
                    }
                    t.start();
                    if (runStateOf(this.ctl.get()) == 536870912 && !t.isInterrupted()) {
                        t.interrupt();
                    }
                    return true;
                }
                n = this.ctl.get();
            } while (runStateOf(n) == rs);
        }
    }
    
    private void processWorkerExit(final Worker w, final boolean completedAbruptly) {
        if (completedAbruptly) {
            this.decrementWorkerCount();
        }
        final ReentrantLock mainLock = this.mainLock;
        mainLock.lock();
        try {
            this.completedTaskCount += w.completedTasks;
            this.workers.remove(w);
        }
        finally {
            mainLock.unlock();
        }
        this.tryTerminate();
        final int c = this.ctl.get();
        if (runStateLessThan(c, 536870912)) {
            if (!completedAbruptly) {
                int min = this.allowCoreThreadTimeOut ? 0 : this.corePoolSize;
                if (min == 0 && !this.workQueue.isEmpty()) {
                    min = 1;
                }
                if (workerCountOf(c) >= min) {
                    return;
                }
            }
            this.addWorker(null, false);
        }
    }
    
    private Runnable getTask() {
        boolean timedOut = false;
    Label_0002:
        while (true) {
            int c = this.ctl.get();
            final int rs = runStateOf(c);
            if (rs >= 0 && (rs >= 536870912 || this.workQueue.isEmpty())) {
                this.decrementWorkerCount();
                return null;
            }
            boolean timed;
            while (true) {
                final int wc = workerCountOf(c);
                timed = (this.allowCoreThreadTimeOut || wc > this.corePoolSize);
                if (wc <= this.maximumPoolSize) {
                    if (!timedOut) {
                        break;
                    }
                    if (!timed) {
                        break;
                    }
                }
                if (this.compareAndDecrementWorkerCount(c)) {
                    return null;
                }
                c = this.ctl.get();
                if (runStateOf(c) != rs) {
                    continue Label_0002;
                }
            }
            try {
                final Runnable r = (Runnable)(timed ? this.workQueue.poll(this.keepAliveTime, TimeUnit.NANOSECONDS) : ((Runnable)this.workQueue.take()));
                if (r != null) {
                    return r;
                }
                timedOut = true;
            }
            catch (InterruptedException retry) {
                timedOut = false;
            }
        }
    }
    
    final void runWorker(final Worker w) {
        Runnable task = w.firstTask;
        w.firstTask = null;
        boolean completedAbruptly = true;
        try {
            while (task != null || (task = this.getTask()) != null) {
                w.lock();
                this.clearInterruptsForTaskRun();
                try {
                    this.beforeExecute(w.thread, task);
                    Throwable thrown = null;
                    try {
                        task.run();
                    }
                    catch (RuntimeException x) {
                        thrown = x;
                        throw x;
                    }
                    catch (Error x2) {
                        thrown = x2;
                        throw x2;
                    }
                    catch (Throwable x3) {
                        thrown = x3;
                        throw new Error(x3);
                    }
                    finally {
                        this.afterExecute(task, thrown);
                    }
                    continue;
                }
                finally {
                    task = null;
                    ++w.completedTasks;
                    w.unlock();
                }
                break;
            }
            completedAbruptly = false;
        }
        finally {
            this.processWorkerExit(w, completedAbruptly);
        }
    }
    
    public ThreadPoolExecutor(final int corePoolSize, final int maximumPoolSize, final long keepAliveTime, final TimeUnit unit, final BlockingQueue workQueue) {
        this(corePoolSize, maximumPoolSize, keepAliveTime, unit, workQueue, Executors.defaultThreadFactory(), ThreadPoolExecutor.defaultHandler);
    }
    
    public ThreadPoolExecutor(final int corePoolSize, final int maximumPoolSize, final long keepAliveTime, final TimeUnit unit, final BlockingQueue workQueue, final ThreadFactory threadFactory) {
        this(corePoolSize, maximumPoolSize, keepAliveTime, unit, workQueue, threadFactory, ThreadPoolExecutor.defaultHandler);
    }
    
    public ThreadPoolExecutor(final int corePoolSize, final int maximumPoolSize, final long keepAliveTime, final TimeUnit unit, final BlockingQueue workQueue, final RejectedExecutionHandler handler) {
        this(corePoolSize, maximumPoolSize, keepAliveTime, unit, workQueue, Executors.defaultThreadFactory(), handler);
    }
    
    public ThreadPoolExecutor(final int corePoolSize, final int maximumPoolSize, final long keepAliveTime, final TimeUnit unit, final BlockingQueue workQueue, final ThreadFactory threadFactory, final RejectedExecutionHandler handler) {
        this.ctl = new AtomicInteger(ctlOf(-536870912, 0));
        this.mainLock = new ReentrantLock();
        this.workers = new HashSet();
        this.termination = this.mainLock.newCondition();
        if (corePoolSize < 0 || maximumPoolSize <= 0 || maximumPoolSize < corePoolSize || keepAliveTime < 0L) {
            throw new IllegalArgumentException();
        }
        if (workQueue == null || threadFactory == null || handler == null) {
            throw new NullPointerException();
        }
        this.corePoolSize = corePoolSize;
        this.maximumPoolSize = maximumPoolSize;
        this.workQueue = workQueue;
        this.keepAliveTime = unit.toNanos(keepAliveTime);
        this.threadFactory = threadFactory;
        this.handler = handler;
    }
    
    public void execute(final Runnable command) {
        if (command == null) {
            throw new NullPointerException();
        }
        int c = this.ctl.get();
        if (workerCountOf(c) < this.corePoolSize) {
            if (this.addWorker(command, true)) {
                return;
            }
            c = this.ctl.get();
        }
        if (isRunning(c) && this.workQueue.offer(command)) {
            final int recheck = this.ctl.get();
            if (!isRunning(recheck) && this.remove(command)) {
                this.reject(command);
            }
            else if (workerCountOf(recheck) == 0) {
                this.addWorker(null, false);
            }
        }
        else if (!this.addWorker(command, false)) {
            this.reject(command);
        }
    }
    
    public void shutdown() {
        final ReentrantLock mainLock = this.mainLock;
        mainLock.lock();
        try {
            this.checkShutdownAccess();
            this.advanceRunState(0);
            this.interruptIdleWorkers();
            this.onShutdown();
        }
        finally {
            mainLock.unlock();
        }
        this.tryTerminate();
    }
    
    public List shutdownNow() {
        final ReentrantLock mainLock = this.mainLock;
        mainLock.lock();
        List tasks;
        try {
            this.checkShutdownAccess();
            this.advanceRunState(536870912);
            this.interruptWorkers();
            tasks = this.drainQueue();
        }
        finally {
            mainLock.unlock();
        }
        this.tryTerminate();
        return tasks;
    }
    
    public boolean isShutdown() {
        return !isRunning(this.ctl.get());
    }
    
    public boolean isTerminating() {
        final int c = this.ctl.get();
        return !isRunning(c) && runStateLessThan(c, 1610612736);
    }
    
    public boolean isTerminated() {
        return runStateAtLeast(this.ctl.get(), 1610612736);
    }
    
    public boolean awaitTermination(final long timeout, final TimeUnit unit) throws InterruptedException {
        long nanos = unit.toNanos(timeout);
        final long deadline = Utils.nanoTime() + nanos;
        final ReentrantLock mainLock = this.mainLock;
        mainLock.lock();
        try {
            if (runStateAtLeast(this.ctl.get(), 1610612736)) {
                return true;
            }
            while (nanos > 0L) {
                this.termination.await(nanos, TimeUnit.NANOSECONDS);
                if (runStateAtLeast(this.ctl.get(), 1610612736)) {
                    return true;
                }
                nanos = deadline - Utils.nanoTime();
            }
            return false;
        }
        finally {
            mainLock.unlock();
        }
    }
    
    protected void finalize() {
        this.shutdown();
    }
    
    public void setThreadFactory(final ThreadFactory threadFactory) {
        if (threadFactory == null) {
            throw new NullPointerException();
        }
        this.threadFactory = threadFactory;
    }
    
    public ThreadFactory getThreadFactory() {
        return this.threadFactory;
    }
    
    public void setRejectedExecutionHandler(final RejectedExecutionHandler handler) {
        if (handler == null) {
            throw new NullPointerException();
        }
        this.handler = handler;
    }
    
    public RejectedExecutionHandler getRejectedExecutionHandler() {
        return this.handler;
    }
    
    public void setCorePoolSize(final int corePoolSize) {
        if (corePoolSize < 0) {
            throw new IllegalArgumentException();
        }
        final int delta = corePoolSize - this.corePoolSize;
        if (workerCountOf(this.ctl.get()) > (this.corePoolSize = corePoolSize)) {
            this.interruptIdleWorkers();
        }
        else if (delta > 0) {
            int k = Math.min(delta, this.workQueue.size());
            while (k-- > 0 && this.addWorker(null, true) && !this.workQueue.isEmpty()) {}
        }
    }
    
    public int getCorePoolSize() {
        return this.corePoolSize;
    }
    
    public boolean prestartCoreThread() {
        return workerCountOf(this.ctl.get()) < this.corePoolSize && this.addWorker(null, true);
    }
    
    public int prestartAllCoreThreads() {
        int n = 0;
        while (this.addWorker(null, true)) {
            ++n;
        }
        return n;
    }
    
    public boolean allowsCoreThreadTimeOut() {
        return this.allowCoreThreadTimeOut;
    }
    
    public void allowCoreThreadTimeOut(final boolean value) {
        if (value && this.keepAliveTime <= 0L) {
            throw new IllegalArgumentException("Core threads must have nonzero keep alive times");
        }
        if (value != this.allowCoreThreadTimeOut && (this.allowCoreThreadTimeOut = value)) {
            this.interruptIdleWorkers();
        }
    }
    
    public void setMaximumPoolSize(final int maximumPoolSize) {
        if (maximumPoolSize <= 0 || maximumPoolSize < this.corePoolSize) {
            throw new IllegalArgumentException();
        }
        if (workerCountOf(this.ctl.get()) > (this.maximumPoolSize = maximumPoolSize)) {
            this.interruptIdleWorkers();
        }
    }
    
    public int getMaximumPoolSize() {
        return this.maximumPoolSize;
    }
    
    public void setKeepAliveTime(final long time, final TimeUnit unit) {
        if (time < 0L) {
            throw new IllegalArgumentException();
        }
        if (time == 0L && this.allowsCoreThreadTimeOut()) {
            throw new IllegalArgumentException("Core threads must have nonzero keep alive times");
        }
        final long keepAliveTime = unit.toNanos(time);
        final long delta = keepAliveTime - this.keepAliveTime;
        this.keepAliveTime = keepAliveTime;
        if (delta < 0L) {
            this.interruptIdleWorkers();
        }
    }
    
    public long getKeepAliveTime(final TimeUnit unit) {
        return unit.convert(this.keepAliveTime, TimeUnit.NANOSECONDS);
    }
    
    public BlockingQueue getQueue() {
        return this.workQueue;
    }
    
    public boolean remove(final Runnable task) {
        final boolean removed = this.workQueue.remove(task);
        this.tryTerminate();
        return removed;
    }
    
    public void purge() {
        final BlockingQueue q = this.workQueue;
        try {
            final Iterator it = q.iterator();
            while (it.hasNext()) {
                final Runnable r = it.next();
                if (r instanceof Future && ((Future)r).isCancelled()) {
                    it.remove();
                }
            }
        }
        catch (ConcurrentModificationException fallThrough) {
            final Object[] arr = q.toArray();
            for (int i = 0; i < arr.length; ++i) {
                final Object r2 = arr[i];
                if (r2 instanceof Future && ((Future)r2).isCancelled()) {
                    q.remove(r2);
                }
            }
        }
        this.tryTerminate();
    }
    
    public int getPoolSize() {
        final ReentrantLock mainLock = this.mainLock;
        mainLock.lock();
        try {
            return runStateAtLeast(this.ctl.get(), 1073741824) ? 0 : this.workers.size();
        }
        finally {
            mainLock.unlock();
        }
    }
    
    public int getActiveCount() {
        final ReentrantLock mainLock = this.mainLock;
        mainLock.lock();
        try {
            int n = 0;
            for (final Worker w : this.workers) {
                if (w.isLocked()) {
                    ++n;
                }
            }
            return n;
        }
        finally {
            mainLock.unlock();
        }
    }
    
    public int getLargestPoolSize() {
        final ReentrantLock mainLock = this.mainLock;
        mainLock.lock();
        try {
            return this.largestPoolSize;
        }
        finally {
            mainLock.unlock();
        }
    }
    
    public long getTaskCount() {
        final ReentrantLock mainLock = this.mainLock;
        mainLock.lock();
        try {
            long n = this.completedTaskCount;
            for (final Worker w : this.workers) {
                n += w.completedTasks;
                if (w.isLocked()) {
                    ++n;
                }
            }
            return n + this.workQueue.size();
        }
        finally {
            mainLock.unlock();
        }
    }
    
    public long getCompletedTaskCount() {
        final ReentrantLock mainLock = this.mainLock;
        mainLock.lock();
        try {
            long n = this.completedTaskCount;
            for (final Worker w : this.workers) {
                n += w.completedTasks;
            }
            return n;
        }
        finally {
            mainLock.unlock();
        }
    }
    
    protected void beforeExecute(final Thread t, final Runnable r) {
    }
    
    protected void afterExecute(final Runnable r, final Throwable t) {
    }
    
    protected void terminated() {
    }
    
    static {
        defaultHandler = new AbortPolicy();
        shutdownPerm = new RuntimePermission("modifyThread");
    }
    
    private final class Worker extends ReentrantLock implements Runnable
    {
        private static final long serialVersionUID = 6138294804551838833L;
        final Thread thread;
        Runnable firstTask;
        volatile long completedTasks;
        
        Worker(final Runnable firstTask) {
            this.firstTask = firstTask;
            this.thread = ThreadPoolExecutor.this.getThreadFactory().newThread(this);
        }
        
        public void run() {
            ThreadPoolExecutor.this.runWorker(this);
        }
    }
    
    public static class CallerRunsPolicy implements RejectedExecutionHandler
    {
        public void rejectedExecution(final Runnable r, final ThreadPoolExecutor e) {
            if (!e.isShutdown()) {
                r.run();
            }
        }
    }
    
    public static class AbortPolicy implements RejectedExecutionHandler
    {
        public void rejectedExecution(final Runnable r, final ThreadPoolExecutor e) {
            throw new RejectedExecutionException();
        }
    }
    
    public static class DiscardPolicy implements RejectedExecutionHandler
    {
        public void rejectedExecution(final Runnable r, final ThreadPoolExecutor e) {
        }
    }
    
    public static class DiscardOldestPolicy implements RejectedExecutionHandler
    {
        public void rejectedExecution(final Runnable r, final ThreadPoolExecutor e) {
            if (!e.isShutdown()) {
                e.getQueue().poll();
                e.execute(r);
            }
        }
    }
}
