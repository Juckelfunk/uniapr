// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.maven.settings.io.xpp3;

import org.codehaus.plexus.util.ReaderFactory;
import java.io.InputStream;
import org.codehaus.plexus.util.xml.pull.MXParser;
import java.io.Reader;
import org.apache.maven.settings.TrackableBase;
import org.apache.maven.settings.Settings;
import org.codehaus.plexus.util.xml.Xpp3DomBuilder;
import org.apache.maven.settings.Server;
import org.apache.maven.settings.RepositoryPolicy;
import org.apache.maven.settings.RepositoryBase;
import org.apache.maven.settings.Proxy;
import org.apache.maven.settings.Repository;
import java.util.List;
import java.util.ArrayList;
import org.apache.maven.settings.Profile;
import org.apache.maven.settings.Mirror;
import org.apache.maven.settings.IdentifiableBase;
import org.apache.maven.settings.ActivationProperty;
import org.apache.maven.settings.ActivationOS;
import org.apache.maven.settings.ActivationFile;
import java.io.IOException;
import java.util.HashSet;
import org.apache.maven.settings.Activation;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Locale;
import java.util.Date;
import org.codehaus.plexus.util.xml.pull.XmlPullParserException;
import java.util.Set;
import org.codehaus.plexus.util.xml.pull.XmlPullParser;

public class SettingsXpp3Reader
{
    private boolean addDefaultEntities;
    
    public SettingsXpp3Reader() {
        this.addDefaultEntities = true;
    }
    
    private boolean checkFieldWithDuplicate(final XmlPullParser parser, final String tagName, final String alias, final Set parsed) throws XmlPullParserException {
        if (!parser.getName().equals(tagName) && !parser.getName().equals(alias)) {
            return false;
        }
        if (parsed.contains(tagName)) {
            throw new XmlPullParserException("Duplicated tag: '" + tagName + "'", parser, null);
        }
        parsed.add(tagName);
        return true;
    }
    
    public boolean getAddDefaultEntities() {
        return this.addDefaultEntities;
    }
    
    private boolean getBooleanValue(final String s, final String attribute, final XmlPullParser parser) throws XmlPullParserException {
        return this.getBooleanValue(s, attribute, parser, null);
    }
    
    private boolean getBooleanValue(final String s, final String attribute, final XmlPullParser parser, final String defaultValue) throws XmlPullParserException {
        if (s != null && s.length() != 0) {
            return Boolean.valueOf(s);
        }
        return defaultValue != null && Boolean.valueOf(defaultValue);
    }
    
    private byte getByteValue(final String s, final String attribute, final XmlPullParser parser, final boolean strict) throws XmlPullParserException {
        if (s != null) {
            try {
                return Byte.valueOf(s);
            }
            catch (NumberFormatException e) {
                if (strict) {
                    throw new XmlPullParserException("Unable to parse element '" + attribute + "', must be a byte", parser, null);
                }
            }
        }
        return 0;
    }
    
    private char getCharacterValue(final String s, final String attribute, final XmlPullParser parser) throws XmlPullParserException {
        if (s != null) {
            return s.charAt(0);
        }
        return '\0';
    }
    
    private Date getDateValue(final String s, final String attribute, final XmlPullParser parser) throws XmlPullParserException {
        return this.getDateValue(s, attribute, null, parser);
    }
    
    private Date getDateValue(final String s, final String attribute, final String dateFormat, final XmlPullParser parser) throws XmlPullParserException {
        if (s != null) {
            String effectiveDateFormat;
            if ((effectiveDateFormat = dateFormat) == null) {
                effectiveDateFormat = "yyyy-MM-dd'T'HH:mm:ss.SSS";
            }
            if ("long".equals(effectiveDateFormat)) {
                try {
                    return new Date(Long.parseLong(s));
                }
                catch (NumberFormatException e) {
                    throw new XmlPullParserException(e.getMessage());
                }
            }
            try {
                final DateFormat dateParser = new SimpleDateFormat(effectiveDateFormat, Locale.US);
                return dateParser.parse(s);
            }
            catch (ParseException e2) {
                throw new XmlPullParserException(e2.getMessage());
            }
        }
        return null;
    }
    
    private double getDoubleValue(final String s, final String attribute, final XmlPullParser parser, final boolean strict) throws XmlPullParserException {
        if (s != null) {
            try {
                return Double.valueOf(s);
            }
            catch (NumberFormatException e) {
                if (strict) {
                    throw new XmlPullParserException("Unable to parse element '" + attribute + "', must be a floating point number", parser, null);
                }
            }
        }
        return 0.0;
    }
    
    private float getFloatValue(final String s, final String attribute, final XmlPullParser parser, final boolean strict) throws XmlPullParserException {
        if (s != null) {
            try {
                return Float.valueOf(s);
            }
            catch (NumberFormatException e) {
                if (strict) {
                    throw new XmlPullParserException("Unable to parse element '" + attribute + "', must be a floating point number", parser, null);
                }
            }
        }
        return 0.0f;
    }
    
    private int getIntegerValue(final String s, final String attribute, final XmlPullParser parser, final boolean strict) throws XmlPullParserException {
        if (s != null) {
            try {
                return Integer.valueOf(s);
            }
            catch (NumberFormatException e) {
                if (strict) {
                    throw new XmlPullParserException("Unable to parse element '" + attribute + "', must be an integer", parser, null);
                }
            }
        }
        return 0;
    }
    
    private long getLongValue(final String s, final String attribute, final XmlPullParser parser, final boolean strict) throws XmlPullParserException {
        if (s != null) {
            try {
                return Long.valueOf(s);
            }
            catch (NumberFormatException e) {
                if (strict) {
                    throw new XmlPullParserException("Unable to parse element '" + attribute + "', must be a long integer", parser, null);
                }
            }
        }
        return 0L;
    }
    
