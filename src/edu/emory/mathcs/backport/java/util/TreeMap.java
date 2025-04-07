// 
// Decompiled by Procyon v0.5.36
// 

package edu.emory.mathcs.backport.java.util;

import java.util.SortedSet;
import java.util.AbstractSet;
import java.util.ConcurrentModificationException;
import java.io.ObjectInputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.util.NoSuchElementException;
import java.util.Set;
import java.util.Map;
import java.util.Iterator;
import java.util.SortedMap;
import java.util.Comparator;
import java.io.Serializable;

public class TreeMap extends AbstractMap implements NavigableMap, Serializable
{
    private static final long serialVersionUID = 919286545866124006L;
    private final Comparator comparator;
    private transient Entry root;
    private transient int size;
    private transient int modCount;
    private transient EntrySet entrySet;
    private transient KeySet navigableKeySet;
    private transient NavigableMap descendingMap;
    private transient Comparator reverseComparator;
    
    public TreeMap() {
        this.size = 0;
        this.modCount = 0;
        this.comparator = null;
    }
    
    public TreeMap(final Comparator comparator) {
        this.size = 0;
        this.modCount = 0;
        this.comparator = comparator;
    }
    
    public TreeMap(final SortedMap map) {
        this.size = 0;
        this.modCount = 0;
        this.comparator = map.comparator();
        this.buildFromSorted(map.entrySet().iterator(), map.size());
    }
    
    public TreeMap(final Map map) {
        this.size = 0;
        this.modCount = 0;
        this.comparator = null;
        this.putAll(map);
    }
    
    public int size() {
        return this.size;
    }
    
    public void clear() {
        this.root = null;
        this.size = 0;
        ++this.modCount;
    }
    
    public Object clone() {
        TreeMap clone;
        try {
            clone = (TreeMap)super.clone();
        }
        catch (CloneNotSupportedException e) {
            throw new InternalError();
        }
        clone.root = null;
        clone.size = 0;
        clone.modCount = 0;
        if (!this.isEmpty()) {
            clone.buildFromSorted(this.entrySet().iterator(), this.size);
        }
        return clone;
    }
    
    public Object put(final Object key, final Object value) {
        if (this.root == null) {
            this.root = new Entry(key, value);
            ++this.size;
            ++this.modCount;
            return null;
        }
        Entry t = this.root;
        while (true) {
            final int diff = compare(key, t.getKey(), this.comparator);
            if (diff == 0) {
                return t.setValue(value);
            }
            if (diff <= 0) {
                if (t.left == null) {
                    ++this.size;
                    ++this.modCount;
                    final Entry e = new Entry(key, value);
                    e.parent = t;
                    t.left = e;
                    this.fixAfterInsertion(e);
                    return null;
                }
                t = t.left;
            }
            else {
                if (t.right == null) {
                    ++this.size;
                    ++this.modCount;
                    final Entry e = new Entry(key, value);
                    e.parent = t;
                    t.right = e;
                    this.fixAfterInsertion(e);
                    return null;
                }
                t = t.right;
            }
        }
    }
    
    public Object get(final Object key) {
        final Entry entry = this.getEntry(key);
        return (entry == null) ? null : entry.getValue();
    }
    
    public boolean containsKey(final Object key) {
        return this.getEntry(key) != null;
    }
    
    public Set entrySet() {
        if (this.entrySet == null) {
            this.entrySet = new EntrySet();
        }
        return this.entrySet;
    }
    
    private static Entry successor(Entry e) {
        if (e.right != null) {
            for (e = e.right; e.left != null; e = e.left) {}
            return e;
        }
        Entry p;
        for (p = e.parent; p != null && e == p.right; e = p, p = p.parent) {}
        return p;
    }
    
    private static Entry predecessor(Entry e) {
        if (e.left != null) {
            for (e = e.left; e.right != null; e = e.right) {}
            return e;
        }
        Entry p;
        for (p = e.parent; p != null && e == p.left; e = p, p = p.parent) {}
        return p;
    }
    
    private Entry getEntry(final Object key) {
        Entry t = this.root;
        if (this.comparator != null) {
            while (t != null) {
                final int diff = this.comparator.compare(key, t.key);
                if (diff == 0) {
                    return t;
                }
                t = ((diff < 0) ? t.left : t.right);
            }
            return null;
        }
        final Comparable c = (Comparable)key;
        while (t != null) {
            final int diff2 = c.compareTo(t.key);
            if (diff2 == 0) {
                return t;
            }
            t = ((diff2 < 0) ? t.left : t.right);
        }
        return null;
    }
    
    private Entry getHigherEntry(final Object key) {
        Entry t = this.root;
        if (t == null) {
            return null;
        }
        while (true) {
            final int diff = compare(key, t.key, this.comparator);
            if (diff < 0) {
                if (t.left == null) {
                    return t;
                }
                t = t.left;
            }
            else {
                if (t.right == null) {
                    Entry parent;
                    for (parent = t.parent; parent != null && t == parent.right; t = parent, parent = parent.parent) {}
                    return parent;
                }
                t = t.right;
            }
        }
    }
    
    private Entry getFirstEntry() {
        Entry e = this.root;
        if (e == null) {
            return null;
        }
        while (e.left != null) {
            e = e.left;
        }
        return e;
    }
    
    private Entry getLastEntry() {
        Entry e = this.root;
        if (e == null) {
            return null;
        }
        while (e.right != null) {
            e = e.right;
        }
        return e;
    }
    
    private Entry getCeilingEntry(final Object key) {
        Entry e = this.root;
        if (e == null) {
            return null;
        }
        while (true) {
            final int diff = compare(key, e.key, this.comparator);
            if (diff < 0) {
                if (e.left == null) {
                    return e;
                }
                e = e.left;
            }
            else {
                if (diff <= 0) {
                    return e;
                }
                if (e.right == null) {
                    Entry p;
                    for (p = e.parent; p != null && e == p.right; e = p, p = p.parent) {}
                    return p;
                }
                e = e.right;
            }
        }
    }
    
    private Entry getLowerEntry(final Object key) {
        Entry e = this.root;
        if (e == null) {
            return null;
        }
        while (true) {
            final int diff = compare(key, e.key, this.comparator);
            if (diff > 0) {
                if (e.right == null) {
                    return e;
                }
                e = e.right;
            }
            else {
                if (e.left == null) {
                    Entry p;
                    for (p = e.parent; p != null && e == p.left; e = p, p = p.parent) {}
                    return p;
                }
                e = e.left;
            }
        }
    }
    
    private Entry getFloorEntry(final Object key) {
        Entry e = this.root;
        if (e == null) {
            return null;
        }
        while (true) {
            final int diff = compare(key, e.key, this.comparator);
            if (diff > 0) {
                if (e.right == null) {
                    return e;
                }
                e = e.right;
            }
            else {
                if (diff >= 0) {
                    return e;
                }
                if (e.left == null) {
                    Entry p;
                    for (p = e.parent; p != null && e == p.left; e = p, p = p.parent) {}
                    return p;
                }
                e = e.left;
            }
        }
    }
    
