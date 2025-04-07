// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.maven.model.io.xpp3;

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
import org.codehaus.plexus.util.xml.Xpp3Dom;
import org.apache.maven.model.ConfigurationContainer;
import org.apache.maven.model.Notifier;
import org.apache.maven.model.CiManagement;
import org.apache.maven.model.BuildBase;
import java.util.Iterator;
import org.apache.maven.model.Plugin;
import org.apache.maven.model.Resource;
import org.apache.maven.model.Extension;
import org.apache.maven.model.Build;
import org.apache.maven.model.ActivationProperty;
import org.apache.maven.model.ActivationOS;
import org.apache.maven.model.ActivationFile;
import org.apache.maven.model.Activation;
import java.io.IOException;
import org.codehaus.plexus.util.xml.pull.XmlSerializer;
import org.codehaus.plexus.util.xml.pull.MXSerializer;
import org.apache.maven.model.Model;
import java.io.Writer;

public class MavenXpp3Writer
{
    private static final String NAMESPACE;
    
    public void write(final Writer writer, final Model model) throws IOException {
        final XmlSerializer serializer = new MXSerializer();
        serializer.setProperty("http://xmlpull.org/v1/doc/properties.html#serializer-indentation", "  ");
        serializer.setProperty("http://xmlpull.org/v1/doc/properties.html#serializer-line-separator", "\n");
        serializer.setOutput(writer);
        serializer.startDocument(model.getModelEncoding(), null);
        this.writeModel(model, "project", serializer);
        serializer.endDocument();
    }
    
    private void writeActivation(final Activation activation, final String tagName, final XmlSerializer serializer) throws IOException {
        if (activation != null) {
            serializer.startTag(MavenXpp3Writer.NAMESPACE, tagName);
            if (activation.isActiveByDefault()) {
                serializer.startTag(MavenXpp3Writer.NAMESPACE, "activeByDefault").text(String.valueOf(activation.isActiveByDefault())).endTag(MavenXpp3Writer.NAMESPACE, "activeByDefault");
            }
            if (activation.getJdk() != null) {
                serializer.startTag(MavenXpp3Writer.NAMESPACE, "jdk").text(activation.getJdk()).endTag(MavenXpp3Writer.NAMESPACE, "jdk");
            }
            if (activation.getOs() != null) {
                this.writeActivationOS(activation.getOs(), "os", serializer);
            }
            if (activation.getProperty() != null) {
                this.writeActivationProperty(activation.getProperty(), "property", serializer);
            }
            if (activation.getFile() != null) {
                this.writeActivationFile(activation.getFile(), "file", serializer);
            }
            serializer.endTag(MavenXpp3Writer.NAMESPACE, tagName);
        }
    }
    
    private void writeActivationFile(final ActivationFile activationFile, final String tagName, final XmlSerializer serializer) throws IOException {
        if (activationFile != null) {
            serializer.startTag(MavenXpp3Writer.NAMESPACE, tagName);
            if (activationFile.getMissing() != null) {
                serializer.startTag(MavenXpp3Writer.NAMESPACE, "missing").text(activationFile.getMissing()).endTag(MavenXpp3Writer.NAMESPACE, "missing");
            }
            if (activationFile.getExists() != null) {
                serializer.startTag(MavenXpp3Writer.NAMESPACE, "exists").text(activationFile.getExists()).endTag(MavenXpp3Writer.NAMESPACE, "exists");
            }
            serializer.endTag(MavenXpp3Writer.NAMESPACE, tagName);
        }
    }
    
    private void writeActivationOS(final ActivationOS activationOS, final String tagName, final XmlSerializer serializer) throws IOException {
        if (activationOS != null) {
            serializer.startTag(MavenXpp3Writer.NAMESPACE, tagName);
            if (activationOS.getName() != null) {
                serializer.startTag(MavenXpp3Writer.NAMESPACE, "name").text(activationOS.getName()).endTag(MavenXpp3Writer.NAMESPACE, "name");
            }
            if (activationOS.getFamily() != null) {
                serializer.startTag(MavenXpp3Writer.NAMESPACE, "family").text(activationOS.getFamily()).endTag(MavenXpp3Writer.NAMESPACE, "family");
            }
            if (activationOS.getArch() != null) {
                serializer.startTag(MavenXpp3Writer.NAMESPACE, "arch").text(activationOS.getArch()).endTag(MavenXpp3Writer.NAMESPACE, "arch");
            }
            if (activationOS.getVersion() != null) {
                serializer.startTag(MavenXpp3Writer.NAMESPACE, "version").text(activationOS.getVersion()).endTag(MavenXpp3Writer.NAMESPACE, "version");
            }
            serializer.endTag(MavenXpp3Writer.NAMESPACE, tagName);
        }
    }
    
    private void writeActivationProperty(final ActivationProperty activationProperty, final String tagName, final XmlSerializer serializer) throws IOException {
        if (activationProperty != null) {
            serializer.startTag(MavenXpp3Writer.NAMESPACE, tagName);
            if (activationProperty.getName() != null) {
                serializer.startTag(MavenXpp3Writer.NAMESPACE, "name").text(activationProperty.getName()).endTag(MavenXpp3Writer.NAMESPACE, "name");
            }
            if (activationProperty.getValue() != null) {
                serializer.startTag(MavenXpp3Writer.NAMESPACE, "value").text(activationProperty.getValue()).endTag(MavenXpp3Writer.NAMESPACE, "value");
            }
            serializer.endTag(MavenXpp3Writer.NAMESPACE, tagName);
        }
    }
    
    private void writeBuild(final Build build, final String tagName, final XmlSerializer serializer) throws IOException {
        if (build != null) {
            serializer.startTag(MavenXpp3Writer.NAMESPACE, tagName);
            if (build.getSourceDirectory() != null) {
                serializer.startTag(MavenXpp3Writer.NAMESPACE, "sourceDirectory").text(build.getSourceDirectory()).endTag(MavenXpp3Writer.NAMESPACE, "sourceDirectory");
            }
            if (build.getScriptSourceDirectory() != null) {
                serializer.startTag(MavenXpp3Writer.NAMESPACE, "scriptSourceDirectory").text(build.getScriptSourceDirectory()).endTag(MavenXpp3Writer.NAMESPACE, "scriptSourceDirectory");
            }
            if (build.getTestSourceDirectory() != null) {
                serializer.startTag(MavenXpp3Writer.NAMESPACE, "testSourceDirectory").text(build.getTestSourceDirectory()).endTag(MavenXpp3Writer.NAMESPACE, "testSourceDirectory");
            }
            if (build.getOutputDirectory() != null) {
                serializer.startTag(MavenXpp3Writer.NAMESPACE, "outputDirectory").text(build.getOutputDirectory()).endTag(MavenXpp3Writer.NAMESPACE, "outputDirectory");
            }
            if (build.getTestOutputDirectory() != null) {
                serializer.startTag(MavenXpp3Writer.NAMESPACE, "testOutputDirectory").text(build.getTestOutputDirectory()).endTag(MavenXpp3Writer.NAMESPACE, "testOutputDirectory");
            }
            if (build.getExtensions() != null && build.getExtensions().size() > 0) {
                serializer.startTag(MavenXpp3Writer.NAMESPACE, "extensions");
                for (final Extension o : build.getExtensions()) {
                    this.writeExtension(o, "extension", serializer);
                }
                serializer.endTag(MavenXpp3Writer.NAMESPACE, "extensions");
            }
            if (build.getDefaultGoal() != null) {
                serializer.startTag(MavenXpp3Writer.NAMESPACE, "defaultGoal").text(build.getDefaultGoal()).endTag(MavenXpp3Writer.NAMESPACE, "defaultGoal");
            }
            if (build.getResources() != null && build.getResources().size() > 0) {
                serializer.startTag(MavenXpp3Writer.NAMESPACE, "resources");
                for (final Resource o2 : build.getResources()) {
                    this.writeResource(o2, "resource", serializer);
                }
                serializer.endTag(MavenXpp3Writer.NAMESPACE, "resources");
            }
            if (build.getTestResources() != null && build.getTestResources().size() > 0) {
                serializer.startTag(MavenXpp3Writer.NAMESPACE, "testResources");
                for (final Resource o2 : build.getTestResources()) {
                    this.writeResource(o2, "testResource", serializer);
                }
                serializer.endTag(MavenXpp3Writer.NAMESPACE, "testResources");
            }
            if (build.getDirectory() != null) {
                serializer.startTag(MavenXpp3Writer.NAMESPACE, "directory").text(build.getDirectory()).endTag(MavenXpp3Writer.NAMESPACE, "directory");
            }
            if (build.getFinalName() != null) {
                serializer.startTag(MavenXpp3Writer.NAMESPACE, "finalName").text(build.getFinalName()).endTag(MavenXpp3Writer.NAMESPACE, "finalName");
            }
            if (build.getFilters() != null && build.getFilters().size() > 0) {
                serializer.startTag(MavenXpp3Writer.NAMESPACE, "filters");
                for (final String filter : build.getFilters()) {
                    serializer.startTag(MavenXpp3Writer.NAMESPACE, "filter").text(filter).endTag(MavenXpp3Writer.NAMESPACE, "filter");
                }
                serializer.endTag(MavenXpp3Writer.NAMESPACE, "filters");
            }
            if (build.getPluginManagement() != null) {
                this.writePluginManagement(build.getPluginManagement(), "pluginManagement", serializer);
            }
            if (build.getPlugins() != null && build.getPlugins().size() > 0) {
                serializer.startTag(MavenXpp3Writer.NAMESPACE, "plugins");
                for (final Plugin o3 : build.getPlugins()) {
                    this.writePlugin(o3, "plugin", serializer);
                }
                serializer.endTag(MavenXpp3Writer.NAMESPACE, "plugins");
            }
            serializer.endTag(MavenXpp3Writer.NAMESPACE, tagName);
        }
    }
    
