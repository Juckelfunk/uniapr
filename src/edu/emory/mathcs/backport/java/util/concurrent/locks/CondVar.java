// 
// Decompiled by Procyon v0.5.36
// 

package edu.emory.mathcs.backport.java.util.concurrent.locks;

import java.util.Collection;
import java.util.Date;
import edu.emory.mathcs.backport.java.util.concurrent.helpers.Utils;
import edu.emory.mathcs.backport.java.util.concurrent.TimeUnit;
import java.io.Serializable;

class CondVar implements Condition, Serializable
{
    protected final ExclusiveLock lock;
    
    CondVar(final ExclusiveLock lock) {
        this.lock = lock;
    }
    
    public void awaitUninterruptibly() {
        final int holdCount = this.lock.getHoldCount();
        if (holdCount == 0) {
            throw new IllegalMonitorStateException();
        }
        boolean wasInterrupted = Thread.interrupted();
        try {
            synchronized (this) {
                for (int i = holdCount; i > 0; --i) {
                    this.lock.unlock();
                }
                try {
                    this.wait();
                }
                catch (InterruptedException ex) {
                    wasInterrupted = true;
                }
            }
        }
        finally {
            for (int j = holdCount; j > 0; --j) {
                this.lock.lock();
            }
            if (wasInterrupted) {
                Thread.currentThread().interrupt();
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
        try {
            synchronized (this) {
                for (int i = holdCount; i > 0; --i) {
                    this.lock.unlock();
                }
                try {
                    this.wait();
                }
                catch (InterruptedException ex) {
                    this.notify();
                    throw ex;
                }
            }
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
        boolean success = false;
        try {
            synchronized (this) {
                for (int i = holdCount; i > 0; --i) {
                    this.lock.unlock();
                }
                try {
                    if (nanos > 0L) {
                        final long start = Utils.nanoTime();
                        TimeUnit.NANOSECONDS.timedWait(this, nanos);
                        success = (Utils.nanoTime() - start < nanos);
                    }
                }
                catch (InterruptedException ex) {
                    this.notify();
                    throw ex;
                }
            }
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
        final int holdCount = this.lock.getHoldCount();
        if (holdCount == 0) {
            throw new IllegalMonitorStateException();
        }
        final long abstime = deadline.getTime();
        if (Thread.interrupted()) {
            throw new InterruptedException();
        }
        boolean success = false;
        try {
            synchronized (this) {
                for (int i = holdCount; i > 0; --i) {
                    this.lock.unlock();
                }
                try {
                    final long start = System.currentTimeMillis();
                    final long msecs = abstime - start;
                    if (msecs > 0L) {
                        this.wait(msecs);
                        success = (System.currentTimeMillis() - start < msecs);
                    }
                }
                catch (InterruptedException ex) {
                    this.notify();
                    throw ex;
                }
            }
        }
        finally {
            for (int j = holdCount; j > 0; --j) {
                this.lock.lock();
            }
        }
        return success;
    }
    
    public synchronized void signal() {
        if (!this.lock.isHeldByCurrentThread()) {
            throw new IllegalMonitorStateException();
        }
        this.notify();
    }
    
    public synchronized void signalAll() {
        if (!this.lock.isHeldByCurrentThread()) {
            throw new IllegalMonitorStateException();
        }
        this.notifyAll();
    }
    
    protected ExclusiveLock getLock() {
        return this.lock;
    }
    
    protected boolean hasWaiters() {
        throw new UnsupportedOperationException("Use FAIR version");
    }
    
    protected int getWaitQueueLength() {
        throw new UnsupportedOperationException("Use FAIR version");
    }
    
    protected Collection getWaitingThreads() {
        throw new UnsupportedOperationException("Use FAIR version");
    }
    
    interface ExclusiveLock extends Lock
    {
        boolean isHeldByCurrentThread();
        
        int getHoldCount();
    }
}
