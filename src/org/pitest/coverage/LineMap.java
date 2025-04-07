// 
// Decompiled by Procyon v0.5.36
// 

package org.pitest.coverage;

import java.util.Set;
import java.util.Map;
import org.pitest.classinfo.ClassName;

public interface LineMap
{
    Map<BlockLocation, Set<Integer>> mapLines(final ClassName p0);
}
