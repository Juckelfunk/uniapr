// 
// Decompiled by Procyon v0.5.36
// 

package edu.emory.mathcs.backport.java.util.concurrent;

import java.io.IOException;
import java.io.ObjectInputStream;
import edu.emory.mathcs.backport.java.util.concurrent.helpers.FIFOWaitQueue;
import edu.emory.mathcs.backport.java.util.concurrent.helpers.WaitQueue;
import edu.emory.mathcs.backport.java.util.concurrent.helpers.Utils;
import java.util.Collection;
import java.io.Serializable;

public class Semaphore implements Serializable
{
    private static final long serialVersionUID = -3222578661600680210L;
    private final Sync sync;
    
    public Semaphore(final int permits) {
        this.sync = new NonfairSync(permits);
    }
    
    public Semaphore(final int permits, final boolean fair) {
        this.sync = (fair ? new FairSync(permits) : new NonfairSync(permits));
    }
    
    public void acquire() throws InterruptedException {
        this.sync.acquire(1);
    }
    
    public void acquireUninterruptibly() {
        this.sync.acquireUninterruptibly(1);
    }
    
    public boolean tryAcquire() {
        return this.sync.attempt(1);
    }
    
    public boolean tryAcquire(final long timeout, final TimeUnit unit) throws InterruptedException {
        return this.sync.attempt(1, unit.toNanos(timeout));
    }
    
    public void release() {
        this.sync.release(1);
    }
    
    public void acquire(final int permits) throws InterruptedException {
        if (permits < 0) {
            throw new IllegalArgumentException();
        }
        this.sync.acquire(permits);
    }
    
    public void acquireUninterruptibly(final int permits) {
        this.sync.acquireUninterruptibly(permits);
    }
    
    public boolean tryAcquire(final int permits) {
        if (permits < 0) {
            throw new IllegalArgumentException();
        }
        return this.sync.attempt(permits);
    }
    
    public boolean tryAcquire(final int permits, final long timeout, final TimeUnit unit) throws InterruptedException {
        if (permits < 0) {
            throw new IllegalArgumentException();
        }
        return this.sync.attempt(permits, unit.toNanos(timeout));
    }
    
    public void release(final int permits) {
        if (permits < 0) {
            throw new IllegalArgumentException();
        }
        this.sync.release(permits);
    }
    
    public int availablePermits() {
        return this.sync.getPermits();
    }
    
    public int drainPermits() {
        return this.sync.drain();
    }
    
    protected void reducePermits(final int reduction) {
        if (reduction < 0) {
            throw new IllegalArgumentException();
        }
        this.sync.reduce(reduction);
    }
    
    public boolean isFair() {
        return this.sync instanceof FairSync;
    }
    
    public final boolean hasQueuedThreads() {
        return this.sync.hasQueuedThreads();
    }
    
    public final int getQueueLength() {
        return this.sync.getQueueLength();
    }
    
    protected Collection getQueuedThreads() {
        return this.sync.getQueuedThreads();
    }
    
    public String toString() {
        return super.toString() + "[Permits = " + this.sync.getPermits() + "]";
    }
    
    abstract static class Sync implements Serializable
    {
        private static final long serialVersionUID = 1192457210091910933L;
        int permits_;
        
        protected Sync(final int permits) {
            this.permits_ = permits;
        }
        
        abstract void acquireUninterruptibly(final int p0);
        
        abstract void acquire(final int p0) throws InterruptedException;
        
        public boolean attempt(final int n) {
            synchronized (this) {
                if (this.permits_ >= n) {
                    this.permits_ -= n;
                    return true;
                }
                return false;
            }
        }
        
        abstract boolean attempt(final int p0, final long p1) throws InterruptedException;
        
        abstract void release(final int p0);
        
        public synchronized int getPermits() {
            return this.permits_;
        }
        
        public synchronized int drain() {
            final int acquired = this.permits_;
            this.permits_ = 0;
            return acquired;
        }
        
        public synchronized void reduce(final int reduction) {
            this.permits_ -= reduction;
        }
        
        abstract boolean hasQueuedThreads();
        
        abstract int getQueueLength();
        
        abstract Collection getQueuedThreads();
    }
    
    static final class NonfairSync extends Sync
    {
        private static final long serialVersionUID = -2694183684443567898L;
        
        protected NonfairSync(final int initialPermits) {
            super(initialPermits);
        }
        
        private static void checkAgainstMultiacquire(final int n) {
            if (n != 1) {
                throw new UnsupportedOperationException("Atomic multi-acquire supported only in FAIR semaphores");
            }
        }
        
        public void acquireUninterruptibly(final int n) {
            if (n == 0) {
                return;
            }
            checkAgainstMultiacquire(n);
            synchronized (this) {
                if (this.permits_ > 0) {
                    --this.permits_;
                    return;
                }
                boolean wasInterrupted = Thread.interrupted();
                try {
                    do {
                        try {
                            this.wait();
                        }
                        catch (InterruptedException e) {
                            wasInterrupted = true;
                        }
                    } while (this.permits_ <= 0);
                    --this.permits_;
                }
                finally {
                    if (wasInterrupted) {
                        Thread.currentThread().interrupt();
                    }
                }
            }
        }
        
