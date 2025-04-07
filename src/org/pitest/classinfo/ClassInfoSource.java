// 
// Decompiled by Procyon v0.5.36
// 

package org.pitest.classinfo;

import org.pitest.functional.Option;

public interface ClassInfoSource
{
    Option<ClassInfo> fetchClass(final ClassName p0);
}
