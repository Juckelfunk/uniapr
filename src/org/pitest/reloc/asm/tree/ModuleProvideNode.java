// 
// Decompiled by Procyon v0.5.36
// 

package org.pitest.reloc.asm.tree;

import org.pitest.reloc.asm.ModuleVisitor;
import java.util.List;

public class ModuleProvideNode
{
    public String service;
    public List<String> providers;
    
    public ModuleProvideNode(final String service, final List<String> providers) {
        this.service = service;
        this.providers = providers;
    }
    
    public void accept(final ModuleVisitor mv) {
        mv.visitProvide(this.service, (String[])this.providers.toArray(new String[0]));
    }
}