        public void acquire(final int n) throws InterruptedException {
            if (Thread.interrupted()) {
                throw new InterruptedException();
            }
            if (n == 0) {
                return;
            }
            checkAgainstMultiacquire(n);
            synchronized (this) {
                while (this.permits_ <= 0) {
                    try {
                        this.wait();
                        continue;
                    }
                    catch (InterruptedException ex) {
                        this.notify();
                        throw ex;
                    }
                    break;
                }
                --this.permits_;
            }
        }
        
        public boolean attempt(final int n, long nanos) throws InterruptedException {
            if (Thread.interrupted()) {
                throw new InterruptedException();
            }
            if (n == 0) {
                return true;
            }
            checkAgainstMultiacquire(n);
            synchronized (this) {
                if (this.permits_ > 0) {
                    --this.permits_;
                    return true;
                }
                if (nanos <= 0L) {
                    return false;
                }
                try {
                    final long deadline = Utils.nanoTime() + nanos;
                    do {
                        TimeUnit.NANOSECONDS.timedWait(this, nanos);
                        if (this.permits_ > 0) {
                            --this.permits_;
                            return true;
                        }
                        nanos = deadline - Utils.nanoTime();
                    } while (nanos > 0L);
                    return false;
                }
                catch (InterruptedException ex) {
                    this.notify();
                    throw ex;
                }
            }
        }
        
        public synchronized void release(final int n) {
            if (n < 0) {
                throw new IllegalArgumentException("Negative argument");
            }
            this.permits_ += n;
            for (int i = 0; i < n; ++i) {
                this.notify();
            }
        }
        
        public boolean hasQueuedThreads() {
            throw new UnsupportedOperationException("Use FAIR version");
        }
        
        public int getQueueLength() {
            throw new UnsupportedOperationException("Use FAIR version");
        }
        
        public Collection getQueuedThreads() {
            throw new UnsupportedOperationException("Use FAIR version");
        }
    }
    
    static final class FairSync extends Sync implements WaitQueue.QueuedSync
    {
        private static final long serialVersionUID = 2014338818796000944L;
        private transient WaitQueue wq_;
        
        FairSync(final int initialPermits) {
            super(initialPermits);
            this.wq_ = new FIFOWaitQueue();
        }
        
        public void acquireUninterruptibly(final int n) {
            if (this.precheck(n)) {
                return;
            }
            final WaitQueue.WaitNode w = new Node(n);
            w.doWaitUninterruptibly(this);
        }
        
        public void acquire(final int n) throws InterruptedException {
            if (Thread.interrupted()) {
                throw new InterruptedException();
            }
            if (this.precheck(n)) {
                return;
            }
            final WaitQueue.WaitNode w = new Node(n);
            w.doWait(this);
        }
        
        public boolean attempt(final int n, final long nanos) throws InterruptedException {
            if (Thread.interrupted()) {
                throw new InterruptedException();
            }
            if (this.precheck(n)) {
                return true;
            }
            if (nanos <= 0L) {
                return false;
            }
            final WaitQueue.WaitNode w = new Node(n);
            return w.doTimedWait(this, nanos);
        }
        
        protected synchronized boolean precheck(final int n) {
            final boolean pass = this.permits_ >= n;
            if (pass) {
                this.permits_ -= n;
            }
            return pass;
        }
        
        public synchronized boolean recheck(final WaitQueue.WaitNode w) {
            final Node node = (Node)w;
            final boolean pass = this.permits_ >= node.requests;
            if (pass) {
                this.permits_ -= node.requests;
            }
            else {
                this.wq_.insert(w);
            }
            return pass;
        }
        
        public void takeOver(final WaitQueue.WaitNode n) {
        }
        
        protected synchronized Node getSignallee(final int n) {
            final Node w = (Node)this.wq_.extract();
            this.permits_ += n;
            if (w == null) {
                return null;
            }
            if (w.requests > this.permits_) {
                this.wq_.putBack(w);
                return null;
            }
            this.permits_ -= w.requests;
            return w;
        }
        
        public void release(int n) {
            if (n < 0) {
                throw new IllegalArgumentException("Negative argument");
            }
            while (true) {
                final Node w = this.getSignallee(n);
                if (w == null) {
                    return;
                }
                if (w.signal(this)) {
                    return;
                }
                n = w.requests;
            }
        }
        
        public synchronized boolean hasQueuedThreads() {
            return this.wq_.hasNodes();
        }
        
        public synchronized int getQueueLength() {
            return this.wq_.getLength();
        }
        
        public synchronized Collection getQueuedThreads() {
            return this.wq_.getWaitingThreads();
        }
        
        private void readObject(final ObjectInputStream in) throws IOException, ClassNotFoundException {
            in.defaultReadObject();
            synchronized (this) {
                this.wq_ = new FIFOWaitQueue();
            }
        }
        
        static final class Node extends WaitQueue.WaitNode
        {
            final int requests;
            
            Node(final int requests) {
                this.requests = requests;
            }
        }
    }
}
