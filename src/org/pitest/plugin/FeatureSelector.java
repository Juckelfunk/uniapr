// 
// Decompiled by Procyon v0.5.36
// 

package org.pitest.plugin;

import org.pitest.functional.F;
import java.util.Iterator;
import java.util.ArrayList;
import org.pitest.functional.FCollection;
import java.util.List;
import java.util.Collection;
import java.util.Map;

public class FeatureSelector<T extends ProvidesFeature>
{
    private final Map<String, Collection<FeatureSetting>> settings;
    private final List<T> active;
    
    public FeatureSelector(final List<FeatureSetting> features, final Collection<T> filters) {
        this.settings = FCollection.bucket(features, this.byFeature());
        this.active = this.selectFeatures(features, filters);
    }
    
    public List<T> getActiveFeatures() {
        return this.active;
    }
    
    public FeatureSetting getSettingForFeature(final String feature) {
        FeatureSetting conf = null;
        final Collection<FeatureSetting> groupedSettings = this.settings.get(feature);
        if (groupedSettings != null) {
            conf = groupedSettings.iterator().next();
        }
        return conf;
    }
    
    public List<T> selectFeatures(final List<FeatureSetting> features, final Collection<T> filters) {
        final List<T> factories = new ArrayList<T>((Collection<? extends T>)filters);
        final Map<String, Collection<T>> featureMap = FCollection.bucket(factories, this.byFeatureName());
        final List<T> active = (List<T>)FCollection.filter(factories, this.isOnByDefault());
        for (final FeatureSetting each : features) {
            final Collection<T> providers = featureMap.get(each.feature());
            if (providers == null || providers.isEmpty()) {
                throw new IllegalArgumentException("Pitest and its installed plugins do not recognise the feature " + each.feature());
            }
            if (each.addsFeature()) {
                active.addAll((Collection<? extends T>)providers);
            }
            if (!each.removesFeature()) {
                continue;
            }
            active.removeAll(providers);
        }
        return active;
    }
    
    private F<T, Boolean> isOnByDefault() {
        return new F<T, Boolean>() {
            @Override
            public Boolean apply(final T a) {
                return a.provides().isOnByDefault();
            }
        };
    }
    
    private F<T, String> byFeatureName() {
        return new F<T, String>() {
            @Override
            public String apply(final T a) {
                return a.provides().name();
            }
        };
    }
    
    private F<FeatureSetting, String> byFeature() {
        return new F<FeatureSetting, String>() {
            @Override
            public String apply(final FeatureSetting a) {
                return a.feature();
            }
        };
    }
}
