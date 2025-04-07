// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.maven.profiles.activation;

import org.apache.maven.model.ActivationFile;
import org.apache.maven.model.Activation;
import org.codehaus.plexus.interpolation.InterpolationException;
import org.codehaus.plexus.util.FileUtils;
import org.codehaus.plexus.util.StringUtils;
import java.util.Map;
import org.codehaus.plexus.interpolation.MapBasedValueSource;
import java.io.IOException;
import org.codehaus.plexus.interpolation.ValueSource;
import org.codehaus.plexus.interpolation.EnvarBasedValueSource;
import org.codehaus.plexus.interpolation.RegexBasedInterpolator;
import org.apache.maven.model.Profile;
import org.codehaus.plexus.logging.Logger;
import org.codehaus.plexus.logging.LogEnabled;

public class FileProfileActivator extends DetectedProfileActivator implements LogEnabled
{
    private Logger logger;
    
    @Override
    protected boolean canDetectActivation(final Profile profile) {
        return profile.getActivation() != null && profile.getActivation().getFile() != null;
    }
    
    public boolean isActive(final Profile profile) {
        final Activation activation = profile.getActivation();
        final ActivationFile actFile = activation.getFile();
        if (actFile != null) {
            String fileString = actFile.getExists();
            final RegexBasedInterpolator interpolator = new RegexBasedInterpolator();
            try {
                interpolator.addValueSource(new EnvarBasedValueSource());
            }
            catch (IOException ex) {}
            interpolator.addValueSource(new MapBasedValueSource(System.getProperties()));
            try {
                if (StringUtils.isNotEmpty(fileString)) {
                    fileString = StringUtils.replace(interpolator.interpolate(fileString, ""), "\\", "/");
                    return FileUtils.fileExists(fileString);
                }
                fileString = actFile.getMissing();
                if (StringUtils.isNotEmpty(fileString)) {
                    fileString = StringUtils.replace(interpolator.interpolate(fileString, ""), "\\", "/");
                    return !FileUtils.fileExists(fileString);
                }
            }
            catch (InterpolationException e) {
                if (this.logger.isDebugEnabled()) {
                    this.logger.debug("Failed to interpolate missing file location for profile activator: " + fileString, e);
                }
                else {
                    this.logger.warn("Failed to interpolate missing file location for profile activator: " + fileString + ". Run in debug mode (-X) for more information.");
                }
            }
        }
        return false;
    }
    
    public void enableLogging(final Logger logger) {
        this.logger = logger;
    }
}