    private String getRequiredAttributeValue(final String s, final String attribute, final XmlPullParser parser, final boolean strict) throws XmlPullParserException {
        if (s == null && strict) {
            throw new XmlPullParserException("Missing required value for attribute '" + attribute + "'", parser, null);
        }
        return s;
    }
    
    private short getShortValue(final String s, final String attribute, final XmlPullParser parser, final boolean strict) throws XmlPullParserException {
        if (s != null) {
            try {
                return Short.valueOf(s);
            }
            catch (NumberFormatException e) {
                if (strict) {
                    throw new XmlPullParserException("Unable to parse element '" + attribute + "', must be a short integer", parser, null);
                }
            }
        }
        return 0;
    }
    
    private String getTrimmedValue(String s) {
        if (s != null) {
            s = s.trim();
        }
        return s;
    }
    
    private Activation parseActivation(final String tagName, final XmlPullParser parser, final boolean strict) throws IOException, XmlPullParserException {
        final Activation activation = new Activation();
        final Set parsed = new HashSet();
        while (parser.nextTag() == 2) {
            if (this.checkFieldWithDuplicate(parser, "activeByDefault", null, parsed)) {
                activation.setActiveByDefault(this.getBooleanValue(this.getTrimmedValue(parser.nextText()), "activeByDefault", parser, "false"));
            }
            else if (this.checkFieldWithDuplicate(parser, "jdk", null, parsed)) {
                activation.setJdk(this.getTrimmedValue(parser.nextText()));
            }
            else if (this.checkFieldWithDuplicate(parser, "os", null, parsed)) {
                activation.setOs(this.parseActivationOS("os", parser, strict));
            }
            else if (this.checkFieldWithDuplicate(parser, "property", null, parsed)) {
                activation.setProperty(this.parseActivationProperty("property", parser, strict));
            }
            else if (this.checkFieldWithDuplicate(parser, "file", null, parsed)) {
                activation.setFile(this.parseActivationFile("file", parser, strict));
            }
            else {
                if (strict) {
                    throw new XmlPullParserException("Unrecognised tag: '" + parser.getName() + "'", parser, null);
                }
                while (parser.next() != 3) {}
            }
        }
        return activation;
    }
    
    private ActivationFile parseActivationFile(final String tagName, final XmlPullParser parser, final boolean strict) throws IOException, XmlPullParserException {
        final ActivationFile activationFile = new ActivationFile();
        final Set parsed = new HashSet();
        while (parser.nextTag() == 2) {
            if (this.checkFieldWithDuplicate(parser, "missing", null, parsed)) {
                activationFile.setMissing(this.getTrimmedValue(parser.nextText()));
            }
            else if (this.checkFieldWithDuplicate(parser, "exists", null, parsed)) {
                activationFile.setExists(this.getTrimmedValue(parser.nextText()));
            }
            else {
                if (strict) {
                    throw new XmlPullParserException("Unrecognised tag: '" + parser.getName() + "'", parser, null);
                }
                while (parser.next() != 3) {}
            }
        }
        return activationFile;
    }
    
    private ActivationOS parseActivationOS(final String tagName, final XmlPullParser parser, final boolean strict) throws IOException, XmlPullParserException {
        final ActivationOS activationOS = new ActivationOS();
        final Set parsed = new HashSet();
        while (parser.nextTag() == 2) {
            if (this.checkFieldWithDuplicate(parser, "name", null, parsed)) {
                activationOS.setName(this.getTrimmedValue(parser.nextText()));
            }
            else if (this.checkFieldWithDuplicate(parser, "family", null, parsed)) {
                activationOS.setFamily(this.getTrimmedValue(parser.nextText()));
            }
            else if (this.checkFieldWithDuplicate(parser, "arch", null, parsed)) {
                activationOS.setArch(this.getTrimmedValue(parser.nextText()));
            }
            else if (this.checkFieldWithDuplicate(parser, "version", null, parsed)) {
                activationOS.setVersion(this.getTrimmedValue(parser.nextText()));
            }
            else {
                if (strict) {
                    throw new XmlPullParserException("Unrecognised tag: '" + parser.getName() + "'", parser, null);
                }
                while (parser.next() != 3) {}
            }
        }
        return activationOS;
    }
    
    private ActivationProperty parseActivationProperty(final String tagName, final XmlPullParser parser, final boolean strict) throws IOException, XmlPullParserException {
        final ActivationProperty activationProperty = new ActivationProperty();
        final Set parsed = new HashSet();
        while (parser.nextTag() == 2) {
            if (this.checkFieldWithDuplicate(parser, "name", null, parsed)) {
                activationProperty.setName(this.getTrimmedValue(parser.nextText()));
            }
            else if (this.checkFieldWithDuplicate(parser, "value", null, parsed)) {
                activationProperty.setValue(this.getTrimmedValue(parser.nextText()));
            }
            else {
                if (strict) {
                    throw new XmlPullParserException("Unrecognised tag: '" + parser.getName() + "'", parser, null);
                }
                while (parser.next() != 3) {}
            }
        }
        return activationProperty;
    }
    
    private IdentifiableBase parseIdentifiableBase(final String tagName, final XmlPullParser parser, final boolean strict) throws IOException, XmlPullParserException {
        final IdentifiableBase identifiableBase = new IdentifiableBase();
        final Set parsed = new HashSet();
        while (parser.nextTag() == 2) {
            if (this.checkFieldWithDuplicate(parser, "id", null, parsed)) {
                identifiableBase.setId(this.getTrimmedValue(parser.nextText()));
            }
            else {
                if (strict) {
                    throw new XmlPullParserException("Unrecognised tag: '" + parser.getName() + "'", parser, null);
                }
                while (parser.next() != 3) {}
            }
        }
        return identifiableBase;
    }
    
