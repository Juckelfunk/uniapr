// 
// Decompiled by Procyon v0.5.36
// 

package edu.emory.mathcs.backport.java.util.concurrent.helpers;

import edu.emory.mathcs.backport.java.util.concurrent.TimeUnit;
import java.util.Collection;

public abstract class WaitQueue
{
    public abstract void insert(final WaitNode p0);
    
    public abstract WaitNode extract();
    
    public abstract void putBack(final WaitNode p0);
    
    public abstract boolean hasNodes();
    
    public abstract int getLength();
    
    public abstract Collection getWaitingThreads();
    
    public abstract boolean isWaiting(final Thread p0);
    
    public static class WaitNode
    {
        boolean waiting;
        WaitNode next;
        final Thread owner;
        
        public WaitNode() {
            this.waiting = true;
            this.next = null;
            this.owner = Thread.currentThread();
        }
        
        public Thread getOwner() {
            return this.owner;
        }
        
        public synchronized boolean signal(final QueuedSync sync) {
            final boolean signalled = this.waiting;
            if (signalled) {
                this.waiting = false;
                this.notify();
                sync.takeOver(this);
            }
            return signalled;
        }
        
        public synchronized boolean doTimedWait(final QueuedSync sync, long nanos) throws InterruptedException {
            if (sync.recheck(this) || !this.waiting) {
                return true;
            }
            if (nanos <= 0L) {
                return this.waiting = false;
            }
            final long deadline = Utils.nanoTime() + nanos;
            try {
                do {
                    TimeUnit.NANOSECONDS.timedWait(this, nanos);
                    if (!this.waiting) {
                        return true;
                    }
                    nanos = deadline - Utils.nanoTime();
                } while (nanos > 0L);
                return this.waiting = false;
            }
            catch (InterruptedException ex) {
                if (this.waiting) {
                    this.waiting = false;
                    throw ex;
                }
                Thread.currentThread().interrupt();
                return true;
            }
        }
        
        public synchronized void doWait(final QueuedSync sync) throws InterruptedException {
            if (!sync.recheck(this)) {
                try {
                    while (this.waiting) {
                        this.wait();
                    }
                }
                catch (InterruptedException ex) {
                    if (this.waiting) {
                        this.waiting = false;
                        throw ex;
                    }
                    Thread.currentThread().interrupt();
                }
            }
        }
        
        public synchronized void doWaitUninterruptibly(final QueuedSync sync) {
            if (!sync.recheck(this)) {
                boolean wasInterrupted = Thread.interrupted();
                try {
                    while (this.waiting) {
                        try {
                            this.wait();
                        }
                        catch (InterruptedException ex) {
                            wasInterrupted = true;
                        }
                    }
                }
                finally {
                    if (wasInterrupted) {
                        Thread.currentThread().interrupt();
                    }
                }
            }
        }
    }
    
    public interface QueuedSync
    {
        boolean recheck(final WaitNode p0);
        
        void takeOver(final WaitNode p0);
    }
}