    private void writeBuildBase(final BuildBase buildBase, final String tagName, final XmlSerializer serializer) throws IOException {
        if (buildBase != null) {
            serializer.startTag(MavenXpp3Writer.NAMESPACE, tagName);
            if (buildBase.getDefaultGoal() != null) {
                serializer.startTag(MavenXpp3Writer.NAMESPACE, "defaultGoal").text(buildBase.getDefaultGoal()).endTag(MavenXpp3Writer.NAMESPACE, "defaultGoal");
            }
            if (buildBase.getResources() != null && buildBase.getResources().size() > 0) {
                serializer.startTag(MavenXpp3Writer.NAMESPACE, "resources");
                for (final Resource o : buildBase.getResources()) {
                    this.writeResource(o, "resource", serializer);
                }
                serializer.endTag(MavenXpp3Writer.NAMESPACE, "resources");
            }
            if (buildBase.getTestResources() != null && buildBase.getTestResources().size() > 0) {
                serializer.startTag(MavenXpp3Writer.NAMESPACE, "testResources");
                for (final Resource o : buildBase.getTestResources()) {
                    this.writeResource(o, "testResource", serializer);
                }
                serializer.endTag(MavenXpp3Writer.NAMESPACE, "testResources");
            }
            if (buildBase.getDirectory() != null) {
                serializer.startTag(MavenXpp3Writer.NAMESPACE, "directory").text(buildBase.getDirectory()).endTag(MavenXpp3Writer.NAMESPACE, "directory");
            }
            if (buildBase.getFinalName() != null) {
                serializer.startTag(MavenXpp3Writer.NAMESPACE, "finalName").text(buildBase.getFinalName()).endTag(MavenXpp3Writer.NAMESPACE, "finalName");
            }
            if (buildBase.getFilters() != null && buildBase.getFilters().size() > 0) {
                serializer.startTag(MavenXpp3Writer.NAMESPACE, "filters");
                for (final String filter : buildBase.getFilters()) {
                    serializer.startTag(MavenXpp3Writer.NAMESPACE, "filter").text(filter).endTag(MavenXpp3Writer.NAMESPACE, "filter");
                }
                serializer.endTag(MavenXpp3Writer.NAMESPACE, "filters");
            }
            if (buildBase.getPluginManagement() != null) {
                this.writePluginManagement(buildBase.getPluginManagement(), "pluginManagement", serializer);
            }
            if (buildBase.getPlugins() != null && buildBase.getPlugins().size() > 0) {
                serializer.startTag(MavenXpp3Writer.NAMESPACE, "plugins");
                for (final Plugin o2 : buildBase.getPlugins()) {
                    this.writePlugin(o2, "plugin", serializer);
                }
                serializer.endTag(MavenXpp3Writer.NAMESPACE, "plugins");
            }
            serializer.endTag(MavenXpp3Writer.NAMESPACE, tagName);
        }
    }
    
    private void writeCiManagement(final CiManagement ciManagement, final String tagName, final XmlSerializer serializer) throws IOException {
        if (ciManagement != null) {
            serializer.startTag(MavenXpp3Writer.NAMESPACE, tagName);
            if (ciManagement.getSystem() != null) {
                serializer.startTag(MavenXpp3Writer.NAMESPACE, "system").text(ciManagement.getSystem()).endTag(MavenXpp3Writer.NAMESPACE, "system");
            }
            if (ciManagement.getUrl() != null) {
                serializer.startTag(MavenXpp3Writer.NAMESPACE, "url").text(ciManagement.getUrl()).endTag(MavenXpp3Writer.NAMESPACE, "url");
            }
            if (ciManagement.getNotifiers() != null && ciManagement.getNotifiers().size() > 0) {
                serializer.startTag(MavenXpp3Writer.NAMESPACE, "notifiers");
                for (final Notifier o : ciManagement.getNotifiers()) {
                    this.writeNotifier(o, "notifier", serializer);
                }
                serializer.endTag(MavenXpp3Writer.NAMESPACE, "notifiers");
            }
            serializer.endTag(MavenXpp3Writer.NAMESPACE, tagName);
        }
    }
    
    private void writeConfigurationContainer(final ConfigurationContainer configurationContainer, final String tagName, final XmlSerializer serializer) throws IOException {
        if (configurationContainer != null) {
            serializer.startTag(MavenXpp3Writer.NAMESPACE, tagName);
            if (configurationContainer.getInherited() != null) {
                serializer.startTag(MavenXpp3Writer.NAMESPACE, "inherited").text(configurationContainer.getInherited()).endTag(MavenXpp3Writer.NAMESPACE, "inherited");
            }
            if (configurationContainer.getConfiguration() != null) {
                ((Xpp3Dom)configurationContainer.getConfiguration()).writeToSerializer(MavenXpp3Writer.NAMESPACE, serializer);
            }
            serializer.endTag(MavenXpp3Writer.NAMESPACE, tagName);
        }
    }
    
    private void writeContributor(final Contributor contributor, final String tagName, final XmlSerializer serializer) throws IOException {
        if (contributor != null) {
            serializer.startTag(MavenXpp3Writer.NAMESPACE, tagName);
            if (contributor.getName() != null) {
                serializer.startTag(MavenXpp3Writer.NAMESPACE, "name").text(contributor.getName()).endTag(MavenXpp3Writer.NAMESPACE, "name");
            }
            if (contributor.getEmail() != null) {
                serializer.startTag(MavenXpp3Writer.NAMESPACE, "email").text(contributor.getEmail()).endTag(MavenXpp3Writer.NAMESPACE, "email");
            }
            if (contributor.getUrl() != null) {
                serializer.startTag(MavenXpp3Writer.NAMESPACE, "url").text(contributor.getUrl()).endTag(MavenXpp3Writer.NAMESPACE, "url");
            }
            if (contributor.getOrganization() != null) {
                serializer.startTag(MavenXpp3Writer.NAMESPACE, "organization").text(contributor.getOrganization()).endTag(MavenXpp3Writer.NAMESPACE, "organization");
            }
            if (contributor.getOrganizationUrl() != null) {
                serializer.startTag(MavenXpp3Writer.NAMESPACE, "organizationUrl").text(contributor.getOrganizationUrl()).endTag(MavenXpp3Writer.NAMESPACE, "organizationUrl");
            }
            if (contributor.getRoles() != null && contributor.getRoles().size() > 0) {
                serializer.startTag(MavenXpp3Writer.NAMESPACE, "roles");
                for (final String role : contributor.getRoles()) {
                    serializer.startTag(MavenXpp3Writer.NAMESPACE, "role").text(role).endTag(MavenXpp3Writer.NAMESPACE, "role");
                }
                serializer.endTag(MavenXpp3Writer.NAMESPACE, "roles");
            }
            if (contributor.getTimezone() != null) {
                serializer.startTag(MavenXpp3Writer.NAMESPACE, "timezone").text(contributor.getTimezone()).endTag(MavenXpp3Writer.NAMESPACE, "timezone");
            }
            if (contributor.getProperties() != null && contributor.getProperties().size() > 0) {
                serializer.startTag(MavenXpp3Writer.NAMESPACE, "properties");
                for (final String key : contributor.getProperties().keySet()) {
                    final String value = (String)contributor.getProperties().get(key);
                    serializer.startTag(MavenXpp3Writer.NAMESPACE, "" + key + "").text(value).endTag(MavenXpp3Writer.NAMESPACE, "" + key + "");
                }
                serializer.endTag(MavenXpp3Writer.NAMESPACE, "properties");
            }
            serializer.endTag(MavenXpp3Writer.NAMESPACE, tagName);
        }
    }
    
    private void writeDependency(final Dependency dependency, final String tagName, final XmlSerializer serializer) throws IOException {
        if (dependency != null) {
            serializer.startTag(MavenXpp3Writer.NAMESPACE, tagName);
            if (dependency.getGroupId() != null) {
                serializer.startTag(MavenXpp3Writer.NAMESPACE, "groupId").text(dependency.getGroupId()).endTag(MavenXpp3Writer.NAMESPACE, "groupId");
            }
            if (dependency.getArtifactId() != null) {
                serializer.startTag(MavenXpp3Writer.NAMESPACE, "artifactId").text(dependency.getArtifactId()).endTag(MavenXpp3Writer.NAMESPACE, "artifactId");
            }
            if (dependency.getVersion() != null) {
                serializer.startTag(MavenXpp3Writer.NAMESPACE, "version").text(dependency.getVersion()).endTag(MavenXpp3Writer.NAMESPACE, "version");
            }
            if (dependency.getType() != null && !dependency.getType().equals("jar")) {
                serializer.startTag(MavenXpp3Writer.NAMESPACE, "type").text(dependency.getType()).endTag(MavenXpp3Writer.NAMESPACE, "type");
            }
            if (dependency.getClassifier() != null) {
                serializer.startTag(MavenXpp3Writer.NAMESPACE, "classifier").text(dependency.getClassifier()).endTag(MavenXpp3Writer.NAMESPACE, "classifier");
            }
            if (dependency.getScope() != null) {
                serializer.startTag(MavenXpp3Writer.NAMESPACE, "scope").text(dependency.getScope()).endTag(MavenXpp3Writer.NAMESPACE, "scope");
            }
            if (dependency.getSystemPath() != null) {
                serializer.startTag(MavenXpp3Writer.NAMESPACE, "systemPath").text(dependency.getSystemPath()).endTag(MavenXpp3Writer.NAMESPACE, "systemPath");
            }
            if (dependency.getExclusions() != null && dependency.getExclusions().size() > 0) {
                serializer.startTag(MavenXpp3Writer.NAMESPACE, "exclusions");
                for (final Exclusion o : dependency.getExclusions()) {
                    this.writeExclusion(o, "exclusion", serializer);
                }
                serializer.endTag(MavenXpp3Writer.NAMESPACE, "exclusions");
            }
            if (dependency.isOptional()) {
                serializer.startTag(MavenXpp3Writer.NAMESPACE, "optional").text(String.valueOf(dependency.isOptional())).endTag(MavenXpp3Writer.NAMESPACE, "optional");
            }
            serializer.endTag(MavenXpp3Writer.NAMESPACE, tagName);
        }
    }
    
