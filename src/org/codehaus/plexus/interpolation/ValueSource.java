// 
// Decompiled by Procyon v0.5.36
// 

package org.codehaus.plexus.interpolation;

import java.util.List;

public interface ValueSource
{
    Object getValue(final String p0);
    
    List getFeedback();
    
    void clearFeedback();
}
