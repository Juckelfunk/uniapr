// 
// Decompiled by Procyon v0.5.36
// 

package org.codehaus.classworlds;

import java.io.FilenameFilter;
import java.util.Iterator;
import java.util.List;
import java.util.Collections;
import java.util.Comparator;
import java.util.Collection;
import java.util.ArrayList;
import java.io.IOException;
import java.net.MalformedURLException;
import java.io.FileNotFoundException;
import java.net.URL;
import java.io.FileInputStream;
import java.util.Properties;
import java.io.File;
import java.io.Reader;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

public class Configurator
{
    public static final String MAIN_PREFIX = "main is";
    public static final String SET_PREFIX = "set";
    public static final String IMPORT_PREFIX = "import";
    public static final String LOAD_PREFIX = "load";
    public static final String OPTIONALLY_PREFIX = "optionally";
    private Launcher launcher;
    private ClassWorld world;
    private Map configuredRealms;
    
    public Configurator(final Launcher launcher) {
        this.launcher = launcher;
        this.configuredRealms = new HashMap();
    }
    
    public Configurator(final ClassWorld world) {
        this.setClassWorld(world);
    }
    
    public void setClassWorld(final ClassWorld world) {
        this.world = world;
        this.configuredRealms = new HashMap();
    }
    
    public void configure(final InputStream is) throws IOException, MalformedURLException, ConfigurationException, DuplicateRealmException, NoSuchRealmException {
        final BufferedReader reader = new BufferedReader(new InputStreamReader(is));
        if (this.world == null) {
            this.world = new ClassWorld();
        }
        ClassLoader foreignClassLoader = null;
        if (this.launcher != null) {
            foreignClassLoader = this.launcher.getSystemClassLoader();
        }
        ClassRealm curRealm = null;
        String line = null;
        int lineNo = 0;
        boolean mainSet = false;
        while (true) {
            line = reader.readLine();
            if (line == null) {
                this.associateRealms();
                if (this.launcher != null) {
                    this.launcher.setWorld(this.world);
                }
                reader.close();
                return;
            }
            ++lineNo;
            line = line.trim();
            if (this.canIgnore(line)) {
                continue;
            }
            if (line.startsWith("main is")) {
                if (mainSet) {
                    throw new ConfigurationException("Duplicate main configuration", lineNo, line);
                }
                final String conf = line.substring("main is".length()).trim();
                final int fromLoc = conf.indexOf("from");
                if (fromLoc < 0) {
                    throw new ConfigurationException("Missing from clause", lineNo, line);
                }
                final String mainClassName = conf.substring(0, fromLoc).trim();
                final String mainRealmName = conf.substring(fromLoc + 4).trim();
                if (this.launcher != null) {
                    this.launcher.setAppMain(mainClassName, mainRealmName);
                }
                mainSet = true;
            }
            else if (line.startsWith("set")) {
                String conf = line.substring("set".length()).trim();
                final int usingLoc = conf.indexOf(" using") + 1;
                String property = null;
                String propertiesFileName = null;
                if (usingLoc > 0) {
                    property = conf.substring(0, usingLoc).trim();
                    propertiesFileName = (conf = this.filter(conf.substring(usingLoc + 5).trim()));
                }
                String defaultValue = null;
                final int defaultLoc = conf.indexOf(" default") + 1;
                if (defaultLoc > 0) {
                    defaultValue = conf.substring(defaultLoc + 7).trim();
                    if (property == null) {
                        property = conf.substring(0, defaultLoc).trim();
                    }
                    else {
                        propertiesFileName = conf.substring(0, defaultLoc).trim();
                    }
                }
                String value = System.getProperty(property);
                if (value != null) {
                    continue;
                }
                if (propertiesFileName != null) {
                    final File propertiesFile = new File(propertiesFileName);
                    if (propertiesFile.exists()) {
                        final Properties properties = new Properties();
                        try {
                            properties.load(new FileInputStream(propertiesFileName));
                            value = properties.getProperty(property);
                        }
                        catch (Exception ex) {}
                    }
                }
                if (value == null && defaultValue != null) {
                    value = defaultValue;
                }
                if (value == null) {
                    continue;
                }
                value = this.filter(value);
                System.setProperty(property, value);
            }
            else if (line.startsWith("[")) {
                final int rbrack = line.indexOf("]");
                if (rbrack < 0) {
                    throw new ConfigurationException("Invalid realm specifier", lineNo, line);
                }
                final String realmName = line.substring(1, rbrack);
                curRealm = this.world.newRealm(realmName, foreignClassLoader);
                this.configuredRealms.put(realmName, curRealm);
            }
            else if (line.startsWith("import")) {
                if (curRealm == null) {
                    throw new ConfigurationException("Unhandled import", lineNo, line);
                }
                final String conf = line.substring("import".length()).trim();
                final int fromLoc = conf.indexOf("from");
                if (fromLoc < 0) {
                    throw new ConfigurationException("Missing from clause", lineNo, line);
                }
                final String importSpec = conf.substring(0, fromLoc).trim();
                final String relamName = conf.substring(fromLoc + 4).trim();
                curRealm.importFrom(relamName, importSpec);
            }
            else if (line.startsWith("load")) {
                String constituent = line.substring("load".length()).trim();
                constituent = this.filter(constituent);
                if (constituent.indexOf("*") >= 0) {
                    this.loadGlob(constituent, curRealm);
                }
                else {
                    final File file = new File(constituent);
                    if (file.exists()) {
                        curRealm.addConstituent(file.toURL());
                    }
                    else {
                        try {
                            curRealm.addConstituent(new URL(constituent));
                        }
                        catch (MalformedURLException e) {
                            throw new FileNotFoundException(constituent);
                        }
                    }
                }
            }
            else {
                if (!line.startsWith("optionally")) {
                    throw new ConfigurationException("Unhandled configuration", lineNo, line);
                }
                String constituent = line.substring("optionally".length()).trim();
                constituent = this.filter(constituent);
                if (constituent.indexOf("*") >= 0) {
                    this.loadGlob(constituent, curRealm, true);
                }
                else {
                    final File file = new File(constituent);
                    if (file.exists()) {
                        curRealm.addConstituent(file.toURL());
                    }
                    else {
                        try {
                            curRealm.addConstituent(new URL(constituent));
                        }
                        catch (MalformedURLException ex2) {}
                    }
                }
            }
        }
    }
    