    private void writeDependencyManagement(final DependencyManagement dependencyManagement, final String tagName, final XmlSerializer serializer) throws IOException {
        if (dependencyManagement != null) {
            serializer.startTag(MavenXpp3Writer.NAMESPACE, tagName);
            if (dependencyManagement.getDependencies() != null && dependencyManagement.getDependencies().size() > 0) {
                serializer.startTag(MavenXpp3Writer.NAMESPACE, "dependencies");
                for (final Dependency o : dependencyManagement.getDependencies()) {
                    this.writeDependency(o, "dependency", serializer);
                }
                serializer.endTag(MavenXpp3Writer.NAMESPACE, "dependencies");
            }
            serializer.endTag(MavenXpp3Writer.NAMESPACE, tagName);
        }
    }
    
    private void writeDeploymentRepository(final DeploymentRepository deploymentRepository, final String tagName, final XmlSerializer serializer) throws IOException {
        if (deploymentRepository != null) {
            serializer.startTag(MavenXpp3Writer.NAMESPACE, tagName);
            if (!deploymentRepository.isUniqueVersion()) {
                serializer.startTag(MavenXpp3Writer.NAMESPACE, "uniqueVersion").text(String.valueOf(deploymentRepository.isUniqueVersion())).endTag(MavenXpp3Writer.NAMESPACE, "uniqueVersion");
            }
            if (deploymentRepository.getId() != null) {
                serializer.startTag(MavenXpp3Writer.NAMESPACE, "id").text(deploymentRepository.getId()).endTag(MavenXpp3Writer.NAMESPACE, "id");
            }
            if (deploymentRepository.getName() != null) {
                serializer.startTag(MavenXpp3Writer.NAMESPACE, "name").text(deploymentRepository.getName()).endTag(MavenXpp3Writer.NAMESPACE, "name");
            }
            if (deploymentRepository.getUrl() != null) {
                serializer.startTag(MavenXpp3Writer.NAMESPACE, "url").text(deploymentRepository.getUrl()).endTag(MavenXpp3Writer.NAMESPACE, "url");
            }
            if (deploymentRepository.getLayout() != null && !deploymentRepository.getLayout().equals("default")) {
                serializer.startTag(MavenXpp3Writer.NAMESPACE, "layout").text(deploymentRepository.getLayout()).endTag(MavenXpp3Writer.NAMESPACE, "layout");
            }
            serializer.endTag(MavenXpp3Writer.NAMESPACE, tagName);
        }
    }
    
    private void writeDeveloper(final Developer developer, final String tagName, final XmlSerializer serializer) throws IOException {
        if (developer != null) {
            serializer.startTag(MavenXpp3Writer.NAMESPACE, tagName);
            if (developer.getId() != null) {
                serializer.startTag(MavenXpp3Writer.NAMESPACE, "id").text(developer.getId()).endTag(MavenXpp3Writer.NAMESPACE, "id");
            }
            if (developer.getName() != null) {
                serializer.startTag(MavenXpp3Writer.NAMESPACE, "name").text(developer.getName()).endTag(MavenXpp3Writer.NAMESPACE, "name");
            }
            if (developer.getEmail() != null) {
                serializer.startTag(MavenXpp3Writer.NAMESPACE, "email").text(developer.getEmail()).endTag(MavenXpp3Writer.NAMESPACE, "email");
            }
            if (developer.getUrl() != null) {
                serializer.startTag(MavenXpp3Writer.NAMESPACE, "url").text(developer.getUrl()).endTag(MavenXpp3Writer.NAMESPACE, "url");
            }
            if (developer.getOrganization() != null) {
                serializer.startTag(MavenXpp3Writer.NAMESPACE, "organization").text(developer.getOrganization()).endTag(MavenXpp3Writer.NAMESPACE, "organization");
            }
            if (developer.getOrganizationUrl() != null) {
                serializer.startTag(MavenXpp3Writer.NAMESPACE, "organizationUrl").text(developer.getOrganizationUrl()).endTag(MavenXpp3Writer.NAMESPACE, "organizationUrl");
            }
            if (developer.getRoles() != null && developer.getRoles().size() > 0) {
                serializer.startTag(MavenXpp3Writer.NAMESPACE, "roles");
                for (final String role : developer.getRoles()) {
                    serializer.startTag(MavenXpp3Writer.NAMESPACE, "role").text(role).endTag(MavenXpp3Writer.NAMESPACE, "role");
                }
                serializer.endTag(MavenXpp3Writer.NAMESPACE, "roles");
            }
            if (developer.getTimezone() != null) {
                serializer.startTag(MavenXpp3Writer.NAMESPACE, "timezone").text(developer.getTimezone()).endTag(MavenXpp3Writer.NAMESPACE, "timezone");
            }
            if (developer.getProperties() != null && developer.getProperties().size() > 0) {
                serializer.startTag(MavenXpp3Writer.NAMESPACE, "properties");
                for (final String key : developer.getProperties().keySet()) {
                    final String value = (String)developer.getProperties().get(key);
                    serializer.startTag(MavenXpp3Writer.NAMESPACE, "" + key + "").text(value).endTag(MavenXpp3Writer.NAMESPACE, "" + key + "");
                }
                serializer.endTag(MavenXpp3Writer.NAMESPACE, "properties");
            }
            serializer.endTag(MavenXpp3Writer.NAMESPACE, tagName);
        }
    }
    
    private void writeDistributionManagement(final DistributionManagement distributionManagement, final String tagName, final XmlSerializer serializer) throws IOException {
        if (distributionManagement != null) {
            serializer.startTag(MavenXpp3Writer.NAMESPACE, tagName);
            if (distributionManagement.getRepository() != null) {
                this.writeDeploymentRepository(distributionManagement.getRepository(), "repository", serializer);
            }
            if (distributionManagement.getSnapshotRepository() != null) {
                this.writeDeploymentRepository(distributionManagement.getSnapshotRepository(), "snapshotRepository", serializer);
            }
            if (distributionManagement.getSite() != null) {
                this.writeSite(distributionManagement.getSite(), "site", serializer);
            }
            if (distributionManagement.getDownloadUrl() != null) {
                serializer.startTag(MavenXpp3Writer.NAMESPACE, "downloadUrl").text(distributionManagement.getDownloadUrl()).endTag(MavenXpp3Writer.NAMESPACE, "downloadUrl");
            }
            if (distributionManagement.getRelocation() != null) {
                this.writeRelocation(distributionManagement.getRelocation(), "relocation", serializer);
            }
            if (distributionManagement.getStatus() != null) {
                serializer.startTag(MavenXpp3Writer.NAMESPACE, "status").text(distributionManagement.getStatus()).endTag(MavenXpp3Writer.NAMESPACE, "status");
            }
            serializer.endTag(MavenXpp3Writer.NAMESPACE, tagName);
        }
    }
    
    private void writeExclusion(final Exclusion exclusion, final String tagName, final XmlSerializer serializer) throws IOException {
        if (exclusion != null) {
            serializer.startTag(MavenXpp3Writer.NAMESPACE, tagName);
            if (exclusion.getArtifactId() != null) {
                serializer.startTag(MavenXpp3Writer.NAMESPACE, "artifactId").text(exclusion.getArtifactId()).endTag(MavenXpp3Writer.NAMESPACE, "artifactId");
            }
            if (exclusion.getGroupId() != null) {
                serializer.startTag(MavenXpp3Writer.NAMESPACE, "groupId").text(exclusion.getGroupId()).endTag(MavenXpp3Writer.NAMESPACE, "groupId");
            }
            serializer.endTag(MavenXpp3Writer.NAMESPACE, tagName);
        }
    }
    
    private void writeExtension(final Extension extension, final String tagName, final XmlSerializer serializer) throws IOException {
        if (extension != null) {
            serializer.startTag(MavenXpp3Writer.NAMESPACE, tagName);
            if (extension.getGroupId() != null) {
                serializer.startTag(MavenXpp3Writer.NAMESPACE, "groupId").text(extension.getGroupId()).endTag(MavenXpp3Writer.NAMESPACE, "groupId");
            }
            if (extension.getArtifactId() != null) {
                serializer.startTag(MavenXpp3Writer.NAMESPACE, "artifactId").text(extension.getArtifactId()).endTag(MavenXpp3Writer.NAMESPACE, "artifactId");
            }
            if (extension.getVersion() != null) {
                serializer.startTag(MavenXpp3Writer.NAMESPACE, "version").text(extension.getVersion()).endTag(MavenXpp3Writer.NAMESPACE, "version");
            }
            serializer.endTag(MavenXpp3Writer.NAMESPACE, tagName);
        }
    }
    
