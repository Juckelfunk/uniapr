// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.maven.plugins.annotations;

public enum ResolutionScope
{
    NONE((String)null), 
    COMPILE("compile"), 
    COMPILE_PLUS_RUNTIME("compile+runtime"), 
    RUNTIME("runtime"), 
    RUNTIME_PLUS_SYSTEM("runtime+system"), 
    TEST("test");
    
    private final String id;
    
    private ResolutionScope(final String id) {
        this.id = id;
    }
    
    public String id() {
        return this.id;
    }
}
