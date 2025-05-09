// 
// Decompiled by Procyon v0.5.36
// 

package edu.utdallas.prf.reloc.apache.commons.io.serialization;

import java.util.regex.Pattern;
import java.io.ObjectStreamClass;
import java.io.InvalidClassException;
import java.util.Iterator;
import java.io.IOException;
import java.util.ArrayList;
import java.io.InputStream;
import java.util.List;
import java.io.ObjectInputStream;

public class ValidatingObjectInputStream extends ObjectInputStream
{
    private final List<ClassNameMatcher> acceptMatchers;
    private final List<ClassNameMatcher> rejectMatchers;
    
    public ValidatingObjectInputStream(final InputStream input) throws IOException {
        super(input);
        this.acceptMatchers = new ArrayList<ClassNameMatcher>();
        this.rejectMatchers = new ArrayList<ClassNameMatcher>();
    }
    
    private void validateClassName(final String name) throws InvalidClassException {
        for (final ClassNameMatcher m : this.rejectMatchers) {
            if (m.matches(name)) {
                this.invalidClassNameFound(name);
            }
        }
        boolean ok = false;
        for (final ClassNameMatcher i : this.acceptMatchers) {
            if (i.matches(name)) {
                ok = true;
                break;
            }
        }
        if (!ok) {
            this.invalidClassNameFound(name);
        }
    }
    
    protected void invalidClassNameFound(final String className) throws InvalidClassException {
        throw new InvalidClassException("Class name not accepted: " + className);
    }
    
    @Override
    protected Class<?> resolveClass(final ObjectStreamClass osc) throws IOException, ClassNotFoundException {
        this.validateClassName(osc.getName());
        return super.resolveClass(osc);
    }
    
    public ValidatingObjectInputStream accept(final Class<?>... classes) {
        for (final Class<?> c : classes) {
            this.acceptMatchers.add(new FullClassNameMatcher(new String[] { c.getName() }));
        }
        return this;
    }
    
    public ValidatingObjectInputStream reject(final Class<?>... classes) {
        for (final Class<?> c : classes) {
            this.rejectMatchers.add(new FullClassNameMatcher(new String[] { c.getName() }));
        }
        return this;
    }
    
    public ValidatingObjectInputStream accept(final String... patterns) {
        for (final String pattern : patterns) {
            this.acceptMatchers.add(new WildcardClassNameMatcher(pattern));
        }
        return this;
    }
    
    public ValidatingObjectInputStream reject(final String... patterns) {
        for (final String pattern : patterns) {
            this.rejectMatchers.add(new WildcardClassNameMatcher(pattern));
        }
        return this;
    }
    
    public ValidatingObjectInputStream accept(final Pattern pattern) {
        this.acceptMatchers.add(new RegexpClassNameMatcher(pattern));
        return this;
    }
    
    public ValidatingObjectInputStream reject(final Pattern pattern) {
        this.rejectMatchers.add(new RegexpClassNameMatcher(pattern));
        return this;
    }
    
    public ValidatingObjectInputStream accept(final ClassNameMatcher m) {
        this.acceptMatchers.add(m);
        return this;
    }
    
    public ValidatingObjectInputStream reject(final ClassNameMatcher m) {
        this.rejectMatchers.add(m);
        return this;
    }
}
