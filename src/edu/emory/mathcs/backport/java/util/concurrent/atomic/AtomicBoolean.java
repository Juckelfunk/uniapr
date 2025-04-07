// 
// Decompiled by Procyon v0.5.36
// 

package edu.emory.mathcs.backport.java.util.concurrent.atomic;

import java.io.Serializable;

public class AtomicBoolean implements Serializable
{
    private static final long serialVersionUID = 4654671469794556979L;
    private volatile int value;
    
    public AtomicBoolean(final boolean initialValue) {
        this.value = (initialValue ? 1 : 0);
    }
    
    public AtomicBoolean() {
    }
    
    public final boolean get() {
        return this.value != 0;
    }
    
    public final synchronized boolean compareAndSet(final boolean expect, final boolean update) {
        if (expect == (this.value != 0)) {
            this.value = (update ? 1 : 0);
            return true;
        }
        return false;
    }
    
    public synchronized boolean weakCompareAndSet(final boolean expect, final boolean update) {
        if (expect == (this.value != 0)) {
            this.value = (update ? 1 : 0);
            return true;
        }
        return false;
    }
    
    public final synchronized void set(final boolean newValue) {
        this.value = (newValue ? 1 : 0);
    }
    
    public final synchronized void lazySet(final boolean newValue) {
        this.value = (newValue ? 1 : 0);
    }
    
    public final synchronized boolean getAndSet(final boolean newValue) {
        final int old = this.value;
        this.value = (newValue ? 1 : 0);
        return old != 0;
    }
    
    public String toString() {
        return Boolean.toString(this.get());
    }
}
