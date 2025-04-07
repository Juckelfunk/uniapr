// 
// Decompiled by Procyon v0.5.36
// 

package org.pitest.mutationtest.tooling;

import org.pitest.mutationtest.ClassMutationResults;
import java.io.PrintStream;
import org.pitest.mutationtest.MutationResultListener;

public class SpinnerListener implements MutationResultListener
{
    private static final String[] SPINNER_CHARS;
    private final PrintStream out;
    private int position;
    
    public SpinnerListener(final PrintStream out) {
        this.position = 0;
        this.out = out;
    }
    
    @Override
    public void runStart() {
    }
    
    @Override
    public void handleMutationResult(final ClassMutationResults metaData) {
        this.out.printf("%s", SpinnerListener.SPINNER_CHARS[this.position % SpinnerListener.SPINNER_CHARS.length]);
        ++this.position;
    }
    
    @Override
    public void runEnd() {
    }
    
    static {
        SPINNER_CHARS = new String[] { "\b/", "\b-", "\b\\", "\b|" };
    }
}
