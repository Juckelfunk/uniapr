// 
// Decompiled by Procyon v0.5.36
// 

package org.pitest.sequence;

public final class QueryParams<T>
{
    private final Match<T> ignoring;
    private final boolean debug;
    
    QueryParams(final Match<T> ignoring, final boolean debug) {
        this.ignoring = ignoring;
        this.debug = debug;
    }
    
    public static <T> QueryParams<T> params(final Class<T> clazz) {
        return params();
    }
    
    public static <T> QueryParams<T> params() {
        return new QueryParams<T>(Match.never(), false);
    }
    
    public QueryParams<T> withIgnores(final Match<T> ignore) {
        return new QueryParams<T>(ignore, this.debug);
    }
    
    public QueryParams<T> withDebug(final boolean debug) {
        return new QueryParams<T>(this.ignoring, debug);
    }
    
    public Match<T> ignoring() {
        return this.ignoring;
    }
    
    public boolean isDebug() {
        return this.debug;
    }
}
