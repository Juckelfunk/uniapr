// 
// Decompiled by Procyon v0.5.36
// 

package org.codehaus.plexus.interpolation;

import java.util.List;

public class PrefixedObjectValueSource extends AbstractDelegatingValueSource implements QueryEnabledValueSource
{
    public PrefixedObjectValueSource(final String prefix, final Object root) {
        super(new PrefixedValueSourceWrapper(new ObjectBasedValueSource(root), prefix));
    }
    
    public PrefixedObjectValueSource(final List possiblePrefixes, final Object root, final boolean allowUnprefixedExpressions) {
        super(new PrefixedValueSourceWrapper(new ObjectBasedValueSource(root), possiblePrefixes, allowUnprefixedExpressions));
    }
    
    public String getLastExpression() {
        return ((QueryEnabledValueSource)this.getDelegate()).getLastExpression();
    }
}
