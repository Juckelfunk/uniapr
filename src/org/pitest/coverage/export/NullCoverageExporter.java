// 
// Decompiled by Procyon v0.5.36
// 

package org.pitest.coverage.export;

import org.pitest.coverage.BlockCoverage;
import java.util.Collection;
import org.pitest.coverage.CoverageExporter;

public class NullCoverageExporter implements CoverageExporter
{
    @Override
    public void recordCoverage(final Collection<BlockCoverage> coverage) {
    }
}
