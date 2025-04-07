// 
// Decompiled by Procyon v0.5.36
// 

package edu.emory.mathcs.backport.java.util.concurrent.atomic;

import java.io.Serializable;

public class AtomicReference implements Serializable
{
    private static final long serialVersionUID = -1848883965231344442L;
    private volatile Object value;
    
    public AtomicReference(final Object initialValue) {
        this.value = initialValue;
    }
    
    public AtomicReference() {
    }
    
    public final Object get() {
        return this.value;
    }
    
    public final synchronized void set(final Object newValue) {
        this.value = newValue;
    }
    
    public final synchronized void lazySet(final Object newValue) {
        this.value = newValue;
    }
    
    public final synchronized boolean compareAndSet(final Object expect, final Object update) {
        if (this.value == expect) {
            this.value = update;
            return true;
        }
        return false;
    }
    
    public final synchronized boolean weakCompareAndSet(final Object expect, final Object update) {
        if (this.value == expect) {
            this.value = update;
            return true;
        }
        return false;
    }
    
    public final synchronized Object getAndSet(final Object newValue) {
        final Object old = this.value;
        this.value = newValue;
        return old;
    }
    
    public String toString() {
        return String.valueOf(this.get());
    }
}
