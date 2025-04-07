// 
// Decompiled by Procyon v0.5.36
// 

package edu.emory.mathcs.backport.java.util.concurrent;

import edu.emory.mathcs.backport.java.util.concurrent.helpers.Utils;

public class Exchanger
{
    private final Object lock;
    private Object item;
    private int arrivalCount;
    
    private Object doExchange(final Object x, final boolean timed, long nanos) throws InterruptedException, TimeoutException {
        synchronized (this.lock) {
            final long deadline = timed ? (Utils.nanoTime() + nanos) : 0L;
            while (this.arrivalCount == 2) {
                if (!timed) {
                    this.lock.wait();
                }
                else {
                    if (nanos <= 0L) {
                        throw new TimeoutException();
                    }
                    TimeUnit.NANOSECONDS.timedWait(this.lock, nanos);
                    nanos = deadline - Utils.nanoTime();
                }
            }
            int count = ++this.arrivalCount;
            if (count == 2) {
                final Object other = this.item;
                this.item = x;
                this.lock.notifyAll();
                return other;
            }
            this.item = x;
            InterruptedException interrupted = null;
            try {
                while (this.arrivalCount != 2) {
                    if (!timed) {
                        this.lock.wait();
                    }
                    else {
                        if (nanos <= 0L) {
                            break;
                        }
                        TimeUnit.NANOSECONDS.timedWait(this.lock, nanos);
                        nanos = deadline - Utils.nanoTime();
                    }
                }
            }
            catch (InterruptedException ie) {
                interrupted = ie;
            }
            final Object other = this.item;
            this.item = null;
            count = this.arrivalCount;
            this.arrivalCount = 0;
            this.lock.notifyAll();
            if (count == 2) {
                if (interrupted != null) {
                    Thread.currentThread().interrupt();
                }
                return other;
            }
            if (interrupted != null) {
                throw interrupted;
            }
            throw new TimeoutException();
        }
    }
    
    public Exchanger() {
        this.lock = new Object();
    }
    
    public Object exchange(final Object x) throws InterruptedException {
        try {
            return this.doExchange(x, false, 0L);
        }
        catch (TimeoutException cannotHappen) {
            throw new Error(cannotHappen);
        }
    }
    
    public Object exchange(final Object x, final long timeout, final TimeUnit unit) throws InterruptedException, TimeoutException {
        return this.doExchange(x, true, unit.toNanos(timeout));
    }
}
