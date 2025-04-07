// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.maven.profiles.activation;

import org.apache.maven.model.ActivationProperty;
import org.apache.maven.model.Activation;
import org.codehaus.plexus.util.StringUtils;
import org.apache.maven.model.Profile;
import org.codehaus.plexus.context.ContextException;
import org.codehaus.plexus.context.Context;
import java.util.Properties;
import org.codehaus.plexus.personality.plexus.lifecycle.phase.Contextualizable;

public class SystemPropertyProfileActivator extends DetectedProfileActivator implements Contextualizable
{
    private Properties properties;
    
    public void contextualize(final Context context) throws ContextException {
        this.properties = (Properties)context.get("SystemProperties");
    }
    
    @Override
    protected boolean canDetectActivation(final Profile profile) {
        return profile.getActivation() != null && profile.getActivation().getProperty() != null;
    }
    
    public boolean isActive(final Profile profile) throws ProfileActivationException {
        final Activation activation = profile.getActivation();
        final ActivationProperty property = activation.getProperty();
        if (property == null) {
            return false;
        }
        String name = property.getName();
        boolean reverseName = false;
        if (name == null) {
            throw new ProfileActivationException("The property name is required to activate the profile '" + profile.getId() + "'");
        }
        if (name.startsWith("!")) {
            reverseName = true;
            name = name.substring(1);
        }
        final String sysValue = this.properties.getProperty(name);
        String propValue = property.getValue();
        if (StringUtils.isNotEmpty(propValue)) {
            boolean reverseValue = false;
            if (propValue.startsWith("!")) {
                reverseValue = true;
                propValue = propValue.substring(1);
            }
            final boolean result = propValue.equals(sysValue);
            if (reverseValue) {
                return !result;
            }
            return result;
        }
        else {
            final boolean result2 = StringUtils.isNotEmpty(sysValue);
            if (reverseName) {
                return !result2;
            }
            return result2;
        }
    }
}
