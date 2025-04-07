// 
// Decompiled by Procyon v0.5.36
// 

package edu.emory.mathcs.backport.java.util.concurrent;

import java.util.NoSuchElementException;
import java.io.ObjectInputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.lang.reflect.Array;
import edu.emory.mathcs.backport.java.util.concurrent.helpers.Utils;
import java.util.Iterator;
import java.util.Collection;
import java.io.Serializable;
import edu.emory.mathcs.backport.java.util.AbstractQueue;

public class LinkedBlockingQueue extends AbstractQueue implements BlockingQueue, Serializable
{
    private static final long serialVersionUID = -6903933977591709194L;
    private final int capacity;
    private volatile int count;
    private transient Node head;
    private transient Node last;
    private final Object takeLock;
    private final Object putLock;
    
    private void signalNotEmpty() {
        synchronized (this.takeLock) {
            this.takeLock.notify();
        }
    }
    
    private void signalNotFull() {
        synchronized (this.putLock) {
            this.putLock.notify();
        }
    }
    
    private void insert(final Object x) {
        final Node last = this.last;
        final Node node = new Node(x);
        last.next = node;
        this.last = node;
    }
    
    private Object extract() {
        final Node first = this.head.next;
        this.head = first;
        final Object x = first.item;
        first.item = null;
        return x;
    }
    
    public LinkedBlockingQueue() {
        this(Integer.MAX_VALUE);
    }
    
    public LinkedBlockingQueue(final int capacity) {
        this.count = 0;
        this.takeLock = new SerializableLock();
        this.putLock = new SerializableLock();
        if (capacity <= 0) {
            throw new IllegalArgumentException();
        }
        this.capacity = capacity;
        final Node node = new Node(null);
        this.head = node;
        this.last = node;
    }
    
    public LinkedBlockingQueue(final Collection c) {
        this(Integer.MAX_VALUE);
        for (final Object e : c) {
            this.add(e);
        }
    }
    
    public int size() {
        return this.count;
    }
    
    public int remainingCapacity() {
        return this.capacity - this.count;
    }
    
    public void put(final Object e) throws InterruptedException {
        if (e == null) {
            throw new NullPointerException();
        }
        int c = -1;
        synchronized (this.putLock) {
            try {
                while (this.count == this.capacity) {
                    this.putLock.wait();
                }
            }
            catch (InterruptedException ie) {
                this.putLock.notify();
                throw ie;
            }
            this.insert(e);
            synchronized (this) {
                c = this.count++;
            }
            if (c + 1 < this.capacity) {
                this.putLock.notify();
            }
        }
        if (c == 0) {
            this.signalNotEmpty();
        }
    }
    
    public boolean offer(final Object e, final long timeout, final TimeUnit unit) throws InterruptedException {
        if (e == null) {
            throw new NullPointerException();
        }
        long nanos = unit.toNanos(timeout);
        int c = -1;
        Label_0169: {
            synchronized (this.putLock) {
                final long deadline = Utils.nanoTime() + nanos;
                while (this.count >= this.capacity) {
                    if (nanos <= 0L) {
                        return false;
                    }
                    try {
                        TimeUnit.NANOSECONDS.timedWait(this.putLock, nanos);
                        nanos = deadline - Utils.nanoTime();
                        continue;
                    }
                    catch (InterruptedException ie) {
                        this.putLock.notify();
                        throw ie;
                    }
                    break Label_0169;
                }
                this.insert(e);
                synchronized (this) {
                    c = this.count++;
                }
                if (c + 1 < this.capacity) {
                    this.putLock.notify();
                }
            }
        }
        if (c == 0) {
            this.signalNotEmpty();
        }
        return true;
    }
    
    public boolean offer(final Object e) {
        if (e == null) {
            throw new NullPointerException();
        }
        if (this.count == this.capacity) {
            return false;
        }
        int c = -1;
        synchronized (this.putLock) {
            if (this.count < this.capacity) {
                this.insert(e);
                synchronized (this) {
                    c = this.count++;
                }
                if (c + 1 < this.capacity) {
                    this.putLock.notify();
                }
            }
        }
        if (c == 0) {
            this.signalNotEmpty();
        }
        return c >= 0;
    }
    
