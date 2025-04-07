// 
// Decompiled by Procyon v0.5.36
// 

package org.pitest.coverage;

public final class CoverageSummary
{
    private final int numberOfLines;
    private final int numberOfCoveredLines;
    
    public CoverageSummary(final int numberOfLines, final int numberOfCoveredLines) {
        this.numberOfLines = numberOfLines;
        this.numberOfCoveredLines = numberOfCoveredLines;
    }
    
    public int getNumberOfLines() {
        return this.numberOfLines;
    }
    
    public int getNumberOfCoveredLines() {
        return this.numberOfCoveredLines;
    }
    
    public int getCoverage() {
        return (this.numberOfLines == 0) ? 100 : Math.round(100.0f * this.numberOfCoveredLines / this.numberOfLines);
    }
}
