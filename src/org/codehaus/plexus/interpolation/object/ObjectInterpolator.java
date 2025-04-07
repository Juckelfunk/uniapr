// 
// Decompiled by Procyon v0.5.36
// 

package org.codehaus.plexus.interpolation.object;

import java.util.List;
import org.codehaus.plexus.interpolation.RecursionInterceptor;
import org.codehaus.plexus.interpolation.InterpolationException;
import org.codehaus.plexus.interpolation.Interpolator;

public interface ObjectInterpolator
{
    void interpolate(final Object p0, final Interpolator p1) throws InterpolationException;
    
    void interpolate(final Object p0, final Interpolator p1, final RecursionInterceptor p2) throws InterpolationException;
    
    boolean hasWarnings();
    
    List getWarnings();
}
