// 
// Decompiled by Procyon v0.5.36
// 

package org.pitest.mutationtest.build;

import org.pitest.mutationtest.MutationMetaData;
import java.util.concurrent.Callable;

public interface MutationAnalysisUnit extends Callable<MutationMetaData>
{
    int priority();
}
