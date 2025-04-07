// 
// Decompiled by Procyon v0.5.36
// 

package org.pitest.coverage;

import java.util.Collection;

public interface CoverageExporter
{
    void recordCoverage(final Collection<BlockCoverage> p0);
}
