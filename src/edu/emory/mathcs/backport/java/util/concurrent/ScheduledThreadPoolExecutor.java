// 
// Decompiled by Procyon v0.5.36
// 

package edu.emory.mathcs.backport.java.util.concurrent;

import java.util.NoSuchElementException;
import java.util.Iterator;
import edu.emory.mathcs.backport.java.util.Arrays;
import java.util.Collection;
import edu.emory.mathcs.backport.java.util.concurrent.locks.Condition;
import edu.emory.mathcs.backport.java.util.concurrent.locks.ReentrantLock;
import edu.emory.mathcs.backport.java.util.AbstractQueue;
import java.util.List;
import edu.emory.mathcs.backport.java.util.concurrent.helpers.Utils;
import edu.emory.mathcs.backport.java.util.concurrent.atomic.AtomicLong;

public class ScheduledThreadPoolExecutor extends ThreadPoolExecutor implements ScheduledExecutorService
{
    private volatile boolean continueExistingPeriodicTasksAfterShutdown;
    private volatile boolean executeExistingDelayedTasksAfterShutdown;
    private volatile boolean removeOnCancel;
    private static final AtomicLong sequencer;
    
    final long now() {
        return Utils.nanoTime();
    }
    
    boolean canRunInCurrentRunState(final boolean periodic) {
        return this.isRunningOrShutdown(periodic ? this.continueExistingPeriodicTasksAfterShutdown : this.executeExistingDelayedTasksAfterShutdown);
    }
    
    private void delayedExecute(final RunnableScheduledFuture task) {
        if (this.isShutdown()) {
            this.reject(task);
        }
        else {
            super.getQueue().add(task);
            if (this.isShutdown() && !this.canRunInCurrentRunState(task.isPeriodic()) && this.remove(task)) {
                task.cancel(false);
            }
            else {
                this.prestartCoreThread();
            }
        }
    }
    
    void reExecutePeriodic(final RunnableScheduledFuture task) {
        if (this.canRunInCurrentRunState(true)) {
            super.getQueue().add(task);
            if (!this.canRunInCurrentRunState(true) && this.remove(task)) {
                task.cancel(false);
            }
            else {
                this.prestartCoreThread();
            }
        }
    }
    
    void onShutdown() {
        final BlockingQueue q = super.getQueue();
        final boolean keepDelayed = this.getExecuteExistingDelayedTasksAfterShutdownPolicy();
        final boolean keepPeriodic = this.getContinueExistingPeriodicTasksAfterShutdownPolicy();
        if (!keepDelayed && !keepPeriodic) {
            q.clear();
        }
        else {
            final Object[] entries = q.toArray();
            for (int i = 0; i < entries.length; ++i) {
                final Object e = entries[i];
                if (e instanceof RunnableScheduledFuture) {
                    final RunnableScheduledFuture t = (RunnableScheduledFuture)e;
                    Label_0104: {
                        if (t.isPeriodic()) {
                            if (!keepPeriodic) {
                                break Label_0104;
                            }
                        }
                        else if (!keepDelayed) {
                            break Label_0104;
                        }
                        if (!t.isCancelled()) {
                            continue;
                        }
                    }
                    if (q.remove(t)) {
                        t.cancel(false);
                    }
                }
            }
        }
        this.tryTerminate();
    }
    
    protected RunnableScheduledFuture decorateTask(final Runnable runnable, final RunnableScheduledFuture task) {
        return task;
    }
    
    protected RunnableScheduledFuture decorateTask(final Callable callable, final RunnableScheduledFuture task) {
        return task;
    }
    
    public ScheduledThreadPoolExecutor(final int corePoolSize) {
        super(corePoolSize, Integer.MAX_VALUE, 0L, TimeUnit.NANOSECONDS, new DelayedWorkQueue());
        this.executeExistingDelayedTasksAfterShutdown = true;
        this.removeOnCancel = false;
    }
    
    public ScheduledThreadPoolExecutor(final int corePoolSize, final ThreadFactory threadFactory) {
        super(corePoolSize, Integer.MAX_VALUE, 0L, TimeUnit.NANOSECONDS, new DelayedWorkQueue(), threadFactory);
        this.executeExistingDelayedTasksAfterShutdown = true;
        this.removeOnCancel = false;
    }
    
    public ScheduledThreadPoolExecutor(final int corePoolSize, final RejectedExecutionHandler handler) {
        super(corePoolSize, Integer.MAX_VALUE, 0L, TimeUnit.NANOSECONDS, new DelayedWorkQueue(), handler);
        this.executeExistingDelayedTasksAfterShutdown = true;
        this.removeOnCancel = false;
    }
    
