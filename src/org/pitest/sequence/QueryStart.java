// 
// Decompiled by Procyon v0.5.36
// 

package org.pitest.sequence;

public class QueryStart<T>
{
    public static <T> SequenceQuery<T> match(final Match<T> p) {
        return new SequenceQuery<T>(new SequenceQuery.Literal<T>(p));
    }
    
    public static <T> SequenceQuery<T> any(final Class<T> clazz) {
        final Match<T> p = Match.always();
        return new SequenceQuery<T>(new SequenceQuery.Repeat<T>(new SequenceQuery.Literal<T>(p)));
    }
}
