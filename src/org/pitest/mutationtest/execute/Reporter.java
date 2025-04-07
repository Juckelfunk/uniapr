// 
// Decompiled by Procyon v0.5.36
// 

package org.pitest.mutationtest.execute;

import org.pitest.util.ExitCode;
import org.pitest.mutationtest.MutationStatusTestPair;
import java.io.IOException;
import org.pitest.mutationtest.engine.MutationIdentifier;

public interface Reporter
{
    void describe(final MutationIdentifier p0) throws IOException;
    
    void report(final MutationIdentifier p0, final MutationStatusTestPair p1) throws IOException;
    
    void done(final ExitCode p0);
}
