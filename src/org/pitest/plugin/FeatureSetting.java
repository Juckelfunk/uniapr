// 
// Decompiled by Procyon v0.5.36
// 

package org.pitest.plugin;

import org.pitest.functional.Option;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public final class FeatureSetting
{
    private final String feature;
    private final ToggleStatus status;
    private final Map<String, List<String>> settings;
    
    public FeatureSetting(final String feature, final ToggleStatus status, final Map<String, List<String>> settings) {
        this.settings = new HashMap<String, List<String>>();
        this.feature = feature;
        this.status = status;
        this.settings.putAll(settings);
    }
    
    public String feature() {
        return this.feature;
    }
    
    public ToggleStatus status() {
        return this.status;
    }
    
    public boolean addsFeature() {
        return this.status == ToggleStatus.ACTIVATE;
    }
    
    public boolean removesFeature() {
        return this.status == ToggleStatus.DEACTIVATE;
    }
    
    public Option<String> getString(final String key) {
        if (!this.settings.containsKey(key)) {
            return (Option<String>)Option.none();
        }
        final List<String> vals = this.getList(key);
        if (vals.size() > 1) {
            throw new IllegalArgumentException("More than one value supplied for " + key);
        }
        return Option.some(vals.get(0));
    }
    
    public List<String> getList(final String key) {
        return this.settings.get(key);
    }
}
