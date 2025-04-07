// 
// Decompiled by Procyon v0.5.36
// 

package edu.emory.mathcs.backport.java.util.concurrent;

import edu.emory.mathcs.backport.java.util.AbstractCollection;
import edu.emory.mathcs.backport.java.util.AbstractSet;
import java.util.NoSuchElementException;
import edu.emory.mathcs.backport.java.util.concurrent.locks.ReentrantLock;
import java.io.ObjectInputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.util.Enumeration;
import java.util.Iterator;
import java.util.Map;
import java.util.Collection;
import java.util.Set;
import java.io.Serializable;
import edu.emory.mathcs.backport.java.util.AbstractMap;

public class ConcurrentHashMap extends AbstractMap implements ConcurrentMap, Serializable
{
    private static final long serialVersionUID = 7249069246763182397L;
    static final int DEFAULT_INITIAL_CAPACITY = 16;
    static final float DEFAULT_LOAD_FACTOR = 0.75f;
    static final int DEFAULT_CONCURRENCY_LEVEL = 16;
    static final int MAXIMUM_CAPACITY = 1073741824;
    static final int MAX_SEGMENTS = 65536;
    static final int RETRIES_BEFORE_LOCK = 2;
    final int segmentMask;
    final int segmentShift;
    final Segment[] segments;
    transient Set keySet;
    transient Set entrySet;
    transient Collection values;
    
    private static int hash(int h) {
        h += (h << 15 ^ 0xFFFFCD7D);
        h ^= h >>> 10;
        h += h << 3;
        h ^= h >>> 6;
        h += (h << 2) + (h << 14);
        return h ^ h >>> 16;
    }
    
    final Segment segmentFor(final int hash) {
        return this.segments[hash >>> this.segmentShift & this.segmentMask];
    }
    
    public ConcurrentHashMap(int initialCapacity, final float loadFactor, int concurrencyLevel) {
        if (loadFactor <= 0.0f || initialCapacity < 0 || concurrencyLevel <= 0) {
            throw new IllegalArgumentException();
        }
        if (concurrencyLevel > 65536) {
            concurrencyLevel = 65536;
        }
        int sshift = 0;
        int ssize;
        for (ssize = 1; ssize < concurrencyLevel; ssize <<= 1) {
            ++sshift;
        }
        this.segmentShift = 32 - sshift;
        this.segmentMask = ssize - 1;
        this.segments = Segment.newArray(ssize);
        if (initialCapacity > 1073741824) {
            initialCapacity = 1073741824;
        }
        int c = initialCapacity / ssize;
        if (c * ssize < initialCapacity) {
            ++c;
        }
        int cap;
        for (cap = 1; cap < c; cap <<= 1) {}
        for (int i = 0; i < this.segments.length; ++i) {
            this.segments[i] = new Segment(cap, loadFactor);
        }
    }
    
    public ConcurrentHashMap(final int initialCapacity, final float loadFactor) {
        this(initialCapacity, loadFactor, 16);
    }
    
    public ConcurrentHashMap(final int initialCapacity) {
        this(initialCapacity, 0.75f, 16);
    }
    
    public ConcurrentHashMap() {
        this(16, 0.75f, 16);
    }
    
    public ConcurrentHashMap(final Map m) {
        this(Math.max((int)(m.size() / 0.75f) + 1, 16), 0.75f, 16);
        this.putAll(m);
    }
    
    public boolean isEmpty() {
        final Segment[] segments = this.segments;
        final int[] mc = new int[segments.length];
        int mcsum = 0;
        for (int i = 0; i < segments.length; ++i) {
            if (segments[i].count != 0) {
                return false;
            }
            final int n = mcsum;
            final int[] array = mc;
            final int n2 = i;
            final int modCount = segments[i].modCount;
            array[n2] = modCount;
            mcsum = n + modCount;
        }
        if (mcsum != 0) {
            for (int i = 0; i < segments.length; ++i) {
                if (segments[i].count != 0 || mc[i] != segments[i].modCount) {
                    return false;
                }
            }
        }
        return true;
    }
    
