// 
// Decompiled by Procyon v0.5.36
// 

package edu.emory.mathcs.backport.java.util.concurrent;

import java.io.ObjectInputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.lang.reflect.Array;
import java.util.NoSuchElementException;
import edu.emory.mathcs.backport.java.util.concurrent.helpers.Utils;
import java.util.Iterator;
import java.util.Collection;
import edu.emory.mathcs.backport.java.util.concurrent.locks.Condition;
import edu.emory.mathcs.backport.java.util.concurrent.locks.ReentrantLock;
import java.io.Serializable;
import edu.emory.mathcs.backport.java.util.AbstractQueue;

public class LinkedBlockingDeque extends AbstractQueue implements BlockingDeque, Serializable
{
    private static final long serialVersionUID = -387911632671998426L;
    private transient Node first;
    private transient Node last;
    private transient int count;
    private final int capacity;
    private final ReentrantLock lock;
    private final Condition notEmpty;
    private final Condition notFull;
    
    public LinkedBlockingDeque() {
        this(Integer.MAX_VALUE);
    }
    
    public LinkedBlockingDeque(final int capacity) {
        this.lock = new ReentrantLock();
        this.notEmpty = this.lock.newCondition();
        this.notFull = this.lock.newCondition();
        if (capacity <= 0) {
            throw new IllegalArgumentException();
        }
        this.capacity = capacity;
    }
    
    public LinkedBlockingDeque(final Collection c) {
        this(Integer.MAX_VALUE);
        for (final Object e : c) {
            this.add(e);
        }
    }
    
    private boolean linkFirst(final Object e) {
        if (this.count >= this.capacity) {
            return false;
        }
        ++this.count;
        final Node f = this.first;
        final Node x = new Node(e, null, f);
        this.first = x;
        if (this.last == null) {
            this.last = x;
        }
        else {
            f.prev = x;
        }
        this.notEmpty.signal();
        return true;
    }
    
    private boolean linkLast(final Object e) {
        if (this.count >= this.capacity) {
            return false;
        }
        ++this.count;
        final Node l = this.last;
        final Node x = new Node(e, l, null);
        this.last = x;
        if (this.first == null) {
            this.first = x;
        }
        else {
            l.next = x;
        }
        this.notEmpty.signal();
        return true;
    }
    
    private Object unlinkFirst() {
        final Node f = this.first;
        if (f == null) {
            return null;
        }
        final Node n = f.next;
        if ((this.first = n) == null) {
            this.last = null;
        }
        else {
            n.prev = null;
        }
        --this.count;
        this.notFull.signal();
        return f.item;
    }
    
    private Object unlinkLast() {
        final Node l = this.last;
        if (l == null) {
            return null;
        }
        final Node p = l.prev;
        if ((this.last = p) == null) {
            this.first = null;
        }
        else {
            p.next = null;
        }
        --this.count;
        this.notFull.signal();
        return l.item;
    }
    
    private void unlink(final Node x) {
        final Node p = x.prev;
        final Node n = x.next;
        if (p == null) {
            if (n == null) {
                final Node node = null;
                this.last = node;
                this.first = node;
            }
            else {
                n.prev = null;
                this.first = n;
            }
        }
        else if (n == null) {
            p.next = null;
            this.last = p;
        }
        else {
            p.next = n;
            n.prev = p;
        }
        --this.count;
        this.notFull.signalAll();
    }
    
    public void addFirst(final Object e) {
        if (!this.offerFirst(e)) {
            throw new IllegalStateException("Deque full");
        }
    }
    
    public void addLast(final Object e) {
        if (!this.offerLast(e)) {
            throw new IllegalStateException("Deque full");
        }
    }
    
    public boolean offerFirst(final Object e) {
        if (e == null) {
            throw new NullPointerException();
        }
        this.lock.lock();
        try {
            return this.linkFirst(e);
        }
        finally {
            this.lock.unlock();
        }
    }
    
    public boolean offerLast(final Object e) {
        if (e == null) {
            throw new NullPointerException();
        }
        this.lock.lock();
        try {
            return this.linkLast(e);
        }
        finally {
            this.lock.unlock();
        }
    }
    
