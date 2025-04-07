// 
// Decompiled by Procyon v0.5.36
// 

package org.codehaus.plexus.personality.plexus.lifecycle.phase;

import org.codehaus.plexus.context.ContextException;
import org.codehaus.plexus.context.Context;

public interface Contextualizable
{
    void contextualize(final Context p0) throws ContextException;
}
