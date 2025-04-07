// 
// Decompiled by Procyon v0.5.36
// 

package edu.emory.mathcs.backport.java.util;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.util.ConcurrentModificationException;
import java.util.ListIterator;
import java.lang.reflect.Array;
import java.io.Serializable;
import java.util.Iterator;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.SortedMap;
import java.util.Map;
import java.util.SortedSet;
import java.util.Set;
import java.util.Collection;
import java.util.Random;
import java.util.Comparator;
import java.util.List;

public class Collections
{
    private Collections() {
    }
    
    public static void sort(final List list) {
        java.util.Collections.sort((List<Comparable>)list);
    }
    
    public static void sort(final List list, final Comparator c) {
        java.util.Collections.sort((List<Object>)list, c);
    }
    
    public static int binarySearch(final List list, final Object key) {
        return java.util.Collections.binarySearch(list, key);
    }
    
    public static int binarySearch(final List list, final Object key, final Comparator c) {
        return java.util.Collections.binarySearch(list, key, c);
    }
    
    public static void reverse(final List list) {
        java.util.Collections.reverse(list);
    }
    
    public static void shuffle(final List list) {
        java.util.Collections.shuffle(list);
    }
    
    public static void shuffle(final List list, final Random rnd) {
        java.util.Collections.shuffle(list, rnd);
    }
    
    public static void swap(final List list, final int i, final int j) {
        java.util.Collections.swap(list, i, i);
    }
    
    public static void fill(final List list, final Object obj) {
        java.util.Collections.fill(list, obj);
    }
    
    public static void copy(final List dest, final List src) {
        java.util.Collections.copy((List<? super Object>)dest, (List<?>)src);
    }
    
    public static Object min(final Collection coll) {
        return java.util.Collections.min((Collection<?>)coll);
    }
    
    public static Object min(final Collection coll, final Comparator comp) {
        return java.util.Collections.min((Collection<?>)coll, (Comparator<? super Object>)comp);
    }
    
    public static Object max(final Collection coll) {
        return java.util.Collections.max((Collection<?>)coll);
    }
    
    public static Object max(final Collection coll, final Comparator comp) {
        return java.util.Collections.max((Collection<?>)coll, (Comparator<? super Object>)comp);
    }
    
    public static void rotate(final List list, final int distance) {
        java.util.Collections.rotate(list, distance);
    }
    
    public static boolean replaceAll(final List list, final Object oldVal, final Object newVal) {
        return java.util.Collections.replaceAll(list, oldVal, newVal);
    }
    
    public static int indexOfSubList(final List source, final List target) {
        return java.util.Collections.indexOfSubList(source, target);
    }
    
    public static int lastIndexOfSubList(final List source, final List target) {
        return java.util.Collections.lastIndexOfSubList(source, target);
    }
    
    public static Collection unmodifiableCollection(final Collection c) {
        return java.util.Collections.unmodifiableCollection((Collection<?>)c);
    }
    
    public static Set unmodifiableSet(final Set s) {
        return java.util.Collections.unmodifiableSet((Set<?>)s);
    }
    
    public static SortedSet unmodifiableSortedSet(final SortedSet s) {
        return java.util.Collections.unmodifiableSortedSet((SortedSet<Object>)s);
    }
    
    public static List unmodifiableList(final List l) {
        return java.util.Collections.unmodifiableList((List<?>)l);
    }
    
    public static Map unmodifiableMap(final Map m) {
        return java.util.Collections.unmodifiableMap((Map<?, ?>)m);
    }
    
    public static SortedMap unmodifiableSortedMap(final SortedMap m) {
        return java.util.Collections.unmodifiableSortedMap((SortedMap<Object, ?>)m);
    }
    
    public static Collection synchronizedCollection(final Collection c) {
        return java.util.Collections.synchronizedCollection((Collection<Object>)c);
    }
    
    public static Set synchronizedSet(final Set s) {
        return java.util.Collections.synchronizedSet((Set<Object>)s);
    }
    
    public static SortedSet synchronizedSortedSet(final SortedSet s) {
        return java.util.Collections.synchronizedSortedSet((SortedSet<Object>)s);
    }
    
    public static List synchronizedList(final List l) {
        return java.util.Collections.synchronizedList((List<Object>)l);
    }
    
