// 
// Decompiled by Procyon v0.5.36
// 

package edu.emory.mathcs.backport.java.util;

import java.io.ObjectInputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.lang.reflect.Array;
import java.util.Iterator;
import java.util.ConcurrentModificationException;
import java.util.NoSuchElementException;
import java.util.Collection;
import java.io.Serializable;

public class ArrayDeque extends AbstractCollection implements Deque, Cloneable, Serializable
{
    private transient Object[] elements;
    private transient int head;
    private transient int tail;
    private static final int MIN_INITIAL_CAPACITY = 8;
    private static final long serialVersionUID = 2340985798034038923L;
    static final /* synthetic */ boolean $assertionsDisabled;
    
    private void allocateElements(final int numElements) {
        int initialCapacity = 8;
        if (numElements >= initialCapacity) {
            initialCapacity = numElements;
            initialCapacity |= initialCapacity >>> 1;
            initialCapacity |= initialCapacity >>> 2;
            initialCapacity |= initialCapacity >>> 4;
            initialCapacity |= initialCapacity >>> 8;
            initialCapacity |= initialCapacity >>> 16;
            if (++initialCapacity < 0) {
                initialCapacity >>>= 1;
            }
        }
        this.elements = new Object[initialCapacity];
    }
    
    private void doubleCapacity() {
        assert this.head == this.tail;
        final int p = this.head;
        final int n = this.elements.length;
        final int r = n - p;
        final int newCapacity = n << 1;
        if (newCapacity < 0) {
            throw new IllegalStateException("Sorry, deque too big");
        }
        final Object[] a = new Object[newCapacity];
        System.arraycopy(this.elements, p, a, 0, r);
        System.arraycopy(this.elements, 0, a, r, p);
        this.elements = a;
        this.head = 0;
        this.tail = n;
    }
    
    private Object[] copyElements(final Object[] a) {
        if (this.head < this.tail) {
            System.arraycopy(this.elements, this.head, a, 0, this.size());
        }
        else if (this.head > this.tail) {
            final int headPortionLen = this.elements.length - this.head;
            System.arraycopy(this.elements, this.head, a, 0, headPortionLen);
            System.arraycopy(this.elements, 0, a, headPortionLen, this.tail);
        }
        return a;
    }
    
    public ArrayDeque() {
        this.elements = new Object[16];
    }
    
    public ArrayDeque(final int numElements) {
        this.allocateElements(numElements);
    }
    
    public ArrayDeque(final Collection c) {
        this.allocateElements(c.size());
        this.addAll(c);
    }
    
    public void addFirst(final Object e) {
        if (e == null) {
            throw new NullPointerException();
        }
        this.elements[this.head = (this.head - 1 & this.elements.length - 1)] = e;
        if (this.head == this.tail) {
            this.doubleCapacity();
        }
    }
    
    public void addLast(final Object e) {
        if (e == null) {
            throw new NullPointerException();
        }
        this.elements[this.tail] = e;
        if ((this.tail = (this.tail + 1 & this.elements.length - 1)) == this.head) {
            this.doubleCapacity();
        }
    }
    
    public boolean offerFirst(final Object e) {
        this.addFirst(e);
        return true;
    }
    
    public boolean offerLast(final Object e) {
        this.addLast(e);
        return true;
    }
    
    public Object removeFirst() {
        final Object x = this.pollFirst();
        if (x == null) {
            throw new NoSuchElementException();
        }
        return x;
    }
    
    public Object removeLast() {
        final Object x = this.pollLast();
        if (x == null) {
            throw new NoSuchElementException();
        }
        return x;
    }
    
    public Object pollFirst() {
        final int h = this.head;
        final Object result = this.elements[h];
        if (result == null) {
            return null;
        }
        this.elements[h] = null;
        this.head = (h + 1 & this.elements.length - 1);
        return result;
    }
    
    public Object pollLast() {
        final int t = this.tail - 1 & this.elements.length - 1;
        final Object result = this.elements[t];
        if (result == null) {
            return null;
        }
        this.elements[t] = null;
        this.tail = t;
        return result;
    }
    
