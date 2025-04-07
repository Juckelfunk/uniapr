// 
// Decompiled by Procyon v0.5.36
// 

package org.uniapr;

import edu.utdallas.prf.reloc.apache.commons.lang3.Validate;
import java.util.Map;
import java.io.File;

public class PatchGenerationPluginInfo
{
    private String name;
    private File compatibleJDKHome;
    private Map<String, String> params;
    
    public void setCompatibleJDKHome(final File compatibleJDKHome) {
        Validate.isTrue(compatibleJDKHome == null || compatibleJDKHome.isDirectory());
        this.compatibleJDKHome = compatibleJDKHome;
    }
    
    public void setName(final String name) {
        Validate.notNull(name);
        Validate.notEmpty(name);
        this.name = name;
    }
    
    public void setParams(final Map<String, String> params) {
        this.params = params;
    }
    
    public File getCompatibleJDKHome() {
        return this.compatibleJDKHome;
    }
    
    public String getName() {
        return this.name;
    }
    
    public Map<String, String> getParams() {
        return this.params;
    }
    
    public boolean matches(final PatchGenerationPlugin plugin) {
        return this.name.equalsIgnoreCase(plugin.name());
    }
}