    public static Map synchronizedMap(final Map m) {
        return java.util.Collections.synchronizedMap((Map<Object, Object>)m);
    }
    
    public static SortedMap synchronizedSortedMap(final SortedMap m) {
        return java.util.Collections.synchronizedSortedMap((SortedMap<Object, Object>)m);
    }
    
    public static Collection checkedCollection(final Collection c, final Class type) {
        return new CheckedCollection(c, type);
    }
    
    public static Set checkedSet(final Set s, final Class type) {
        return new CheckedSet(s, type);
    }
    
    public static SortedSet checkedSortedSet(final SortedSet s, final Class type) {
        return new CheckedSortedSet(s, type);
    }
    
    public static List checkedList(final List l, final Class type) {
        return new CheckedList(l, type);
    }
    
    public static Map checkedMap(final Map m, final Class keyType, final Class valueType) {
        return new CheckedMap(m, keyType, valueType);
    }
    
    public static SortedMap checkedSortedMap(final SortedMap m, final Class keyType, final Class valueType) {
        return new CheckedSortedMap(m, keyType, valueType);
    }
    
    public static Set emptySet() {
        return java.util.Collections.EMPTY_SET;
    }
    
    public static List emptyList() {
        return java.util.Collections.EMPTY_LIST;
    }
    
    public static Map emptyMap() {
        return java.util.Collections.EMPTY_MAP;
    }
    
    public static Set singleton(final Object o) {
        return java.util.Collections.singleton(o);
    }
    
    public static List singletonList(final Object o) {
        return java.util.Collections.singletonList(o);
    }
    
    public static Map singletonMap(final Object key, final Object value) {
        return java.util.Collections.singletonMap(key, value);
    }
    
    public static List nCopies(final int n, final Object o) {
        return java.util.Collections.nCopies(n, o);
    }
    
    public static Comparator reverseOrder() {
        return java.util.Collections.reverseOrder();
    }
    
    public static Comparator reverseOrder(final Comparator cmp) {
        return (cmp instanceof ReverseComparator) ? ((ReverseComparator)cmp).cmp : ((cmp == null) ? reverseOrder() : new ReverseComparator(cmp));
    }
    
    public static Enumeration enumeration(final Collection c) {
        return java.util.Collections.enumeration((Collection<Object>)c);
    }
    
    public static ArrayList list(final Enumeration e) {
        return java.util.Collections.list((Enumeration<Object>)e);
    }
    
    public static int frequency(final Collection c, final Object o) {
        int freq = 0;
        if (o == null) {
            final Iterator itr = c.iterator();
            while (itr.hasNext()) {
                if (itr.next() == null) {
                    ++freq;
                }
            }
        }
        else {
            final Iterator itr = c.iterator();
            while (itr.hasNext()) {
                if (o.equals(itr.next())) {
                    ++freq;
                }
            }
        }
        return freq;
    }
    
    public static boolean disjoint(Collection a, Collection b) {
        if (a instanceof Set && (!(b instanceof Set) || a.size() < b.size())) {
            final Collection tmp = a;
            a = b;
            b = tmp;
        }
        final Iterator itr = a.iterator();
        while (itr.hasNext()) {
            if (b.contains(itr.next())) {
                return false;
            }
        }
        return true;
    }
    
    public static boolean addAll(final Collection c, final Object[] a) {
        boolean modified = false;
        for (int i = 0; i < a.length; ++i) {
            modified |= c.add(a[i]);
        }
        return modified;
    }
    
    public static Set newSetFromMap(final Map map) {
        return new SetFromMap(map);
    }
    
    public static Queue asLifoQueue(final Deque deque) {
        return new AsLifoQueue(deque);
    }
    
    private static boolean eq(final Object o1, final Object o2) {
        return (o1 == null) ? (o2 == null) : o1.equals(o2);
    }
    
    private static class CheckedCollection implements Collection, Serializable
    {
        final Collection coll;
        final Class type;
        transient Object[] emptyArr;
        
        CheckedCollection(final Collection coll, final Class type) {
            if (coll == null || type == null) {
                throw new NullPointerException();
            }
            this.coll = coll;
            this.type = type;
        }
        
        void typeCheck(final Object obj) {
            if (!this.type.isInstance(obj)) {
                throw new ClassCastException("Attempted to insert an element of type " + obj.getClass().getName() + " to a collection of type " + this.type.getName());
            }
        }
        
