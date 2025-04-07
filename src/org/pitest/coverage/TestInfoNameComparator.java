// 
// Decompiled by Procyon v0.5.36
// 

package org.pitest.coverage;

import java.io.Serializable;
import java.util.Comparator;

class TestInfoNameComparator implements Comparator<TestInfo>, Serializable
{
    private static final long serialVersionUID = 1L;
    
    @Override
    public int compare(final TestInfo lhs, final TestInfo rhs) {
        return lhs.getName().compareTo(rhs.getName());
    }
}
