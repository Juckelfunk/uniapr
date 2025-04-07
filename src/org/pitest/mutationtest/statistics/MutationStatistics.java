// 
// Decompiled by Procyon v0.5.36
// 

package org.pitest.mutationtest.statistics;

import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.Locale;
import java.io.PrintStream;

public final class MutationStatistics
{
    private final Iterable<Score> scores;
    private final long totalMutations;
    private final long numberOfTestsRun;
    private final long totalDetected;
    
    public MutationStatistics(final Iterable<Score> scores, final long totalMutations, final long totalDetected, final long numberOfTestsRun) {
        this.scores = scores;
        this.totalMutations = totalMutations;
        this.totalDetected = totalDetected;
        this.numberOfTestsRun = numberOfTestsRun;
    }
    
    public Iterable<Score> getScores() {
        return this.scores;
    }
    
    public long getTotalMutations() {
        return this.totalMutations;
    }
    
    public long getTotalDetectedMutations() {
        return this.totalDetected;
    }
    
    public long getTotalSurvivingMutations() {
        return this.getTotalMutations() - this.getTotalDetectedMutations();
    }
    
    public long getPercentageDetected() {
        if (this.getTotalMutations() == 0L) {
            return 100L;
        }
        if (this.getTotalDetectedMutations() == 0L) {
            return 0L;
        }
        return Math.round(100.0f / this.getTotalMutations() * this.getTotalDetectedMutations());
    }
    
    public void report(final PrintStream out) {
        out.println(">> Generated " + this.getTotalMutations() + " mutations Killed " + this.getTotalDetectedMutations() + " (" + this.getPercentageDetected() + "%)");
        out.println(">> Ran " + this.numberOfTestsRun + " tests (" + this.getTestsPerMutation() + " tests per mutation)");
    }
    
    private String getTestsPerMutation() {
        if (this.getTotalMutations() == 0L) {
            return "0";
        }
        final float testsPerMutation = this.numberOfTestsRun / (float)this.getTotalMutations();
        return new DecimalFormat("#.##", new DecimalFormatSymbols(Locale.ENGLISH)).format(testsPerMutation);
    }
}
