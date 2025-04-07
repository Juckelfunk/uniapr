// 
// Decompiled by Procyon v0.5.36
// 

package edu.emory.mathcs.backport.java.util.concurrent;

import java.util.Set;
import java.util.Iterator;
import java.util.Collection;
import java.io.Serializable;
import java.util.AbstractSet;

public class CopyOnWriteArraySet extends AbstractSet implements Serializable
{
    private static final long serialVersionUID = 5457747651344034263L;
    private final CopyOnWriteArrayList al;
    
    public CopyOnWriteArraySet() {
        this.al = new CopyOnWriteArrayList();
    }
    
    public CopyOnWriteArraySet(final Collection c) {
        (this.al = new CopyOnWriteArrayList()).addAllAbsent(c);
    }
    
    public int size() {
        return this.al.size();
    }
    
    public boolean isEmpty() {
        return this.al.isEmpty();
    }
    
    public boolean contains(final Object o) {
        return this.al.contains(o);
    }
    
    public Object[] toArray() {
        return this.al.toArray();
    }
    
    public Object[] toArray(final Object[] a) {
        return this.al.toArray(a);
    }
    
    public void clear() {
        this.al.clear();
    }
    
    public boolean remove(final Object o) {
        return this.al.remove(o);
    }
    
    public boolean add(final Object e) {
        return this.al.addIfAbsent(e);
    }
    
    public boolean containsAll(final Collection c) {
        return this.al.containsAll(c);
    }
    
    public boolean addAll(final Collection c) {
        return this.al.addAllAbsent(c) > 0;
    }
    
    public boolean removeAll(final Collection c) {
        return this.al.removeAll(c);
    }
    
    public boolean retainAll(final Collection c) {
        return this.al.retainAll(c);
    }
    
    public Iterator iterator() {
        return this.al.iterator();
    }
    
    public boolean equals(final Object o) {
        if (o == this) {
            return true;
        }
        if (!(o instanceof Set)) {
            return false;
        }
        final Set set = (Set)o;
        final Iterator it = set.iterator();
        final Object[] elements = this.al.getArray();
        final int len = elements.length;
        final boolean[] matched = new boolean[len];
        int k = 0;
    Label_0051:
        while (it.hasNext()) {
            if (++k > len) {
                return false;
            }
            final Object x = it.next();
            for (int i = 0; i < len; ++i) {
                if (!matched[i] && eq(x, elements[i])) {
                    matched[i] = true;
                    continue Label_0051;
                }
            }
            return false;
        }
        return k == len;
    }
    
    private static boolean eq(final Object o1, final Object o2) {
        return (o1 == null) ? (o2 == null) : o1.equals(o2);
    }
}
