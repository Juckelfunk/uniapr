// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.maven.project.interpolation;

import java.util.Arrays;
import org.codehaus.plexus.personality.plexus.lifecycle.phase.InitializationException;
import java.util.Collection;
import org.codehaus.plexus.interpolation.PrefixAwareRecursionInterceptor;
import java.util.Iterator;
import org.codehaus.plexus.logging.Logger;
import org.codehaus.plexus.interpolation.InterpolationException;
import java.util.Collections;
import org.codehaus.plexus.interpolation.MapBasedValueSource;
import java.util.ArrayList;
import org.codehaus.plexus.interpolation.PrefixedValueSourceWrapper;
import org.codehaus.plexus.interpolation.AbstractValueSource;
import org.codehaus.plexus.interpolation.ObjectBasedValueSource;
import org.codehaus.plexus.interpolation.PrefixedObjectValueSource;
import org.codehaus.plexus.interpolation.InterpolationPostProcessor;
import org.codehaus.plexus.interpolation.ValueSource;
import org.codehaus.plexus.util.xml.pull.XmlPullParserException;
import java.io.Reader;
import org.apache.maven.model.io.xpp3.MavenXpp3Reader;
import java.io.StringReader;
import java.io.IOException;
import java.io.Writer;
import org.apache.maven.model.io.xpp3.MavenXpp3Writer;
import java.io.StringWriter;
import org.apache.maven.project.ProjectBuilderConfiguration;
import java.io.File;
import org.apache.maven.project.DefaultProjectBuilderConfiguration;
import java.util.Properties;
import java.util.Map;
import org.apache.maven.model.Model;
import org.codehaus.plexus.interpolation.RecursionInterceptor;
import org.codehaus.plexus.interpolation.Interpolator;
import org.apache.maven.project.path.PathTranslator;
import java.util.List;
import org.codehaus.plexus.personality.plexus.lifecycle.phase.Initializable;
import org.codehaus.plexus.logging.AbstractLogEnabled;

public abstract class AbstractStringBasedModelInterpolator extends AbstractLogEnabled implements ModelInterpolator, Initializable
{
    private static final List<String> PROJECT_PREFIXES;
    private static final List<String> TRANSLATED_PATH_EXPRESSIONS;
    private PathTranslator pathTranslator;
    private Interpolator interpolator;
    private RecursionInterceptor recursionInterceptor;
    
    protected AbstractStringBasedModelInterpolator(final PathTranslator pathTranslator) {
        this.pathTranslator = pathTranslator;
    }
    
    protected AbstractStringBasedModelInterpolator() {
    }
    
    public Model interpolate(final Model model, final Map<String, ?> context) throws ModelInterpolationException {
        return this.interpolate(model, context, true);
    }
    
    @Deprecated
    public Model interpolate(final Model model, final Map<String, ?> context, final boolean strict) throws ModelInterpolationException {
        final Properties props = new Properties();
        props.putAll(context);
        return this.interpolate(model, null, new DefaultProjectBuilderConfiguration().setExecutionProperties(props), true);
    }
    
    public Model interpolate(Model model, final File projectDir, final ProjectBuilderConfiguration config, final boolean debugEnabled) throws ModelInterpolationException {
        final StringWriter sWriter = new StringWriter(1024);
        final MavenXpp3Writer writer = new MavenXpp3Writer();
        try {
            writer.write(sWriter, model);
        }
        catch (IOException e) {
            throw new ModelInterpolationException("Cannot serialize project model for interpolation.", e);
        }
        String serializedModel = sWriter.toString();
        serializedModel = this.interpolate(serializedModel, model, projectDir, config, debugEnabled);
        final StringReader sReader = new StringReader(serializedModel);
        final MavenXpp3Reader modelReader = new MavenXpp3Reader();
        try {
            model = modelReader.read(sReader);
        }
        catch (IOException e2) {
            throw new ModelInterpolationException("Cannot read project model from interpolating filter of serialized version.", e2);
        }
        catch (XmlPullParserException e3) {
            throw new ModelInterpolationException("Cannot read project model from interpolating filter of serialized version.", e3);
        }
        return model;
    }
    
    public String interpolate(final String src, final Model model, final File projectDir, final ProjectBuilderConfiguration config, final boolean debug) throws ModelInterpolationException {
        try {
            final List<ValueSource> valueSources = this.createValueSources(model, projectDir, config);
            final List<InterpolationPostProcessor> postProcessors = this.createPostProcessors(model, projectDir, config);
            return this.interpolateInternal(src, valueSources, postProcessors, debug);
        }
        finally {
            this.interpolator.clearAnswers();
        }
    }
    
