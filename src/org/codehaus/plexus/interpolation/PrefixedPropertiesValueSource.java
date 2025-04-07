// 
// Decompiled by Procyon v0.5.36
// 

package org.codehaus.plexus.interpolation;

import java.util.List;
import java.util.Properties;

public class PrefixedPropertiesValueSource extends AbstractDelegatingValueSource implements QueryEnabledValueSource
{
    public PrefixedPropertiesValueSource(final String prefix, final Properties properties) {
        super(new PrefixedValueSourceWrapper(new PropertiesBasedValueSource(properties), prefix));
    }
    
    public PrefixedPropertiesValueSource(final List possiblePrefixes, final Properties properties, final boolean allowUnprefixedExpressions) {
        super(new PrefixedValueSourceWrapper(new PropertiesBasedValueSource(properties), possiblePrefixes, allowUnprefixedExpressions));
    }
    
    public String getLastExpression() {
        return ((QueryEnabledValueSource)this.getDelegate()).getLastExpression();
    }
}