    void buildFromSorted(final Iterator itr, final int size) {
        ++this.modCount;
        this.size = size;
        int bottom = 0;
        for (int ssize = 1; ssize - 1 < size; ssize <<= 1) {
            ++bottom;
        }
        this.root = createFromSorted(itr, size, 0, bottom);
    }
    
    private static Entry createFromSorted(final Iterator itr, final int size, int level, final int bottom) {
        ++level;
        if (size == 0) {
            return null;
        }
        final int leftSize = size - 1 >> 1;
        final int rightSize = size - 1 - leftSize;
        final Entry left = createFromSorted(itr, leftSize, level, bottom);
        final Map.Entry orig = itr.next();
        final Entry right = createFromSorted(itr, rightSize, level, bottom);
        final Entry e = new Entry(orig.getKey(), orig.getValue());
        if (left != null) {
            e.left = left;
            left.parent = e;
        }
        if (right != null) {
            e.right = right;
            right.parent = e;
        }
        if (level == bottom) {
            e.color = false;
        }
        return e;
    }
    
    private void delete(Entry e) {
        if (e.left == null && e.right == null && e.parent == null) {
            this.root = null;
            this.size = 0;
            ++this.modCount;
            return;
        }
        if (e.left != null && e.right != null) {
            final Entry s = successor(e);
            e.key = s.key;
            e.element = s.element;
            e = s;
        }
        if (e.left == null && e.right == null) {
            if (e.color) {
                this.fixAfterDeletion(e);
            }
            if (e.parent != null) {
                if (e == e.parent.left) {
                    e.parent.left = null;
                }
                else if (e == e.parent.right) {
                    e.parent.right = null;
                }
                e.parent = null;
            }
        }
        else {
            Entry replacement = e.left;
            if (replacement == null) {
                replacement = e.right;
            }
            replacement.parent = e.parent;
            if (e.parent == null) {
                this.root = replacement;
            }
            else if (e == e.parent.left) {
                e.parent.left = replacement;
            }
            else {
                e.parent.right = replacement;
            }
            e.left = null;
            e.right = null;
            e.parent = null;
            if (e.color) {
                this.fixAfterDeletion(replacement);
            }
        }
        --this.size;
        ++this.modCount;
    }
    
    static boolean colorOf(final Entry p) {
        return p == null || p.color;
    }
    
    static Entry parentOf(final Entry p) {
        return (p == null) ? null : p.parent;
    }
    
    private static void setColor(final Entry p, final boolean c) {
        if (p != null) {
            p.color = c;
        }
    }
    
    private static Entry leftOf(final Entry p) {
        return (p == null) ? null : p.left;
    }
    
    private static Entry rightOf(final Entry p) {
        return (p == null) ? null : p.right;
    }
    
    private final void rotateLeft(final Entry e) {
        final Entry r = e.right;
        e.right = r.left;
        if (r.left != null) {
            r.left.parent = e;
        }
        r.parent = e.parent;
        if (e.parent == null) {
            this.root = r;
        }
        else if (e.parent.left == e) {
            e.parent.left = r;
        }
        else {
            e.parent.right = r;
        }
        r.left = e;
        e.parent = r;
    }
    
    private final void rotateRight(final Entry e) {
        final Entry l = e.left;
        e.left = l.right;
        if (l.right != null) {
            l.right.parent = e;
        }
        l.parent = e.parent;
        if (e.parent == null) {
            this.root = l;
        }
        else if (e.parent.right == e) {
            e.parent.right = l;
        }
        else {
            e.parent.left = l;
        }
        l.right = e;
        e.parent = l;
    }
    
    private final void fixAfterInsertion(final Entry e) {
        e.color = false;
        Entry x = e;
        while (x != null && x != this.root && !x.parent.color) {
            if (parentOf(x) == leftOf(parentOf(parentOf(x)))) {
                final Entry y = rightOf(parentOf(parentOf(x)));
                if (!colorOf(y)) {
                    setColor(parentOf(x), true);
                    setColor(y, true);
                    setColor(parentOf(parentOf(x)), false);
                    x = parentOf(parentOf(x));
                }
                else {
                    if (x == rightOf(parentOf(x))) {
                        x = parentOf(x);
                        this.rotateLeft(x);
                    }
                    setColor(parentOf(x), true);
                    setColor(parentOf(parentOf(x)), false);
                    if (parentOf(parentOf(x)) == null) {
                        continue;
                    }
                    this.rotateRight(parentOf(parentOf(x)));
                }
            }
            else {
                final Entry y = leftOf(parentOf(parentOf(x)));
                if (!colorOf(y)) {
                    setColor(parentOf(x), true);
                    setColor(y, true);
                    setColor(parentOf(parentOf(x)), false);
                    x = parentOf(parentOf(x));
                }
                else {
                    if (x == leftOf(parentOf(x))) {
                        x = parentOf(x);
                        this.rotateRight(x);
                    }
                    setColor(parentOf(x), true);
                    setColor(parentOf(parentOf(x)), false);
                    if (parentOf(parentOf(x)) == null) {
                        continue;
                    }
                    this.rotateLeft(parentOf(parentOf(x)));
                }
            }
        }
        this.root.color = true;
    }
    
    private final Entry fixAfterDeletion(final Entry e) {
        Entry x = e;
        while (x != this.root && colorOf(x)) {
            if (x == leftOf(parentOf(x))) {
                Entry sib = rightOf(parentOf(x));
                if (!colorOf(sib)) {
                    setColor(sib, true);
                    setColor(parentOf(x), false);
                    this.rotateLeft(parentOf(x));
                    sib = rightOf(parentOf(x));
                }
                if (colorOf(leftOf(sib)) && colorOf(rightOf(sib))) {
                    setColor(sib, false);
                    x = parentOf(x);
                }
                else {
                    if (colorOf(rightOf(sib))) {
                        setColor(leftOf(sib), true);
                        setColor(sib, false);
                        this.rotateRight(sib);
                        sib = rightOf(parentOf(x));
                    }
                    setColor(sib, colorOf(parentOf(x)));
                    setColor(parentOf(x), true);
                    setColor(rightOf(sib), true);
                    this.rotateLeft(parentOf(x));
                    x = this.root;
                }
            }
            else {
                Entry sib = leftOf(parentOf(x));
                if (!colorOf(sib)) {
                    setColor(sib, true);
                    setColor(parentOf(x), false);
                    this.rotateRight(parentOf(x));
                    sib = leftOf(parentOf(x));
                }
                if (colorOf(rightOf(sib)) && colorOf(leftOf(sib))) {
                    setColor(sib, false);
                    x = parentOf(x);
                }
                else {
                    if (colorOf(leftOf(sib))) {
                        setColor(rightOf(sib), true);
                        setColor(sib, false);
                        this.rotateLeft(sib);
                        sib = leftOf(parentOf(x));
                    }
                    setColor(sib, colorOf(parentOf(x)));
                    setColor(parentOf(x), true);
                    setColor(leftOf(sib), true);
                    this.rotateRight(parentOf(x));
                    x = this.root;
                }
            }
        }
        setColor(x, true);
        return this.root;
    }
    