    public Object getFirst() {
        final Object x = this.elements[this.head];
        if (x == null) {
            throw new NoSuchElementException();
        }
        return x;
    }
    
    public Object getLast() {
        final Object x = this.elements[this.tail - 1 & this.elements.length - 1];
        if (x == null) {
            throw new NoSuchElementException();
        }
        return x;
    }
    
    public Object peekFirst() {
        return this.elements[this.head];
    }
    
    public Object peekLast() {
        return this.elements[this.tail - 1 & this.elements.length - 1];
    }
    
    public boolean removeFirstOccurrence(final Object o) {
        if (o == null) {
            return false;
        }
        Object x;
        for (int mask = this.elements.length - 1, i = this.head; (x = this.elements[i]) != null; i = (i + 1 & mask)) {
            if (o.equals(x)) {
                this.delete(i);
                return true;
            }
        }
        return false;
    }
    
    public boolean removeLastOccurrence(final Object o) {
        if (o == null) {
            return false;
        }
        Object x;
        for (int mask = this.elements.length - 1, i = this.tail - 1 & mask; (x = this.elements[i]) != null; i = (i - 1 & mask)) {
            if (o.equals(x)) {
                this.delete(i);
                return true;
            }
        }
        return false;
    }
    
    public boolean add(final Object e) {
        this.addLast(e);
        return true;
    }
    
