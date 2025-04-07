// 
// Decompiled by Procyon v0.5.36
// 

package edu.emory.mathcs.backport.java.util.concurrent.locks;

import edu.emory.mathcs.backport.java.util.concurrent.helpers.Utils;
import edu.emory.mathcs.backport.java.util.concurrent.TimeUnit;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.util.HashMap;
import java.io.Serializable;

public class ReentrantReadWriteLock implements ReadWriteLock, Serializable
{
    private static final long serialVersionUID = -3463448656717690166L;
    final ReadLock readerLock_;
    final WriteLock writerLock_;
    final Sync sync;
    
    public ReentrantReadWriteLock() {
        this.readerLock_ = new ReadLock(this);
        this.writerLock_ = new WriteLock(this);
        this.sync = new NonfairSync();
    }
    
    public Lock writeLock() {
        return this.writerLock_;
    }
    
    public Lock readLock() {
        return this.readerLock_;
    }
    
    public final boolean isFair() {
        return false;
    }
    
    protected Thread getOwner() {
        return this.sync.getOwner();
    }
    
    public int getReadLockCount() {
        return this.sync.getReadLockCount();
    }
    
    public boolean isWriteLocked() {
        return this.sync.isWriteLocked();
    }
    
    public boolean isWriteLockedByCurrentThread() {
        return this.sync.isWriteLockedByCurrentThread();
    }
    
    public int getWriteHoldCount() {
        return this.sync.getWriteHoldCount();
    }
    
    public int getReadHoldCount() {
        return this.sync.getReadHoldCount();
    }
    
    public final boolean hasQueuedThreads() {
        return this.sync.hasQueuedThreads();
    }
    
    public final int getQueueLength() {
        return this.sync.getQueueLength();
    }
    
    public String toString() {
        return super.toString() + "[Write locks = " + this.getWriteHoldCount() + ", Read locks = " + this.getReadLockCount() + "]";
    }
    
    private abstract static class Sync implements Serializable
    {
        private static final int NONE = 0;
        private static final int READER = 1;
        private static final int WRITER = 2;
        transient int activeReaders_;
        transient Thread activeWriter_;
        transient int waitingReaders_;
        transient int waitingWriters_;
        transient int writeHolds_;
        transient HashMap readers_;
        static final Integer IONE;
        
        Sync() {
            this.activeReaders_ = 0;
            this.activeWriter_ = null;
            this.waitingReaders_ = 0;
            this.waitingWriters_ = 0;
            this.writeHolds_ = 0;
            this.readers_ = new HashMap();
        }
        
        synchronized boolean startReadFromNewReader() {
            final boolean pass = this.startRead();
            if (!pass) {
                ++this.waitingReaders_;
            }
            return pass;
        }
        
        synchronized boolean startWriteFromNewWriter() {
            final boolean pass = this.startWrite();
            if (!pass) {
                ++this.waitingWriters_;
            }
            return pass;
        }
        
        synchronized boolean startReadFromWaitingReader() {
            final boolean pass = this.startRead();
            if (pass) {
                --this.waitingReaders_;
            }
            return pass;
        }
        
        synchronized boolean startWriteFromWaitingWriter() {
            final boolean pass = this.startWrite();
            if (pass) {
                --this.waitingWriters_;
            }
            return pass;
        }
        
        synchronized void cancelledWaitingReader() {
            --this.waitingReaders_;
        }
        
        synchronized void cancelledWaitingWriter() {
            --this.waitingWriters_;
        }
        
        boolean allowReader() {
            return (this.activeWriter_ == null && this.waitingWriters_ == 0) || this.activeWriter_ == Thread.currentThread();
        }
        
        synchronized boolean startRead() {
            final Thread t = Thread.currentThread();
            final Object c = this.readers_.get(t);
            if (c != null) {
                this.readers_.put(t, new Integer((int)c + 1));
                ++this.activeReaders_;
                return true;
            }
            if (this.allowReader()) {
                this.readers_.put(t, Sync.IONE);
                ++this.activeReaders_;
                return true;
            }
            return false;
        }
        
        synchronized boolean startWrite() {
            if (this.activeWriter_ == Thread.currentThread()) {
                ++this.writeHolds_;
                return true;
            }
            if (this.writeHolds_ != 0) {
                return false;
            }
            if (this.activeReaders_ == 0 || (this.readers_.size() == 1 && this.readers_.get(Thread.currentThread()) != null)) {
                this.activeWriter_ = Thread.currentThread();
                this.writeHolds_ = 1;
                return true;
            }
            return false;
        }
        
