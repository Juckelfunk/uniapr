// 
// Decompiled by Procyon v0.5.36
// 

package edu.emory.mathcs.backport.java.util.concurrent;

import java.util.NoSuchElementException;
import java.lang.reflect.Array;
import edu.emory.mathcs.backport.java.util.concurrent.helpers.Utils;
import java.util.Iterator;
import java.util.Collection;
import edu.emory.mathcs.backport.java.util.concurrent.locks.Condition;
import edu.emory.mathcs.backport.java.util.concurrent.locks.ReentrantLock;
import java.io.Serializable;
import edu.emory.mathcs.backport.java.util.AbstractQueue;

public class ArrayBlockingQueue extends AbstractQueue implements BlockingQueue, Serializable
{
    private static final long serialVersionUID = -817911632652898426L;
    private final Object[] items;
    private int takeIndex;
    private int putIndex;
    private int count;
    private final ReentrantLock lock;
    private final Condition notEmpty;
    private final Condition notFull;
    
    final int inc(int i) {
        return (++i == this.items.length) ? 0 : i;
    }
    
    private void insert(final Object x) {
        this.items[this.putIndex] = x;
        this.putIndex = this.inc(this.putIndex);
        ++this.count;
        this.notEmpty.signal();
    }
    
    private Object extract() {
        final Object[] items = this.items;
        final Object x = items[this.takeIndex];
        items[this.takeIndex] = null;
        this.takeIndex = this.inc(this.takeIndex);
        --this.count;
        this.notFull.signal();
        return x;
    }
    
    void removeAt(int i) {
        final Object[] items = this.items;
        if (i == this.takeIndex) {
            items[this.takeIndex] = null;
            this.takeIndex = this.inc(this.takeIndex);
        }
        else {
            while (true) {
                final int nexti = this.inc(i);
                if (nexti == this.putIndex) {
                    break;
                }
                items[i] = items[nexti];
                i = nexti;
            }
            items[i] = null;
            this.putIndex = i;
        }
        --this.count;
        this.notFull.signal();
    }
    
    public ArrayBlockingQueue(final int capacity) {
        this(capacity, false);
    }
    
    public ArrayBlockingQueue(final int capacity, final boolean fair) {
        if (capacity <= 0) {
            throw new IllegalArgumentException();
        }
        this.items = new Object[capacity];
        this.lock = new ReentrantLock(fair);
        this.notEmpty = this.lock.newCondition();
        this.notFull = this.lock.newCondition();
    }
    
    public ArrayBlockingQueue(final int capacity, final boolean fair, final Collection c) {
        this(capacity, fair);
        if (capacity < c.size()) {
            throw new IllegalArgumentException();
        }
        final Iterator it = c.iterator();
        while (it.hasNext()) {
            this.add(it.next());
        }
    }
    
    public boolean add(final Object e) {
        return super.add(e);
    }
    
    public boolean offer(final Object e) {
        if (e == null) {
            throw new NullPointerException();
        }
        final ReentrantLock lock = this.lock;
        lock.lock();
        try {
            if (this.count == this.items.length) {
                return false;
            }
            this.insert(e);
            return true;
        }
        finally {
            lock.unlock();
        }
    }
    
    public void put(final Object e) throws InterruptedException {
        if (e == null) {
            throw new NullPointerException();
        }
        final Object[] items = this.items;
        final ReentrantLock lock = this.lock;
        lock.lockInterruptibly();
        try {
            try {
                while (this.count == items.length) {
                    this.notFull.await();
                }
            }
            catch (InterruptedException ie) {
                this.notFull.signal();
                throw ie;
            }
            this.insert(e);
        }
        finally {
            lock.unlock();
        }
    }
    
    public boolean offer(final Object e, final long timeout, final TimeUnit unit) throws InterruptedException {
        if (e == null) {
            throw new NullPointerException();
        }
        long nanos = unit.toNanos(timeout);
        final ReentrantLock lock = this.lock;
        lock.lockInterruptibly();
        try {
            final long deadline = Utils.nanoTime() + nanos;
            while (this.count == this.items.length) {
                if (nanos <= 0L) {
                    return false;
                }
                try {
                    this.notFull.await(nanos, TimeUnit.NANOSECONDS);
                    nanos = deadline - Utils.nanoTime();
                }
                catch (InterruptedException ie) {
                    this.notFull.signal();
                    throw ie;
                }
            }
            this.insert(e);
            return true;
        }
        finally {
            lock.unlock();
        }
    }
    
    public Object poll() {
        final ReentrantLock lock = this.lock;
        lock.lock();
        try {
            if (this.count == 0) {
                return null;
            }
            final Object x = this.extract();
            return x;
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
                while (this.count == 0) {
                    this.notEmpty.await();
                }
            }
            catch (InterruptedException ie) {
                this.notEmpty.signal();
                throw ie;
            }
            final Object x = this.extract();
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
            while (this.count == 0) {
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
            final Object x = this.extract();
            return x;
        }
        finally {
            lock.unlock();
        }
    }
    
    public Object peek() {
        final ReentrantLock lock = this.lock;
        lock.lock();
        try {
            return (this.count == 0) ? null : this.items[this.takeIndex];
        }
        finally {
            lock.unlock();
        }
    }
    
    public int size() {
        final ReentrantLock lock = this.lock;
        lock.lock();
        try {
            return this.count;
        }
        finally {
            lock.unlock();
        }
    }
    
    public int remainingCapacity() {
        final ReentrantLock lock = this.lock;
        lock.lock();
        try {
            return this.items.length - this.count;
        }
        finally {
            lock.unlock();
        }
    }
    
