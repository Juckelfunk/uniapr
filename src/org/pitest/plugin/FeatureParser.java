// 
// Decompiled by Procyon v0.5.36
// 

package org.pitest.plugin;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import org.pitest.functional.F;
import org.pitest.functional.FCollection;
import java.util.List;
import java.util.Collection;

public class FeatureParser
{
    public List<FeatureSetting> parseFeatures(final Collection<String> config) {
        return FCollection.map(config, this.stringToSettings());
    }
    
    private F<String, FeatureSetting> stringToSettings() {
        return new F<String, FeatureSetting>() {
            @Override
            public FeatureSetting apply(final String a) {
                final String text = a.trim();
                final ToggleStatus status = FeatureParser.this.parseStatus(text);
                final String name = FeatureParser.this.parseName(text);
                return new FeatureSetting(name, status, FeatureParser.this.parseConfig(text));
            }
        };
    }
    
    private Map<String, List<String>> parseConfig(final String a) {
        final Map<String, List<String>> vals = new HashMap<String, List<String>>();
        final int confStart = a.indexOf(40) + 1;
        final int end = a.indexOf(41);
        if (confStart != -1 && confStart < end) {
            final String[] split;
            final String[] parts = split = this.split(a.substring(confStart, end));
            for (final String part : split) {
                this.extractValue(part, vals);
            }
        }
        return vals;
    }
    
    private void extractValue(final String part, final Map<String, List<String>> vals) {
        final String[] pairs = part.split("\\[");
        for (int i = 0; i != pairs.length; i += 2) {
            final String key = pairs[i].trim();
            List<String> current = vals.get(key);
            if (current == null) {
                current = new ArrayList<String>();
            }
            current.add(pairs[i + 1].trim());
            vals.put(key, current);
        }
    }
    
    private String[] split(final String body) {
        return body.split("\\]");
    }
    
    private String parseName(final String a) {
        final String name = a.substring(1, a.length());
        final int confStart = name.indexOf(40);
        if (confStart == -1) {
            return name;
        }
        return name.substring(0, confStart);
    }
    
    private ToggleStatus parseStatus(final String a) {
        if (a.startsWith("+")) {
            return ToggleStatus.ACTIVATE;
        }
        if (a.startsWith("-")) {
            return ToggleStatus.DEACTIVATE;
        }
        throw new RuntimeException("Could not parse " + a + " should start with + or -");
    }
}
