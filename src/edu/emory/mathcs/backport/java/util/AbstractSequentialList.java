// 
// Decompiled by Procyon v0.5.36
// 

package edu.emory.mathcs.backport.java.util;

import java.util.Collection;
import edu.emory.mathcs.backport.java.util.concurrent.helpers.Utils;

public abstract class AbstractSequentialList extends java.util.AbstractSequentialList
{
    protected AbstractSequentialList() {
    }
    
    public Object[] toArray() {
        return Utils.collectionToArray(this);
    }
    
    public Object[] toArray(final Object[] a) {
        return Utils.collectionToArray(this, a);
    }
}
