// 
// Decompiled by Procyon v0.5.36
// 

package edu.emory.mathcs.backport.java.util;

import java.util.ConcurrentModificationException;
import java.io.ObjectInputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.util.ListIterator;
import java.util.Iterator;
import java.util.NoSuchElementException;
import java.lang.reflect.Array;
import java.util.Collection;
import java.io.Serializable;
import java.util.List;
import java.util.AbstractSequentialList;

public class LinkedList extends AbstractSequentialList implements List, Deque, Cloneable, Serializable
{
    private static final long serialVersionUID = 876323262645176354L;
    private transient int size;
    private transient int modCount;
    private transient Entry head;
    
    public LinkedList() {
        this.size = 0;
        final Entry entry3;
        final Entry entry2;
        final Entry entry;
        final Entry sentinel = entry = (entry2 = (entry3 = new Entry(null)));
        entry2.prev = entry;
        entry3.next = entry;
        this.head = sentinel;
    }
    
    public LinkedList(final Collection c) {
        this();
        this.addAll(c);
    }
    
    public int size() {
        return this.size;
    }
    
    public boolean isEmpty() {
        return this.size == 0;
    }
    
    public boolean contains(final Object o) {
        return this.findFirst(o) != null;
    }
    
    private Entry getAt(int idx) {
        final int size = this.size;
        if (idx < 0 || idx >= size) {
            throw new ArrayIndexOutOfBoundsException("Index: " + idx + "; Size: " + size);
        }
        if (idx < size >> 1) {
            Entry e = this.head.next;
            while (idx > 0) {
                e = e.next;
                --idx;
            }
            return e;
        }
        idx = size - idx - 1;
        Entry e = this.head.prev;
        while (idx > 0) {
            e = e.prev;
            --idx;
        }
        return e;
    }
    
    private Entry findFirst(final Object o) {
        if (o == null) {
            for (Entry e = this.head.next; e != this.head; e = e.next) {
                if (e.val == null) {
                    return e;
                }
            }
        }
        else {
            for (Entry e = this.head.next; e != this.head; e = e.next) {
                if (o.equals(e.val)) {
                    return e;
                }
            }
        }
        return null;
    }
    
    private Entry findLast(final Object o) {
        if (o == null) {
            for (Entry e = this.head.prev; e != this.head; e = e.prev) {
                if (e.val == null) {
                    return e;
                }
            }
        }
        else {
            for (Entry e = this.head.prev; e != this.head; e = e.prev) {
                if (o.equals(e.val)) {
                    return e;
                }
            }
        }
        return null;
    }
    
    public int indexOf(final Object o) {
        int idx = 0;
        if (o == null) {
            for (Entry e = this.head.next; e != this.head; e = e.next, ++idx) {
                if (e.val == null) {
                    return idx;
                }
            }
        }
        else {
            for (Entry e = this.head.next; e != this.head; e = e.next, ++idx) {
                if (o.equals(e.val)) {
                    return idx;
                }
            }
        }
        return -1;
    }
    
    public int lastIndexOf(final Object o) {
        int idx = this.size - 1;
        if (o == null) {
            for (Entry e = this.head.prev; e != this.head; e = e.prev, --idx) {
                if (e.val == null) {
                    return idx;
                }
            }
        }
        else {
            for (Entry e = this.head.prev; e != this.head; e = e.prev, --idx) {
                if (o.equals(e.val)) {
                    return idx;
                }
            }
        }
        return -1;
    }
    
    public Object[] toArray() {
        final Object[] a = new Object[this.size];
        int i = 0;
        for (Entry e = this.head.next; e != this.head; e = e.next) {
            a[i++] = e.val;
        }
        return a;
    }
    
    public Object[] toArray(Object[] a) {
        final int size = this.size;
        if (a.length < size) {
            a = (Object[])Array.newInstance(a.getClass().getComponentType(), size);
        }
        int i = 0;
        for (Entry e = this.head.next; e != this.head; e = e.next) {
            a[i++] = e.val;
        }
        if (i < a.length) {
            a[i++] = null;
        }
        return a;
    }
    
