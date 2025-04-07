// 
// Decompiled by Procyon v0.5.36
// 

package edu.emory.mathcs.backport.java.util.concurrent;

import edu.emory.mathcs.backport.java.util.Collections;
import edu.emory.mathcs.backport.java.util.AbstractCollection;
import java.util.SortedSet;
import edu.emory.mathcs.backport.java.util.AbstractSet;
import java.util.List;
import java.util.NoSuchElementException;
import edu.emory.mathcs.backport.java.util.NavigableMap;
import java.util.Collection;
import edu.emory.mathcs.backport.java.util.NavigableSet;
import java.util.Set;
import java.io.ObjectInputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.util.Iterator;
import java.util.ArrayList;
import java.util.SortedMap;
import java.util.Map;
import java.util.Comparator;
import java.util.Random;
import java.io.Serializable;
import edu.emory.mathcs.backport.java.util.AbstractMap;

public class ConcurrentSkipListMap extends AbstractMap implements ConcurrentNavigableMap, Cloneable, Serializable
{
    private static final long serialVersionUID = -8627078645895051609L;
    private static final Random seedGenerator;
    private static final Object BASE_HEADER;
    private transient volatile HeadIndex head;
    private final Comparator comparator;
    private transient int randomSeed;
    private transient KeySet keySet;
    private transient EntrySet entrySet;
    private transient Values values;
    private transient ConcurrentNavigableMap descendingMap;
    private static final int EQ = 1;
    private static final int LT = 2;
    private static final int GT = 0;
    
    final void initialize() {
        this.keySet = null;
        this.entrySet = null;
        this.values = null;
        this.descendingMap = null;
        this.randomSeed = (ConcurrentSkipListMap.seedGenerator.nextInt() | 0x100);
        this.head = new HeadIndex(new Node(null, ConcurrentSkipListMap.BASE_HEADER, null), null, null, 1);
    }
    
    private synchronized boolean casHead(final HeadIndex cmp, final HeadIndex val) {
        if (this.head == cmp) {
            this.head = val;
            return true;
        }
        return false;
    }
    
    private Comparable comparable(final Object key) throws ClassCastException {
        if (key == null) {
            throw new NullPointerException();
        }
        if (this.comparator != null) {
            return new ComparableUsingComparator(key, this.comparator);
        }
        return (Comparable)key;
    }
    
    int compare(final Object k1, final Object k2) throws ClassCastException {
        final Comparator cmp = this.comparator;
        if (cmp != null) {
            return cmp.compare(k1, k2);
        }
        return ((Comparable)k1).compareTo(k2);
    }
    
    boolean inHalfOpenRange(final Object key, final Object least, final Object fence) {
        if (key == null) {
            throw new NullPointerException();
        }
        return (least == null || this.compare(key, least) >= 0) && (fence == null || this.compare(key, fence) < 0);
    }
    
    boolean inOpenRange(final Object key, final Object least, final Object fence) {
        if (key == null) {
            throw new NullPointerException();
        }
        return (least == null || this.compare(key, least) >= 0) && (fence == null || this.compare(key, fence) <= 0);
    }
    
    private Node findPredecessor(final Comparable key) {
        if (key == null) {
            throw new NullPointerException();
        }
        Index q = null;
    Label_0110:
        while (true) {
            q = this.head;
            Index r = q.right;
            while (true) {
                if (r != null) {
                    final Node n = r.node;
                    final Object k = n.key;
                    if (n.value == null) {
                        if (!q.unlink(r)) {
                            break;
                        }
                        r = q.right;
                        continue;
                    }
                    else if (key.compareTo(k) > 0) {
                        q = r;
                        r = r.right;
                        continue;
                    }
                }
                final Index d = q.down;
                if (d == null) {
                    break Label_0110;
                }
                q = d;
                r = d.right;
            }
        }
        return q.node;
    }
    
    private Node findNode(final Comparable key) {
    Label_0000:
        while (true) {
            Node b = this.findPredecessor(key);
            Node n = b.next;
            while (n != null) {
                final Node f = n.next;
                if (n == b.next) {
                    final Object v = n.value;
                    if (v == null) {
                        n.helpDelete(b, f);
                    }
                    else if (v != n) {
                        if (b.value != null) {
                            final int c = key.compareTo(n.key);
                            if (c == 0) {
                                return n;
                            }
                            if (c < 0) {
                                return null;
                            }
                            b = n;
                            n = f;
                            continue;
                        }
                    }
                }
                continue Label_0000;
            }
            return null;
        }
    }
    
    private Object doGet(final Object okey) {
        final Comparable key = this.comparable(okey);
        Node bound = null;
        Index q = this.head;
        Index r = q.right;
        while (true) {
            Node n;
            Object k;
            if (r != null && (n = r.node) != bound && (k = n.key) != null) {
                final int c;
                if ((c = key.compareTo(k)) > 0) {
                    q = r;
                    r = r.right;
                    continue;
                }
                if (c == 0) {
                    final Object v = n.value;
                    return (v != null) ? v : this.getUsingFindNode(key);
                }
                bound = n;
            }
            final Index d;
            if ((d = q.down) == null) {
                for (n = q.node.next; n != null; n = n.next) {
                    if ((k = n.key) != null) {
                        final int c;
                        if ((c = key.compareTo(k)) == 0) {
                            final Object v2 = n.value;
                            return (v2 != null) ? v2 : this.getUsingFindNode(key);
                        }
                        if (c < 0) {
                            break;
                        }
                    }
                }
                return null;
            }
            q = d;
            r = d.right;
        }
    }
    
    private Object getUsingFindNode(final Comparable key) {
        while (true) {
            final Node n = this.findNode(key);
            if (n == null) {
                return null;
            }
            final Object v = n.value;
            if (v != null) {
                return v;
            }
        }
    }
    
    private Object doPut(final Object kkey, final Object value, final boolean onlyIfAbsent) {
        final Comparable key = this.comparable(kkey);
    Label_0191_Outer:
        while (true) {
            Node b = this.findPredecessor(key);
            Node n = b.next;
            Node z;
            while (true) {
                while (n != null) {
                    final Node f = n.next;
                    if (n == b.next) {
                        final Object v = n.value;
                        if (v == null) {
                            n.helpDelete(b, f);
                        }
                        else if (v != n) {
                            if (b.value != null) {
                                final int c = key.compareTo(n.key);
                                if (c > 0) {
                                    b = n;
                                    n = f;
                                    continue Label_0191_Outer;
                                }
                                if (c != 0) {
                                    break;
                                }
                                if (onlyIfAbsent || n.casValue(v, value)) {
                                    return v;
                                }
                            }
                        }
                    }
                    continue Label_0191_Outer;
                }
                z = new Node(kkey, value, n);
                if (!b.casNext(n, z)) {
                    continue;
                }
                break;
            }
            final int level = this.randomLevel();
            if (level > 0) {
                this.insertIndex(z, level);
            }
            return null;
        }
    }
    
