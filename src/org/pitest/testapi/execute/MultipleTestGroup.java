// 
// Decompiled by Procyon v0.5.36
// 

package org.pitest.testapi.execute;

import java.util.Iterator;
import org.pitest.testapi.ResultCollector;
import org.pitest.testapi.Description;
import org.pitest.testapi.TestUnit;
import java.util.List;
import org.pitest.testapi.AbstractTestUnit;

public final class MultipleTestGroup extends AbstractTestUnit
{
    private final List<TestUnit> children;
    
    public MultipleTestGroup(final List<TestUnit> children) {
        super(new Description("MultipleTestGroup"));
        this.children = children;
    }
    
    @Override
    public void execute(final ResultCollector rc) {
        for (final TestUnit each : this.children) {
            each.execute(rc);
            if (rc.shouldExit()) {
                break;
            }
        }
    }
    
    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = 31 * result + ((this.children == null) ? 0 : this.children.hashCode());
        return result;
    }
    
    @Override
    public boolean equals(final Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (this.getClass() != obj.getClass()) {
            return false;
        }
        final MultipleTestGroup other = (MultipleTestGroup)obj;
        if (this.children == null) {
            if (other.children != null) {
                return false;
            }
        }
        else if (!this.children.equals(other.children)) {
            return false;
        }
        return true;
    }
}
