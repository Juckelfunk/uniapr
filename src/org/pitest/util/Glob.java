// 
// Decompiled by Procyon v0.5.36
// 

package org.pitest.util;

import org.pitest.functional.FCollection;
import java.util.Collection;
import org.pitest.functional.F;
import java.util.regex.Pattern;
import org.pitest.functional.predicate.Predicate;

public class Glob implements Predicate<String>
{
    private final Pattern regex;
    
    public Glob(final String glob) {
        if (glob.startsWith("~")) {
            this.regex = Pattern.compile(glob.substring(1));
        }
        else {
            this.regex = Pattern.compile(convertGlobToRegex(glob));
        }
    }
    
    public boolean matches(final CharSequence seq) {
        return this.regex.matcher(seq).matches();
    }
    
    public static F<String, Predicate<String>> toGlobPredicate() {
        return new F<String, Predicate<String>>() {
            @Override
            public Glob apply(final String glob) {
                return new Glob(glob);
            }
        };
    }
    
    public static Collection<Predicate<String>> toGlobPredicates(final Collection<String> globs) {
        return FCollection.map(globs, toGlobPredicate());
    }
    
    private static String convertGlobToRegex(final String glob) {
        final StringBuilder out = new StringBuilder("^");
        for (int i = 0; i < glob.length(); ++i) {
            final char c = glob.charAt(i);
            switch (c) {
                case '$': {
                    out.append("\\$");
                    break;
                }
                case '*': {
                    out.append(".*");
                    break;
                }
                case '?': {
                    out.append('.');
                    break;
                }
                case '.': {
                    out.append("\\.");
                    break;
                }
                case '\\': {
                    out.append("\\\\");
                    break;
                }
                default: {
                    out.append(c);
                    break;
                }
            }
        }
        out.append('$');
        return out.toString();
    }
    
    @Override
    public Boolean apply(final String value) {
        return this.matches(value);
    }
    
    @Override
    public String toString() {
        return this.regex.pattern();
    }
}
