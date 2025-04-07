// 
// Decompiled by Procyon v0.5.36
// 

package org.codehaus.plexus.util.interpolation;

import java.util.List;

public class RegexBasedInterpolator extends hidden.org.codehaus.plexus.interpolation.RegexBasedInterpolator implements Interpolator
{
    public RegexBasedInterpolator() {
    }
    
    public RegexBasedInterpolator(final List valueSources) {
        super(valueSources);
    }
    
    public RegexBasedInterpolator(final String startRegex, final String endRegex, final List valueSources) {
        super(startRegex, endRegex, valueSources);
    }
    
    public RegexBasedInterpolator(final String startRegex, final String endRegex) {
        super(startRegex, endRegex);
    }
    
    public void addValueSource(final ValueSource valueSource) {
        super.addValueSource((hidden.org.codehaus.plexus.interpolation.ValueSource)valueSource);
    }
    
    public void removeValuesSource(final ValueSource valueSource) {
        super.removeValuesSource((hidden.org.codehaus.plexus.interpolation.ValueSource)valueSource);
    }
}