    public int size() {
        final Segment[] segments = this.segments;
        long sum = 0L;
        long check = 0L;
        final int[] mc = new int[segments.length];
        for (int k = 0; k < 2; ++k) {
            check = 0L;
            sum = 0L;
            int mcsum = 0;
            for (int i = 0; i < segments.length; ++i) {
                sum += segments[i].count;
                final int n = mcsum;
                final int[] array = mc;
                final int n2 = i;
                final int modCount = segments[i].modCount;
                array[n2] = modCount;
                mcsum = n + modCount;
            }
            if (mcsum != 0) {
                for (int i = 0; i < segments.length; ++i) {
                    check += segments[i].count;
                    if (mc[i] != segments[i].modCount) {
                        check = -1L;
                        break;
                    }
                }
            }
            if (check == sum) {
                break;
            }
        }
        if (check != sum) {
            sum = 0L;
            for (int j = 0; j < segments.length; ++j) {
                segments[j].lock();
            }
            for (int j = 0; j < segments.length; ++j) {
                sum += segments[j].count;
            }
            for (int j = 0; j < segments.length; ++j) {
                segments[j].unlock();
            }
        }
        if (sum > 2147483647L) {
            return Integer.MAX_VALUE;
        }
        return (int)sum;
    }
    
    public Object get(final Object key) {
        final int hash = hash(key.hashCode());
        return this.segmentFor(hash).get(key, hash);
    }
    
    public boolean containsKey(final Object key) {
        final int hash = hash(key.hashCode());
        return this.segmentFor(hash).containsKey(key, hash);
    }
    
    public boolean containsValue(final Object value) {
        if (value == null) {
            throw new NullPointerException();
        }
        final Segment[] segments = this.segments;
        final int[] mc = new int[segments.length];
        for (int k = 0; k < 2; ++k) {
            final int sum = 0;
            int mcsum = 0;
            for (int i = 0; i < segments.length; ++i) {
                final int c = segments[i].count;
                final int n = mcsum;
                final int[] array = mc;
                final int n2 = i;
                final int modCount = segments[i].modCount;
                array[n2] = modCount;
                mcsum = n + modCount;
                if (segments[i].containsValue(value)) {
                    return true;
                }
            }
            boolean cleanSweep = true;
            if (mcsum != 0) {
                for (int j = 0; j < segments.length; ++j) {
                    final int c2 = segments[j].count;
                    if (mc[j] != segments[j].modCount) {
                        cleanSweep = false;
                        break;
                    }
                }
            }
            if (cleanSweep) {
                return false;
            }
        }
        for (int l = 0; l < segments.length; ++l) {
            segments[l].lock();
        }
        boolean found = false;
        try {
            for (int m = 0; m < segments.length; ++m) {
                if (segments[m].containsValue(value)) {
                    found = true;
                    break;
                }
            }
        }
        finally {
            for (int i2 = 0; i2 < segments.length; ++i2) {
                segments[i2].unlock();
            }
        }
        return found;
    }
    
    public boolean contains(final Object value) {
        return this.containsValue(value);
    }
    
    public Object put(final Object key, final Object value) {
        if (value == null) {
            throw new NullPointerException();
        }
        final int hash = hash(key.hashCode());
        return this.segmentFor(hash).put(key, hash, value, false);
    }
    
    public Object putIfAbsent(final Object key, final Object value) {
        if (value == null) {
            throw new NullPointerException();
        }
        final int hash = hash(key.hashCode());
        return this.segmentFor(hash).put(key, hash, value, true);
    }
    
    public void putAll(final Map m) {
        for (final Map.Entry e : m.entrySet()) {
            this.put(e.getKey(), e.getValue());
        }
    }
    
    public Object remove(final Object key) {
        final int hash = hash(key.hashCode());
        return this.segmentFor(hash).remove(key, hash, null);
    }
    
    public boolean remove(final Object key, final Object value) {
        if (value == null) {
            return false;
        }
        final int hash = hash(key.hashCode());
        return this.segmentFor(hash).remove(key, hash, value) != null;
    }
    
    public boolean replace(final Object key, final Object oldValue, final Object newValue) {
        if (oldValue == null || newValue == null) {
            throw new NullPointerException();
        }
        final int hash = hash(key.hashCode());
        return this.segmentFor(hash).replace(key, hash, oldValue, newValue);
    }
    
    public Object replace(final Object key, final Object value) {
        if (value == null) {
            throw new NullPointerException();
        }
        final int hash = hash(key.hashCode());
        return this.segmentFor(hash).replace(key, hash, value);
    }
    
