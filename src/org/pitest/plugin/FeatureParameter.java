// 
// Decompiled by Procyon v0.5.36
// 

package org.pitest.plugin;

public final class FeatureParameter
{
    private final String name;
    private final String description;
    
    private FeatureParameter(final String name, final String desc) {
        this.name = name;
        this.description = desc;
    }
    
    public static FeatureParameter named(final String name) {
        return new FeatureParameter(name, "");
    }
    
    public FeatureParameter withDescription(final String desc) {
        return new FeatureParameter(this.name, desc);
    }
    
    public String name() {
        return this.name;
    }
    
    public String description() {
        return this.description;
    }
}