    private Mirror parseMirror(final String tagName, final XmlPullParser parser, final boolean strict) throws IOException, XmlPullParserException {
        final Mirror mirror = new Mirror();
        final Set parsed = new HashSet();
        while (parser.nextTag() == 2) {
            if (this.checkFieldWithDuplicate(parser, "mirrorOf", null, parsed)) {
                mirror.setMirrorOf(this.getTrimmedValue(parser.nextText()));
            }
            else if (this.checkFieldWithDuplicate(parser, "name", null, parsed)) {
                mirror.setName(this.getTrimmedValue(parser.nextText()));
            }
            else if (this.checkFieldWithDuplicate(parser, "url", null, parsed)) {
                mirror.setUrl(this.getTrimmedValue(parser.nextText()));
            }
            else if (this.checkFieldWithDuplicate(parser, "id", null, parsed)) {
                mirror.setId(this.getTrimmedValue(parser.nextText()));
            }
            else {
                if (strict) {
                    throw new XmlPullParserException("Unrecognised tag: '" + parser.getName() + "'", parser, null);
                }
                while (parser.next() != 3) {}
            }
        }
        return mirror;
    }
    
    private Profile parseProfile(final String tagName, final XmlPullParser parser, final boolean strict) throws IOException, XmlPullParserException {
        final Profile profile = new Profile();
        final Set parsed = new HashSet();
        while (parser.nextTag() == 2) {
            if (this.checkFieldWithDuplicate(parser, "activation", null, parsed)) {
                profile.setActivation(this.parseActivation("activation", parser, strict));
            }
            else if (this.checkFieldWithDuplicate(parser, "properties", null, parsed)) {
                while (parser.nextTag() == 2) {
                    final String key = parser.getName();
                    final String value = parser.nextText().trim();
                    profile.addProperty(key, value);
                }
            }
            else if (this.checkFieldWithDuplicate(parser, "repositories", null, parsed)) {
                final List repositories = new ArrayList();
                profile.setRepositories(repositories);
                while (parser.nextTag() == 2) {
                    if (parser.getName().equals("repository")) {
                        repositories.add(this.parseRepository("repository", parser, strict));
                    }
                    else {
                        if (strict) {
                            throw new XmlPullParserException("Unrecognised association: '" + parser.getName() + "'", parser, null);
                        }
                        while (parser.next() != 3) {}
                    }
                }
            }
            else if (this.checkFieldWithDuplicate(parser, "pluginRepositories", null, parsed)) {
                final List pluginRepositories = new ArrayList();
                profile.setPluginRepositories(pluginRepositories);
                while (parser.nextTag() == 2) {
                    if (parser.getName().equals("pluginRepository")) {
                        pluginRepositories.add(this.parseRepository("pluginRepository", parser, strict));
                    }
                    else {
                        if (strict) {
                            throw new XmlPullParserException("Unrecognised association: '" + parser.getName() + "'", parser, null);
                        }
                        while (parser.next() != 3) {}
                    }
                }
            }
            else if (this.checkFieldWithDuplicate(parser, "id", null, parsed)) {
                profile.setId(this.getTrimmedValue(parser.nextText()));
            }
            else {
                if (strict) {
                    throw new XmlPullParserException("Unrecognised tag: '" + parser.getName() + "'", parser, null);
                }
                while (parser.next() != 3) {}
            }
        }
        return profile;
    }
    
    private Proxy parseProxy(final String tagName, final XmlPullParser parser, final boolean strict) throws IOException, XmlPullParserException {
        final Proxy proxy = new Proxy();
        final Set parsed = new HashSet();
        while (parser.nextTag() == 2) {
            if (this.checkFieldWithDuplicate(parser, "active", null, parsed)) {
                proxy.setActive(this.getBooleanValue(this.getTrimmedValue(parser.nextText()), "active", parser, "true"));
            }
            else if (this.checkFieldWithDuplicate(parser, "protocol", null, parsed)) {
                proxy.setProtocol(this.getTrimmedValue(parser.nextText()));
            }
            else if (this.checkFieldWithDuplicate(parser, "username", null, parsed)) {
                proxy.setUsername(this.getTrimmedValue(parser.nextText()));
            }
            else if (this.checkFieldWithDuplicate(parser, "password", null, parsed)) {
                proxy.setPassword(this.getTrimmedValue(parser.nextText()));
            }
            else if (this.checkFieldWithDuplicate(parser, "port", null, parsed)) {
                proxy.setPort(this.getIntegerValue(this.getTrimmedValue(parser.nextText()), "port", parser, strict));
            }
            else if (this.checkFieldWithDuplicate(parser, "host", null, parsed)) {
                proxy.setHost(this.getTrimmedValue(parser.nextText()));
            }
            else if (this.checkFieldWithDuplicate(parser, "nonProxyHosts", null, parsed)) {
                proxy.setNonProxyHosts(this.getTrimmedValue(parser.nextText()));
            }
            else if (this.checkFieldWithDuplicate(parser, "id", null, parsed)) {
                proxy.setId(this.getTrimmedValue(parser.nextText()));
            }
            else {
                if (strict) {
                    throw new XmlPullParserException("Unrecognised tag: '" + parser.getName() + "'", parser, null);
                }
                while (parser.next() != 3) {}
            }
        }
        return proxy;
    }
    
