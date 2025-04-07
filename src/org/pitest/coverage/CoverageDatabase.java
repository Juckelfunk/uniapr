// 
// Decompiled by Procyon v0.5.36
// 

package org.pitest.coverage;

import java.math.BigInteger;
import org.pitest.classinfo.ClassInfo;
import org.pitest.classinfo.ClassName;
import java.util.Collection;

public interface CoverageDatabase
{
    Collection<ClassInfo> getClassInfo(final Collection<ClassName> p0);
    
    int getNumberOfCoveredLines(final Collection<ClassName> p0);
    
    Collection<TestInfo> getTestsForClass(final ClassName p0);
    
    Collection<TestInfo> getTestsForClassLine(final ClassLine p0);
    
    BigInteger getCoverageIdForClass(final ClassName p0);
    
    Collection<ClassInfo> getClassesForFile(final String p0, final String p1);
    
    CoverageSummary createSummary();
}
