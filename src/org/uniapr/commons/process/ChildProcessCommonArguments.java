// 
// Decompiled by Procyon v0.5.36
// 

package org.uniapr.commons.process;

import edu.utdallas.prf.reloc.apache.commons.lang3.Validate;
import java.util.Collection;
import java.io.Serializable;

public abstract class ChildProcessCommonArguments implements Serializable
{
    protected static final long serialVersionUID = 1L;
    protected final Collection<String> appClassNames;
    
    protected ChildProcessCommonArguments(final Collection<String> appClassNames) {
        Validate.isInstanceOf(Serializable.class, appClassNames);
        this.appClassNames = appClassNames;
    }
    
    public Collection<String> getAppClassNames() {
        return this.appClassNames;
    }
}