    private int randomLevel() {
        int x = this.randomSeed;
        x ^= x << 13;
        x ^= x >>> 17;
        x = (this.randomSeed = (x ^ x << 5));
        if ((x & 0x8001) != 0x0) {
            return 0;
        }
        int level = 1;
        while (((x >>>= 1) & 0x1) != 0x0) {
            ++level;
        }
        return level;
    }
    
    private void insertIndex(final Node z, int level) {
        final HeadIndex h = this.head;
        final int max = h.level;
        if (level <= max) {
            Index idx = null;
            for (int i = 1; i <= level; ++i) {
                idx = new Index(z, idx, null);
            }
            this.addIndex(idx, h, level);
        }
        else {
            level = max + 1;
            final Index[] idxs = new Index[level + 1];
            Index idx2 = null;
            for (int j = 1; j <= level; ++j) {
                idx2 = (idxs[j] = new Index(z, idx2, null));
            }
            HeadIndex oldh;
            while (true) {
                oldh = this.head;
                final int oldLevel = oldh.level;
                if (level <= oldLevel) {
                    final int k = level;
                    break;
                }
                HeadIndex newh = oldh;
                final Node oldbase = oldh.node;
                for (int l = oldLevel + 1; l <= level; ++l) {
                    newh = new HeadIndex(oldbase, newh, idxs[l], l);
                }
                if (this.casHead(oldh, newh)) {
                    final int k = oldLevel;
                    break;
                }
            }
            final int indexLevel;
            this.addIndex(idxs[indexLevel], oldh, indexLevel);
        }
    }
    
    private void addIndex(final Index idx, final HeadIndex h, final int indexLevel) {
        int insertionLevel = indexLevel;
        final Comparable key = this.comparable(idx.node.key);
        if (key == null) {
            throw new NullPointerException();
        }
        while (true) {
            int j = h.level;
            Index q = h;
            Index r = q.right;
            Index t = idx;
            while (true) {
                if (r != null) {
                    final Node n = r.node;
                    final int c = key.compareTo(n.key);
                    if (n.value == null) {
                        if (!q.unlink(r)) {
                            break;
                        }
                        r = q.right;
                        continue;
                    }
                    else if (c > 0) {
                        q = r;
                        r = r.right;
                        continue;
                    }
                }
                if (j == insertionLevel) {
                    if (t.indexesDeletedNode()) {
                        this.findNode(key);
                        return;
                    }
                    if (!q.link(r, t)) {
                        break;
                    }
                    if (--insertionLevel == 0) {
                        if (t.indexesDeletedNode()) {
                            this.findNode(key);
                        }
                        return;
                    }
                }
                if (--j >= insertionLevel && j < indexLevel) {
                    t = t.down;
                }
                q = q.down;
                r = q.right;
            }
        }
    }
    
    final Object doRemove(final Object okey, final Object value) {
        final Comparable key = this.comparable(okey);
    Label_0006:
        while (true) {
            Node b = this.findPredecessor(key);
            Node n = b.next;
            while (n != null) {
                final Node f = n.next;
                if (n == b.next) {
                    final Object v = n.value;
                    if (v == null) {
                        n.helpDelete(b, f);
                    }
                    else if (v != n) {
                        if (b.value != null) {
                            final int c = key.compareTo(n.key);
                            if (c < 0) {
                                return null;
                            }
                            if (c > 0) {
                                b = n;
                                n = f;
                                continue;
                            }
                            if (value != null && !value.equals(v)) {
                                return null;
                            }
                            if (n.casValue(v, null)) {
                                if (!n.appendMarker(f) || !b.casNext(n, f)) {
                                    this.findNode(key);
                                }
                                else {
                                    this.findPredecessor(key);
                                    if (this.head.right == null) {
                                        this.tryReduceLevel();
                                    }
                                }
                                return v;
                            }
                        }
                    }
                }
                continue Label_0006;
            }
            return null;
        }
    }
    
    private void tryReduceLevel() {
        final HeadIndex h = this.head;
        final HeadIndex d;
        final HeadIndex e;
        if (h.level > 3 && (d = (HeadIndex)h.down) != null && (e = (HeadIndex)d.down) != null && e.right == null && d.right == null && h.right == null && this.casHead(h, d) && h.right != null) {
            this.casHead(d, h);
        }
    }
    
    Node findFirst() {
        while (true) {
            final Node b = this.head.node;
            final Node n = b.next;
            if (n == null) {
                return null;
            }
            if (n.value != null) {
                return n;
            }
            n.helpDelete(b, n.next);
        }
    }
    
    Map.Entry doRemoveFirstEntry() {
        while (true) {
            final Node b = this.head.node;
            final Node n = b.next;
            if (n == null) {
                return null;
            }
            final Node f = n.next;
            if (n != b.next) {
                continue;
            }
            final Object v = n.value;
            if (v == null) {
                n.helpDelete(b, f);
            }
            else {
                if (!n.casValue(v, null)) {
                    continue;
                }
                if (!n.appendMarker(f) || !b.casNext(n, f)) {
                    this.findFirst();
                }
                this.clearIndexToFirst();
                return new SimpleImmutableEntry(n.key, v);
            }
        }
    }
    
    private void clearIndexToFirst() {
    Block_3:
        while (true) {
            Index q = this.head;
            while (true) {
                final Index r = q.right;
                if (r != null && r.indexesDeletedNode() && !q.unlink(r)) {
                    break;
                }
                if ((q = q.down) == null) {
                    break Block_3;
                }
            }
        }
        if (this.head.right == null) {
            this.tryReduceLevel();
        }
    }
    