        public int size() {
            return this.coll.size();
        }
        
        public void clear() {
            this.coll.clear();
        }
        
        public boolean isEmpty() {
            return this.coll.isEmpty();
        }
        
        public Object[] toArray() {
            return this.coll.toArray();
        }
        
        public Object[] toArray(final Object[] a) {
            return this.coll.toArray(a);
        }
        
        public boolean contains(final Object o) {
            return this.coll.contains(o);
        }
        
        public boolean remove(final Object o) {
            return this.coll.remove(o);
        }
        
        public boolean containsAll(final Collection c) {
            return this.coll.containsAll(c);
        }
        
        public boolean removeAll(final Collection c) {
            return this.coll.removeAll(c);
        }
        
        public boolean retainAll(final Collection c) {
            return this.coll.retainAll(c);
        }
        
        public String toString() {
            return this.coll.toString();
        }
        
        public boolean add(final Object o) {
            this.typeCheck(o);
            return this.coll.add(o);
        }
        
        public boolean addAll(final Collection c) {
            Object[] checked;
            try {
                checked = c.toArray(this.getEmptyArr());
            }
            catch (ArrayStoreException e) {
                throw new ClassCastException("Attempted to insert an element of invalid type  to a collection of type " + this.type.getName());
            }
            return this.coll.addAll(Arrays.asList(checked));
        }
        
        public Iterator iterator() {
            return new Itr(this.coll.iterator());
        }
        
        protected Object[] getEmptyArr() {
            if (this.emptyArr == null) {
                this.emptyArr = (Object[])Array.newInstance(this.type, 0);
            }
            return this.emptyArr;
        }
        
        class Itr implements Iterator
        {
            final Iterator itr;
            
            Itr(final Iterator itr) {
                this.itr = itr;
            }
            
            public boolean hasNext() {
                return this.itr.hasNext();
            }
            
            public Object next() {
                return this.itr.next();
            }
            
            public void remove() {
                this.itr.remove();
            }
        }
    }
    
    private static class CheckedList extends CheckedCollection implements List, Serializable
    {
        final List list;
        
        CheckedList(final List list, final Class type) {
            super(list, type);
            this.list = list;
        }
        
        public Object get(final int index) {
            return this.list.get(index);
        }
        
        public Object remove(final int index) {
            return this.list.remove(index);
        }
        
        public int indexOf(final Object o) {
            return this.list.indexOf(o);
        }
        
        public int lastIndexOf(final Object o) {
            return this.list.lastIndexOf(o);
        }
        
        public int hashCode() {
            return this.list.hashCode();
        }
        
        public boolean equals(final Object o) {
            return o == this || this.list.equals(o);
        }
        
        public Object set(final int index, final Object element) {
            this.typeCheck(element);
            return this.list.set(index, element);
        }
        
        public void add(final int index, final Object element) {
            this.typeCheck(element);
            this.list.add(index, element);
        }
        
        public boolean addAll(final int index, final Collection c) {
            Object[] checked;
            try {
                checked = c.toArray(this.getEmptyArr());
            }
            catch (ArrayStoreException e) {
                throw new ClassCastException("Attempted to insert an element of invalid type  to a list of type " + this.type.getName());
            }
            return this.list.addAll(index, Arrays.asList(checked));
        }
        
        public List subList(final int fromIndex, final int toIndex) {
            return new CheckedList(this.list.subList(fromIndex, toIndex), this.type);
        }
        
        public ListIterator listIterator() {
            return new ListItr(this.list.listIterator());
        }
        
        public ListIterator listIterator(final int index) {
            return new ListItr(this.list.listIterator(index));
        }
        
        private class ListItr implements ListIterator
        {
            final ListIterator itr;
            
            ListItr(final ListIterator itr) {
                this.itr = itr;
            }
            
            public boolean hasNext() {
                return this.itr.hasNext();
            }
            
            public boolean hasPrevious() {
                return this.itr.hasPrevious();
            }
            
            public int nextIndex() {
                return this.itr.nextIndex();
            }
            
            public int previousIndex() {
                return this.itr.previousIndex();
            }
            
            public Object next() {
                return this.itr.next();
            }
            
            public Object previous() {
                return this.itr.previous();
            }
            
