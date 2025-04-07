// 
// Decompiled by Procyon v0.5.36
// 

package org.codehaus.plexus.util;

import java.util.ArrayList;

public class SweeperPool
{
    private static final boolean DEBUG = false;
    private transient Sweeper sweeper;
    private transient int maxSize;
    private transient int minSize;
    private int triggerSize;
    private ArrayList pooledObjects;
    private boolean shuttingDown;
    
    public SweeperPool(final int maxSize, final int minSize, final int intialCapacity, final int sweepInterval, final int triggerSize) {
        this.shuttingDown = false;
        this.maxSize = this.saneConvert(maxSize);
        this.minSize = this.saneConvert(minSize);
        this.triggerSize = this.saneConvert(triggerSize);
        this.pooledObjects = new ArrayList(intialCapacity);
        if (sweepInterval > 0) {
            (this.sweeper = new Sweeper(this, sweepInterval)).start();
        }
    }
    
    private int saneConvert(final int value) {
        if (value < 0) {
            return 0;
        }
        return value;
    }
    
    public synchronized Object get() {
        if (this.pooledObjects.size() == 0 || this.shuttingDown) {
            return null;
        }
        final Object obj = this.pooledObjects.remove(0);
        this.objectRetrieved(obj);
        return obj;
    }
    
    public synchronized boolean put(final Object obj) {
        this.objectAdded(obj);
        if (obj != null && this.pooledObjects.size() < this.maxSize && !this.shuttingDown) {
            this.pooledObjects.add(obj);
            return true;
        }
        if (obj != null) {
            this.objectDisposed(obj);
        }
        return false;
    }
    
    public synchronized int getSize() {
        return this.pooledObjects.size();
    }
    
    public void dispose() {
        this.shuttingDown = true;
        if (this.sweeper != null) {
            this.sweeper.stop();
            try {
                this.sweeper.join();
            }
            catch (InterruptedException e) {
                System.err.println("Unexpected execption occurred: ");
                e.printStackTrace();
            }
        }
        synchronized (this) {
            final Object[] objects = this.pooledObjects.toArray();
            for (int i = 0; i < objects.length; ++i) {
                this.objectDisposed(objects[i]);
            }
            this.pooledObjects.clear();
        }
    }
    
    boolean isDisposed() {
        return this.shuttingDown && (this.sweeper == null || this.sweeper.hasStopped());
    }
    
    public synchronized void trim() {
        if ((this.triggerSize > 0 && this.pooledObjects.size() >= this.triggerSize) || (this.maxSize > 0 && this.pooledObjects.size() >= this.maxSize)) {
            while (this.pooledObjects.size() > this.minSize) {
                this.objectDisposed(this.pooledObjects.remove(0));
            }
        }
    }
    
    public void objectDisposed(final Object obj) {
    }
    
    public void objectAdded(final Object obj) {
    }
    
    public void objectRetrieved(final Object obj) {
    }
    
    private static class Sweeper implements Runnable
    {
        private final transient SweeperPool pool;
        private transient boolean service;
        private final transient int sweepInterval;
        private transient Thread t;
        
        public Sweeper(final SweeperPool pool, final int sweepInterval) {
            this.service = false;
            this.t = null;
            this.sweepInterval = sweepInterval;
            this.pool = pool;
        }
        
        public void run() {
            this.debug("started");
            if (this.sweepInterval > 0) {
                synchronized (this) {
                    while (this.service) {
                        try {
                            this.wait(this.sweepInterval * 1000);
                        }
                        catch (InterruptedException ex) {}
                        this.runSweep();
                    }
                }
            }
            this.debug("stopped");
        }
        
        public void start() {
            if (!this.service) {
                this.service = true;
                (this.t = new Thread(this)).setName("Sweeper");
                this.t.start();
            }
        }
        
        public synchronized void stop() {
            this.service = false;
            this.notifyAll();
        }
        
        void join() throws InterruptedException {
            this.t.join();
        }
        
        boolean hasStopped() {
            return !this.service && !this.t.isAlive();
        }
        
        private final void debug(final String msg) {
        }
        
        private void runSweep() {
            this.debug("runningSweep. time=" + System.currentTimeMillis());
            this.pool.trim();
        }
    }
}
