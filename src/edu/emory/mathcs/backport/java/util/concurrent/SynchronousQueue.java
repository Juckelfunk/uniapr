// 
// Decompiled by Procyon v0.5.36
// 

package edu.emory.mathcs.backport.java.util.concurrent;

import java.util.NoSuchElementException;
import edu.emory.mathcs.backport.java.util.concurrent.helpers.Utils;
import java.util.Iterator;
import java.util.Collection;
import edu.emory.mathcs.backport.java.util.concurrent.locks.ReentrantLock;
import java.io.Serializable;
import edu.emory.mathcs.backport.java.util.AbstractQueue;

public class SynchronousQueue extends AbstractQueue implements BlockingQueue, Serializable
{
    private static final long serialVersionUID = -3223113410248163686L;
    private final ReentrantLock qlock;
    private final WaitQueue waitingProducers;
    private final WaitQueue waitingConsumers;
    
    public SynchronousQueue() {
        this(false);
    }
    
    public SynchronousQueue(final boolean fair) {
        if (fair) {
            this.qlock = new ReentrantLock(true);
            this.waitingProducers = new FifoWaitQueue();
            this.waitingConsumers = new FifoWaitQueue();
        }
        else {
            this.qlock = new ReentrantLock();
            this.waitingProducers = new LifoWaitQueue();
            this.waitingConsumers = new LifoWaitQueue();
        }
    }
    
    private void unlinkCancelledConsumer(final Node node) {
        if (this.waitingConsumers.shouldUnlink(node)) {
            this.qlock.lock();
            try {
                if (this.waitingConsumers.shouldUnlink(node)) {
                    this.waitingConsumers.unlink(node);
                }
            }
            finally {
                this.qlock.unlock();
            }
        }
    }
    
    private void unlinkCancelledProducer(final Node node) {
        if (this.waitingProducers.shouldUnlink(node)) {
            this.qlock.lock();
            try {
                if (this.waitingProducers.shouldUnlink(node)) {
                    this.waitingProducers.unlink(node);
                }
            }
            finally {
                this.qlock.unlock();
            }
        }
    }
    
    public void put(final Object e) throws InterruptedException {
        if (e == null) {
            throw new NullPointerException();
        }
        final ReentrantLock qlock = this.qlock;
        while (!Thread.interrupted()) {
            qlock.lock();
            Node node;
            boolean mustWait;
            try {
                node = this.waitingConsumers.deq();
                if (mustWait = (node == null)) {
                    node = this.waitingProducers.enq(e);
                }
            }
            finally {
                qlock.unlock();
            }
            if (mustWait) {
                try {
                    node.waitForTake();
                    return;
                }
                catch (InterruptedException ex) {
                    this.unlinkCancelledProducer(node);
                    throw ex;
                }
            }
            if (node.setItem(e)) {
                return;
            }
        }
        throw new InterruptedException();
    }
    
    public boolean offer(final Object e, final long timeout, final TimeUnit unit) throws InterruptedException {
        if (e == null) {
            throw new NullPointerException();
        }
        final long nanos = unit.toNanos(timeout);
        final ReentrantLock qlock = this.qlock;
        while (!Thread.interrupted()) {
            qlock.lock();
            Node node;
            boolean mustWait;
            try {
                node = this.waitingConsumers.deq();
                if (mustWait = (node == null)) {
                    node = this.waitingProducers.enq(e);
                }
            }
            finally {
                qlock.unlock();
            }
            if (mustWait) {
                try {
                    final boolean x = node.waitForTake(nanos);
                    if (!x) {
                        this.unlinkCancelledProducer(node);
                    }
                    return x;
                }
                catch (InterruptedException ex) {
                    this.unlinkCancelledProducer(node);
                    throw ex;
                }
            }
            if (node.setItem(e)) {
                return true;
            }
        }
        throw new InterruptedException();
    }
    
    public Object take() throws InterruptedException {
        final ReentrantLock qlock = this.qlock;
        while (!Thread.interrupted()) {
            qlock.lock();
            Node node;
            boolean mustWait;
            try {
                node = this.waitingProducers.deq();
                if (mustWait = (node == null)) {
                    node = this.waitingConsumers.enq(null);
                }
            }
            finally {
                qlock.unlock();
            }
            if (mustWait) {
                try {
                    final Object x = node.waitForPut();
                    return x;
                }
                catch (InterruptedException ex) {
                    this.unlinkCancelledConsumer(node);
                    throw ex;
                }
            }
            final Object x = node.getItem();
            if (x != null) {
                return x;
            }
        }
        throw new InterruptedException();
    }
    