    public void clear() {
        for (int i = 0; i < this.segments.length; ++i) {
            this.segments[i].clear();
        }
    }
    
    public Set keySet() {
        final Set ks = this.keySet;
        return (ks != null) ? ks : (this.keySet = new KeySet());
    }
    
    public Collection values() {
        final Collection vs = this.values;
        return (vs != null) ? vs : (this.values = new Values());
    }
    
    public Set entrySet() {
        final Set es = this.entrySet;
        return (es != null) ? es : (this.entrySet = new EntrySet());
    }
    
    public Enumeration keys() {
        return new KeyIterator();
    }
    
    public Enumeration elements() {
        return new ValueIterator();
    }
    
    private void writeObject(final ObjectOutputStream s) throws IOException {
        s.defaultWriteObject();
        for (int k = 0; k < this.segments.length; ++k) {
            final Segment seg = this.segments[k];
            seg.lock();
            try {
                final HashEntry[] tab = seg.table;
                for (int i = 0; i < tab.length; ++i) {
                    for (HashEntry e = tab[i]; e != null; e = e.next) {
                        s.writeObject(e.key);
                        s.writeObject(e.value);
                    }
                }
            }
            finally {
                seg.unlock();
            }
        }
        s.writeObject(null);
        s.writeObject(null);
    }
    
    private void readObject(final ObjectInputStream s) throws IOException, ClassNotFoundException {
        s.defaultReadObject();
        for (int i = 0; i < this.segments.length; ++i) {
            this.segments[i].setTable(new HashEntry[1]);
        }
        while (true) {
            final Object key = s.readObject();
            final Object value = s.readObject();
            if (key == null) {
                break;
            }
            this.put(key, value);
        }
    }
    
    static final class HashEntry
    {
        final Object key;
        final int hash;
        volatile Object value;
        final HashEntry next;
        
        HashEntry(final Object key, final int hash, final HashEntry next, final Object value) {
            this.key = key;
            this.hash = hash;
            this.next = next;
            this.value = value;
        }
        
        static final HashEntry[] newArray(final int i) {
            return new HashEntry[i];
        }
    }
    
    static final class Segment extends ReentrantLock implements Serializable
    {
        private static final long serialVersionUID = 2249069246763182397L;
        transient volatile int count;
        transient int modCount;
        transient int threshold;
        transient volatile HashEntry[] table;
        final float loadFactor;
        
        Segment(final int initialCapacity, final float lf) {
            this.loadFactor = lf;
            this.setTable(HashEntry.newArray(initialCapacity));
        }
        
        static final Segment[] newArray(final int i) {
            return new Segment[i];
        }
        
        void setTable(final HashEntry[] newTable) {
            this.threshold = (int)(newTable.length * this.loadFactor);
            this.table = newTable;
        }
        
        HashEntry getFirst(final int hash) {
            final HashEntry[] tab = this.table;
            return tab[hash & tab.length - 1];
        }
        
        Object readValueUnderLock(final HashEntry e) {
            this.lock();
            try {
                return e.value;
            }
            finally {
                this.unlock();
            }
        }
        
        Object get(final Object key, final int hash) {
            if (this.count != 0) {
                HashEntry e = this.getFirst(hash);
                while (e != null) {
                    if (e.hash == hash && key.equals(e.key)) {
                        final Object v = e.value;
                        if (v != null) {
                            return v;
                        }
                        return this.readValueUnderLock(e);
                    }
                    else {
                        e = e.next;
                    }
                }
            }
            return null;
        }
        
        boolean containsKey(final Object key, final int hash) {
            if (this.count != 0) {
                for (HashEntry e = this.getFirst(hash); e != null; e = e.next) {
                    if (e.hash == hash && key.equals(e.key)) {
                        return true;
                    }
                }
            }
            return false;
        }
        
        boolean containsValue(final Object value) {
            if (this.count != 0) {
                for (HashEntry e : this.table) {
                    while (e != null) {
                        Object v = e.value;
                        if (v == null) {
                            v = this.readValueUnderLock(e);
                        }
                        if (value.equals(v)) {
                            return true;
                        }
                        e = e.next;
                    }
                }
            }
            return false;
        }
        
