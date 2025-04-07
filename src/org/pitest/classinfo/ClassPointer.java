// 
// Decompiled by Procyon v0.5.36
// 

package org.pitest.classinfo;

import org.pitest.functional.Option;

interface ClassPointer
{
    Option<ClassInfo> fetch();
}