    public ScheduledThreadPoolExecutor(final int corePoolSize, final ThreadFactory threadFactory, final RejectedExecutionHandler handler) {
        super(corePoolSize, Integer.MAX_VALUE, 0L, TimeUnit.NANOSECONDS, new DelayedWorkQueue(), threadFactory, handler);
        this.executeExistingDelayedTasksAfterShutdown = true;
        this.removeOnCancel = false;
    }
    
    public ScheduledFuture schedule(final Runnable command, long delay, final TimeUnit unit) {
        if (command == null || unit == null) {
            throw new NullPointerException();
        }
        if (delay < 0L) {
            delay = 0L;
        }
        final long triggerTime = this.now() + unit.toNanos(delay);
        final RunnableScheduledFuture t = this.decorateTask(command, new ScheduledFutureTask(command, null, triggerTime));
        this.delayedExecute(t);
        return t;
    }
    
    public ScheduledFuture schedule(final Callable callable, long delay, final TimeUnit unit) {
        if (callable == null || unit == null) {
            throw new NullPointerException();
        }
        if (delay < 0L) {
            delay = 0L;
        }
        final long triggerTime = this.now() + unit.toNanos(delay);
        final RunnableScheduledFuture t = this.decorateTask(callable, new ScheduledFutureTask(callable, triggerTime));
        this.delayedExecute(t);
        return t;
    }
    
    public ScheduledFuture scheduleAtFixedRate(final Runnable command, long initialDelay, final long period, final TimeUnit unit) {
        if (command == null || unit == null) {
            throw new NullPointerException();
        }
        if (period <= 0L) {
            throw new IllegalArgumentException();
        }
        if (initialDelay < 0L) {
            initialDelay = 0L;
        }
        final long triggerTime = this.now() + unit.toNanos(initialDelay);
        final RunnableScheduledFuture t = this.decorateTask(command, new ScheduledFutureTask(command, null, triggerTime, unit.toNanos(period)));
        this.delayedExecute(t);
        return t;
    }
    
    public ScheduledFuture scheduleWithFixedDelay(final Runnable command, long initialDelay, final long delay, final TimeUnit unit) {
        if (command == null || unit == null) {
            throw new NullPointerException();
        }
        if (delay <= 0L) {
            throw new IllegalArgumentException();
        }
        if (initialDelay < 0L) {
            initialDelay = 0L;
        }
        final long triggerTime = this.now() + unit.toNanos(initialDelay);
        final RunnableScheduledFuture t = this.decorateTask(command, new ScheduledFutureTask(command, null, triggerTime, unit.toNanos(-delay)));
        this.delayedExecute(t);
        return t;
    }
    
    public void execute(final Runnable command) {
        this.schedule(command, 0L, TimeUnit.NANOSECONDS);
    }
    
    public Future submit(final Runnable task) {
        return this.schedule(task, 0L, TimeUnit.NANOSECONDS);
    }
    
    public Future submit(final Runnable task, final Object result) {
        return this.schedule(Executors.callable(task, result), 0L, TimeUnit.NANOSECONDS);
    }
    
    public Future submit(final Callable task) {
        return this.schedule(task, 0L, TimeUnit.NANOSECONDS);
    }
    
    public void setContinueExistingPeriodicTasksAfterShutdownPolicy(final boolean value) {
        this.continueExistingPeriodicTasksAfterShutdown = value;
        if (!value && this.isShutdown()) {
            this.onShutdown();
        }
    }
    
    public boolean getContinueExistingPeriodicTasksAfterShutdownPolicy() {
        return this.continueExistingPeriodicTasksAfterShutdown;
    }
    
    public void setExecuteExistingDelayedTasksAfterShutdownPolicy(final boolean value) {
        this.executeExistingDelayedTasksAfterShutdown = value;
        if (!value && this.isShutdown()) {
            this.onShutdown();
        }
    }
    
    public boolean getExecuteExistingDelayedTasksAfterShutdownPolicy() {
        return this.executeExistingDelayedTasksAfterShutdown;
    }
    
    public void setRemoveOnCancelPolicy(final boolean value) {
        this.removeOnCancel = value;
    }
    
    public boolean getRemoveOnCancelPolicy() {
        return this.removeOnCancel;
    }
    
    public void shutdown() {
        super.shutdown();
    }
    
    public List shutdownNow() {
        return super.shutdownNow();
    }
    
    public BlockingQueue getQueue() {
        return super.getQueue();
    }
    
    static {
        sequencer = new AtomicLong(0L);
    }
    