    public boolean add(final Object o) {
        this.insertBefore(this.head, o);
        return true;
    }
    
    private void insertAfter(final Entry e, final Object val) {
        ++this.modCount;
        final Entry succ = e.next;
        final Entry newe = new Entry(val);
        newe.prev = e;
        newe.next = succ;
        e.next = newe;
        succ.prev = newe;
        ++this.size;
    }
    
    private void insertBefore(final Entry e, final Object val) {
        ++this.modCount;
        final Entry pred = e.prev;
        final Entry newe = new Entry(val);
        newe.prev = pred;
        newe.next = e;
        pred.next = newe;
        e.prev = newe;
        ++this.size;
    }
    
    private Object remove(final Entry e) {
        if (e == this.head) {
            throw new NoSuchElementException();
        }
        ++this.modCount;
        final Entry succ = e.next;
        final Entry pred = e.prev;
        pred.next = succ;
        succ.prev = pred;
        --this.size;
        return e.val;
    }
    
    public boolean remove(final Object o) {
        final Entry e = this.findFirst(o);
        if (e == null) {
            return false;
        }
        this.remove(e);
        return true;
    }
    
    public boolean addAll(final Collection c) {
        return this.insertAllBefore(this.head, c);
    }
    
    public boolean addAll(final int index, final Collection c) {
        return this.insertAllBefore((index == this.size) ? this.head : this.getAt(index), c);
    }
    
    private boolean insertAllBefore(final Entry succ, final Collection c) {
        final Iterator itr = c.iterator();
        if (!itr.hasNext()) {
            return false;
        }
        ++this.modCount;
        Entry prev;
        Entry curr;
        final Entry first = curr = (prev = new Entry(itr.next()));
        int added = 1;
        while (itr.hasNext()) {
            curr = new Entry(itr.next());
            prev.next = curr;
            curr.prev = prev;
            prev = curr;
            ++added;
        }
        final Entry pred = succ.prev;
        first.prev = pred;
        curr.next = succ;
        pred.next = first;
        succ.prev = curr;
        this.size += added;
        return true;
    }
    
    public void clear() {
        ++this.modCount;
        final Entry head = this.head;
        final Entry head2 = this.head;
        final Entry head3 = this.head;
        head2.prev = head3;
        head.next = head3;
        this.size = 0;
    }
    
    public Object get(final int index) {
        return this.getAt(index).val;
    }
    
    public Object set(final int index, final Object element) {
        final Entry e = this.getAt(index);
        final Object old = e.val;
        e.val = element;
        return old;
    }
    
    public void add(final int index, final Object element) {
        if (index == this.size) {
            this.insertBefore(this.head, element);
        }
        else {
            this.insertBefore((index == this.size) ? this.head : this.getAt(index), element);
        }
    }
    
    public Object remove(final int index) {
        return this.remove(this.getAt(index));
    }
    
    public ListIterator listIterator() {
        return new Itr();
    }
    
    public ListIterator listIterator(final int index) {
        return new Itr((index == this.size) ? this.head : this.getAt(index), index);
    }
    
    public void addFirst(final Object e) {
        this.insertAfter(this.head, e);
    }
    
    public void addLast(final Object e) {
        this.insertBefore(this.head, e);
    }
    
    public boolean offerFirst(final Object e) {
        this.insertAfter(this.head, e);
        return true;
    }
    
    public boolean offerLast(final Object e) {
        this.insertBefore(this.head, e);
        return true;
    }
    
    public Object removeFirst() {
        return this.remove(this.head.next);
    }
    
    public Object removeLast() {
        return this.remove(this.head.prev);
    }
    
    public Object pollFirst() {
        return (this.size == 0) ? null : this.remove(this.head.next);
    }
    