    private void writeFileSet(final FileSet fileSet, final String tagName, final XmlSerializer serializer) throws IOException {
        if (fileSet != null) {
            serializer.startTag(MavenXpp3Writer.NAMESPACE, tagName);
            if (fileSet.getDirectory() != null) {
                serializer.startTag(MavenXpp3Writer.NAMESPACE, "directory").text(fileSet.getDirectory()).endTag(MavenXpp3Writer.NAMESPACE, "directory");
            }
            if (fileSet.getIncludes() != null && fileSet.getIncludes().size() > 0) {
                serializer.startTag(MavenXpp3Writer.NAMESPACE, "includes");
                for (final String include : fileSet.getIncludes()) {
                    serializer.startTag(MavenXpp3Writer.NAMESPACE, "include").text(include).endTag(MavenXpp3Writer.NAMESPACE, "include");
                }
                serializer.endTag(MavenXpp3Writer.NAMESPACE, "includes");
            }
            if (fileSet.getExcludes() != null && fileSet.getExcludes().size() > 0) {
                serializer.startTag(MavenXpp3Writer.NAMESPACE, "excludes");
                for (final String exclude : fileSet.getExcludes()) {
                    serializer.startTag(MavenXpp3Writer.NAMESPACE, "exclude").text(exclude).endTag(MavenXpp3Writer.NAMESPACE, "exclude");
                }
                serializer.endTag(MavenXpp3Writer.NAMESPACE, "excludes");
            }
            serializer.endTag(MavenXpp3Writer.NAMESPACE, tagName);
        }
    }
    
    private void writeIssueManagement(final IssueManagement issueManagement, final String tagName, final XmlSerializer serializer) throws IOException {
        if (issueManagement != null) {
            serializer.startTag(MavenXpp3Writer.NAMESPACE, tagName);
            if (issueManagement.getSystem() != null) {
                serializer.startTag(MavenXpp3Writer.NAMESPACE, "system").text(issueManagement.getSystem()).endTag(MavenXpp3Writer.NAMESPACE, "system");
            }
            if (issueManagement.getUrl() != null) {
                serializer.startTag(MavenXpp3Writer.NAMESPACE, "url").text(issueManagement.getUrl()).endTag(MavenXpp3Writer.NAMESPACE, "url");
            }
            serializer.endTag(MavenXpp3Writer.NAMESPACE, tagName);
        }
    }
    
    private void writeLicense(final License license, final String tagName, final XmlSerializer serializer) throws IOException {
        if (license != null) {
            serializer.startTag(MavenXpp3Writer.NAMESPACE, tagName);
            if (license.getName() != null) {
                serializer.startTag(MavenXpp3Writer.NAMESPACE, "name").text(license.getName()).endTag(MavenXpp3Writer.NAMESPACE, "name");
            }
            if (license.getUrl() != null) {
                serializer.startTag(MavenXpp3Writer.NAMESPACE, "url").text(license.getUrl()).endTag(MavenXpp3Writer.NAMESPACE, "url");
            }
            if (license.getDistribution() != null) {
                serializer.startTag(MavenXpp3Writer.NAMESPACE, "distribution").text(license.getDistribution()).endTag(MavenXpp3Writer.NAMESPACE, "distribution");
            }
            if (license.getComments() != null) {
                serializer.startTag(MavenXpp3Writer.NAMESPACE, "comments").text(license.getComments()).endTag(MavenXpp3Writer.NAMESPACE, "comments");
            }
            serializer.endTag(MavenXpp3Writer.NAMESPACE, tagName);
        }
    }
    
    private void writeMailingList(final MailingList mailingList, final String tagName, final XmlSerializer serializer) throws IOException {
        if (mailingList != null) {
            serializer.startTag(MavenXpp3Writer.NAMESPACE, tagName);
            if (mailingList.getName() != null) {
                serializer.startTag(MavenXpp3Writer.NAMESPACE, "name").text(mailingList.getName()).endTag(MavenXpp3Writer.NAMESPACE, "name");
            }
            if (mailingList.getSubscribe() != null) {
                serializer.startTag(MavenXpp3Writer.NAMESPACE, "subscribe").text(mailingList.getSubscribe()).endTag(MavenXpp3Writer.NAMESPACE, "subscribe");
            }
            if (mailingList.getUnsubscribe() != null) {
                serializer.startTag(MavenXpp3Writer.NAMESPACE, "unsubscribe").text(mailingList.getUnsubscribe()).endTag(MavenXpp3Writer.NAMESPACE, "unsubscribe");
            }
            if (mailingList.getPost() != null) {
                serializer.startTag(MavenXpp3Writer.NAMESPACE, "post").text(mailingList.getPost()).endTag(MavenXpp3Writer.NAMESPACE, "post");
            }
            if (mailingList.getArchive() != null) {
                serializer.startTag(MavenXpp3Writer.NAMESPACE, "archive").text(mailingList.getArchive()).endTag(MavenXpp3Writer.NAMESPACE, "archive");
            }
            if (mailingList.getOtherArchives() != null && mailingList.getOtherArchives().size() > 0) {
                serializer.startTag(MavenXpp3Writer.NAMESPACE, "otherArchives");
                for (final String otherArchive : mailingList.getOtherArchives()) {
                    serializer.startTag(MavenXpp3Writer.NAMESPACE, "otherArchive").text(otherArchive).endTag(MavenXpp3Writer.NAMESPACE, "otherArchive");
                }
                serializer.endTag(MavenXpp3Writer.NAMESPACE, "otherArchives");
            }
            serializer.endTag(MavenXpp3Writer.NAMESPACE, tagName);
        }
    }
    
    private void writeModel(final Model model, final String tagName, final XmlSerializer serializer) throws IOException {
        if (model != null) {
            serializer.setPrefix("", "http://maven.apache.org/POM/4.0.0");
            serializer.setPrefix("xsi", "http://www.w3.org/2001/XMLSchema-instance");
            serializer.startTag(MavenXpp3Writer.NAMESPACE, tagName);
            serializer.attribute("", "xsi:schemaLocation", "http://maven.apache.org/POM/4.0.0 http://maven.apache.org/xsd/maven-4.0.0.xsd");
            if (model.getModelVersion() != null) {
                serializer.startTag(MavenXpp3Writer.NAMESPACE, "modelVersion").text(model.getModelVersion()).endTag(MavenXpp3Writer.NAMESPACE, "modelVersion");
            }
            if (model.getParent() != null) {
                this.writeParent(model.getParent(), "parent", serializer);
            }
            if (model.getGroupId() != null) {
                serializer.startTag(MavenXpp3Writer.NAMESPACE, "groupId").text(model.getGroupId()).endTag(MavenXpp3Writer.NAMESPACE, "groupId");
            }
            if (model.getArtifactId() != null) {
                serializer.startTag(MavenXpp3Writer.NAMESPACE, "artifactId").text(model.getArtifactId()).endTag(MavenXpp3Writer.NAMESPACE, "artifactId");
            }
            if (model.getVersion() != null) {
                serializer.startTag(MavenXpp3Writer.NAMESPACE, "version").text(model.getVersion()).endTag(MavenXpp3Writer.NAMESPACE, "version");
            }
            if (model.getPackaging() != null && !model.getPackaging().equals("jar")) {
                serializer.startTag(MavenXpp3Writer.NAMESPACE, "packaging").text(model.getPackaging()).endTag(MavenXpp3Writer.NAMESPACE, "packaging");
            }
            if (model.getName() != null) {
                serializer.startTag(MavenXpp3Writer.NAMESPACE, "name").text(model.getName()).endTag(MavenXpp3Writer.NAMESPACE, "name");
            }
            if (model.getDescription() != null) {
                serializer.startTag(MavenXpp3Writer.NAMESPACE, "description").text(model.getDescription()).endTag(MavenXpp3Writer.NAMESPACE, "description");
            }
            if (model.getUrl() != null) {
                serializer.startTag(MavenXpp3Writer.NAMESPACE, "url").text(model.getUrl()).endTag(MavenXpp3Writer.NAMESPACE, "url");
            }
            if (model.getInceptionYear() != null) {
                serializer.startTag(MavenXpp3Writer.NAMESPACE, "inceptionYear").text(model.getInceptionYear()).endTag(MavenXpp3Writer.NAMESPACE, "inceptionYear");
            }
            if (model.getOrganization() != null) {
                this.writeOrganization(model.getOrganization(), "organization", serializer);
            }
            if (model.getLicenses() != null && model.getLicenses().size() > 0) {
                serializer.startTag(MavenXpp3Writer.NAMESPACE, "licenses");
                for (final License o : model.getLicenses()) {
                    this.writeLicense(o, "license", serializer);
                }
                serializer.endTag(MavenXpp3Writer.NAMESPACE, "licenses");
            }
            if (model.getMailingLists() != null && model.getMailingLists().size() > 0) {
                serializer.startTag(MavenXpp3Writer.NAMESPACE, "mailingLists");
                for (final MailingList o2 : model.getMailingLists()) {
                    this.writeMailingList(o2, "mailingList", serializer);
                }
                serializer.endTag(MavenXpp3Writer.NAMESPACE, "mailingLists");
            }
            if (model.getDevelopers() != null && model.getDevelopers().size() > 0) {
                serializer.startTag(MavenXpp3Writer.NAMESPACE, "developers");
                for (final Developer o3 : model.getDevelopers()) {
                    this.writeDeveloper(o3, "developer", serializer);
                }
                serializer.endTag(MavenXpp3Writer.NAMESPACE, "developers");
            }
            if (model.getContributors() != null && model.getContributors().size() > 0) {
                serializer.startTag(MavenXpp3Writer.NAMESPACE, "contributors");
                for (final Contributor o4 : model.getContributors()) {
                    this.writeContributor(o4, "contributor", serializer);
                }
                serializer.endTag(MavenXpp3Writer.NAMESPACE, "contributors");
            }
            if (model.getIssueManagement() != null) {
                this.writeIssueManagement(model.getIssueManagement(), "issueManagement", serializer);
            }
            if (model.getScm() != null) {
                this.writeScm(model.getScm(), "scm", serializer);
            }
            if (model.getCiManagement() != null) {
                this.writeCiManagement(model.getCiManagement(), "ciManagement", serializer);
            }
            if (model.getPrerequisites() != null) {
                this.writePrerequisites(model.getPrerequisites(), "prerequisites", serializer);
            }
            if (model.getBuild() != null) {
                this.writeBuild(model.getBuild(), "build", serializer);
            }
            if (model.getProfiles() != null && model.getProfiles().size() > 0) {
                serializer.startTag(MavenXpp3Writer.NAMESPACE, "profiles");
                for (final Profile o5 : model.getProfiles()) {
                    this.writeProfile(o5, "profile", serializer);
                }
                serializer.endTag(MavenXpp3Writer.NAMESPACE, "profiles");
            }
            if (model.getDistributionManagement() != null) {
                this.writeDistributionManagement(model.getDistributionManagement(), "distributionManagement", serializer);
            }
            if (model.getModules() != null && model.getModules().size() > 0) {
                serializer.startTag(MavenXpp3Writer.NAMESPACE, "modules");
                for (final String module : model.getModules()) {
                    serializer.startTag(MavenXpp3Writer.NAMESPACE, "module").text(module).endTag(MavenXpp3Writer.NAMESPACE, "module");
                }
                serializer.endTag(MavenXpp3Writer.NAMESPACE, "modules");
            }
            if (model.getRepositories() != null && model.getRepositories().size() > 0) {
                serializer.startTag(MavenXpp3Writer.NAMESPACE, "repositories");
                for (final Repository o6 : model.getRepositories()) {
                    this.writeRepository(o6, "repository", serializer);
                }
                serializer.endTag(MavenXpp3Writer.NAMESPACE, "repositories");
            }
            if (model.getPluginRepositories() != null && model.getPluginRepositories().size() > 0) {
                serializer.startTag(MavenXpp3Writer.NAMESPACE, "pluginRepositories");
                for (final Repository o6 : model.getPluginRepositories()) {
                    this.writeRepository(o6, "pluginRepository", serializer);
                }
                serializer.endTag(MavenXpp3Writer.NAMESPACE, "pluginRepositories");
            }
            if (model.getDependencies() != null && model.getDependencies().size() > 0) {
                serializer.startTag(MavenXpp3Writer.NAMESPACE, "dependencies");
                for (final Dependency o7 : model.getDependencies()) {
                    this.writeDependency(o7, "dependency", serializer);
                }
                serializer.endTag(MavenXpp3Writer.NAMESPACE, "dependencies");
            }
            if (model.getReports() != null) {
                ((Xpp3Dom)model.getReports()).writeToSerializer(MavenXpp3Writer.NAMESPACE, serializer);
            }
            if (model.getReporting() != null) {
                this.writeReporting(model.getReporting(), "reporting", serializer);
            }
            if (model.getDependencyManagement() != null) {
                this.writeDependencyManagement(model.getDependencyManagement(), "dependencyManagement", serializer);
            }
            if (model.getProperties() != null && model.getProperties().size() > 0) {
                serializer.startTag(MavenXpp3Writer.NAMESPACE, "properties");
                for (final String key : model.getProperties().keySet()) {
                    final String value = (String)model.getProperties().get(key);
                    serializer.startTag(MavenXpp3Writer.NAMESPACE, "" + key + "").text(value).endTag(MavenXpp3Writer.NAMESPACE, "" + key + "");
                }
                serializer.endTag(MavenXpp3Writer.NAMESPACE, "properties");
            }
            serializer.endTag(MavenXpp3Writer.NAMESPACE, tagName);
        }
    }
    