    private Entry getMatchingEntry(final Object o) {
        if (!(o instanceof Map.Entry)) {
            return null;
        }
        final Map.Entry e = (Map.Entry)o;
        final Entry found = this.getEntry(e.getKey());
        return (found != null && eq(found.getValue(), e.getValue())) ? found : null;
    }
    
    private static boolean eq(final Object o1, final Object o2) {
        return (o1 == null) ? (o2 == null) : o1.equals(o2);
    }
    
    private static int compare(final Object o1, final Object o2, final Comparator cmp) {
        return (cmp == null) ? ((Comparable)o1).compareTo(o2) : cmp.compare(o1, o2);
    }
    
    public Map.Entry lowerEntry(final Object key) {
        final Map.Entry e = this.getLowerEntry(key);
        return (e == null) ? null : new SimpleImmutableEntry(e);
    }
    
    public Object lowerKey(final Object key) {
        final Map.Entry e = this.getLowerEntry(key);
        return (e == null) ? null : e.getKey();
    }
    
    public Map.Entry floorEntry(final Object key) {
        final Entry e = this.getFloorEntry(key);
        return (e == null) ? null : new SimpleImmutableEntry(e);
    }
    
    public Object floorKey(final Object key) {
        final Entry e = this.getFloorEntry(key);
        return (e == null) ? null : e.key;
    }
    
    public Map.Entry ceilingEntry(final Object key) {
        final Entry e = this.getCeilingEntry(key);
        return (e == null) ? null : new SimpleImmutableEntry(e);
    }
    
    public Object ceilingKey(final Object key) {
        final Entry e = this.getCeilingEntry(key);
        return (e == null) ? null : e.key;
    }
    
    public Map.Entry higherEntry(final Object key) {
        final Entry e = this.getHigherEntry(key);
        return (e == null) ? null : new SimpleImmutableEntry(e);
    }
    
    public Object higherKey(final Object key) {
        final Entry e = this.getHigherEntry(key);
        return (e == null) ? null : e.key;
    }
    
    public Map.Entry firstEntry() {
        final Entry e = this.getFirstEntry();
        return (e == null) ? null : new SimpleImmutableEntry(e);
    }
    
    public Map.Entry lastEntry() {
        final Entry e = this.getLastEntry();
        return (e == null) ? null : new SimpleImmutableEntry(e);
    }
    
    public Map.Entry pollFirstEntry() {
        final Entry e = this.getFirstEntry();
        if (e == null) {
            return null;
        }
        final Map.Entry res = new SimpleImmutableEntry(e);
        this.delete(e);
        return res;
    }
    
    public Map.Entry pollLastEntry() {
        final Entry e = this.getLastEntry();
        if (e == null) {
            return null;
        }
        final Map.Entry res = new SimpleImmutableEntry(e);
        this.delete(e);
        return res;
    }
    
    public NavigableMap descendingMap() {
        NavigableMap map = this.descendingMap;
        if (map == null) {
            map = (this.descendingMap = new DescendingSubMap(true, null, true, true, null, true));
        }
        return map;
    }
    
    public NavigableSet descendingKeySet() {
        return this.descendingMap().navigableKeySet();
    }
    
    public SortedMap subMap(final Object fromKey, final Object toKey) {
        return this.subMap(fromKey, true, toKey, false);
    }
    
    public SortedMap headMap(final Object toKey) {
        return this.headMap(toKey, false);
    }
    
    public SortedMap tailMap(final Object fromKey) {
        return this.tailMap(fromKey, true);
    }
    
    public NavigableMap subMap(final Object fromKey, final boolean fromInclusive, final Object toKey, final boolean toInclusive) {
        return new AscendingSubMap(false, fromKey, fromInclusive, false, toKey, toInclusive);
    }
    
    public NavigableMap headMap(final Object toKey, final boolean toInclusive) {
        return new AscendingSubMap(true, null, true, false, toKey, toInclusive);
    }
    
    public NavigableMap tailMap(final Object fromKey, final boolean fromInclusive) {
        return new AscendingSubMap(false, fromKey, fromInclusive, true, null, true);
    }
    
    public Comparator comparator() {
        return this.comparator;
    }
    
    final Comparator reverseComparator() {
        if (this.reverseComparator == null) {
            this.reverseComparator = Collections.reverseOrder(this.comparator);
        }
        return this.reverseComparator;
    }
    
    public Object firstKey() {
        final Entry e = this.getFirstEntry();
        if (e == null) {
            throw new NoSuchElementException();
        }
        return e.key;
    }
    
    public Object lastKey() {
        final Entry e = this.getLastEntry();
        if (e == null) {
            throw new NoSuchElementException();
        }
        return e.key;
    }
    
    public boolean isEmpty() {
        return this.size == 0;
    }
    
    public boolean containsValue(final Object value) {
        return this.root != null && ((value == null) ? containsNull(this.root) : containsValue(this.root, value));
    }
    
    private static boolean containsNull(final Entry e) {
        return e.element == null || (e.left != null && containsNull(e.left)) || (e.right != null && containsNull(e.right));
    }
    
    private static boolean containsValue(final Entry e, final Object val) {
        return val.equals(e.element) || (e.left != null && containsValue(e.left, val)) || (e.right != null && containsValue(e.right, val));
    }
    
    public Object remove(final Object key) {
        final Entry e = this.getEntry(key);
        if (e == null) {
            return null;
        }
        final Object old = e.getValue();
        this.delete(e);
        return old;
    }
    
    public void putAll(final Map map) {
        if (map instanceof SortedMap) {
            final SortedMap smap = (SortedMap)map;
            if (eq(this.comparator, smap.comparator())) {
                this.buildFromSorted(smap.entrySet().iterator(), map.size());
                return;
            }
        }
        super.putAll(map);
    }
    
    public Set keySet() {
        return this.navigableKeySet();
    }
    
    public NavigableSet navigableKeySet() {
        if (this.navigableKeySet == null) {
            this.navigableKeySet = new AscendingKeySet();
        }
        return this.navigableKeySet;
    }
    
    private void writeObject(final ObjectOutputStream out) throws IOException {
        out.defaultWriteObject();
        out.writeInt(this.size);
        for (Entry e = this.getFirstEntry(); e != null; e = successor(e)) {
            out.writeObject(e.key);
            out.writeObject(e.element);
        }
    }
    
