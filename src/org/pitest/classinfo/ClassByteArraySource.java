// 
// Decompiled by Procyon v0.5.36
// 

package org.pitest.classinfo;

import org.pitest.functional.Option;

public interface ClassByteArraySource
{
    Option<byte[]> getBytes(final String p0);
}