    private void writeModelBase(final ModelBase modelBase, final String tagName, final XmlSerializer serializer) throws IOException {
        if (modelBase != null) {
            serializer.startTag(MavenXpp3Writer.NAMESPACE, tagName);
            if (modelBase.getDistributionManagement() != null) {
                this.writeDistributionManagement(modelBase.getDistributionManagement(), "distributionManagement", serializer);
            }
            if (modelBase.getModules() != null && modelBase.getModules().size() > 0) {
                serializer.startTag(MavenXpp3Writer.NAMESPACE, "modules");
                for (final String module : modelBase.getModules()) {
                    serializer.startTag(MavenXpp3Writer.NAMESPACE, "module").text(module).endTag(MavenXpp3Writer.NAMESPACE, "module");
                }
                serializer.endTag(MavenXpp3Writer.NAMESPACE, "modules");
            }
            if (modelBase.getRepositories() != null && modelBase.getRepositories().size() > 0) {
                serializer.startTag(MavenXpp3Writer.NAMESPACE, "repositories");
                for (final Repository o : modelBase.getRepositories()) {
                    this.writeRepository(o, "repository", serializer);
                }
                serializer.endTag(MavenXpp3Writer.NAMESPACE, "repositories");
            }
            if (modelBase.getPluginRepositories() != null && modelBase.getPluginRepositories().size() > 0) {
                serializer.startTag(MavenXpp3Writer.NAMESPACE, "pluginRepositories");
                for (final Repository o : modelBase.getPluginRepositories()) {
                    this.writeRepository(o, "pluginRepository", serializer);
                }
                serializer.endTag(MavenXpp3Writer.NAMESPACE, "pluginRepositories");
            }
            if (modelBase.getDependencies() != null && modelBase.getDependencies().size() > 0) {
                serializer.startTag(MavenXpp3Writer.NAMESPACE, "dependencies");
                for (final Dependency o2 : modelBase.getDependencies()) {
                    this.writeDependency(o2, "dependency", serializer);
                }
                serializer.endTag(MavenXpp3Writer.NAMESPACE, "dependencies");
            }
            if (modelBase.getReports() != null) {
                ((Xpp3Dom)modelBase.getReports()).writeToSerializer(MavenXpp3Writer.NAMESPACE, serializer);
            }
            if (modelBase.getReporting() != null) {
                this.writeReporting(modelBase.getReporting(), "reporting", serializer);
            }
            if (modelBase.getDependencyManagement() != null) {
                this.writeDependencyManagement(modelBase.getDependencyManagement(), "dependencyManagement", serializer);
            }
            if (modelBase.getProperties() != null && modelBase.getProperties().size() > 0) {
                serializer.startTag(MavenXpp3Writer.NAMESPACE, "properties");
                for (final String key : modelBase.getProperties().keySet()) {
                    final String value = (String)modelBase.getProperties().get(key);
                    serializer.startTag(MavenXpp3Writer.NAMESPACE, "" + key + "").text(value).endTag(MavenXpp3Writer.NAMESPACE, "" + key + "");
                }
                serializer.endTag(MavenXpp3Writer.NAMESPACE, "properties");
            }
            serializer.endTag(MavenXpp3Writer.NAMESPACE, tagName);
        }
    }
    
    private void writeNotifier(final Notifier notifier, final String tagName, final XmlSerializer serializer) throws IOException {
        if (notifier != null) {
            serializer.startTag(MavenXpp3Writer.NAMESPACE, tagName);
            if (notifier.getType() != null && !notifier.getType().equals("mail")) {
                serializer.startTag(MavenXpp3Writer.NAMESPACE, "type").text(notifier.getType()).endTag(MavenXpp3Writer.NAMESPACE, "type");
            }
            if (!notifier.isSendOnError()) {
                serializer.startTag(MavenXpp3Writer.NAMESPACE, "sendOnError").text(String.valueOf(notifier.isSendOnError())).endTag(MavenXpp3Writer.NAMESPACE, "sendOnError");
            }
            if (!notifier.isSendOnFailure()) {
                serializer.startTag(MavenXpp3Writer.NAMESPACE, "sendOnFailure").text(String.valueOf(notifier.isSendOnFailure())).endTag(MavenXpp3Writer.NAMESPACE, "sendOnFailure");
            }
            if (!notifier.isSendOnSuccess()) {
                serializer.startTag(MavenXpp3Writer.NAMESPACE, "sendOnSuccess").text(String.valueOf(notifier.isSendOnSuccess())).endTag(MavenXpp3Writer.NAMESPACE, "sendOnSuccess");
            }
            if (!notifier.isSendOnWarning()) {
                serializer.startTag(MavenXpp3Writer.NAMESPACE, "sendOnWarning").text(String.valueOf(notifier.isSendOnWarning())).endTag(MavenXpp3Writer.NAMESPACE, "sendOnWarning");
            }
            if (notifier.getAddress() != null) {
                serializer.startTag(MavenXpp3Writer.NAMESPACE, "address").text(notifier.getAddress()).endTag(MavenXpp3Writer.NAMESPACE, "address");
            }
            if (notifier.getConfiguration() != null && notifier.getConfiguration().size() > 0) {
                serializer.startTag(MavenXpp3Writer.NAMESPACE, "configuration");
                for (final String key : notifier.getConfiguration().keySet()) {
                    final String value = (String)notifier.getConfiguration().get(key);
                    serializer.startTag(MavenXpp3Writer.NAMESPACE, "" + key + "").text(value).endTag(MavenXpp3Writer.NAMESPACE, "" + key + "");
                }
                serializer.endTag(MavenXpp3Writer.NAMESPACE, "configuration");
            }
            serializer.endTag(MavenXpp3Writer.NAMESPACE, tagName);
        }
    }
    