    public Object pollLast() {
        return (this.size == 0) ? null : this.remove(this.head.prev);
    }
    
    public Object getFirst() {
        if (this.size == 0) {
            throw new NoSuchElementException();
        }
        return this.head.next.val;
    }
    
    public Object getLast() {
        if (this.size == 0) {
            throw new NoSuchElementException();
        }
        return this.head.prev.val;
    }
    
    public Object peekFirst() {
        return (this.size == 0) ? null : this.head.next.val;
    }
    
    public Object peekLast() {
        return (this.size == 0) ? null : this.head.prev.val;
    }
    
    public boolean removeFirstOccurrence(final Object o) {
        final Entry e = this.findFirst(o);
        if (e == null) {
            return false;
        }
        this.remove(e);
        return true;
    }
    
    public boolean removeLastOccurrence(final Object o) {
        final Entry e = this.findLast(o);
        if (e == null) {
            return false;
        }
        this.remove(e);
        return true;
    }
    
    public boolean offer(final Object e) {
        return this.add(e);
    }
    
    public Object remove() {
        return this.removeFirst();
    }
    
    public Object poll() {
        return this.pollFirst();
    }
    
    public Object element() {
        return this.getFirst();
    }
    
    public Object peek() {
        return this.peekFirst();
    }
    
    public void push(final Object e) {
        this.addFirst(e);
    }
    
    public Object pop() {
        return this.removeFirst();
    }
    
    public Iterator descendingIterator() {
        return new DescItr();
    }
    
    private void writeObject(final ObjectOutputStream out) throws IOException {
        out.defaultWriteObject();
        out.writeInt(this.size);
        for (Entry e = this.head.next; e != this.head; e = e.next) {
            out.writeObject(e.val);
        }
    }
    
    private void readObject(final ObjectInputStream in) throws IOException, ClassNotFoundException {
        in.defaultReadObject();
        final int size = in.readInt();
        final Entry entry3;
        final Entry entry2;
        final Entry entry;
        final Entry head = entry = (entry2 = (entry3 = new Entry(null)));
        entry2.prev = entry;
        entry3.next = entry;
        for (int i = 0; i < size; ++i) {
            this.insertBefore(head, in.readObject());
        }
        this.size = size;
        this.head = head;
    }
    
    public Object clone() {
        LinkedList clone = null;
        try {
            clone = (LinkedList)super.clone();
        }
        catch (CloneNotSupportedException e) {
            throw new InternalError();
        }
        final Entry entry3;
        final Entry entry2;
        final Entry entry;
        final Entry head = entry = (entry2 = (entry3 = new Entry(null)));
        entry2.prev = entry;
        entry3.next = entry;
        clone.head = head;
        clone.addAll(this);
        return clone;
    }
    
    private static class Entry
    {
        Entry prev;
        Entry next;
        Object val;
        
        Entry(final Object val) {
            this.val = val;
        }
    }
    
    private class Itr implements ListIterator
    {
        int expectedModCount;
        int idx;
        Entry cursor;
        Entry lastRet;
        
        Itr(final Entry cursor, final int idx) {
            this.cursor = cursor;
            this.idx = idx;
            this.expectedModCount = LinkedList.this.modCount;
        }
        
        Itr(final LinkedList this$0) {
            this(this$0, this$0.head.next, 0);
        }
        
        public boolean hasNext() {
            return this.cursor != LinkedList.this.head;
        }
        
        public int nextIndex() {
            return this.idx;
        }
        
        public boolean hasPrevious() {
            return this.cursor.prev != LinkedList.this.head;
        }
        
        public int previousIndex() {
            return this.idx - 1;
        }
        
        public Object next() {
            if (this.expectedModCount != LinkedList.this.modCount) {
                throw new ConcurrentModificationException();
            }
            if (this.cursor == LinkedList.this.head) {
                throw new NoSuchElementException();
            }
            this.lastRet = this.cursor;
            this.cursor = this.cursor.next;
            ++this.idx;
            return this.lastRet.val;
        }
        
