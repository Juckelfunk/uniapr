// 
// Decompiled by Procyon v0.5.36
// 

package edu.emory.mathcs.backport.java.util.concurrent;

import edu.emory.mathcs.backport.java.util.concurrent.helpers.Utils;

public class CountDownLatch
{
    private int count_;
    
    public CountDownLatch(final int count) {
        if (count < 0) {
            throw new IllegalArgumentException("count < 0");
        }
        this.count_ = count;
    }
    
    public void await() throws InterruptedException {
        if (Thread.interrupted()) {
            throw new InterruptedException();
        }
        synchronized (this) {
            while (this.count_ > 0) {
                this.wait();
            }
        }
    }
    
    public boolean await(final long timeout, final TimeUnit unit) throws InterruptedException {
        if (Thread.interrupted()) {
            throw new InterruptedException();
        }
        long nanos = unit.toNanos(timeout);
        synchronized (this) {
            if (this.count_ <= 0) {
                return true;
            }
            if (nanos <= 0L) {
                return false;
            }
            final long deadline = Utils.nanoTime() + nanos;
            do {
                TimeUnit.NANOSECONDS.timedWait(this, nanos);
                if (this.count_ <= 0) {
                    return true;
                }
                nanos = deadline - Utils.nanoTime();
            } while (nanos > 0L);
            return false;
        }
    }
    
    public synchronized void countDown() {
        if (this.count_ == 0) {
            return;
        }
        if (--this.count_ == 0) {
            this.notifyAll();
        }
    }
    
    public long getCount() {
        return this.count_;
    }
    
    public String toString() {
        return super.toString() + "[Count = " + this.getCount() + "]";
    }
}