    public void putFirst(final Object e) throws InterruptedException {
        if (e == null) {
            throw new NullPointerException();
        }
        this.lock.lock();
        try {
            while (!this.linkFirst(e)) {
                this.notFull.await();
            }
        }
        finally {
            this.lock.unlock();
        }
    }
    
    public void putLast(final Object e) throws InterruptedException {
        if (e == null) {
            throw new NullPointerException();
        }
        this.lock.lock();
        try {
            while (!this.linkLast(e)) {
                this.notFull.await();
            }
        }
        finally {
            this.lock.unlock();
        }
    }
    
    public boolean offerFirst(final Object e, final long timeout, final TimeUnit unit) throws InterruptedException {
        if (e == null) {
            throw new NullPointerException();
        }
        long nanos = unit.toNanos(timeout);
        final long deadline = Utils.nanoTime() + nanos;
        this.lock.lockInterruptibly();
        try {
            while (!this.linkFirst(e)) {
                if (nanos <= 0L) {
                    return false;
                }
                this.notFull.await(nanos, TimeUnit.NANOSECONDS);
                nanos = deadline - Utils.nanoTime();
            }
            return true;
        }
        finally {
            this.lock.unlock();
        }
    }
    
    public boolean offerLast(final Object e, final long timeout, final TimeUnit unit) throws InterruptedException {
        if (e == null) {
            throw new NullPointerException();
        }
        long nanos = unit.toNanos(timeout);
        final long deadline = Utils.nanoTime() + nanos;
        this.lock.lockInterruptibly();
        try {
            while (!this.linkLast(e)) {
                if (nanos <= 0L) {
                    return false;
                }
                this.notFull.await(nanos, TimeUnit.NANOSECONDS);
                nanos = deadline - Utils.nanoTime();
            }
            return true;
        }
        finally {
            this.lock.unlock();
        }
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
        this.lock.lock();
        try {
            return this.unlinkFirst();
        }
        finally {
            this.lock.unlock();
        }
    }
    
    public Object pollLast() {
        this.lock.lock();
        try {
            return this.unlinkLast();
        }
        finally {
            this.lock.unlock();
        }
    }
    
    public Object takeFirst() throws InterruptedException {
        this.lock.lock();
        try {
            Object x;
            while ((x = this.unlinkFirst()) == null) {
                this.notEmpty.await();
            }
            return x;
        }
        finally {
            this.lock.unlock();
        }
    }
    
    public Object takeLast() throws InterruptedException {
        this.lock.lock();
        try {
            Object x;
            while ((x = this.unlinkLast()) == null) {
                this.notEmpty.await();
            }
            return x;
        }
        finally {
            this.lock.unlock();
        }
    }
    
    public Object pollFirst(final long timeout, final TimeUnit unit) throws InterruptedException {
        long nanos = unit.toNanos(timeout);
        final long deadline = Utils.nanoTime() + nanos;
        this.lock.lockInterruptibly();
        try {
            while (true) {
                final Object x = this.unlinkFirst();
                if (x != null) {
                    return x;
                }
                if (nanos <= 0L) {
                    return null;
                }
                this.notEmpty.await(nanos, TimeUnit.NANOSECONDS);
                nanos = deadline - Utils.nanoTime();
            }
        }
        finally {
            this.lock.unlock();
        }
    }
    
    public Object pollLast(final long timeout, final TimeUnit unit) throws InterruptedException {
        long nanos = unit.toNanos(timeout);
        final long deadline = Utils.nanoTime() + nanos;
        this.lock.lockInterruptibly();
        try {
            while (true) {
                final Object x = this.unlinkLast();
                if (x != null) {
                    return x;
                }
                if (nanos <= 0L) {
                    return null;
                }
                this.notEmpty.await(nanos, TimeUnit.NANOSECONDS);
                nanos = deadline - Utils.nanoTime();
            }
        }
        finally {
            this.lock.unlock();
        }
    }
    
