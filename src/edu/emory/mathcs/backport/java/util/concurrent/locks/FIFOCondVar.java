// 
// Decompiled by Procyon v0.5.36
// 

package edu.emory.mathcs.backport.java.util.concurrent.locks;

import java.util.Collection;
import java.util.Date;
import edu.emory.mathcs.backport.java.util.concurrent.TimeUnit;
import edu.emory.mathcs.backport.java.util.concurrent.helpers.FIFOWaitQueue;
import edu.emory.mathcs.backport.java.util.concurrent.helpers.WaitQueue;
import java.io.Serializable;

class FIFOCondVar extends CondVar implements Condition, Serializable
{
    private static final WaitQueue.QueuedSync sync;
    private final WaitQueue wq;
    
    FIFOCondVar(final ExclusiveLock lock) {
        super(lock);
        this.wq = new FIFOWaitQueue();
    }
    
    public void awaitUninterruptibly() {
        final int holdCount = this.lock.getHoldCount();
        if (holdCount == 0) {
            throw new IllegalMonitorStateException();
        }
        final WaitQueue.WaitNode n = new WaitQueue.WaitNode();
        this.wq.insert(n);
        for (int i = holdCount; i > 0; --i) {
            this.lock.unlock();
        }
        try {
            n.doWaitUninterruptibly(FIFOCondVar.sync);
        }
        finally {
            for (int j = holdCount; j > 0; --j) {
                this.lock.lock();
            }
        }
    }
    
    public void await() throws InterruptedException {
        final int holdCount = this.lock.getHoldCount();
        if (holdCount == 0) {
            throw new IllegalMonitorStateException();
        }
        if (Thread.interrupted()) {
            throw new InterruptedException();
        }
        final WaitQueue.WaitNode n = new WaitQueue.WaitNode();
        this.wq.insert(n);
        for (int i = holdCount; i > 0; --i) {
            this.lock.unlock();
        }
        try {
            n.doWait(FIFOCondVar.sync);
        }
        finally {
            for (int j = holdCount; j > 0; --j) {
                this.lock.lock();
            }
        }
    }
    
    public boolean await(final long timeout, final TimeUnit unit) throws InterruptedException {
        final int holdCount = this.lock.getHoldCount();
        if (holdCount == 0) {
            throw new IllegalMonitorStateException();
        }
        if (Thread.interrupted()) {
            throw new InterruptedException();
        }
        final long nanos = unit.toNanos(timeout);
        final WaitQueue.WaitNode n = new WaitQueue.WaitNode();
        this.wq.insert(n);
        boolean success = false;
        for (int i = holdCount; i > 0; --i) {
            this.lock.unlock();
        }
        try {
            success = n.doTimedWait(FIFOCondVar.sync, nanos);
        }
        finally {
            for (int j = holdCount; j > 0; --j) {
                this.lock.lock();
            }
        }
        return success;
    }
    
    public boolean awaitUntil(final Date deadline) throws InterruptedException {
        if (deadline == null) {
            throw new NullPointerException();
        }
        final long abstime = deadline.getTime();
        final long start = System.currentTimeMillis();
        final long msecs = abstime - start;
        return this.await(msecs, TimeUnit.MILLISECONDS);
    }
    
    public void signal() {
        if (!this.lock.isHeldByCurrentThread()) {
            throw new IllegalMonitorStateException();
        }
        while (true) {
            final WaitQueue.WaitNode w = this.wq.extract();
            if (w == null) {
                return;
            }
            if (w.signal(FIFOCondVar.sync)) {
                return;
            }
        }
    }
    
    public void signalAll() {
        if (!this.lock.isHeldByCurrentThread()) {
            throw new IllegalMonitorStateException();
        }
        while (true) {
            final WaitQueue.WaitNode w = this.wq.extract();
            if (w == null) {
                break;
            }
            w.signal(FIFOCondVar.sync);
        }
    }
    
    protected boolean hasWaiters() {
        if (!this.lock.isHeldByCurrentThread()) {
            throw new IllegalMonitorStateException();
        }
        return this.wq.hasNodes();
    }
    
    protected int getWaitQueueLength() {
        if (!this.lock.isHeldByCurrentThread()) {
            throw new IllegalMonitorStateException();
        }
        return this.wq.getLength();
    }
    
    protected Collection getWaitingThreads() {
        if (!this.lock.isHeldByCurrentThread()) {
            throw new IllegalMonitorStateException();
        }
        return this.wq.getWaitingThreads();
    }
    
    static {
        sync = new WaitQueue.QueuedSync() {
            public boolean recheck(final WaitQueue.WaitNode node) {
                return false;
            }
            
            public void takeOver(final WaitQueue.WaitNode node) {
            }
        };
    }
}
