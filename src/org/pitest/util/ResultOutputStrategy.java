// 
// Decompiled by Procyon v0.5.36
// 

package org.pitest.util;

import java.io.Writer;

public interface ResultOutputStrategy
{
    Writer createWriterForFile(final String p0);
}
