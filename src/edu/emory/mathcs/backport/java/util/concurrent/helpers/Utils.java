// 
// Decompiled by Procyon v0.5.36
// 

package edu.emory.mathcs.backport.java.util.concurrent.helpers;

import sun.misc.Perf;
import java.security.AccessController;
import java.security.PrivilegedAction;
import java.lang.reflect.Array;
import java.util.Iterator;
import edu.emory.mathcs.backport.java.util.Arrays;
import java.util.Collection;
import edu.emory.mathcs.backport.java.util.concurrent.TimeUnit;
import edu.emory.mathcs.backport.java.util.concurrent.locks.Condition;

public final class Utils
{
    private static final NanoTimer nanoTimer;
    private static final String providerProp = "edu.emory.mathcs.backport.java.util.concurrent.NanoTimerProvider";
    
    private Utils() {
    }
    
    public static long nanoTime() {
        return Utils.nanoTimer.nanoTime();
    }
    
    public static long awaitNanos(final Condition cond, final long nanosTimeout) throws InterruptedException {
        if (nanosTimeout <= 0L) {
            return nanosTimeout;
        }
        final long now = nanoTime();
        cond.await(nanosTimeout, TimeUnit.NANOSECONDS);
        return nanosTimeout - (nanoTime() - now);
    }
    
    private static long gcd(long a, long b) {
        while (b > 0L) {
            final long r = a % b;
            a = b;
            b = r;
        }
        return a;
    }
    
    public static Object[] collectionToArray(final Collection c) {
        int len = c.size();
        Object[] arr = new Object[len];
        final Iterator itr = c.iterator();
        int idx = 0;
        while (true) {
            if (idx < len && itr.hasNext()) {
                arr[idx++] = itr.next();
            }
            else if (!itr.hasNext()) {
                if (idx == len) {
                    return arr;
                }
                return Arrays.copyOf(arr, idx, Object[].class);
            }
            else {
                int newcap = (arr.length / 2 + 1) * 3;
                if (newcap < arr.length) {
                    if (arr.length >= Integer.MAX_VALUE) {
                        throw new OutOfMemoryError("required array size too large");
                    }
                    newcap = Integer.MAX_VALUE;
                }
                arr = Arrays.copyOf(arr, newcap, Object[].class);
                len = newcap;
            }
        }
    }
    
    public static Object[] collectionToArray(final Collection c, final Object[] a) {
        final Class aType = a.getClass();
        int len = c.size();
        Object[] arr = (Object[])((a.length >= len) ? a : Array.newInstance(aType.getComponentType(), len));
        final Iterator itr = c.iterator();
        int idx = 0;
        while (true) {
            if (idx < len && itr.hasNext()) {
                arr[idx++] = itr.next();
            }
            else if (!itr.hasNext()) {
                if (idx == len) {
                    return arr;
                }
                if (arr == a) {
                    a[idx] = null;
                    return a;
                }
                return Arrays.copyOf(arr, idx, aType);
            }
            else {
                int newcap = (arr.length / 2 + 1) * 3;
                if (newcap < arr.length) {
                    if (arr.length >= Integer.MAX_VALUE) {
                        throw new OutOfMemoryError("required array size too large");
                    }
                    newcap = Integer.MAX_VALUE;
                }
                arr = Arrays.copyOf(arr, newcap, aType);
                len = newcap;
            }
        }
    }
    
    static {
        NanoTimer timer = null;
        try {
            final String nanoTimerClassName = AccessController.doPrivileged((PrivilegedAction<String>)new PrivilegedAction() {
                public Object run() {
                    return System.getProperty("edu.emory.mathcs.backport.java.util.concurrent.NanoTimerProvider");
                }
            });
            if (nanoTimerClassName != null) {
                final Class cls = Class.forName(nanoTimerClassName);
                timer = cls.newInstance();
            }
        }
        catch (Exception e) {
            System.err.println("WARNING: unable to load the system-property-defined nanotime provider; switching to the default");
            e.printStackTrace();
        }
        if (timer == null) {
            try {
                timer = new SunPerfProvider();
            }
            catch (Throwable t) {}
        }
        if (timer == null) {
            timer = new MillisProvider();
        }
        nanoTimer = timer;
    }
    
    private static final class SunPerfProvider implements NanoTimer
    {
        final Perf perf;
        final long multiplier;
        final long divisor;
        
        SunPerfProvider() {
            this.perf = AccessController.doPrivileged((PrivilegedAction<Perf>)new PrivilegedAction() {
                public Object run() {
                    return Perf.getPerf();
                }
            });
            final long numerator = 1000000000L;
            final long denominator = this.perf.highResFrequency();
            final long gcd = gcd(numerator, denominator);
            this.multiplier = numerator / gcd;
            this.divisor = denominator / gcd;
        }
        
        public long nanoTime() {
            final long ctr = this.perf.highResCounter();
            return ctr / this.divisor * this.multiplier + ctr % this.divisor * this.multiplier / this.divisor;
        }
    }
    
    private static final class MillisProvider implements NanoTimer
    {
        MillisProvider() {
        }
        
        public long nanoTime() {
            return System.currentTimeMillis() * 1000000L;
        }
    }
}
