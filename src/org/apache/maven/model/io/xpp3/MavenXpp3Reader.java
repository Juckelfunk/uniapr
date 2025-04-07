// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.maven.model.io.xpp3;

import org.codehaus.plexus.util.ReaderFactory;
import java.io.InputStream;
import org.codehaus.plexus.util.xml.pull.MXParser;
import java.io.Reader;
import org.apache.maven.model.Site;
import org.apache.maven.model.Scm;
import org.apache.maven.model.RepositoryPolicy;
import org.apache.maven.model.RepositoryBase;
import org.apache.maven.model.Reporting;
import org.apache.maven.model.ReportSet;
import org.apache.maven.model.ReportPlugin;
import org.apache.maven.model.Relocation;
import org.apache.maven.model.Prerequisites;
import org.apache.maven.model.PluginManagement;
import org.apache.maven.model.PluginContainer;
import org.apache.maven.model.PluginConfiguration;
import org.apache.maven.model.PluginExecution;
import org.apache.maven.model.PatternSet;
import org.apache.maven.model.Parent;
import org.apache.maven.model.Organization;
import org.apache.maven.model.ModelBase;
import org.apache.maven.model.Repository;
import org.apache.maven.model.Profile;
import org.apache.maven.model.Model;
import org.apache.maven.model.MailingList;
import org.apache.maven.model.License;
import org.apache.maven.model.IssueManagement;
import org.apache.maven.model.FileSet;
import org.apache.maven.model.DistributionManagement;
import org.apache.maven.model.Developer;
import org.apache.maven.model.DeploymentRepository;
import org.apache.maven.model.DependencyManagement;
import org.apache.maven.model.Exclusion;
import org.apache.maven.model.Dependency;
import org.apache.maven.model.Contributor;
import org.codehaus.plexus.util.xml.Xpp3DomBuilder;
import org.apache.maven.model.ConfigurationContainer;
import org.apache.maven.model.Notifier;
import org.apache.maven.model.CiManagement;
import org.apache.maven.model.BuildBase;
import org.apache.maven.model.Plugin;
import org.apache.maven.model.Resource;
import org.apache.maven.model.Extension;
import java.util.List;
import java.util.ArrayList;
import org.apache.maven.model.Build;
import org.apache.maven.model.ActivationProperty;
import org.apache.maven.model.ActivationOS;
import org.apache.maven.model.ActivationFile;
import java.io.IOException;
import java.util.HashSet;
import org.apache.maven.model.Activation;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Locale;
import java.util.Date;
import org.codehaus.plexus.util.xml.pull.XmlPullParserException;
import java.util.Set;
import org.codehaus.plexus.util.xml.pull.XmlPullParser;

public class MavenXpp3Reader
{
    private boolean addDefaultEntities;
    