    private class ScheduledFutureTask extends FutureTask implements RunnableScheduledFuture
    {
        private final long sequenceNumber;
        private long time;
        private final long period;
        int heapIndex;
        
        ScheduledFutureTask(final Runnable r, final Object result, final long ns) {
            super(r, result);
            this.time = ns;
            this.period = 0L;
            this.sequenceNumber = ScheduledThreadPoolExecutor.sequencer.getAndIncrement();
        }
        
        ScheduledFutureTask(final Runnable r, final Object result, final long ns, final long period) {
            super(r, result);
            this.time = ns;
            this.period = period;
            this.sequenceNumber = ScheduledThreadPoolExecutor.sequencer.getAndIncrement();
        }
        
        ScheduledFutureTask(final Callable callable, final long ns) {
            super(callable);
            this.time = ns;
            this.period = 0L;
            this.sequenceNumber = ScheduledThreadPoolExecutor.sequencer.getAndIncrement();
        }
        
        public long getDelay(final TimeUnit unit) {
            final long d = unit.convert(this.time - ScheduledThreadPoolExecutor.this.now(), TimeUnit.NANOSECONDS);
            return d;
        }
        
        public int compareTo(final Object other) {
            final Delayed otherd = (Delayed)other;
            if (otherd == this) {
                return 0;
            }
            if (!(otherd instanceof ScheduledFutureTask)) {
                final long d = this.getDelay(TimeUnit.NANOSECONDS) - otherd.getDelay(TimeUnit.NANOSECONDS);
                return (d == 0L) ? 0 : ((d < 0L) ? -1 : 1);
            }
            final ScheduledFutureTask x = (ScheduledFutureTask)other;
            final long diff = this.time - x.time;
            if (diff < 0L) {
                return -1;
            }
            if (diff > 0L) {
                return 1;
            }
            if (this.sequenceNumber < x.sequenceNumber) {
                return -1;
            }
            return 1;
        }
        
        public boolean isPeriodic() {
            return this.period != 0L;
        }
        
        private void setNextRunTime() {
            final long p = this.period;
            if (p > 0L) {
                this.time += p;
            }
            else {
                this.time = ScheduledThreadPoolExecutor.this.now() - p;
            }
        }
        
        public boolean cancel(final boolean mayInterruptIfRunning) {
            final boolean cancelled = super.cancel(mayInterruptIfRunning);
            if (cancelled && ScheduledThreadPoolExecutor.this.removeOnCancel && this.heapIndex >= 0) {
                ScheduledThreadPoolExecutor.this.remove(this);
            }
            return cancelled;
        }
        
        public void run() {
            final boolean periodic = this.isPeriodic();
            if (!ScheduledThreadPoolExecutor.this.canRunInCurrentRunState(periodic)) {
                this.cancel(false);
            }
            else if (!periodic) {
                FutureTask.this.run();
            }
            else if (FutureTask.this.runAndReset()) {
                this.setNextRunTime();
                ScheduledThreadPoolExecutor.this.reExecutePeriodic(this);
            }
        }
    }
    
    static class DelayedWorkQueue extends AbstractQueue implements BlockingQueue
    {
        private static final int INITIAL_CAPACITY = 64;
        private transient RunnableScheduledFuture[] queue;
        private final transient ReentrantLock lock;
        private final transient Condition available;
        private int size;
        
        DelayedWorkQueue() {
            this.queue = new RunnableScheduledFuture[64];
            this.lock = new ReentrantLock();
            this.available = this.lock.newCondition();
            this.size = 0;
        }
        
        private void setIndex(final Object f, final int idx) {
            if (f instanceof ScheduledFutureTask) {
                ((ScheduledFutureTask)f).heapIndex = idx;
            }
        }
        
        private void siftUp(int k, final RunnableScheduledFuture key) {
            while (k > 0) {
                final int parent = k - 1 >>> 1;
                final RunnableScheduledFuture e = this.queue[parent];
                if (key.compareTo(e) >= 0) {
                    break;
                }
                this.setIndex(this.queue[k] = e, k);
                k = parent;
            }
            this.setIndex(this.queue[k] = key, k);
        }
        
        private void siftDown(int k, final RunnableScheduledFuture key) {
            int child;
            for (int half = this.size >>> 1; k < half; k = child) {
                child = (k << 1) + 1;
                RunnableScheduledFuture c = this.queue[child];
                final int right = child + 1;
                if (right < this.size && c.compareTo(this.queue[right]) > 0) {
                    c = this.queue[child = right];
                }
                if (key.compareTo(c) <= 0) {
                    break;
                }
                this.setIndex(this.queue[k] = c, k);
            }
            this.setIndex(this.queue[k] = key, k);
        }
        