            public void remove() {
                this.itr.remove();
            }
            
            public void set(final Object element) {
                CheckedList.this.typeCheck(element);
                this.itr.set(element);
            }
            
            public void add(final Object element) {
                CheckedList.this.typeCheck(element);
                this.itr.add(element);
            }
        }
    }
    
    private static class CheckedSet extends CheckedCollection implements Set, Serializable
    {
        CheckedSet(final Set set, final Class type) {
            super(set, type);
        }
        
        public int hashCode() {
            return this.coll.hashCode();
        }
        
        public boolean equals(final Object o) {
            return o == this || this.coll.equals(o);
        }
    }
    
    private static class CheckedSortedSet extends CheckedSet implements SortedSet, Serializable
    {
        final SortedSet set;
        
        CheckedSortedSet(final SortedSet set, final Class type) {
            super(set, type);
            this.set = set;
        }
        
        public Object first() {
            return this.set.first();
        }
        
        public Object last() {
            return this.set.last();
        }
        
        public Comparator comparator() {
            return this.set.comparator();
        }
        
        public SortedSet headSet(final Object toElement) {
            return new CheckedSortedSet(this.set.headSet(toElement), this.type);
        }
        
        public SortedSet tailSet(final Object fromElement) {
            return new CheckedSortedSet(this.set.tailSet(fromElement), this.type);
        }
        
        public SortedSet subSet(final Object fromElement, final Object toElement) {
            return new CheckedSortedSet(this.set.subSet(fromElement, toElement), this.type);
        }
    }
    
    private static class CheckedMap implements Map, Serializable
    {
        final Map map;
        final Class keyType;
        final Class valueType;
        transient Set entrySet;
        private transient Object[] emptyKeyArray;
        private transient Object[] emptyValueArray;
        
        CheckedMap(final Map map, final Class keyType, final Class valueType) {
            if (map == null || keyType == null || valueType == null) {
                throw new NullPointerException();
            }
            this.map = map;
            this.keyType = keyType;
            this.valueType = valueType;
        }
        
        private void typeCheckKey(final Object key) {
            if (!this.keyType.isInstance(key)) {
                throw new ClassCastException("Attempted to use a key of type " + key.getClass().getName() + " with a map with keys of type " + this.keyType.getName());
            }
        }
        
        private void typeCheckValue(final Object value) {
            if (!this.valueType.isInstance(value)) {
                throw new ClassCastException("Attempted to use a value of type " + value.getClass().getName() + " with a map with values of type " + this.valueType.getName());
            }
        }
        
        public int hashCode() {
            return this.map.hashCode();
        }
        
        public boolean equals(final Object o) {
            return o == this || this.map.equals(o);
        }
        
        public int size() {
            return this.map.size();
        }
        
        public void clear() {
            this.map.clear();
        }
        
        public boolean isEmpty() {
            return this.map.isEmpty();
        }
        
        public boolean containsKey(final Object key) {
            return this.map.containsKey(key);
        }
        
        public boolean containsValue(final Object value) {
            return this.map.containsValue(value);
        }
        
        public Collection values() {
            return this.map.values();
        }
        
        public Set keySet() {
            return this.map.keySet();
        }
        
        public void putAll(final Map m) {
            if (this.emptyKeyArray == null) {
                this.emptyKeyArray = (Object[])Array.newInstance(this.keyType, 0);
            }
            if (this.emptyValueArray == null) {
                this.emptyValueArray = (Object[])Array.newInstance(this.valueType, 0);
            }
            Object[] keys;
            try {
                keys = m.keySet().toArray(this.emptyKeyArray);
            }
            catch (ArrayStoreException e) {
                throw new ClassCastException("Attempted to use an invalid key type  with a map with keys of type " + this.keyType.getName());
            }
            Object[] values;
            try {
                values = m.keySet().toArray(this.emptyKeyArray);
            }
            catch (ArrayStoreException e) {
                throw new ClassCastException("Attempted to use an invalid value type  with a map with values of type " + this.valueType.getName());
            }
            if (keys.length != values.length) {
                throw new ConcurrentModificationException();
            }
            for (int i = 0; i < keys.length; ++i) {
                this.map.put(keys[i], values[i]);
            }
        }
        