    protected void associateRealms() {
        final List sortRealmNames = new ArrayList(this.configuredRealms.keySet());
        final Comparator comparator = new Comparator() {
            public int compare(final Object o1, final Object o2) {
                final String g1 = (String)o1;
                final String g2 = (String)o2;
                return g1.compareTo(g2);
            }
        };
        Collections.sort((List<Object>)sortRealmNames, comparator);
        for (final String realmName : sortRealmNames) {
            final int j = realmName.lastIndexOf(46);
            if (j > 0) {
                final String parentRealmName = realmName.substring(0, j);
                final ClassRealm parentRealm = this.configuredRealms.get(parentRealmName);
                if (parentRealm == null) {
                    continue;
                }
                final ClassRealm realm = this.configuredRealms.get(realmName);
                realm.setParent(parentRealm);
            }
        }
    }
    
    protected void loadGlob(final String line, final ClassRealm realm) throws MalformedURLException, FileNotFoundException {
        this.loadGlob(line, realm, false);
    }
    
    protected void loadGlob(final String line, final ClassRealm realm, final boolean optionally) throws MalformedURLException, FileNotFoundException {
        final File globFile = new File(line);
        final File dir = globFile.getParentFile();
        if (dir.exists()) {
            final String localName = globFile.getName();
            final int starLoc = localName.indexOf("*");
            final String prefix = localName.substring(0, starLoc);
            final String suffix = localName.substring(starLoc + 1);
            final File[] matches = dir.listFiles(new FilenameFilter() {
                public boolean accept(final File dir, final String name) {
                    return name.startsWith(prefix) && name.endsWith(suffix);
                }
            });
            for (int i = 0; i < matches.length; ++i) {
                realm.addConstituent(matches[i].toURL());
            }
            return;
        }
        if (optionally) {
            return;
        }
        throw new FileNotFoundException(dir.toString());
    }
    
    protected String filter(final String text) throws ConfigurationException {
        String result = "";
        int cur = 0;
        final int textLen = text.length();
        int propStart = -1;
        int propStop = -1;
        String propName = null;
        String propValue = null;
        while (cur < textLen) {
            propStart = text.indexOf("${", cur);
            if (propStart < 0) {
                break;
            }
            result += text.substring(cur, propStart);
            propStop = text.indexOf("}", propStart);
            if (propStop < 0) {
                throw new ConfigurationException("Unterminated property: " + text.substring(propStart));
            }
            propName = text.substring(propStart + 2, propStop);
            propValue = System.getProperty(propName);
            if (propValue == null) {
                throw new ConfigurationException("No such property: " + propName);
            }
            result += propValue;
            cur = propStop + 1;
        }
        result += text.substring(cur);
        return result;
    }
    
    private boolean canIgnore(final String line) {
        return line.length() == 0 || line.startsWith("#");
    }
}
