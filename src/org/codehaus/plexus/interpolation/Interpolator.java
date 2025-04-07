// 
// Decompiled by Procyon v0.5.36
// 

package org.codehaus.plexus.interpolation;

import java.util.List;

public interface Interpolator
{
    void addValueSource(final ValueSource p0);
    
    void removeValuesSource(final ValueSource p0);
    
    void addPostProcessor(final InterpolationPostProcessor p0);
    
    void removePostProcessor(final InterpolationPostProcessor p0);
    
    String interpolate(final String p0, final String p1) throws InterpolationException;
    
    String interpolate(final String p0, final String p1, final RecursionInterceptor p2) throws InterpolationException;
    
    String interpolate(final String p0) throws InterpolationException;
    
    String interpolate(final String p0, final RecursionInterceptor p1) throws InterpolationException;
    
    List getFeedback();
    
    void clearFeedback();
    
    boolean isCacheAnswers();
    
    void setCacheAnswers(final boolean p0);
    
    void clearAnswers();
}
