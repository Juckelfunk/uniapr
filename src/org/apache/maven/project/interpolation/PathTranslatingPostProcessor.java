// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.maven.project.interpolation;

import java.util.Collection;
import org.codehaus.plexus.interpolation.util.ValueSourceUtils;
import org.apache.maven.project.path.PathTranslator;
import java.io.File;
import java.util.List;
import org.codehaus.plexus.interpolation.InterpolationPostProcessor;

public class PathTranslatingPostProcessor implements InterpolationPostProcessor
{
    private final List<String> unprefixedPathKeys;
    private final File projectDir;
    private final PathTranslator pathTranslator;
    private final List<String> expressionPrefixes;
    
    public PathTranslatingPostProcessor(final List<String> expressionPrefixes, final List<String> unprefixedPathKeys, final File projectDir, final PathTranslator pathTranslator) {
        this.expressionPrefixes = expressionPrefixes;
        this.unprefixedPathKeys = unprefixedPathKeys;
        this.projectDir = projectDir;
        this.pathTranslator = pathTranslator;
    }
    
    public Object execute(String expression, final Object value) {
        expression = ValueSourceUtils.trimPrefix(expression, this.expressionPrefixes, true);
        if (this.projectDir != null && value != null && this.unprefixedPathKeys.contains(expression)) {
            return this.pathTranslator.alignToBaseDirectory(String.valueOf(value), this.projectDir);
        }
        return value;
    }
}
