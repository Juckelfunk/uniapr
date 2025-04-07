// 
// Decompiled by Procyon v0.5.36
// 

package edu.emory.mathcs.backport.java.util.concurrent.atomic;

import java.io.Serializable;

public class AtomicLong extends Number implements Serializable
{
    private static final long serialVersionUID = 1927816293512124184L;
    private volatile long value;
    
    public AtomicLong(final long initialValue) {
        this.value = initialValue;
    }
    
    public AtomicLong() {
    }
    
    public final long get() {
        return this.value;
    }
    
    public final synchronized void set(final long newValue) {
        this.value = newValue;
    }
    
    public final synchronized void lazySet(final long newValue) {
        this.value = newValue;
    }
    
    public final synchronized long getAndSet(final long newValue) {
        final long old = this.value;
        this.value = newValue;
        return old;
    }
    
    public final synchronized boolean compareAndSet(final long expect, final long update) {
        if (this.value == expect) {
            this.value = update;
            return true;
        }
        return false;
    }
    
    public final synchronized boolean weakCompareAndSet(final long expect, final long update) {
        if (this.value == expect) {
            this.value = update;
            return true;
        }
        return false;
    }
    
    public final synchronized long getAndIncrement() {
        return this.value++;
    }
    
    public final synchronized long getAndDecrement() {
        return this.value--;
    }
    
    public final synchronized long getAndAdd(final long delta) {
        final long old = this.value;
        this.value += delta;
        return old;
    }
    
    public final synchronized long incrementAndGet() {
        return ++this.value;
    }
    
    public final synchronized long decrementAndGet() {
        return --this.value;
    }
    
    public final synchronized long addAndGet(final long delta) {
        return this.value += delta;
    }
    
    public String toString() {
        return Long.toString(this.get());
    }
    
    public int intValue() {
        return (int)this.get();
    }
    
    public long longValue() {
        return this.get();
    }
    
    public float floatValue() {
        return (float)this.get();
    }
    
    public double doubleValue() {
        return (double)this.get();
    }
}