    public Object take() throws InterruptedException {
        int c = -1;
        final Object x;
        synchronized (this.takeLock) {
            try {
                while (this.count == 0) {
                    this.takeLock.wait();
                }
            }
            catch (InterruptedException ie) {
                this.takeLock.notify();
                throw ie;
            }
            x = this.extract();
            synchronized (this) {
                c = this.count--;
            }
            if (c > 1) {
                this.takeLock.notify();
            }
        }
        if (c == this.capacity) {
            this.signalNotFull();
        }
        return x;
    }
    
    public Object poll(final long timeout, final TimeUnit unit) throws InterruptedException {
        Object x = null;
        int c = -1;
        long nanos = unit.toNanos(timeout);
        Label_0151: {
            synchronized (this.takeLock) {
                final long deadline = Utils.nanoTime() + nanos;
                while (this.count <= 0) {
                    if (nanos <= 0L) {
                        return null;
                    }
                    try {
                        TimeUnit.NANOSECONDS.timedWait(this.takeLock, nanos);
                        nanos = deadline - Utils.nanoTime();
                        continue;
                    }
                    catch (InterruptedException ie) {
                        this.takeLock.notify();
                        throw ie;
                    }
                    break Label_0151;
                }
                x = this.extract();
                synchronized (this) {
                    c = this.count--;
                }
                if (c > 1) {
                    this.takeLock.notify();
                }
            }
        }
        if (c == this.capacity) {
            this.signalNotFull();
        }
        return x;
    }
    
    public Object poll() {
        if (this.count == 0) {
            return null;
        }
        Object x = null;
        int c = -1;
        synchronized (this.takeLock) {
            if (this.count > 0) {
                x = this.extract();
                synchronized (this) {
                    c = this.count--;
                }
                if (c > 1) {
                    this.takeLock.notify();
                }
            }
        }
        if (c == this.capacity) {
            this.signalNotFull();
        }
        return x;
    }
    
    public Object peek() {
        if (this.count == 0) {
            return null;
        }
        synchronized (this.takeLock) {
            final Node first = this.head.next;
            if (first == null) {
                return null;
            }
            return first.item;
        }
    }
    
    public boolean remove(final Object o) {
        if (o == null) {
            return false;
        }
        boolean removed = false;
        synchronized (this.putLock) {
            synchronized (this.takeLock) {
                Node trail = this.head;
                Node p;
                for (p = this.head.next; p != null; p = p.next) {
                    if (o.equals(p.item)) {
                        removed = true;
                        break;
                    }
                    trail = p;
                }
                if (removed) {
                    p.item = null;
                    trail.next = p.next;
                    if (this.last == p) {
                        this.last = trail;
                    }
                    synchronized (this) {
                        if (this.count-- == this.capacity) {
                            this.putLock.notifyAll();
                        }
                    }
                }
            }
        }
        return removed;
    }
    
    public Object[] toArray() {
        synchronized (this.putLock) {
            synchronized (this.takeLock) {
                final int size = this.count;
                final Object[] a = new Object[size];
                int k = 0;
                for (Node p = this.head.next; p != null; p = p.next) {
                    a[k++] = p.item;
                }
                return a;
            }
        }
    }
    
    public Object[] toArray(Object[] a) {
        synchronized (this.putLock) {
            synchronized (this.takeLock) {
                final int size = this.count;
                if (a.length < size) {
                    a = (Object[])Array.newInstance(a.getClass().getComponentType(), size);
                }
                int k = 0;
                for (Node p = this.head.next; p != null; p = p.next) {
                    a[k++] = p.item;
                }
                if (a.length > k) {
                    a[k] = null;
                }
                return a;
            }
        }
    }
    
    public String toString() {
        synchronized (this.putLock) {
            synchronized (this.takeLock) {
                return super.toString();
            }
        }
    }
    
    public void clear() {
        synchronized (this.putLock) {
            synchronized (this.takeLock) {
                this.head.next = null;
                assert this.head.item == null;
                this.last = this.head;
                final int c;
                synchronized (this) {
                    c = this.count;
                    this.count = 0;
                }
                if (c == this.capacity) {
                    this.putLock.notifyAll();
                }
            }
        }
    }
    
