// 
// Decompiled by Procyon v0.5.36
// 

package org.pitest.bytecode.analysis;

import org.pitest.mutationtest.engine.Location;
import org.pitest.functional.predicate.Predicate;

public class MethodMatchers
{
    public static Predicate<MethodTree> named(final String name) {
        return new Predicate<MethodTree>() {
            @Override
            public Boolean apply(final MethodTree a) {
                return a.rawNode().name.equals(name);
            }
        };
    }
    
    public static Predicate<MethodTree> forLocation(final Location location) {
        return new Predicate<MethodTree>() {
            @Override
            public Boolean apply(final MethodTree a) {
                return a.asLocation().equals(location);
            }
        };
    }
}