    Node findLast() {
        Index q = this.head;
        Node b;
        while (true) {
            final Index r;
            if ((r = q.right) != null) {
                if (r.indexesDeletedNode()) {
                    q.unlink(r);
                    q = this.head;
                }
                else {
                    q = r;
                }
            }
            else {
                final Index d;
                if ((d = q.down) == null) {
                    b = q.node;
                    Node n = b.next;
                    while (n != null) {
                        final Node f = n.next;
                        if (n == b.next) {
                            final Object v = n.value;
                            if (v == null) {
                                n.helpDelete(b, f);
                            }
                            else if (v != n) {
                                if (b.value != null) {
                                    b = n;
                                    n = f;
                                    continue;
                                }
                            }
                        }
                        q = this.head;
                        continue Label_0165;
                    }
                    break;
                }
                q = d;
            }
            Label_0165:;
        }
        return b.isBaseHeader() ? null : b;
    }
    
    private Node findPredecessorOfLast() {
        Index q = null;
    Label_0059:
        while (true) {
            q = this.head;
            while (true) {
                final Index r;
                if ((r = q.right) != null) {
                    if (r.indexesDeletedNode()) {
                        q.unlink(r);
                        break;
                    }
                    if (r.node.next != null) {
                        q = r;
                        continue;
                    }
                }
                final Index d;
                if ((d = q.down) == null) {
                    break Label_0059;
                }
                q = d;
            }
        }
        return q.node;
    }
    
    Map.Entry doRemoveLastEntry() {
        while (true) {
            Node b = this.findPredecessorOfLast();
            Node n = b.next;
            if (n == null) {
                if (b.isBaseHeader()) {
                    return null;
                }
                continue;
            }
            else {
                while (true) {
                    final Node f = n.next;
                    if (n != b.next) {
                        break;
                    }
                    final Object v = n.value;
                    if (v == null) {
                        n.helpDelete(b, f);
                        break;
                    }
                    if (v == n) {
                        break;
                    }
                    if (b.value == null) {
                        break;
                    }
                    if (f != null) {
                        b = n;
                        n = f;
                    }
                    else {
                        if (!n.casValue(v, null)) {
                            break;
                        }
                        final Object key = n.key;
                        final Comparable ck = this.comparable(key);
                        if (!n.appendMarker(f) || !b.casNext(n, f)) {
                            this.findNode(ck);
                        }
                        else {
                            this.findPredecessor(ck);
                            if (this.head.right == null) {
                                this.tryReduceLevel();
                            }
                        }
                        return new SimpleImmutableEntry(key, v);
                    }
                }
            }
        }
    }
    
    Node findNear(final Object kkey, final int rel) {
        final Comparable key = this.comparable(kkey);
    Label_0006:
        while (true) {
            Node b = this.findPredecessor(key);
            Node n = b.next;
            while (n != null) {
                final Node f = n.next;
                if (n == b.next) {
                    final Object v = n.value;
                    if (v == null) {
                        n.helpDelete(b, f);
                    }
                    else if (v != n) {
                        if (b.value != null) {
                            final int c = key.compareTo(n.key);
                            if ((c == 0 && (rel & 0x1) != 0x0) || (c < 0 && (rel & 0x2) == 0x0)) {
                                return n;
                            }
                            if (c <= 0 && (rel & 0x2) != 0x0) {
                                return b.isBaseHeader() ? null : b;
                            }
                            b = n;
                            n = f;
                            continue;
                        }
                    }
                }
                continue Label_0006;
            }
            return ((rel & 0x2) == 0x0 || b.isBaseHeader()) ? null : b;
        }
    }
    
    SimpleImmutableEntry getNear(final Object key, final int rel) {
        while (true) {
            final Node n = this.findNear(key, rel);
            if (n == null) {
                return null;
            }
            final SimpleImmutableEntry e = n.createSnapshot();
            if (e != null) {
                return e;
            }
        }
    }
    
    public ConcurrentSkipListMap() {
        this.comparator = null;
        this.initialize();
    }
    
    public ConcurrentSkipListMap(final Comparator comparator) {
        this.comparator = comparator;
        this.initialize();
    }
    
    public ConcurrentSkipListMap(final Map m) {
        this.comparator = null;
        this.initialize();
        this.putAll(m);
    }
    
    public ConcurrentSkipListMap(final SortedMap m) {
        this.comparator = m.comparator();
        this.initialize();
        this.buildFromSorted(m);
    }
    
    public Object clone() {
        ConcurrentSkipListMap clone = null;
        try {
            clone = (ConcurrentSkipListMap)super.clone();
        }
        catch (CloneNotSupportedException e) {
            throw new InternalError();
        }
        clone.initialize();
        clone.buildFromSorted(this);
        return clone;
    }
    
    private void buildFromSorted(final SortedMap map) {
        if (map == null) {
            throw new NullPointerException();
        }
        HeadIndex h = this.head;
        Node basepred = h.node;
        final ArrayList preds = new ArrayList();
        for (int i = 0; i <= h.level; ++i) {
            preds.add(null);
        }
        Index q = h;
        for (int j = h.level; j > 0; --j) {
            preds.set(j, q);
            q = q.down;
        }
        for (final Map.Entry e : map.entrySet()) {
            int k = this.randomLevel();
            if (k > h.level) {
                k = h.level + 1;
            }
            final Object l = e.getKey();
            final Object v = e.getValue();
            if (l == null || v == null) {
                throw new NullPointerException();
            }
            final Node z = new Node(l, v, null);
            basepred.next = z;
            basepred = z;
            if (k <= 0) {
                continue;
            }
            Index idx = null;
            for (int m = 1; m <= k; ++m) {
                idx = new Index(z, idx, null);
                if (m > h.level) {
                    h = new HeadIndex(h.node, h, idx, m);
                }
                if (m < preds.size()) {
                    preds.set(m, preds.get(m).right = idx);
                }
                else {
                    preds.add(idx);
                }
            }
        }
        this.head = h;
    }
    
    private void writeObject(final ObjectOutputStream s) throws IOException {
        s.defaultWriteObject();
        for (Node n = this.findFirst(); n != null; n = n.next) {
            final Object v = n.getValidValue();
            if (v != null) {
                s.writeObject(n.key);
                s.writeObject(v);
            }
        }
        s.writeObject(null);
    }
    
