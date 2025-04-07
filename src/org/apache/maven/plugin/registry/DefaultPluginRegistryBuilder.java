// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.maven.plugin.registry;

import org.codehaus.plexus.util.StringUtils;
import java.io.Reader;
import org.codehaus.plexus.util.IOUtil;
import org.apache.maven.plugin.registry.io.xpp3.PluginRegistryXpp3Reader;
import org.codehaus.plexus.util.ReaderFactory;
import org.codehaus.plexus.util.xml.pull.XmlPullParserException;
import java.io.IOException;
import java.io.File;
import org.codehaus.plexus.personality.plexus.lifecycle.phase.Initializable;
import org.codehaus.plexus.logging.AbstractLogEnabled;

public class DefaultPluginRegistryBuilder extends AbstractLogEnabled implements MavenPluginRegistryBuilder, Initializable
{
    public static final String userHome;
    private String userRegistryPath;
    private String globalRegistryPath;
    private File userRegistryFile;
    private File globalRegistryFile;
    
    public void initialize() {
        this.userRegistryFile = this.getFile(this.userRegistryPath, "user.home", "org.apache.maven.user-plugin-registry");
        this.getLogger().debug("Building Maven user-level plugin registry from: '" + this.userRegistryFile.getAbsolutePath() + "'");
        if (System.getProperty("maven.home") != null || System.getProperty("org.apache.maven.global-plugin-registry") != null) {
            this.globalRegistryFile = this.getFile(this.globalRegistryPath, "maven.home", "org.apache.maven.global-plugin-registry");
            this.getLogger().debug("Building Maven global-level plugin registry from: '" + this.globalRegistryFile.getAbsolutePath() + "'");
        }
    }
    
    public PluginRegistry buildPluginRegistry() throws IOException, XmlPullParserException {
        final PluginRegistry global = this.readPluginRegistry(this.globalRegistryFile);
        PluginRegistry user = this.readPluginRegistry(this.userRegistryFile);
        if (user == null && global != null) {
            PluginRegistryUtils.recursivelySetSourceLevel(global, "global-level");
            user = global;
        }
        else {
            PluginRegistryUtils.merge(user, global, "global-level");
        }
        return user;
    }
    
    private PluginRegistry readPluginRegistry(final File registryFile) throws IOException, XmlPullParserException {
        PluginRegistry registry = null;
        if (registryFile != null && registryFile.exists() && registryFile.isFile()) {
            Reader reader = null;
            try {
                reader = ReaderFactory.newXmlReader(registryFile);
                final PluginRegistryXpp3Reader modelReader = new PluginRegistryXpp3Reader();
                registry = modelReader.read(reader);
                final RuntimeInfo rtInfo = new RuntimeInfo(registry);
                registry.setRuntimeInfo(rtInfo);
                rtInfo.setFile(registryFile);
            }
            finally {
                IOUtil.close(reader);
            }
        }
        return registry;
    }
    
    private File getFile(final String pathPattern, final String basedirSysProp, final String altLocationSysProp) {
        String path = System.getProperty(altLocationSysProp);
        if (StringUtils.isEmpty(path)) {
            String basedir = System.getProperty(basedirSysProp);
            basedir = basedir.replaceAll("\\\\", "/");
            basedir = basedir.replaceAll("\\$", "\\\\\\$");
            path = pathPattern.replaceAll("\\$\\{" + basedirSysProp + "\\}", basedir);
            path = path.replaceAll("\\\\", "/");
            path = path.replaceAll("//", "/");
            return new File(path).getAbsoluteFile();
        }
        return new File(path).getAbsoluteFile();
    }
    
    public PluginRegistry createUserPluginRegistry() {
        final PluginRegistry registry = new PluginRegistry();
        final RuntimeInfo rtInfo = new RuntimeInfo(registry);
        registry.setRuntimeInfo(rtInfo);
        rtInfo.setFile(this.userRegistryFile);
        return registry;
    }
    
    static {
        userHome = System.getProperty("user.home");
    }
}