        private RunnableScheduledFuture finishPoll(final RunnableScheduledFuture f) {
            final int size = this.size - 1;
            this.size = size;
            final int s = size;
            final RunnableScheduledFuture x = this.queue[s];
            this.queue[s] = null;
            if (s != 0) {
                this.siftDown(0, x);
                this.available.signalAll();
            }
            this.setIndex(f, -1);
            return f;
        }
        
        private void grow() {
            final int oldCapacity = this.queue.length;
            int newCapacity = oldCapacity + (oldCapacity >> 1);
            if (newCapacity < 0) {
                newCapacity = Integer.MAX_VALUE;
            }
            final RunnableScheduledFuture[] newqueue = new RunnableScheduledFuture[newCapacity];
            System.arraycopy(this.queue, 0, newqueue, 0, this.queue.length);
            this.queue = newqueue;
        }
        
        private int indexOf(final Object x) {
            if (x != null) {
                for (int i = 0; i < this.size; ++i) {
                    if (x.equals(this.queue[i])) {
                        return i;
                    }
                }
            }
            return -1;
        }
        
        public boolean remove(final Object x) {
            final ReentrantLock lock = this.lock;
            lock.lock();
            boolean removed;
            try {
                int i;
                if (x instanceof ScheduledFutureTask) {
                    i = ((ScheduledFutureTask)x).heapIndex;
                }
                else {
                    i = this.indexOf(x);
                }
                if (removed = (i >= 0 && i < this.size && this.queue[i] == x)) {
                    this.setIndex(x, -1);
                    final int size = this.size - 1;
                    this.size = size;
                    final int s = size;
                    final RunnableScheduledFuture replacement = this.queue[s];
                    this.queue[s] = null;
                    if (s != i) {
                        this.siftDown(i, replacement);
                        if (this.queue[i] == replacement) {
                            this.siftUp(i, replacement);
                        }
                    }
                }
            }
            finally {
                lock.unlock();
            }
            return removed;
        }
        
        public int size() {
            final ReentrantLock lock = this.lock;
            lock.lock();
            int s;
            try {
                s = this.size;
            }
            finally {
                lock.unlock();
            }
            return s;
        }
        
        public boolean isEmpty() {
            return this.size() == 0;
        }
        
        public int remainingCapacity() {
            return Integer.MAX_VALUE;
        }
        
        public Object peek() {
            final ReentrantLock lock = this.lock;
            lock.lock();
            try {
                return this.queue[0];
            }
            finally {
                lock.unlock();
            }
        }
        
        public boolean offer(final Object x) {
            if (x == null) {
                throw new NullPointerException();
            }
            final RunnableScheduledFuture e = (RunnableScheduledFuture)x;
            final ReentrantLock lock = this.lock;
            lock.lock();
            try {
                final int i = this.size;
                if (i >= this.queue.length) {
                    this.grow();
                }
                this.size = i + 1;
                boolean notify;
                if (i == 0) {
                    notify = true;
                    this.setIndex(this.queue[0] = e, 0);
                }
                else {
                    notify = (e.compareTo(this.queue[0]) < 0);
                    this.siftUp(i, e);
                }
                if (notify) {
                    this.available.signalAll();
                }
            }
            finally {
                lock.unlock();
            }
            return true;
        }
        
        public void put(final Object e) {
            this.offer(e);
        }
        
        public boolean add(final Runnable e) {
            return this.offer(e);
        }
        
        public boolean offer(final Object e, final long timeout, final TimeUnit unit) {
            return this.offer(e);
        }
        
        public Object poll() {
            final ReentrantLock lock = this.lock;
            lock.lock();
            try {
                final RunnableScheduledFuture first = this.queue[0];
                if (first == null || first.getDelay(TimeUnit.NANOSECONDS) > 0L) {
                    return null;
                }
                return this.finishPoll(first);
            }
            finally {
                lock.unlock();
            }
        }
        
        public Object take() throws InterruptedException {
            final ReentrantLock lock = this.lock;
            lock.lockInterruptibly();
            try {
                RunnableScheduledFuture first;
                while (true) {
                    first = this.queue[0];
                    if (first == null) {
                        this.available.await();
                    }
                    else {
                        final long delay = first.getDelay(TimeUnit.NANOSECONDS);
                        if (delay <= 0L) {
                            break;
                        }
                        this.available.await(delay, TimeUnit.NANOSECONDS);
                    }
                }
                return this.finishPoll(first);
            }
            finally {
                lock.unlock();
            }
        }
        
