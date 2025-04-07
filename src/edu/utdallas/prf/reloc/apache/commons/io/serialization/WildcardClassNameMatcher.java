// 
// Decompiled by Procyon v0.5.36
// 

package edu.utdallas.prf.reloc.apache.commons.io.serialization;

import edu.utdallas.prf.reloc.apache.commons.io.FilenameUtils;

final class WildcardClassNameMatcher implements ClassNameMatcher
{
    private final String pattern;
    
    public WildcardClassNameMatcher(final String pattern) {
        this.pattern = pattern;
    }
    
    @Override
    public boolean matches(final String className) {
        return FilenameUtils.wildcardMatch(className, this.pattern);
    }
}
