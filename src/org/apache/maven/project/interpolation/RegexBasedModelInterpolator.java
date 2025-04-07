// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.maven.project.interpolation;

import org.codehaus.plexus.interpolation.RegexBasedInterpolator;
import org.codehaus.plexus.interpolation.Interpolator;
import java.util.Properties;
import org.apache.maven.project.path.PathTranslator;
import java.io.IOException;

public class RegexBasedModelInterpolator extends AbstractStringBasedModelInterpolator
{
    public RegexBasedModelInterpolator() throws IOException {
    }
    
    public RegexBasedModelInterpolator(final PathTranslator pathTranslator) {
        super(pathTranslator);
    }
    
    public RegexBasedModelInterpolator(final Properties envars) {
    }
    
    @Override
    protected Interpolator createInterpolator() {
        return new RegexBasedInterpolator(true);
    }
}