    public Object poll(final long timeout, final TimeUnit unit) throws InterruptedException {
        final long nanos = unit.toNanos(timeout);
        final ReentrantLock qlock = this.qlock;
        while (!Thread.interrupted()) {
            qlock.lock();
            Node node;
            boolean mustWait;
            try {
                node = this.waitingProducers.deq();
                if (mustWait = (node == null)) {
                    node = this.waitingConsumers.enq(null);
                }
            }
            finally {
                qlock.unlock();
            }
            if (mustWait) {
                try {
                    final Object x = node.waitForPut(nanos);
                    if (x == null) {
                        this.unlinkCancelledConsumer(node);
                    }
                    return x;
                }
                catch (InterruptedException ex) {
                    this.unlinkCancelledConsumer(node);
                    throw ex;
                }
            }
            final Object x = node.getItem();
            if (x != null) {
                return x;
            }
        }
        throw new InterruptedException();
    }
    
    public boolean offer(final Object e) {
        if (e == null) {
            throw new NullPointerException();
        }
        final ReentrantLock qlock = this.qlock;
        while (true) {
            qlock.lock();
            Node node;
            try {
                node = this.waitingConsumers.deq();
            }
            finally {
                qlock.unlock();
            }
            if (node == null) {
                return false;
            }
            if (node.setItem(e)) {
                return true;
            }
        }
    }
    
    public Object poll() {
        final ReentrantLock qlock = this.qlock;
        while (true) {
            qlock.lock();
            Node node;
            try {
                node = this.waitingProducers.deq();
            }
            finally {
                qlock.unlock();
            }
            if (node == null) {
                return null;
            }
            final Object x = node.getItem();
            if (x != null) {
                return x;
            }
        }
    }
    
    public boolean isEmpty() {
        return true;
    }
    
    public int size() {
        return 0;
    }
    
    public int remainingCapacity() {
        return 0;
    }
    
    public void clear() {
    }
    
    public boolean contains(final Object o) {
        return false;
    }
    
    public boolean remove(final Object o) {
        return false;
    }
    
    public boolean containsAll(final Collection c) {
        return c.isEmpty();
    }
    
    public boolean removeAll(final Collection c) {
        return false;
    }
    
    public boolean retainAll(final Collection c) {
        return false;
    }
    
    public Object peek() {
        return null;
    }
    
    public Iterator iterator() {
        return new EmptyIterator();
    }
    
    public Object[] toArray() {
        return new Object[0];
    }
    
    public Object[] toArray(final Object[] a) {
        if (a.length > 0) {
            a[0] = null;
        }
        return a;
    }
    