    private void readObject(final ObjectInputStream in) throws IOException, ClassNotFoundException {
        in.defaultReadObject();
        final int size = in.readInt();
        try {
            this.buildFromSorted(new IOIterator(in, size), size);
        }
        catch (IteratorIOException e) {
            throw e.getException();
        }
        catch (IteratorNoClassException e2) {
            throw e2.getException();
        }
    }
    
    public static class Entry implements Map.Entry, Cloneable, Serializable
    {
        private static final boolean RED = false;
        private static final boolean BLACK = true;
        private Object key;
        private Object element;
        private boolean color;
        private Entry left;
        private Entry right;
        private Entry parent;
        
        public Entry(final Object key, final Object element) {
            this.key = key;
            this.element = element;
            this.color = true;
        }
        
        protected Object clone() throws CloneNotSupportedException {
            final Entry t = new Entry(this.key, this.element);
            t.color = this.color;
            return t;
        }
        
        public final Object getKey() {
            return this.key;
        }
        
        public final Object getValue() {
            return this.element;
        }
        
        public final Object setValue(final Object v) {
            final Object old = this.element;
            this.element = v;
            return old;
        }
        
        public boolean equals(final Object o) {
            if (!(o instanceof Map.Entry)) {
                return false;
            }
            final Map.Entry e = (Map.Entry)o;
            return eq(this.key, e.getKey()) && eq(this.element, e.getValue());
        }
        
        public int hashCode() {
            return ((this.key == null) ? 0 : this.key.hashCode()) ^ ((this.element == null) ? 0 : this.element.hashCode());
        }
        
        public String toString() {
            return this.key + "=" + this.element;
        }
    }
    
    private class BaseEntryIterator
    {
        Entry cursor;
        Entry lastRet;
        int expectedModCount;
        
        BaseEntryIterator(final Entry cursor) {
            this.cursor = cursor;
            this.expectedModCount = TreeMap.this.modCount;
        }
        
        public boolean hasNext() {
            return this.cursor != null;
        }
        
        Entry nextEntry() {
            final Entry curr = this.cursor;
            if (curr == null) {
                throw new NoSuchElementException();
            }
            if (this.expectedModCount != TreeMap.this.modCount) {
                throw new ConcurrentModificationException();
            }
            this.cursor = successor(curr);
            return this.lastRet = curr;
        }
        
        Entry prevEntry() {
            final Entry curr = this.cursor;
            if (curr == null) {
                throw new NoSuchElementException();
            }
            if (this.expectedModCount != TreeMap.this.modCount) {
                throw new ConcurrentModificationException();
            }
            this.cursor = predecessor(curr);
            return this.lastRet = curr;
        }
        
        public void remove() {
            if (this.lastRet == null) {
                throw new IllegalStateException();
            }
            if (this.expectedModCount != TreeMap.this.modCount) {
                throw new ConcurrentModificationException();
            }
            if (this.lastRet.left != null && this.lastRet.right != null && this.cursor != null) {
                this.cursor = this.lastRet;
            }
            TreeMap.this.delete(this.lastRet);
            this.lastRet = null;
            ++this.expectedModCount;
        }
    }
    
    class EntryIterator extends BaseEntryIterator implements Iterator
    {
        EntryIterator(final Entry cursor) {
            super(cursor);
        }
        
        public Object next() {
            return this.nextEntry();
        }
    }
    
    class KeyIterator extends BaseEntryIterator implements Iterator
    {
        KeyIterator(final Entry cursor) {
            super(cursor);
        }
        
        public Object next() {
            return this.nextEntry().key;
        }
    }
    
    class ValueIterator extends BaseEntryIterator implements Iterator
    {
        ValueIterator(final Entry cursor) {
            super(cursor);
        }
        
        public Object next() {
            return this.nextEntry().element;
        }
    }
    
    class DescendingEntryIterator extends BaseEntryIterator implements Iterator
    {
        DescendingEntryIterator(final Entry cursor) {
            super(cursor);
        }
        
        public Object next() {
            return this.prevEntry();
        }
    }
    
    class DescendingKeyIterator extends BaseEntryIterator implements Iterator
    {
        DescendingKeyIterator(final Entry cursor) {
            super(cursor);
        }
        
        public Object next() {
            return this.prevEntry().key;
        }
    }
    
    class DescendingValueIterator extends BaseEntryIterator implements Iterator
    {
        DescendingValueIterator(final Entry cursor) {
            super(cursor);
        }
        
        public Object next() {
            return this.prevEntry().element;
        }
    }
    
    class EntrySet extends AbstractSet
    {
        public int size() {
            return TreeMap.this.size();
        }
        
        public boolean isEmpty() {
            return TreeMap.this.isEmpty();
        }
        
        public void clear() {
            TreeMap.this.clear();
        }
        
        public Iterator iterator() {
            return new EntryIterator(TreeMap.this.getFirstEntry());
        }
        
        public boolean contains(final Object o) {
            return TreeMap.this.getMatchingEntry(o) != null;
        }
        
        public boolean remove(final Object o) {
            final Entry e = TreeMap.this.getMatchingEntry(o);
            if (e == null) {
                return false;
            }
            TreeMap.this.delete(e);
            return true;
        }
    }
    
    class DescendingEntrySet extends EntrySet
    {
        public Iterator iterator() {
            return new DescendingEntryIterator(TreeMap.this.getLastEntry());
        }
    }
    
    class ValueSet extends AbstractSet
    {
        public int size() {
            return TreeMap.this.size();
        }
        
        public boolean isEmpty() {
            return TreeMap.this.isEmpty();
        }
        
        public void clear() {
            TreeMap.this.clear();
        }
        
        public boolean contains(final Object o) {
            for (Entry e = TreeMap.this.getFirstEntry(); e != null; e = successor(e)) {
                if (eq(o, e.element)) {
                    return true;
                }
            }
            return false;
        }
        
        public Iterator iterator() {
            return new ValueIterator(TreeMap.this.getFirstEntry());
        }
        
        public boolean remove(final Object o) {
            for (Entry e = TreeMap.this.getFirstEntry(); e != null; e = successor(e)) {
                if (eq(o, e.element)) {
                    TreeMap.this.delete(e);
                    return true;
                }
            }
            return false;
        }
    }
    
    abstract class KeySet extends AbstractSet implements NavigableSet
    {
        public int size() {
            return TreeMap.this.size();
        }
        
        public boolean isEmpty() {
            return TreeMap.this.isEmpty();
        }
        
        public void clear() {
            TreeMap.this.clear();
        }
        
        public boolean contains(final Object o) {
            return TreeMap.this.getEntry(o) != null;
        }
        
        public boolean remove(final Object o) {
            final Entry found = TreeMap.this.getEntry(o);
            if (found == null) {
                return false;
            }
            TreeMap.this.delete(found);
            return true;
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
    }
    