        public Object poll(final long timeout, final TimeUnit unit) throws InterruptedException {
            long nanos = unit.toNanos(timeout);
            final long deadline = Utils.nanoTime() + nanos;
            final ReentrantLock lock = this.lock;
            lock.lockInterruptibly();
            try {
                while (true) {
                    final RunnableScheduledFuture first = this.queue[0];
                    if (first == null) {
                        if (nanos <= 0L) {
                            return null;
                        }
                        this.available.await(nanos, TimeUnit.NANOSECONDS);
                        nanos = deadline - Utils.nanoTime();
                    }
                    else {
                        long delay = first.getDelay(TimeUnit.NANOSECONDS);
                        if (delay <= 0L) {
                            return this.finishPoll(first);
                        }
                        if (nanos <= 0L) {
                            return null;
                        }
                        if (delay > nanos) {
                            delay = nanos;
                        }
                        this.available.await(delay, TimeUnit.NANOSECONDS);
                        nanos = deadline - Utils.nanoTime();
                    }
                }
            }
            finally {
                lock.unlock();
            }
        }
        
        public void clear() {
            final ReentrantLock lock = this.lock;
            lock.lock();
            try {
                for (int i = 0; i < this.size; ++i) {
                    final RunnableScheduledFuture t = this.queue[i];
                    if (t != null) {
                        this.queue[i] = null;
                        this.setIndex(t, -1);
                    }
                }
                this.size = 0;
            }
            finally {
                lock.unlock();
            }
        }
        
        private RunnableScheduledFuture pollExpired() {
            final RunnableScheduledFuture first = this.queue[0];
            if (first == null || first.getDelay(TimeUnit.NANOSECONDS) > 0L) {
                return null;
            }
            this.setIndex(first, -1);
            final int size = this.size - 1;
            this.size = size;
            final int s = size;
            final RunnableScheduledFuture x = this.queue[s];
            this.queue[s] = null;
            if (s != 0) {
                this.siftDown(0, x);
            }
            return first;
        }
        
        public int drainTo(final Collection c) {
            if (c == null) {
                throw new NullPointerException();
            }
            if (c == this) {
                throw new IllegalArgumentException();
            }
            final ReentrantLock lock = this.lock;
            lock.lock();
            try {
                int n = 0;
                while (true) {
                    final RunnableScheduledFuture first = this.pollExpired();
                    if (first == null) {
                        break;
                    }
                    c.add(first);
                    ++n;
                }
                if (n > 0) {
                    this.available.signalAll();
                }
                return n;
            }
            finally {
                lock.unlock();
            }
        }
        
        public int drainTo(final Collection c, final int maxElements) {
            if (c == null) {
                throw new NullPointerException();
            }
            if (c == this) {
                throw new IllegalArgumentException();
            }
            if (maxElements <= 0) {
                return 0;
            }
            final ReentrantLock lock = this.lock;
            lock.lock();
            try {
                int n;
                for (n = 0; n < maxElements; ++n) {
                    final RunnableScheduledFuture first = this.pollExpired();
                    if (first == null) {
                        break;
                    }
                    c.add(first);
                }
                if (n > 0) {
                    this.available.signalAll();
                }
                return n;
            }
            finally {
                lock.unlock();
            }
        }
        
        public Object[] toArray() {
            final ReentrantLock lock = this.lock;
            lock.lock();
            try {
                return Arrays.copyOf(this.queue, this.size);
            }
            finally {
                lock.unlock();
            }
        }
        
        public Object[] toArray(final Object[] a) {
            final ReentrantLock lock = this.lock;
            lock.lock();
            try {
                if (a.length < this.size) {
                    return Arrays.copyOf(this.queue, this.size, a.getClass());
                }
                System.arraycopy(this.queue, 0, a, 0, this.size);
                if (a.length > this.size) {
                    a[this.size] = null;
                }
                return a;
            }
            finally {
                lock.unlock();
            }
        }
        
        public Iterator iterator() {
            return new Itr(this.toArray());
        }
        
        private class Itr implements Iterator
        {
            final Object[] array;
            int cursor;
            int lastRet;
            
            Itr(final Object[] array) {
                this.lastRet = -1;
                this.array = array;
            }
            
            public boolean hasNext() {
                return this.cursor < this.array.length;
            }
            
            public Object next() {
                if (this.cursor >= this.array.length) {
                    throw new NoSuchElementException();
                }
                this.lastRet = this.cursor;
                return this.array[this.cursor++];
            }
            
            public void remove() {
                if (this.lastRet < 0) {
                    throw new IllegalStateException();
                }
                DelayedWorkQueue.this.remove(this.array[this.lastRet]);
                this.lastRet = -1;
            }
        }
    }
}