    private Repository parseRepository(final String tagName, final XmlPullParser parser, final boolean strict) throws IOException, XmlPullParserException {
        final Repository repository = new Repository();
        final Set parsed = new HashSet();
        while (parser.nextTag() == 2) {
            if (this.checkFieldWithDuplicate(parser, "releases", null, parsed)) {
                repository.setReleases(this.parseRepositoryPolicy("releases", parser, strict));
            }
            else if (this.checkFieldWithDuplicate(parser, "snapshots", null, parsed)) {
                repository.setSnapshots(this.parseRepositoryPolicy("snapshots", parser, strict));
            }
            else if (this.checkFieldWithDuplicate(parser, "id", null, parsed)) {
                repository.setId(this.getTrimmedValue(parser.nextText()));
            }
            else if (this.checkFieldWithDuplicate(parser, "name", null, parsed)) {
                repository.setName(this.getTrimmedValue(parser.nextText()));
            }
            else if (this.checkFieldWithDuplicate(parser, "url", null, parsed)) {
                repository.setUrl(this.getTrimmedValue(parser.nextText()));
            }
            else if (this.checkFieldWithDuplicate(parser, "layout", null, parsed)) {
                repository.setLayout(this.getTrimmedValue(parser.nextText()));
            }
            else {
                if (strict) {
                    throw new XmlPullParserException("Unrecognised tag: '" + parser.getName() + "'", parser, null);
                }
                while (parser.next() != 3) {}
            }
        }
        return repository;
    }
    
    private RepositoryBase parseRepositoryBase(final String tagName, final XmlPullParser parser, final boolean strict) throws IOException, XmlPullParserException {
        final RepositoryBase repositoryBase = new RepositoryBase();
        final Set parsed = new HashSet();
        while (parser.nextTag() == 2) {
            if (this.checkFieldWithDuplicate(parser, "id", null, parsed)) {
                repositoryBase.setId(this.getTrimmedValue(parser.nextText()));
            }
            else if (this.checkFieldWithDuplicate(parser, "name", null, parsed)) {
                repositoryBase.setName(this.getTrimmedValue(parser.nextText()));
            }
            else if (this.checkFieldWithDuplicate(parser, "url", null, parsed)) {
                repositoryBase.setUrl(this.getTrimmedValue(parser.nextText()));
            }
            else if (this.checkFieldWithDuplicate(parser, "layout", null, parsed)) {
                repositoryBase.setLayout(this.getTrimmedValue(parser.nextText()));
            }
            else {
                if (strict) {
                    throw new XmlPullParserException("Unrecognised tag: '" + parser.getName() + "'", parser, null);
                }
                while (parser.next() != 3) {}
            }
        }
        return repositoryBase;
    }
    
    private RepositoryPolicy parseRepositoryPolicy(final String tagName, final XmlPullParser parser, final boolean strict) throws IOException, XmlPullParserException {
        final RepositoryPolicy repositoryPolicy = new RepositoryPolicy();
        final Set parsed = new HashSet();
        while (parser.nextTag() == 2) {
            if (this.checkFieldWithDuplicate(parser, "enabled", null, parsed)) {
                repositoryPolicy.setEnabled(this.getBooleanValue(this.getTrimmedValue(parser.nextText()), "enabled", parser, "true"));
            }
            else if (this.checkFieldWithDuplicate(parser, "updatePolicy", null, parsed)) {
                repositoryPolicy.setUpdatePolicy(this.getTrimmedValue(parser.nextText()));
            }
            else if (this.checkFieldWithDuplicate(parser, "checksumPolicy", null, parsed)) {
                repositoryPolicy.setChecksumPolicy(this.getTrimmedValue(parser.nextText()));
            }
            else {
                if (strict) {
                    throw new XmlPullParserException("Unrecognised tag: '" + parser.getName() + "'", parser, null);
                }
                while (parser.next() != 3) {}
            }
        }
        return repositoryPolicy;
    }
    
    private Server parseServer(final String tagName, final XmlPullParser parser, final boolean strict) throws IOException, XmlPullParserException {
        final Server server = new Server();
        final Set parsed = new HashSet();
        while (parser.nextTag() == 2) {
            if (this.checkFieldWithDuplicate(parser, "username", null, parsed)) {
                server.setUsername(this.getTrimmedValue(parser.nextText()));
            }
            else if (this.checkFieldWithDuplicate(parser, "password", null, parsed)) {
                server.setPassword(this.getTrimmedValue(parser.nextText()));
            }
            else if (this.checkFieldWithDuplicate(parser, "privateKey", null, parsed)) {
                server.setPrivateKey(this.getTrimmedValue(parser.nextText()));
            }
            else if (this.checkFieldWithDuplicate(parser, "passphrase", null, parsed)) {
                server.setPassphrase(this.getTrimmedValue(parser.nextText()));
            }
            else if (this.checkFieldWithDuplicate(parser, "filePermissions", null, parsed)) {
                server.setFilePermissions(this.getTrimmedValue(parser.nextText()));
            }
            else if (this.checkFieldWithDuplicate(parser, "directoryPermissions", null, parsed)) {
                server.setDirectoryPermissions(this.getTrimmedValue(parser.nextText()));
            }
            else if (this.checkFieldWithDuplicate(parser, "configuration", null, parsed)) {
                server.setConfiguration(Xpp3DomBuilder.build(parser));
            }
            else if (this.checkFieldWithDuplicate(parser, "id", null, parsed)) {
                server.setId(this.getTrimmedValue(parser.nextText()));
            }
            else {
                if (strict) {
                    throw new XmlPullParserException("Unrecognised tag: '" + parser.getName() + "'", parser, null);
                }
                while (parser.next() != 3) {}
            }
        }
        return server;
    }
    