    class AscendingKeySet extends KeySet
    {
        public Iterator iterator() {
            return new KeyIterator(TreeMap.this.getFirstEntry());
        }
        
        public Iterator descendingIterator() {
            return new DescendingKeyIterator(TreeMap.this.getFirstEntry());
        }
        
        public Object lower(final Object e) {
            return TreeMap.this.lowerKey(e);
        }
        
        public Object floor(final Object e) {
            return TreeMap.this.floorKey(e);
        }
        
        public Object ceiling(final Object e) {
            return TreeMap.this.ceilingKey(e);
        }
        
        public Object higher(final Object e) {
            return TreeMap.this.higherKey(e);
        }
        
        public Object first() {
            return TreeMap.this.firstKey();
        }
        
        public Object last() {
            return TreeMap.this.lastKey();
        }
        
        public Comparator comparator() {
            return TreeMap.this.comparator();
        }
        
        public Object pollFirst() {
            final Map.Entry e = TreeMap.this.pollFirstEntry();
            return (e == null) ? null : e.getKey();
        }
        
        public Object pollLast() {
            final Map.Entry e = TreeMap.this.pollLastEntry();
            return (e == null) ? null : e.getKey();
        }
        
        public NavigableSet subSet(final Object fromElement, final boolean fromInclusive, final Object toElement, final boolean toInclusive) {
            return (NavigableSet)TreeMap.this.subMap(fromElement, fromInclusive, toElement, toInclusive).keySet();
        }
        
        public NavigableSet headSet(final Object toElement, final boolean inclusive) {
            return (NavigableSet)TreeMap.this.headMap(toElement, inclusive).keySet();
        }
        
        public NavigableSet tailSet(final Object fromElement, final boolean inclusive) {
            return (NavigableSet)TreeMap.this.tailMap(fromElement, inclusive).keySet();
        }
        
        public NavigableSet descendingSet() {
            return (NavigableSet)TreeMap.this.descendingMap().keySet();
        }
    }
    
    class DescendingKeySet extends KeySet
    {
        public Iterator iterator() {
            return new DescendingKeyIterator(TreeMap.this.getLastEntry());
        }
        
        public Iterator descendingIterator() {
            return new KeyIterator(TreeMap.this.getFirstEntry());
        }
        
        public Object lower(final Object e) {
            return TreeMap.this.higherKey(e);
        }
        
        public Object floor(final Object e) {
            return TreeMap.this.ceilingKey(e);
        }
        
        public Object ceiling(final Object e) {
            return TreeMap.this.floorKey(e);
        }
        
        public Object higher(final Object e) {
            return TreeMap.this.lowerKey(e);
        }
        
        public Object first() {
            return TreeMap.this.lastKey();
        }
        
        public Object last() {
            return TreeMap.this.firstKey();
        }
        
        public Comparator comparator() {
            return TreeMap.this.descendingMap().comparator();
        }
        
        public Object pollFirst() {
            final Map.Entry e = TreeMap.this.pollLastEntry();
            return (e == null) ? null : e.getKey();
        }
        
        public Object pollLast() {
            final Map.Entry e = TreeMap.this.pollFirstEntry();
            return (e == null) ? null : e.getKey();
        }
        
        public NavigableSet subSet(final Object fromElement, final boolean fromInclusive, final Object toElement, final boolean toInclusive) {
            return (NavigableSet)TreeMap.this.descendingMap().subMap(fromElement, fromInclusive, toElement, toInclusive).keySet();
        }
        
        public NavigableSet headSet(final Object toElement, final boolean inclusive) {
            return (NavigableSet)TreeMap.this.descendingMap().headMap(toElement, inclusive).keySet();
        }
        
        public NavigableSet tailSet(final Object fromElement, final boolean inclusive) {
            return (NavigableSet)TreeMap.this.descendingMap().tailMap(fromElement, inclusive).keySet();
        }
        
        public NavigableSet descendingSet() {
            return (NavigableSet)TreeMap.this.keySet();
        }
    }
    
    private abstract class NavigableSubMap extends AbstractMap implements NavigableMap, Serializable
    {
        private static final long serialVersionUID = -6520786458950516097L;
        final Object fromKey;
        final Object toKey;
        final boolean fromStart;
        final boolean toEnd;
        final boolean fromInclusive;
        final boolean toInclusive;
        transient int cachedSize;
        transient int cacheVersion;
        transient SubEntrySet entrySet;
        transient NavigableMap descendingMap;
        transient NavigableSet navigableKeySet;
        private final /* synthetic */ TreeMap this$0;
        
        NavigableSubMap(final boolean fromStart, final Object fromKey, final boolean fromInclusive, final boolean toEnd, final Object toKey, final boolean toInclusive) {
            this.cachedSize = -1;
            if (!fromStart && !toEnd) {
                if (compare(fromKey, toKey, TreeMap.this.comparator) > 0) {
                    throw new IllegalArgumentException("fromKey > toKey");
                }
            }
            else {
                if (!fromStart) {
                    compare(fromKey, fromKey, TreeMap.this.comparator);
                }
                if (!toEnd) {
                    compare(toKey, toKey, TreeMap.this.comparator);
                }
            }
            this.fromStart = fromStart;
            this.toEnd = toEnd;
            this.fromKey = fromKey;
            this.toKey = toKey;
            this.fromInclusive = fromInclusive;
            this.toInclusive = toInclusive;
        }
        
        final TreeMap.Entry checkLoRange(final TreeMap.Entry e) {
            return (e == null || this.absTooLow(e.key)) ? null : e;
        }
        
        final TreeMap.Entry checkHiRange(final TreeMap.Entry e) {
            return (e == null || this.absTooHigh(e.key)) ? null : e;
        }
        
        final boolean inRange(final Object key) {
            return !this.absTooLow(key) && !this.absTooHigh(key);
        }
        
        final boolean inRangeExclusive(final Object key) {
            return (this.fromStart || compare(key, this.fromKey, TreeMap.this.comparator) >= 0) && (this.toEnd || compare(this.toKey, key, TreeMap.this.comparator) >= 0);
        }
        
        final boolean inRange(final Object key, final boolean inclusive) {
            return inclusive ? this.inRange(key) : this.inRangeExclusive(key);
        }
        
        private boolean absTooHigh(final Object key) {
            if (this.toEnd) {
                return false;
            }
            final int c = compare(key, this.toKey, TreeMap.this.comparator);
            return c > 0 || (c == 0 && !this.toInclusive);
        }
        
        private boolean absTooLow(final Object key) {
            if (this.fromStart) {
                return false;
            }
            final int c = compare(key, this.fromKey, TreeMap.this.comparator);
            return c < 0 || (c == 0 && !this.fromInclusive);
        }
        
