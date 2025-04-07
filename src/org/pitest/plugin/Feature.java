// 
// Decompiled by Procyon v0.5.36
// 

package org.pitest.plugin;

import java.util.Collection;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public final class Feature
{
    private final boolean onByDefault;
    private final String name;
    private final String description;
    private final List<FeatureParameter> params;
    
    private Feature(final boolean onByDefault, final String name, final String description, final List<FeatureParameter> params) {
        this.onByDefault = onByDefault;
        this.name = name;
        this.description = description;
        this.params = params;
    }
    
    public static Feature named(final String name) {
        return new Feature(false, name, "", Collections.emptyList());
    }
    
    public Feature withOnByDefault(final boolean onByDefault) {
        return new Feature(onByDefault, this.name, this.description, this.params);
    }
    
    public Feature withDescription(final String description) {
        return new Feature(this.onByDefault, this.name, description, this.params);
    }
    
    public Feature withParameter(final FeatureParameter param) {
        final List<FeatureParameter> params = new ArrayList<FeatureParameter>();
        params.addAll(this.params);
        params.add(param);
        return new Feature(this.onByDefault, this.name, this.description, params);
    }
    
    public String name() {
        return this.name;
    }
    
    public String description() {
        return this.description;
    }
    
    public boolean isOnByDefault() {
        return this.onByDefault;
    }
    
    public List<FeatureParameter> params() {
        return this.params;
    }
    
    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = 31 * result + ((this.name == null) ? 0 : this.name.hashCode());
        return result;
    }
    
    @Override
    public boolean equals(final Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (this.getClass() != obj.getClass()) {
            return false;
        }
        final Feature other = (Feature)obj;
        if (this.name == null) {
            if (other.name != null) {
                return false;
            }
        }
        else if (!this.name.equals(other.name)) {
            return false;
        }
        return true;
    }
}