        public Set entrySet() {
            if (this.entrySet == null) {
                this.entrySet = new EntrySetView(this.map.entrySet());
            }
            return this.entrySet;
        }
        
        public Object get(final Object key) {
            return this.map.get(key);
        }
        
        public Object remove(final Object key) {
            return this.map.remove(key);
        }
        
        public Object put(final Object key, final Object value) {
            this.typeCheckKey(key);
            this.typeCheckValue(value);
            return this.map.put(key, value);
        }
        
        private class EntrySetView extends AbstractSet implements Set
        {
            final Set entrySet;
            private final /* synthetic */ CheckedMap this$0;
            
            EntrySetView(final Set entrySet) {
                this.entrySet = entrySet;
            }
            
            public int size() {
                return this.entrySet.size();
            }
            
            public boolean isEmpty() {
                return this.entrySet.isEmpty();
            }
            
            public boolean remove(final Object o) {
                return this.entrySet.remove(o);
            }
            
            public void clear() {
                this.entrySet.clear();
            }
            
            public boolean contains(final Object o) {
                return o instanceof Entry && this.entrySet.contains(new EntryView((Entry)o));
            }
            
            public Iterator iterator() {
                final Iterator itr = this.entrySet.iterator();
                return new Iterator() {
                    private final /* synthetic */ EntrySetView this$1;
                    
                    public boolean hasNext() {
                        return itr.hasNext();
                    }
                    
                    public Object next() {
                        return this.this$1.this$0.new EntryView(itr.next());
                    }
                    
                    public void remove() {
                        itr.remove();
                    }
                };
            }
            
            public Object[] toArray() {
                final Object[] a = this.entrySet.toArray();
                if (a.getClass().getComponentType().isAssignableFrom(EntryView.class)) {
                    for (int i = 0; i < a.length; ++i) {
                        a[i] = new EntryView((Entry)a[i]);
                    }
                    return a;
                }
                final Object[] newa = new Object[a.length];
                for (int j = 0; j < a.length; ++j) {
                    newa[j] = new EntryView((Entry)a[j]);
                }
                return newa;
            }
            
            public Object[] toArray(Object[] a) {
                Object[] base;
                if (a.length == 0) {
                    base = a;
                }
                else {
                    base = (Object[])Array.newInstance(a.getClass().getComponentType(), a.length);
                }
                base = this.entrySet.toArray(base);
                for (int i = 0; i < base.length; ++i) {
                    base[i] = new EntryView((Entry)base[i]);
                }
                if (base.length > a.length) {
                    a = base;
                }
                else {
                    System.arraycopy(base, 0, a, 0, base.length);
                    if (base.length < a.length) {
                        a[base.length] = null;
                    }
                }
                return a;
            }
        }
        
        private class EntryView implements Entry, Serializable
        {
            final Entry entry;
            
            EntryView(final Entry entry) {
                this.entry = entry;
            }
            
            public Object getKey() {
                return this.entry.getKey();
            }
            
            public Object getValue() {
                return this.entry.getValue();
            }
            
            public int hashCode() {
                return this.entry.hashCode();
            }
            
            public boolean equals(final Object o) {
                if (o == this) {
                    return true;
                }
                if (!(o instanceof Entry)) {
                    return false;
                }
                final Entry e = (Entry)o;
                return eq(this.getKey(), e.getKey()) && eq(this.getValue(), e.getValue());
            }
            
            public Object setValue(final Object val) {
                CheckedMap.this.typeCheckValue(val);
                return this.entry.setValue(val);
            }
        }
    }
    
    private static class CheckedSortedMap extends CheckedMap implements SortedMap, Serializable
    {
        final SortedMap map;
        
        CheckedSortedMap(final SortedMap map, final Class keyType, final Class valueType) {
            super(map, keyType, valueType);
            this.map = map;
        }
        
        public Comparator comparator() {
            return this.map.comparator();
        }
        
        public Object firstKey() {
            return this.map.firstKey();
        }
        
        public Object lastKey() {
            return this.map.lastKey();
        }
        
        public SortedMap subMap(final Object fromKey, final Object toKey) {
            return new CheckedSortedMap(this.map.subMap(fromKey, toKey), this.keyType, this.valueType);
        }
        
        public SortedMap headMap(final Object toKey) {
            return new CheckedSortedMap(this.map.headMap(toKey), this.keyType, this.valueType);
        }
        
