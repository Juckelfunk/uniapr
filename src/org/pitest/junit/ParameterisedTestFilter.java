// 
// Decompiled by Procyon v0.5.36
// 

package org.pitest.junit;

import org.junit.runner.Description;
import org.junit.runner.manipulation.Filter;

public class ParameterisedTestFilter extends Filter
{
    private final String desc;
    private final String parent;
    
    ParameterisedTestFilter(final String desc) {
        this.desc = desc;
        this.parent = desc.substring(desc.indexOf(91), desc.indexOf(93) + 1);
    }
    
    @Override
    public String describe() {
        return this.desc;
    }
    
    @Override
    public boolean shouldRun(final Description description) {
        return description.toString().equals(this.desc) || description.toString().equals(this.parent);
    }
}