    public boolean remove(final Object o) {
        if (o == null) {
            return false;
        }
        final Object[] items = this.items;
        final ReentrantLock lock = this.lock;
        lock.lock();
        try {
            int i = this.takeIndex;
            int k = 0;
            while (k++ < this.count) {
                if (o.equals(items[i])) {
                    this.removeAt(i);
                    return true;
                }
                i = this.inc(i);
            }
            return false;
        }
        finally {
            lock.unlock();
        }
    }
    
    public boolean contains(final Object o) {
        if (o == null) {
            return false;
        }
        final Object[] items = this.items;
        final ReentrantLock lock = this.lock;
        lock.lock();
        try {
            int i = this.takeIndex;
            int k = 0;
            while (k++ < this.count) {
                if (o.equals(items[i])) {
                    return true;
                }
                i = this.inc(i);
            }
            return false;
        }
        finally {
            lock.unlock();
        }
    }
    
    public Object[] toArray() {
        final Object[] items = this.items;
        final ReentrantLock lock = this.lock;
        lock.lock();
        try {
            final Object[] a = new Object[this.count];
            for (int k = 0, i = this.takeIndex; k < this.count; a[k++] = items[i], i = this.inc(i)) {}
            return a;
        }
        finally {
            lock.unlock();
        }
    }
    
    public Object[] toArray(Object[] a) {
        final Object[] items = this.items;
        final ReentrantLock lock = this.lock;
        lock.lock();
        try {
            if (a.length < this.count) {
                a = (Object[])Array.newInstance(a.getClass().getComponentType(), this.count);
            }
            for (int k = 0, i = this.takeIndex; k < this.count; a[k++] = items[i], i = this.inc(i)) {}
            if (a.length > this.count) {
                a[this.count] = null;
            }
            return a;
        }
        finally {
            lock.unlock();
        }
    }
    
    public String toString() {
        final ReentrantLock lock = this.lock;
        lock.lock();
        try {
            return super.toString();
        }
        finally {
            lock.unlock();
        }
    }
    
    public void clear() {
        final Object[] items = this.items;
        final ReentrantLock lock = this.lock;
        lock.lock();
        try {
            int i = this.takeIndex;
            int k = this.count;
            while (k-- > 0) {
                items[i] = null;
                i = this.inc(i);
            }
            this.count = 0;
            this.putIndex = 0;
            this.takeIndex = 0;
            this.notFull.signalAll();
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
        final Object[] items = this.items;
        final ReentrantLock lock = this.lock;
        lock.lock();
        try {
            int i = this.takeIndex;
            int n = 0;
            for (int max = this.count; n < max; ++n) {
                c.add(items[i]);
                items[i] = null;
                i = this.inc(i);
            }
            if (n > 0) {
                this.count = 0;
                this.putIndex = 0;
                this.takeIndex = 0;
                this.notFull.signalAll();
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
        final Object[] items = this.items;
        final ReentrantLock lock = this.lock;
        lock.lock();
        try {
            int i = this.takeIndex;
            int n = 0;
            final int sz = this.count;
            for (int max = (maxElements < this.count) ? maxElements : this.count; n < max; ++n) {
                c.add(items[i]);
                items[i] = null;
                i = this.inc(i);
            }
            if (n > 0) {
                this.count -= n;
                this.takeIndex = i;
                this.notFull.signalAll();
            }
            return n;
        }
        finally {
            lock.unlock();
        }
    }
    
    public Iterator iterator() {
        final ReentrantLock lock = this.lock;
        lock.lock();
        try {
            return new Itr();
        }
        finally {
            lock.unlock();
        }
    }
    
    private class Itr implements Iterator
    {
        private int nextIndex;
        private Object nextItem;
        private int lastRet;
        
        Itr() {
            this.lastRet = -1;
            if (ArrayBlockingQueue.this.count == 0) {
                this.nextIndex = -1;
            }
            else {
                this.nextIndex = ArrayBlockingQueue.this.takeIndex;
                this.nextItem = ArrayBlockingQueue.this.items[ArrayBlockingQueue.this.takeIndex];
            }
        }
        
        public boolean hasNext() {
            return this.nextIndex >= 0;
        }
        
        private void checkNext() {
            if (this.nextIndex == ArrayBlockingQueue.this.putIndex) {
                this.nextIndex = -1;
                this.nextItem = null;
            }
            else {
                this.nextItem = ArrayBlockingQueue.this.items[this.nextIndex];
                if (this.nextItem == null) {
                    this.nextIndex = -1;
                }
            }
        }
        
        public Object next() {
            final ReentrantLock lock = ArrayBlockingQueue.this.lock;
            lock.lock();
            try {
                if (this.nextIndex < 0) {
                    throw new NoSuchElementException();
                }
                this.lastRet = this.nextIndex;
                final Object x = this.nextItem;
                this.nextIndex = ArrayBlockingQueue.this.inc(this.nextIndex);
                this.checkNext();
                return x;
            }
            finally {
                lock.unlock();
            }
        }
        
        public void remove() {
            final ReentrantLock lock = ArrayBlockingQueue.this.lock;
            lock.lock();
            try {
                final int i = this.lastRet;
                if (i == -1) {
                    throw new IllegalStateException();
                }
                this.lastRet = -1;
                final int ti = ArrayBlockingQueue.this.takeIndex;
                ArrayBlockingQueue.this.removeAt(i);
                this.nextIndex = ((i == ti) ? ArrayBlockingQueue.this.takeIndex : i);
                this.checkNext();
            }
            finally {
                lock.unlock();
            }
        }
    }
}
