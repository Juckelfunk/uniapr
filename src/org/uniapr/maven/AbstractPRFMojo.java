// 
// Decompiled by Procyon v0.5.36
// 

package org.uniapr.maven;

import java.util.Set;
import org.pitest.functional.Option;
import org.pitest.classpath.ClassloaderByteArraySource;
import org.pitest.classinfo.CachingByteArraySource;
import org.pitest.classpath.ClassPathByteArraySource;
import org.reflections.Reflections;
import org.uniapr.commons.misc.MemberNameUtils;
import java.util.LinkedList;
import java.util.Iterator;
import org.apache.maven.artifact.DependencyResolutionRequiredException;
import java.util.Collection;
import java.util.ArrayList;
import org.apache.maven.plugin.MojoExecutionException;
import org.pitest.classinfo.ClassByteArraySource;
import org.pitest.classpath.ClassPath;
import org.apache.maven.plugin.MojoFailureException;
import org.uniapr.PRFEntryPoint;
import org.uniapr.jvm.offline.LeakingFieldMain;
import org.uniapr.PatchGenerationPluginInfo;
import java.util.List;
import org.apache.maven.artifact.Artifact;
import java.util.Map;
import org.apache.maven.plugins.annotations.Parameter;
import org.apache.maven.project.MavenProject;
import org.uniapr.PatchGenerationPlugin;
import java.io.File;
import org.apache.maven.plugin.AbstractMojo;

public abstract class AbstractPRFMojo extends AbstractMojo
{
    private static final int CACHE_SIZE = 200;
    protected File compatibleJREHome;
    protected boolean inferFailingTests;
    private PatchGenerationPlugin patchGenerationPluginImpl;
    @Parameter(property = "project", readonly = true, required = true)
    protected MavenProject project;
    @Parameter(property = "plugin.artifactMap", readonly = true, required = true)
    protected Map<String, Artifact> pluginArtifactMap;
    @Parameter(property = "failingTests")
    protected List<String> failingTests;
    @Parameter(property = "whiteListPrefix", defaultValue = "${project.groupId}")
    protected String whiteListPrefix;
    @Parameter(property = "timeoutBias", defaultValue = "1000")
    protected long timeoutBias;
    @Parameter(property = "timeoutCoefficient", defaultValue = "0.5")
    protected double timeoutCoefficient;
    @Parameter(property = "patchesPool", defaultValue = "patches-pool")
    protected File patchesPool;
    @Parameter(property = "leakAnalysis", defaultValue = "true")
    protected boolean leakAnalysis;
    @Parameter(property = "resetJVM", defaultValue = "false")
    protected boolean resetJVM;
    @Parameter(property = "patchGenerationPlugin")
    protected PatchGenerationPluginInfo patchGenerationPlugin;
    
    @Override
    public void execute() throws MojoExecutionException, MojoFailureException {
        this.validateAndSanitizeParameters();
        final ClassPath classPath = this.createClassPath();
        final ClassByteArraySource byteArraySource = this.createClassByteArraySource(classPath);
        if (this.leakAnalysis && this.resetJVM) {
            LeakingFieldMain.invoke(this.obtainCompleteCP());
        }
        try {
            PRFEntryPoint.createEntryPoint().withClassPath(classPath).withByteArraySource(byteArraySource).withWhiteListPrefix(this.whiteListPrefix).withFailingTests(this.failingTests).withCompatibleJREHome(this.compatibleJREHome).withTimeoutBias(this.timeoutBias).withTimeoutCoefficient(this.timeoutCoefficient).withPatchesPool(this.patchesPool).withPatchGenerationPlugin(this.patchGenerationPluginImpl).withResetJVM(this.resetJVM).run(this.patchGenerationPlugin);
        }
        catch (Exception e) {
            e.printStackTrace();
            throw new MojoFailureException(e.getMessage());
        }
    }
    
    private ClassPath createClassPath() {
        final List<File> classPathElements = new ArrayList<File>();
        classPathElements.addAll(this.getProjectClassPath());
        classPathElements.addAll(this.getPluginClassPath());
        return new ClassPath(classPathElements);
    }
    
    private List<File> getProjectClassPath() {
        final List<File> classPath = new ArrayList<File>();
        try {
            for (final Object cpElement : this.project.getTestClasspathElements()) {
                classPath.add(new File((String)cpElement));
            }
        }
        catch (DependencyResolutionRequiredException e) {
            this.getLog().warn(e);
        }
        return classPath;
    }
    
