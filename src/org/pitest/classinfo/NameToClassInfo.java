// 
// Decompiled by Procyon v0.5.36
// 

package org.pitest.classinfo;

import org.pitest.functional.Option;
import org.pitest.functional.F;

public class NameToClassInfo implements F<ClassName, Option<ClassInfo>>
{
    private final ClassInfoSource repository;
    
    public NameToClassInfo(final ClassInfoSource repository) {
        this.repository = repository;
    }
    
    @Override
    public Option<ClassInfo> apply(final ClassName a) {
        return this.repository.fetchClass(a);
    }
}
