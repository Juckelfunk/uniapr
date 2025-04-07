// 
// Decompiled by Procyon v0.5.36
// 

package edu.emory.mathcs.backport.java.util.concurrent;

import java.util.NoSuchElementException;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.util.Iterator;
import edu.emory.mathcs.backport.java.util.concurrent.helpers.Utils;
import java.util.Collection;
import java.util.Comparator;
import edu.emory.mathcs.backport.java.util.concurrent.locks.Condition;
import edu.emory.mathcs.backport.java.util.concurrent.locks.ReentrantLock;
import edu.emory.mathcs.backport.java.util.PriorityQueue;
import java.io.Serializable;
import edu.emory.mathcs.backport.java.util.AbstractQueue;

public class PriorityBlockingQueue extends AbstractQueue implements BlockingQueue, Serializable
{
    private static final long serialVersionUID = 5595510919245408276L;
    private final PriorityQueue q;
    private final ReentrantLock lock;
    private final Condition notEmpty;
    
    public PriorityBlockingQueue() {
        this.lock = new ReentrantLock(true);
        this.notEmpty = this.lock.newCondition();
        this.q = new PriorityQueue();
    }
    
    public PriorityBlockingQueue(final int initialCapacity) {
        this.lock = new ReentrantLock(true);
        this.notEmpty = this.lock.newCondition();
        this.q = new PriorityQueue(initialCapacity, null);
    }
    
    public PriorityBlockingQueue(final int initialCapacity, final Comparator comparator) {
        this.lock = new ReentrantLock(true);
        this.notEmpty = this.lock.newCondition();
        this.q = new PriorityQueue(initialCapacity, comparator);
    }
    
    public PriorityBlockingQueue(final Collection c) {
        this.lock = new ReentrantLock(true);
        this.notEmpty = this.lock.newCondition();
        this.q = new PriorityQueue(c);
    }
    
    public boolean add(final Object e) {
        return this.offer(e);
    }
    
    public boolean offer(final Object e) {
        final ReentrantLock lock = this.lock;
        lock.lock();
        try {
            final boolean ok = this.q.offer(e);
            assert ok;
            this.notEmpty.signal();
            return true;
        }
        finally {
            lock.unlock();
        }
    }
    
    public void put(final Object e) {
        this.offer(e);
    }
    
    public boolean offer(final Object e, final long timeout, final TimeUnit unit) {
        return this.offer(e);
    }
    
    public Object poll() {
        final ReentrantLock lock = this.lock;
        lock.lock();
        try {
            return this.q.poll();
        }
        finally {
            lock.unlock();
        }
    }
    
    public Object take() throws InterruptedException {
        final ReentrantLock lock = this.lock;
        lock.lockInterruptibly();
        try {
            try {
                while (this.q.size() == 0) {
                    this.notEmpty.await();
                }
            }
            catch (InterruptedException ie) {
                this.notEmpty.signal();
                throw ie;
            }
            final Object x = this.q.poll();
            assert x != null;
            return x;
        }
        finally {
            lock.unlock();
        }
    }
    
    public Object poll(final long timeout, final TimeUnit unit) throws InterruptedException {
        long nanos = unit.toNanos(timeout);
        final ReentrantLock lock = this.lock;
        lock.lockInterruptibly();
        try {
            final long deadline = Utils.nanoTime() + nanos;
            while (true) {
                final Object x = this.q.poll();
                if (x != null) {
                    return x;
                }
                if (nanos <= 0L) {
                    return null;
                }
                try {
                    this.notEmpty.await(nanos, TimeUnit.NANOSECONDS);
                    nanos = deadline - Utils.nanoTime();
                }
                catch (InterruptedException ie) {
                    this.notEmpty.signal();
                    throw ie;
                }
            }
        }
        finally {
            lock.unlock();
        }
    }
    