        synchronized int endRead() {
            final Thread t = Thread.currentThread();
            final Object c = this.readers_.get(t);
            if (c == null) {
                throw new IllegalMonitorStateException();
            }
            --this.activeReaders_;
            if (c != Sync.IONE) {
                final int h = (int)c - 1;
                final Integer ih = (h == 1) ? Sync.IONE : new Integer(h);
                this.readers_.put(t, ih);
                return 0;
            }
            this.readers_.remove(t);
            if (this.writeHolds_ > 0) {
                return 0;
            }
            if (this.activeReaders_ == 0 && this.waitingWriters_ > 0) {
                return 2;
            }
            return 0;
        }
        
        synchronized int endWrite() {
            if (this.activeWriter_ != Thread.currentThread()) {
                throw new IllegalMonitorStateException();
            }
            --this.writeHolds_;
            if (this.writeHolds_ > 0) {
                return 0;
            }
            this.activeWriter_ = null;
            if (this.waitingReaders_ > 0 && this.allowReader()) {
                return 1;
            }
            if (this.waitingWriters_ > 0) {
                return 2;
            }
            return 0;
        }
        
        synchronized Thread getOwner() {
            return this.activeWriter_;
        }
        
        synchronized int getReadLockCount() {
            return this.activeReaders_;
        }
        
        synchronized boolean isWriteLocked() {
            return this.activeWriter_ != null;
        }
        
        synchronized boolean isWriteLockedByCurrentThread() {
            return this.activeWriter_ == Thread.currentThread();
        }
        
        synchronized int getWriteHoldCount() {
            return this.isWriteLockedByCurrentThread() ? this.writeHolds_ : 0;
        }
        
        synchronized int getReadHoldCount() {
            if (this.activeReaders_ == 0) {
                return 0;
            }
            final Thread t = Thread.currentThread();
            final Integer i = this.readers_.get(t);
            return (i == null) ? 0 : i;
        }
        
        final synchronized boolean hasQueuedThreads() {
            return this.waitingWriters_ > 0 || this.waitingReaders_ > 0;
        }
        
        final synchronized int getQueueLength() {
            return this.waitingWriters_ + this.waitingReaders_;
        }
        
        private void readObject(final ObjectInputStream in) throws IOException, ClassNotFoundException {
            in.defaultReadObject();
            synchronized (this) {
                this.readers_ = new HashMap();
            }
        }
        
        static {
            IONE = new Integer(1);
        }
    }
    
    private static class NonfairSync extends Sync
    {
        NonfairSync() {
        }
    }
    
    public static class ReadLock implements Lock, Serializable
    {
        private static final long serialVersionUID = -5992448646407690164L;
        final ReentrantReadWriteLock lock;
        
        protected ReadLock(final ReentrantReadWriteLock lock) {
            if (lock == null) {
                throw new NullPointerException();
            }
            this.lock = lock;
        }
        
        public void lock() {
            synchronized (this) {
                if (this.lock.sync.startReadFromNewReader()) {
                    return;
                }
                boolean wasInterrupted = Thread.interrupted();
                try {
                    do {
                        try {
                            this.wait();
                        }
                        catch (InterruptedException ex) {
                            wasInterrupted = true;
                        }
                    } while (!this.lock.sync.startReadFromWaitingReader());
                }
                finally {
                    if (wasInterrupted) {
                        Thread.currentThread().interrupt();
                    }
                }
            }
        }
        
        public void lockInterruptibly() throws InterruptedException {
            if (Thread.interrupted()) {
                throw new InterruptedException();
            }
            InterruptedException ie = null;
            synchronized (this) {
                if (!this.lock.sync.startReadFromNewReader()) {
                    try {
                        do {
                            this.wait();
                        } while (!this.lock.sync.startReadFromWaitingReader());
                        return;
                    }
                    catch (InterruptedException ex) {
                        this.lock.sync.cancelledWaitingReader();
                        ie = ex;
                    }
                }
            }
            if (ie != null) {
                this.lock.writerLock_.signalWaiters();
                throw ie;
            }
        }
        
        public boolean tryLock() {
            return this.lock.sync.startRead();
        }
        
        public boolean tryLock(final long timeout, final TimeUnit unit) throws InterruptedException {
            if (Thread.interrupted()) {
                throw new InterruptedException();
            }
            InterruptedException ie = null;
            long nanos = unit.toNanos(timeout);
            synchronized (this) {
                if (nanos <= 0L) {
                    return this.lock.sync.startRead();
                }
                if (this.lock.sync.startReadFromNewReader()) {
                    return true;
                }
                final long deadline = Utils.nanoTime() + nanos;
                while (true) {
                    try {
                        TimeUnit.NANOSECONDS.timedWait(this, nanos);
                    }
                    catch (InterruptedException ex) {
                        this.lock.sync.cancelledWaitingReader();
                        ie = ex;
                        break;
                    }
                    if (this.lock.sync.startReadFromWaitingReader()) {
                        return true;
                    }
                    nanos = deadline - Utils.nanoTime();
                    if (nanos <= 0L) {
                        this.lock.sync.cancelledWaitingReader();
                        break;
                    }
                }
            }
            this.lock.writerLock_.signalWaiters();
            if (ie != null) {
                throw ie;
            }
            return false;
        }
        