    protected List<ValueSource> createValueSources(final Model model, final File projectDir, final ProjectBuilderConfiguration config) {
        String timestampFormat = "yyyyMMdd-HHmm";
        final Properties modelProperties = model.getProperties();
        if (modelProperties != null) {
            timestampFormat = modelProperties.getProperty("maven.build.timestamp.format", timestampFormat);
        }
        final ValueSource modelValueSource1 = new PrefixedObjectValueSource(AbstractStringBasedModelInterpolator.PROJECT_PREFIXES, model, false);
        final ValueSource modelValueSource2 = new ObjectBasedValueSource(model);
        final ValueSource basedirValueSource = new PrefixedValueSourceWrapper(new AbstractValueSource(false) {
            public Object getValue(final String expression) {
                if (projectDir != null && "basedir".equals(expression)) {
                    return projectDir.getAbsolutePath();
                }
                return null;
            }
        }, AbstractStringBasedModelInterpolator.PROJECT_PREFIXES, true);
        final ValueSource baseUriValueSource = new PrefixedValueSourceWrapper(new AbstractValueSource(false) {
            public Object getValue(final String expression) {
                if (projectDir != null && "baseUri".equals(expression)) {
                    return projectDir.getAbsoluteFile().toURI().toString();
                }
                return null;
            }
        }, AbstractStringBasedModelInterpolator.PROJECT_PREFIXES, false);
        final List<ValueSource> valueSources = new ArrayList<ValueSource>(9);
        valueSources.add(basedirValueSource);
        valueSources.add(baseUriValueSource);
        valueSources.add(new BuildTimestampValueSource(config.getBuildStartTime(), timestampFormat));
        valueSources.add(modelValueSource1);
        valueSources.add(new MapBasedValueSource(config.getUserProperties()));
        valueSources.add(new MapBasedValueSource(modelProperties));
        valueSources.add(new MapBasedValueSource(config.getExecutionProperties()));
        valueSources.add(new AbstractValueSource(false) {
            public Object getValue(final String expression) {
                return config.getExecutionProperties().getProperty("env." + expression);
            }
        });
        valueSources.add(modelValueSource2);
        return valueSources;
    }
    
    protected List<InterpolationPostProcessor> createPostProcessors(final Model model, final File projectDir, final ProjectBuilderConfiguration config) {
        return (List<InterpolationPostProcessor>)Collections.singletonList(new PathTranslatingPostProcessor(AbstractStringBasedModelInterpolator.PROJECT_PREFIXES, AbstractStringBasedModelInterpolator.TRANSLATED_PATH_EXPRESSIONS, projectDir, this.pathTranslator));
    }
    
    protected String interpolateInternal(final String src, final List<ValueSource> valueSources, final List<InterpolationPostProcessor> postProcessors, final boolean debug) throws ModelInterpolationException {
        if (src.indexOf("${") < 0) {
            return src;
        }
        final Logger logger = this.getLogger();
        String result = src;
        synchronized (this) {
            for (final ValueSource vs : valueSources) {
                this.interpolator.addValueSource(vs);
            }
            for (final InterpolationPostProcessor postProcessor : postProcessors) {
                this.interpolator.addPostProcessor(postProcessor);
            }
            try {
                try {
                    result = this.interpolator.interpolate(result, this.recursionInterceptor);
                }
                catch (InterpolationException e) {
                    throw new ModelInterpolationException(e.getMessage(), e);
                }
                if (debug) {
                    final List<Object> feedback = (List<Object>)this.interpolator.getFeedback();
                    if (feedback != null && !feedback.isEmpty()) {
                        logger.debug("Maven encountered the following problems during initial POM interpolation:");
                        Object last = null;
                        for (final Object next : feedback) {
                            if (next instanceof Throwable) {
                                if (last == null) {
                                    logger.debug("", (Throwable)next);
                                }
                                else {
                                    logger.debug(String.valueOf(last), (Throwable)next);
                                }
                            }
                            else {
                                if (last != null) {
                                    logger.debug(String.valueOf(last));
                                }
                                last = next;
                            }
                        }
                        if (last != null) {
                            logger.debug(String.valueOf(last));
                        }
                    }
                }
                this.interpolator.clearFeedback();
            }
            finally {
                for (final ValueSource vs2 : valueSources) {
                    this.interpolator.removeValuesSource(vs2);
                }
                for (final InterpolationPostProcessor postProcessor2 : postProcessors) {
                    this.interpolator.removePostProcessor(postProcessor2);
                }
            }
        }
        return result;
    }
    
    protected RecursionInterceptor getRecursionInterceptor() {
        return this.recursionInterceptor;
    }
    
    protected void setRecursionInterceptor(final RecursionInterceptor recursionInterceptor) {
        this.recursionInterceptor = recursionInterceptor;
    }
    
    protected abstract Interpolator createInterpolator();
    
    public void initialize() throws InitializationException {
        this.interpolator = this.createInterpolator();
        this.recursionInterceptor = new PrefixAwareRecursionInterceptor(AbstractStringBasedModelInterpolator.PROJECT_PREFIXES);
    }
    
    protected final Interpolator getInterpolator() {
        return this.interpolator;
    }
    
    static {
        PROJECT_PREFIXES = Arrays.asList("pom.", "project.");
        final List<String> translatedPrefixes = new ArrayList<String>();
        translatedPrefixes.add("build.directory");
        translatedPrefixes.add("build.outputDirectory");
        translatedPrefixes.add("build.testOutputDirectory");
        translatedPrefixes.add("build.sourceDirectory");
        translatedPrefixes.add("build.testSourceDirectory");
        translatedPrefixes.add("build.scriptSourceDirectory");
        translatedPrefixes.add("reporting.outputDirectory");
        TRANSLATED_PATH_EXPRESSIONS = translatedPrefixes;
    }
}
