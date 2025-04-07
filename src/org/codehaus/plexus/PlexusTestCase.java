// 
// Decompiled by Procyon v0.5.36
// 

package org.codehaus.plexus;

import org.codehaus.plexus.context.Context;
import java.io.File;
import java.io.InputStream;
import java.io.Reader;
import java.io.InputStreamReader;
import junit.framework.Assert;
import junit.framework.TestCase;

public abstract class PlexusTestCase extends TestCase
{
    protected PlexusContainer container;
    protected String basedir;
    private static String basedirPath;
    
    public PlexusTestCase() {
    }
    
    public PlexusTestCase(final String testName) {
        super(testName);
    }
    
    protected void setUp() throws Exception {
        InputStream configuration = null;
        try {
            configuration = this.getCustomConfiguration();
            if (configuration == null) {
                configuration = this.getConfiguration();
            }
        }
        catch (Exception e) {
            System.out.println("Error with configuration:");
            System.out.println("configuration = " + configuration);
            Assert.fail(e.getMessage());
        }
        this.basedir = getBasedir();
        (this.container = this.createContainerInstance()).addContextValue("basedir", getBasedir());
        this.customizeContext();
        this.customizeContext(this.getContext());
        final boolean hasPlexusHome = this.getContext().contains("plexus.home");
        if (!hasPlexusHome) {
            final File f = getTestFile("target/plexus-home");
            if (!f.isDirectory()) {
                f.mkdir();
            }
            this.getContext().put("plexus.home", f.getAbsolutePath());
        }
        if (configuration != null) {
            this.container.setConfigurationResource(new InputStreamReader(configuration));
        }
        this.container.initialize();
        this.container.start();
    }
    
    protected PlexusContainer createContainerInstance() {
        return new DefaultPlexusContainer();
    }
    
    private Context getContext() {
        return this.container.getContext();
    }
    
    protected void customizeContext() throws Exception {
    }
    
    protected void customizeContext(final Context context) throws Exception {
    }
    
    protected InputStream getCustomConfiguration() throws Exception {
        return null;
    }
    
    protected void tearDown() throws Exception {
        this.container.dispose();
        this.container = null;
    }
    
    protected PlexusContainer getContainer() {
        return this.container;
    }
    
    protected InputStream getConfiguration() throws Exception {
        return this.getConfiguration(null);
    }
    
    protected InputStream getConfiguration(final String subname) throws Exception {
        final String className = this.getClass().getName();
        final String base = className.substring(className.lastIndexOf(".") + 1);
        String config = null;
        if (subname == null || subname.equals("")) {
            config = base + ".xml";
        }
        else {
            config = base + "-" + subname + ".xml";
        }
        final InputStream configStream = this.getResourceAsStream(config);
        return configStream;
    }
    
    protected InputStream getResourceAsStream(final String resource) {
        return this.getClass().getResourceAsStream(resource);
    }
    
    protected ClassLoader getClassLoader() {
        return this.getClass().getClassLoader();
    }
    
    protected Object lookup(final String componentKey) throws Exception {
        return this.getContainer().lookup(componentKey);
    }
    
    protected Object lookup(final String role, final String id) throws Exception {
        return this.getContainer().lookup(role, id);
    }
    
    protected void release(final Object component) throws Exception {
        this.getContainer().release(component);
    }
    
    public static File getTestFile(final String path) {
        return new File(getBasedir(), path);
    }
    
    public static File getTestFile(final String basedir, final String path) {
        File basedirFile = new File(basedir);
        if (!basedirFile.isAbsolute()) {
            basedirFile = getTestFile(basedir);
        }
        return new File(basedirFile, path);
    }
    
    public static String getTestPath(final String path) {
        return getTestFile(path).getAbsolutePath();
    }
    
    public static String getTestPath(final String basedir, final String path) {
        return getTestFile(basedir, path).getAbsolutePath();
    }
    
    public static String getBasedir() {
        if (PlexusTestCase.basedirPath != null) {
            return PlexusTestCase.basedirPath;
        }
        PlexusTestCase.basedirPath = System.getProperty("basedir");
        if (PlexusTestCase.basedirPath == null) {
            PlexusTestCase.basedirPath = new File("").getAbsolutePath();
        }
        return PlexusTestCase.basedirPath;
    }
}