    public boolean offer(final Object e) {
        return this.offerLast(e);
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
    
    private void checkInvariants() {
        assert this.elements[this.tail] == null;
        Label_0100: {
            if (!ArrayDeque.$assertionsDisabled) {
                if (this.head == this.tail) {
                    if (this.elements[this.head] == null) {
                        break Label_0100;
                    }
                }
                else if (this.elements[this.head] != null && this.elements[this.tail - 1 & this.elements.length - 1] != null) {
                    break Label_0100;
                }
                throw new AssertionError();
            }
        }
        assert this.elements[this.head - 1 & this.elements.length - 1] == null;
    }
    
    private boolean delete(final int i) {
        this.checkInvariants();
        final Object[] elements = this.elements;
        final int mask = elements.length - 1;
        final int h = this.head;
        final int t = this.tail;
        final int front = i - h & mask;
        final int back = t - i & mask;
        if (front >= (t - h & mask)) {
            throw new ConcurrentModificationException();
        }
        if (front < back) {
            if (h <= i) {
                System.arraycopy(elements, h, elements, h + 1, front);
            }
            else {
                System.arraycopy(elements, 0, elements, 1, i);
                elements[0] = elements[mask];
                System.arraycopy(elements, h, elements, h + 1, mask - h);
            }
            elements[h] = null;
            this.head = (h + 1 & mask);
            return false;
        }
        if (i < t) {
            System.arraycopy(elements, i + 1, elements, i, back);
            this.tail = t - 1;
        }
        else {
            System.arraycopy(elements, i + 1, elements, i, mask - i);
            elements[mask] = elements[0];
            System.arraycopy(elements, 1, elements, 0, t);
            this.tail = (t - 1 & mask);
        }
        return true;
    }
    
    public int size() {
        return this.tail - this.head & this.elements.length - 1;
    }
    
    public boolean isEmpty() {
        return this.head == this.tail;
    }
    
    public Iterator iterator() {
        return new DeqIterator();
    }
    
    public Iterator descendingIterator() {
        return new DescendingIterator();
    }
    
    public boolean contains(final Object o) {
        if (o == null) {
            return false;
        }
        Object x;
        for (int mask = this.elements.length - 1, i = this.head; (x = this.elements[i]) != null; i = (i + 1 & mask)) {
            if (o.equals(x)) {
                return true;
            }
        }
        return false;
    }
    
    public boolean remove(final Object o) {
        return this.removeFirstOccurrence(o);
    }
    
    public void clear() {
        final int h = this.head;
        final int t = this.tail;
        if (h != t) {
            final int n = 0;
            this.tail = n;
            this.head = n;
            int i = h;
            final int mask = this.elements.length - 1;
            do {
                this.elements[i] = null;
                i = (i + 1 & mask);
            } while (i != t);
        }
    }
    
    public Object[] toArray() {
        return this.copyElements(new Object[this.size()]);
    }
    
    public Object[] toArray(Object[] a) {
        final int size = this.size();
        if (a.length < size) {
            a = (Object[])Array.newInstance(a.getClass().getComponentType(), size);
        }
        this.copyElements(a);
        if (a.length > size) {
            a[size] = null;
        }
        return a;
    }
    
    public Object clone() {
        try {
            final ArrayDeque result = (ArrayDeque)super.clone();
            result.elements = Arrays.copyOf(this.elements, this.elements.length);
            return result;
        }
        catch (CloneNotSupportedException e) {
            throw new AssertionError();
        }
    }
    
    private void writeObject(final ObjectOutputStream s) throws IOException {
        s.defaultWriteObject();
        s.writeInt(this.size());
        for (int mask = this.elements.length - 1, i = this.head; i != this.tail; i = (i + 1 & mask)) {
            s.writeObject(this.elements[i]);
        }
    }
    
    private void readObject(final ObjectInputStream s) throws IOException, ClassNotFoundException {
        s.defaultReadObject();
        final int size = s.readInt();
        this.allocateElements(size);
        this.head = 0;
        this.tail = size;
        for (int i = 0; i < size; ++i) {
            this.elements[i] = s.readObject();
        }
    }
    
    private class DeqIterator implements Iterator
    {
        private int cursor;
        private int fence;
        private int lastRet;
        
        private DeqIterator() {
            this.cursor = ArrayDeque.this.head;
            this.fence = ArrayDeque.this.tail;
            this.lastRet = -1;
        }
        
        public boolean hasNext() {
            return this.cursor != this.fence;
        }
        
        public Object next() {
            if (this.cursor == this.fence) {
                throw new NoSuchElementException();
            }
            final Object result = ArrayDeque.this.elements[this.cursor];
            if (ArrayDeque.this.tail != this.fence || result == null) {
                throw new ConcurrentModificationException();
            }
            this.lastRet = this.cursor;
            this.cursor = (this.cursor + 1 & ArrayDeque.this.elements.length - 1);
            return result;
        }
        
        public void remove() {
            if (this.lastRet < 0) {
                throw new IllegalStateException();
            }
            if (ArrayDeque.this.delete(this.lastRet)) {
                this.cursor = (this.cursor - 1 & ArrayDeque.this.elements.length - 1);
                this.fence = ArrayDeque.this.tail;
            }
            this.lastRet = -1;
        }
    }
    
    private class DescendingIterator implements Iterator
    {
        private int cursor;
        private int fence;
        private int lastRet;
        
        private DescendingIterator() {
            this.cursor = ArrayDeque.this.tail;
            this.fence = ArrayDeque.this.head;
            this.lastRet = -1;
        }
        
        public boolean hasNext() {
            return this.cursor != this.fence;
        }
        
        public Object next() {
            if (this.cursor == this.fence) {
                throw new NoSuchElementException();
            }
            this.cursor = (this.cursor - 1 & ArrayDeque.this.elements.length - 1);
            final Object result = ArrayDeque.this.elements[this.cursor];
            if (ArrayDeque.this.head != this.fence || result == null) {
                throw new ConcurrentModificationException();
            }
            this.lastRet = this.cursor;
            return result;
        }
        
        public void remove() {
            if (this.lastRet < 0) {
                throw new IllegalStateException();
            }
            if (!ArrayDeque.this.delete(this.lastRet)) {
                this.cursor = (this.cursor + 1 & ArrayDeque.this.elements.length - 1);
                this.fence = ArrayDeque.this.head;
            }
            this.lastRet = -1;
        }
    }
}