    private void writeOrganization(final Organization organization, final String tagName, final XmlSerializer serializer) throws IOException {
        if (organization != null) {
            serializer.startTag(MavenXpp3Writer.NAMESPACE, tagName);
            if (organization.getName() != null) {
                serializer.startTag(MavenXpp3Writer.NAMESPACE, "name").text(organization.getName()).endTag(MavenXpp3Writer.NAMESPACE, "name");
            }
            if (organization.getUrl() != null) {
                serializer.startTag(MavenXpp3Writer.NAMESPACE, "url").text(organization.getUrl()).endTag(MavenXpp3Writer.NAMESPACE, "url");
            }
            serializer.endTag(MavenXpp3Writer.NAMESPACE, tagName);
        }
    }
    
    private void writeParent(final Parent parent, final String tagName, final XmlSerializer serializer) throws IOException {
        if (parent != null) {
            serializer.startTag(MavenXpp3Writer.NAMESPACE, tagName);
            if (parent.getArtifactId() != null) {
                serializer.startTag(MavenXpp3Writer.NAMESPACE, "artifactId").text(parent.getArtifactId()).endTag(MavenXpp3Writer.NAMESPACE, "artifactId");
            }
            if (parent.getGroupId() != null) {
                serializer.startTag(MavenXpp3Writer.NAMESPACE, "groupId").text(parent.getGroupId()).endTag(MavenXpp3Writer.NAMESPACE, "groupId");
            }
            if (parent.getVersion() != null) {
                serializer.startTag(MavenXpp3Writer.NAMESPACE, "version").text(parent.getVersion()).endTag(MavenXpp3Writer.NAMESPACE, "version");
            }
            if (parent.getRelativePath() != null && !parent.getRelativePath().equals("../pom.xml")) {
                serializer.startTag(MavenXpp3Writer.NAMESPACE, "relativePath").text(parent.getRelativePath()).endTag(MavenXpp3Writer.NAMESPACE, "relativePath");
            }
            serializer.endTag(MavenXpp3Writer.NAMESPACE, tagName);
        }
    }
    
    private void writePatternSet(final PatternSet patternSet, final String tagName, final XmlSerializer serializer) throws IOException {
        if (patternSet != null) {
            serializer.startTag(MavenXpp3Writer.NAMESPACE, tagName);
            if (patternSet.getIncludes() != null && patternSet.getIncludes().size() > 0) {
                serializer.startTag(MavenXpp3Writer.NAMESPACE, "includes");
                for (final String include : patternSet.getIncludes()) {
                    serializer.startTag(MavenXpp3Writer.NAMESPACE, "include").text(include).endTag(MavenXpp3Writer.NAMESPACE, "include");
                }
                serializer.endTag(MavenXpp3Writer.NAMESPACE, "includes");
            }
            if (patternSet.getExcludes() != null && patternSet.getExcludes().size() > 0) {
                serializer.startTag(MavenXpp3Writer.NAMESPACE, "excludes");
                for (final String exclude : patternSet.getExcludes()) {
                    serializer.startTag(MavenXpp3Writer.NAMESPACE, "exclude").text(exclude).endTag(MavenXpp3Writer.NAMESPACE, "exclude");
                }
                serializer.endTag(MavenXpp3Writer.NAMESPACE, "excludes");
            }
            serializer.endTag(MavenXpp3Writer.NAMESPACE, tagName);
        }
    }
    
    private void writePlugin(final Plugin plugin, final String tagName, final XmlSerializer serializer) throws IOException {
        if (plugin != null) {
            serializer.startTag(MavenXpp3Writer.NAMESPACE, tagName);
            if (plugin.getGroupId() != null && !plugin.getGroupId().equals("org.apache.maven.plugins")) {
                serializer.startTag(MavenXpp3Writer.NAMESPACE, "groupId").text(plugin.getGroupId()).endTag(MavenXpp3Writer.NAMESPACE, "groupId");
            }
            if (plugin.getArtifactId() != null) {
                serializer.startTag(MavenXpp3Writer.NAMESPACE, "artifactId").text(plugin.getArtifactId()).endTag(MavenXpp3Writer.NAMESPACE, "artifactId");
            }
            if (plugin.getVersion() != null) {
                serializer.startTag(MavenXpp3Writer.NAMESPACE, "version").text(plugin.getVersion()).endTag(MavenXpp3Writer.NAMESPACE, "version");
            }
            if (plugin.isExtensions()) {
                serializer.startTag(MavenXpp3Writer.NAMESPACE, "extensions").text(String.valueOf(plugin.isExtensions())).endTag(MavenXpp3Writer.NAMESPACE, "extensions");
            }
            if (plugin.getExecutions() != null && plugin.getExecutions().size() > 0) {
                serializer.startTag(MavenXpp3Writer.NAMESPACE, "executions");
                for (final PluginExecution o : plugin.getExecutions()) {
                    this.writePluginExecution(o, "execution", serializer);
                }
                serializer.endTag(MavenXpp3Writer.NAMESPACE, "executions");
            }
            if (plugin.getDependencies() != null && plugin.getDependencies().size() > 0) {
                serializer.startTag(MavenXpp3Writer.NAMESPACE, "dependencies");
                for (final Dependency o2 : plugin.getDependencies()) {
                    this.writeDependency(o2, "dependency", serializer);
                }
                serializer.endTag(MavenXpp3Writer.NAMESPACE, "dependencies");
            }
            if (plugin.getGoals() != null) {
                ((Xpp3Dom)plugin.getGoals()).writeToSerializer(MavenXpp3Writer.NAMESPACE, serializer);
            }
            if (plugin.getInherited() != null) {
                serializer.startTag(MavenXpp3Writer.NAMESPACE, "inherited").text(plugin.getInherited()).endTag(MavenXpp3Writer.NAMESPACE, "inherited");
            }
            if (plugin.getConfiguration() != null) {
                ((Xpp3Dom)plugin.getConfiguration()).writeToSerializer(MavenXpp3Writer.NAMESPACE, serializer);
            }
            serializer.endTag(MavenXpp3Writer.NAMESPACE, tagName);
        }
    }
    
    private void writePluginConfiguration(final PluginConfiguration pluginConfiguration, final String tagName, final XmlSerializer serializer) throws IOException {
        if (pluginConfiguration != null) {
            serializer.startTag(MavenXpp3Writer.NAMESPACE, tagName);
            if (pluginConfiguration.getPluginManagement() != null) {
                this.writePluginManagement(pluginConfiguration.getPluginManagement(), "pluginManagement", serializer);
            }
            if (pluginConfiguration.getPlugins() != null && pluginConfiguration.getPlugins().size() > 0) {
                serializer.startTag(MavenXpp3Writer.NAMESPACE, "plugins");
                for (final Plugin o : pluginConfiguration.getPlugins()) {
                    this.writePlugin(o, "plugin", serializer);
                }
                serializer.endTag(MavenXpp3Writer.NAMESPACE, "plugins");
            }
            serializer.endTag(MavenXpp3Writer.NAMESPACE, tagName);
        }
    }
    
    private void writePluginContainer(final PluginContainer pluginContainer, final String tagName, final XmlSerializer serializer) throws IOException {
        if (pluginContainer != null) {
            serializer.startTag(MavenXpp3Writer.NAMESPACE, tagName);
            if (pluginContainer.getPlugins() != null && pluginContainer.getPlugins().size() > 0) {
                serializer.startTag(MavenXpp3Writer.NAMESPACE, "plugins");
                for (final Plugin o : pluginContainer.getPlugins()) {
                    this.writePlugin(o, "plugin", serializer);
                }
                serializer.endTag(MavenXpp3Writer.NAMESPACE, "plugins");
            }
            serializer.endTag(MavenXpp3Writer.NAMESPACE, tagName);
        }
    }
    
    private void writePluginExecution(final PluginExecution pluginExecution, final String tagName, final XmlSerializer serializer) throws IOException {
        if (pluginExecution != null) {
            serializer.startTag(MavenXpp3Writer.NAMESPACE, tagName);
            if (pluginExecution.getId() != null && !pluginExecution.getId().equals("default")) {
                serializer.startTag(MavenXpp3Writer.NAMESPACE, "id").text(pluginExecution.getId()).endTag(MavenXpp3Writer.NAMESPACE, "id");
            }
            if (pluginExecution.getPhase() != null) {
                serializer.startTag(MavenXpp3Writer.NAMESPACE, "phase").text(pluginExecution.getPhase()).endTag(MavenXpp3Writer.NAMESPACE, "phase");
            }
            if (pluginExecution.getGoals() != null && pluginExecution.getGoals().size() > 0) {
                serializer.startTag(MavenXpp3Writer.NAMESPACE, "goals");
                for (final String goal : pluginExecution.getGoals()) {
                    serializer.startTag(MavenXpp3Writer.NAMESPACE, "goal").text(goal).endTag(MavenXpp3Writer.NAMESPACE, "goal");
                }
                serializer.endTag(MavenXpp3Writer.NAMESPACE, "goals");
            }
            if (pluginExecution.getInherited() != null) {
                serializer.startTag(MavenXpp3Writer.NAMESPACE, "inherited").text(pluginExecution.getInherited()).endTag(MavenXpp3Writer.NAMESPACE, "inherited");
            }
            if (pluginExecution.getConfiguration() != null) {
                ((Xpp3Dom)pluginExecution.getConfiguration()).writeToSerializer(MavenXpp3Writer.NAMESPACE, serializer);
            }
            serializer.endTag(MavenXpp3Writer.NAMESPACE, tagName);
        }
    }
    
