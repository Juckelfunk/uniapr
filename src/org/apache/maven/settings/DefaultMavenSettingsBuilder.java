// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.maven.settings;

import org.codehaus.plexus.util.StringUtils;
import java.util.Iterator;
import java.util.List;
import org.codehaus.plexus.util.xml.pull.XmlPullParserException;
import java.io.IOException;
import java.io.Reader;
import org.apache.maven.settings.io.xpp3.SettingsXpp3Reader;
import java.io.StringReader;
import org.codehaus.plexus.interpolation.ValueSource;
import org.codehaus.plexus.interpolation.EnvarBasedValueSource;
import org.codehaus.plexus.interpolation.RegexBasedInterpolator;
import java.io.Writer;
import org.codehaus.plexus.util.IOUtil;
import java.io.StringWriter;
import org.codehaus.plexus.util.ReaderFactory;
import java.io.File;
import org.codehaus.plexus.personality.plexus.lifecycle.phase.Initializable;
import org.codehaus.plexus.logging.AbstractLogEnabled;

public class DefaultMavenSettingsBuilder extends AbstractLogEnabled implements MavenSettingsBuilder, Initializable
{
    public static final String userHome;
    private String userSettingsPath;
    private String globalSettingsPath;
    private File userSettingsFile;
    private File globalSettingsFile;
    private Settings loadedSettings;
    
    public void initialize() {
        this.userSettingsFile = this.getFile(this.userSettingsPath, "user.home", "org.apache.maven.user-settings");
        this.globalSettingsFile = this.getFile(this.globalSettingsPath, "maven.home", "org.apache.maven.global-settings");
        this.getLogger().debug("Building Maven global-level settings from: '" + this.globalSettingsFile.getAbsolutePath() + "'");
        this.getLogger().debug("Building Maven user-level settings from: '" + this.userSettingsFile.getAbsolutePath() + "'");
    }
    
    private Settings readSettings(final File settingsFile) throws IOException, XmlPullParserException {
        Settings settings = null;
        if (settingsFile != null && settingsFile.exists() && settingsFile.isFile()) {
            Reader reader = null;
            try {
                reader = ReaderFactory.newXmlReader(settingsFile);
                final StringWriter sWriter = new StringWriter();
                IOUtil.copy(reader, sWriter);
                String rawInput = sWriter.toString();
                try {
                    final RegexBasedInterpolator interpolator = new RegexBasedInterpolator();
                    interpolator.addValueSource(new EnvarBasedValueSource());
                    rawInput = interpolator.interpolate(rawInput, "settings");
                }
                catch (Exception e) {
                    this.getLogger().warn("Failed to initialize environment variable resolver. Skipping environment substitution in settings.");
                    this.getLogger().debug("Failed to initialize envar resolver. Skipping resolution.", e);
                }
                final StringReader sReader = new StringReader(rawInput);
                final SettingsXpp3Reader modelReader = new SettingsXpp3Reader();
                settings = modelReader.read(sReader, true);
                final RuntimeInfo rtInfo = new RuntimeInfo(settings);
                rtInfo.setFile(settingsFile);
                settings.setRuntimeInfo(rtInfo);
            }
            finally {
                IOUtil.close(reader);
            }
        }
        return settings;
    }
    
    public Settings buildSettings() throws IOException, XmlPullParserException {
        return this.buildSettings(this.userSettingsFile);
    }
    
    public Settings buildSettings(final boolean useCachedSettings) throws IOException, XmlPullParserException {
        return this.buildSettings(this.userSettingsFile, useCachedSettings);
    }
    
    public Settings buildSettings(final File userSettingsFile) throws IOException, XmlPullParserException {
        return this.buildSettings(userSettingsFile, true);
    }
    
    public Settings buildSettings(final File userSettingsFile, final boolean useCachedSettings) throws IOException, XmlPullParserException {
        if (!useCachedSettings || this.loadedSettings == null) {
            Settings globalSettings = this.readSettings(this.globalSettingsFile);
            Settings userSettings = this.readSettings(userSettingsFile);
            if (globalSettings == null) {
                globalSettings = new Settings();
            }
            if (userSettings == null) {
                userSettings = new Settings();
                userSettings.setRuntimeInfo(new RuntimeInfo(userSettings));
            }
            SettingsUtils.merge(userSettings, globalSettings, "global-level");
            this.activateDefaultProfiles(userSettings);
            this.setLocalRepository(userSettings);
            this.loadedSettings = userSettings;
        }
        return this.loadedSettings;
    }
    
    private void activateDefaultProfiles(final Settings settings) {
        final List activeProfiles = settings.getActiveProfiles();
        for (final Profile profile : settings.getProfiles()) {
            if (profile.getActivation() != null && profile.getActivation().isActiveByDefault() && !activeProfiles.contains(profile.getId())) {
                settings.addActiveProfile(profile.getId());
            }
        }
    }
    
    private void setLocalRepository(final Settings userSettings) {
        String localRepository = System.getProperty("maven.repo.local");
        if (localRepository == null || localRepository.length() < 1) {
            localRepository = userSettings.getLocalRepository();
        }
        if (localRepository == null || localRepository.length() < 1) {
            final File mavenUserConfigurationDirectory = new File(DefaultMavenSettingsBuilder.userHome, ".m2");
            if (mavenUserConfigurationDirectory.exists() || !mavenUserConfigurationDirectory.mkdirs()) {}
            localRepository = new File(mavenUserConfigurationDirectory, "repository").getAbsolutePath();
        }
        final File file = new File(localRepository);
        if (!file.isAbsolute() && file.getPath().startsWith(File.separator)) {
            localRepository = file.getAbsolutePath();
        }
        userSettings.setLocalRepository(localRepository);
    }
    
    private File getFile(final String pathPattern, final String basedirSysProp, final String altLocationSysProp) {
        String path = System.getProperty(altLocationSysProp);
        if (StringUtils.isEmpty(path)) {
            String basedir = System.getProperty(basedirSysProp);
            if (basedir == null) {
                basedir = System.getProperty("user.dir");
            }
            basedir = basedir.replaceAll("\\\\", "/");
            basedir = basedir.replaceAll("\\$", "\\\\\\$");
            path = pathPattern.replaceAll("\\$\\{" + basedirSysProp + "\\}", basedir);
            path = path.replaceAll("\\\\", "/");
            return new File(path).getAbsoluteFile();
        }
        return new File(path).getAbsoluteFile();
    }
    
    static {
        userHome = System.getProperty("user.home");
    }
}
