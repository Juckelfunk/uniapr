// 
// Decompiled by Procyon v0.5.36
// 

package edu.emory.mathcs.backport.java.util.concurrent.helpers;

import java.util.List;
import java.util.ArrayList;
import java.util.Collection;
import java.io.Serializable;

public class FIFOWaitQueue extends WaitQueue implements Serializable
{
    private static final long serialVersionUID = 2416444691925378811L;
    protected transient WaitNode head_;
    protected transient WaitNode tail_;
    
    public FIFOWaitQueue() {
        this.head_ = null;
        this.tail_ = null;
    }
    
    public void insert(final WaitNode w) {
        if (this.tail_ == null) {
            this.tail_ = w;
            this.head_ = w;
        }
        else {
            this.tail_.next = w;
            this.tail_ = w;
        }
    }
    
    public WaitNode extract() {
        if (this.head_ == null) {
            return null;
        }
        final WaitNode w = this.head_;
        this.head_ = w.next;
        if (this.head_ == null) {
            this.tail_ = null;
        }
        w.next = null;
        return w;
    }
    
    public void putBack(final WaitNode w) {
        w.next = this.head_;
        this.head_ = w;
        if (this.tail_ == null) {
            this.tail_ = w;
        }
    }
    
    public boolean hasNodes() {
        return this.head_ != null;
    }
    
    public int getLength() {
        int count = 0;
        for (WaitNode node = this.head_; node != null; node = node.next) {
            if (node.waiting) {
                ++count;
            }
        }
        return count;
    }
    
    public Collection getWaitingThreads() {
        final List list = new ArrayList();
        final int count = 0;
        for (WaitNode node = this.head_; node != null; node = node.next) {
            if (node.waiting) {
                list.add(node.owner);
            }
        }
        return list;
    }
    
    public boolean isWaiting(final Thread thread) {
        if (thread == null) {
            throw new NullPointerException();
        }
        for (WaitNode node = this.head_; node != null; node = node.next) {
            if (node.waiting && node.owner == thread) {
                return true;
            }
        }
        return false;
    }
}
