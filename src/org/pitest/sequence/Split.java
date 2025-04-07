// 
// Decompiled by Procyon v0.5.36
// 

package org.pitest.sequence;

class Split<T> implements State<T>
{
    State<T> out1;
    State<T> out2;
    
    Split(final State<T> out1, final State<T> out2) {
        this.out1 = out1;
        this.out2 = out2;
    }
}
