// 
// Decompiled by Procyon v0.5.36
// 

package org.pitest.sequence;

class Consume<T> implements State<T>
{
    final Match<T> c;
    State<T> out;
    
    Consume(final Match<T> c, final State<T> out) {
        this.c = c;
        this.out = out;
    }
    
    boolean matches(final Context<T> context, final T t) {
        return this.c.test(context, t);
    }
}