        protected abstract TreeMap.Entry first();
        
        protected abstract TreeMap.Entry last();
        
        protected abstract TreeMap.Entry lower(final Object p0);
        
        protected abstract TreeMap.Entry floor(final Object p0);
        
        protected abstract TreeMap.Entry ceiling(final Object p0);
        
        protected abstract TreeMap.Entry higher(final Object p0);
        
        protected abstract TreeMap.Entry uncheckedHigher(final TreeMap.Entry p0);
        
        final TreeMap.Entry absLowest() {
            return this.checkHiRange(this.fromStart ? TreeMap.this.getFirstEntry() : (this.fromInclusive ? TreeMap.this.getCeilingEntry(this.fromKey) : TreeMap.this.getHigherEntry(this.fromKey)));
        }
        
        final TreeMap.Entry absHighest() {
            return this.checkLoRange(this.toEnd ? TreeMap.this.getLastEntry() : (this.toInclusive ? TreeMap.this.getFloorEntry(this.toKey) : TreeMap.this.getLowerEntry(this.toKey)));
        }
        
        final TreeMap.Entry absLower(final Object key) {
            return this.absTooHigh(key) ? this.absHighest() : this.checkLoRange(TreeMap.this.getLowerEntry(key));
        }
        
        final TreeMap.Entry absFloor(final Object key) {
            return this.absTooHigh(key) ? this.absHighest() : this.checkLoRange(TreeMap.this.getFloorEntry(key));
        }
        
        final TreeMap.Entry absCeiling(final Object key) {
            return this.absTooLow(key) ? this.absLowest() : this.checkHiRange(TreeMap.this.getCeilingEntry(key));
        }
        
        final TreeMap.Entry absHigher(final Object key) {
            return this.absTooLow(key) ? this.absLowest() : this.checkHiRange(TreeMap.this.getHigherEntry(key));
        }
        
        public Map.Entry firstEntry() {
            final TreeMap.Entry e = this.first();
            return (e == null) ? null : new SimpleImmutableEntry(e);
        }
        
        public Object firstKey() {
            final TreeMap.Entry e = this.first();
            if (e == null) {
                throw new NoSuchElementException();
            }
            return e.key;
        }
        
        public Map.Entry lastEntry() {
            final TreeMap.Entry e = this.last();
            return (e == null) ? null : new SimpleImmutableEntry(e);
        }
        
        public Object lastKey() {
            final TreeMap.Entry e = this.last();
            if (e == null) {
                throw new NoSuchElementException();
            }
            return e.key;
        }
        
        public Map.Entry pollFirstEntry() {
            final TreeMap.Entry e = this.first();
            if (e == null) {
                return null;
            }
            final Map.Entry result = new SimpleImmutableEntry(e);
            TreeMap.this.delete(e);
            return result;
        }
        
        public Map.Entry pollLastEntry() {
            final TreeMap.Entry e = this.last();
            if (e == null) {
                return null;
            }
            final Map.Entry result = new SimpleImmutableEntry(e);
            TreeMap.this.delete(e);
            return result;
        }
        
        public Map.Entry lowerEntry(final Object key) {
            final TreeMap.Entry e = this.lower(key);
            return (e == null) ? null : new SimpleImmutableEntry(e);
        }
        
        public Object lowerKey(final Object key) {
            final TreeMap.Entry e = this.lower(key);
            return (e == null) ? null : e.key;
        }
        
        public Map.Entry floorEntry(final Object key) {
            final TreeMap.Entry e = this.floor(key);
            return (e == null) ? null : new SimpleImmutableEntry(e);
        }
        
        public Object floorKey(final Object key) {
            final TreeMap.Entry e = this.floor(key);
            return (e == null) ? null : e.key;
        }
        
        public Map.Entry ceilingEntry(final Object key) {
            final TreeMap.Entry e = this.ceiling(key);
            return (e == null) ? null : new SimpleImmutableEntry(e);
        }
        
        public Object ceilingKey(final Object key) {
            final TreeMap.Entry e = this.ceiling(key);
            return (e == null) ? null : e.key;
        }
        
        public Map.Entry higherEntry(final Object key) {
            final TreeMap.Entry e = this.higher(key);
            return (e == null) ? null : new SimpleImmutableEntry(e);
        }
        
        public Object higherKey(final Object key) {
            final TreeMap.Entry e = this.higher(key);
            return (e == null) ? null : e.key;
        }
        
        public NavigableSet descendingKeySet() {
            return this.descendingMap().navigableKeySet();
        }
        
        public SortedMap subMap(final Object fromKey, final Object toKey) {
            return this.subMap(fromKey, true, toKey, false);
        }
        
        public SortedMap headMap(final Object toKey) {
            return this.headMap(toKey, false);
        }
        
        public SortedMap tailMap(final Object fromKey) {
            return this.tailMap(fromKey, true);
        }
        
        public int size() {
            if (this.cachedSize < 0 || this.cacheVersion != TreeMap.this.modCount) {
                this.cachedSize = this.recalculateSize();
                this.cacheVersion = TreeMap.this.modCount;
            }
            return this.cachedSize;
        }
        
        private int recalculateSize() {
            final TreeMap.Entry terminator = this.absHighest();
            final Object terminalKey = (terminator != null) ? terminator.key : null;
            int size = 0;
            for (TreeMap.Entry e = this.absLowest(); e != null; e = ((e.key == terminalKey) ? null : successor(e))) {
                ++size;
            }
            return size;
        }
        
        public boolean isEmpty() {
            return this.absLowest() == null;
        }
        
        public boolean containsKey(final Object key) {
            return this.inRange(key) && TreeMap.this.containsKey(key);
        }
        
        public Object get(final Object key) {
            if (!this.inRange(key)) {
                return null;
            }
            return TreeMap.this.get(key);
        }
        
        public Object put(final Object key, final Object value) {
            if (!this.inRange(key)) {
                throw new IllegalArgumentException("Key out of range");
            }
            return TreeMap.this.put(key, value);
        }
        
        public Object remove(final Object key) {
            if (!this.inRange(key)) {
                return null;
            }
            return TreeMap.this.remove(key);
        }
        
        public Set entrySet() {
            if (this.entrySet == null) {
                this.entrySet = new SubEntrySet();
            }
            return this.entrySet;
        }
        
        public Set keySet() {
            return this.navigableKeySet();
        }
        
        public NavigableSet navigableKeySet() {
            if (this.navigableKeySet == null) {
                this.navigableKeySet = new SubKeySet();
            }
            return this.navigableKeySet;
        }
        
        private TreeMap.Entry getMatchingSubEntry(final Object o) {
            if (!(o instanceof Map.Entry)) {
                return null;
            }
            final Map.Entry e = (Map.Entry)o;
            final Object key = e.getKey();
            if (!this.inRange(key)) {
                return null;
            }
            final TreeMap.Entry found = TreeMap.this.getEntry(key);
            return (found != null && eq(found.getValue(), e.getValue())) ? found : null;
        }
        
