// 
// Decompiled by Procyon v0.5.36
// 

package edu.emory.mathcs.backport.java.util.concurrent.atomic;

import java.io.Serializable;

public class AtomicIntegerArray implements Serializable
{
    private static final long serialVersionUID = 2862133569453604235L;
    private final int[] array;
    
    public AtomicIntegerArray(final int length) {
        this.array = new int[length];
    }
    
    public AtomicIntegerArray(final int[] array) {
        if (array == null) {
            throw new NullPointerException();
        }
        final int length = array.length;
        System.arraycopy(array, 0, this.array = new int[length], 0, array.length);
    }
    
    public final int length() {
        return this.array.length;
    }
    
    public final synchronized int get(final int i) {
        return this.array[i];
    }
    
    public final synchronized void set(final int i, final int newValue) {
        this.array[i] = newValue;
    }
    
    public final synchronized void lazySet(final int i, final int newValue) {
        this.array[i] = newValue;
    }
    
    public final synchronized int getAndSet(final int i, final int newValue) {
        final int old = this.array[i];
        this.array[i] = newValue;
        return old;
    }
    
    public final synchronized boolean compareAndSet(final int i, final int expect, final int update) {
        if (this.array[i] == expect) {
            this.array[i] = update;
            return true;
        }
        return false;
    }
    
    public final synchronized boolean weakCompareAndSet(final int i, final int expect, final int update) {
        if (this.array[i] == expect) {
            this.array[i] = update;
            return true;
        }
        return false;
    }
    
    public final synchronized int getAndIncrement(final int i) {
        return this.array[i]++;
    }
    
    public final synchronized int getAndDecrement(final int i) {
        return this.array[i]--;
    }
    
    public final synchronized int getAndAdd(final int i, final int delta) {
        final int old = this.array[i];
        final int[] array = this.array;
        array[i] += delta;
        return old;
    }
    
    public final synchronized int incrementAndGet(final int i) {
        return ++this.array[i];
    }
    
    public final synchronized int decrementAndGet(final int i) {
        final int[] array = this.array;
        return --array[i];
    }
    
    public final synchronized int addAndGet(final int i, final int delta) {
        final int[] array = this.array;
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
