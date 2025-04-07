// 
// Decompiled by Procyon v0.5.36
// 

package edu.emory.mathcs.backport.java.util;

import java.util.Iterator;
import java.util.Collection;
import java.util.NoSuchElementException;

public abstract class AbstractQueue extends AbstractCollection implements Queue
{
    protected AbstractQueue() {
    }
    
    public boolean add(final Object e) {
        if (this.offer(e)) {
            return true;
        }
        throw new IllegalStateException("Queue full");
    }
    
    public Object remove() {
        final Object x = this.poll();
        if (x != null) {
            return x;
        }
        throw new NoSuchElementException();
    }
    
    public Object element() {
        final Object x = this.peek();
        if (x != null) {
            return x;
        }
        throw new NoSuchElementException();
    }
    
    public void clear() {
        while (this.poll() != null) {}
    }
    
    public boolean addAll(final Collection c) {
        if (c == null) {
            throw new NullPointerException();
        }
        if (c == this) {
            throw new IllegalArgumentException();
        }
        boolean modified = false;
        final Iterator e = c.iterator();
        while (e.hasNext()) {
            if (this.add(e.next())) {
                modified = true;
            }
        }
        return modified;
    }
}
