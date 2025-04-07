// 
// Decompiled by Procyon v0.5.36
// 

package edu.emory.mathcs.backport.java.util.concurrent.atomic;

import java.io.Serializable;

public class AtomicReferenceArray implements Serializable
{
    private static final long serialVersionUID = -6209656149925076980L;
    private final Object[] array;
    
    public AtomicReferenceArray(final int length) {
        this.array = new Object[length];
    }
    
    public AtomicReferenceArray(final Object[] array) {
        if (array == null) {
            throw new NullPointerException();
        }
        final int length = array.length;
        System.arraycopy(array, 0, this.array = new Object[length], 0, array.length);
    }
    
    public final int length() {
        return this.array.length;
    }
    
    public final synchronized Object get(final int i) {
        return this.array[i];
    }
    
    public final synchronized void set(final int i, final Object newValue) {
        this.array[i] = newValue;
    }
    
    public final synchronized void lazySet(final int i, final Object newValue) {
        this.array[i] = newValue;
    }
    
    public final synchronized Object getAndSet(final int i, final Object newValue) {
        final Object old = this.array[i];
        this.array[i] = newValue;
        return old;
    }
    
    public final synchronized boolean compareAndSet(final int i, final Object expect, final Object update) {
        if (this.array[i] == expect) {
            this.array[i] = update;
            return true;
        }
        return false;
    }
    
    public final synchronized boolean weakCompareAndSet(final int i, final Object expect, final Object update) {
        if (this.array[i] == expect) {
            this.array[i] = update;
            return true;
        }
        return false;
    }
    
    public synchronized String toString() {
        if (this.array.length == 0) {
            return "[]";
        }
        final StringBuffer buf = new StringBuffer();
        for (int i = 0; i < this.array.length; ++i) {
            if (i == 0) {
                buf.append('[');
            }
            else {
                buf.append(", ");
            }
            buf.append(String.valueOf(this.array[i]));
        }
        buf.append("]");
        return buf.toString();
    }
}
