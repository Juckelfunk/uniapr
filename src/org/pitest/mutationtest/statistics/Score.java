// 
// Decompiled by Procyon v0.5.36
// 

package org.pitest.mutationtest.statistics;

import java.util.Iterator;
import java.io.PrintStream;

public final class Score
{
    private final String mutatorName;
    private final Iterable<StatusCount> counts;
    private final long totalMutations;
    private final long totalDetected;
    
    public Score(final String name, final Iterable<StatusCount> counts, final long totalMutations, final long totalDetected) {
        this.mutatorName = name;
        this.counts = counts;
        this.totalMutations = totalMutations;
        this.totalDetected = totalDetected;
    }
    
    public void report(final PrintStream out) {
        out.println("> " + this.mutatorName);
        out.println(">> Generated " + this.totalMutations + " Killed " + this.totalDetected + " (" + this.getPercentageDetected() + "%)");
        int i = 0;
        StringBuilder sb = new StringBuilder();
        for (final StatusCount each : this.counts) {
            sb.append(each + " ");
            if (++i % 4 == 0) {
                out.println("> " + sb.toString());
                sb = new StringBuilder();
            }
        }
        out.println("> " + sb.toString());
    }
    
    public String getMutatorName() {
        return this.mutatorName;
    }
    
    public long getTotalMutations() {
        return this.totalMutations;
    }
    
    public long getTotalDetectedMutations() {
        return this.totalDetected;
    }
    
    public int getPercentageDetected() {
        if (this.getTotalMutations() == 0L) {
            return 100;
        }
        if (this.getTotalDetectedMutations() == 0L) {
            return 0;
        }
        return Math.round(100.0f / this.getTotalMutations() * this.getTotalDetectedMutations());
    }
}
