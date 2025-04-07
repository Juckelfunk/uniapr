// 
// Decompiled by Procyon v0.5.36
// 

package edu.emory.mathcs.backport.java.util.concurrent;

import java.util.NoSuchElementException;
import java.util.Iterator;
import edu.emory.mathcs.backport.java.util.concurrent.helpers.Utils;
import java.util.Collection;
import edu.emory.mathcs.backport.java.util.PriorityQueue;
import edu.emory.mathcs.backport.java.util.AbstractQueue;

public class DelayQueue extends AbstractQueue implements BlockingQueue
{
    private final transient Object lock;
    private final PriorityQueue q;
    
    public DelayQueue() {
        this.lock = new Object();
        this.q = new PriorityQueue();
    }
    
    public DelayQueue(final Collection c) {
        this.lock = new Object();
        this.q = new PriorityQueue();
        this.addAll(c);
    }
    
    public boolean add(final Object e) {
        return this.offer(e);
    }
    
    public boolean offer(final Object e) {
        synchronized (this.lock) {
            final Object first = this.q.peek();
            this.q.offer(e);
            if (first == null || ((Delayed)e).compareTo(first) < 0) {
                this.lock.notifyAll();
            }
            return true;
        }
    }
    
    public void put(final Object e) {
        this.offer(e);
    }
    
    public boolean offer(final Object e, final long timeout, final TimeUnit unit) {
        return this.offer(e);
    }
    
    public Object poll() {
        synchronized (this.lock) {
            final Object first = this.q.peek();
            if (first == null || ((Delayed)first).getDelay(TimeUnit.NANOSECONDS) > 0L) {
                return null;
            }
            final Object x = this.q.poll();
            assert x != null;
            if (this.q.size() != 0) {
                this.lock.notifyAll();
            }
            return x;
        }
    }
    
    public Object take() throws InterruptedException {
        synchronized (this.lock) {
            while (true) {
                final Object first = this.q.peek();
                if (first == null) {
                    this.lock.wait();
                }
                else {
                    final long delay = ((Delayed)first).getDelay(TimeUnit.NANOSECONDS);
                    if (delay <= 0L) {
                        break;
                    }
                    TimeUnit.NANOSECONDS.timedWait(this.lock, delay);
                }
            }
            final Object x = this.q.poll();
            assert x != null;
            if (this.q.size() != 0) {
                this.lock.notifyAll();
            }
            return x;
        }
    }
    
    public Object poll(final long timeout, final TimeUnit unit) throws InterruptedException {
        long nanos = unit.toNanos(timeout);
        final long deadline = Utils.nanoTime() + nanos;
        synchronized (this.lock) {
            while (true) {
                final Object first = this.q.peek();
                if (first == null) {
                    if (nanos <= 0L) {
                        return null;
                    }
                    TimeUnit.NANOSECONDS.timedWait(this.lock, nanos);
                    nanos = deadline - Utils.nanoTime();
                }
                else {
                    long delay = ((Delayed)first).getDelay(TimeUnit.NANOSECONDS);
                    if (delay > 0L) {
                        if (nanos <= 0L) {
                            return null;
                        }
                        if (delay > nanos) {
                            delay = nanos;
                        }
                        TimeUnit.NANOSECONDS.timedWait(this.lock, delay);
                        nanos = deadline - Utils.nanoTime();
                    }
                    else {
                        final Object x = this.q.poll();
                        assert x != null;
                        if (this.q.size() != 0) {
                            this.lock.notifyAll();
                        }
                        return x;
                    }
                }
            }
        }
    }
    
    public Object peek() {
        synchronized (this.lock) {
            return this.q.peek();
        }
    }
    
    public int size() {
        synchronized (this.lock) {
            return this.q.size();
        }
    }
    
    public int drainTo(final Collection c) {
        if (c == null) {
            throw new NullPointerException();
        }
        if (c == this) {
            throw new IllegalArgumentException();
        }
        synchronized (this.lock) {
            int n = 0;
            while (true) {
                final Object first = this.q.peek();
                if (first == null || ((Delayed)first).getDelay(TimeUnit.NANOSECONDS) > 0L) {
                    break;
                }
                c.add(this.q.poll());
                ++n;
            }
            if (n > 0) {
                this.lock.notifyAll();
            }
            return n;
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
        synchronized (this.lock) {
            int n;
            for (n = 0; n < maxElements; ++n) {
                final Object first = this.q.peek();
                if (first == null) {
                    break;
                }
                if (((Delayed)first).getDelay(TimeUnit.NANOSECONDS) > 0L) {
                    break;
                }
                c.add(this.q.poll());
            }
            if (n > 0) {
                this.lock.notifyAll();
            }
            return n;
        }
    }
    
    public void clear() {
        synchronized (this.lock) {
            this.q.clear();
        }
    }
    
    public int remainingCapacity() {
        return Integer.MAX_VALUE;
    }
    
    public Object[] toArray() {
        synchronized (this.lock) {
            return this.q.toArray();
        }
    }
    
    public Object[] toArray(final Object[] a) {
        synchronized (this.lock) {
            return this.q.toArray(a);
        }
    }
    
    public boolean remove(final Object o) {
        synchronized (this.lock) {
            return this.q.remove(o);
        }
    }
    
    public Iterator iterator() {
        return new Itr(this.toArray());
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
            synchronized (DelayQueue.this.lock) {
                final Iterator it = DelayQueue.this.q.iterator();
                while (it.hasNext()) {
                    if (it.next() == x) {
                        it.remove();
                    }
                }
            }
        }
    }
}