    public Object getFirst() {
        final Object x = this.peekFirst();
        if (x == null) {
            throw new NoSuchElementException();
        }
        return x;
    }
    
    public Object getLast() {
        final Object x = this.peekLast();
        if (x == null) {
            throw new NoSuchElementException();
        }
        return x;
    }
    
    public Object peekFirst() {
        this.lock.lock();
        try {
            return (this.first == null) ? null : this.first.item;
        }
        finally {
            this.lock.unlock();
        }
    }
    
    public Object peekLast() {
        this.lock.lock();
        try {
            return (this.last == null) ? null : this.last.item;
        }
        finally {
            this.lock.unlock();
        }
    }
    
    public boolean removeFirstOccurrence(final Object o) {
        if (o == null) {
            return false;
        }
        this.lock.lock();
        try {
            for (Node p = this.first; p != null; p = p.next) {
                if (o.equals(p.item)) {
                    this.unlink(p);
                    return true;
                }
            }
            return false;
        }
        finally {
            this.lock.unlock();
        }
    }
    
    public boolean removeLastOccurrence(final Object o) {
        if (o == null) {
            return false;
        }
        this.lock.lock();
        try {
            for (Node p = this.last; p != null; p = p.prev) {
                if (o.equals(p.item)) {
                    this.unlink(p);
                    return true;
                }
            }
            return false;
        }
        finally {
            this.lock.unlock();
        }
    }
    
    public boolean add(final Object e) {
        this.addLast(e);
        return true;
    }
    
    public boolean offer(final Object e) {
        return this.offerLast(e);
    }
    
    public void put(final Object e) throws InterruptedException {
        this.putLast(e);
    }
    
    public boolean offer(final Object e, final long timeout, final TimeUnit unit) throws InterruptedException {
        return this.offerLast(e, timeout, unit);
    }
    
    public Object remove() {
        return this.removeFirst();
    }
    
    public Object poll() {
        return this.pollFirst();
    }
    
    public Object take() throws InterruptedException {
        return this.takeFirst();
    }
    
    public Object poll(final long timeout, final TimeUnit unit) throws InterruptedException {
        return this.pollFirst(timeout, unit);
    }
    
    public Object element() {
        return this.getFirst();
    }
    
    public Object peek() {
        return this.peekFirst();
    }
    
    public int remainingCapacity() {
        this.lock.lock();
        try {
            return this.capacity - this.count;
        }
        finally {
            this.lock.unlock();
        }
    }
    
    public int drainTo(final Collection c) {
        if (c == null) {
            throw new NullPointerException();
        }
        if (c == this) {
            throw new IllegalArgumentException();
        }
        this.lock.lock();
        try {
            for (Node p = this.first; p != null; p = p.next) {
                c.add(p.item);
            }
            final int n = this.count;
            this.count = 0;
            final Node node = null;
            this.last = node;
            this.first = node;
            this.notFull.signalAll();
            return n;
        }
        finally {
            this.lock.unlock();
        }
    }
    
    public int drainTo(final Collection c, final int maxElements) {
        if (c == null) {
            throw new NullPointerException();
        }
        if (c == this) {
            throw new IllegalArgumentException();
        }
        this.lock.lock();
        try {
            int n;
            for (n = 0; n < maxElements && this.first != null; ++n) {
                c.add(this.first.item);
                this.first.prev = null;
                this.first = this.first.next;
                --this.count;
            }
            if (this.first == null) {
                this.last = null;
            }
            this.notFull.signalAll();
            return n;
        }
        finally {
            this.lock.unlock();
        }
    }
    
    public void push(final Object e) {
        this.addFirst(e);
    }
    
    public Object pop() {
        return this.removeFirst();
    }
    
    public boolean remove(final Object o) {
        return this.removeFirstOccurrence(o);
    }
    
    public int size() {
        this.lock.lock();
        try {
            return this.count;
        }
        finally {
            this.lock.unlock();
        }
    }
    
    public boolean contains(final Object o) {
        if (o == null) {
            return false;
        }
        this.lock.lock();
        try {
            for (Node p = this.first; p != null; p = p.next) {
                if (o.equals(p.item)) {
                    return true;
                }
            }
            return false;
        }
        finally {
            this.lock.unlock();
        }
    }
    
