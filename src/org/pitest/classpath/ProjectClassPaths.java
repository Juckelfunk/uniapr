// 
// Decompiled by Procyon v0.5.36
// 

package org.pitest.classpath;

import org.pitest.functional.F;
import org.pitest.functional.FCollection;
import org.pitest.classinfo.ClassName;

public class ProjectClassPaths
{
    private final ClassPath classPath;
    private final ClassFilter classFilter;
    private final PathFilter pathFilter;
    
    public ProjectClassPaths(final ClassPath classPath, final ClassFilter classFilter, final PathFilter pathFilter) {
        this.classPath = classPath;
        this.classFilter = classFilter;
        this.pathFilter = pathFilter;
    }
    
    public Iterable<ClassName> code() {
        return FCollection.filter(this.classPath.getComponent(this.pathFilter.getCodeFilter()).findClasses(this.classFilter.getCode()), (F<String, Boolean>)this.classFilter.getCode()).map(ClassName.stringToClassName());
    }
    
    public Iterable<ClassName> test() {
        return FCollection.filter(this.classPath.getComponent(this.pathFilter.getTestFilter()).findClasses(this.classFilter.getTest()), (F<String, Boolean>)this.classFilter.getTest()).map(ClassName.stringToClassName());
    }
    
    public ClassPath getClassPath() {
        return this.classPath;
    }
    
    public ClassFilter getFilter() {
        return this.classFilter;
    }
}
