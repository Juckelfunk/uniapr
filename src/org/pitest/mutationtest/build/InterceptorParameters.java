// 
// Decompiled by Procyon v0.5.36
// 

package org.pitest.mutationtest.build;

import java.util.Collections;
import java.util.List;
import org.pitest.plugin.FeatureParameter;
import org.pitest.functional.Option;
import org.pitest.classinfo.ClassByteArraySource;
import org.pitest.mutationtest.config.ReportOptions;
import org.pitest.plugin.FeatureSetting;

public final class InterceptorParameters
{
    private final FeatureSetting conf;
    private final ReportOptions data;
    private final ClassByteArraySource source;
    
    public InterceptorParameters(final FeatureSetting conf, final ReportOptions data, final ClassByteArraySource source) {
        this.conf = conf;
        this.data = data;
        this.source = source;
    }
    
    public ReportOptions data() {
        return this.data;
    }
    
    public Option<FeatureSetting> settings() {
        return Option.some(this.conf);
    }
    
    public ClassByteArraySource source() {
        return this.source;
    }
    
    public Option<String> getString(final FeatureParameter limit) {
        if (this.conf == null) {
            return (Option<String>)Option.none();
        }
        return this.conf.getString(limit.name());
    }
    
    public List<String> getList(final FeatureParameter key) {
        if (this.conf == null) {
            return Collections.emptyList();
        }
        return this.conf.getList(key.name());
    }
    
    public Option<Integer> getInteger(final FeatureParameter key) {
        final Option<String> val = this.getString(key);
        if (val.hasSome()) {
            return Option.some(Integer.parseInt(val.value()));
        }
        return (Option<Integer>)Option.none();
    }
}
