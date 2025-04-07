// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.maven.artifact.manager;

import java.util.HashMap;
import java.util.Map;

public class DefaultWagonProviderMapping implements WagonProviderMapping
{
    private Map<String, String> wagonProviders;
    
    public DefaultWagonProviderMapping() {
        this.wagonProviders = new HashMap<String, String>();
    }
    
    public String getWagonProvider(final String protocol) {
        return this.wagonProviders.get(protocol);
    }
    
    public void setWagonProvider(final String protocol, final String provider) {
        this.wagonProviders.put(protocol, provider);
    }
    
    public void setWagonProviders(final Map<String, String> wagonProviders) {
        this.wagonProviders = wagonProviders;
    }
}