    private void readObject(final ObjectInputStream s) throws IOException, ClassNotFoundException {
        s.defaultReadObject();
        this.initialize();
        HeadIndex h = this.head;
        Node basepred = h.node;
        final ArrayList preds = new ArrayList();
        for (int i = 0; i <= h.level; ++i) {
            preds.add(null);
        }
        Index q = h;
        for (int j = h.level; j > 0; --j) {
            preds.set(j, q);
            q = q.down;
        }
        while (true) {
            final Object k = s.readObject();
            if (k == null) {
                this.head = h;
                return;
            }
            final Object v = s.readObject();
            if (v == null) {
                throw new NullPointerException();
            }
            final Object key = k;
            final Object val = v;
            int l = this.randomLevel();
            if (l > h.level) {
                l = h.level + 1;
            }
            final Node z = new Node(key, val, null);
            basepred.next = z;
            basepred = z;
            if (l <= 0) {
                continue;
            }
            Index idx = null;
            for (int m = 1; m <= l; ++m) {
                idx = new Index(z, idx, null);
                if (m > h.level) {
                    h = new HeadIndex(h.node, h, idx, m);
                }
                if (m < preds.size()) {
                    preds.set(m, preds.get(m).right = idx);
                }
                else {
                    preds.add(idx);
                }
            }
        }
    }
    
    public boolean containsKey(final Object key) {
        return this.doGet(key) != null;
    }
    
    public Object get(final Object key) {
        return this.doGet(key);
    }
    
    public Object put(final Object key, final Object value) {
        if (value == null) {
            throw new NullPointerException();
        }
        return this.doPut(key, value, false);
    }
    
    public Object remove(final Object key) {
        return this.doRemove(key, null);
    }
    
    public boolean containsValue(final Object value) {
        if (value == null) {
            throw new NullPointerException();
        }
        for (Node n = this.findFirst(); n != null; n = n.next) {
            final Object v = n.getValidValue();
            if (v != null && value.equals(v)) {
                return true;
            }
        }
        return false;
    }
    
    public int size() {
        long count = 0L;
        for (Node n = this.findFirst(); n != null; n = n.next) {
            if (n.getValidValue() != null) {
                ++count;
            }
        }
        return (count >= 2147483647L) ? Integer.MAX_VALUE : ((int)count);
    }
    
    public boolean isEmpty() {
        return this.findFirst() == null;
    }
    
    public void clear() {
        this.initialize();
    }
    
    public Set keySet() {
        final KeySet ks = this.keySet;
        return (ks != null) ? ks : (this.keySet = new KeySet(this));
    }
    
    public NavigableSet navigableKeySet() {
        final KeySet ks = this.keySet;
        return (ks != null) ? ks : (this.keySet = new KeySet(this));
    }
    
    public Collection values() {
        final Values vs = this.values;
        return (vs != null) ? vs : (this.values = new Values(this));
    }
    
    public Set entrySet() {
        final EntrySet es = this.entrySet;
        return (es != null) ? es : (this.entrySet = new EntrySet(this));
    }
    
    public NavigableMap descendingMap() {
        final ConcurrentNavigableMap dm = this.descendingMap;
        return (dm != null) ? dm : (this.descendingMap = new SubMap(this, null, false, null, false, true));
    }
    
    public NavigableSet descendingKeySet() {
        return this.descendingMap().navigableKeySet();
    }
    
    public boolean equals(final Object o) {
        if (o == this) {
            return true;
        }
        if (!(o instanceof Map)) {
            return false;
        }
        final Map m = (Map)o;
        try {
            for (final Map.Entry e : this.entrySet()) {
                if (!e.getValue().equals(m.get(e.getKey()))) {
                    return false;
                }
            }
            for (final Map.Entry e : m.entrySet()) {
                final Object k = e.getKey();
                final Object v = e.getValue();
                if (k == null || v == null || !v.equals(this.get(k))) {
                    return false;
                }
            }
            return true;
        }
        catch (ClassCastException unused) {
            return false;
        }
        catch (NullPointerException unused2) {
            return false;
        }
    }
    
    public Object putIfAbsent(final Object key, final Object value) {
        if (value == null) {
            throw new NullPointerException();
        }
        return this.doPut(key, value, true);
    }
    
    public boolean remove(final Object key, final Object value) {
        if (key == null) {
            throw new NullPointerException();
        }
        return value != null && this.doRemove(key, value) != null;
    }
    
    public boolean replace(final Object key, final Object oldValue, final Object newValue) {
        if (oldValue == null || newValue == null) {
            throw new NullPointerException();
        }
        final Comparable k = this.comparable(key);
        while (true) {
            final Node n = this.findNode(k);
            if (n == null) {
                return false;
            }
            final Object v = n.value;
            if (v == null) {
                continue;
            }
            if (!oldValue.equals(v)) {
                return false;
            }
            if (n.casValue(v, newValue)) {
                return true;
            }
        }
    }
    
    public Object replace(final Object key, final Object value) {
        if (value == null) {
            throw new NullPointerException();
        }
        final Comparable k = this.comparable(key);
        while (true) {
            final Node n = this.findNode(k);
            if (n == null) {
                return null;
            }
            final Object v = n.value;
            if (v != null && n.casValue(v, value)) {
                return v;
            }
        }
    }
    
    public Comparator comparator() {
        return this.comparator;
    }
    
    public Object firstKey() {
        final Node n = this.findFirst();
        if (n == null) {
            throw new NoSuchElementException();
        }
        return n.key;
    }
    
    public Object lastKey() {
        final Node n = this.findLast();
        if (n == null) {
            throw new NoSuchElementException();
        }
        return n.key;
    }
    
    public NavigableMap subMap(final Object fromKey, final boolean fromInclusive, final Object toKey, final boolean toInclusive) {
        if (fromKey == null || toKey == null) {
            throw new NullPointerException();
        }
        return new SubMap(this, fromKey, fromInclusive, toKey, toInclusive, false);
    }
    
    public NavigableMap headMap(final Object toKey, final boolean inclusive) {
        if (toKey == null) {
            throw new NullPointerException();
        }
        return new SubMap(this, null, false, toKey, inclusive, false);
    }
    
