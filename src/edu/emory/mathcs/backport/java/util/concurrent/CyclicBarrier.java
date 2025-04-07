// 
// Decompiled by Procyon v0.5.36
// 

package edu.emory.mathcs.backport.java.util.concurrent;

import edu.emory.mathcs.backport.java.util.concurrent.helpers.Utils;

public class CyclicBarrier
{
    private final Object lock;
    private final int parties;
    private final Runnable barrierCommand;
    private Generation generation;
    private int count;
    
    private void nextGeneration() {
        this.lock.notifyAll();
        this.count = this.parties;
        this.generation = new Generation();
    }
    
    private void breakBarrier() {
        this.generation.broken = true;
        this.count = this.parties;
        this.lock.notifyAll();
    }
    
    private int dowait(final boolean timed, long nanos) throws InterruptedException, BrokenBarrierException, TimeoutException {
        synchronized (this.lock) {
            final Generation g = this.generation;
            if (g.broken) {
                throw new BrokenBarrierException();
            }
            if (Thread.interrupted()) {
                this.breakBarrier();
                throw new InterruptedException();
            }
            final int count = this.count - 1;
            this.count = count;
            final int index = count;
            if (index == 0) {
                boolean ranAction = false;
                try {
                    final Runnable command = this.barrierCommand;
                    if (command != null) {
                        command.run();
                    }
                    ranAction = true;
                    this.nextGeneration();
                    return 0;
                }
                finally {
                    if (!ranAction) {
                        this.breakBarrier();
                    }
                }
            }
            final long deadline = timed ? (Utils.nanoTime() + nanos) : 0L;
            while (true) {
                try {
                    if (!timed) {
                        this.lock.wait();
                    }
                    else if (nanos > 0L) {
                        TimeUnit.NANOSECONDS.timedWait(this.lock, nanos);
                    }
                }
                catch (InterruptedException ie) {
                    if (g == this.generation && !g.broken) {
                        this.breakBarrier();
                        throw ie;
                    }
                    Thread.currentThread().interrupt();
                }
                if (g.broken) {
                    throw new BrokenBarrierException();
                }
                if (g != this.generation) {
                    return index;
                }
                if (timed && nanos <= 0L) {
                    this.breakBarrier();
                    throw new TimeoutException();
                }
                nanos = deadline - Utils.nanoTime();
            }
        }
    }
    
    public CyclicBarrier(final int parties, final Runnable barrierAction) {
        this.lock = new Object();
        this.generation = new Generation();
        if (parties <= 0) {
            throw new IllegalArgumentException();
        }
        this.parties = parties;
        this.count = parties;
        this.barrierCommand = barrierAction;
    }
    
    public CyclicBarrier(final int parties) {
        this(parties, null);
    }
    
    public int getParties() {
        return this.parties;
    }
    
    public int await() throws InterruptedException, BrokenBarrierException {
        try {
            return this.dowait(false, 0L);
        }
        catch (TimeoutException toe) {
            throw new Error(toe);
        }
    }
    
    public int await(final long timeout, final TimeUnit unit) throws InterruptedException, BrokenBarrierException, TimeoutException {
        return this.dowait(true, unit.toNanos(timeout));
    }
    
    public boolean isBroken() {
        synchronized (this.lock) {
            return this.generation.broken;
        }
    }
    
    public void reset() {
        synchronized (this.lock) {
            this.breakBarrier();
            this.nextGeneration();
        }
    }
    
    public int getNumberWaiting() {
        synchronized (this.lock) {
            return this.parties - this.count;
        }
    }
    
    private static class Generation
    {
        boolean broken;
        
        private Generation() {
            this.broken = false;
        }
    }
}
