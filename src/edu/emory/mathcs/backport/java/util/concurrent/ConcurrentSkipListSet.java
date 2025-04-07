// 
// Decompiled by Procyon v0.5.36
// 

package edu.emory.mathcs.backport.java.util.concurrent;

import java.util.Map;
import java.util.Set;
import java.util.Iterator;
import java.util.SortedMap;
import java.util.SortedSet;
import java.util.Collection;
import java.util.Comparator;
import java.io.Serializable;
import edu.emory.mathcs.backport.java.util.NavigableSet;
import edu.emory.mathcs.backport.java.util.AbstractSet;

public class ConcurrentSkipListSet extends AbstractSet implements NavigableSet, Cloneable, Serializable
{
    private static final long serialVersionUID = -2479143111061671589L;
    private final ConcurrentNavigableMap m;
    
    public ConcurrentSkipListSet() {
        this.m = new ConcurrentSkipListMap();
    }
    
    public ConcurrentSkipListSet(final Comparator comparator) {
        this.m = new ConcurrentSkipListMap(comparator);
    }
    
    public ConcurrentSkipListSet(final Collection c) {
        this.m = new ConcurrentSkipListMap();
        this.addAll(c);
    }
    
    public ConcurrentSkipListSet(final SortedSet s) {
        this.m = new ConcurrentSkipListMap(s.comparator());
        this.addAll(s);
    }
    
    ConcurrentSkipListSet(final ConcurrentNavigableMap m) {
        this.m = m;
    }
    
    public Object clone() {
        if (this.getClass() != ConcurrentSkipListSet.class) {
            throw new UnsupportedOperationException("Can't clone subclasses");
        }
        return new ConcurrentSkipListSet(new ConcurrentSkipListMap(this.m));
    }
    
    public int size() {
        return this.m.size();
    }
    
    public boolean isEmpty() {
        return this.m.isEmpty();
    }
    
    public boolean contains(final Object o) {
        return this.m.containsKey(o);
    }
    
    public boolean add(final Object e) {
        return this.m.putIfAbsent(e, Boolean.TRUE) == null;
    }
    
    public boolean remove(final Object o) {
        return this.m.remove(o, Boolean.TRUE);
    }
    
    public void clear() {
        this.m.clear();
    }
    
    public Iterator iterator() {
        return this.m.navigableKeySet().iterator();
    }
    
    public Iterator descendingIterator() {
        return this.m.descendingKeySet().iterator();
    }
    
    public boolean equals(final Object o) {
        if (o == this) {
            return true;
        }
        if (!(o instanceof Set)) {
            return false;
        }
        final Collection c = (Collection)o;
        try {
            return this.containsAll(c) && c.containsAll(this);
        }
        catch (ClassCastException unused) {
            return false;
        }
        catch (NullPointerException unused2) {
            return false;
        }
    }
    
    public boolean removeAll(final Collection c) {
        boolean modified = false;
        final Iterator i = c.iterator();
        while (i.hasNext()) {
            if (this.remove(i.next())) {
                modified = true;
            }
        }
        return modified;
    }
    
    public Object lower(final Object e) {
        return this.m.lowerKey(e);
    }
    
    public Object floor(final Object e) {
        return this.m.floorKey(e);
    }
    
    public Object ceiling(final Object e) {
        return this.m.ceilingKey(e);
    }
    
    public Object higher(final Object e) {
        return this.m.higherKey(e);
    }
    
    public Object pollFirst() {
        final Map.Entry e = this.m.pollFirstEntry();
        return (e == null) ? null : e.getKey();
    }
    
    public Object pollLast() {
        final Map.Entry e = this.m.pollLastEntry();
        return (e == null) ? null : e.getKey();
    }
    
    public Comparator comparator() {
        return this.m.comparator();
    }
    
    public Object first() {
        return this.m.firstKey();
    }
    
    public Object last() {
        return this.m.lastKey();
    }
    
    public NavigableSet subSet(final Object fromElement, final boolean fromInclusive, final Object toElement, final boolean toInclusive) {
        return new ConcurrentSkipListSet((ConcurrentNavigableMap)this.m.subMap(fromElement, fromInclusive, toElement, toInclusive));
    }
    
    public NavigableSet headSet(final Object toElement, final boolean inclusive) {
        return new ConcurrentSkipListSet((ConcurrentNavigableMap)this.m.headMap(toElement, inclusive));
    }
    
    public NavigableSet tailSet(final Object fromElement, final boolean inclusive) {
        return new ConcurrentSkipListSet((ConcurrentNavigableMap)this.m.tailMap(fromElement, inclusive));
    }
    
    public SortedSet subSet(final Object fromElement, final Object toElement) {
        return this.subSet(fromElement, true, toElement, false);
    }
    
    public SortedSet headSet(final Object toElement) {
        return this.headSet(toElement, false);
    }
    
    public SortedSet tailSet(final Object fromElement) {
        return this.tailSet(fromElement, true);
    }
    
    public NavigableSet descendingSet() {
        return new ConcurrentSkipListSet((ConcurrentNavigableMap)this.m.descendingMap());
    }
}