        class SubEntrySet extends AbstractSet
        {
            public int size() {
                return NavigableSubMap.this.size();
            }
            
            public boolean isEmpty() {
                return NavigableSubMap.this.isEmpty();
            }
            
            public boolean contains(final Object o) {
                return NavigableSubMap.this.getMatchingSubEntry(o) != null;
            }
            
            public boolean remove(final Object o) {
                final TreeMap.Entry e = NavigableSubMap.this.getMatchingSubEntry(o);
                if (e == null) {
                    return false;
                }
                TreeMap.this.delete(e);
                return true;
            }
            
            public Iterator iterator() {
                return new SubEntryIterator();
            }
        }
        
        class SubKeySet extends AbstractSet implements NavigableSet
        {
            public int size() {
                return NavigableSubMap.this.size();
            }
            
            public boolean isEmpty() {
                return NavigableSubMap.this.isEmpty();
            }
            
            public void clear() {
                NavigableSubMap.this.clear();
            }
            
            public boolean contains(final Object o) {
                return TreeMap.this.getEntry(o) != null;
            }
            
            public boolean remove(final Object o) {
                if (!NavigableSubMap.this.inRange(o)) {
                    return false;
                }
                final TreeMap.Entry found = TreeMap.this.getEntry(o);
                if (found == null) {
                    return false;
                }
                TreeMap.this.delete(found);
                return true;
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
            
            public Iterator iterator() {
                return new SubKeyIterator(NavigableSubMap.this.entrySet().iterator());
            }
            
            public Iterator descendingIterator() {
                return new SubKeyIterator(NavigableSubMap.this.descendingMap().entrySet().iterator());
            }
            
            public Object lower(final Object e) {
                return NavigableSubMap.this.lowerKey(e);
            }
            
            public Object floor(final Object e) {
                return NavigableSubMap.this.floorKey(e);
            }
            
            public Object ceiling(final Object e) {
                return NavigableSubMap.this.ceilingKey(e);
            }
            
            public Object higher(final Object e) {
                return NavigableSubMap.this.higherKey(e);
            }
            
            public Object first() {
                return NavigableSubMap.this.firstKey();
            }
            
            public Object last() {
                return NavigableSubMap.this.lastKey();
            }
            
            public Comparator comparator() {
                return NavigableSubMap.this.comparator();
            }
            
            public Object pollFirst() {
                final Map.Entry e = NavigableSubMap.this.pollFirstEntry();
                return (e == null) ? null : e.getKey();
            }
            
            public Object pollLast() {
                final Map.Entry e = NavigableSubMap.this.pollLastEntry();
                return (e == null) ? null : e.getKey();
            }
            
            public NavigableSet subSet(final Object fromElement, final boolean fromInclusive, final Object toElement, final boolean toInclusive) {
                return (NavigableSet)NavigableSubMap.this.subMap(fromElement, fromInclusive, toElement, toInclusive).keySet();
            }
            
            public NavigableSet headSet(final Object toElement, final boolean inclusive) {
                return (NavigableSet)NavigableSubMap.this.headMap(toElement, inclusive).keySet();
            }
            
            public NavigableSet tailSet(final Object fromElement, final boolean inclusive) {
                return (NavigableSet)NavigableSubMap.this.tailMap(fromElement, inclusive).keySet();
            }
            
            public NavigableSet descendingSet() {
                return (NavigableSet)NavigableSubMap.this.descendingMap().keySet();
            }
        }
        
        class SubEntryIterator extends BaseEntryIterator implements Iterator
        {
            final Object terminalKey;
            
            SubEntryIterator() {
                NavigableSubMap.this.this$0.super(NavigableSubMap.this.first());
                final TreeMap.Entry terminator = NavigableSubMap.this.last();
                this.terminalKey = ((terminator == null) ? null : terminator.key);
            }
            
            public boolean hasNext() {
                return this.cursor != null;
            }
            
            public Object next() {
                final TreeMap.Entry curr = this.cursor;
                if (curr == null) {
                    throw new NoSuchElementException();
                }
                if (this.expectedModCount != TreeMap.this.modCount) {
                    throw new ConcurrentModificationException();
                }
                this.cursor = ((curr.key == this.terminalKey) ? null : NavigableSubMap.this.uncheckedHigher(curr));
                return this.lastRet = curr;
            }
        }
        
        class SubKeyIterator implements Iterator
        {
            final Iterator itr;
            
            SubKeyIterator(final Iterator itr) {
                this.itr = itr;
            }
            
            public boolean hasNext() {
                return this.itr.hasNext();
            }
            
            public Object next() {
                return this.itr.next().getKey();
            }
            
            public void remove() {
                this.itr.remove();
            }
        }
    }
    
    class AscendingSubMap extends NavigableSubMap
    {
        AscendingSubMap(final boolean fromStart, final Object fromKey, final boolean fromInclusive, final boolean toEnd, final Object toKey, final boolean toInclusive) {
            super(fromStart, fromKey, fromInclusive, toEnd, toKey, toInclusive);
        }
        
        public Comparator comparator() {
            return TreeMap.this.comparator;
        }
        
        protected TreeMap.Entry first() {
            return this.absLowest();
        }
        
        protected TreeMap.Entry last() {
            return this.absHighest();
        }
        
        protected TreeMap.Entry lower(final Object key) {
            return this.absLower(key);
        }
        
        protected TreeMap.Entry floor(final Object key) {
            return this.absFloor(key);
        }
        
        protected TreeMap.Entry ceiling(final Object key) {
            return this.absCeiling(key);
        }
        
        protected TreeMap.Entry higher(final Object key) {
            return this.absHigher(key);
        }
        
        protected TreeMap.Entry uncheckedHigher(final TreeMap.Entry e) {
            return successor(e);
        }
        
        public NavigableMap subMap(final Object fromKey, final boolean fromInclusive, final Object toKey, final boolean toInclusive) {
            if (!this.inRange(fromKey, fromInclusive)) {
                throw new IllegalArgumentException("fromKey out of range");
            }
            if (!this.inRange(toKey, toInclusive)) {
                throw new IllegalArgumentException("toKey out of range");
            }
            return new AscendingSubMap(false, fromKey, fromInclusive, false, toKey, toInclusive);
        }
        
        public NavigableMap headMap(final Object toKey, final boolean toInclusive) {
            if (!this.inRange(toKey, toInclusive)) {
                throw new IllegalArgumentException("toKey out of range");
            }
            return new AscendingSubMap(this.fromStart, this.fromKey, this.fromInclusive, false, toKey, toInclusive);
        }
        
