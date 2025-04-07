// 
// Decompiled by Procyon v0.5.36
// 

package org.pitest.testapi;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.io.Serializable;

public class TestGroupConfig implements Serializable
{
    private static final long serialVersionUID = 1L;
    private final List<String> excludedGroups;
    private final List<String> includedGroups;
    
    public TestGroupConfig(final List<String> excludedGroups, final List<String> includedGroups) {
        this.excludedGroups = ((excludedGroups != null) ? excludedGroups : Collections.emptyList());
        this.includedGroups = ((includedGroups != null) ? includedGroups : Collections.emptyList());
    }
    
    public TestGroupConfig() {
        this(null, null);
    }
    
    public static TestGroupConfig emptyConfig() {
        return new TestGroupConfig();
    }
    
    public TestGroupConfig withExcludedGroups(final String... excluded) {
        return new TestGroupConfig(Arrays.asList(excluded), this.includedGroups);
    }
    
    public TestGroupConfig withIncludedGroups(final String... included) {
        return new TestGroupConfig(this.excludedGroups, Arrays.asList(included));
    }
    
    public List<String> getExcludedGroups() {
        return this.excludedGroups;
    }
    
    public List<String> getIncludedGroups() {
        return this.includedGroups;
    }
    
    @Override
    public String toString() {
        return "TestGroupConfig [excludedGroups=" + this.excludedGroups + ", includedGroups=" + this.includedGroups + "]";
    }
}
