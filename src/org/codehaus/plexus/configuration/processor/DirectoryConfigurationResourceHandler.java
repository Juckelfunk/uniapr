// 
// Decompiled by Procyon v0.5.36
// 

package org.codehaus.plexus.configuration.processor;

import java.util.List;
import org.codehaus.plexus.configuration.PlexusConfigurationException;
import java.io.IOException;
import java.io.FileNotFoundException;
import java.io.Reader;
import org.codehaus.plexus.component.repository.io.PlexusTools;
import java.io.FileReader;
import org.codehaus.plexus.util.FileUtils;
import java.io.File;
import org.codehaus.plexus.configuration.PlexusConfiguration;
import java.util.Map;

public class DirectoryConfigurationResourceHandler extends AbstractConfigurationResourceHandler
{
    public String getId() {
        return "directory-configuration-resource";
    }
    
    public PlexusConfiguration[] handleRequest(final Map parameters) throws ConfigurationResourceNotFoundException, ConfigurationProcessingException {
        final File f = new File(this.getSource(parameters));
        if (!f.exists()) {
            throw new ConfigurationResourceNotFoundException("The specified resource " + f + " cannot be found.");
        }
        if (!f.isDirectory()) {
            throw new ConfigurationResourceNotFoundException("The specified resource " + f + " is not a directory.");
        }
        String includes = parameters.get("includes");
        if (includes == null) {
            includes = "**/*.xml";
        }
        final String excludes = parameters.get("excludes");
        try {
            final List files = FileUtils.getFiles(f, includes, excludes);
            final PlexusConfiguration[] configurations = new PlexusConfiguration[files.size()];
            for (int i = 0; i < configurations.length; ++i) {
                final File configurationFile = files.get(i);
                final PlexusConfiguration configuration = PlexusTools.buildConfiguration(configurationFile.getAbsolutePath(), new FileReader(configurationFile));
                configurations[i] = configuration;
            }
            return configurations;
        }
        catch (FileNotFoundException e) {
            throw new ConfigurationProcessingException(e);
        }
        catch (IOException e2) {
            throw new ConfigurationProcessingException(e2);
        }
        catch (PlexusConfigurationException e3) {
            throw new ConfigurationProcessingException(e3);
        }
    }
}
