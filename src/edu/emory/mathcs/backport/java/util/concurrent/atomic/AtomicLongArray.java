// 
// Decompiled by Procyon v0.5.36
// 

package edu.emory.mathcs.backport.java.util.concurrent.atomic;

import java.io.Serializable;

public class AtomicLongArray implements Serializable
{
    private static final long serialVersionUID = -2308431214976778248L;
    private final long[] array;
    
    public AtomicLongArray(final int length) {
        this.array = new long[length];
    }
    
    public AtomicLongArray(final long[] array) {
        if (array == null) {
            throw new NullPointerException();
        }
        final int length = array.length;
        System.arraycopy(array, 0, this.array = new long[length], 0, array.length);
    }
    
    public final int length() {
        return this.array.length;
    }
    
    public final synchronized long get(final int i) {
        return this.array[i];
    }
    
    public final synchronized void set(final int i, final long newValue) {
        this.array[i] = newValue;
    }
    
    public final synchronized void lazySet(final int i, final long newValue) {
        this.array[i] = newValue;
    }
    
    public final synchronized long getAndSet(final int i, final long newValue) {
        final long old = this.array[i];
        this.array[i] = newValue;
        return old;
    }
    
    public final synchronized boolean compareAndSet(final int i, final long expect, final long update) {
        if (this.array[i] == expect) {
            this.array[i] = update;
            return true;
        }
        return false;
    }
    
    public final synchronized boolean weakCompareAndSet(final int i, final long expect, final long update) {
        if (this.array[i] == expect) {
            this.array[i] = update;
            return true;
        }
        return false;
    }
    
    public final synchronized long getAndIncrement(final int i) {
        return this.array[i]++;
    }
    
    public final synchronized long getAndDecrement(final int i) {
        return this.array[i]--;
    }
    
    public final synchronized long getAndAdd(final int i, final long delta) {
        final long old = this.array[i];
        final long[] array = this.array;
        array[i] += delta;
        return old;
    }
    
    public final synchronized long incrementAndGet(final int i) {
        final long[] array = this.array;
        return ++array[i];
    }
    
    public final synchronized long decrementAndGet(final int i) {
        final long[] array = this.array;
        return --array[i];
    }
    
    public synchronized long addAndGet(final int i, final long delta) {
        final long[] array = this.array;
        return array[i] += delta;
    }
    
    public synchronized String toString() {
        if (this.array.length == 0) {
            return "[]";
        }
        final StringBuffer buf = new StringBuffer();
        buf.append('[');
        buf.append(this.array[0]);
        for (int i = 1; i < this.array.length; ++i) {
            buf.append(", ");
            buf.append(this.array[i]);
        }
        buf.append("]");
        return buf.toString();
    }
}