        boolean replace(final Object key, final int hash, final Object oldValue, final Object newValue) {
            this.lock();
            try {
                HashEntry e;
                for (e = this.getFirst(hash); e != null && (e.hash != hash || !key.equals(e.key)); e = e.next) {}
                boolean replaced = false;
                if (e != null && oldValue.equals(e.value)) {
                    replaced = true;
                    e.value = newValue;
                }
                return replaced;
            }
            finally {
                this.unlock();
            }
        }
        
        Object replace(final Object key, final int hash, final Object newValue) {
            this.lock();
            try {
                HashEntry e;
                for (e = this.getFirst(hash); e != null && (e.hash != hash || !key.equals(e.key)); e = e.next) {}
                Object oldValue = null;
                if (e != null) {
                    oldValue = e.value;
                    e.value = newValue;
                }
                return oldValue;
            }
            finally {
                this.unlock();
            }
        }
        
        Object put(final Object key, final int hash, final Object value, final boolean onlyIfAbsent) {
            this.lock();
            try {
                int c = this.count;
                if (c++ > this.threshold) {
                    this.rehash();
                }
                final HashEntry[] tab = this.table;
                final int index = hash & tab.length - 1;
                HashEntry e;
                HashEntry first;
                for (first = (e = tab[index]); e != null && (e.hash != hash || !key.equals(e.key)); e = e.next) {}
                Object oldValue;
                if (e != null) {
                    oldValue = e.value;
                    if (!onlyIfAbsent) {
                        e.value = value;
                    }
                }
                else {
                    oldValue = null;
                    ++this.modCount;
                    tab[index] = new HashEntry(key, hash, first, value);
                    this.count = c;
                }
                return oldValue;
            }
            finally {
                this.unlock();
            }
        }
        
        void rehash() {
            final HashEntry[] oldTable = this.table;
            final int oldCapacity = oldTable.length;
            if (oldCapacity >= 1073741824) {
                return;
            }
            final HashEntry[] newTable = HashEntry.newArray(oldCapacity << 1);
            this.threshold = (int)(newTable.length * this.loadFactor);
            final int sizeMask = newTable.length - 1;
            for (final HashEntry e : oldTable) {
                if (e != null) {
                    final HashEntry next = e.next;
                    final int idx = e.hash & sizeMask;
                    if (next == null) {
                        newTable[idx] = e;
                    }
                    else {
                        HashEntry lastRun = e;
                        int lastIdx = idx;
                        for (HashEntry last = next; last != null; last = last.next) {
                            final int k = last.hash & sizeMask;
                            if (k != lastIdx) {
                                lastIdx = k;
                                lastRun = last;
                            }
                        }
                        newTable[lastIdx] = lastRun;
                        for (HashEntry p = e; p != lastRun; p = p.next) {
                            final int k = p.hash & sizeMask;
                            final HashEntry n = newTable[k];
                            newTable[k] = new HashEntry(p.key, p.hash, n, p.value);
                        }
                    }
                }
            }
            this.table = newTable;
        }
        
        Object remove(final Object key, final int hash, final Object value) {
            this.lock();
            try {
                final int c = this.count - 1;
                final HashEntry[] tab = this.table;
                final int index = hash & tab.length - 1;
                HashEntry e;
                HashEntry first;
                for (first = (e = tab[index]); e != null && (e.hash != hash || !key.equals(e.key)); e = e.next) {}
                Object oldValue = null;
                if (e != null) {
                    final Object v = e.value;
                    if (value == null || value.equals(v)) {
                        oldValue = v;
                        ++this.modCount;
                        HashEntry newFirst = e.next;
                        for (HashEntry p = first; p != e; p = p.next) {
                            newFirst = new HashEntry(p.key, p.hash, newFirst, p.value);
                        }
                        tab[index] = newFirst;
                        this.count = c;
                    }
                }
                return oldValue;
            }
            finally {
                this.unlock();
            }
        }
        
        void clear() {
            if (this.count != 0) {
                this.lock();
                try {
                    final HashEntry[] tab = this.table;
                    for (int i = 0; i < tab.length; ++i) {
                        tab[i] = null;
                    }
                    ++this.modCount;
                    this.count = 0;
                }
                finally {
                    this.unlock();
                }
            }
        }
    }
    
    abstract class HashIterator
    {
        int nextSegmentIndex;
        int nextTableIndex;
        HashEntry[] currentTable;
        HashEntry nextEntry;
        HashEntry lastReturned;
        
