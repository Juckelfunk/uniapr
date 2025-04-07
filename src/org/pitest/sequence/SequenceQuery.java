// 
// Decompiled by Procyon v0.5.36
// 

package org.pitest.sequence;

public class SequenceQuery<T>
{
    private final Partial<T> token;
    
    SequenceQuery(final Partial<T> token) {
        this.token = token;
    }
    
    public SequenceQuery<T> then(final Match<T> next) {
        return this.then(new SequenceQuery<T>(new Literal<T>(next)));
    }
    
    public SequenceQuery<T> then(final SequenceQuery<T> next) {
        final Concat<T> concat = new Concat<T>(this.token, next.token);
        return new SequenceQuery<T>(concat);
    }
    
    public SequenceQuery<T> or(final SequenceQuery<T> next) {
        final Or<T> or = new Or<T>(this.token, next.token);
        return new SequenceQuery<T>(or);
    }
    
    public SequenceQuery<T> thenAnyOf(final SequenceQuery<T> left, final SequenceQuery<T> right) {
        final Or<T> or = new Or<T>(left.token, right.token);
        final Concat<T> concat = new Concat<T>(this.token, or);
        return new SequenceQuery<T>(concat);
    }
    
    public SequenceQuery<T> zeroOrMore(final SequenceQuery<T> next) {
        final Concat<T> concat = new Concat<T>(this.token, new Repeat<T>(next.token));
        return new SequenceQuery<T>(concat);
    }
    
    public SequenceQuery<T> oneOrMore(final SequenceQuery<T> next) {
        final Concat<T> concat = new Concat<T>(this.token, new Plus<T>(next.token));
        return new SequenceQuery<T>(concat);
    }
    
    public SequenceMatcher<T> compile() {
        return this.compile(QueryParams.params());
    }
    
    public SequenceMatcher<T> compile(final QueryParams<T> params) {
        return new NFASequenceMatcher<T>(params.ignoring(), this.token.make(EndMatch.MATCH), params.isDebug());
    }
    
    static class Literal<T> implements Partial<T>
    {
        Match<T> c;
        
        Literal(final Match<T> p) {
            this.c = p;
        }
        
        @Override
        public State<T> make(final State<T> andThen) {
            return new Consume<T>(this.c, andThen);
        }
    }
    
    static class Or<T> implements Partial<T>
    {
        Partial<T> left;
        Partial<T> right;
        
        Or(final Partial<T> left, final Partial<T> right) {
            this.left = left;
            this.right = right;
        }
        
        @Override
        public State<T> make(final State<T> andThen) {
            final State<T> l = this.left.make(andThen);
            final State<T> r = this.right.make(andThen);
            return new Split<T>(l, r);
        }
    }
    
    static class Concat<T> implements Partial<T>
    {
        Partial<T> left;
        Partial<T> right;
        
        Concat(final Partial<T> left, final Partial<T> right) {
            this.left = left;
            this.right = right;
        }
        
        @Override
        public State<T> make(final State<T> andThen) {
            return this.left.make(this.right.make(andThen));
        }
    }
    
    static class Repeat<T> implements Partial<T>
    {
        Partial<T> r;
        
        Repeat(final Partial<T> r) {
            this.r = r;
        }
        
        @Override
        public State<T> make(final State<T> andThen) {
            final Split<T> placeHolder = new Split<T>(null, null);
            final State<T> right = this.r.make(placeHolder);
            final Split<T> split = new Split<T>(right, andThen);
            placeHolder.out1 = split;
            return placeHolder;
        }
    }
    
    static class Plus<T> implements Partial<T>
    {
        Partial<T> r;
        
        Plus(final Partial<T> r) {
            this.r = r;
        }
        
        @Override
        public State<T> make(final State<T> andThen) {
            final Concat<T> concat = new Concat<T>(this.r, new Repeat<T>(this.r));
            return concat.make(andThen);
        }
    }
    
    interface Partial<T>
    {
        State<T> make(final State<T> p0);
    }
}