    boolean removeNode(final Node e) {
        this.lock.lock();
        try {
            for (Node p = this.first; p != null; p = p.next) {
                if (p == e) {
                    this.unlink(p);
                    return true;
                }
            }
            return false;
        }
        finally {
            this.lock.unlock();
        }
    }
    
    public Object[] toArray() {
        this.lock.lock();
        try {
            final Object[] a = new Object[this.count];
            int k = 0;
            for (Node p = this.first; p != null; p = p.next) {
                a[k++] = p.item;
            }
            return a;
        }
        finally {
            this.lock.unlock();
        }
    }
    
    public Object[] toArray(Object[] a) {
        this.lock.lock();
        try {
            if (a.length < this.count) {
                a = (Object[])Array.newInstance(a.getClass().getComponentType(), this.count);
            }
            int k = 0;
            for (Node p = this.first; p != null; p = p.next) {
                a[k++] = p.item;
            }
            if (a.length > k) {
                a[k] = null;
            }
            return a;
        }
        finally {
            this.lock.unlock();
        }
    }
    
    public String toString() {
        this.lock.lock();
        try {
            return super.toString();
        }
        finally {
            this.lock.unlock();
        }
    }
    
    public void clear() {
        this.lock.lock();
        try {
            final Node node = null;
            this.last = node;
            this.first = node;
            this.count = 0;
            this.notFull.signalAll();
        }
        finally {
            this.lock.unlock();
        }
    }
    
    public Iterator iterator() {
        return new Itr();
    }
    
    public Iterator descendingIterator() {
        return new DescendingItr();
    }
    
    private void writeObject(final ObjectOutputStream s) throws IOException {
        this.lock.lock();
        try {
            s.defaultWriteObject();
            for (Node p = this.first; p != null; p = p.next) {
                s.writeObject(p.item);
            }
            s.writeObject(null);
        }
        finally {
            this.lock.unlock();
        }
    }
    
    private void readObject(final ObjectInputStream s) throws IOException, ClassNotFoundException {
        s.defaultReadObject();
        this.count = 0;
        this.first = null;
        this.last = null;
        while (true) {
            final Object item = s.readObject();
            if (item == null) {
                break;
            }
            this.add(item);
        }
    }
    
    static final class Node
    {
        Object item;
        Node prev;
        Node next;
        
        Node(final Object x, final Node p, final Node n) {
            this.item = x;
            this.prev = p;
            this.next = n;
        }
    }
    
    private abstract class AbstractItr implements Iterator
    {
        Node next;
        Object nextItem;
        private Node lastRet;
        
        AbstractItr() {
            this.advance();
        }
        
        abstract void advance();
        
        public boolean hasNext() {
            return this.next != null;
        }
        
        public Object next() {
            if (this.next == null) {
                throw new NoSuchElementException();
            }
            this.lastRet = this.next;
            final Object x = this.nextItem;
            this.advance();
            return x;
        }
        
        public void remove() {
            final Node n = this.lastRet;
            if (n == null) {
                throw new IllegalStateException();
            }
            this.lastRet = null;
            LinkedBlockingDeque.this.removeNode(n);
        }
    }
    
    private class Itr extends AbstractItr
    {
        void advance() {
            final ReentrantLock lock = LinkedBlockingDeque.this.lock;
            lock.lock();
            try {
                this.next = ((this.next == null) ? LinkedBlockingDeque.this.first : this.next.next);
                this.nextItem = ((this.next == null) ? null : this.next.item);
            }
            finally {
                lock.unlock();
            }
        }
    }
    
    private class DescendingItr extends AbstractItr
    {
        void advance() {
            final ReentrantLock lock = LinkedBlockingDeque.this.lock;
            lock.lock();
            try {
                this.next = ((this.next == null) ? LinkedBlockingDeque.this.last : this.next.prev);
                this.nextItem = ((this.next == null) ? null : this.next.item);
            }
            finally {
                lock.unlock();
            }
        }
    }
}
