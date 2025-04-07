// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.maven.profiles.activation;

import org.codehaus.plexus.util.Os;
import org.apache.maven.model.ActivationOS;
import org.apache.maven.model.Activation;
import org.apache.maven.model.Profile;

public class OperatingSystemProfileActivator implements ProfileActivator
{
    public boolean canDetermineActivation(final Profile profile) {
        final Activation activation = profile.getActivation();
        return activation != null && activation.getOs() != null;
    }
    
    public boolean isActive(final Profile profile) {
        final Activation activation = profile.getActivation();
        final ActivationOS os = activation.getOs();
        boolean result = this.ensureAtLeastOneNonNull(os);
        if (result && os.getFamily() != null) {
            result = this.determineFamilyMatch(os.getFamily());
        }
        if (result && os.getName() != null) {
            result = this.determineNameMatch(os.getName());
        }
        if (result && os.getArch() != null) {
            result = this.determineArchMatch(os.getArch());
        }
        if (result && os.getVersion() != null) {
            result = this.determineVersionMatch(os.getVersion());
        }
        return result;
    }
    
    private boolean ensureAtLeastOneNonNull(final ActivationOS os) {
        return os.getArch() != null || os.getFamily() != null || os.getName() != null || os.getVersion() != null;
    }
    
    private boolean determineVersionMatch(final String version) {
        String test = version;
        boolean reverse = false;
        if (test.startsWith("!")) {
            reverse = true;
            test = test.substring(1);
        }
        final boolean result = Os.isVersion(test);
        if (reverse) {
            return !result;
        }
        return result;
    }
    
    private boolean determineArchMatch(final String arch) {
        String test = arch;
        boolean reverse = false;
        if (test.startsWith("!")) {
            reverse = true;
            test = test.substring(1);
        }
        final boolean result = Os.isArch(test);
        if (reverse) {
            return !result;
        }
        return result;
    }
    
    private boolean determineNameMatch(final String name) {
        String test = name;
        boolean reverse = false;
        if (test.startsWith("!")) {
            reverse = true;
            test = test.substring(1);
        }
        final boolean result = Os.isName(test);
        if (reverse) {
            return !result;
        }
        return result;
    }
    
    private boolean determineFamilyMatch(final String family) {
        String test = family;
        boolean reverse = false;
        if (test.startsWith("!")) {
            reverse = true;
            test = test.substring(1);
        }
        final boolean result = Os.isFamily(test);
        if (reverse) {
            return !result;
        }
        return result;
    }
}
