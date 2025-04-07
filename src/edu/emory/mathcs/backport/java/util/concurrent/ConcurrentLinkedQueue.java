// 
// Decompiled by Procyon v0.5.36
// 

package edu.emory.mathcs.backport.java.util.concurrent;

import java.util.NoSuchElementException;
import java.io.ObjectInputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.util.Iterator;
import java.util.Collection;
import java.io.Serializable;
import edu.emory.mathcs.backport.java.util.Queue;
import edu.emory.mathcs.backport.java.util.AbstractQueue;

public class ConcurrentLinkedQueue extends AbstractQueue implements Queue, Serializable
{
    private static final long serialVersionUID = 196745693267521676L;
    private final Object headLock;
    private final Object tailLock;
    private transient volatile Node head;
    private transient volatile Node tail;
    
    private boolean casTail(final Node cmp, final Node val) {
        synchronized (this.tailLock) {
            if (this.tail == cmp) {
                this.tail = val;
                return true;
            }
            return false;
        }
    }
    
    private boolean casHead(final Node cmp, final Node val) {
        synchronized (this.headLock) {
            if (this.head == cmp) {
                this.head = val;
                return true;
            }
            return false;
        }
    }
    
    public ConcurrentLinkedQueue() {
        this.headLock = new SerializableLock();
        this.tailLock = new SerializableLock();
        this.head = new Node(null, null);
        this.tail = this.head;
    }
    
    public ConcurrentLinkedQueue(final Collection c) {
        this.headLock = new SerializableLock();
        this.tailLock = new SerializableLock();
        this.head = new Node(null, null);
        this.tail = this.head;
        final Iterator it = c.iterator();
        while (it.hasNext()) {
            this.add(it.next());
        }
    }
    
    public boolean add(final Object e) {
        return this.offer(e);
    }
    
    public boolean offer(final Object e) {
        if (e == null) {
            throw new NullPointerException();
        }
        final Node n = new Node(e, null);
        Node t;
        while (true) {
            t = this.tail;
            final Node s = t.getNext();
            if (t == this.tail) {
                if (s == null) {
                    if (t.casNext(s, n)) {
                        break;
                    }
                    continue;
                }
                else {
                    this.casTail(t, s);
                }
            }
        }
        this.casTail(t, n);
        return true;
    }
    
    public Object poll() {
        while (true) {
            final Node h = this.head;
            final Node t = this.tail;
            final Node first = h.getNext();
            if (h == this.head) {
                if (h == t) {
                    if (first == null) {
                        return null;
                    }
                    this.casTail(t, first);
                }
                else {
                    if (!this.casHead(h, first)) {
                        continue;
                    }
                    final Object item = first.getItem();
                    if (item != null) {
                        first.setItem(null);
                        return item;
                    }
                    continue;
                }
            }
        }
    }
    
    public Object peek() {
        while (true) {
            final Node h = this.head;
            final Node t = this.tail;
            final Node first = h.getNext();
            if (h == this.head) {
                if (h == t) {
                    if (first == null) {
                        return null;
                    }
                    this.casTail(t, first);
                }
                else {
                    final Object item = first.getItem();
                    if (item != null) {
                        return item;
                    }
                    this.casHead(h, first);
                }
            }
        }
    }
    
    Node first() {
        while (true) {
            final Node h = this.head;
            final Node t = this.tail;
            final Node first = h.getNext();
            if (h == this.head) {
                if (h == t) {
                    if (first == null) {
                        return null;
                    }
                    this.casTail(t, first);
                }
                else {
                    if (first.getItem() != null) {
                        return first;
                    }
                    this.casHead(h, first);
                }
            }
        }
    }
    
    public boolean isEmpty() {
        return this.first() == null;
    }
    
    public int size() {
        int count = 0;
        for (Node p = this.first(); p != null && (p.getItem() == null || ++count != Integer.MAX_VALUE); p = p.getNext()) {}
        return count;
    }
    
    public boolean contains(final Object o) {
        if (o == null) {
            return false;
        }
        for (Node p = this.first(); p != null; p = p.getNext()) {
            final Object item = p.getItem();
            if (item != null && o.equals(item)) {
                return true;
            }
        }
        return false;
    }
    
    public boolean remove(final Object o) {
        if (o == null) {
            return false;
        }
        for (Node p = this.first(); p != null; p = p.getNext()) {
            final Object item = p.getItem();
            if (item != null && o.equals(item) && p.casItem(item, null)) {
                return true;
            }
        }
        return false;
    }
    
    public Iterator iterator() {
        return new Itr();
    }
    
    private void writeObject(final ObjectOutputStream s) throws IOException {
        s.defaultWriteObject();
        for (Node p = this.first(); p != null; p = p.getNext()) {
            final Object item = p.getItem();
            if (item != null) {
                s.writeObject(item);
            }
        }
        s.writeObject(null);
    }
    
    private void readObject(final ObjectInputStream s) throws IOException, ClassNotFoundException {
        s.defaultReadObject();
        this.head = new Node(null, null);
        this.tail = this.head;
        while (true) {
            final Object item = s.readObject();
            if (item == null) {
                break;
            }
            this.offer(item);
        }
    }
    
    private static class Node
    {
        private volatile Object item;
        private volatile Node next;
        
        Node(final Object x) {
            this.item = x;
        }
        
        Node(final Object x, final Node n) {
            this.item = x;
            this.next = n;
        }
        
        Object getItem() {
            return this.item;
        }
        
        synchronized boolean casItem(final Object cmp, final Object val) {
            if (this.item == cmp) {
                this.item = val;
                return true;
            }
            return false;
        }
        
        synchronized void setItem(final Object val) {
            this.item = val;
        }
        
        Node getNext() {
            return this.next;
        }
        
        synchronized boolean casNext(final Node cmp, final Node val) {
            if (this.next == cmp) {
                this.next = val;
                return true;
            }
            return false;
        }
        
        synchronized void setNext(final Node val) {
            this.next = val;
        }
    }
    
    private class Itr implements Iterator
    {
        private Node nextNode;
        private Object nextItem;
        private Node lastRet;
        
        Itr() {
            this.advance();
        }
        
        private Object advance() {
            this.lastRet = this.nextNode;
            final Object x = this.nextItem;
            for (Node p = (this.nextNode == null) ? ConcurrentLinkedQueue.this.first() : this.nextNode.getNext(); p != null; p = p.getNext()) {
                final Object item = p.getItem();
                if (item != null) {
                    this.nextNode = p;
                    this.nextItem = item;
                    return x;
                }
            }
            this.nextNode = null;
            this.nextItem = null;
            return x;
        }
        
        public boolean hasNext() {
            return this.nextNode != null;
        }
        
        public Object next() {
            if (this.nextNode == null) {
                throw new NoSuchElementException();
            }
            return this.advance();
        }
        
        public void remove() {
            final Node l = this.lastRet;
            if (l == null) {
                throw new IllegalStateException();
            }
            l.setItem(null);
            this.lastRet = null;
        }
    }
    
    private static class SerializableLock implements Serializable
    {
    }
}
