// 
// Decompiled by Procyon v0.5.36
// 

package edu.emory.mathcs.backport.java.util.concurrent;

public interface Delayed extends Comparable
{
    long getDelay(final TimeUnit p0);
}