        HashIterator() {
            this.nextSegmentIndex = ConcurrentHashMap.this.segments.length - 1;
            this.nextTableIndex = -1;
            this.advance();
        }
        
        public boolean hasMoreElements() {
            return this.hasNext();
        }
        
        final void advance() {
            if (this.nextEntry != null && (this.nextEntry = this.nextEntry.next) != null) {
                return;
            }
            while (this.nextTableIndex >= 0) {
                if ((this.nextEntry = this.currentTable[this.nextTableIndex--]) != null) {
                    return;
                }
            }
            while (this.nextSegmentIndex >= 0) {
                final Segment seg = ConcurrentHashMap.this.segments[this.nextSegmentIndex--];
                if (seg.count != 0) {
                    this.currentTable = seg.table;
                    for (int j = this.currentTable.length - 1; j >= 0; --j) {
                        if ((this.nextEntry = this.currentTable[j]) != null) {
                            this.nextTableIndex = j - 1;
                            return;
                        }
                    }
                }
            }
        }
        
        public boolean hasNext() {
            return this.nextEntry != null;
        }
        
        HashEntry nextEntry() {
            if (this.nextEntry == null) {
                throw new NoSuchElementException();
            }
            this.lastReturned = this.nextEntry;
            this.advance();
            return this.lastReturned;
        }
        
        public void remove() {
            if (this.lastReturned == null) {
                throw new IllegalStateException();
            }
            ConcurrentHashMap.this.remove(this.lastReturned.key);
            this.lastReturned = null;
        }
    }
    
    final class KeyIterator extends HashIterator implements Iterator, Enumeration
    {
        public Object next() {
            return super.nextEntry().key;
        }
        
        public Object nextElement() {
            return super.nextEntry().key;
        }
    }
    
    final class ValueIterator extends HashIterator implements Iterator, Enumeration
    {
        public Object next() {
            return super.nextEntry().value;
        }
        
        public Object nextElement() {
            return super.nextEntry().value;
        }
    }
    
    final class WriteThroughEntry extends SimpleEntry
    {
        WriteThroughEntry(final Object k, final Object v) {
            super(k, v);
        }
        
        public Object setValue(final Object value) {
            if (value == null) {
                throw new NullPointerException();
            }
            final Object v = super.setValue(value);
            ConcurrentHashMap.this.put(this.getKey(), value);
            return v;
        }
    }
    
    final class EntryIterator extends HashIterator implements Iterator
    {
        public Object next() {
            final HashEntry e = super.nextEntry();
            return new WriteThroughEntry(e.key, e.value);
        }
    }
    
    final class KeySet extends AbstractSet
    {
        public Iterator iterator() {
            return new KeyIterator();
        }
        
        public int size() {
            return ConcurrentHashMap.this.size();
        }
        
        public boolean contains(final Object o) {
            return ConcurrentHashMap.this.containsKey(o);
        }
        
        public boolean remove(final Object o) {
            return ConcurrentHashMap.this.remove(o) != null;
        }
        
        public void clear() {
            ConcurrentHashMap.this.clear();
        }
    }
    
    final class Values extends AbstractCollection
    {
        public Iterator iterator() {
            return new ValueIterator();
        }
        
        public int size() {
            return ConcurrentHashMap.this.size();
        }
        
        public boolean contains(final Object o) {
            return ConcurrentHashMap.this.containsValue(o);
        }
        
        public void clear() {
            ConcurrentHashMap.this.clear();
        }
    }
    
    final class EntrySet extends AbstractSet
    {
        public Iterator iterator() {
            return new EntryIterator();
        }
        
        public boolean contains(final Object o) {
            if (!(o instanceof Map.Entry)) {
                return false;
            }
            final Map.Entry e = (Map.Entry)o;
            final Object v = ConcurrentHashMap.this.get(e.getKey());
            return v != null && v.equals(e.getValue());
        }
        
        public boolean remove(final Object o) {
            if (!(o instanceof Map.Entry)) {
                return false;
            }
            final Map.Entry e = (Map.Entry)o;
            return ConcurrentHashMap.this.remove(e.getKey(), e.getValue());
        }
        
        public int size() {
            return ConcurrentHashMap.this.size();
        }
        
        public void clear() {
            ConcurrentHashMap.this.clear();
        }
    }
}