    public MavenXpp3Reader() {
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
    
    private Build parseBuild(final String tagName, final XmlPullParser parser, final boolean strict) throws IOException, XmlPullParserException {
        final Build build = new Build();
        final Set parsed = new HashSet();
        while (parser.nextTag() == 2) {
            if (this.checkFieldWithDuplicate(parser, "sourceDirectory", null, parsed)) {
                build.setSourceDirectory(this.getTrimmedValue(parser.nextText()));
            }
            else if (this.checkFieldWithDuplicate(parser, "scriptSourceDirectory", null, parsed)) {
                build.setScriptSourceDirectory(this.getTrimmedValue(parser.nextText()));
            }
            else if (this.checkFieldWithDuplicate(parser, "testSourceDirectory", null, parsed)) {
                build.setTestSourceDirectory(this.getTrimmedValue(parser.nextText()));
            }
            else if (this.checkFieldWithDuplicate(parser, "outputDirectory", null, parsed)) {
                build.setOutputDirectory(this.getTrimmedValue(parser.nextText()));
            }
            else if (this.checkFieldWithDuplicate(parser, "testOutputDirectory", null, parsed)) {
                build.setTestOutputDirectory(this.getTrimmedValue(parser.nextText()));
            }
            else if (this.checkFieldWithDuplicate(parser, "extensions", null, parsed)) {
                final List extensions = new ArrayList();
                build.setExtensions(extensions);
                while (parser.nextTag() == 2) {
                    if (parser.getName().equals("extension")) {
                        extensions.add(this.parseExtension("extension", parser, strict));
                    }
                    else {
                        if (strict) {
                            throw new XmlPullParserException("Unrecognised association: '" + parser.getName() + "'", parser, null);
                        }
                        while (parser.next() != 3) {}
                    }
                }
            }
            else if (this.checkFieldWithDuplicate(parser, "defaultGoal", null, parsed)) {
                build.setDefaultGoal(this.getTrimmedValue(parser.nextText()));
            }
            else if (this.checkFieldWithDuplicate(parser, "resources", null, parsed)) {
                final List resources = new ArrayList();
                build.setResources(resources);
                while (parser.nextTag() == 2) {
                    if (parser.getName().equals("resource")) {
                        resources.add(this.parseResource("resource", parser, strict));
                    }
                    else {
                        if (strict) {
                            throw new XmlPullParserException("Unrecognised association: '" + parser.getName() + "'", parser, null);
                        }
                        while (parser.next() != 3) {}
                    }
                }
            }
            else if (this.checkFieldWithDuplicate(parser, "testResources", null, parsed)) {
                final List testResources = new ArrayList();
                build.setTestResources(testResources);
                while (parser.nextTag() == 2) {
                    if (parser.getName().equals("testResource")) {
                        testResources.add(this.parseResource("testResource", parser, strict));
                    }
                    else {
                        if (strict) {
                            throw new XmlPullParserException("Unrecognised association: '" + parser.getName() + "'", parser, null);
                        }
                        while (parser.next() != 3) {}
                    }
                }
            }
            else if (this.checkFieldWithDuplicate(parser, "directory", null, parsed)) {
                build.setDirectory(this.getTrimmedValue(parser.nextText()));
            }
            else if (this.checkFieldWithDuplicate(parser, "finalName", null, parsed)) {
                build.setFinalName(this.getTrimmedValue(parser.nextText()));
            }
            else if (this.checkFieldWithDuplicate(parser, "filters", null, parsed)) {
                final List filters = new ArrayList();
                build.setFilters(filters);
                while (parser.nextTag() == 2) {
                    if (parser.getName().equals("filter")) {
                        filters.add(this.getTrimmedValue(parser.nextText()));
                    }
                    else {
                        if (strict) {
                            throw new XmlPullParserException("Unrecognised association: '" + parser.getName() + "'", parser, null);
                        }
                        while (parser.next() != 3) {}
                    }
                }
            }
            else if (this.checkFieldWithDuplicate(parser, "pluginManagement", null, parsed)) {
                build.setPluginManagement(this.parsePluginManagement("pluginManagement", parser, strict));
            }
            else if (this.checkFieldWithDuplicate(parser, "plugins", null, parsed)) {
                final List plugins = new ArrayList();
                build.setPlugins(plugins);
                while (parser.nextTag() == 2) {
                    if (parser.getName().equals("plugin")) {
                        plugins.add(this.parsePlugin("plugin", parser, strict));
                    }
                    else {
                        if (strict) {
                            throw new XmlPullParserException("Unrecognised association: '" + parser.getName() + "'", parser, null);
                        }
                        while (parser.next() != 3) {}
                    }
                }
            }
            else {
                if (strict) {
                    throw new XmlPullParserException("Unrecognised tag: '" + parser.getName() + "'", parser, null);
                }
                while (parser.next() != 3) {}
            }
        }
        return build;
    }
    
    private BuildBase parseBuildBase(final String tagName, final XmlPullParser parser, final boolean strict) throws IOException, XmlPullParserException {
        final BuildBase buildBase = new BuildBase();
        final Set parsed = new HashSet();
        while (parser.nextTag() == 2) {
            if (this.checkFieldWithDuplicate(parser, "defaultGoal", null, parsed)) {
                buildBase.setDefaultGoal(this.getTrimmedValue(parser.nextText()));
            }
            else if (this.checkFieldWithDuplicate(parser, "resources", null, parsed)) {
                final List resources = new ArrayList();
                buildBase.setResources(resources);
                while (parser.nextTag() == 2) {
                    if (parser.getName().equals("resource")) {
                        resources.add(this.parseResource("resource", parser, strict));
                    }
                    else {
                        if (strict) {
                            throw new XmlPullParserException("Unrecognised association: '" + parser.getName() + "'", parser, null);
                        }
                        while (parser.next() != 3) {}
                    }
                }
            }
            else if (this.checkFieldWithDuplicate(parser, "testResources", null, parsed)) {
                final List testResources = new ArrayList();
                buildBase.setTestResources(testResources);
                while (parser.nextTag() == 2) {
                    if (parser.getName().equals("testResource")) {
                        testResources.add(this.parseResource("testResource", parser, strict));
                    }
                    else {
                        if (strict) {
                            throw new XmlPullParserException("Unrecognised association: '" + parser.getName() + "'", parser, null);
                        }
                        while (parser.next() != 3) {}
                    }
                }
            }
            else if (this.checkFieldWithDuplicate(parser, "directory", null, parsed)) {
                buildBase.setDirectory(this.getTrimmedValue(parser.nextText()));
            }
            else if (this.checkFieldWithDuplicate(parser, "finalName", null, parsed)) {
                buildBase.setFinalName(this.getTrimmedValue(parser.nextText()));
            }
            else if (this.checkFieldWithDuplicate(parser, "filters", null, parsed)) {
                final List filters = new ArrayList();
                buildBase.setFilters(filters);
                while (parser.nextTag() == 2) {
                    if (parser.getName().equals("filter")) {
                        filters.add(this.getTrimmedValue(parser.nextText()));
                    }
                    else {
                        if (strict) {
                            throw new XmlPullParserException("Unrecognised association: '" + parser.getName() + "'", parser, null);
                        }
                        while (parser.next() != 3) {}
                    }
                }
            }
            else if (this.checkFieldWithDuplicate(parser, "pluginManagement", null, parsed)) {
                buildBase.setPluginManagement(this.parsePluginManagement("pluginManagement", parser, strict));
            }
            else if (this.checkFieldWithDuplicate(parser, "plugins", null, parsed)) {
                final List plugins = new ArrayList();
                buildBase.setPlugins(plugins);
                while (parser.nextTag() == 2) {
                    if (parser.getName().equals("plugin")) {
                        plugins.add(this.parsePlugin("plugin", parser, strict));
                    }
                    else {
                        if (strict) {
                            throw new XmlPullParserException("Unrecognised association: '" + parser.getName() + "'", parser, null);
                        }
                        while (parser.next() != 3) {}
                    }
                }
            }
            else {
                if (strict) {
                    throw new XmlPullParserException("Unrecognised tag: '" + parser.getName() + "'", parser, null);
                }
                while (parser.next() != 3) {}
            }
        }
        return buildBase;
    }
    
    private CiManagement parseCiManagement(final String tagName, final XmlPullParser parser, final boolean strict) throws IOException, XmlPullParserException {
        final CiManagement ciManagement = new CiManagement();
        final Set parsed = new HashSet();
        while (parser.nextTag() == 2) {
            if (this.checkFieldWithDuplicate(parser, "system", null, parsed)) {
                ciManagement.setSystem(this.getTrimmedValue(parser.nextText()));
            }
            else if (this.checkFieldWithDuplicate(parser, "url", null, parsed)) {
                ciManagement.setUrl(this.getTrimmedValue(parser.nextText()));
            }
            else if (this.checkFieldWithDuplicate(parser, "notifiers", null, parsed)) {
                final List notifiers = new ArrayList();
                ciManagement.setNotifiers(notifiers);
                while (parser.nextTag() == 2) {
                    if (parser.getName().equals("notifier")) {
                        notifiers.add(this.parseNotifier("notifier", parser, strict));
                    }
                    else {
                        if (strict) {
                            throw new XmlPullParserException("Unrecognised association: '" + parser.getName() + "'", parser, null);
                        }
                        while (parser.next() != 3) {}
                    }
                }
            }
            else {
                if (strict) {
                    throw new XmlPullParserException("Unrecognised tag: '" + parser.getName() + "'", parser, null);
                }
                while (parser.next() != 3) {}
            }
        }
        return ciManagement;
    }
    
    private ConfigurationContainer parseConfigurationContainer(final String tagName, final XmlPullParser parser, final boolean strict) throws IOException, XmlPullParserException {
        final ConfigurationContainer configurationContainer = new ConfigurationContainer();
        final Set parsed = new HashSet();
        while (parser.nextTag() == 2) {
            if (this.checkFieldWithDuplicate(parser, "inherited", null, parsed)) {
                configurationContainer.setInherited(this.getTrimmedValue(parser.nextText()));
            }
            else if (this.checkFieldWithDuplicate(parser, "configuration", null, parsed)) {
                configurationContainer.setConfiguration(Xpp3DomBuilder.build(parser));
            }
            else {
                if (strict) {
                    throw new XmlPullParserException("Unrecognised tag: '" + parser.getName() + "'", parser, null);
                }
                while (parser.next() != 3) {}
            }
        }
        return configurationContainer;
    }
    
    private Contributor parseContributor(final String tagName, final XmlPullParser parser, final boolean strict) throws IOException, XmlPullParserException {
        final Contributor contributor = new Contributor();
        final Set parsed = new HashSet();
        while (parser.nextTag() == 2) {
            if (this.checkFieldWithDuplicate(parser, "name", null, parsed)) {
                contributor.setName(this.getTrimmedValue(parser.nextText()));
            }
            else if (this.checkFieldWithDuplicate(parser, "email", null, parsed)) {
                contributor.setEmail(this.getTrimmedValue(parser.nextText()));
            }
            else if (this.checkFieldWithDuplicate(parser, "url", null, parsed)) {
                contributor.setUrl(this.getTrimmedValue(parser.nextText()));
            }
            else if (this.checkFieldWithDuplicate(parser, "organization", "organisation", parsed)) {
                contributor.setOrganization(this.getTrimmedValue(parser.nextText()));
            }
            else if (this.checkFieldWithDuplicate(parser, "organizationUrl", "organisationUrl", parsed)) {
                contributor.setOrganizationUrl(this.getTrimmedValue(parser.nextText()));
            }
            else if (this.checkFieldWithDuplicate(parser, "roles", null, parsed)) {
                final List roles = new ArrayList();
                contributor.setRoles(roles);
                while (parser.nextTag() == 2) {
                    if (parser.getName().equals("role")) {
                        roles.add(this.getTrimmedValue(parser.nextText()));
                    }
                    else {
                        if (strict) {
                            throw new XmlPullParserException("Unrecognised association: '" + parser.getName() + "'", parser, null);
                        }
                        while (parser.next() != 3) {}
                    }
                }
            }
            else if (this.checkFieldWithDuplicate(parser, "timezone", null, parsed)) {
                contributor.setTimezone(this.getTrimmedValue(parser.nextText()));
            }
            else if (this.checkFieldWithDuplicate(parser, "properties", null, parsed)) {
                while (parser.nextTag() == 2) {
                    final String key = parser.getName();
                    final String value = parser.nextText().trim();
                    contributor.addProperty(key, value);
                }
            }
            else {
                if (strict) {
                    throw new XmlPullParserException("Unrecognised tag: '" + parser.getName() + "'", parser, null);
                }
                while (parser.next() != 3) {}
            }
        }
        return contributor;
    }
    
    private Dependency parseDependency(final String tagName, final XmlPullParser parser, final boolean strict) throws IOException, XmlPullParserException {
        final Dependency dependency = new Dependency();
        final Set parsed = new HashSet();
        while (parser.nextTag() == 2) {
            if (this.checkFieldWithDuplicate(parser, "groupId", null, parsed)) {
                dependency.setGroupId(this.getTrimmedValue(parser.nextText()));
            }
            else if (this.checkFieldWithDuplicate(parser, "artifactId", null, parsed)) {
                dependency.setArtifactId(this.getTrimmedValue(parser.nextText()));
            }
            else if (this.checkFieldWithDuplicate(parser, "version", null, parsed)) {
                dependency.setVersion(this.getTrimmedValue(parser.nextText()));
            }
            else if (this.checkFieldWithDuplicate(parser, "type", null, parsed)) {
                dependency.setType(this.getTrimmedValue(parser.nextText()));
            }
            else if (this.checkFieldWithDuplicate(parser, "classifier", null, parsed)) {
                dependency.setClassifier(this.getTrimmedValue(parser.nextText()));
            }
            else if (this.checkFieldWithDuplicate(parser, "scope", null, parsed)) {
                dependency.setScope(this.getTrimmedValue(parser.nextText()));
            }
            else if (this.checkFieldWithDuplicate(parser, "systemPath", null, parsed)) {
                dependency.setSystemPath(this.getTrimmedValue(parser.nextText()));
            }
            else if (this.checkFieldWithDuplicate(parser, "exclusions", null, parsed)) {
                final List exclusions = new ArrayList();
                dependency.setExclusions(exclusions);
                while (parser.nextTag() == 2) {
                    if (parser.getName().equals("exclusion")) {
                        exclusions.add(this.parseExclusion("exclusion", parser, strict));
                    }
                    else {
                        if (strict) {
                            throw new XmlPullParserException("Unrecognised association: '" + parser.getName() + "'", parser, null);
                        }
                        while (parser.next() != 3) {}
                    }
                }
            }
            else if (this.checkFieldWithDuplicate(parser, "optional", null, parsed)) {
                dependency.setOptional(this.getBooleanValue(this.getTrimmedValue(parser.nextText()), "optional", parser, "false"));
            }
            else {
                if (strict) {
                    throw new XmlPullParserException("Unrecognised tag: '" + parser.getName() + "'", parser, null);
                }
                while (parser.next() != 3) {}
            }
        }
        return dependency;
    }
    
    private DependencyManagement parseDependencyManagement(final String tagName, final XmlPullParser parser, final boolean strict) throws IOException, XmlPullParserException {
        final DependencyManagement dependencyManagement = new DependencyManagement();
        final Set parsed = new HashSet();
        while (parser.nextTag() == 2) {
            if (this.checkFieldWithDuplicate(parser, "dependencies", null, parsed)) {
                final List dependencies = new ArrayList();
                dependencyManagement.setDependencies(dependencies);
                while (parser.nextTag() == 2) {
                    if (parser.getName().equals("dependency")) {
                        dependencies.add(this.parseDependency("dependency", parser, strict));
                    }
                    else {
                        if (strict) {
                            throw new XmlPullParserException("Unrecognised association: '" + parser.getName() + "'", parser, null);
                        }
                        while (parser.next() != 3) {}
                    }
                }
            }
            else {
                if (strict) {
                    throw new XmlPullParserException("Unrecognised tag: '" + parser.getName() + "'", parser, null);
                }
                while (parser.next() != 3) {}
            }
        }
        return dependencyManagement;
    }
    
    private DeploymentRepository parseDeploymentRepository(final String tagName, final XmlPullParser parser, final boolean strict) throws IOException, XmlPullParserException {
        final DeploymentRepository deploymentRepository = new DeploymentRepository();
        final Set parsed = new HashSet();
        while (parser.nextTag() == 2) {
            if (this.checkFieldWithDuplicate(parser, "uniqueVersion", null, parsed)) {
                deploymentRepository.setUniqueVersion(this.getBooleanValue(this.getTrimmedValue(parser.nextText()), "uniqueVersion", parser, "true"));
            }
            else if (this.checkFieldWithDuplicate(parser, "id", null, parsed)) {
                deploymentRepository.setId(this.getTrimmedValue(parser.nextText()));
            }
            else if (this.checkFieldWithDuplicate(parser, "name", null, parsed)) {
                deploymentRepository.setName(this.getTrimmedValue(parser.nextText()));
            }
            else if (this.checkFieldWithDuplicate(parser, "url", null, parsed)) {
                deploymentRepository.setUrl(this.getTrimmedValue(parser.nextText()));
            }
            else if (this.checkFieldWithDuplicate(parser, "layout", null, parsed)) {
                deploymentRepository.setLayout(this.getTrimmedValue(parser.nextText()));
            }
            else {
                if (strict) {
                    throw new XmlPullParserException("Unrecognised tag: '" + parser.getName() + "'", parser, null);
                }
                while (parser.next() != 3) {}
            }
        }
        return deploymentRepository;
    }
    
    private Developer parseDeveloper(final String tagName, final XmlPullParser parser, final boolean strict) throws IOException, XmlPullParserException {
        final Developer developer = new Developer();
        final Set parsed = new HashSet();
        while (parser.nextTag() == 2) {
            if (this.checkFieldWithDuplicate(parser, "id", null, parsed)) {
                developer.setId(this.getTrimmedValue(parser.nextText()));
            }
            else if (this.checkFieldWithDuplicate(parser, "name", null, parsed)) {
                developer.setName(this.getTrimmedValue(parser.nextText()));
            }
            else if (this.checkFieldWithDuplicate(parser, "email", null, parsed)) {
                developer.setEmail(this.getTrimmedValue(parser.nextText()));
            }
            else if (this.checkFieldWithDuplicate(parser, "url", null, parsed)) {
                developer.setUrl(this.getTrimmedValue(parser.nextText()));
            }
            else if (this.checkFieldWithDuplicate(parser, "organization", "organisation", parsed)) {
                developer.setOrganization(this.getTrimmedValue(parser.nextText()));
            }
            else if (this.checkFieldWithDuplicate(parser, "organizationUrl", "organisationUrl", parsed)) {
                developer.setOrganizationUrl(this.getTrimmedValue(parser.nextText()));
            }
            else if (this.checkFieldWithDuplicate(parser, "roles", null, parsed)) {
                final List roles = new ArrayList();
                developer.setRoles(roles);
                while (parser.nextTag() == 2) {
                    if (parser.getName().equals("role")) {
                        roles.add(this.getTrimmedValue(parser.nextText()));
                    }
                    else {
                        if (strict) {
                            throw new XmlPullParserException("Unrecognised association: '" + parser.getName() + "'", parser, null);
                        }
                        while (parser.next() != 3) {}
                    }
                }
            }
            else if (this.checkFieldWithDuplicate(parser, "timezone", null, parsed)) {
                developer.setTimezone(this.getTrimmedValue(parser.nextText()));
            }
            else if (this.checkFieldWithDuplicate(parser, "properties", null, parsed)) {
                while (parser.nextTag() == 2) {
                    final String key = parser.getName();
                    final String value = parser.nextText().trim();
                    developer.addProperty(key, value);
                }
            }
            else {
                if (strict) {
                    throw new XmlPullParserException("Unrecognised tag: '" + parser.getName() + "'", parser, null);
                }
                while (parser.next() != 3) {}
            }
        }
        return developer;
    }
    
    private DistributionManagement parseDistributionManagement(final String tagName, final XmlPullParser parser, final boolean strict) throws IOException, XmlPullParserException {
        final DistributionManagement distributionManagement = new DistributionManagement();
        final Set parsed = new HashSet();
        while (parser.nextTag() == 2) {
            if (this.checkFieldWithDuplicate(parser, "repository", null, parsed)) {
                distributionManagement.setRepository(this.parseDeploymentRepository("repository", parser, strict));
            }
            else if (this.checkFieldWithDuplicate(parser, "snapshotRepository", null, parsed)) {
                distributionManagement.setSnapshotRepository(this.parseDeploymentRepository("snapshotRepository", parser, strict));
            }
            else if (this.checkFieldWithDuplicate(parser, "site", null, parsed)) {
                distributionManagement.setSite(this.parseSite("site", parser, strict));
            }
            else if (this.checkFieldWithDuplicate(parser, "downloadUrl", null, parsed)) {
                distributionManagement.setDownloadUrl(this.getTrimmedValue(parser.nextText()));
            }
            else if (this.checkFieldWithDuplicate(parser, "relocation", null, parsed)) {
                distributionManagement.setRelocation(this.parseRelocation("relocation", parser, strict));
            }
            else if (this.checkFieldWithDuplicate(parser, "status", null, parsed)) {
                distributionManagement.setStatus(this.getTrimmedValue(parser.nextText()));
            }
            else {
                if (strict) {
                    throw new XmlPullParserException("Unrecognised tag: '" + parser.getName() + "'", parser, null);
                }
                while (parser.next() != 3) {}
            }
        }
        return distributionManagement;
    }
    
    private Exclusion parseExclusion(final String tagName, final XmlPullParser parser, final boolean strict) throws IOException, XmlPullParserException {
        final Exclusion exclusion = new Exclusion();
        final Set parsed = new HashSet();
        while (parser.nextTag() == 2) {
            if (this.checkFieldWithDuplicate(parser, "artifactId", null, parsed)) {
                exclusion.setArtifactId(this.getTrimmedValue(parser.nextText()));
            }
            else if (this.checkFieldWithDuplicate(parser, "groupId", null, parsed)) {
                exclusion.setGroupId(this.getTrimmedValue(parser.nextText()));
            }
            else {
                if (strict) {
                    throw new XmlPullParserException("Unrecognised tag: '" + parser.getName() + "'", parser, null);
                }
                while (parser.next() != 3) {}
            }
        }
        return exclusion;
    }
    
    private Extension parseExtension(final String tagName, final XmlPullParser parser, final boolean strict) throws IOException, XmlPullParserException {
        final Extension extension = new Extension();
        final Set parsed = new HashSet();
        while (parser.nextTag() == 2) {
            if (this.checkFieldWithDuplicate(parser, "groupId", null, parsed)) {
                extension.setGroupId(this.getTrimmedValue(parser.nextText()));
            }
            else if (this.checkFieldWithDuplicate(parser, "artifactId", null, parsed)) {
                extension.setArtifactId(this.getTrimmedValue(parser.nextText()));
            }
            else if (this.checkFieldWithDuplicate(parser, "version", null, parsed)) {
                extension.setVersion(this.getTrimmedValue(parser.nextText()));
            }
            else {
                if (strict) {
                    throw new XmlPullParserException("Unrecognised tag: '" + parser.getName() + "'", parser, null);
                }
                while (parser.next() != 3) {}
            }
        }
        return extension;
    }
    
    private FileSet parseFileSet(final String tagName, final XmlPullParser parser, final boolean strict) throws IOException, XmlPullParserException {
        final FileSet fileSet = new FileSet();
        final Set parsed = new HashSet();
        while (parser.nextTag() == 2) {
            if (this.checkFieldWithDuplicate(parser, "directory", null, parsed)) {
                fileSet.setDirectory(this.getTrimmedValue(parser.nextText()));
            }
            else if (this.checkFieldWithDuplicate(parser, "includes", null, parsed)) {
                final List includes = new ArrayList();
                fileSet.setIncludes(includes);
                while (parser.nextTag() == 2) {
                    if (parser.getName().equals("include")) {
                        includes.add(this.getTrimmedValue(parser.nextText()));
                    }
                    else {
                        if (strict) {
                            throw new XmlPullParserException("Unrecognised association: '" + parser.getName() + "'", parser, null);
                        }
                        while (parser.next() != 3) {}
                    }
                }
            }
            else if (this.checkFieldWithDuplicate(parser, "excludes", null, parsed)) {
                final List excludes = new ArrayList();
                fileSet.setExcludes(excludes);
                while (parser.nextTag() == 2) {
                    if (parser.getName().equals("exclude")) {
                        excludes.add(this.getTrimmedValue(parser.nextText()));
                    }
                    else {
                        if (strict) {
                            throw new XmlPullParserException("Unrecognised association: '" + parser.getName() + "'", parser, null);
                        }
                        while (parser.next() != 3) {}
                    }
                }
            }
            else {
                if (strict) {
                    throw new XmlPullParserException("Unrecognised tag: '" + parser.getName() + "'", parser, null);
                }
                while (parser.next() != 3) {}
            }
        }
        return fileSet;
    }
    
    private IssueManagement parseIssueManagement(final String tagName, final XmlPullParser parser, final boolean strict) throws IOException, XmlPullParserException {
        final IssueManagement issueManagement = new IssueManagement();
        final Set parsed = new HashSet();
        while (parser.nextTag() == 2) {
            if (this.checkFieldWithDuplicate(parser, "system", null, parsed)) {
                issueManagement.setSystem(this.getTrimmedValue(parser.nextText()));
            }
            else if (this.checkFieldWithDuplicate(parser, "url", null, parsed)) {
                issueManagement.setUrl(this.getTrimmedValue(parser.nextText()));
            }
            else {
                if (strict) {
                    throw new XmlPullParserException("Unrecognised tag: '" + parser.getName() + "'", parser, null);
                }
                while (parser.next() != 3) {}
            }
        }
        return issueManagement;
    }
    
    private License parseLicense(final String tagName, final XmlPullParser parser, final boolean strict) throws IOException, XmlPullParserException {
        final License license = new License();
        final Set parsed = new HashSet();
        while (parser.nextTag() == 2) {
            if (this.checkFieldWithDuplicate(parser, "name", null, parsed)) {
                license.setName(this.getTrimmedValue(parser.nextText()));
            }
            else if (this.checkFieldWithDuplicate(parser, "url", null, parsed)) {
                license.setUrl(this.getTrimmedValue(parser.nextText()));
            }
            else if (this.checkFieldWithDuplicate(parser, "distribution", null, parsed)) {
                license.setDistribution(this.getTrimmedValue(parser.nextText()));
            }
            else if (this.checkFieldWithDuplicate(parser, "comments", null, parsed)) {
                license.setComments(this.getTrimmedValue(parser.nextText()));
            }
            else {
                if (strict) {
                    throw new XmlPullParserException("Unrecognised tag: '" + parser.getName() + "'", parser, null);
                }
                while (parser.next() != 3) {}
            }
        }
        return license;
    }
    
    private MailingList parseMailingList(final String tagName, final XmlPullParser parser, final boolean strict) throws IOException, XmlPullParserException {
        final MailingList mailingList = new MailingList();
        final Set parsed = new HashSet();
        while (parser.nextTag() == 2) {
            if (this.checkFieldWithDuplicate(parser, "name", null, parsed)) {
                mailingList.setName(this.getTrimmedValue(parser.nextText()));
            }
            else if (this.checkFieldWithDuplicate(parser, "subscribe", null, parsed)) {
                mailingList.setSubscribe(this.getTrimmedValue(parser.nextText()));
            }
            else if (this.checkFieldWithDuplicate(parser, "unsubscribe", null, parsed)) {
                mailingList.setUnsubscribe(this.getTrimmedValue(parser.nextText()));
            }
            else if (this.checkFieldWithDuplicate(parser, "post", null, parsed)) {
                mailingList.setPost(this.getTrimmedValue(parser.nextText()));
            }
            else if (this.checkFieldWithDuplicate(parser, "archive", null, parsed)) {
                mailingList.setArchive(this.getTrimmedValue(parser.nextText()));
            }
            else if (this.checkFieldWithDuplicate(parser, "otherArchives", null, parsed)) {
                final List otherArchives = new ArrayList();
                mailingList.setOtherArchives(otherArchives);
                while (parser.nextTag() == 2) {
                    if (parser.getName().equals("otherArchive")) {
                        otherArchives.add(this.getTrimmedValue(parser.nextText()));
                    }
                    else {
                        if (strict) {
                            throw new XmlPullParserException("Unrecognised association: '" + parser.getName() + "'", parser, null);
                        }
                        while (parser.next() != 3) {}
                    }
                }
            }
            else {
                if (strict) {
                    throw new XmlPullParserException("Unrecognised tag: '" + parser.getName() + "'", parser, null);
                }
                while (parser.next() != 3) {}
            }
        }
        return mailingList;
    }
    
    private Model parseModel(final String tagName, final XmlPullParser parser, final boolean strict) throws IOException, XmlPullParserException {
        final Model model = new Model();
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
                    if (this.checkFieldWithDuplicate(parser, "modelVersion", null, parsed)) {
                        model.setModelVersion(this.getTrimmedValue(parser.nextText()));
                    }
                    else if (this.checkFieldWithDuplicate(parser, "parent", null, parsed)) {
                        model.setParent(this.parseParent("parent", parser, strict));
                    }
                    else if (this.checkFieldWithDuplicate(parser, "groupId", null, parsed)) {
                        model.setGroupId(this.getTrimmedValue(parser.nextText()));
                    }
                    else if (this.checkFieldWithDuplicate(parser, "artifactId", null, parsed)) {
                        model.setArtifactId(this.getTrimmedValue(parser.nextText()));
                    }
                    else if (this.checkFieldWithDuplicate(parser, "version", null, parsed)) {
                        model.setVersion(this.getTrimmedValue(parser.nextText()));
                    }
                    else if (this.checkFieldWithDuplicate(parser, "packaging", null, parsed)) {
                        model.setPackaging(this.getTrimmedValue(parser.nextText()));
                    }
                    else if (this.checkFieldWithDuplicate(parser, "name", null, parsed)) {
                        model.setName(this.getTrimmedValue(parser.nextText()));
                    }
                    else if (this.checkFieldWithDuplicate(parser, "description", null, parsed)) {
                        model.setDescription(this.getTrimmedValue(parser.nextText()));
                    }
                    else if (this.checkFieldWithDuplicate(parser, "url", null, parsed)) {
                        model.setUrl(this.getTrimmedValue(parser.nextText()));
                    }
                    else if (this.checkFieldWithDuplicate(parser, "inceptionYear", null, parsed)) {
                        model.setInceptionYear(this.getTrimmedValue(parser.nextText()));
                    }
                    else if (this.checkFieldWithDuplicate(parser, "organization", "organisation", parsed)) {
                        model.setOrganization(this.parseOrganization("organization", parser, strict));
                    }
                    else if (this.checkFieldWithDuplicate(parser, "licenses", null, parsed)) {
                        final List licenses = new ArrayList();
                        model.setLicenses(licenses);
                        while (parser.nextTag() == 2) {
                            if (parser.getName().equals("license")) {
                                licenses.add(this.parseLicense("license", parser, strict));
                            }
                            else {
                                if (strict) {
                                    throw new XmlPullParserException("Unrecognised association: '" + parser.getName() + "'", parser, null);
                                }
                                while (parser.next() != 3) {}
                            }
                        }
                    }
                    else if (this.checkFieldWithDuplicate(parser, "mailingLists", null, parsed)) {
                        final List mailingLists = new ArrayList();
                        model.setMailingLists(mailingLists);
                        while (parser.nextTag() == 2) {
                            if (parser.getName().equals("mailingList")) {
                                mailingLists.add(this.parseMailingList("mailingList", parser, strict));
                            }
                            else {
                                if (strict) {
                                    throw new XmlPullParserException("Unrecognised association: '" + parser.getName() + "'", parser, null);
                                }
                                while (parser.next() != 3) {}
                            }
                        }
                    }
                    else if (this.checkFieldWithDuplicate(parser, "developers", null, parsed)) {
                        final List developers = new ArrayList();
                        model.setDevelopers(developers);
                        while (parser.nextTag() == 2) {
                            if (parser.getName().equals("developer")) {
                                developers.add(this.parseDeveloper("developer", parser, strict));
                            }
                            else {
                                if (strict) {
                                    throw new XmlPullParserException("Unrecognised association: '" + parser.getName() + "'", parser, null);
                                }
                                while (parser.next() != 3) {}
                            }
                        }
                    }
                    else if (this.checkFieldWithDuplicate(parser, "contributors", null, parsed)) {
                        final List contributors = new ArrayList();
                        model.setContributors(contributors);
                        while (parser.nextTag() == 2) {
                            if (parser.getName().equals("contributor")) {
                                contributors.add(this.parseContributor("contributor", parser, strict));
                            }
                            else {
                                if (strict) {
                                    throw new XmlPullParserException("Unrecognised association: '" + parser.getName() + "'", parser, null);
                                }
                                while (parser.next() != 3) {}
                            }
                        }
                    }
                    else if (this.checkFieldWithDuplicate(parser, "issueManagement", null, parsed)) {
                        model.setIssueManagement(this.parseIssueManagement("issueManagement", parser, strict));
                    }
                    else if (this.checkFieldWithDuplicate(parser, "scm", null, parsed)) {
                        model.setScm(this.parseScm("scm", parser, strict));
                    }
                    else if (this.checkFieldWithDuplicate(parser, "ciManagement", null, parsed)) {
                        model.setCiManagement(this.parseCiManagement("ciManagement", parser, strict));
                    }
                    else if (this.checkFieldWithDuplicate(parser, "prerequisites", null, parsed)) {
                        model.setPrerequisites(this.parsePrerequisites("prerequisites", parser, strict));
                    }
                    else if (this.checkFieldWithDuplicate(parser, "build", null, parsed)) {
                        model.setBuild(this.parseBuild("build", parser, strict));
                    }
                    else if (this.checkFieldWithDuplicate(parser, "profiles", null, parsed)) {
                        final List profiles = new ArrayList();
                        model.setProfiles(profiles);
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
                    else if (this.checkFieldWithDuplicate(parser, "distributionManagement", null, parsed)) {
                        model.setDistributionManagement(this.parseDistributionManagement("distributionManagement", parser, strict));
                    }
                    else if (this.checkFieldWithDuplicate(parser, "modules", null, parsed)) {
                        final List modules = new ArrayList();
                        model.setModules(modules);
                        while (parser.nextTag() == 2) {
                            if (parser.getName().equals("module")) {
                                modules.add(this.getTrimmedValue(parser.nextText()));
                            }
                            else {
                                if (strict) {
                                    throw new XmlPullParserException("Unrecognised association: '" + parser.getName() + "'", parser, null);
                                }
                                while (parser.next() != 3) {}
                            }
                        }
                    }
                    else if (this.checkFieldWithDuplicate(parser, "repositories", null, parsed)) {
                        final List repositories = new ArrayList();
                        model.setRepositories(repositories);
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
                        model.setPluginRepositories(pluginRepositories);
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
                    else if (this.checkFieldWithDuplicate(parser, "dependencies", null, parsed)) {
                        final List dependencies = new ArrayList();
                        model.setDependencies(dependencies);
                        while (parser.nextTag() == 2) {
                            if (parser.getName().equals("dependency")) {
                                dependencies.add(this.parseDependency("dependency", parser, strict));
                            }
                            else {
                                if (strict) {
                                    throw new XmlPullParserException("Unrecognised association: '" + parser.getName() + "'", parser, null);
                                }
                                while (parser.next() != 3) {}
                            }
                        }
                    }
                    else if (this.checkFieldWithDuplicate(parser, "reports", null, parsed)) {
                        model.setReports(Xpp3DomBuilder.build(parser));
                    }
                    else if (this.checkFieldWithDuplicate(parser, "reporting", null, parsed)) {
                        model.setReporting(this.parseReporting("reporting", parser, strict));
                    }
                    else if (this.checkFieldWithDuplicate(parser, "dependencyManagement", null, parsed)) {
                        model.setDependencyManagement(this.parseDependencyManagement("dependencyManagement", parser, strict));
                    }
                    else if (this.checkFieldWithDuplicate(parser, "properties", null, parsed)) {
                        while (parser.nextTag() == 2) {
                            final String key = parser.getName();
                            final String value = parser.nextText().trim();
                            model.addProperty(key, value);
                        }
                    }
                    else if (strict) {
                        throw new XmlPullParserException("Unrecognised tag: '" + parser.getName() + "'", parser, null);
                    }
                }
            }
            eventType = parser.next();
        }
        return model;
    }
    