    private void writePluginManagement(final PluginManagement pluginManagement, final String tagName, final XmlSerializer serializer) throws IOException {
        if (pluginManagement != null) {
            serializer.startTag(MavenXpp3Writer.NAMESPACE, tagName);
            if (pluginManagement.getPlugins() != null && pluginManagement.getPlugins().size() > 0) {
                serializer.startTag(MavenXpp3Writer.NAMESPACE, "plugins");
                for (final Plugin o : pluginManagement.getPlugins()) {
                    this.writePlugin(o, "plugin", serializer);
                }
                serializer.endTag(MavenXpp3Writer.NAMESPACE, "plugins");
            }
            serializer.endTag(MavenXpp3Writer.NAMESPACE, tagName);
        }
    }
    
    private void writePrerequisites(final Prerequisites prerequisites, final String tagName, final XmlSerializer serializer) throws IOException {
        if (prerequisites != null) {
            serializer.startTag(MavenXpp3Writer.NAMESPACE, tagName);
            if (prerequisites.getMaven() != null && !prerequisites.getMaven().equals("2.0")) {
                serializer.startTag(MavenXpp3Writer.NAMESPACE, "maven").text(prerequisites.getMaven()).endTag(MavenXpp3Writer.NAMESPACE, "maven");
            }
            serializer.endTag(MavenXpp3Writer.NAMESPACE, tagName);
        }
    }
    
    private void writeProfile(final Profile profile, final String tagName, final XmlSerializer serializer) throws IOException {
        if (profile != null) {
            serializer.startTag(MavenXpp3Writer.NAMESPACE, tagName);
            if (profile.getId() != null) {
                serializer.startTag(MavenXpp3Writer.NAMESPACE, "id").text(profile.getId()).endTag(MavenXpp3Writer.NAMESPACE, "id");
            }
            if (profile.getActivation() != null) {
                this.writeActivation(profile.getActivation(), "activation", serializer);
            }
            if (profile.getBuild() != null) {
                this.writeBuildBase(profile.getBuild(), "build", serializer);
            }
            if (profile.getDistributionManagement() != null) {
                this.writeDistributionManagement(profile.getDistributionManagement(), "distributionManagement", serializer);
            }
            if (profile.getModules() != null && profile.getModules().size() > 0) {
                serializer.startTag(MavenXpp3Writer.NAMESPACE, "modules");
                for (final String module : profile.getModules()) {
                    serializer.startTag(MavenXpp3Writer.NAMESPACE, "module").text(module).endTag(MavenXpp3Writer.NAMESPACE, "module");
                }
                serializer.endTag(MavenXpp3Writer.NAMESPACE, "modules");
            }
            if (profile.getRepositories() != null && profile.getRepositories().size() > 0) {
                serializer.startTag(MavenXpp3Writer.NAMESPACE, "repositories");
                for (final Repository o : profile.getRepositories()) {
                    this.writeRepository(o, "repository", serializer);
                }
                serializer.endTag(MavenXpp3Writer.NAMESPACE, "repositories");
            }
            if (profile.getPluginRepositories() != null && profile.getPluginRepositories().size() > 0) {
                serializer.startTag(MavenXpp3Writer.NAMESPACE, "pluginRepositories");
                for (final Repository o : profile.getPluginRepositories()) {
                    this.writeRepository(o, "pluginRepository", serializer);
                }
                serializer.endTag(MavenXpp3Writer.NAMESPACE, "pluginRepositories");
            }
            if (profile.getDependencies() != null && profile.getDependencies().size() > 0) {
                serializer.startTag(MavenXpp3Writer.NAMESPACE, "dependencies");
                for (final Dependency o2 : profile.getDependencies()) {
                    this.writeDependency(o2, "dependency", serializer);
                }
                serializer.endTag(MavenXpp3Writer.NAMESPACE, "dependencies");
            }
            if (profile.getReports() != null) {
                ((Xpp3Dom)profile.getReports()).writeToSerializer(MavenXpp3Writer.NAMESPACE, serializer);
            }
            if (profile.getReporting() != null) {
                this.writeReporting(profile.getReporting(), "reporting", serializer);
            }
            if (profile.getDependencyManagement() != null) {
                this.writeDependencyManagement(profile.getDependencyManagement(), "dependencyManagement", serializer);
            }
            if (profile.getProperties() != null && profile.getProperties().size() > 0) {
                serializer.startTag(MavenXpp3Writer.NAMESPACE, "properties");
                for (final String key : profile.getProperties().keySet()) {
                    final String value = (String)profile.getProperties().get(key);
                    serializer.startTag(MavenXpp3Writer.NAMESPACE, "" + key + "").text(value).endTag(MavenXpp3Writer.NAMESPACE, "" + key + "");
                }
                serializer.endTag(MavenXpp3Writer.NAMESPACE, "properties");
            }
            serializer.endTag(MavenXpp3Writer.NAMESPACE, tagName);
        }
    }
    
    private void writeRelocation(final Relocation relocation, final String tagName, final XmlSerializer serializer) throws IOException {
        if (relocation != null) {
            serializer.startTag(MavenXpp3Writer.NAMESPACE, tagName);
            if (relocation.getGroupId() != null) {
                serializer.startTag(MavenXpp3Writer.NAMESPACE, "groupId").text(relocation.getGroupId()).endTag(MavenXpp3Writer.NAMESPACE, "groupId");
            }
            if (relocation.getArtifactId() != null) {
                serializer.startTag(MavenXpp3Writer.NAMESPACE, "artifactId").text(relocation.getArtifactId()).endTag(MavenXpp3Writer.NAMESPACE, "artifactId");
            }
            if (relocation.getVersion() != null) {
                serializer.startTag(MavenXpp3Writer.NAMESPACE, "version").text(relocation.getVersion()).endTag(MavenXpp3Writer.NAMESPACE, "version");
            }
            if (relocation.getMessage() != null) {
                serializer.startTag(MavenXpp3Writer.NAMESPACE, "message").text(relocation.getMessage()).endTag(MavenXpp3Writer.NAMESPACE, "message");
            }
            serializer.endTag(MavenXpp3Writer.NAMESPACE, tagName);
        }
    }
    
    private void writeReportPlugin(final ReportPlugin reportPlugin, final String tagName, final XmlSerializer serializer) throws IOException {
        if (reportPlugin != null) {
            serializer.startTag(MavenXpp3Writer.NAMESPACE, tagName);
            if (reportPlugin.getGroupId() != null && !reportPlugin.getGroupId().equals("org.apache.maven.plugins")) {
                serializer.startTag(MavenXpp3Writer.NAMESPACE, "groupId").text(reportPlugin.getGroupId()).endTag(MavenXpp3Writer.NAMESPACE, "groupId");
            }
            if (reportPlugin.getArtifactId() != null) {
                serializer.startTag(MavenXpp3Writer.NAMESPACE, "artifactId").text(reportPlugin.getArtifactId()).endTag(MavenXpp3Writer.NAMESPACE, "artifactId");
            }
            if (reportPlugin.getVersion() != null) {
                serializer.startTag(MavenXpp3Writer.NAMESPACE, "version").text(reportPlugin.getVersion()).endTag(MavenXpp3Writer.NAMESPACE, "version");
            }
            if (reportPlugin.getInherited() != null) {
                serializer.startTag(MavenXpp3Writer.NAMESPACE, "inherited").text(reportPlugin.getInherited()).endTag(MavenXpp3Writer.NAMESPACE, "inherited");
            }
            if (reportPlugin.getConfiguration() != null) {
                ((Xpp3Dom)reportPlugin.getConfiguration()).writeToSerializer(MavenXpp3Writer.NAMESPACE, serializer);
            }
            if (reportPlugin.getReportSets() != null && reportPlugin.getReportSets().size() > 0) {
                serializer.startTag(MavenXpp3Writer.NAMESPACE, "reportSets");
                for (final ReportSet o : reportPlugin.getReportSets()) {
                    this.writeReportSet(o, "reportSet", serializer);
                }
                serializer.endTag(MavenXpp3Writer.NAMESPACE, "reportSets");
            }
            serializer.endTag(MavenXpp3Writer.NAMESPACE, tagName);
        }
    }
    
    private void writeReportSet(final ReportSet reportSet, final String tagName, final XmlSerializer serializer) throws IOException {
        if (reportSet != null) {
            serializer.startTag(MavenXpp3Writer.NAMESPACE, tagName);
            if (reportSet.getId() != null && !reportSet.getId().equals("default")) {
                serializer.startTag(MavenXpp3Writer.NAMESPACE, "id").text(reportSet.getId()).endTag(MavenXpp3Writer.NAMESPACE, "id");
            }
            if (reportSet.getConfiguration() != null) {
                ((Xpp3Dom)reportSet.getConfiguration()).writeToSerializer(MavenXpp3Writer.NAMESPACE, serializer);
            }
            if (reportSet.getInherited() != null) {
                serializer.startTag(MavenXpp3Writer.NAMESPACE, "inherited").text(reportSet.getInherited()).endTag(MavenXpp3Writer.NAMESPACE, "inherited");
            }
            if (reportSet.getReports() != null && reportSet.getReports().size() > 0) {
                serializer.startTag(MavenXpp3Writer.NAMESPACE, "reports");
                for (final String report : reportSet.getReports()) {
                    serializer.startTag(MavenXpp3Writer.NAMESPACE, "report").text(report).endTag(MavenXpp3Writer.NAMESPACE, "report");
                }
                serializer.endTag(MavenXpp3Writer.NAMESPACE, "reports");
            }
            serializer.endTag(MavenXpp3Writer.NAMESPACE, tagName);
        }
    }
    