    public Object peek() {
        final ReentrantLock lock = this.lock;
        lock.lock();
        try {
            return this.q.peek();
        }
        finally {
            lock.unlock();
        }
    }
    
    public Comparator comparator() {
        return this.q.comparator();
    }
    
    public int size() {
        final ReentrantLock lock = this.lock;
        lock.lock();
        try {
            return this.q.size();
        }
        finally {
            lock.unlock();
        }
    }
    
    public int remainingCapacity() {
        return Integer.MAX_VALUE;
    }
    
    public boolean remove(final Object o) {
        final ReentrantLock lock = this.lock;
        lock.lock();
        try {
            return this.q.remove(o);
        }
        finally {
            lock.unlock();
        }
    }
    
    public boolean contains(final Object o) {
        final ReentrantLock lock = this.lock;
        lock.lock();
        try {
            return this.q.contains(o);
        }
        finally {
            lock.unlock();
        }
    }
    
    public Object[] toArray() {
        final ReentrantLock lock = this.lock;
        lock.lock();
        try {
            return this.q.toArray();
        }
        finally {
            lock.unlock();
        }
    }
    
    public String toString() {
        final ReentrantLock lock = this.lock;
        lock.lock();
        try {
            return this.q.toString();
        }
        finally {
            lock.unlock();
        }
    }
    
    public int drainTo(final Collection c) {
        if (c == null) {
            throw new NullPointerException();
        }
        if (c == this) {
            throw new IllegalArgumentException();
        }
        final ReentrantLock lock = this.lock;
        lock.lock();
        try {
            int n = 0;
            Object e;
            while ((e = this.q.poll()) != null) {
                c.add(e);
                ++n;
            }
            return n;
        }
        finally {
            lock.unlock();
        }
    }
    
    public int drainTo(final Collection c, final int maxElements) {
        if (c == null) {
            throw new NullPointerException();
        }
        if (c == this) {
            throw new IllegalArgumentException();
        }
        if (maxElements <= 0) {
            return 0;
        }
        final ReentrantLock lock = this.lock;
        lock.lock();
        try {
            int n;
            Object e;
            for (n = 0; n < maxElements && (e = this.q.poll()) != null; ++n) {
                c.add(e);
            }
            return n;
        }
        finally {
            lock.unlock();
        }
    }
    
    public void clear() {
        final ReentrantLock lock = this.lock;
        lock.lock();
        try {
            this.q.clear();
        }
        finally {
            lock.unlock();
        }
    }
    
    public Object[] toArray(final Object[] a) {
        final ReentrantLock lock = this.lock;
        lock.lock();
        try {
            return this.q.toArray(a);
        }
        finally {
            lock.unlock();
        }
    }
    
    public Iterator iterator() {
        return new Itr(this.toArray());
    }
    
    private void writeObject(final ObjectOutputStream s) throws IOException {
        this.lock.lock();
        try {
            s.defaultWriteObject();
        }
        finally {
            this.lock.unlock();
        }
    }
    
    private class Itr implements Iterator
    {
        final Object[] array;
        int cursor;
        int lastRet;
        
        Itr(final Object[] array) {
            this.lastRet = -1;
            this.array = array;
        }
        
        public boolean hasNext() {
            return this.cursor < this.array.length;
        }
        
        public Object next() {
            if (this.cursor >= this.array.length) {
                throw new NoSuchElementException();
            }
            this.lastRet = this.cursor;
            return this.array[this.cursor++];
        }
        
        public void remove() {
            if (this.lastRet < 0) {
                throw new IllegalStateException();
            }
            final Object x = this.array[this.lastRet];
            this.lastRet = -1;
            PriorityBlockingQueue.this.lock.lock();
            try {
                final Iterator it = PriorityBlockingQueue.this.q.iterator();
                while (it.hasNext()) {
                    if (it.next() == x) {
                        it.remove();
                    }
                }
            }
            finally {
                PriorityBlockingQueue.this.lock.unlock();
            }
        }
    }
}