        public Object previous() {
            if (this.expectedModCount != LinkedList.this.modCount) {
                throw new ConcurrentModificationException();
            }
            if (this.cursor.prev == LinkedList.this.head) {
                throw new NoSuchElementException();
            }
            final Entry prev = this.cursor.prev;
            this.cursor = prev;
            this.lastRet = prev;
            --this.idx;
            return this.lastRet.val;
        }
        
        public void add(final Object val) {
            if (this.expectedModCount != LinkedList.this.modCount) {
                throw new ConcurrentModificationException();
            }
            LinkedList.this.insertBefore(this.cursor, val);
            this.lastRet = null;
            ++this.idx;
            ++this.expectedModCount;
        }
        
        public void set(final Object newVal) {
            if (this.lastRet == null) {
                throw new IllegalStateException();
            }
            this.lastRet.val = newVal;
        }
        
        public void remove() {
            if (this.expectedModCount != LinkedList.this.modCount) {
                throw new ConcurrentModificationException();
            }
            if (this.lastRet == null) {
                throw new IllegalStateException();
            }
            if (this.lastRet.next == this.cursor) {
                --this.idx;
            }
            else {
                this.cursor = this.lastRet.next;
            }
            LinkedList.this.remove(this.lastRet);
            this.lastRet = null;
            ++this.expectedModCount;
        }
    }
    
    private class DescItr implements ListIterator
    {
        int expectedModCount;
        int idx;
        Entry cursor;
        Entry lastRet;
        
        DescItr(final Entry cursor, final int idx) {
            this.cursor = cursor;
            this.idx = idx;
            this.expectedModCount = LinkedList.this.modCount;
        }
        
        DescItr(final LinkedList this$0) {
            this(this$0, this$0.head.prev, 0);
        }
        
        public boolean hasNext() {
            return this.cursor != LinkedList.this.head;
        }
        
        public int nextIndex() {
            return this.idx;
        }
        
        public boolean hasPrevious() {
            return this.cursor.next != LinkedList.this.head;
        }
        
        public int previousIndex() {
            return this.idx - 1;
        }
        
        public Object next() {
            if (this.expectedModCount != LinkedList.this.modCount) {
                throw new ConcurrentModificationException();
            }
            if (this.cursor == LinkedList.this.head) {
                throw new NoSuchElementException();
            }
            this.lastRet = this.cursor;
            this.cursor = this.cursor.prev;
            ++this.idx;
            return this.lastRet.val;
        }
        
        public Object previous() {
            if (this.expectedModCount != LinkedList.this.modCount) {
                throw new ConcurrentModificationException();
            }
            if (this.cursor.next == LinkedList.this.head) {
                throw new NoSuchElementException();
            }
            final Entry next = this.cursor.next;
            this.cursor = next;
            this.lastRet = next;
            --this.idx;
            return this.lastRet;
        }
        
        public void add(final Object val) {
            if (this.expectedModCount != LinkedList.this.modCount) {
                throw new ConcurrentModificationException();
            }
            LinkedList.this.insertAfter(this.cursor, val);
            this.lastRet = null;
            ++this.idx;
            ++this.expectedModCount;
        }
        
        public void set(final Object newVal) {
            if (this.lastRet == null) {
                throw new IllegalStateException();
            }
            this.lastRet.val = newVal;
        }
        
        public void remove() {
            if (this.expectedModCount != LinkedList.this.modCount) {
                throw new ConcurrentModificationException();
            }
            if (this.lastRet == null) {
                throw new IllegalStateException();
            }
            if (this.lastRet.next == this.cursor) {
                --this.idx;
            }
            else {
                this.cursor = this.lastRet.next;
            }
            LinkedList.this.remove(this.lastRet);
            this.lastRet = null;
            ++this.expectedModCount;
        }
    }
}