    public NavigableMap tailMap(final Object fromKey, final boolean inclusive) {
        if (fromKey == null) {
            throw new NullPointerException();
        }
        return new SubMap(this, fromKey, inclusive, null, false, false);
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
    
    public Map.Entry lowerEntry(final Object key) {
        return this.getNear(key, 2);
    }
    
    public Object lowerKey(final Object key) {
        final Node n = this.findNear(key, 2);
        return (n == null) ? null : n.key;
    }
    
    public Map.Entry floorEntry(final Object key) {
        return this.getNear(key, 3);
    }
    
    public Object floorKey(final Object key) {
        final Node n = this.findNear(key, 3);
        return (n == null) ? null : n.key;
    }
    
    public Map.Entry ceilingEntry(final Object key) {
        return this.getNear(key, 1);
    }
    
    public Object ceilingKey(final Object key) {
        final Node n = this.findNear(key, 1);
        return (n == null) ? null : n.key;
    }
    
    public Map.Entry higherEntry(final Object key) {
        return this.getNear(key, 0);
    }
    
    public Object higherKey(final Object key) {
        final Node n = this.findNear(key, 0);
        return (n == null) ? null : n.key;
    }
    
    public Map.Entry firstEntry() {
        while (true) {
            final Node n = this.findFirst();
            if (n == null) {
                return null;
            }
            final SimpleImmutableEntry e = n.createSnapshot();
            if (e != null) {
                return e;
            }
        }
    }
    
    public Map.Entry lastEntry() {
        while (true) {
            final Node n = this.findLast();
            if (n == null) {
                return null;
            }
            final SimpleImmutableEntry e = n.createSnapshot();
            if (e != null) {
                return e;
            }
        }
    }
    
    public Map.Entry pollFirstEntry() {
        return this.doRemoveFirstEntry();
    }
    
    public Map.Entry pollLastEntry() {
        return this.doRemoveLastEntry();
    }
    
    Iterator keyIterator() {
        return new KeyIterator();
    }
    
    Iterator valueIterator() {
        return new ValueIterator();
    }
    
    Iterator entryIterator() {
        return new EntryIterator();
    }
    
    static final List toList(final Collection c) {
        final List list = new ArrayList();
        final Iterator itr = c.iterator();
        while (itr.hasNext()) {
            list.add(itr.next());
        }
        return list;
    }
    
    static {
        seedGenerator = new Random();
        BASE_HEADER = new Object();
    }
    
    static final class Node
    {
        final Object key;
        volatile Object value;
        volatile Node next;
        
        Node(final Object key, final Object value, final Node next) {
            this.key = key;
            this.value = value;
            this.next = next;
        }
        
        Node(final Node next) {
            this.key = null;
            this.value = this;
            this.next = next;
        }
        
        synchronized boolean casValue(final Object cmp, final Object val) {
            if (this.value == cmp) {
                this.value = val;
                return true;
            }
            return false;
        }
        
        synchronized boolean casNext(final Node cmp, final Node val) {
            if (this.next == cmp) {
                this.next = val;
                return true;
            }
            return false;
        }
        
        boolean isMarker() {
            return this.value == this;
        }
        
        boolean isBaseHeader() {
            return this.value == ConcurrentSkipListMap.BASE_HEADER;
        }
        
        boolean appendMarker(final Node f) {
            return this.casNext(f, new Node(f));
        }
        
        void helpDelete(final Node b, final Node f) {
            if (f == this.next && this == b.next) {
                if (f == null || f.value != f) {
                    this.appendMarker(f);
                }
                else {
                    b.casNext(this, f.next);
                }
            }
        }
        
        Object getValidValue() {
            final Object v = this.value;
            if (v == this || v == ConcurrentSkipListMap.BASE_HEADER) {
                return null;
            }
            return v;
        }
        
        SimpleImmutableEntry createSnapshot() {
            final Object v = this.getValidValue();
            if (v == null) {
                return null;
            }
            return new SimpleImmutableEntry(this.key, v);
        }
    }
    
    static class Index
    {
        final Node node;
        final Index down;
        volatile Index right;
        
        Index(final Node node, final Index down, final Index right) {
            this.node = node;
            this.down = down;
            this.right = right;
        }
        
        final synchronized boolean casRight(final Index cmp, final Index val) {
            if (this.right == cmp) {
                this.right = val;
                return true;
            }
            return false;
        }
        
        final boolean indexesDeletedNode() {
            return this.node.value == null;
        }
        
        final boolean link(final Index succ, final Index newSucc) {
            final Node n = this.node;
            newSucc.right = succ;
            return n.value != null && this.casRight(succ, newSucc);
        }
        
        final boolean unlink(final Index succ) {
            return !this.indexesDeletedNode() && this.casRight(succ, succ.right);
        }
    }
    
    static final class HeadIndex extends Index
    {
        final int level;
        
        HeadIndex(final Node node, final Index down, final Index right, final int level) {
            super(node, down, right);
            this.level = level;
        }
    }
    
    static final class ComparableUsingComparator implements Comparable
    {
        final Object actualKey;
        final Comparator cmp;
        
        ComparableUsingComparator(final Object key, final Comparator cmp) {
            this.actualKey = key;
            this.cmp = cmp;
        }
        
        public int compareTo(final Object k2) {
            return this.cmp.compare(this.actualKey, k2);
        }
    }
    
    abstract class Iter implements Iterator
    {
        Node lastReturned;
        Node next;
        Object nextValue;
        
        Iter() {
            while (true) {
                this.next = ConcurrentSkipListMap.this.findFirst();
                if (this.next == null) {
                    break;
                }
                final Object x = this.next.value;
                if (x != null && x != this.next) {
                    this.nextValue = x;
                    break;
                }
            }
        }
        
        public final boolean hasNext() {
            return this.next != null;
        }
        
        final void advance() {
            if (this.next == null) {
                throw new NoSuchElementException();
            }
            this.lastReturned = this.next;
            while (true) {
                this.next = this.next.next;
                if (this.next == null) {
                    break;
                }
                final Object x = this.next.value;
                if (x != null && x != this.next) {
                    this.nextValue = x;
                    break;
                }
            }
        }
        
        public void remove() {
            final Node l = this.lastReturned;
            if (l == null) {
                throw new IllegalStateException();
            }
            ConcurrentSkipListMap.this.remove(l.key);
            this.lastReturned = null;
        }
    }
    
    final class ValueIterator extends Iter
    {
        public Object next() {
            final Object v = this.nextValue;
            this.advance();
            return v;
        }
    }
    
    final class KeyIterator extends Iter
    {
        public Object next() {
            final Node n = this.next;
            this.advance();
            return n.key;
        }
    }
    
    final class EntryIterator extends Iter
    {
        public Object next() {
            final Node n = this.next;
            final Object v = this.nextValue;
            this.advance();
            return new SimpleImmutableEntry(n.key, v);
        }
    }
    
    static final class KeySet extends AbstractSet implements NavigableSet
    {
        private final ConcurrentNavigableMap m;
        
        KeySet(final ConcurrentNavigableMap map) {
            this.m = map;
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
        
        public boolean remove(final Object o) {
            return this.m.remove(o) != null;
        }
        
        public void clear() {
            this.m.clear();
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
        
        public Comparator comparator() {
            return this.m.comparator();
        }
        
        public Object first() {
            return this.m.firstKey();
        }
        
        public Object last() {
            return this.m.lastKey();
        }
        
        public Object pollFirst() {
            final Map.Entry e = this.m.pollFirstEntry();
            return (e == null) ? null : e.getKey();
        }
        
        public Object pollLast() {
            final Map.Entry e = this.m.pollLastEntry();
            return (e == null) ? null : e.getKey();
        }
        
        public Object[] toArray() {
            return ConcurrentSkipListMap.toList(this).toArray();
        }
        
        public Object[] toArray(final Object[] a) {
            return ConcurrentSkipListMap.toList(this).toArray(a);
        }
        
        public Iterator iterator() {
            if (this.m instanceof ConcurrentSkipListMap) {
                return ((ConcurrentSkipListMap)this.m).keyIterator();
            }
            return ((SubMap)this.m).keyIterator();
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
        
        public Iterator descendingIterator() {
            return this.descendingSet().iterator();
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
    
    static final class Values extends AbstractCollection
    {
        private final ConcurrentNavigableMap m;
        
        Values(final ConcurrentNavigableMap map) {
            this.m = map;
        }
        
        public Iterator iterator() {
            if (this.m instanceof ConcurrentSkipListMap) {
                return ((ConcurrentSkipListMap)this.m).valueIterator();
            }
            return ((SubMap)this.m).valueIterator();
        }
        
        public boolean isEmpty() {
            return this.m.isEmpty();
        }
        
        public int size() {
            return this.m.size();
        }
        
        public boolean contains(final Object o) {
            return this.m.containsValue(o);
        }
        
        public void clear() {
            this.m.clear();
        }
        
        public Object[] toArray() {
            return ConcurrentSkipListMap.toList(this).toArray();
        }
        
        public Object[] toArray(final Object[] a) {
            return ConcurrentSkipListMap.toList(this).toArray(a);
        }
    }
    
    static final class EntrySet extends AbstractSet
    {
        private final ConcurrentNavigableMap m;
        
        EntrySet(final ConcurrentNavigableMap map) {
            this.m = map;
        }
        
        public Iterator iterator() {
            if (this.m instanceof ConcurrentSkipListMap) {
                return ((ConcurrentSkipListMap)this.m).entryIterator();
            }
            return ((SubMap)this.m).entryIterator();
        }
        
        public boolean contains(final Object o) {
            if (!(o instanceof Map.Entry)) {
                return false;
            }
            final Map.Entry e = (Map.Entry)o;
            final Object v = this.m.get(e.getKey());
            return v != null && v.equals(e.getValue());
        }
        
        public boolean remove(final Object o) {
            if (!(o instanceof Map.Entry)) {
                return false;
            }
            final Map.Entry e = (Map.Entry)o;
            return this.m.remove(e.getKey(), e.getValue());
        }
        
        public boolean isEmpty() {
            return this.m.isEmpty();
        }
        
        public int size() {
            return this.m.size();
        }
        
        public void clear() {
            this.m.clear();
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
        
        public Object[] toArray() {
            return ConcurrentSkipListMap.toList(this).toArray();
        }
        
        public Object[] toArray(final Object[] a) {
            return ConcurrentSkipListMap.toList(this).toArray(a);
        }
    }
    
    static final class SubMap extends AbstractMap implements ConcurrentNavigableMap, Cloneable, Serializable
    {
        private static final long serialVersionUID = -7647078645895051609L;
        private final ConcurrentSkipListMap m;
        private final Object lo;
        private final Object hi;
        private final boolean loInclusive;
        private final boolean hiInclusive;
        private final boolean isDescending;
        private transient KeySet keySetView;
        private transient Set entrySetView;
        private transient Collection valuesView;
        
        SubMap(final ConcurrentSkipListMap map, final Object fromKey, final boolean fromInclusive, final Object toKey, final boolean toInclusive, final boolean isDescending) {
            if (fromKey != null && toKey != null && map.compare(fromKey, toKey) > 0) {
                throw new IllegalArgumentException("inconsistent range");
            }
            this.m = map;
            this.lo = fromKey;
            this.hi = toKey;
            this.loInclusive = fromInclusive;
            this.hiInclusive = toInclusive;
            this.isDescending = isDescending;
        }
        
        private boolean tooLow(final Object key) {
            if (this.lo != null) {
                final int c = this.m.compare(key, this.lo);
                if (c < 0 || (c == 0 && !this.loInclusive)) {
                    return true;
                }
            }
            return false;
        }
        
        private boolean tooHigh(final Object key) {
            if (this.hi != null) {
                final int c = this.m.compare(key, this.hi);
                if (c > 0 || (c == 0 && !this.hiInclusive)) {
                    return true;
                }
            }
            return false;
        }
        
        private boolean inBounds(final Object key) {
            return !this.tooLow(key) && !this.tooHigh(key);
        }
        
        private void checkKeyBounds(final Object key) throws IllegalArgumentException {
            if (key == null) {
                throw new NullPointerException();
            }
            if (!this.inBounds(key)) {
                throw new IllegalArgumentException("key out of range");
            }
        }
        
        private boolean isBeforeEnd(final Node n) {
            if (n == null) {
                return false;
            }
            if (this.hi == null) {
                return true;
            }
            final Object k = n.key;
            if (k == null) {
                return true;
            }
            final int c = this.m.compare(k, this.hi);
            return c <= 0 && (c != 0 || this.hiInclusive);
        }
        
        private Node loNode() {
            if (this.lo == null) {
                return this.m.findFirst();
            }
            if (this.loInclusive) {
                return this.m.findNear(this.lo, (false | true) ? 1 : 0);
            }
            return this.m.findNear(this.lo, 0);
        }
        
        private Node hiNode() {
            if (this.hi == null) {
                return this.m.findLast();
            }
            if (this.hiInclusive) {
                return this.m.findNear(this.hi, 0x2 | 0x1);
            }
            return this.m.findNear(this.hi, 2);
        }
        
        private Object lowestKey() {
            final Node n = this.loNode();
            if (this.isBeforeEnd(n)) {
                return n.key;
            }
            throw new NoSuchElementException();
        }
        
        private Object highestKey() {
            final Node n = this.hiNode();
            if (n != null) {
                final Object last = n.key;
                if (this.inBounds(last)) {
                    return last;
                }
            }
            throw new NoSuchElementException();
        }
        
        private Map.Entry lowestEntry() {
            while (true) {
                final Node n = this.loNode();
                if (!this.isBeforeEnd(n)) {
                    return null;
                }
                final Map.Entry e = n.createSnapshot();
                if (e != null) {
                    return e;
                }
            }
        }
        
        private Map.Entry highestEntry() {
            while (true) {
                final Node n = this.hiNode();
                if (n == null || !this.inBounds(n.key)) {
                    return null;
                }
                final Map.Entry e = n.createSnapshot();
                if (e != null) {
                    return e;
                }
            }
        }
        
        private Map.Entry removeLowest() {
            while (true) {
                final Node n = this.loNode();
                if (n == null) {
                    return null;
                }
                final Object k = n.key;
                if (!this.inBounds(k)) {
                    return null;
                }
                final Object v = this.m.doRemove(k, null);
                if (v != null) {
                    return new SimpleImmutableEntry(k, v);
                }
            }
        }
        
        private Map.Entry removeHighest() {
            while (true) {
                final Node n = this.hiNode();
                if (n == null) {
                    return null;
                }
                final Object k = n.key;
                if (!this.inBounds(k)) {
                    return null;
                }
                final Object v = this.m.doRemove(k, null);
                if (v != null) {
                    return new SimpleImmutableEntry(k, v);
                }
            }
        }
        
        private Map.Entry getNearEntry(final Object key, int rel) {
            if (this.isDescending) {
                if ((rel & 0x2) == 0x0) {
                    rel |= 0x2;
                }
                else {
                    rel &= ~0x2;
                }
            }
            if (this.tooLow(key)) {
                return ((rel & 0x2) != 0x0) ? null : this.lowestEntry();
            }
            if (this.tooHigh(key)) {
                return ((rel & 0x2) != 0x0) ? this.highestEntry() : null;
            }
            while (true) {
                final Node n = this.m.findNear(key, rel);
                if (n == null || !this.inBounds(n.key)) {
                    return null;
                }
                final Object k = n.key;
                final Object v = n.getValidValue();
                if (v != null) {
                    return new SimpleImmutableEntry(k, v);
                }
            }
        }
        
        private Object getNearKey(final Object key, int rel) {
            if (this.isDescending) {
                if ((rel & 0x2) == 0x0) {
                    rel |= 0x2;
                }
                else {
                    rel &= ~0x2;
                }
            }
            if (this.tooLow(key)) {
                if ((rel & 0x2) == 0x0) {
                    final Node n = this.loNode();
                    if (this.isBeforeEnd(n)) {
                        return n.key;
                    }
                }
                return null;
            }
            if (this.tooHigh(key)) {
                if ((rel & 0x2) != 0x0) {
                    final Node n = this.hiNode();
                    if (n != null) {
                        final Object last = n.key;
                        if (this.inBounds(last)) {
                            return last;
                        }
                    }
                }
                return null;
            }
            while (true) {
                final Node n = this.m.findNear(key, rel);
                if (n == null || !this.inBounds(n.key)) {
                    return null;
                }
                final Object k = n.key;
                final Object v = n.getValidValue();
                if (v != null) {
                    return k;
                }
            }
        }
        
        public boolean containsKey(final Object key) {
            if (key == null) {
                throw new NullPointerException();
            }
            final Object k = key;
            return this.inBounds(k) && this.m.containsKey(k);
        }
        
        public Object get(final Object key) {
            if (key == null) {
                throw new NullPointerException();
            }
            final Object k = key;
            return this.inBounds(k) ? this.m.get(k) : null;
        }
        
        public Object put(final Object key, final Object value) {
            this.checkKeyBounds(key);
            return this.m.put(key, value);
        }
        
        public Object remove(final Object key) {
            final Object k = key;
            return this.inBounds(k) ? this.m.remove(k) : null;
        }
        
        public int size() {
            long count = 0L;
            for (Node n = this.loNode(); this.isBeforeEnd(n); n = n.next) {
                if (n.getValidValue() != null) {
                    ++count;
                }
            }
            return (count >= 2147483647L) ? Integer.MAX_VALUE : ((int)count);
        }
        
        public boolean isEmpty() {
            return !this.isBeforeEnd(this.loNode());
        }
        
        public boolean containsValue(final Object value) {
            if (value == null) {
                throw new NullPointerException();
            }
            for (Node n = this.loNode(); this.isBeforeEnd(n); n = n.next) {
                final Object v = n.getValidValue();
                if (v != null && value.equals(v)) {
                    return true;
                }
            }
            return false;
        }
        
        public void clear() {
            for (Node n = this.loNode(); this.isBeforeEnd(n); n = n.next) {
                if (n.getValidValue() != null) {
                    this.m.remove(n.key);
                }
            }
        }
        
        public Object putIfAbsent(final Object key, final Object value) {
            this.checkKeyBounds(key);
            return this.m.putIfAbsent(key, value);
        }
        
        public boolean remove(final Object key, final Object value) {
            final Object k = key;
            return this.inBounds(k) && this.m.remove(k, value);
        }
        
        public boolean replace(final Object key, final Object oldValue, final Object newValue) {
            this.checkKeyBounds(key);
            return this.m.replace(key, oldValue, newValue);
        }
        
        public Object replace(final Object key, final Object value) {
            this.checkKeyBounds(key);
            return this.m.replace(key, value);
        }
        
        public Comparator comparator() {
            final Comparator cmp = this.m.comparator();
            if (this.isDescending) {
                return Collections.reverseOrder(cmp);
            }
            return cmp;
        }
        
        private SubMap newSubMap(Object fromKey, boolean fromInclusive, Object toKey, boolean toInclusive) {
            if (this.isDescending) {
                final Object tk = fromKey;
                fromKey = toKey;
                toKey = tk;
                final boolean ti = fromInclusive;
                fromInclusive = toInclusive;
                toInclusive = ti;
            }
            if (this.lo != null) {
                if (fromKey == null) {
                    fromKey = this.lo;
                    fromInclusive = this.loInclusive;
                }
                else {
                    final int c = this.m.compare(fromKey, this.lo);
                    if (c < 0 || (c == 0 && !this.loInclusive && fromInclusive)) {
                        throw new IllegalArgumentException("key out of range");
                    }
                }
            }
            if (this.hi != null) {
                if (toKey == null) {
                    toKey = this.hi;
                    toInclusive = this.hiInclusive;
                }
                else {
                    final int c = this.m.compare(toKey, this.hi);
                    if (c > 0 || (c == 0 && !this.hiInclusive && toInclusive)) {
                        throw new IllegalArgumentException("key out of range");
                    }
                }
            }
            return new SubMap(this.m, fromKey, fromInclusive, toKey, toInclusive, this.isDescending);
        }
        
        public NavigableMap subMap(final Object fromKey, final boolean fromInclusive, final Object toKey, final boolean toInclusive) {
            if (fromKey == null || toKey == null) {
                throw new NullPointerException();
            }
            return this.newSubMap(fromKey, fromInclusive, toKey, toInclusive);
        }
        
        public NavigableMap headMap(final Object toKey, final boolean inclusive) {
            if (toKey == null) {
                throw new NullPointerException();
            }
            return this.newSubMap(null, false, toKey, inclusive);
        }
        
        public NavigableMap tailMap(final Object fromKey, final boolean inclusive) {
            if (fromKey == null) {
                throw new NullPointerException();
            }
            return this.newSubMap(fromKey, inclusive, null, false);
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
        
        public NavigableMap descendingMap() {
            return new SubMap(this.m, this.lo, this.loInclusive, this.hi, this.hiInclusive, !this.isDescending);
        }
        
        public Map.Entry ceilingEntry(final Object key) {
            return this.getNearEntry(key, (false | true) ? 1 : 0);
        }
        
        public Object ceilingKey(final Object key) {
            return this.getNearKey(key, (false | true) ? 1 : 0);
        }
        
        public Map.Entry lowerEntry(final Object key) {
            return this.getNearEntry(key, 2);
        }
        
        public Object lowerKey(final Object key) {
            return this.getNearKey(key, 2);
        }
        
        public Map.Entry floorEntry(final Object key) {
            return this.getNearEntry(key, 0x2 | 0x1);
        }
        
        public Object floorKey(final Object key) {
            return this.getNearKey(key, 0x2 | 0x1);
        }
        
        public Map.Entry higherEntry(final Object key) {
            return this.getNearEntry(key, 0);
        }
        
        public Object higherKey(final Object key) {
            return this.getNearKey(key, 0);
        }
        
        public Object firstKey() {
            return this.isDescending ? this.highestKey() : this.lowestKey();
        }
        
        public Object lastKey() {
            return this.isDescending ? this.lowestKey() : this.highestKey();
        }
        
        public Map.Entry firstEntry() {
            return this.isDescending ? this.highestEntry() : this.lowestEntry();
        }
        
        public Map.Entry lastEntry() {
            return this.isDescending ? this.lowestEntry() : this.highestEntry();
        }
        
        public Map.Entry pollFirstEntry() {
            return this.isDescending ? this.removeHighest() : this.removeLowest();
        }
        
        public Map.Entry pollLastEntry() {
            return this.isDescending ? this.removeLowest() : this.removeHighest();
        }
        
        public Set keySet() {
            final KeySet ks = this.keySetView;
            return (ks != null) ? ks : (this.keySetView = new KeySet(this));
        }
        
        public NavigableSet navigableKeySet() {
            final KeySet ks = this.keySetView;
            return (ks != null) ? ks : (this.keySetView = new KeySet(this));
        }
        
        public Collection values() {
            final Collection vs = this.valuesView;
            return (vs != null) ? vs : (this.valuesView = new Values(this));
        }
        
        public Set entrySet() {
            final Set es = this.entrySetView;
            return (es != null) ? es : (this.entrySetView = new EntrySet(this));
        }
        
        public NavigableSet descendingKeySet() {
            return this.descendingMap().navigableKeySet();
        }
        
        Iterator keyIterator() {
            return new SubMapKeyIterator();
        }
        
        Iterator valueIterator() {
            return new SubMapValueIterator();
        }
        
        Iterator entryIterator() {
            return new SubMapEntryIterator();
        }
        
        abstract class SubMapIter implements Iterator
        {
            Node lastReturned;
            Node next;
            Object nextValue;
            
            SubMapIter() {
                while (true) {
                    this.next = (SubMap.this.isDescending ? SubMap.this.hiNode() : SubMap.this.loNode());
                    if (this.next == null) {
                        break;
                    }
                    final Object x = this.next.value;
                    if (x == null || x == this.next) {
                        continue;
                    }
                    if (!SubMap.this.inBounds(this.next.key)) {
                        this.next = null;
                        break;
                    }
                    this.nextValue = x;
                    break;
                }
            }
            
            public final boolean hasNext() {
                return this.next != null;
            }
            
            final void advance() {
                if (this.next == null) {
                    throw new NoSuchElementException();
                }
                this.lastReturned = this.next;
                if (SubMap.this.isDescending) {
                    this.descend();
                }
                else {
                    this.ascend();
                }
            }
            
            private void ascend() {
                while (true) {
                    this.next = this.next.next;
                    if (this.next == null) {
                        break;
                    }
                    final Object x = this.next.value;
                    if (x == null || x == this.next) {
                        continue;
                    }
                    if (SubMap.this.tooHigh(this.next.key)) {
                        this.next = null;
                        break;
                    }
                    this.nextValue = x;
                    break;
                }
            }
            
            private void descend() {
                while (true) {
                    this.next = SubMap.this.m.findNear(this.lastReturned.key, 2);
                    if (this.next == null) {
                        break;
                    }
                    final Object x = this.next.value;
                    if (x == null || x == this.next) {
                        continue;
                    }
                    if (SubMap.this.tooLow(this.next.key)) {
                        this.next = null;
                        break;
                    }
                    this.nextValue = x;
                    break;
                }
            }
            
            public void remove() {
                final Node l = this.lastReturned;
                if (l == null) {
                    throw new IllegalStateException();
                }
                SubMap.this.m.remove(l.key);
                this.lastReturned = null;
            }
        }
        
        final class SubMapValueIterator extends SubMapIter
        {
            public Object next() {
                final Object v = this.nextValue;
                this.advance();
                return v;
            }
        }
        
        final class SubMapKeyIterator extends SubMapIter
        {
            public Object next() {
                final Node n = this.next;
                this.advance();
                return n.key;
            }
        }
        
        final class SubMapEntryIterator extends SubMapIter
        {
            public Object next() {
                final Node n = this.next;
                final Object v = this.nextValue;
                this.advance();
                return new SimpleImmutableEntry(n.key, v);
            }
        }
    }
}