    private ModelBase parseModelBase(final String tagName, final XmlPullParser parser, final boolean strict) throws IOException, XmlPullParserException {
        final ModelBase modelBase = new ModelBase();
        final Set parsed = new HashSet();
        while (parser.nextTag() == 2) {
            if (this.checkFieldWithDuplicate(parser, "distributionManagement", null, parsed)) {
                modelBase.setDistributionManagement(this.parseDistributionManagement("distributionManagement", parser, strict));
            }
            else if (this.checkFieldWithDuplicate(parser, "modules", null, parsed)) {
                final List modules = new ArrayList();
                modelBase.setModules(modules);
                while (parser.nextTag() == 2) {
                    if (parser.getName().equals("module")) {
                        modules.add(this.getTrimmedValue(parser.nextText()));
                    }
                    else {
                        if (strict) {
                            throw new XmlPullParserException("Unrecognised association: '" + parser.getName() + "'", parser, null);
                        }
                        while (parser.next() != 3) {}
                    }
                }
            }
            else if (this.checkFieldWithDuplicate(parser, "repositories", null, parsed)) {
                final List repositories = new ArrayList();
                modelBase.setRepositories(repositories);
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
                modelBase.setPluginRepositories(pluginRepositories);
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
            else if (this.checkFieldWithDuplicate(parser, "dependencies", null, parsed)) {
                final List dependencies = new ArrayList();
                modelBase.setDependencies(dependencies);
                while (parser.nextTag() == 2) {
                    if (parser.getName().equals("dependency")) {
                        dependencies.add(this.parseDependency("dependency", parser, strict));
                    }
                    else {
                        if (strict) {
                            throw new XmlPullParserException("Unrecognised association: '" + parser.getName() + "'", parser, null);
                        }
                        while (parser.next() != 3) {}
                    }
                }
            }
            else if (this.checkFieldWithDuplicate(parser, "reports", null, parsed)) {
                modelBase.setReports(Xpp3DomBuilder.build(parser));
            }
            else if (this.checkFieldWithDuplicate(parser, "reporting", null, parsed)) {
                modelBase.setReporting(this.parseReporting("reporting", parser, strict));
            }
            else if (this.checkFieldWithDuplicate(parser, "dependencyManagement", null, parsed)) {
                modelBase.setDependencyManagement(this.parseDependencyManagement("dependencyManagement", parser, strict));
            }
            else if (this.checkFieldWithDuplicate(parser, "properties", null, parsed)) {
                while (parser.nextTag() == 2) {
                    final String key = parser.getName();
                    final String value = parser.nextText().trim();
                    modelBase.addProperty(key, value);
                }
            }
            else {
                if (strict) {
                    throw new XmlPullParserException("Unrecognised tag: '" + parser.getName() + "'", parser, null);
                }
                while (parser.next() != 3) {}
            }
        }
        return modelBase;
    }
    
    private Notifier parseNotifier(final String tagName, final XmlPullParser parser, final boolean strict) throws IOException, XmlPullParserException {
        final Notifier notifier = new Notifier();
        final Set parsed = new HashSet();
        while (parser.nextTag() == 2) {
            if (this.checkFieldWithDuplicate(parser, "type", null, parsed)) {
                notifier.setType(this.getTrimmedValue(parser.nextText()));
            }
            else if (this.checkFieldWithDuplicate(parser, "sendOnError", null, parsed)) {
                notifier.setSendOnError(this.getBooleanValue(this.getTrimmedValue(parser.nextText()), "sendOnError", parser, "true"));
            }
            else if (this.checkFieldWithDuplicate(parser, "sendOnFailure", null, parsed)) {
                notifier.setSendOnFailure(this.getBooleanValue(this.getTrimmedValue(parser.nextText()), "sendOnFailure", parser, "true"));
            }
            else if (this.checkFieldWithDuplicate(parser, "sendOnSuccess", null, parsed)) {
                notifier.setSendOnSuccess(this.getBooleanValue(this.getTrimmedValue(parser.nextText()), "sendOnSuccess", parser, "true"));
            }
            else if (this.checkFieldWithDuplicate(parser, "sendOnWarning", null, parsed)) {
                notifier.setSendOnWarning(this.getBooleanValue(this.getTrimmedValue(parser.nextText()), "sendOnWarning", parser, "true"));
            }
            else if (this.checkFieldWithDuplicate(parser, "address", null, parsed)) {
                notifier.setAddress(this.getTrimmedValue(parser.nextText()));
            }
            else if (this.checkFieldWithDuplicate(parser, "configuration", null, parsed)) {
                while (parser.nextTag() == 2) {
                    final String key = parser.getName();
                    final String value = parser.nextText().trim();
                    notifier.addConfiguration(key, value);
                }
            }
            else {
                if (strict) {
                    throw new XmlPullParserException("Unrecognised tag: '" + parser.getName() + "'", parser, null);
                }
                while (parser.next() != 3) {}
            }
        }
        return notifier;
    }
    
    private Organization parseOrganization(final String tagName, final XmlPullParser parser, final boolean strict) throws IOException, XmlPullParserException {
        final Organization organization = new Organization();
        final Set parsed = new HashSet();
        while (parser.nextTag() == 2) {
            if (this.checkFieldWithDuplicate(parser, "name", null, parsed)) {
                organization.setName(this.getTrimmedValue(parser.nextText()));
            }
            else if (this.checkFieldWithDuplicate(parser, "url", null, parsed)) {
                organization.setUrl(this.getTrimmedValue(parser.nextText()));
            }
            else {
                if (strict) {
                    throw new XmlPullParserException("Unrecognised tag: '" + parser.getName() + "'", parser, null);
                }
                while (parser.next() != 3) {}
            }
        }
        return organization;
    }
    
    private Parent parseParent(final String tagName, final XmlPullParser parser, final boolean strict) throws IOException, XmlPullParserException {
        final Parent parent = new Parent();
        final Set parsed = new HashSet();
        while (parser.nextTag() == 2) {
            if (this.checkFieldWithDuplicate(parser, "artifactId", null, parsed)) {
                parent.setArtifactId(this.getTrimmedValue(parser.nextText()));
            }
            else if (this.checkFieldWithDuplicate(parser, "groupId", null, parsed)) {
                parent.setGroupId(this.getTrimmedValue(parser.nextText()));
            }
            else if (this.checkFieldWithDuplicate(parser, "version", null, parsed)) {
                parent.setVersion(this.getTrimmedValue(parser.nextText()));
            }
            else if (this.checkFieldWithDuplicate(parser, "relativePath", null, parsed)) {
                parent.setRelativePath(this.getTrimmedValue(parser.nextText()));
            }
            else {
                if (strict) {
                    throw new XmlPullParserException("Unrecognised tag: '" + parser.getName() + "'", parser, null);
                }
                while (parser.next() != 3) {}
            }
        }
        return parent;
    }
    
    private PatternSet parsePatternSet(final String tagName, final XmlPullParser parser, final boolean strict) throws IOException, XmlPullParserException {
        final PatternSet patternSet = new PatternSet();
        final Set parsed = new HashSet();
        while (parser.nextTag() == 2) {
            if (this.checkFieldWithDuplicate(parser, "includes", null, parsed)) {
                final List includes = new ArrayList();
                patternSet.setIncludes(includes);
                while (parser.nextTag() == 2) {
                    if (parser.getName().equals("include")) {
                        includes.add(this.getTrimmedValue(parser.nextText()));
                    }
                    else {
                        if (strict) {
                            throw new XmlPullParserException("Unrecognised association: '" + parser.getName() + "'", parser, null);
                        }
                        while (parser.next() != 3) {}
                    }
                }
            }
            else if (this.checkFieldWithDuplicate(parser, "excludes", null, parsed)) {
                final List excludes = new ArrayList();
                patternSet.setExcludes(excludes);
                while (parser.nextTag() == 2) {
                    if (parser.getName().equals("exclude")) {
                        excludes.add(this.getTrimmedValue(parser.nextText()));
                    }
                    else {
                        if (strict) {
                            throw new XmlPullParserException("Unrecognised association: '" + parser.getName() + "'", parser, null);
                        }
                        while (parser.next() != 3) {}
                    }
                }
            }
            else {
                if (strict) {
                    throw new XmlPullParserException("Unrecognised tag: '" + parser.getName() + "'", parser, null);
                }
                while (parser.next() != 3) {}
            }
        }
        return patternSet;
    }
    
    private Plugin parsePlugin(final String tagName, final XmlPullParser parser, final boolean strict) throws IOException, XmlPullParserException {
        final Plugin plugin = new Plugin();
        final Set parsed = new HashSet();
        while (parser.nextTag() == 2) {
            if (this.checkFieldWithDuplicate(parser, "groupId", null, parsed)) {
                plugin.setGroupId(this.getTrimmedValue(parser.nextText()));
            }
            else if (this.checkFieldWithDuplicate(parser, "artifactId", null, parsed)) {
                plugin.setArtifactId(this.getTrimmedValue(parser.nextText()));
            }
            else if (this.checkFieldWithDuplicate(parser, "version", null, parsed)) {
                plugin.setVersion(this.getTrimmedValue(parser.nextText()));
            }
            else if (this.checkFieldWithDuplicate(parser, "extensions", null, parsed)) {
                plugin.setExtensions(this.getBooleanValue(this.getTrimmedValue(parser.nextText()), "extensions", parser, "false"));
            }
            else if (this.checkFieldWithDuplicate(parser, "executions", null, parsed)) {
                final List executions = new ArrayList();
                plugin.setExecutions(executions);
                while (parser.nextTag() == 2) {
                    if (parser.getName().equals("execution")) {
                        executions.add(this.parsePluginExecution("execution", parser, strict));
                    }
                    else {
                        if (strict) {
                            throw new XmlPullParserException("Unrecognised association: '" + parser.getName() + "'", parser, null);
                        }
                        while (parser.next() != 3) {}
                    }
                }
            }
            else if (this.checkFieldWithDuplicate(parser, "dependencies", null, parsed)) {
                final List dependencies = new ArrayList();
                plugin.setDependencies(dependencies);
                while (parser.nextTag() == 2) {
                    if (parser.getName().equals("dependency")) {
                        dependencies.add(this.parseDependency("dependency", parser, strict));
                    }
                    else {
                        if (strict) {
                            throw new XmlPullParserException("Unrecognised association: '" + parser.getName() + "'", parser, null);
                        }
                        while (parser.next() != 3) {}
                    }
                }
            }
            else if (this.checkFieldWithDuplicate(parser, "goals", null, parsed)) {
                plugin.setGoals(Xpp3DomBuilder.build(parser));
            }
            else if (this.checkFieldWithDuplicate(parser, "inherited", null, parsed)) {
                plugin.setInherited(this.getTrimmedValue(parser.nextText()));
            }
            else if (this.checkFieldWithDuplicate(parser, "configuration", null, parsed)) {
                plugin.setConfiguration(Xpp3DomBuilder.build(parser));
            }
            else {
                if (strict) {
                    throw new XmlPullParserException("Unrecognised tag: '" + parser.getName() + "'", parser, null);
                }
                while (parser.next() != 3) {}
            }
        }
        return plugin;
    }
    
    private PluginConfiguration parsePluginConfiguration(final String tagName, final XmlPullParser parser, final boolean strict) throws IOException, XmlPullParserException {
        final PluginConfiguration pluginConfiguration = new PluginConfiguration();
        final Set parsed = new HashSet();
        while (parser.nextTag() == 2) {
            if (this.checkFieldWithDuplicate(parser, "pluginManagement", null, parsed)) {
                pluginConfiguration.setPluginManagement(this.parsePluginManagement("pluginManagement", parser, strict));
            }
            else if (this.checkFieldWithDuplicate(parser, "plugins", null, parsed)) {
                final List plugins = new ArrayList();
                pluginConfiguration.setPlugins(plugins);
                while (parser.nextTag() == 2) {
                    if (parser.getName().equals("plugin")) {
                        plugins.add(this.parsePlugin("plugin", parser, strict));
                    }
                    else {
                        if (strict) {
                            throw new XmlPullParserException("Unrecognised association: '" + parser.getName() + "'", parser, null);
                        }
                        while (parser.next() != 3) {}
                    }
                }
            }
            else {
                if (strict) {
                    throw new XmlPullParserException("Unrecognised tag: '" + parser.getName() + "'", parser, null);
                }
                while (parser.next() != 3) {}
            }
        }
        return pluginConfiguration;
    }
    
    private PluginContainer parsePluginContainer(final String tagName, final XmlPullParser parser, final boolean strict) throws IOException, XmlPullParserException {
        final PluginContainer pluginContainer = new PluginContainer();
        final Set parsed = new HashSet();
        while (parser.nextTag() == 2) {
            if (this.checkFieldWithDuplicate(parser, "plugins", null, parsed)) {
                final List plugins = new ArrayList();
                pluginContainer.setPlugins(plugins);
                while (parser.nextTag() == 2) {
                    if (parser.getName().equals("plugin")) {
                        plugins.add(this.parsePlugin("plugin", parser, strict));
                    }
                    else {
                        if (strict) {
                            throw new XmlPullParserException("Unrecognised association: '" + parser.getName() + "'", parser, null);
                        }
                        while (parser.next() != 3) {}
                    }
                }
            }
            else {
                if (strict) {
                    throw new XmlPullParserException("Unrecognised tag: '" + parser.getName() + "'", parser, null);
                }
                while (parser.next() != 3) {}
            }
        }
        return pluginContainer;
    }
    
    private PluginExecution parsePluginExecution(final String tagName, final XmlPullParser parser, final boolean strict) throws IOException, XmlPullParserException {
        final PluginExecution pluginExecution = new PluginExecution();
        final Set parsed = new HashSet();
        while (parser.nextTag() == 2) {
            if (this.checkFieldWithDuplicate(parser, "id", null, parsed)) {
                pluginExecution.setId(this.getTrimmedValue(parser.nextText()));
            }
            else if (this.checkFieldWithDuplicate(parser, "phase", null, parsed)) {
                pluginExecution.setPhase(this.getTrimmedValue(parser.nextText()));
            }
            else if (this.checkFieldWithDuplicate(parser, "goals", null, parsed)) {
                final List goals = new ArrayList();
                pluginExecution.setGoals(goals);
                while (parser.nextTag() == 2) {
                    if (parser.getName().equals("goal")) {
                        goals.add(this.getTrimmedValue(parser.nextText()));
                    }
                    else {
                        if (strict) {
                            throw new XmlPullParserException("Unrecognised association: '" + parser.getName() + "'", parser, null);
                        }
                        while (parser.next() != 3) {}
                    }
                }
            }
            else if (this.checkFieldWithDuplicate(parser, "inherited", null, parsed)) {
                pluginExecution.setInherited(this.getTrimmedValue(parser.nextText()));
            }
            else if (this.checkFieldWithDuplicate(parser, "configuration", null, parsed)) {
                pluginExecution.setConfiguration(Xpp3DomBuilder.build(parser));
            }
            else {
                if (strict) {
                    throw new XmlPullParserException("Unrecognised tag: '" + parser.getName() + "'", parser, null);
                }
                while (parser.next() != 3) {}
            }
        }
        return pluginExecution;
    }
    
    private PluginManagement parsePluginManagement(final String tagName, final XmlPullParser parser, final boolean strict) throws IOException, XmlPullParserException {
        final PluginManagement pluginManagement = new PluginManagement();
        final Set parsed = new HashSet();
        while (parser.nextTag() == 2) {
            if (this.checkFieldWithDuplicate(parser, "plugins", null, parsed)) {
                final List plugins = new ArrayList();
                pluginManagement.setPlugins(plugins);
                while (parser.nextTag() == 2) {
                    if (parser.getName().equals("plugin")) {
                        plugins.add(this.parsePlugin("plugin", parser, strict));
                    }
                    else {
                        if (strict) {
                            throw new XmlPullParserException("Unrecognised association: '" + parser.getName() + "'", parser, null);
                        }
                        while (parser.next() != 3) {}
                    }
                }
            }
            else {
                if (strict) {
                    throw new XmlPullParserException("Unrecognised tag: '" + parser.getName() + "'", parser, null);
                }
                while (parser.next() != 3) {}
            }
        }
        return pluginManagement;
    }
    
    private Prerequisites parsePrerequisites(final String tagName, final XmlPullParser parser, final boolean strict) throws IOException, XmlPullParserException {
        final Prerequisites prerequisites = new Prerequisites();
        final Set parsed = new HashSet();
        while (parser.nextTag() == 2) {
            if (this.checkFieldWithDuplicate(parser, "maven", null, parsed)) {
                prerequisites.setMaven(this.getTrimmedValue(parser.nextText()));
            }
            else {
                if (strict) {
                    throw new XmlPullParserException("Unrecognised tag: '" + parser.getName() + "'", parser, null);
                }
                while (parser.next() != 3) {}
            }
        }
        return prerequisites;
    }
    
    private Profile parseProfile(final String tagName, final XmlPullParser parser, final boolean strict) throws IOException, XmlPullParserException {
        final Profile profile = new Profile();
        final Set parsed = new HashSet();
        while (parser.nextTag() == 2) {
            if (this.checkFieldWithDuplicate(parser, "id", null, parsed)) {
                profile.setId(this.getTrimmedValue(parser.nextText()));
            }
            else if (this.checkFieldWithDuplicate(parser, "activation", null, parsed)) {
                profile.setActivation(this.parseActivation("activation", parser, strict));
            }
            else if (this.checkFieldWithDuplicate(parser, "build", null, parsed)) {
                profile.setBuild(this.parseBuildBase("build", parser, strict));
            }
            else if (this.checkFieldWithDuplicate(parser, "distributionManagement", null, parsed)) {
                profile.setDistributionManagement(this.parseDistributionManagement("distributionManagement", parser, strict));
            }
            else if (this.checkFieldWithDuplicate(parser, "modules", null, parsed)) {
                final List modules = new ArrayList();
                profile.setModules(modules);
                while (parser.nextTag() == 2) {
                    if (parser.getName().equals("module")) {
                        modules.add(this.getTrimmedValue(parser.nextText()));
                    }
                    else {
                        if (strict) {
                            throw new XmlPullParserException("Unrecognised association: '" + parser.getName() + "'", parser, null);
                        }
                        while (parser.next() != 3) {}
                    }
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
            else if (this.checkFieldWithDuplicate(parser, "dependencies", null, parsed)) {
                final List dependencies = new ArrayList();
                profile.setDependencies(dependencies);
                while (parser.nextTag() == 2) {
                    if (parser.getName().equals("dependency")) {
                        dependencies.add(this.parseDependency("dependency", parser, strict));
                    }
                    else {
                        if (strict) {
                            throw new XmlPullParserException("Unrecognised association: '" + parser.getName() + "'", parser, null);
                        }
                        while (parser.next() != 3) {}
                    }
                }
            }
            else if (this.checkFieldWithDuplicate(parser, "reports", null, parsed)) {
                profile.setReports(Xpp3DomBuilder.build(parser));
            }
            else if (this.checkFieldWithDuplicate(parser, "reporting", null, parsed)) {
                profile.setReporting(this.parseReporting("reporting", parser, strict));
            }
            else if (this.checkFieldWithDuplicate(parser, "dependencyManagement", null, parsed)) {
                profile.setDependencyManagement(this.parseDependencyManagement("dependencyManagement", parser, strict));
            }
            else if (this.checkFieldWithDuplicate(parser, "properties", null, parsed)) {
                while (parser.nextTag() == 2) {
                    final String key = parser.getName();
                    final String value = parser.nextText().trim();
                    profile.addProperty(key, value);
                }
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
    
    private Relocation parseRelocation(final String tagName, final XmlPullParser parser, final boolean strict) throws IOException, XmlPullParserException {
        final Relocation relocation = new Relocation();
        final Set parsed = new HashSet();
        while (parser.nextTag() == 2) {
            if (this.checkFieldWithDuplicate(parser, "groupId", null, parsed)) {
                relocation.setGroupId(this.getTrimmedValue(parser.nextText()));
            }
            else if (this.checkFieldWithDuplicate(parser, "artifactId", null, parsed)) {
                relocation.setArtifactId(this.getTrimmedValue(parser.nextText()));
            }
            else if (this.checkFieldWithDuplicate(parser, "version", null, parsed)) {
                relocation.setVersion(this.getTrimmedValue(parser.nextText()));
            }
            else if (this.checkFieldWithDuplicate(parser, "message", null, parsed)) {
                relocation.setMessage(this.getTrimmedValue(parser.nextText()));
            }
            else {
                if (strict) {
                    throw new XmlPullParserException("Unrecognised tag: '" + parser.getName() + "'", parser, null);
                }
                while (parser.next() != 3) {}
            }
        }
        return relocation;
    }
    
    private ReportPlugin parseReportPlugin(final String tagName, final XmlPullParser parser, final boolean strict) throws IOException, XmlPullParserException {
        final ReportPlugin reportPlugin = new ReportPlugin();
        final Set parsed = new HashSet();
        while (parser.nextTag() == 2) {
            if (this.checkFieldWithDuplicate(parser, "groupId", null, parsed)) {
                reportPlugin.setGroupId(this.getTrimmedValue(parser.nextText()));
            }
            else if (this.checkFieldWithDuplicate(parser, "artifactId", null, parsed)) {
                reportPlugin.setArtifactId(this.getTrimmedValue(parser.nextText()));
            }
            else if (this.checkFieldWithDuplicate(parser, "version", null, parsed)) {
                reportPlugin.setVersion(this.getTrimmedValue(parser.nextText()));
            }
            else if (this.checkFieldWithDuplicate(parser, "inherited", null, parsed)) {
                reportPlugin.setInherited(this.getTrimmedValue(parser.nextText()));
            }
            else if (this.checkFieldWithDuplicate(parser, "configuration", null, parsed)) {
                reportPlugin.setConfiguration(Xpp3DomBuilder.build(parser));
            }
            else if (this.checkFieldWithDuplicate(parser, "reportSets", null, parsed)) {
                final List reportSets = new ArrayList();
                reportPlugin.setReportSets(reportSets);
                while (parser.nextTag() == 2) {
                    if (parser.getName().equals("reportSet")) {
                        reportSets.add(this.parseReportSet("reportSet", parser, strict));
                    }
                    else {
                        if (strict) {
                            throw new XmlPullParserException("Unrecognised association: '" + parser.getName() + "'", parser, null);
                        }
                        while (parser.next() != 3) {}
                    }
                }
            }
            else {
                if (strict) {
                    throw new XmlPullParserException("Unrecognised tag: '" + parser.getName() + "'", parser, null);
                }
                while (parser.next() != 3) {}
            }
        }
        return reportPlugin;
    }
    
    private ReportSet parseReportSet(final String tagName, final XmlPullParser parser, final boolean strict) throws IOException, XmlPullParserException {
        final ReportSet reportSet = new ReportSet();
        final Set parsed = new HashSet();
        while (parser.nextTag() == 2) {
            if (this.checkFieldWithDuplicate(parser, "id", null, parsed)) {
                reportSet.setId(this.getTrimmedValue(parser.nextText()));
            }
            else if (this.checkFieldWithDuplicate(parser, "configuration", null, parsed)) {
                reportSet.setConfiguration(Xpp3DomBuilder.build(parser));
            }
            else if (this.checkFieldWithDuplicate(parser, "inherited", null, parsed)) {
                reportSet.setInherited(this.getTrimmedValue(parser.nextText()));
            }
            else if (this.checkFieldWithDuplicate(parser, "reports", null, parsed)) {
                final List reports = new ArrayList();
                reportSet.setReports(reports);
                while (parser.nextTag() == 2) {
                    if (parser.getName().equals("report")) {
                        reports.add(this.getTrimmedValue(parser.nextText()));
                    }
                    else {
                        if (strict) {
                            throw new XmlPullParserException("Unrecognised association: '" + parser.getName() + "'", parser, null);
                        }
                        while (parser.next() != 3) {}
                    }
                }
            }
            else {
                if (strict) {
                    throw new XmlPullParserException("Unrecognised tag: '" + parser.getName() + "'", parser, null);
                }
                while (parser.next() != 3) {}
            }
        }
        return reportSet;
    }
    
    private Reporting parseReporting(final String tagName, final XmlPullParser parser, final boolean strict) throws IOException, XmlPullParserException {
        final Reporting reporting = new Reporting();
        final Set parsed = new HashSet();
        while (parser.nextTag() == 2) {
            if (this.checkFieldWithDuplicate(parser, "excludeDefaults", null, parsed)) {
                reporting.setExcludeDefaultsValue(this.getTrimmedValue(parser.nextText()));
            }
            else if (this.checkFieldWithDuplicate(parser, "outputDirectory", null, parsed)) {
                reporting.setOutputDirectory(this.getTrimmedValue(parser.nextText()));
            }
            else if (this.checkFieldWithDuplicate(parser, "plugins", null, parsed)) {
                final List plugins = new ArrayList();
                reporting.setPlugins(plugins);
                while (parser.nextTag() == 2) {
                    if (parser.getName().equals("plugin")) {
                        plugins.add(this.parseReportPlugin("plugin", parser, strict));
                    }
                    else {
                        if (strict) {
                            throw new XmlPullParserException("Unrecognised association: '" + parser.getName() + "'", parser, null);
                        }
                        while (parser.next() != 3) {}
                    }
                }
            }
            else {
                if (strict) {
                    throw new XmlPullParserException("Unrecognised tag: '" + parser.getName() + "'", parser, null);
                }
                while (parser.next() != 3) {}
            }
        }
        return reporting;
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
    
    private Resource parseResource(final String tagName, final XmlPullParser parser, final boolean strict) throws IOException, XmlPullParserException {
        final Resource resource = new Resource();
        final Set parsed = new HashSet();
        while (parser.nextTag() == 2) {
            if (this.checkFieldWithDuplicate(parser, "targetPath", null, parsed)) {
                resource.setTargetPath(this.getTrimmedValue(parser.nextText()));
            }
            else if (this.checkFieldWithDuplicate(parser, "filtering", null, parsed)) {
                resource.setFiltering(this.getBooleanValue(this.getTrimmedValue(parser.nextText()), "filtering", parser, "false"));
            }
            else if (this.checkFieldWithDuplicate(parser, "mergeId", null, parsed)) {
                resource.setMergeId(this.getTrimmedValue(parser.nextText()));
            }
            else if (this.checkFieldWithDuplicate(parser, "directory", null, parsed)) {
                resource.setDirectory(this.getTrimmedValue(parser.nextText()));
            }
            else if (this.checkFieldWithDuplicate(parser, "includes", null, parsed)) {
                final List includes = new ArrayList();
                resource.setIncludes(includes);
                while (parser.nextTag() == 2) {
                    if (parser.getName().equals("include")) {
                        includes.add(this.getTrimmedValue(parser.nextText()));
                    }
                    else {
                        if (strict) {
                            throw new XmlPullParserException("Unrecognised association: '" + parser.getName() + "'", parser, null);
                        }
                        while (parser.next() != 3) {}
                    }
                }
            }
            else if (this.checkFieldWithDuplicate(parser, "excludes", null, parsed)) {
                final List excludes = new ArrayList();
                resource.setExcludes(excludes);
                while (parser.nextTag() == 2) {
                    if (parser.getName().equals("exclude")) {
                        excludes.add(this.getTrimmedValue(parser.nextText()));
                    }
                    else {
                        if (strict) {
                            throw new XmlPullParserException("Unrecognised association: '" + parser.getName() + "'", parser, null);
                        }
                        while (parser.next() != 3) {}
                    }
                }
            }
            else {
                if (strict) {
                    throw new XmlPullParserException("Unrecognised tag: '" + parser.getName() + "'", parser, null);
                }
                while (parser.next() != 3) {}
            }
        }
        return resource;
    }
    
    private Scm parseScm(final String tagName, final XmlPullParser parser, final boolean strict) throws IOException, XmlPullParserException {
        final Scm scm = new Scm();
        final Set parsed = new HashSet();
        while (parser.nextTag() == 2) {
            if (this.checkFieldWithDuplicate(parser, "connection", null, parsed)) {
                scm.setConnection(this.getTrimmedValue(parser.nextText()));
            }
            else if (this.checkFieldWithDuplicate(parser, "developerConnection", null, parsed)) {
                scm.setDeveloperConnection(this.getTrimmedValue(parser.nextText()));
            }
            else if (this.checkFieldWithDuplicate(parser, "tag", null, parsed)) {
                scm.setTag(this.getTrimmedValue(parser.nextText()));
            }
            else if (this.checkFieldWithDuplicate(parser, "url", null, parsed)) {
                scm.setUrl(this.getTrimmedValue(parser.nextText()));
            }
            else {
                if (strict) {
                    throw new XmlPullParserException("Unrecognised tag: '" + parser.getName() + "'", parser, null);
                }
                while (parser.next() != 3) {}
            }
        }
        return scm;
    }
    
    private Site parseSite(final String tagName, final XmlPullParser parser, final boolean strict) throws IOException, XmlPullParserException {
        final Site site = new Site();
        final Set parsed = new HashSet();
        while (parser.nextTag() == 2) {
            if (this.checkFieldWithDuplicate(parser, "id", null, parsed)) {
                site.setId(this.getTrimmedValue(parser.nextText()));
            }
            else if (this.checkFieldWithDuplicate(parser, "name", null, parsed)) {
                site.setName(this.getTrimmedValue(parser.nextText()));
            }
            else if (this.checkFieldWithDuplicate(parser, "url", null, parsed)) {
                site.setUrl(this.getTrimmedValue(parser.nextText()));
            }
            else {
                if (strict) {
                    throw new XmlPullParserException("Unrecognised tag: '" + parser.getName() + "'", parser, null);
                }
                while (parser.next() != 3) {}
            }
        }
        return site;
    }
    
    public Model read(final Reader reader, final boolean strict) throws IOException, XmlPullParserException {
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
        return this.parseModel("project", parser, strict);
    }
    
    public Model read(final Reader reader) throws IOException, XmlPullParserException {
        return this.read(reader, true);
    }
    
    public Model read(final InputStream in, final boolean strict) throws IOException, XmlPullParserException {
        final Reader reader = ReaderFactory.newXmlReader(in);
        return this.read(reader, strict);
    }
    
    public Model read(final InputStream in) throws IOException, XmlPullParserException {
        final Reader reader = ReaderFactory.newXmlReader(in);
        return this.read(reader);
    }
    
    public void setAddDefaultEntities(final boolean addDefaultEntities) {
        this.addDefaultEntities = addDefaultEntities;
    }
}
