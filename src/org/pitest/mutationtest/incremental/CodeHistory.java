// 
// Decompiled by Procyon v0.5.36
// 

package org.pitest.mutationtest.incremental;

import java.math.BigInteger;
import org.pitest.classinfo.ClassName;
import org.pitest.mutationtest.MutationStatusTestPair;
import org.pitest.functional.Option;
import org.pitest.mutationtest.engine.MutationIdentifier;

public interface CodeHistory
{
    Option<MutationStatusTestPair> getPreviousResult(final MutationIdentifier p0);
    
    boolean hasClassChanged(final ClassName p0);
    
    boolean hasCoverageChanged(final ClassName p0, final BigInteger p1);
}
