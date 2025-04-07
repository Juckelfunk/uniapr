// 
// Decompiled by Procyon v0.5.36
// 

package edu.emory.mathcs.backport.java.util.concurrent.atomic;

public class AtomicMarkableReference
{
    private final AtomicReference atomicRef;
    
    public AtomicMarkableReference(final Object initialRef, final boolean initialMark) {
        this.atomicRef = new AtomicReference(new ReferenceBooleanPair(initialRef, initialMark));
    }
    
    private ReferenceBooleanPair getPair() {
        return (ReferenceBooleanPair)this.atomicRef.get();
    }
    
    public Object getReference() {
        return this.getPair().reference;
    }
    
    public boolean isMarked() {
        return this.getPair().bit;
    }
    
    public Object get(final boolean[] markHolder) {
        final ReferenceBooleanPair p = this.getPair();
        markHolder[0] = p.bit;
        return p.reference;
    }
    
    public boolean weakCompareAndSet(final Object expectedReference, final Object newReference, final boolean expectedMark, final boolean newMark) {
        final ReferenceBooleanPair current = this.getPair();
        return expectedReference == current.reference && expectedMark == current.bit && ((newReference == current.reference && newMark == current.bit) || this.atomicRef.weakCompareAndSet(current, new ReferenceBooleanPair(newReference, newMark)));
    }
    
    public boolean compareAndSet(final Object expectedReference, final Object newReference, final boolean expectedMark, final boolean newMark) {
        final ReferenceBooleanPair current = this.getPair();
        return expectedReference == current.reference && expectedMark == current.bit && ((newReference == current.reference && newMark == current.bit) || this.atomicRef.compareAndSet(current, new ReferenceBooleanPair(newReference, newMark)));
    }
    
    public void set(final Object newReference, final boolean newMark) {
        final ReferenceBooleanPair current = this.getPair();
        if (newReference != current.reference || newMark != current.bit) {
            this.atomicRef.set(new ReferenceBooleanPair(newReference, newMark));
        }
    }
    
    public boolean attemptMark(final Object expectedReference, final boolean newMark) {
        final ReferenceBooleanPair current = this.getPair();
        return expectedReference == current.reference && (newMark == current.bit || this.atomicRef.compareAndSet(current, new ReferenceBooleanPair(expectedReference, newMark)));
    }
    
    private static class ReferenceBooleanPair
    {
        private final Object reference;
        private final boolean bit;
        
        ReferenceBooleanPair(final Object r, final boolean i) {
            this.reference = r;
            this.bit = i;
        }
    }
}