    private Settings parseSettings(final String tagName, final XmlPullParser parser, final boolean strict) throws IOException, XmlPullParserException {
        final Settings settings = new Settings();
        final Set parsed = new HashSet();
        int eventType = parser.getEventType();
        boolean foundRoot = false;
        while (eventType != 1) {
            if (eventType == 2) {
                if (parser.getName().equals(tagName)) {
                    foundRoot = true;
                }
                else {
                    if (strict && !foundRoot) {
                        throw new XmlPullParserException("Expected root element '" + tagName + "' but found '" + parser.getName() + "'", parser, null);
                    }
                    if (this.checkFieldWithDuplicate(parser, "localRepository", null, parsed)) {
                        settings.setLocalRepository(this.getTrimmedValue(parser.nextText()));
                    }
                    else if (this.checkFieldWithDuplicate(parser, "interactiveMode", null, parsed)) {
                        settings.setInteractiveMode(this.getBooleanValue(this.getTrimmedValue(parser.nextText()), "interactiveMode", parser, "true"));
                    }
                    else if (this.checkFieldWithDuplicate(parser, "usePluginRegistry", null, parsed)) {
                        settings.setUsePluginRegistry(this.getBooleanValue(this.getTrimmedValue(parser.nextText()), "usePluginRegistry", parser, "false"));
                    }
                    else if (this.checkFieldWithDuplicate(parser, "offline", null, parsed)) {
                        settings.setOffline(this.getBooleanValue(this.getTrimmedValue(parser.nextText()), "offline", parser, "false"));
                    }
                    else if (this.checkFieldWithDuplicate(parser, "proxies", null, parsed)) {
                        final List proxies = new ArrayList();
                        settings.setProxies(proxies);
                        while (parser.nextTag() == 2) {
                            if (parser.getName().equals("proxy")) {
                                proxies.add(this.parseProxy("proxy", parser, strict));
                            }
                            else {
                                if (strict) {
                                    throw new XmlPullParserException("Unrecognised association: '" + parser.getName() + "'", parser, null);
                                }
                                while (parser.next() != 3) {}
                            }
                        }
                    }
                    else if (this.checkFieldWithDuplicate(parser, "servers", null, parsed)) {
                        final List servers = new ArrayList();
                        settings.setServers(servers);
                        while (parser.nextTag() == 2) {
                            if (parser.getName().equals("server")) {
                                servers.add(this.parseServer("server", parser, strict));
                            }
                            else {
                                if (strict) {
                                    throw new XmlPullParserException("Unrecognised association: '" + parser.getName() + "'", parser, null);
                                }
                                while (parser.next() != 3) {}
                            }
                        }
                    }
                    else if (this.checkFieldWithDuplicate(parser, "mirrors", null, parsed)) {
                        final List mirrors = new ArrayList();
                        settings.setMirrors(mirrors);
                        while (parser.nextTag() == 2) {
                            if (parser.getName().equals("mirror")) {
                                mirrors.add(this.parseMirror("mirror", parser, strict));
                            }
                            else {
                                if (strict) {
                                    throw new XmlPullParserException("Unrecognised association: '" + parser.getName() + "'", parser, null);
                                }
                                while (parser.next() != 3) {}
                            }
                        }
                    }
                    else if (this.checkFieldWithDuplicate(parser, "profiles", null, parsed)) {
                        final List profiles = new ArrayList();
                        settings.setProfiles(profiles);
                        while (parser.nextTag() == 2) {
                            if (parser.getName().equals("profile")) {
                                profiles.add(this.parseProfile("profile", parser, strict));
                            }
                            else {
                                if (strict) {
                                    throw new XmlPullParserException("Unrecognised association: '" + parser.getName() + "'", parser, null);
                                }
                                while (parser.next() != 3) {}
                            }
                        }
                    }
                    else if (this.checkFieldWithDuplicate(parser, "activeProfiles", null, parsed)) {
                        final List activeProfiles = new ArrayList();
                        settings.setActiveProfiles(activeProfiles);
                        while (parser.nextTag() == 2) {
                            if (parser.getName().equals("activeProfile")) {
                                activeProfiles.add(this.getTrimmedValue(parser.nextText()));
                            }
                            else {
                                if (strict) {
                                    throw new XmlPullParserException("Unrecognised association: '" + parser.getName() + "'", parser, null);
                                }
                                while (parser.next() != 3) {}
                            }
                        }
                    }
                    else if (this.checkFieldWithDuplicate(parser, "pluginGroups", null, parsed)) {
                        final List pluginGroups = new ArrayList();
                        settings.setPluginGroups(pluginGroups);
                        while (parser.nextTag() == 2) {
                            if (parser.getName().equals("pluginGroup")) {
                                pluginGroups.add(this.getTrimmedValue(parser.nextText()));
                            }
                            else {
                                if (strict) {
                                    throw new XmlPullParserException("Unrecognised association: '" + parser.getName() + "'", parser, null);
                                }
                                while (parser.next() != 3) {}
                            }
                        }
                    }
                    else if (strict) {
                        throw new XmlPullParserException("Unrecognised tag: '" + parser.getName() + "'", parser, null);
                    }
                }
            }
            eventType = parser.next();
        }
        return settings;
    }
    
    private TrackableBase parseTrackableBase(final String tagName, final XmlPullParser parser, final boolean strict) throws IOException, XmlPullParserException {
        final TrackableBase trackableBase = new TrackableBase();
        final Set parsed = new HashSet();
        while (parser.nextTag() == 2) {
            if (strict) {
                throw new XmlPullParserException("Unrecognised tag: '" + parser.getName() + "'", parser, null);
            }
            while (parser.next() != 3) {}
        }
        return trackableBase;
    }
    
