// 
// Decompiled by Procyon v0.5.36
// 

package org.codehaus.plexus.configuration.processor;

import org.codehaus.plexus.util.IOUtil;
import java.io.FileNotFoundException;
import org.codehaus.plexus.configuration.PlexusConfigurationException;
import java.io.Reader;
import org.codehaus.plexus.component.repository.io.PlexusTools;
import java.io.FileReader;
import java.io.File;
import org.codehaus.plexus.configuration.PlexusConfiguration;
import java.util.Map;

public class FileConfigurationResourceHandler extends AbstractConfigurationResourceHandler
{
    public String getId() {
        return "file-configuration-resource";
    }
    
    public PlexusConfiguration[] handleRequest(final Map parameters) throws ConfigurationResourceNotFoundException, ConfigurationProcessingException {
        final File f = new File(this.getSource(parameters));
        if (!f.exists()) {
            throw new ConfigurationResourceNotFoundException("The specified resource " + f + " cannot be found.");
        }
        FileReader configurationReader = null;
        try {
            configurationReader = new FileReader(f);
            return new PlexusConfiguration[] { PlexusTools.buildConfiguration(f.getAbsolutePath(), configurationReader) };
        }
        catch (PlexusConfigurationException e) {
            throw new ConfigurationProcessingException(e);
        }
        catch (FileNotFoundException e2) {
            throw new ConfigurationProcessingException(e2);
        }
        finally {
            IOUtil.close(configurationReader);
        }
    }
}