    private void validateAndSanitizeParameters() throws MojoFailureException {
        final String jreHome = System.getProperty("java.home");
        if (jreHome == null) {
            throw new MojoFailureException("JAVA_HOME is not set");
        }
        this.compatibleJREHome = new File(jreHome);
        if (!this.compatibleJREHome.isDirectory()) {
            throw new MojoFailureException("Invalid JAVA_HOME");
        }
        if (this.whiteListPrefix.isEmpty()) {
            this.getLog().warn("Missing whiteListPrefix");
            this.whiteListPrefix = this.project.getGroupId();
            this.getLog().info("Using " + this.whiteListPrefix + " as whiteListPrefix");
        }
        if (this.timeoutBias < 0L) {
            throw new MojoFailureException("Invalid timeout bias");
        }
        if (this.timeoutBias < 1000L) {
            this.getLog().warn("Too small timeout bias");
        }
        if (this.timeoutCoefficient < 0.0) {
            throw new MojoFailureException("Invalid timeout coefficient");
        }
        if (!(this.inferFailingTests = (this.failingTests == null || this.failingTests.isEmpty()))) {
            final List<String> failingTests = new LinkedList<String>();
            for (final String testName : this.failingTests) {
                failingTests.add(MemberNameUtils.sanitizeTestName(testName));
            }
            this.failingTests = failingTests;
        }
        this.patchGenerationPluginImpl = null;
        if (this.patchGenerationPlugin.getName() != null) {
            this.patchGenerationPluginImpl = this.findPatchGenerationPlugin();
            if (this.patchGenerationPluginImpl == null) {
                throw new MojoFailureException("No plugin with the name " + this.patchGenerationPlugin + " found in classpath." + " This is perhaps a classpath issue.");
            }
            this.getLog().info("Found Patch Generation Plugin: " + this.patchGenerationPluginImpl.name() + " (" + this.patchGenerationPluginImpl.description() + ")");
        }
    }
    
    private PatchGenerationPlugin findPatchGenerationPlugin() {
        final Reflections reflections = new Reflections(new Object[] { Thread.currentThread().getContextClassLoader() });
        for (final Class<? extends PatchGenerationPlugin> pluginClass : reflections.getSubTypesOf(PatchGenerationPlugin.class)) {
            PatchGenerationPlugin plugin;
            try {
                plugin = (PatchGenerationPlugin)pluginClass.newInstance();
            }
            catch (Exception e) {
                e.printStackTrace();
                return null;
            }
            if (this.patchGenerationPlugin.matches(plugin)) {
                return plugin;
            }
        }
        return null;
    }
    
    private List<File> getPluginClassPath() {
        final List<File> classPath = new ArrayList<File>();
        for (final Object artifact : this.pluginArtifactMap.values()) {
            final Artifact dependency = (Artifact)artifact;
            if (this.isRelevantDep(dependency)) {
                classPath.add(dependency.getFile());
            }
        }
        return classPath;
    }
    
    private boolean isRelevantDep(final Artifact dependency) {
        return dependency.getGroupId().equals("org.uniapr") && dependency.getArtifactId().equals("uniapr-plugin");
    }
    
    private ClassByteArraySource createClassByteArraySource(final ClassPath classPath) {
        final ClassPathByteArraySource cpbas = new ClassPathByteArraySource(classPath);
        final ClassByteArraySource cbas = this.fallbackToClassLoader(cpbas);
        return new CachingByteArraySource(cbas, 200);
    }
    
    private ClassByteArraySource fallbackToClassLoader(final ClassByteArraySource bas) {
        final ClassByteArraySource clSource = ClassloaderByteArraySource.fromContext();
        return new ClassByteArraySource() {
            @Override
            public Option<byte[]> getBytes(final String clazz) {
                final Option<byte[]> maybeBytes = bas.getBytes(clazz);
                if (maybeBytes.hasSome()) {
                    return maybeBytes;
                }
                return clSource.getBytes(clazz);
            }
        };
    }
    
    public String obtainCompleteCP() {
        System.out.println("!!cp: start!!");
        String cp = this.project.getBuild().getOutputDirectory() + ":" + this.project.getBuild().getTestOutputDirectory();
        final List<String> classPathElements = new ArrayList<String>();
        final Set<Artifact> dependencies = (Set<Artifact>)this.project.getArtifacts();
        for (final Artifact dependency : dependencies) {
            cp = cp + ":" + dependency.getFile().getAbsolutePath();
        }
        System.out.println("!!cp: " + cp);
        return cp;
    }
}