        public NavigableMap tailMap(final Object fromKey, final boolean fromInclusive) {
            if (!this.inRange(fromKey, fromInclusive)) {
                throw new IllegalArgumentException("fromKey out of range");
            }
            return new AscendingSubMap(false, fromKey, fromInclusive, this.toEnd, this.toKey, this.toInclusive);
        }
        
        public NavigableMap descendingMap() {
            if (this.descendingMap == null) {
                this.descendingMap = new DescendingSubMap(this.fromStart, this.fromKey, this.fromInclusive, this.toEnd, this.toKey, this.toInclusive);
            }
            return this.descendingMap;
        }
    }
    
    class DescendingSubMap extends NavigableSubMap
    {
        DescendingSubMap(final boolean fromStart, final Object fromKey, final boolean fromInclusive, final boolean toEnd, final Object toKey, final boolean toInclusive) {
            super(fromStart, fromKey, fromInclusive, toEnd, toKey, toInclusive);
        }
        
        public Comparator comparator() {
            return TreeMap.this.reverseComparator();
        }
        
        protected TreeMap.Entry first() {
            return this.absHighest();
        }
        
        protected TreeMap.Entry last() {
            return this.absLowest();
        }
        
        protected TreeMap.Entry lower(final Object key) {
            return this.absHigher(key);
        }
        
        protected TreeMap.Entry floor(final Object key) {
            return this.absCeiling(key);
        }
        
        protected TreeMap.Entry ceiling(final Object key) {
            return this.absFloor(key);
        }
        
        protected TreeMap.Entry higher(final Object key) {
            return this.absLower(key);
        }
        
        protected TreeMap.Entry uncheckedHigher(final TreeMap.Entry e) {
            return predecessor(e);
        }
        
        public NavigableMap subMap(final Object fromKey, final boolean fromInclusive, final Object toKey, final boolean toInclusive) {
            if (!this.inRange(fromKey, fromInclusive)) {
                throw new IllegalArgumentException("fromKey out of range");
            }
            if (!this.inRange(toKey, toInclusive)) {
                throw new IllegalArgumentException("toKey out of range");
            }
            return new DescendingSubMap(false, toKey, toInclusive, false, fromKey, fromInclusive);
        }
        
        public NavigableMap headMap(final Object toKey, final boolean toInclusive) {
            if (!this.inRange(toKey, toInclusive)) {
                throw new IllegalArgumentException("toKey out of range");
            }
            return new DescendingSubMap(false, toKey, toInclusive, this.toEnd, this.toKey, this.toInclusive);
        }
        
        public NavigableMap tailMap(final Object fromKey, final boolean fromInclusive) {
            if (!this.inRange(fromKey, fromInclusive)) {
                throw new IllegalArgumentException("fromKey out of range");
            }
            return new DescendingSubMap(this.fromStart, this.fromKey, this.fromInclusive, false, fromKey, fromInclusive);
        }
        
        public NavigableMap descendingMap() {
            if (this.descendingMap == null) {
                this.descendingMap = new AscendingSubMap(this.fromStart, this.fromKey, this.fromInclusive, this.toEnd, this.toKey, this.toInclusive);
            }
            return this.descendingMap;
        }
    }
    
    static class IteratorIOException extends RuntimeException
    {
        IteratorIOException(final IOException e) {
            super(e);
        }
        
        IOException getException() {
            return (IOException)this.getCause();
        }
    }
    
    static class IteratorNoClassException extends RuntimeException
    {
        IteratorNoClassException(final ClassNotFoundException e) {
            super(e);
        }
        
        ClassNotFoundException getException() {
            return (ClassNotFoundException)this.getCause();
        }
    }
    
    static class IOIterator implements Iterator
    {
        final ObjectInputStream ois;
        int remaining;
        
        IOIterator(final ObjectInputStream ois, final int remaining) {
            this.ois = ois;
            this.remaining = remaining;
        }
        
        public boolean hasNext() {
            return this.remaining > 0;
        }
        
        public Object next() {
            if (this.remaining <= 0) {
                throw new NoSuchElementException();
            }
            --this.remaining;
            try {
                return new SimpleImmutableEntry(this.ois.readObject(), this.ois.readObject());
            }
            catch (IOException e) {
                throw new IteratorIOException(e);
            }
            catch (ClassNotFoundException e2) {
                throw new IteratorNoClassException(e2);
            }
        }
        
        public void remove() {
            throw new UnsupportedOperationException();
        }
    }
    
    private class SubMap extends AbstractMap implements Serializable, NavigableMap
    {
        private static final long serialVersionUID = -6520786458950516097L;
        final Object fromKey;
        final Object toKey;
        
        SubMap() {
            final Object o = null;
            this.toKey = o;
            this.fromKey = o;
        }
        
        private Object readResolve() {
            return new AscendingSubMap(this.fromKey == null, this.fromKey, true, this.toKey == null, this.toKey, false);
        }
        
        public Map.Entry lowerEntry(final Object key) {
            throw new Error();
        }
        
        public Object lowerKey(final Object key) {
            throw new Error();
        }
        
        public Map.Entry floorEntry(final Object key) {
            throw new Error();
        }
        
        public Object floorKey(final Object key) {
            throw new Error();
        }
        
        public Map.Entry ceilingEntry(final Object key) {
            throw new Error();
        }
        
        public Object ceilingKey(final Object key) {
            throw new Error();
        }
        
        public Map.Entry higherEntry(final Object key) {
            throw new Error();
        }
        
        public Object higherKey(final Object key) {
            throw new Error();
        }
        
        public Map.Entry firstEntry() {
            throw new Error();
        }
        
        public Map.Entry lastEntry() {
            throw new Error();
        }
        
        public Map.Entry pollFirstEntry() {
            throw new Error();
        }
        
        public Map.Entry pollLastEntry() {
            throw new Error();
        }
        
        public NavigableMap descendingMap() {
            throw new Error();
        }
        
        public NavigableSet navigableKeySet() {
            throw new Error();
        }
        
        public NavigableSet descendingKeySet() {
            throw new Error();
        }
        
        public Set entrySet() {
            throw new Error();
        }
        
        public NavigableMap subMap(final Object fromKey, final boolean fromInclusive, final Object toKey, final boolean toInclusive) {
            throw new Error();
        }
        
        public NavigableMap headMap(final Object toKey, final boolean inclusive) {
            throw new Error();
        }
        
        public NavigableMap tailMap(final Object fromKey, final boolean inclusive) {
            throw new Error();
        }
        
        public SortedMap subMap(final Object fromKey, final Object toKey) {
            throw new Error();
        }
        
        public SortedMap headMap(final Object toKey) {
            throw new Error();
        }
        
        public SortedMap tailMap(final Object fromKey) {
            throw new Error();
        }
        
        public Comparator comparator() {
            throw new Error();
        }
        
        public Object firstKey() {
            throw new Error();
        }
        
        public Object lastKey() {
            throw new Error();
        }
    }
}