    public Settings read(final Reader reader, final boolean strict) throws IOException, XmlPullParserException {
        final XmlPullParser parser = new MXParser();
        parser.setInput(reader);
        if (this.addDefaultEntities) {
            parser.defineEntityReplacementText("nbsp", " ");
            parser.defineEntityReplacementText("iexcl", "¡");
            parser.defineEntityReplacementText("cent", "¢");
            parser.defineEntityReplacementText("pound", "£");
            parser.defineEntityReplacementText("curren", "¤");
            parser.defineEntityReplacementText("yen", "¥");
            parser.defineEntityReplacementText("brvbar", "¦");
            parser.defineEntityReplacementText("sect", "§");
            parser.defineEntityReplacementText("uml", "¨");
            parser.defineEntityReplacementText("copy", "©");
            parser.defineEntityReplacementText("ordf", "ª");
            parser.defineEntityReplacementText("laquo", "«");
            parser.defineEntityReplacementText("not", "¬");
            parser.defineEntityReplacementText("shy", "\u00ad");
            parser.defineEntityReplacementText("reg", "®");
            parser.defineEntityReplacementText("macr", "¯");
            parser.defineEntityReplacementText("deg", "°");
            parser.defineEntityReplacementText("plusmn", "±");
            parser.defineEntityReplacementText("sup2", "²");
            parser.defineEntityReplacementText("sup3", "³");
            parser.defineEntityReplacementText("acute", "´");
            parser.defineEntityReplacementText("micro", "µ");
            parser.defineEntityReplacementText("para", "¶");
            parser.defineEntityReplacementText("middot", "·");
            parser.defineEntityReplacementText("cedil", "¸");
            parser.defineEntityReplacementText("sup1", "¹");
            parser.defineEntityReplacementText("ordm", "º");
            parser.defineEntityReplacementText("raquo", "»");
            parser.defineEntityReplacementText("frac14", "¼");
            parser.defineEntityReplacementText("frac12", "½");
            parser.defineEntityReplacementText("frac34", "¾");
            parser.defineEntityReplacementText("iquest", "¿");
            parser.defineEntityReplacementText("Agrave", "\u00c0");
            parser.defineEntityReplacementText("Aacute", "\u00c1");
            parser.defineEntityReplacementText("Acirc", "\u00c2");
            parser.defineEntityReplacementText("Atilde", "\u00c3");
            parser.defineEntityReplacementText("Auml", "\u00c4");
            parser.defineEntityReplacementText("Aring", "\u00c5");
            parser.defineEntityReplacementText("AElig", "\u00c6");
            parser.defineEntityReplacementText("Ccedil", "\u00c7");
            parser.defineEntityReplacementText("Egrave", "\u00c8");
            parser.defineEntityReplacementText("Eacute", "\u00c9");
            parser.defineEntityReplacementText("Ecirc", "\u00ca");
            parser.defineEntityReplacementText("Euml", "\u00cb");
            parser.defineEntityReplacementText("Igrave", "\u00cc");
            parser.defineEntityReplacementText("Iacute", "\u00cd");
            parser.defineEntityReplacementText("Icirc", "\u00ce");
            parser.defineEntityReplacementText("Iuml", "\u00cf");
            parser.defineEntityReplacementText("ETH", "\u00d0");
            parser.defineEntityReplacementText("Ntilde", "\u00d1");
            parser.defineEntityReplacementText("Ograve", "\u00d2");
            parser.defineEntityReplacementText("Oacute", "\u00d3");
            parser.defineEntityReplacementText("Ocirc", "\u00d4");
            parser.defineEntityReplacementText("Otilde", "\u00d5");
            parser.defineEntityReplacementText("Ouml", "\u00d6");
            parser.defineEntityReplacementText("times", "\u00d7");
            parser.defineEntityReplacementText("Oslash", "\u00d8");
            parser.defineEntityReplacementText("Ugrave", "\u00d9");
            parser.defineEntityReplacementText("Uacute", "\u00da");
            parser.defineEntityReplacementText("Ucirc", "\u00db");
            parser.defineEntityReplacementText("Uuml", "\u00dc");
            parser.defineEntityReplacementText("Yacute", "\u00dd");
            parser.defineEntityReplacementText("THORN", "\u00de");
            parser.defineEntityReplacementText("szlig", "\u00df");
            parser.defineEntityReplacementText("agrave", "\u00e0");
            parser.defineEntityReplacementText("aacute", "\u00e1");
            parser.defineEntityReplacementText("acirc", "\u00e2");
            parser.defineEntityReplacementText("atilde", "\u00e3");
            parser.defineEntityReplacementText("auml", "\u00e4");
            parser.defineEntityReplacementText("aring", "\u00e5");
            parser.defineEntityReplacementText("aelig", "\u00e6");
            parser.defineEntityReplacementText("ccedil", "\u00e7");
            parser.defineEntityReplacementText("egrave", "\u00e8");
            parser.defineEntityReplacementText("eacute", "\u00e9");
            parser.defineEntityReplacementText("ecirc", "\u00ea");
            parser.defineEntityReplacementText("euml", "\u00eb");
            parser.defineEntityReplacementText("igrave", "\u00ec");
            parser.defineEntityReplacementText("iacute", "\u00ed");
            parser.defineEntityReplacementText("icirc", "\u00ee");
            parser.defineEntityReplacementText("iuml", "\u00ef");
            parser.defineEntityReplacementText("eth", "\u00f0");
            parser.defineEntityReplacementText("ntilde", "\u00f1");
            parser.defineEntityReplacementText("ograve", "\u00f2");
            parser.defineEntityReplacementText("oacute", "\u00f3");
            parser.defineEntityReplacementText("ocirc", "\u00f4");
            parser.defineEntityReplacementText("otilde", "\u00f5");
            parser.defineEntityReplacementText("ouml", "\u00f6");
            parser.defineEntityReplacementText("divide", "\u00f7");
            parser.defineEntityReplacementText("oslash", "\u00f8");
            parser.defineEntityReplacementText("ugrave", "\u00f9");
            parser.defineEntityReplacementText("uacute", "\u00fa");
            parser.defineEntityReplacementText("ucirc", "\u00fb");
            parser.defineEntityReplacementText("uuml", "\u00fc");
            parser.defineEntityReplacementText("yacute", "\u00fd");
            parser.defineEntityReplacementText("thorn", "\u00fe");
            parser.defineEntityReplacementText("yuml", "\u00ff");
            parser.defineEntityReplacementText("OElig", "\u0152");
            parser.defineEntityReplacementText("oelig", "\u0153");
            parser.defineEntityReplacementText("Scaron", "\u0160");
            parser.defineEntityReplacementText("scaron", "\u0161");
            parser.defineEntityReplacementText("Yuml", "\u0178");
            parser.defineEntityReplacementText("circ", "\u02c6");
            parser.defineEntityReplacementText("tilde", "\u02dc");
            parser.defineEntityReplacementText("ensp", "\u2002");
            parser.defineEntityReplacementText("emsp", "\u2003");
            parser.defineEntityReplacementText("thinsp", "\u2009");
            parser.defineEntityReplacementText("zwnj", "\u200c");
            parser.defineEntityReplacementText("zwj", "\u200d");
            parser.defineEntityReplacementText("lrm", "\u200e");
            parser.defineEntityReplacementText("rlm", "\u200f");
            parser.defineEntityReplacementText("ndash", "\u2013");
            parser.defineEntityReplacementText("mdash", "\u2014");
            parser.defineEntityReplacementText("lsquo", "\u2018");
            parser.defineEntityReplacementText("rsquo", "\u2019");
            parser.defineEntityReplacementText("sbquo", "\u201a");
            parser.defineEntityReplacementText("ldquo", "\u201c");
            parser.defineEntityReplacementText("rdquo", "\u201d");
            parser.defineEntityReplacementText("bdquo", "\u201e");
            parser.defineEntityReplacementText("dagger", "\u2020");
            parser.defineEntityReplacementText("Dagger", "\u2021");
            parser.defineEntityReplacementText("permil", "\u2030");
            parser.defineEntityReplacementText("lsaquo", "\u2039");
            parser.defineEntityReplacementText("rsaquo", "\u203a");
            parser.defineEntityReplacementText("euro", "\u20ac");
            parser.defineEntityReplacementText("fnof", "\u0192");
            parser.defineEntityReplacementText("Alpha", "\u0391");
            parser.defineEntityReplacementText("Beta", "\u0392");
            parser.defineEntityReplacementText("Gamma", "\u0393");
            parser.defineEntityReplacementText("Delta", "\u0394");
            parser.defineEntityReplacementText("Epsilon", "\u0395");
            parser.defineEntityReplacementText("Zeta", "\u0396");
            parser.defineEntityReplacementText("Eta", "\u0397");
            parser.defineEntityReplacementText("Theta", "\u0398");
            parser.defineEntityReplacementText("Iota", "\u0399");
            parser.defineEntityReplacementText("Kappa", "\u039a");
            parser.defineEntityReplacementText("Lambda", "\u039b");
            parser.defineEntityReplacementText("Mu", "\u039c");
            parser.defineEntityReplacementText("Nu", "\u039d");
            parser.defineEntityReplacementText("Xi", "\u039e");
            parser.defineEntityReplacementText("Omicron", "\u039f");
            parser.defineEntityReplacementText("Pi", "\u03a0");
            parser.defineEntityReplacementText("Rho", "\u03a1");
            parser.defineEntityReplacementText("Sigma", "\u03a3");
            parser.defineEntityReplacementText("Tau", "\u03a4");
            parser.defineEntityReplacementText("Upsilon", "\u03a5");
            parser.defineEntityReplacementText("Phi", "\u03a6");
            parser.defineEntityReplacementText("Chi", "\u03a7");
            parser.defineEntityReplacementText("Psi", "\u03a8");
            parser.defineEntityReplacementText("Omega", "\u03a9");
            parser.defineEntityReplacementText("alpha", "\u03b1");
            parser.defineEntityReplacementText("beta", "\u03b2");
            parser.defineEntityReplacementText("gamma", "\u03b3");
            parser.defineEntityReplacementText("delta", "\u03b4");
            parser.defineEntityReplacementText("epsilon", "\u03b5");
            parser.defineEntityReplacementText("zeta", "\u03b6");
            parser.defineEntityReplacementText("eta", "\u03b7");
            parser.defineEntityReplacementText("theta", "\u03b8");
            parser.defineEntityReplacementText("iota", "\u03b9");
            parser.defineEntityReplacementText("kappa", "\u03ba");
            parser.defineEntityReplacementText("lambda", "\u03bb");
            parser.defineEntityReplacementText("mu", "\u03bc");
            parser.defineEntityReplacementText("nu", "\u03bd");
            parser.defineEntityReplacementText("xi", "\u03be");
            parser.defineEntityReplacementText("omicron", "\u03bf");
            parser.defineEntityReplacementText("pi", "\u03c0");
            parser.defineEntityReplacementText("rho", "\u03c1");
            parser.defineEntityReplacementText("sigmaf", "\u03c2");
            parser.defineEntityReplacementText("sigma", "\u03c3");
            parser.defineEntityReplacementText("tau", "\u03c4");
            parser.defineEntityReplacementText("upsilon", "\u03c5");
            parser.defineEntityReplacementText("phi", "\u03c6");
            parser.defineEntityReplacementText("chi", "\u03c7");
            parser.defineEntityReplacementText("psi", "\u03c8");
            parser.defineEntityReplacementText("omega", "\u03c9");
            parser.defineEntityReplacementText("thetasym", "\u03d1");
            parser.defineEntityReplacementText("upsih", "\u03d2");
            parser.defineEntityReplacementText("piv", "\u03d6");
            parser.defineEntityReplacementText("bull", "\u2022");
            parser.defineEntityReplacementText("hellip", "\u2026");
            parser.defineEntityReplacementText("prime", "\u2032");
            parser.defineEntityReplacementText("Prime", "\u2033");
            parser.defineEntityReplacementText("oline", "\u203e");
            parser.defineEntityReplacementText("frasl", "\u2044");
            parser.defineEntityReplacementText("weierp", "\u2118");
            parser.defineEntityReplacementText("image", "\u2111");
            parser.defineEntityReplacementText("real", "\u211c");
            parser.defineEntityReplacementText("trade", "\u2122");
            parser.defineEntityReplacementText("alefsym", "\u2135");
            parser.defineEntityReplacementText("larr", "\u2190");
            parser.defineEntityReplacementText("uarr", "\u2191");
            parser.defineEntityReplacementText("rarr", "\u2192");
            parser.defineEntityReplacementText("darr", "\u2193");
            parser.defineEntityReplacementText("harr", "\u2194");
            parser.defineEntityReplacementText("crarr", "\u21b5");
            parser.defineEntityReplacementText("lArr", "\u21d0");
            parser.defineEntityReplacementText("uArr", "\u21d1");
            parser.defineEntityReplacementText("rArr", "\u21d2");
            parser.defineEntityReplacementText("dArr", "\u21d3");
            parser.defineEntityReplacementText("hArr", "\u21d4");
            parser.defineEntityReplacementText("forall", "\u2200");
            parser.defineEntityReplacementText("part", "\u2202");
            parser.defineEntityReplacementText("exist", "\u2203");
            parser.defineEntityReplacementText("empty", "\u2205");
            parser.defineEntityReplacementText("nabla", "\u2207");
            parser.defineEntityReplacementText("isin", "\u2208");
            parser.defineEntityReplacementText("notin", "\u2209");
            parser.defineEntityReplacementText("ni", "\u220b");
            parser.defineEntityReplacementText("prod", "\u220f");
            parser.defineEntityReplacementText("sum", "\u2211");
            parser.defineEntityReplacementText("minus", "\u2212");
            parser.defineEntityReplacementText("lowast", "\u2217");
            parser.defineEntityReplacementText("radic", "\u221a");
            parser.defineEntityReplacementText("prop", "\u221d");
            parser.defineEntityReplacementText("infin", "\u221e");
            parser.defineEntityReplacementText("ang", "\u2220");
            parser.defineEntityReplacementText("and", "\u2227");
            parser.defineEntityReplacementText("or", "\u2228");
            parser.defineEntityReplacementText("cap", "\u2229");
            parser.defineEntityReplacementText("cup", "\u222a");
            parser.defineEntityReplacementText("int", "\u222b");
            parser.defineEntityReplacementText("there4", "\u2234");
            parser.defineEntityReplacementText("sim", "\u223c");
            parser.defineEntityReplacementText("cong", "\u2245");
            parser.defineEntityReplacementText("asymp", "\u2248");
            parser.defineEntityReplacementText("ne", "\u2260");
            parser.defineEntityReplacementText("equiv", "\u2261");
            parser.defineEntityReplacementText("le", "\u2264");
            parser.defineEntityReplacementText("ge", "\u2265");
            parser.defineEntityReplacementText("sub", "\u2282");
            parser.defineEntityReplacementText("sup", "\u2283");
            parser.defineEntityReplacementText("nsub", "\u2284");
            parser.defineEntityReplacementText("sube", "\u2286");
            parser.defineEntityReplacementText("supe", "\u2287");
            parser.defineEntityReplacementText("oplus", "\u2295");
            parser.defineEntityReplacementText("otimes", "\u2297");
            parser.defineEntityReplacementText("perp", "\u22a5");
            parser.defineEntityReplacementText("sdot", "\u22c5");
            parser.defineEntityReplacementText("lceil", "\u2308");
            parser.defineEntityReplacementText("rceil", "\u2309");
            parser.defineEntityReplacementText("lfloor", "\u230a");
            parser.defineEntityReplacementText("rfloor", "\u230b");
            parser.defineEntityReplacementText("lang", "\u2329");
            parser.defineEntityReplacementText("rang", "\u232a");
            parser.defineEntityReplacementText("loz", "\u25ca");
            parser.defineEntityReplacementText("spades", "\u2660");
            parser.defineEntityReplacementText("clubs", "\u2663");
            parser.defineEntityReplacementText("hearts", "\u2665");
            parser.defineEntityReplacementText("diams", "\u2666");
        }
        parser.next();
        return this.parseSettings("settings", parser, strict);
    }
    
    public Settings read(final Reader reader) throws IOException, XmlPullParserException {
        return this.read(reader, true);
    }
    
    public Settings read(final InputStream in, final boolean strict) throws IOException, XmlPullParserException {
        final Reader reader = ReaderFactory.newXmlReader(in);
        return this.read(reader, strict);
    }
    
    public Settings read(final InputStream in) throws IOException, XmlPullParserException {
        final Reader reader = ReaderFactory.newXmlReader(in);
        return this.read(reader);
    }
    
    public void setAddDefaultEntities(final boolean addDefaultEntities) {
        this.addDefaultEntities = addDefaultEntities;
    }
}
