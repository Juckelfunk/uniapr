// 
// Decompiled by Procyon v0.5.36
// 

package edu.emory.mathcs.backport.java.util.concurrent.locks;

import java.io.IOException;
import java.io.ObjectInputStream;
import edu.emory.mathcs.backport.java.util.concurrent.helpers.FIFOWaitQueue;
import edu.emory.mathcs.backport.java.util.concurrent.helpers.WaitQueue;
import edu.emory.mathcs.backport.java.util.concurrent.helpers.Utils;
import java.util.Collection;
import edu.emory.mathcs.backport.java.util.concurrent.TimeUnit;
import java.io.Serializable;

public class ReentrantLock implements Lock, Serializable, CondVar.ExclusiveLock
{
    private static final long serialVersionUID = 7373984872572414699L;
    private final Sync sync;
    
    public ReentrantLock() {
        this.sync = new NonfairSync();
    }
    
    public ReentrantLock(final boolean fair) {
        this.sync = (fair ? new FairSync() : new NonfairSync());
    }
    
    public void lock() {
        this.sync.lock();
    }
    
    public void lockInterruptibly() throws InterruptedException {
        this.sync.lockInterruptibly();
    }
    
    public boolean tryLock() {
        return this.sync.tryLock();
    }
    
    public boolean tryLock(final long timeout, final TimeUnit unit) throws InterruptedException {
        return this.sync.tryLock(unit.toNanos(timeout));
    }
    
    public void unlock() {
        this.sync.unlock();
    }
    
    public Condition newCondition() {
        return this.isFair() ? new FIFOCondVar(this) : new CondVar(this);
    }
    
    public int getHoldCount() {
        return this.sync.getHoldCount();
    }
    
    public boolean isHeldByCurrentThread() {
        return this.sync.isHeldByCurrentThread();
    }
    
    public boolean isLocked() {
        return this.sync.isLocked();
    }
    
    public final boolean isFair() {
        return this.sync.isFair();
    }
    
    protected Thread getOwner() {
        return this.sync.getOwner();
    }
    
    public final boolean hasQueuedThreads() {
        return this.sync.hasQueuedThreads();
    }
    
    public final boolean hasQueuedThread(final Thread thread) {
        return this.sync.isQueued(thread);
    }
    
    public final int getQueueLength() {
        return this.sync.getQueueLength();
    }
    
    protected Collection getQueuedThreads() {
        return this.sync.getQueuedThreads();
    }
    
    public boolean hasWaiters(final Condition condition) {
        return this.asCondVar(condition).hasWaiters();
    }
    
    public int getWaitQueueLength(final Condition condition) {
        return this.asCondVar(condition).getWaitQueueLength();
    }
    
    protected Collection getWaitingThreads(final Condition condition) {
        return this.asCondVar(condition).getWaitingThreads();
    }
    
    public String toString() {
        final Thread o = this.getOwner();
        return super.toString() + ((o == null) ? "[Unlocked]" : ("[Locked by thread " + o.getName() + "]"));
    }
    
    private CondVar asCondVar(final Condition condition) {
        if (condition == null) {
            throw new NullPointerException();
        }
        if (!(condition instanceof CondVar)) {
            throw new IllegalArgumentException("not owner");
        }
        final CondVar condVar = (CondVar)condition;
        if (condVar.lock != this) {
            throw new IllegalArgumentException("not owner");
        }
        return condVar;
    }
    
    abstract static class Sync implements Serializable
    {
        private static final long serialVersionUID = -5179523762034025860L;
        protected transient Thread owner_;
        protected transient int holds_;
        
        protected Sync() {
            this.owner_ = null;
            this.holds_ = 0;
        }
        
        public abstract void lock();
        
        public abstract void lockInterruptibly() throws InterruptedException;
        
        final void incHolds() {
            final int nextHolds = ++this.holds_;
            if (nextHolds < 0) {
                throw new Error("Maximum lock count exceeded");
            }
            this.holds_ = nextHolds;
        }
        
        public boolean tryLock() {
            final Thread caller = Thread.currentThread();
            synchronized (this) {
                if (this.owner_ == null) {
                    this.owner_ = caller;
                    this.holds_ = 1;
                    return true;
                }
                if (caller == this.owner_) {
                    this.incHolds();
                    return true;
                }
            }
            return false;
        }
        
        public abstract boolean tryLock(final long p0) throws InterruptedException;
        
        public abstract void unlock();
        
        public synchronized int getHoldCount() {
            return this.isHeldByCurrentThread() ? this.holds_ : 0;
        }
        
        public synchronized boolean isHeldByCurrentThread() {
            return this.holds_ > 0 && Thread.currentThread() == this.owner_;
        }
        
        public synchronized boolean isLocked() {
            return this.owner_ != null;
        }
        
        public abstract boolean isFair();
        
        protected synchronized Thread getOwner() {
            return this.owner_;
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
        
        public boolean isQueued(final Thread thread) {
            throw new UnsupportedOperationException("Use FAIR version");
        }
    }
    
    static final class NonfairSync extends Sync
    {
        private static final long serialVersionUID = 7316153563782823691L;
        
        public void lock() {
            final Thread caller = Thread.currentThread();
            synchronized (this) {
                if (this.owner_ == null) {
                    this.owner_ = caller;
                    this.holds_ = 1;
                    return;
                }
                if (caller == this.owner_) {
                    this.incHolds();
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
                    } while (this.owner_ != null);
                    this.owner_ = caller;
                    this.holds_ = 1;
                }
                finally {
                    if (wasInterrupted) {
                        Thread.currentThread().interrupt();
                    }
                }
            }
        }
        