    public int drainTo(final Collection c) {
        if (c == null) {
            throw new NullPointerException();
        }
        if (c == this) {
            throw new IllegalArgumentException();
        }
        int n = 0;
        Object e;
        while ((e = this.poll()) != null) {
            c.add(e);
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
        int n;
        Object e;
        for (n = 0; n < maxElements && (e = this.poll()) != null; ++n) {
            c.add(e);
        }
        return n;
    }
    
    abstract static class WaitQueue implements Serializable
    {
        abstract Node enq(final Object p0);
        
        abstract Node deq();
        
        abstract void unlink(final Node p0);
        
        abstract boolean shouldUnlink(final Node p0);
    }
    
    static final class FifoWaitQueue extends WaitQueue implements Serializable
    {
        private static final long serialVersionUID = -3623113410248163686L;
        private transient Node head;
        private transient Node last;
        
        Node enq(final Object x) {
            final Node p = new Node(x);
            if (this.last == null) {
                final Node node = p;
                this.head = node;
                this.last = node;
            }
            else {
                final Node last = this.last;
                final Node node2 = p;
                last.next = node2;
                this.last = node2;
            }
            return p;
        }
        
        Node deq() {
            final Node p = this.head;
            if (p != null) {
                if ((this.head = p.next) == null) {
                    this.last = null;
                }
                p.next = null;
            }
            return p;
        }
        
        boolean shouldUnlink(final Node node) {
            return node == this.last || node.next != null;
        }
        
        void unlink(final Node node) {
            Node p = this.head;
            Node trail = null;
            while (p != null) {
                if (p == node) {
                    final Node next = p.next;
                    if (trail == null) {
                        this.head = next;
                    }
                    else {
                        trail.next = next;
                    }
                    if (this.last == node) {
                        this.last = trail;
                        break;
                    }
                    break;
                }
                else {
                    trail = p;
                    p = p.next;
                }
            }
        }
    }
    
    static final class LifoWaitQueue extends WaitQueue implements Serializable
    {
        private static final long serialVersionUID = -3633113410248163686L;
        private transient Node head;
        
        Node enq(final Object x) {
            return this.head = new Node(x, this.head);
        }
        
        Node deq() {
            final Node p = this.head;
            if (p != null) {
                this.head = p.next;
                p.next = null;
            }
            return p;
        }
        
        boolean shouldUnlink(final Node node) {
            return node == this.head || node.next != null;
        }
        
        void unlink(final Node node) {
            Node p = this.head;
            Node trail = null;
            while (p != null) {
                if (p == node) {
                    final Node next = p.next;
                    if (trail == null) {
                        this.head = next;
                        break;
                    }
                    trail.next = next;
                    break;
                }
                else {
                    trail = p;
                    p = p.next;
                }
            }
        }
    }
    
    static final class Node implements Serializable
    {
        private static final long serialVersionUID = -3223113410248163686L;
        private static final int ACK = 1;
        private static final int CANCEL = -1;
        int state;
        Object item;
        Node next;
        
        Node(final Object x) {
            this.state = 0;
            this.item = x;
        }
        
        Node(final Object x, final Node n) {
            this.state = 0;
            this.item = x;
            this.next = n;
        }
        
        private Object extract() {
            final Object x = this.item;
            this.item = null;
            return x;
        }
        
        private void checkCancellationOnInterrupt(final InterruptedException ie) throws InterruptedException {
            if (this.state == 0) {
                this.state = -1;
                this.notify();
                throw ie;
            }
            Thread.currentThread().interrupt();
        }
        
        synchronized boolean setItem(final Object x) {
            if (this.state != 0) {
                return false;
            }
            this.item = x;
            this.state = 1;
            this.notify();
            return true;
        }
        
        synchronized Object getItem() {
            if (this.state != 0) {
                return null;
            }
            this.state = 1;
            this.notify();
            return this.extract();
        }
        
        synchronized void waitForTake() throws InterruptedException {
            try {
                while (this.state == 0) {
                    this.wait();
                }
            }
            catch (InterruptedException ie) {
                this.checkCancellationOnInterrupt(ie);
            }
        }
        
        synchronized Object waitForPut() throws InterruptedException {
            try {
                while (this.state == 0) {
                    this.wait();
                }
            }
            catch (InterruptedException ie) {
                this.checkCancellationOnInterrupt(ie);
            }
            return this.extract();
        }
        
        private boolean attempt(long nanos) throws InterruptedException {
            if (this.state != 0) {
                return true;
            }
            if (nanos <= 0L) {
                this.state = -1;
                this.notify();
                return false;
            }
            final long deadline = Utils.nanoTime() + nanos;
            do {
                TimeUnit.NANOSECONDS.timedWait(this, nanos);
                if (this.state != 0) {
                    return true;
                }
                nanos = deadline - Utils.nanoTime();
            } while (nanos > 0L);
            this.state = -1;
            this.notify();
            return false;
        }
        
        synchronized boolean waitForTake(final long nanos) throws InterruptedException {
            try {
                if (!this.attempt(nanos)) {
                    return false;
                }
            }
            catch (InterruptedException ie) {
                this.checkCancellationOnInterrupt(ie);
            }
            return true;
        }
        
        synchronized Object waitForPut(final long nanos) throws InterruptedException {
            try {
                if (!this.attempt(nanos)) {
                    return null;
                }
            }
            catch (InterruptedException ie) {
                this.checkCancellationOnInterrupt(ie);
            }
            return this.extract();
        }
    }
    
    static class EmptyIterator implements Iterator
    {
        public boolean hasNext() {
            return false;
        }
        
        public Object next() {
            throw new NoSuchElementException();
        }
        
        public void remove() {
            throw new IllegalStateException();
        }
    }
}