        public SortedMap tailMap(final Object fromKey) {
            return new CheckedSortedMap(this.map.tailMap(fromKey), this.keyType, this.valueType);
        }
    }
    
    private static class SetFromMap extends AbstractSet implements Serializable
    {
        private static final Object PRESENT;
        final Map map;
        transient Set keySet;
        
        SetFromMap(final Map map) {
            this.map = map;
            this.keySet = map.keySet();
        }
        
        public int hashCode() {
            return this.keySet.hashCode();
        }
        
        public int size() {
            return this.map.size();
        }
        
        public void clear() {
            this.map.clear();
        }
        
        public boolean isEmpty() {
            return this.map.isEmpty();
        }
        
        public boolean add(final Object o) {
            return this.map.put(o, SetFromMap.PRESENT) == null;
        }
        
        public boolean contains(final Object o) {
            return this.map.containsKey(o);
        }
        
        public boolean equals(final Object o) {
            return o == this || this.keySet.equals(o);
        }
        
        public boolean remove(final Object o) {
            return this.map.remove(o) == SetFromMap.PRESENT;
        }
        
        public boolean removeAll(final Collection c) {
            return this.keySet.removeAll(c);
        }
        
        public boolean retainAll(final Collection c) {
            return this.keySet.retainAll(c);
        }
        
        public Iterator iterator() {
            return this.keySet.iterator();
        }
        
        public Object[] toArray() {
            return this.keySet.toArray();
        }
        
        public Object[] toArray(final Object[] a) {
            return this.keySet.toArray(a);
        }
        
        public boolean addAll(final Collection c) {
            boolean modified = false;
            final Iterator it = c.iterator();
            while (it.hasNext()) {
                modified |= (this.map.put(it.next(), SetFromMap.PRESENT) == null);
            }
            return modified;
        }
        
        private void readObject(final ObjectInputStream in) throws IOException, ClassNotFoundException {
            in.defaultReadObject();
            this.keySet = this.map.keySet();
        }
        
        static {
            PRESENT = Boolean.TRUE;
        }
    }
    
    private static class AsLifoQueue extends AbstractQueue implements Queue, Serializable
    {
        final Deque deque;
        
        AsLifoQueue(final Deque deque) {
            this.deque = deque;
        }
        
        public boolean add(final Object e) {
            return this.deque.offerFirst(e);
        }
        
        public boolean offer(final Object e) {
            return this.deque.offerFirst(e);
        }
        
        public Object remove() {
            return this.deque.removeFirst();
        }
        
        public Object poll() {
            return this.deque.pollFirst();
        }
        
        public Object element() {
            return this.deque.getFirst();
        }
        
        public Object peek() {
            return this.deque.peekFirst();
        }
        
        public int size() {
            return this.deque.size();
        }
        
        public void clear() {
            this.deque.clear();
        }
        
        public boolean isEmpty() {
            return this.deque.isEmpty();
        }
        
        public Object[] toArray() {
            return this.deque.toArray();
        }
        
        public Object[] toArray(final Object[] a) {
            return this.deque.toArray(a);
        }
        
        public boolean contains(final Object o) {
            return this.deque.contains(o);
        }
        
        public boolean remove(final Object o) {
            return this.deque.remove(o);
        }
        
        public Iterator iterator() {
            return this.deque.iterator();
        }
        
        public String toString() {
            return this.deque.toString();
        }
        
        public boolean containsAll(final Collection c) {
            return this.deque.containsAll(c);
        }
        
        public boolean removeAll(final Collection c) {
            return this.deque.removeAll(c);
        }
        
        public boolean retainAll(final Collection c) {
            return this.deque.retainAll(c);
        }
    }
    
    private static class ReverseComparator implements Comparator, Serializable
    {
        final Comparator cmp;
        
        ReverseComparator(final Comparator cmp) {
            this.cmp = cmp;
        }
        
        public int compare(final Object o1, final Object o2) {
            return this.cmp.compare(o2, o1);
        }
        
        public boolean equals(final Object o) {
            return o == this || (o instanceof ReverseComparator && this.cmp.equals(((ReverseComparator)o).cmp));
        }
        
        public int hashCode() {
            return this.cmp.hashCode() ^ 0x10000000;
        }
    }
}
