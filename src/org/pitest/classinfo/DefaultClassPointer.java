// 
// Decompiled by Procyon v0.5.36
// 

package org.pitest.classinfo;

import org.pitest.functional.Option;

class DefaultClassPointer implements ClassPointer
{
    private final ClassInfo clazz;
    
    DefaultClassPointer(final ClassInfo clazz) {
        this.clazz = clazz;
    }
    
    @Override
    public Option<ClassInfo> fetch() {
        return Option.some(this.clazz);
    }
}
