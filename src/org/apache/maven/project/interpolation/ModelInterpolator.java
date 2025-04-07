// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.maven.project.interpolation;

import org.apache.maven.project.ProjectBuilderConfiguration;
import java.io.File;
import java.util.Map;
import org.apache.maven.model.Model;

public interface ModelInterpolator
{
    public static final String DEFAULT_BUILD_TIMESTAMP_FORMAT = "yyyyMMdd-HHmm";
    public static final String BUILD_TIMESTAMP_FORMAT_PROPERTY = "maven.build.timestamp.format";
    public static final String ROLE = ModelInterpolator.class.getName();
    
    @Deprecated
    Model interpolate(final Model p0, final Map<String, ?> p1) throws ModelInterpolationException;
    
    @Deprecated
    Model interpolate(final Model p0, final Map<String, ?> p1, final boolean p2) throws ModelInterpolationException;
    
    Model interpolate(final Model p0, final File p1, final ProjectBuilderConfiguration p2, final boolean p3) throws ModelInterpolationException;
    
    String interpolate(final String p0, final Model p1, final File p2, final ProjectBuilderConfiguration p3, final boolean p4) throws ModelInterpolationException;
}