    public int drainTo(final Collection c) {
        if (c == null) {
            throw new NullPointerException();
        }
        if (c == this) {
            throw new IllegalArgumentException();
        }
        final Node first;
        synchronized (this.putLock) {
            synchronized (this.takeLock) {
                first = this.head.next;
                this.head.next = null;
                assert this.head.item == null;
                this.last = this.head;
                final int cold;
                synchronized (this) {
                    cold = this.count;
                    this.count = 0;
                }
                if (cold == this.capacity) {
                    this.putLock.notifyAll();
                }
            }
        }
        int n = 0;
        for (Node p = first; p != null; p = p.next) {
            c.add(p.item);
            p.item = null;
            ++n;
        }
        return n;
    }
    
    public int drainTo(final Collection c, final int maxElements) {
        if (c == null) {
            throw new NullPointerException();
        }
        if (c == this) {
            throw new IllegalArgumentException();
        }
        synchronized (this.putLock) {
            synchronized (this.takeLock) {
                int n;
                Node p;
                for (n = 0, p = this.head.next; p != null && n < maxElements; p = p.next, ++n) {
                    c.add(p.item);
                    p.item = null;
                }
                if (n != 0) {
                    this.head.next = p;
                    assert this.head.item == null;
                    if (p == null) {
                        this.last = this.head;
                    }
                    final int cold;
                    synchronized (this) {
                        cold = this.count;
                        this.count -= n;
                    }
                    if (cold == this.capacity) {
                        this.putLock.notifyAll();
                    }
                }
                return n;
            }
        }
    }
    
    public Iterator iterator() {
        return new Itr();
    }
    
    private void writeObject(final ObjectOutputStream s) throws IOException {
        synchronized (this.putLock) {
            synchronized (this.takeLock) {
                s.defaultWriteObject();
                for (Node p = this.head.next; p != null; p = p.next) {
                    s.writeObject(p.item);
                }
                s.writeObject(null);
            }
        }
    }
    
    private void readObject(final ObjectInputStream s) throws IOException, ClassNotFoundException {
        s.defaultReadObject();
        synchronized (this) {
            this.count = 0;
        }
        final Node node = new Node(null);
        this.head = node;
        this.last = node;
        while (true) {
            final Object item = s.readObject();
            if (item == null) {
                break;
            }
            this.add(item);
        }
    }
    
    static class Node
    {
        volatile Object item;
        Node next;
        
        Node(final Object x) {
            this.item = x;
        }
    }
    
    private class Itr implements Iterator
    {
        private Node current;
        private Node lastRet;
        private Object currentElement;
        
        Itr() {
            synchronized (LinkedBlockingQueue.this.putLock) {
                synchronized (LinkedBlockingQueue.this.takeLock) {
                    this.current = LinkedBlockingQueue.this.head.next;
                    if (this.current != null) {
                        this.currentElement = this.current.item;
                    }
                }
            }
        }
        
        public boolean hasNext() {
            return this.current != null;
        }
        
        public Object next() {
            synchronized (LinkedBlockingQueue.this.putLock) {
                synchronized (LinkedBlockingQueue.this.takeLock) {
                    if (this.current == null) {
                        throw new NoSuchElementException();
                    }
                    final Object x = this.currentElement;
                    this.lastRet = this.current;
                    this.current = this.current.next;
                    if (this.current != null) {
                        this.currentElement = this.current.item;
                    }
                    return x;
                }
            }
        }
        
        public void remove() {
            if (this.lastRet == null) {
                throw new IllegalStateException();
            }
            synchronized (LinkedBlockingQueue.this.putLock) {
                synchronized (LinkedBlockingQueue.this.takeLock) {
                    final Node node = this.lastRet;
                    this.lastRet = null;
                    Node trail = LinkedBlockingQueue.this.head;
                    Node p;
                    for (p = LinkedBlockingQueue.this.head.next; p != null && p != node; p = p.next) {
                        trail = p;
                    }
                    if (p == node) {
                        p.item = null;
                        trail.next = p.next;
                        if (LinkedBlockingQueue.this.last == p) {
                            LinkedBlockingQueue.this.last = trail;
                        }
                        final int c;
                        synchronized (this) {
                            c = LinkedBlockingQueue.this.count--;
                        }
                        if (c == LinkedBlockingQueue.this.capacity) {
                            LinkedBlockingQueue.this.putLock.notifyAll();
                        }
                    }
                }
            }
        }
    }
    
    private static class SerializableLock implements Serializable
    {
        private static final long serialVersionUID = -8856990691138858668L;
    }
}