    private void writeReporting(final Reporting reporting, final String tagName, final XmlSerializer serializer) throws IOException {
        if (reporting != null) {
            serializer.startTag(MavenXpp3Writer.NAMESPACE, tagName);
            if (reporting.isExcludeDefaultsValue() != null) {
                serializer.startTag(MavenXpp3Writer.NAMESPACE, "excludeDefaults").text(String.valueOf(reporting.isExcludeDefaultsValue())).endTag(MavenXpp3Writer.NAMESPACE, "excludeDefaults");
            }
            if (reporting.getOutputDirectory() != null) {
                serializer.startTag(MavenXpp3Writer.NAMESPACE, "outputDirectory").text(reporting.getOutputDirectory()).endTag(MavenXpp3Writer.NAMESPACE, "outputDirectory");
            }
            if (reporting.getPlugins() != null && reporting.getPlugins().size() > 0) {
                serializer.startTag(MavenXpp3Writer.NAMESPACE, "plugins");
                for (final ReportPlugin o : reporting.getPlugins()) {
                    this.writeReportPlugin(o, "plugin", serializer);
                }
                serializer.endTag(MavenXpp3Writer.NAMESPACE, "plugins");
            }
            serializer.endTag(MavenXpp3Writer.NAMESPACE, tagName);
        }
    }
    
    private void writeRepository(final Repository repository, final String tagName, final XmlSerializer serializer) throws IOException {
        if (repository != null) {
            serializer.startTag(MavenXpp3Writer.NAMESPACE, tagName);
            if (repository.getReleases() != null) {
                this.writeRepositoryPolicy(repository.getReleases(), "releases", serializer);
            }
            if (repository.getSnapshots() != null) {
                this.writeRepositoryPolicy(repository.getSnapshots(), "snapshots", serializer);
            }
            if (repository.getId() != null) {
                serializer.startTag(MavenXpp3Writer.NAMESPACE, "id").text(repository.getId()).endTag(MavenXpp3Writer.NAMESPACE, "id");
            }
            if (repository.getName() != null) {
                serializer.startTag(MavenXpp3Writer.NAMESPACE, "name").text(repository.getName()).endTag(MavenXpp3Writer.NAMESPACE, "name");
            }
            if (repository.getUrl() != null) {
                serializer.startTag(MavenXpp3Writer.NAMESPACE, "url").text(repository.getUrl()).endTag(MavenXpp3Writer.NAMESPACE, "url");
            }
            if (repository.getLayout() != null && !repository.getLayout().equals("default")) {
                serializer.startTag(MavenXpp3Writer.NAMESPACE, "layout").text(repository.getLayout()).endTag(MavenXpp3Writer.NAMESPACE, "layout");
            }
            serializer.endTag(MavenXpp3Writer.NAMESPACE, tagName);
        }
    }
    
    private void writeRepositoryBase(final RepositoryBase repositoryBase, final String tagName, final XmlSerializer serializer) throws IOException {
        if (repositoryBase != null) {
            serializer.startTag(MavenXpp3Writer.NAMESPACE, tagName);
            if (repositoryBase.getId() != null) {
                serializer.startTag(MavenXpp3Writer.NAMESPACE, "id").text(repositoryBase.getId()).endTag(MavenXpp3Writer.NAMESPACE, "id");
            }
            if (repositoryBase.getName() != null) {
                serializer.startTag(MavenXpp3Writer.NAMESPACE, "name").text(repositoryBase.getName()).endTag(MavenXpp3Writer.NAMESPACE, "name");
            }
            if (repositoryBase.getUrl() != null) {
                serializer.startTag(MavenXpp3Writer.NAMESPACE, "url").text(repositoryBase.getUrl()).endTag(MavenXpp3Writer.NAMESPACE, "url");
            }
            if (repositoryBase.getLayout() != null && !repositoryBase.getLayout().equals("default")) {
                serializer.startTag(MavenXpp3Writer.NAMESPACE, "layout").text(repositoryBase.getLayout()).endTag(MavenXpp3Writer.NAMESPACE, "layout");
            }
            serializer.endTag(MavenXpp3Writer.NAMESPACE, tagName);
        }
    }
    
    private void writeRepositoryPolicy(final RepositoryPolicy repositoryPolicy, final String tagName, final XmlSerializer serializer) throws IOException {
        if (repositoryPolicy != null) {
            serializer.startTag(MavenXpp3Writer.NAMESPACE, tagName);
            if (!repositoryPolicy.isEnabled()) {
                serializer.startTag(MavenXpp3Writer.NAMESPACE, "enabled").text(String.valueOf(repositoryPolicy.isEnabled())).endTag(MavenXpp3Writer.NAMESPACE, "enabled");
            }
            if (repositoryPolicy.getUpdatePolicy() != null) {
                serializer.startTag(MavenXpp3Writer.NAMESPACE, "updatePolicy").text(repositoryPolicy.getUpdatePolicy()).endTag(MavenXpp3Writer.NAMESPACE, "updatePolicy");
            }
            if (repositoryPolicy.getChecksumPolicy() != null) {
                serializer.startTag(MavenXpp3Writer.NAMESPACE, "checksumPolicy").text(repositoryPolicy.getChecksumPolicy()).endTag(MavenXpp3Writer.NAMESPACE, "checksumPolicy");
            }
            serializer.endTag(MavenXpp3Writer.NAMESPACE, tagName);
        }
    }
    
    private void writeResource(final Resource resource, final String tagName, final XmlSerializer serializer) throws IOException {
        if (resource != null) {
            serializer.startTag(MavenXpp3Writer.NAMESPACE, tagName);
            if (resource.getTargetPath() != null) {
                serializer.startTag(MavenXpp3Writer.NAMESPACE, "targetPath").text(resource.getTargetPath()).endTag(MavenXpp3Writer.NAMESPACE, "targetPath");
            }
            if (resource.isFiltering()) {
                serializer.startTag(MavenXpp3Writer.NAMESPACE, "filtering").text(String.valueOf(resource.isFiltering())).endTag(MavenXpp3Writer.NAMESPACE, "filtering");
            }
            if (resource.getMergeId() != null) {
                serializer.startTag(MavenXpp3Writer.NAMESPACE, "mergeId").text(resource.getMergeId()).endTag(MavenXpp3Writer.NAMESPACE, "mergeId");
            }
            if (resource.getDirectory() != null) {
                serializer.startTag(MavenXpp3Writer.NAMESPACE, "directory").text(resource.getDirectory()).endTag(MavenXpp3Writer.NAMESPACE, "directory");
            }
            if (resource.getIncludes() != null && resource.getIncludes().size() > 0) {
                serializer.startTag(MavenXpp3Writer.NAMESPACE, "includes");
                for (final String include : resource.getIncludes()) {
                    serializer.startTag(MavenXpp3Writer.NAMESPACE, "include").text(include).endTag(MavenXpp3Writer.NAMESPACE, "include");
                }
                serializer.endTag(MavenXpp3Writer.NAMESPACE, "includes");
            }
            if (resource.getExcludes() != null && resource.getExcludes().size() > 0) {
                serializer.startTag(MavenXpp3Writer.NAMESPACE, "excludes");
                for (final String exclude : resource.getExcludes()) {
                    serializer.startTag(MavenXpp3Writer.NAMESPACE, "exclude").text(exclude).endTag(MavenXpp3Writer.NAMESPACE, "exclude");
                }
                serializer.endTag(MavenXpp3Writer.NAMESPACE, "excludes");
            }
            serializer.endTag(MavenXpp3Writer.NAMESPACE, tagName);
        }
    }
    
    private void writeScm(final Scm scm, final String tagName, final XmlSerializer serializer) throws IOException {
        if (scm != null) {
            serializer.startTag(MavenXpp3Writer.NAMESPACE, tagName);
            if (scm.getConnection() != null) {
                serializer.startTag(MavenXpp3Writer.NAMESPACE, "connection").text(scm.getConnection()).endTag(MavenXpp3Writer.NAMESPACE, "connection");
            }
            if (scm.getDeveloperConnection() != null) {
                serializer.startTag(MavenXpp3Writer.NAMESPACE, "developerConnection").text(scm.getDeveloperConnection()).endTag(MavenXpp3Writer.NAMESPACE, "developerConnection");
            }
            if (scm.getTag() != null && !scm.getTag().equals("HEAD")) {
                serializer.startTag(MavenXpp3Writer.NAMESPACE, "tag").text(scm.getTag()).endTag(MavenXpp3Writer.NAMESPACE, "tag");
            }
            if (scm.getUrl() != null) {
                serializer.startTag(MavenXpp3Writer.NAMESPACE, "url").text(scm.getUrl()).endTag(MavenXpp3Writer.NAMESPACE, "url");
            }
            serializer.endTag(MavenXpp3Writer.NAMESPACE, tagName);
        }
    }
    
    private void writeSite(final Site site, final String tagName, final XmlSerializer serializer) throws IOException {
        if (site != null) {
            serializer.startTag(MavenXpp3Writer.NAMESPACE, tagName);
            if (site.getId() != null) {
                serializer.startTag(MavenXpp3Writer.NAMESPACE, "id").text(site.getId()).endTag(MavenXpp3Writer.NAMESPACE, "id");
            }
            if (site.getName() != null) {
                serializer.startTag(MavenXpp3Writer.NAMESPACE, "name").text(site.getName()).endTag(MavenXpp3Writer.NAMESPACE, "name");
            }
            if (site.getUrl() != null) {
                serializer.startTag(MavenXpp3Writer.NAMESPACE, "url").text(site.getUrl()).endTag(MavenXpp3Writer.NAMESPACE, "url");
            }
            serializer.endTag(MavenXpp3Writer.NAMESPACE, tagName);
        }
    }
    
    static {
        NAMESPACE = null;
    }
}
