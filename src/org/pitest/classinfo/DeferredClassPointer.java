// 
// Decompiled by Procyon v0.5.36
// 

package org.pitest.classinfo;

import org.pitest.functional.Option;

class DeferredClassPointer implements ClassPointer
{
    private final Repository repository;
    private final ClassName name;
    
    DeferredClassPointer(final Repository repository, final ClassName name) {
        this.repository = repository;
        this.name = name;
    }
    
    @Override
    public Option<ClassInfo> fetch() {
        return this.repository.fetchClass(this.name);
    }
}
