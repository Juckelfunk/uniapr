// 
// Decompiled by Procyon v0.5.36
// 

package edu.emory.mathcs.backport.java.util.concurrent.atomic;

public class AtomicStampedReference
{
    private final AtomicReference atomicRef;
    
    public AtomicStampedReference(final Object initialRef, final int initialStamp) {
        this.atomicRef = new AtomicReference(new ReferenceIntegerPair(initialRef, initialStamp));
    }
    
    public Object getReference() {
        return this.getPair().reference;
    }
    
    public int getStamp() {
        return this.getPair().integer;
    }
    
    public Object get(final int[] stampHolder) {
        final ReferenceIntegerPair p = this.getPair();
        stampHolder[0] = p.integer;
        return p.reference;
    }
    
    public boolean weakCompareAndSet(final Object expectedReference, final Object newReference, final int expectedStamp, final int newStamp) {
        final ReferenceIntegerPair current = this.getPair();
        return expectedReference == current.reference && expectedStamp == current.integer && ((newReference == current.reference && newStamp == current.integer) || this.atomicRef.weakCompareAndSet(current, new ReferenceIntegerPair(newReference, newStamp)));
    }
    
    public boolean compareAndSet(final Object expectedReference, final Object newReference, final int expectedStamp, final int newStamp) {
        final ReferenceIntegerPair current = this.getPair();
        return expectedReference == current.reference && expectedStamp == current.integer && ((newReference == current.reference && newStamp == current.integer) || this.atomicRef.compareAndSet(current, new ReferenceIntegerPair(newReference, newStamp)));
    }
    
    public void set(final Object newReference, final int newStamp) {
        final ReferenceIntegerPair current = this.getPair();
        if (newReference != current.reference || newStamp != current.integer) {
            this.atomicRef.set(new ReferenceIntegerPair(newReference, newStamp));
        }
    }
    
    public boolean attemptStamp(final Object expectedReference, final int newStamp) {
        final ReferenceIntegerPair current = this.getPair();
        return expectedReference == current.reference && (newStamp == current.integer || this.atomicRef.compareAndSet(current, new ReferenceIntegerPair(expectedReference, newStamp)));
    }
    
    private ReferenceIntegerPair getPair() {
        return (ReferenceIntegerPair)this.atomicRef.get();
    }
    
    private static class ReferenceIntegerPair
    {
        private final Object reference;
        private final int integer;
        
        ReferenceIntegerPair(final Object r, final int i) {
            this.reference = r;
            this.integer = i;
        }
    }
}
