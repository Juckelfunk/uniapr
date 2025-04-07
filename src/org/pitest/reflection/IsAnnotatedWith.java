// 
// Decompiled by Procyon v0.5.36
// 

package org.pitest.reflection;

import java.lang.annotation.Annotation;
import java.lang.reflect.AccessibleObject;
import org.pitest.functional.predicate.Predicate;

public class IsAnnotatedWith implements Predicate<AccessibleObject>
{
    private final Class<? extends Annotation> clazz;
    
    public static IsAnnotatedWith instance(final Class<? extends Annotation> clazz) {
        return new IsAnnotatedWith(clazz);
    }
    
    public IsAnnotatedWith(final Class<? extends Annotation> clazz) {
        this.clazz = clazz;
    }
    
    @Override
    public Boolean apply(final AccessibleObject a) {
        return a.isAnnotationPresent(this.clazz);
    }
}
