// 
// Decompiled by Procyon v0.5.36
// 

package edu.utdallas.prf.reloc.apache.commons.lang3.tuple;

public final class ImmutablePair<L, R> extends Pair<L, R>
{
    private static final ImmutablePair NULL;
    private static final long serialVersionUID = 4954918890077093841L;
    public final L left;
    public final R right;
    
    public static <L, R> ImmutablePair<L, R> nullPair() {
        return (ImmutablePair<L, R>)ImmutablePair.NULL;
    }
    
    public static <L, R> ImmutablePair<L, R> of(final L left, final R right) {
        return new ImmutablePair<L, R>(left, right);
    }
    
    public ImmutablePair(final L left, final R right) {
        this.left = left;
        this.right = right;
    }
    
    @Override
    public L getLeft() {
        return this.left;
    }
    
    @Override
    public R getRight() {
        return this.right;
    }
    
    @Override
    public R setValue(final R value) {
        throw new UnsupportedOperationException();
    }
    
    static {
        NULL = of((Object)null, (Object)null);
    }
}
