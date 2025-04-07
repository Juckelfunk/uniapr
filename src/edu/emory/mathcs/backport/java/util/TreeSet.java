// 
// Decompiled by Procyon v0.5.36
// 

package edu.emory.mathcs.backport.java.util;

import java.util.NoSuchElementException;
import java.io.ObjectInputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.util.SortedMap;
import java.util.Iterator;
import java.util.Map;
import java.util.SortedSet;
import java.util.Collection;
import java.util.Comparator;
import java.io.Serializable;
import java.util.AbstractSet;

public class TreeSet extends AbstractSet implements NavigableSet, Cloneable, Serializable
{
    private static final long serialVersionUID = -2479143000061671589L;
    private static final Object PRESENT;
    private transient NavigableMap map;
    
    public TreeSet() {
        this.map = new TreeMap();
    }
    
    public TreeSet(final Comparator comparator) {
        this.map = new TreeMap(comparator);
    }
    
    public TreeSet(final Collection c) {
        this.map = new TreeMap();
        this.addAll(c);
    }
    
    public TreeSet(final SortedSet s) {
        this.map = new TreeMap(s.comparator());
        this.addAll(s);
    }
    
    private TreeSet(final NavigableMap map) {
        this.map = map;
    }
    
    public Object lower(final Object e) {
        return this.map.lowerKey(e);
    }
    
    public Object floor(final Object e) {
        return this.map.floorKey(e);
    }
    
    public Object ceiling(final Object e) {
        return this.map.ceilingKey(e);
    }
    
    public Object higher(final Object e) {
        return this.map.higherKey(e);
    }
    
    public Object pollFirst() {
        final Map.Entry e = this.map.pollFirstEntry();
        return (e != null) ? e.getKey() : null;
    }
    
    public Object pollLast() {
        final Map.Entry e = this.map.pollLastEntry();
        return (e != null) ? e.getKey() : null;
    }
    
    public Iterator iterator() {
        return this.map.keySet().iterator();
    }
    
    public Iterator descendingIterator() {
        return this.map.descendingKeySet().iterator();
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
    
    public NavigableSet subSet(final Object fromElement, final boolean fromInclusive, final Object toElement, final boolean toInclusive) {
        return new TreeSet(this.map.subMap(fromElement, fromInclusive, toElement, toInclusive));
    }
    
    public NavigableSet headSet(final Object toElement, final boolean toInclusive) {
        return new TreeSet(this.map.headMap(toElement, toInclusive));
    }
    
    public NavigableSet tailSet(final Object fromElement, final boolean fromInclusive) {
        return new TreeSet(this.map.tailMap(fromElement, fromInclusive));
    }
    
    public NavigableSet descendingSet() {
        return new TreeSet(this.map.descendingMap());
    }
    
    public Comparator comparator() {
        return this.map.comparator();
    }
    
    public Object first() {
        return this.map.firstKey();
    }
    
    public Object last() {
        return this.map.lastKey();
    }
    
    public int size() {
        return this.map.size();
    }
    
    public boolean isEmpty() {
        return this.map.isEmpty();
    }
    
    public boolean contains(final Object o) {
        return this.map.containsKey(o);
    }
    
    public Object[] toArray() {
        return this.map.keySet().toArray();
    }
    
    public Object[] toArray(final Object[] a) {
        return this.map.keySet().toArray(a);
    }
    
    public boolean add(final Object o) {
        return this.map.put(o, TreeSet.PRESENT) == null;
    }
    
    public boolean remove(final Object o) {
        return this.map.remove(o) != null;
    }
    
    public boolean addAll(final Collection c) {
        if (this.map.size() == 0 && c.size() > 0 && c instanceof SortedSet && this.map instanceof TreeMap && eq(((SortedSet)c).comparator(), this.comparator())) {
            ((TreeMap)this.map).buildFromSorted(new MapIterator(c.iterator()), c.size());
            return true;
        }
        return super.addAll(c);
    }
    
    public void clear() {
        this.map.clear();
    }
    
    public Object clone() {
        TreeSet clone;
        try {
            clone = (TreeSet)super.clone();
        }
        catch (CloneNotSupportedException e) {
            throw new InternalError();
        }
        clone.map = new TreeMap(this.map);
        return clone;
    }
    
    private static boolean eq(final Object o1, final Object o2) {
        return (o1 == null) ? (o2 == null) : o1.equals(o2);
    }
    
    private void writeObject(final ObjectOutputStream out) throws IOException {
        out.defaultWriteObject();
        out.writeObject(this.map.comparator());
        out.writeInt(this.map.size());
        final Iterator itr = this.map.keySet().iterator();
        while (itr.hasNext()) {
            out.writeObject(itr.next());
        }
    }
    
    private void readObject(final ObjectInputStream in) throws IOException, ClassNotFoundException {
        in.defaultReadObject();
        final Comparator comparator = (Comparator)in.readObject();
        final TreeMap map = new TreeMap(comparator);
        final int size = in.readInt();
        try {
            map.buildFromSorted(new IOIterator(in, size), size);
            this.map = map;
        }
        catch (TreeMap.IteratorIOException e) {
            throw e.getException();
        }
        catch (TreeMap.IteratorNoClassException e2) {
            throw e2.getException();
        }
    }
    
    static {
        PRESENT = new Object();
    }
    
    private static class MapIterator implements Iterator
    {
        final Iterator itr;
        
        MapIterator(final Iterator itr) {
            this.itr = itr;
        }
        
        public boolean hasNext() {
            return this.itr.hasNext();
        }
        
        public Object next() {
            return new AbstractMap.SimpleImmutableEntry(this.itr.next(), TreeSet.PRESENT);
        }
        
        public void remove() {
            throw new UnsupportedOperationException();
        }
    }
    
    static class IOIterator extends TreeMap.IOIterator
    {
        IOIterator(final ObjectInputStream in, final int remaining) {
            super(in, remaining);
        }
        
        public Object next() {
            if (this.remaining <= 0) {
                throw new NoSuchElementException();
            }
            --this.remaining;
            try {
                return new AbstractMap.SimpleImmutableEntry(this.ois.readObject(), TreeSet.PRESENT);
            }
            catch (IOException e) {
                throw new TreeMap.IteratorIOException(e);
            }
            catch (ClassNotFoundException e2) {
                throw new TreeMap.IteratorNoClassException(e2);
            }
        }
        
        public void remove() {
            throw new UnsupportedOperationException();
        }
    }
}