        public void lockInterruptibly() throws InterruptedException {
            if (Thread.interrupted()) {
                throw new InterruptedException();
            }
            final Thread caller = Thread.currentThread();
            synchronized (this) {
                if (this.owner_ == null) {
                    this.owner_ = caller;
                    this.holds_ = 1;
                    return;
                }
                if (caller == this.owner_) {
                    this.incHolds();
                    return;
                }
                Label_0057: {
                    break Label_0057;
                    try {
                        do {
                            this.wait();
                        } while (this.owner_ != null);
                        this.owner_ = caller;
                        this.holds_ = 1;
                    }
                    catch (InterruptedException ex) {
                        if (this.owner_ == null) {
                            this.notify();
                        }
                        throw ex;
                    }
                }
            }
        }
        
        public boolean tryLock(long nanos) throws InterruptedException {
            if (Thread.interrupted()) {
                throw new InterruptedException();
            }
            final Thread caller = Thread.currentThread();
            synchronized (this) {
                if (this.owner_ == null) {
                    this.owner_ = caller;
                    this.holds_ = 1;
                    return true;
                }
                if (caller == this.owner_) {
                    this.incHolds();
                    return true;
                }
                if (nanos <= 0L) {
                    return false;
                }
                final long deadline = Utils.nanoTime() + nanos;
                try {
                    do {
                        TimeUnit.NANOSECONDS.timedWait(this, nanos);
                        if (caller == this.owner_) {
                            this.incHolds();
                            return true;
                        }
                        if (this.owner_ == null) {
                            this.owner_ = caller;
                            this.holds_ = 1;
                            return true;
                        }
                        nanos = deadline - Utils.nanoTime();
                    } while (nanos > 0L);
                    return false;
                }
                catch (InterruptedException ex) {
                    if (this.owner_ == null) {
                        this.notify();
                    }
                    throw ex;
                }
            }
        }
        
        public synchronized void unlock() {
            if (Thread.currentThread() != this.owner_) {
                throw new IllegalMonitorStateException("Not owner");
            }
            if (--this.holds_ == 0) {
                this.owner_ = null;
                this.notify();
            }
        }
        
        public final boolean isFair() {
            return false;
        }
    }
    
    static final class FairSync extends Sync implements WaitQueue.QueuedSync
    {
        private static final long serialVersionUID = -3000897897090466540L;
        private transient WaitQueue wq_;
        
        FairSync() {
            this.wq_ = new FIFOWaitQueue();
        }
        
        public synchronized boolean recheck(final WaitQueue.WaitNode node) {
            final Thread caller = Thread.currentThread();
            if (this.owner_ == null) {
                this.owner_ = caller;
                this.holds_ = 1;
                return true;
            }
            if (caller == this.owner_) {
                this.incHolds();
                return true;
            }
            this.wq_.insert(node);
            return false;
        }
        
        public synchronized void takeOver(final WaitQueue.WaitNode node) {
            this.owner_ = node.getOwner();
        }
        
        public void lock() {
            final Thread caller = Thread.currentThread();
            synchronized (this) {
                if (this.owner_ == null) {
                    this.owner_ = caller;
                    this.holds_ = 1;
                    return;
                }
                if (caller == this.owner_) {
                    this.incHolds();
                    return;
                }
            }
            final WaitQueue.WaitNode n = new WaitQueue.WaitNode();
            n.doWaitUninterruptibly(this);
        }
        
        public void lockInterruptibly() throws InterruptedException {
            if (Thread.interrupted()) {
                throw new InterruptedException();
            }
            final Thread caller = Thread.currentThread();
            synchronized (this) {
                if (this.owner_ == null) {
                    this.owner_ = caller;
                    this.holds_ = 1;
                    return;
                }
                if (caller == this.owner_) {
                    this.incHolds();
                    return;
                }
            }
            final WaitQueue.WaitNode n = new WaitQueue.WaitNode();
            n.doWait(this);
        }
        
        public boolean tryLock(final long nanos) throws InterruptedException {
            if (Thread.interrupted()) {
                throw new InterruptedException();
            }
            final Thread caller = Thread.currentThread();
            synchronized (this) {
                if (this.owner_ == null) {
                    this.owner_ = caller;
                    this.holds_ = 1;
                    return true;
                }
                if (caller == this.owner_) {
                    this.incHolds();
                    return true;
                }
            }
            final WaitQueue.WaitNode n = new WaitQueue.WaitNode();
            return n.doTimedWait(this, nanos);
        }
        
        protected synchronized WaitQueue.WaitNode getSignallee(final Thread caller) {
            if (caller != this.owner_) {
                throw new IllegalMonitorStateException("Not owner");
            }
            if (this.holds_ >= 2) {
                --this.holds_;
                return null;
            }
            final WaitQueue.WaitNode w = this.wq_.extract();
            if (w == null) {
                this.owner_ = null;
                this.holds_ = 0;
            }
            return w;
        }
        
        public void unlock() {
            final Thread caller = Thread.currentThread();
            while (true) {
                final WaitQueue.WaitNode w = this.getSignallee(caller);
                if (w == null) {
                    return;
                }
                if (w.signal(this)) {
                    return;
                }
            }
        }
        
        public final boolean isFair() {
            return true;
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
        
        public synchronized boolean isQueued(final Thread thread) {
            return this.wq_.isWaiting(thread);
        }
        
        private void readObject(final ObjectInputStream in) throws IOException, ClassNotFoundException {
            in.defaultReadObject();
            synchronized (this) {
                this.wq_ = new FIFOWaitQueue();
            }
        }
    }
}
