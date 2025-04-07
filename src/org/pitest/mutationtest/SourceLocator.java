// 
// Decompiled by Procyon v0.5.36
// 

package org.pitest.mutationtest;

import java.io.Reader;
import org.pitest.functional.Option;
import java.util.Collection;

public interface SourceLocator
{
    Option<Reader> locate(final Collection<String> p0, final String p1);
}
