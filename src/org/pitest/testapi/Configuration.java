// 
// Decompiled by Procyon v0.5.36
// 

package org.pitest.testapi;

import org.pitest.help.PitHelpError;
import org.pitest.functional.Option;

public interface Configuration
{
    TestUnitFinder testUnitFinder();
    
    TestSuiteFinder testSuiteFinder();
    
    Option<PitHelpError> verifyEnvironment();
}