        public void unlock() {
            switch (this.lock.sync.endRead()) {
                case 0: {}
                case 1: {
                    this.lock.readerLock_.signalWaiters();
                }
                case 2: {
                    this.lock.writerLock_.signalWaiters();
                }
                default: {}
            }
        }
        
        public Condition newCondition() {
            throw new UnsupportedOperationException();
        }
        
        synchronized void signalWaiters() {
            this.notifyAll();
        }
        
        public String toString() {
            final int r = this.lock.getReadLockCount();
            return super.toString() + "[Read locks = " + r + "]";
        }
    }
    
    public static class WriteLock implements Lock, CondVar.ExclusiveLock, Serializable
    {
        private static final long serialVersionUID = -4992448646407690164L;
        final ReentrantReadWriteLock lock;
        
        protected WriteLock(final ReentrantReadWriteLock lock) {
            if (lock == null) {
                throw new NullPointerException();
            }
            this.lock = lock;
        }
        
        public void lock() {
            synchronized (this) {
                if (this.lock.sync.startWriteFromNewWriter()) {
                    return;
                }
                boolean wasInterrupted = Thread.interrupted();
                try {
                    do {
                        try {
                            this.wait();
                        }
                        catch (InterruptedException ex) {
                            wasInterrupted = true;
                        }
                    } while (!this.lock.sync.startWriteFromWaitingWriter());
                }
                finally {
                    if (wasInterrupted) {
                        Thread.currentThread().interrupt();
                    }
                }
            }
        }
        
        public void lockInterruptibly() throws InterruptedException {
            if (Thread.interrupted()) {
                throw new InterruptedException();
            }
            InterruptedException ie = null;
            synchronized (this) {
                if (!this.lock.sync.startWriteFromNewWriter()) {
                    try {
                        do {
                            this.wait();
                        } while (!this.lock.sync.startWriteFromWaitingWriter());
                        return;
                    }
                    catch (InterruptedException ex) {
                        this.lock.sync.cancelledWaitingWriter();
                        this.notify();
                        ie = ex;
                    }
                }
            }
            if (ie != null) {
                this.lock.readerLock_.signalWaiters();
                throw ie;
            }
        }
        
        public boolean tryLock() {
            return this.lock.sync.startWrite();
        }
        
        public boolean tryLock(final long timeout, final TimeUnit unit) throws InterruptedException {
            if (Thread.interrupted()) {
                throw new InterruptedException();
            }
            InterruptedException ie = null;
            long nanos = unit.toNanos(timeout);
            synchronized (this) {
                if (nanos <= 0L) {
                    return this.lock.sync.startWrite();
                }
                if (this.lock.sync.startWriteFromNewWriter()) {
                    return true;
                }
                final long deadline = Utils.nanoTime() + nanos;
                while (true) {
                    try {
                        TimeUnit.NANOSECONDS.timedWait(this, nanos);
                    }
                    catch (InterruptedException ex) {
                        this.lock.sync.cancelledWaitingWriter();
                        this.notify();
                        ie = ex;
                        break;
                    }
                    if (this.lock.sync.startWriteFromWaitingWriter()) {
                        return true;
                    }
                    nanos = deadline - Utils.nanoTime();
                    if (nanos <= 0L) {
                        this.lock.sync.cancelledWaitingWriter();
                        this.notify();
                        break;
                    }
                }
            }
            this.lock.readerLock_.signalWaiters();
            if (ie != null) {
                throw ie;
            }
            return false;
        }
        
        public void unlock() {
            switch (this.lock.sync.endWrite()) {
                case 0: {}
                case 1: {
                    this.lock.readerLock_.signalWaiters();
                }
                case 2: {
                    this.lock.writerLock_.signalWaiters();
                }
                default: {}
            }
        }
        
        public Condition newCondition() {
            return new CondVar(this);
        }
        
        synchronized void signalWaiters() {
            this.notify();
        }
        
        public String toString() {
            final Thread o = this.lock.getOwner();
            return super.toString() + ((o == null) ? "[Unlocked]" : ("[Locked by thread " + o.getName() + "]"));
        }
        
        public boolean isHeldByCurrentThread() {
            return this.lock.sync.isWriteLockedByCurrentThread();
        }
        
        public int getHoldCount() {
            return this.lock.sync.getWriteHoldCount();
        }
    }
}
