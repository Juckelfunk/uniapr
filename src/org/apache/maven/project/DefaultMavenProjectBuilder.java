// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.maven.project;

import org.apache.maven.model.Resource;
import java.util.HashSet;
import org.codehaus.plexus.context.ContextException;
import org.codehaus.plexus.context.Context;
import org.apache.maven.model.Extension;
import org.apache.maven.model.ReportPlugin;
import java.net.URL;
import java.io.StringReader;
import java.io.Reader;
import org.codehaus.plexus.util.IOUtil;
import java.io.FileNotFoundException;
import org.codehaus.plexus.util.ReaderFactory;
import org.apache.maven.profiles.ProfilesRoot;
import org.codehaus.plexus.util.xml.pull.XmlPullParserException;
import org.apache.maven.profiles.ProfilesConversionUtils;
import java.util.TreeMap;
import org.apache.maven.model.Parent;
import org.apache.maven.model.PluginExecution;
import org.apache.maven.model.Plugin;
import org.apache.maven.model.PluginManagement;
import org.apache.maven.project.validation.ModelValidationResult;
import org.apache.maven.artifact.ArtifactUtils;
import org.codehaus.plexus.util.StringUtils;
import org.apache.maven.model.Build;
import java.util.Set;
import org.apache.maven.model.PluginContainer;
import java.util.Collection;
import java.io.IOException;
import org.apache.maven.model.Profile;
import org.apache.maven.profiles.activation.ProfileActivationException;
import java.util.LinkedHashSet;
import java.util.LinkedList;
import java.util.Date;
import org.apache.maven.artifact.repository.ArtifactRepositoryPolicy;
import org.apache.maven.model.Repository;
import org.apache.maven.model.DistributionManagement;
import org.apache.maven.artifact.ArtifactStatus;
import java.util.Iterator;
import java.util.Collections;
import org.apache.maven.artifact.versioning.InvalidVersionSpecificationException;
import org.apache.maven.artifact.resolver.filter.ExcludesArtifactFilter;
import org.apache.maven.model.Exclusion;
import java.util.ArrayList;
import org.apache.maven.artifact.versioning.VersionRange;
import org.apache.maven.model.Dependency;
import org.apache.maven.artifact.versioning.ManagedVersionMap;
import org.apache.maven.model.DependencyManagement;
import org.codehaus.plexus.component.repository.exception.ComponentLookupException;
import org.apache.maven.artifact.resolver.ArtifactResolutionResult;
import org.apache.maven.project.artifact.InvalidDependencyVersionException;
import org.apache.maven.artifact.resolver.filter.ArtifactFilter;
import org.apache.maven.artifact.factory.ArtifactFactory;
import org.apache.maven.artifact.resolver.ArtifactNotFoundException;
import org.apache.maven.artifact.resolver.ArtifactResolutionException;
import org.apache.maven.wagon.events.TransferListener;
import org.apache.maven.artifact.InvalidRepositoryException;
import org.apache.maven.project.interpolation.ModelInterpolationException;
import org.apache.maven.profiles.DefaultProfileManager;
import org.apache.maven.model.Model;
import java.util.List;
import org.apache.maven.artifact.Artifact;
import org.apache.maven.profiles.ProfileManager;
import org.apache.maven.artifact.repository.ArtifactRepository;
import java.io.File;
import java.util.HashMap;
import org.apache.maven.artifact.manager.WagonManager;
import org.apache.maven.artifact.repository.ArtifactRepositoryFactory;
import org.apache.maven.project.interpolation.ModelInterpolator;
import org.apache.maven.project.injection.ModelDefaultsInjector;
import org.apache.maven.project.path.PathTranslator;
import org.apache.maven.model.io.xpp3.MavenXpp3Reader;
import java.util.Map;
import org.apache.maven.project.validation.ModelValidator;
import org.apache.maven.project.injection.ProfileInjector;
import org.apache.maven.project.inheritance.ModelInheritanceAssembler;
import org.apache.maven.project.artifact.ProjectArtifactFactory;
import org.apache.maven.artifact.metadata.ArtifactMetadataSource;
import org.apache.maven.artifact.resolver.ArtifactResolver;
import org.apache.maven.profiles.MavenProfilesBuilder;
import org.codehaus.plexus.PlexusContainer;
import org.codehaus.plexus.personality.plexus.lifecycle.phase.Contextualizable;
import org.codehaus.plexus.personality.plexus.lifecycle.phase.Initializable;
import org.codehaus.plexus.logging.AbstractLogEnabled;

public class DefaultMavenProjectBuilder extends AbstractLogEnabled implements MavenProjectBuilder, Initializable, Contextualizable
{
    private PlexusContainer container;
    protected MavenProfilesBuilder profilesBuilder;
    protected ArtifactResolver artifactResolver;
    protected ArtifactMetadataSource artifactMetadataSource;
    private ProjectArtifactFactory artifactFactory;
    private ModelInheritanceAssembler modelInheritanceAssembler;
    private ProfileInjector profileInjector;
    private ModelValidator validator;
    private Map rawProjectCache;
    private Map processedProjectCache;
    private MavenXpp3Reader modelReader;
    private PathTranslator pathTranslator;
    private ModelDefaultsInjector modelDefaultsInjector;
    private ModelInterpolator modelInterpolator;
    private ArtifactRepositoryFactory artifactRepositoryFactory;
    private WagonManager wagonManager;
    public static final String MAVEN_MODEL_VERSION = "4.0.0";
    
    public DefaultMavenProjectBuilder() {
        this.rawProjectCache = new HashMap();
        this.processedProjectCache = new HashMap();
    }
    
    public void initialize() {
        this.modelReader = new MavenXpp3Reader();
    }
    
    public MavenProject build(final File pom, final ProjectBuilderConfiguration config) throws ProjectBuildingException {
        return this.buildFromSourceFileInternal(pom, config, true);
    }
    
    public MavenProject build(final File pom, final ProjectBuilderConfiguration config, final boolean checkDistributionManagementStatus) throws ProjectBuildingException {
        return this.buildFromSourceFileInternal(pom, config, checkDistributionManagementStatus);
    }
    
    public MavenProject build(final File projectDescriptor, final ArtifactRepository localRepository, final ProfileManager profileManager) throws ProjectBuildingException {
        final ProjectBuilderConfiguration config = new DefaultProjectBuilderConfiguration().setLocalRepository(localRepository).setGlobalProfileManager(profileManager);
        return this.buildFromSourceFileInternal(projectDescriptor, config, true);
    }
    
    public MavenProject build(final File projectDescriptor, final ArtifactRepository localRepository, final ProfileManager profileManager, final boolean checkDistributionManagementStatus) throws ProjectBuildingException {
        final ProjectBuilderConfiguration config = new DefaultProjectBuilderConfiguration().setLocalRepository(localRepository).setGlobalProfileManager(profileManager);
        return this.buildFromSourceFileInternal(projectDescriptor, config, checkDistributionManagementStatus);
    }
    
    public MavenProject buildFromRepository(final Artifact artifact, final List remoteArtifactRepositories, final ArtifactRepository localRepository, final boolean allowStubModel) throws ProjectBuildingException {
        final String cacheKey = createCacheKey(artifact.getGroupId(), artifact.getArtifactId(), artifact.getVersion());
        final MavenProject project = this.processedProjectCache.get(cacheKey);
        if (project != null) {
            return project;
        }
        final Model model = this.findModelFromRepository(artifact, remoteArtifactRepositories, localRepository, allowStubModel);
        final ProjectBuilderConfiguration config = new DefaultProjectBuilderConfiguration().setLocalRepository(localRepository);
        return this.buildInternal("Artifact [" + artifact + "]", model, config, remoteArtifactRepositories, null, false);
    }
    
    public MavenProject buildFromRepository(final Artifact artifact, final List remoteArtifactRepositories, final ArtifactRepository localRepository) throws ProjectBuildingException {
        return this.buildFromRepository(artifact, remoteArtifactRepositories, localRepository, true);
    }
    
    public MavenProject buildStandaloneSuperProject(final ArtifactRepository localRepository) throws ProjectBuildingException {
        final ProfileManager profileManager = new DefaultProfileManager(this.container);
        return this.buildStandaloneSuperProject(new DefaultProjectBuilderConfiguration().setLocalRepository(localRepository).setGlobalProfileManager(profileManager));
    }
    
    public MavenProject buildStandaloneSuperProject(final ArtifactRepository localRepository, final ProfileManager profileManager) throws ProjectBuildingException {
        return this.buildStandaloneSuperProject(new DefaultProjectBuilderConfiguration().setLocalRepository(localRepository).setGlobalProfileManager(profileManager));
    }
    
    public MavenProject buildStandaloneSuperProject(final ProjectBuilderConfiguration config) throws ProjectBuildingException {
        final Model superModel = this.getSuperModel();
        superModel.setGroupId("org.apache.maven");
        superModel.setArtifactId("super-pom");
        superModel.setVersion("2.0");
        ProfileManager profileManager = config.getGlobalProfileManager();
        if (profileManager == null) {
            profileManager = new DefaultProfileManager(this.container);
        }
        profileManager.addProfiles(superModel.getProfiles());
        final String projectId = this.safeVersionlessKey("org.apache.maven", "super-pom");
        final List activeProfiles = this.injectActiveProfiles(profileManager, superModel);
        MavenProject project = new MavenProject(superModel);
        project.setManagedVersionMap(this.createManagedVersionMap(projectId, superModel.getDependencyManagement(), null));
        project.setActiveProfiles(activeProfiles);
        project.setOriginalModel(superModel);
        try {
            project = this.processProjectLogic("<Super-POM>", project, config, null, null, true, true);
            project.setExecutionRoot(true);
            return project;
        }
        catch (ModelInterpolationException e) {
            throw new ProjectBuildingException(projectId, e.getMessage(), e);
        }
        catch (InvalidRepositoryException e2) {
            throw new ProjectBuildingException(projectId, e2.getMessage(), e2);
        }
    }
    
    public MavenProject buildWithDependencies(final File projectDescriptor, final ArtifactRepository localRepository, final ProfileManager profileManager) throws ProjectBuildingException, ArtifactResolutionException, ArtifactNotFoundException {
        return this.buildWithDependencies(projectDescriptor, localRepository, profileManager, null);
    }
    
    public MavenProject buildWithDependencies(final File projectDescriptor, final ArtifactRepository localRepository, final ProfileManager profileManager, final TransferListener transferListener) throws ProjectBuildingException, ArtifactResolutionException, ArtifactNotFoundException {
        final MavenProject project = this.build(projectDescriptor, localRepository, profileManager, false);
        final Artifact projectArtifact = project.getArtifact();
        final String projectId = this.safeVersionlessKey(project.getGroupId(), project.getArtifactId());
        final Map managedVersions = project.getManagedVersionMap();
        this.ensureMetadataSourceIsInitialized();
        try {
            project.setDependencyArtifacts(project.createArtifacts(this.artifactFactory, null, null));
        }
        catch (InvalidDependencyVersionException e) {
            throw new ProjectBuildingException(projectId, "Unable to build project due to an invalid dependency version: " + e.getMessage(), e);
        }
        if (transferListener != null) {
            this.wagonManager.setDownloadMonitor(transferListener);
        }
        final ArtifactResolutionResult result = this.artifactResolver.resolveTransitively(project.getDependencyArtifacts(), projectArtifact, managedVersions, localRepository, project.getRemoteArtifactRepositories(), this.artifactMetadataSource);
        project.setArtifacts(result.getArtifacts());
        return project;
    }
    
    private void ensureMetadataSourceIsInitialized() throws ProjectBuildingException {
        if (this.artifactMetadataSource == null) {
            try {
                this.artifactMetadataSource = (ArtifactMetadataSource)this.container.lookup(ArtifactMetadataSource.ROLE);
            }
            catch (ComponentLookupException e) {
                throw new ProjectBuildingException("all", "Cannot lookup metadata source for building the project.", e);
            }
        }
    }
    
    private Map createManagedVersionMap(final String projectId, final DependencyManagement dependencyManagement, final MavenProject parent) throws ProjectBuildingException {
        Map map = null;
        final List deps;
        if (dependencyManagement != null && (deps = dependencyManagement.getDependencies()) != null && deps.size() > 0) {
            map = new ManagedVersionMap(map);
            if (this.getLogger().isDebugEnabled()) {
                this.getLogger().debug("Adding managed dependencies for " + projectId);
            }
            for (final Dependency d : dependencyManagement.getDependencies()) {
                try {
                    final VersionRange versionRange = VersionRange.createFromVersionSpec(d.getVersion());
                    final Artifact artifact = this.artifactFactory.createDependencyArtifact(d.getGroupId(), d.getArtifactId(), versionRange, d.getType(), d.getClassifier(), d.getScope(), d.isOptional());
                    if (this.getLogger().isDebugEnabled()) {
                        this.getLogger().debug("  " + artifact);
                    }
                    if (null != d.getExclusions() && !d.getExclusions().isEmpty()) {
                        final List exclusions = new ArrayList();
                        for (final Exclusion e : d.getExclusions()) {
                            exclusions.add(e.getGroupId() + ":" + e.getArtifactId());
                        }
                        final ExcludesArtifactFilter eaf = new ExcludesArtifactFilter(exclusions);
                        artifact.setDependencyFilter(eaf);
                    }
                    else {
                        artifact.setDependencyFilter(null);
                    }
                    map.put(d.getManagementKey(), artifact);
                }
                catch (InvalidVersionSpecificationException e2) {
                    throw new ProjectBuildingException(projectId, "Unable to parse version '" + d.getVersion() + "' for dependency '" + d.getManagementKey() + "': " + e2.getMessage(), e2);
                }
            }
        }
        else if (map == null) {
            map = Collections.EMPTY_MAP;
        }
        return map;
    }
    
    private MavenProject buildFromSourceFileInternal(final File projectDescriptor, final ProjectBuilderConfiguration config, final boolean checkDistributionManagementStatus) throws ProjectBuildingException {
        final Model model = this.readModel("unknown", projectDescriptor, true);
        final MavenProject project = this.buildInternal(projectDescriptor.getAbsolutePath(), model, config, this.buildArtifactRepositories(this.getSuperModel()), projectDescriptor, true);
        if (checkDistributionManagementStatus && project.getDistributionManagement() != null && project.getDistributionManagement().getStatus() != null) {
            final String projectId = this.safeVersionlessKey(project.getGroupId(), project.getArtifactId());
            throw new ProjectBuildingException(projectId, "Invalid project file: distribution status must not be specified for a project outside of the repository");
        }
        return project;
    }
    
    private Model findModelFromRepository(final Artifact artifact, final List remoteArtifactRepositories, final ArtifactRepository localRepository, final boolean allowStubModel) throws ProjectBuildingException {
        final String projectId = this.safeVersionlessKey(artifact.getGroupId(), artifact.getArtifactId());
        this.normalizeToArtifactRepositories(remoteArtifactRepositories, projectId);
        Artifact projectArtifact;
        if ("pom".equals(artifact.getType())) {
            projectArtifact = artifact;
        }
        else {
            this.getLogger().debug("Attempting to build MavenProject instance for Artifact (" + artifact.getGroupId() + ":" + artifact.getArtifactId() + ":" + artifact.getVersion() + ") of type: " + artifact.getType() + "; constructing POM artifact instead.");
            projectArtifact = this.artifactFactory.createProjectArtifact(artifact.getGroupId(), artifact.getArtifactId(), artifact.getVersion(), artifact.getScope());
        }
        Model model;
        try {
            this.artifactResolver.resolve(projectArtifact, remoteArtifactRepositories, localRepository);
            final File file = projectArtifact.getFile();
            model = this.readModel(projectId, file, false);
            String downloadUrl = null;
            ArtifactStatus status = ArtifactStatus.NONE;
            final DistributionManagement distributionManagement = model.getDistributionManagement();
            if (distributionManagement != null) {
                downloadUrl = distributionManagement.getDownloadUrl();
                status = ArtifactStatus.valueOf(distributionManagement.getStatus());
            }
            this.checkStatusAndUpdate(projectArtifact, status, file, remoteArtifactRepositories, localRepository);
            if (downloadUrl != null) {
                projectArtifact.setDownloadUrl(downloadUrl);
            }
            else {
                projectArtifact.setDownloadUrl(model.getUrl());
            }
        }
        catch (ArtifactResolutionException e) {
            throw new ProjectBuildingException(projectId, "Error getting POM for '" + projectId + "' from the repository: " + e.getMessage(), e);
        }
        catch (ArtifactNotFoundException e2) {
            if (!allowStubModel) {
                throw new ProjectBuildingException(projectId, "POM '" + projectId + "' not found in repository: " + e2.getMessage(), e2);
            }
            this.getLogger().debug("Artifact not found - using stub model: " + e2.getMessage());
            model = this.createStubModel(projectArtifact);
        }
        return model;
    }
    
    private List normalizeToArtifactRepositories(final List remoteArtifactRepositories, final String projectId) throws ProjectBuildingException {
        final List normalized = new ArrayList(remoteArtifactRepositories.size());
        boolean normalizationNeeded = false;
        for (Object item : remoteArtifactRepositories) {
            if (item instanceof ArtifactRepository) {
                normalized.add(item);
            }
            else {
                if (!(item instanceof Repository)) {
                    throw new ProjectBuildingException(projectId, "Error building artifact repository from non-repository information item: " + item);
                }
                final Repository repo = (Repository)item;
                try {
                    item = ProjectUtils.buildArtifactRepository(repo, this.artifactRepositoryFactory, this.container);
                    normalized.add(item);
                    normalizationNeeded = true;
                }
                catch (InvalidRepositoryException e) {
                    throw new ProjectBuildingException(projectId, "Error building artifact repository for id: " + repo.getId(), e);
                }
            }
        }
        if (normalizationNeeded) {
            return normalized;
        }
        return remoteArtifactRepositories;
    }
    
    private void checkStatusAndUpdate(final Artifact projectArtifact, final ArtifactStatus status, final File file, final List remoteArtifactRepositories, final ArtifactRepository localRepository) throws ArtifactNotFoundException {
        if (!projectArtifact.isSnapshot() && status.compareTo(ArtifactStatus.DEPLOYED) < 0) {
            final ArtifactRepositoryPolicy policy = new ArtifactRepositoryPolicy();
            policy.setUpdatePolicy("never");
            if (policy.checkOutOfDate(new Date(file.lastModified()))) {
                this.getLogger().info(projectArtifact.getArtifactId() + ": updating metadata due to status of '" + status + "'");
                try {
                    projectArtifact.setResolved(false);
                    this.artifactResolver.resolveAlways(projectArtifact, remoteArtifactRepositories, localRepository);
                }
                catch (ArtifactResolutionException e) {
                    this.getLogger().warn("Error updating POM - using existing version");
                    this.getLogger().debug("Cause", e);
                }
                catch (ArtifactNotFoundException e2) {
                    this.getLogger().warn("Error updating POM - not found. Removing local copy.");
                    this.getLogger().debug("Cause", e2);
                    file.delete();
                    throw e2;
                }
            }
        }
    }
    
    private Model createStubModel(final Artifact projectArtifact) {
        this.getLogger().debug("Using defaults for missing POM " + projectArtifact);
        final Model model = new Model();
        model.setModelVersion("4.0.0");
        model.setArtifactId(projectArtifact.getArtifactId());
        model.setGroupId(projectArtifact.getGroupId());
        model.setVersion(projectArtifact.getVersion());
        model.setPackaging(projectArtifact.getType());
        model.setDistributionManagement(new DistributionManagement());
        model.getDistributionManagement().setStatus(ArtifactStatus.GENERATED.toString());
        return model;
    }
    
    private MavenProject buildInternal(final String pomLocation, final Model model, final ProjectBuilderConfiguration config, final List parentSearchRepositories, final File projectDescriptor, final boolean strict) throws ProjectBuildingException {
        File projectDir = null;
        if (projectDescriptor != null) {
            projectDir = projectDescriptor.getAbsoluteFile().getParentFile();
        }
        final Model superModel = this.getSuperModel();
        final ProfileManager externalProfileManager = config.getGlobalProfileManager();
        ProfileManager superProjectProfileManager;
        if (externalProfileManager != null) {
            superProjectProfileManager = new DefaultProfileManager(this.container, externalProfileManager.getRequestProperties());
        }
        else {
            superProjectProfileManager = new DefaultProfileManager(this.container);
        }
        superProjectProfileManager.addProfiles(superModel.getProfiles());
        final List activeProfiles = this.injectActiveProfiles(superProjectProfileManager, superModel);
        final MavenProject superProject = new MavenProject(superModel);
        superProject.setActiveProfiles(activeProfiles);
        final LinkedList lineage = new LinkedList();
        final Set aggregatedRemoteWagonRepositories = new LinkedHashSet();
        final String projectId = this.safeVersionlessKey(model.getGroupId(), model.getArtifactId());
        List activeExternalProfiles;
        try {
            if (externalProfileManager != null) {
                activeExternalProfiles = externalProfileManager.getActiveProfiles();
            }
            else {
                activeExternalProfiles = Collections.EMPTY_LIST;
            }
        }
        catch (ProfileActivationException e) {
            throw new ProjectBuildingException(projectId, "Failed to calculate active external profiles.", e);
        }
        for (final Profile externalProfile : activeExternalProfiles) {
            for (final Repository mavenRepo : externalProfile.getRepositories()) {
                ArtifactRepository artifactRepo = null;
                try {
                    artifactRepo = ProjectUtils.buildArtifactRepository(mavenRepo, this.artifactRepositoryFactory, this.container);
                }
                catch (InvalidRepositoryException e2) {
                    throw new ProjectBuildingException(projectId, e2.getMessage(), e2);
                }
                aggregatedRemoteWagonRepositories.add(artifactRepo);
            }
        }
        MavenProject project = null;
        try {
            project = this.assembleLineage(model, lineage, config, projectDescriptor, parentSearchRepositories, aggregatedRemoteWagonRepositories, strict);
        }
        catch (InvalidRepositoryException e3) {
            throw new ProjectBuildingException(projectId, e3.getMessage(), e3);
        }
        MavenProject previousProject = superProject;
        Model previous = superProject.getModel();
        for (final MavenProject currentProject : lineage) {
            final Model current = currentProject.getModel();
            String pathAdjustment = null;
            try {
                pathAdjustment = previousProject.getModulePathAdjustment(currentProject);
            }
            catch (IOException e4) {
                this.getLogger().debug("Cannot determine whether " + currentProject.getId() + " is a module of " + previousProject.getId() + ". Reason: " + e4.getMessage(), e4);
            }
            this.modelInheritanceAssembler.assembleModelInheritance(current, previous, pathAdjustment);
            previous = current;
            previousProject = currentProject;
        }
        final List repositories = new ArrayList(aggregatedRemoteWagonRepositories);
        final List superRepositories = this.buildArtifactRepositories(superModel);
        for (final ArtifactRepository repository : superRepositories) {
            if (!repositories.contains(repository)) {
                repositories.add(repository);
            }
        }
        ModelUtils.mergeDuplicatePluginDefinitions(project.getModel().getBuild());
        try {
            project = this.processProjectLogic(pomLocation, project, config, projectDir, repositories, strict, false);
        }
        catch (ModelInterpolationException e5) {
            throw new InvalidProjectModelException(projectId, pomLocation, e5.getMessage(), e5);
        }
        catch (InvalidRepositoryException e2) {
            throw new InvalidProjectModelException(projectId, pomLocation, e2.getMessage(), e2);
        }
        this.processedProjectCache.put(createCacheKey(project.getGroupId(), project.getArtifactId(), project.getVersion()), project);
        if (projectDescriptor != null) {
            final Build build = project.getBuild();
            project.addCompileSourceRoot(build.getSourceDirectory());
            project.addScriptSourceRoot(build.getScriptSourceDirectory());
            project.addTestCompileSourceRoot(build.getTestSourceDirectory());
            project.setFile(projectDescriptor);
        }
        project.setManagedVersionMap(this.createManagedVersionMap(projectId, project.getDependencyManagement(), project.getParent()));
        return project;
    }
    
    private String safeVersionlessKey(final String groupId, final String artifactId) {
        String gid = groupId;
        if (StringUtils.isEmpty(gid)) {
            gid = "unknown";
        }
        String aid = artifactId;
        if (StringUtils.isEmpty(aid)) {
            aid = "unknown";
        }
        return ArtifactUtils.versionlessKey(gid, aid);
    }
    
    private List buildArtifactRepositories(final Model model) throws ProjectBuildingException {
        try {
            return ProjectUtils.buildArtifactRepositories(model.getRepositories(), this.artifactRepositoryFactory, this.container);
        }
        catch (InvalidRepositoryException e) {
            final String projectId = this.safeVersionlessKey(model.getGroupId(), model.getArtifactId());
            throw new ProjectBuildingException(projectId, e.getMessage(), e);
        }
    }
    
    private MavenProject processProjectLogic(final String pomLocation, MavenProject project, final ProjectBuilderConfiguration config, final File projectDir, final List remoteRepositories, final boolean strict, final boolean isSuperPom) throws ProjectBuildingException, ModelInterpolationException, InvalidRepositoryException {
        Model model = project.getModel();
        List activeProfiles = project.getActiveProfiles();
        if (activeProfiles == null) {
            activeProfiles = new ArrayList();
        }
        final ProfileManager profileMgr = (config == null) ? null : config.getGlobalProfileManager();
        final List injectedProfiles = this.injectActiveProfiles(profileMgr, model);
        activeProfiles.addAll(injectedProfiles);
        final Build dynamicBuild = model.getBuild();
        model.setBuild(ModelUtils.cloneBuild(dynamicBuild));
        model = this.modelInterpolator.interpolate(model, projectDir, config, this.getLogger().isDebugEnabled());
        this.mergeDeterministicBuildElements(model.getBuild(), dynamicBuild);
        model.setBuild(dynamicBuild);
        if (!isSuperPom) {
            this.mergeManagedDependencies(model, config.getLocalRepository(), remoteRepositories);
        }
        this.modelDefaultsInjector.injectDefaults(model);
        final MavenProject parentProject = project.getParent();
        final Model originalModel = project.getOriginalModel();
        project = new MavenProject(model, this.getLogger());
        project.setOriginalModel(originalModel);
        project.setActiveProfiles(activeProfiles);
        final Artifact projectArtifact = this.artifactFactory.create(project);
        project.setArtifact(projectArtifact);
        project.setProjectBuilderConfiguration(config);
        project.setPluginArtifactRepositories(ProjectUtils.buildArtifactRepositories(model.getPluginRepositories(), this.artifactRepositoryFactory, this.container));
        final DistributionManagement dm = model.getDistributionManagement();
        if (dm != null) {
            ArtifactRepository repo = ProjectUtils.buildDeploymentArtifactRepository(dm.getRepository(), this.artifactRepositoryFactory, this.container);
            project.setReleaseArtifactRepository(repo);
            if (dm.getSnapshotRepository() != null) {
                repo = ProjectUtils.buildDeploymentArtifactRepository(dm.getSnapshotRepository(), this.artifactRepositoryFactory, this.container);
                project.setSnapshotArtifactRepository(repo);
            }
        }
        if (parentProject != null) {
            final String cacheKey = createCacheKey(parentProject.getGroupId(), parentProject.getArtifactId(), parentProject.getVersion());
            final MavenProject processedParent = this.processedProjectCache.get(cacheKey);
            Artifact parentArtifact;
            if (processedParent != null) {
                project.setParent(processedParent);
                parentArtifact = processedParent.getArtifact();
            }
            else {
                project.setParent(parentProject);
                parentArtifact = this.artifactFactory.createParentArtifact(parentProject.getGroupId(), parentProject.getArtifactId(), parentProject.getVersion());
            }
            project.setParentArtifact(parentArtifact);
        }
        final ModelValidationResult validationResult = this.validator.validate(model);
        final String projectId = this.safeVersionlessKey(model.getGroupId(), model.getArtifactId());
        if (validationResult.getMessageCount() > 0) {
            throw new InvalidProjectModelException(projectId, pomLocation, "Failed to validate POM", validationResult);
        }
        project.setRemoteArtifactRepositories(ProjectUtils.buildArtifactRepositories(model.getRepositories(), this.artifactRepositoryFactory, this.container));
        project.setPluginArtifacts(this.createPluginArtifacts(projectId, project.getBuildPlugins()));
        project.setReportArtifacts(this.createReportArtifacts(projectId, project.getReportPlugins()));
        project.setExtensionArtifacts(this.createExtensionArtifacts(projectId, project.getBuildExtensions()));
        return project;
    }
    
    private void mergeDeterministicBuildElements(final Build interpolatedBuild, final Build dynamicBuild) {
        this.mergeDeterministicPluginElements(interpolatedBuild.getPlugins(), dynamicBuild.getPlugins());
        final PluginManagement dPluginMgmt = dynamicBuild.getPluginManagement();
        final PluginManagement iPluginMgmt = interpolatedBuild.getPluginManagement();
        if (dPluginMgmt != null) {
            this.mergeDeterministicPluginElements(iPluginMgmt.getPlugins(), dPluginMgmt.getPlugins());
        }
        if (dynamicBuild.getExtensions() != null) {
            dynamicBuild.setExtensions(interpolatedBuild.getExtensions());
        }
    }
    
    private void mergeDeterministicPluginElements(final List iPlugins, final List dPlugins) {
        if (dPlugins != null) {
            for (int i = 0; i < dPlugins.size(); ++i) {
                final Plugin dPlugin = dPlugins.get(i);
                final Plugin iPlugin = iPlugins.get(i);
                dPlugin.setGroupId(iPlugin.getGroupId());
                dPlugin.setArtifactId(iPlugin.getArtifactId());
                dPlugin.setVersion(iPlugin.getVersion());
                dPlugin.setDependencies(iPlugin.getDependencies());
                final List dExecutions = dPlugin.getExecutions();
                if (dExecutions != null) {
                    final List iExecutions = iPlugin.getExecutions();
                    for (int j = 0; j < dExecutions.size(); ++j) {
                        final PluginExecution dExec = dExecutions.get(j);
                        final PluginExecution iExec = iExecutions.get(j);
                        dExec.setId(iExec.getId());
                    }
                }
            }
        }
    }
    
    private MavenProject assembleLineage(Model model, final LinkedList lineage, final ProjectBuilderConfiguration config, final File projectDescriptor, final List parentSearchRepositories, final Set aggregatedRemoteWagonRepositories, final boolean strict) throws ProjectBuildingException, InvalidRepositoryException {
        final Model originalModel = ModelUtils.cloneModel(model);
        File projectDir = null;
        if (projectDescriptor != null) {
            projectDir = projectDescriptor.getAbsoluteFile().getParentFile();
        }
        final ProfileManager externalProfileManager = config.getGlobalProfileManager();
        ProfileManager profileManager;
        if (externalProfileManager != null) {
            profileManager = new DefaultProfileManager(this.container, externalProfileManager.getRequestProperties());
        }
        else {
            profileManager = new DefaultProfileManager(this.container);
        }
        if (externalProfileManager != null) {
            profileManager.explicitlyActivate(externalProfileManager.getExplicitlyActivatedIds());
            profileManager.explicitlyDeactivate(externalProfileManager.getExplicitlyDeactivatedIds());
        }
        List activeProfiles;
        try {
            profileManager.addProfiles(model.getProfiles());
            this.loadProjectExternalProfiles(profileManager, projectDir);
            activeProfiles = this.injectActiveProfiles(profileManager, model);
        }
        catch (ProfileActivationException e) {
            final String projectId = this.safeVersionlessKey(model.getGroupId(), model.getArtifactId());
            throw new ProjectBuildingException(projectId, "Failed to activate local (project-level) build profiles: " + e.getMessage(), e);
        }
        if (!model.getRepositories().isEmpty()) {
            final List respositories = this.buildArtifactRepositories(model);
            for (final ArtifactRepository repository : respositories) {
                if (!aggregatedRemoteWagonRepositories.contains(repository)) {
                    aggregatedRemoteWagonRepositories.add(repository);
                }
            }
        }
        final MavenProject project = new MavenProject(model, this.getLogger());
        project.setFile(projectDescriptor);
        project.setActiveProfiles(activeProfiles);
        project.setOriginalModel(originalModel);
        lineage.addFirst(project);
        final Parent parentModel = model.getParent();
        final String projectId2 = this.safeVersionlessKey(model.getGroupId(), model.getArtifactId());
        if (parentModel != null) {
            if (StringUtils.isEmpty(parentModel.getGroupId())) {
                throw new ProjectBuildingException(projectId2, "Missing groupId element from parent element");
            }
            if (StringUtils.isEmpty(parentModel.getArtifactId())) {
                throw new ProjectBuildingException(projectId2, "Missing artifactId element from parent element");
            }
            if (parentModel.getGroupId().equals(model.getGroupId()) && parentModel.getArtifactId().equals(model.getArtifactId())) {
                throw new ProjectBuildingException(projectId2, "Parent element is a duplicate of the current project ");
            }
            if (StringUtils.isEmpty(parentModel.getVersion())) {
                throw new ProjectBuildingException(projectId2, "Missing version element from parent element");
            }
            File parentDescriptor = null;
            model = null;
            final String parentKey = createCacheKey(parentModel.getGroupId(), parentModel.getArtifactId(), parentModel.getVersion());
            final MavenProject parentProject = this.rawProjectCache.get(parentKey);
            if (parentProject != null) {
                model = ModelUtils.cloneModel(parentProject.getOriginalModel());
                parentDescriptor = parentProject.getFile();
            }
            final String parentRelativePath = parentModel.getRelativePath();
            if (model == null && projectDir != null && StringUtils.isNotEmpty(parentRelativePath)) {
                parentDescriptor = new File(projectDir, parentRelativePath);
                if (this.getLogger().isDebugEnabled()) {
                    this.getLogger().debug("Searching for parent-POM: " + parentModel.getId() + " of project: " + project.getId() + " in relative path: " + parentRelativePath);
                }
                if (parentDescriptor.isDirectory()) {
                    if (this.getLogger().isDebugEnabled()) {
                        this.getLogger().debug("Path specified in <relativePath/> (" + parentRelativePath + ") is a directory. Searching for 'pom.xml' within this directory.");
                    }
                    parentDescriptor = new File(parentDescriptor, "pom.xml");
                    if (!parentDescriptor.exists() && this.getLogger().isDebugEnabled()) {
                        this.getLogger().debug("Parent-POM: " + parentModel.getId() + " for project: " + project.getId() + " cannot be loaded from relative path: " + parentDescriptor + "; path does not exist.");
                    }
                }
                if (parentDescriptor != null) {
                    try {
                        parentDescriptor = parentDescriptor.getCanonicalFile();
                    }
                    catch (IOException e2) {
                        this.getLogger().debug("Failed to canonicalize potential parent POM: '" + parentDescriptor + "'", e2);
                        parentDescriptor = null;
                    }
                }
                if (parentDescriptor != null && parentDescriptor.exists()) {
                    final Model candidateParent = this.readModel(projectId2, parentDescriptor, strict);
                    String candidateParentGroupId = candidateParent.getGroupId();
                    if (candidateParentGroupId == null && candidateParent.getParent() != null) {
                        candidateParentGroupId = candidateParent.getParent().getGroupId();
                    }
                    String candidateParentVersion = candidateParent.getVersion();
                    if (candidateParentVersion == null && candidateParent.getParent() != null) {
                        candidateParentVersion = candidateParent.getParent().getVersion();
                    }
                    if (parentModel.getGroupId().equals(candidateParentGroupId) && parentModel.getArtifactId().equals(candidateParent.getArtifactId()) && parentModel.getVersion().equals(candidateParentVersion)) {
                        model = candidateParent;
                        this.getLogger().debug("Using parent-POM from the project hierarchy at: '" + parentModel.getRelativePath() + "' for project: " + project.getId());
                    }
                    else {
                        this.getLogger().debug("Invalid parent-POM referenced by relative path '" + parentModel.getRelativePath() + "' in parent specification in " + project.getId() + ":" + "\n  Specified: " + parentModel.getId() + "\n  Found:     " + candidateParent.getId());
                    }
                }
                else if (this.getLogger().isDebugEnabled()) {
                    this.getLogger().debug("Parent-POM: " + parentModel.getId() + " not found in relative path: " + parentRelativePath);
                }
            }
            Artifact parentArtifact = null;
            if (model == null) {
                parentDescriptor = null;
                final List remoteRepositories = new ArrayList(aggregatedRemoteWagonRepositories);
                remoteRepositories.addAll(parentSearchRepositories);
                if (this.getLogger().isDebugEnabled()) {
                    this.getLogger().debug("Retrieving parent-POM: " + parentModel.getId() + " for project: " + project.getId() + " from the repository.");
                }
                parentArtifact = this.artifactFactory.createParentArtifact(parentModel.getGroupId(), parentModel.getArtifactId(), parentModel.getVersion());
                try {
                    model = this.findModelFromRepository(parentArtifact, remoteRepositories, config.getLocalRepository(), false);
                }
                catch (ProjectBuildingException e3) {
                    throw new ProjectBuildingException(project.getId(), "Cannot find parent: " + e3.getProjectId() + " for project: " + project.getId(), e3);
                }
            }
            if (model != null && !"pom".equals(model.getPackaging())) {
                throw new ProjectBuildingException(projectId2, "Parent: " + model.getId() + " of project: " + projectId2 + " has wrong packaging: " + model.getPackaging() + ". Must be 'pom'.");
            }
            final MavenProject parent = this.assembleLineage(model, lineage, config, parentDescriptor, parentSearchRepositories, aggregatedRemoteWagonRepositories, strict);
            project.setParent(parent);
            project.setParentArtifact(parentArtifact);
        }
        this.rawProjectCache.put(createCacheKey(project.getGroupId(), project.getArtifactId(), project.getVersion()), new MavenProject(project));
        return project;
    }
    
    private void mergeManagedDependencies(final Model model, final ArtifactRepository localRepository, final List parentSearchRepositories) throws ProjectBuildingException {
        final DependencyManagement modelDepMgmt = model.getDependencyManagement();
        if (modelDepMgmt != null) {
            final Map depsMap = new TreeMap();
            Iterator iter = modelDepMgmt.getDependencies().iterator();
            boolean doInclude = false;
            while (iter.hasNext()) {
                final Dependency dep = iter.next();
                depsMap.put(dep.getManagementKey(), dep);
                if (dep.getType().equals("pom") && "import".equals(dep.getScope())) {
                    doInclude = true;
                }
            }
            final Map newDeps = new TreeMap(depsMap);
            iter = modelDepMgmt.getDependencies().iterator();
            if (doInclude) {
                while (iter.hasNext()) {
                    final Dependency dep2 = iter.next();
                    if (dep2.getType().equals("pom") && "import".equals(dep2.getScope())) {
                        final Artifact artifact = this.artifactFactory.createProjectArtifact(dep2.getGroupId(), dep2.getArtifactId(), dep2.getVersion(), dep2.getScope());
                        final MavenProject project = this.buildFromRepository(artifact, parentSearchRepositories, localRepository, false);
                        final DependencyManagement depMgmt = project.getDependencyManagement();
                        if (depMgmt == null) {
                            continue;
                        }
                        if (this.getLogger().isDebugEnabled()) {
                            this.getLogger().debug("Importing managed dependencies for " + dep2.toString());
                        }
                        for (final Dependency includedDep : depMgmt.getDependencies()) {
                            final String key = includedDep.getManagementKey();
                            if (!newDeps.containsKey(key)) {
                                newDeps.put(includedDep.getManagementKey(), includedDep);
                            }
                        }
                        newDeps.remove(dep2.getManagementKey());
                    }
                }
                final List deps = new ArrayList(newDeps.values());
                modelDepMgmt.setDependencies(deps);
            }
        }
    }
    
    private List injectActiveProfiles(final ProfileManager profileManager, final Model model) throws ProjectBuildingException {
        List activeProfiles;
        if (profileManager != null) {
            try {
                activeProfiles = profileManager.getActiveProfiles();
            }
            catch (ProfileActivationException e) {
                final String projectId = this.safeVersionlessKey(model.getGroupId(), model.getArtifactId());
                throw new ProjectBuildingException(projectId, e.getMessage(), e);
            }
            for (final Profile profile : activeProfiles) {
                this.profileInjector.inject(profile, model);
            }
        }
        else {
            activeProfiles = Collections.EMPTY_LIST;
        }
        return activeProfiles;
    }
    
    private void loadProjectExternalProfiles(final ProfileManager profileManager, final File projectDir) throws ProfileActivationException {
        if (projectDir != null) {
            try {
                final ProfilesRoot root = this.profilesBuilder.buildProfiles(projectDir);
                if (root != null) {
                    final List active = root.getActiveProfiles();
                    if (active != null && !active.isEmpty()) {
                        profileManager.explicitlyActivate(root.getActiveProfiles());
                    }
                    for (final org.apache.maven.profiles.Profile rawProfile : root.getProfiles()) {
                        final Profile converted = ProfilesConversionUtils.convertFromProfileXmlProfile(rawProfile);
                        profileManager.addProfile(converted);
                    }
                }
            }
            catch (IOException e) {
                throw new ProfileActivationException("Cannot read profiles.xml resource from directory: " + projectDir, e);
            }
            catch (XmlPullParserException e2) {
                throw new ProfileActivationException("Cannot parse profiles.xml resource from directory: " + projectDir, e2);
            }
        }
    }
    
    private Model readModel(final String projectId, final File file, final boolean strict) throws ProjectBuildingException {
        Reader reader = null;
        try {
            reader = ReaderFactory.newXmlReader(file);
            return this.readModel(projectId, file.getAbsolutePath(), reader, strict);
        }
        catch (FileNotFoundException e) {
            throw new ProjectBuildingException(projectId, "Could not find the model file '" + file.getAbsolutePath() + "'.", e);
        }
        catch (IOException e2) {
            throw new ProjectBuildingException(projectId, "Failed to build model from file '" + file.getAbsolutePath() + "'.\nError: '" + e2.getLocalizedMessage() + "'", e2);
        }
        finally {
            IOUtil.close(reader);
        }
    }
    
    private Model readModel(final String projectId, final String pomLocation, final Reader reader, final boolean strict) throws IOException, InvalidProjectModelException {
        final String modelSource = IOUtil.toString(reader);
        if (modelSource.indexOf("<modelVersion>4.0.0") < 0) {
            throw new InvalidProjectModelException(projectId, pomLocation, "Not a v4.0.0 POM.");
        }
        final StringReader sReader = new StringReader(modelSource);
        try {
            return this.modelReader.read(sReader, strict);
        }
        catch (XmlPullParserException e) {
            throw new InvalidProjectModelException(projectId, pomLocation, "Parse error reading POM. Reason: " + e.getMessage(), e);
        }
    }
    
    private Model readModel(final String projectId, final URL url, final boolean strict) throws ProjectBuildingException {
        Reader reader = null;
        try {
            reader = ReaderFactory.newXmlReader(url.openStream());
            return this.readModel(projectId, url.toExternalForm(), reader, strict);
        }
        catch (IOException e) {
            throw new ProjectBuildingException(projectId, "Failed build model from URL '" + url.toExternalForm() + "'\nError: '" + e.getLocalizedMessage() + "'", e);
        }
        finally {
            IOUtil.close(reader);
        }
    }
    
    private static String createCacheKey(final String groupId, final String artifactId, final String version) {
        return groupId + ":" + artifactId + ":" + version;
    }
    
    protected Set createPluginArtifacts(final String projectId, final List plugins) throws ProjectBuildingException {
        final Set pluginArtifacts = new LinkedHashSet();
        for (final Plugin p : plugins) {
            String version;
            if (StringUtils.isEmpty(p.getVersion())) {
                version = "RELEASE";
            }
            else {
                version = p.getVersion();
            }
            Artifact artifact;
            try {
                artifact = this.artifactFactory.createPluginArtifact(p.getGroupId(), p.getArtifactId(), VersionRange.createFromVersionSpec(version));
            }
            catch (InvalidVersionSpecificationException e) {
                throw new ProjectBuildingException(projectId, "Unable to parse version '" + version + "' for plugin '" + ArtifactUtils.versionlessKey(p.getGroupId(), p.getArtifactId()) + "': " + e.getMessage(), e);
            }
            if (artifact != null) {
                pluginArtifacts.add(artifact);
            }
        }
        return pluginArtifacts;
    }
    
    protected Set createReportArtifacts(final String projectId, final List reports) throws ProjectBuildingException {
        final Set pluginArtifacts = new LinkedHashSet();
        if (reports != null) {
            for (final ReportPlugin p : reports) {
                String version;
                if (StringUtils.isEmpty(p.getVersion())) {
                    version = "RELEASE";
                }
                else {
                    version = p.getVersion();
                }
                Artifact artifact;
                try {
                    artifact = this.artifactFactory.createPluginArtifact(p.getGroupId(), p.getArtifactId(), VersionRange.createFromVersionSpec(version));
                }
                catch (InvalidVersionSpecificationException e) {
                    throw new ProjectBuildingException(projectId, "Unable to parse version '" + version + "' for report '" + ArtifactUtils.versionlessKey(p.getGroupId(), p.getArtifactId()) + "': " + e.getMessage(), e);
                }
                if (artifact != null) {
                    pluginArtifacts.add(artifact);
                }
            }
        }
        return pluginArtifacts;
    }
    
    protected Set createExtensionArtifacts(final String projectId, final List extensions) throws ProjectBuildingException {
        final Set extensionArtifacts = new LinkedHashSet();
        if (extensions != null) {
            for (final Extension ext : extensions) {
                String version;
                if (StringUtils.isEmpty(ext.getVersion())) {
                    version = "RELEASE";
                }
                else {
                    version = ext.getVersion();
                }
                Artifact artifact;
                try {
                    final VersionRange versionRange = VersionRange.createFromVersionSpec(version);
                    artifact = this.artifactFactory.createExtensionArtifact(ext.getGroupId(), ext.getArtifactId(), versionRange);
                }
                catch (InvalidVersionSpecificationException e) {
                    throw new ProjectBuildingException(projectId, "Unable to parse version '" + version + "' for extension '" + ArtifactUtils.versionlessKey(ext.getGroupId(), ext.getArtifactId()) + "': " + e.getMessage(), e);
                }
                if (artifact != null) {
                    extensionArtifacts.add(artifact);
                }
            }
        }
        return extensionArtifacts;
    }
    
    private Model getSuperModel() throws ProjectBuildingException {
        final URL url = DefaultMavenProjectBuilder.class.getResource("pom-4.0.0.xml");
        final String projectId = this.safeVersionlessKey("org.apache.maven", "super-pom");
        return this.readModel(projectId, url, true);
    }
    
    public void contextualize(final Context context) throws ContextException {
        this.container = (PlexusContainer)context.get("plexus");
    }
    
    public void calculateConcreteState(final MavenProject project, final ProjectBuilderConfiguration config) throws ModelInterpolationException {
        this.calculateConcreteStateInternal(project, config, true, new HashSet());
    }
    
    public void calculateConcreteState(final MavenProject project, final ProjectBuilderConfiguration config, final boolean processProjectReferences) throws ModelInterpolationException {
        this.calculateConcreteStateInternal(project, config, processProjectReferences, processProjectReferences ? new HashSet() : null);
    }
    
    private void calculateConcreteStateInternal(final MavenProject project, final ProjectBuilderConfiguration config, final boolean processProjectReferences, final Set processedProjects) throws ModelInterpolationException {
        if (processProjectReferences) {
            processedProjects.add(project.getId());
        }
        this.restoreDynamicStateInternal(project, config, processProjectReferences, processProjectReferences ? new HashSet(processedProjects) : null);
        if (!project.isConcrete()) {
            if (project.getParent() != null) {
                this.calculateConcreteStateInternal(project.getParent(), config, processProjectReferences, processedProjects);
            }
            final Build build = project.getBuild();
            if (build != null) {
                this.initResourceMergeIds(build.getResources());
                this.initResourceMergeIds(build.getTestResources());
            }
            final File basedir = project.getBasedir();
            Model model = ModelUtils.cloneModel(project.getModel());
            model = this.modelInterpolator.interpolate(model, project.getBasedir(), config, this.getLogger().isDebugEnabled());
            final List originalInterpolatedCompileSourceRoots = this.interpolateListOfStrings(project.getCompileSourceRoots(), model, project.getBasedir(), config, this.getLogger().isDebugEnabled());
            project.preserveCompileSourceRoots(originalInterpolatedCompileSourceRoots);
            project.setCompileSourceRoots((originalInterpolatedCompileSourceRoots == null) ? null : this.translateListOfPaths(originalInterpolatedCompileSourceRoots, basedir));
            final List originalInterpolatedTestCompileSourceRoots = this.interpolateListOfStrings(project.getTestCompileSourceRoots(), model, project.getBasedir(), config, this.getLogger().isDebugEnabled());
            project.preserveTestCompileSourceRoots(originalInterpolatedTestCompileSourceRoots);
            project.setTestCompileSourceRoots((originalInterpolatedTestCompileSourceRoots == null) ? null : this.translateListOfPaths(originalInterpolatedTestCompileSourceRoots, basedir));
            final List originalInterpolatedScriptSourceRoots = this.interpolateListOfStrings(project.getScriptSourceRoots(), model, project.getBasedir(), config, this.getLogger().isDebugEnabled());
            project.preserveScriptSourceRoots(originalInterpolatedScriptSourceRoots);
            project.setScriptSourceRoots(originalInterpolatedScriptSourceRoots);
            if (basedir != null) {
                this.pathTranslator.alignToBaseDirectory(model, basedir);
            }
            project.preserveBuild(ModelUtils.cloneBuild(model.getBuild()));
            project.preserveProperties();
            project.preserveBasedir();
            project.setBuild(model.getBuild());
            if (project.getExecutionProject() != null) {
                this.calculateConcreteStateInternal(project.getExecutionProject(), config, processProjectReferences, processedProjects);
            }
            project.setConcrete(true);
        }
        if (processProjectReferences) {
            this.calculateConcreteProjectReferences(project, config, processedProjects);
        }
    }
    
    private void initResourceMergeIds(final List resources) {
        if (resources != null) {
            for (final Resource resource : resources) {
                resource.initMergeId();
            }
        }
    }
    
    private void calculateConcreteProjectReferences(final MavenProject project, final ProjectBuilderConfiguration config, final Set processedProjects) throws ModelInterpolationException {
        final Map projectRefs = project.getProjectReferences();
        if (projectRefs != null) {
            for (final MavenProject reference : projectRefs.values()) {
                if (!processedProjects.contains(reference.getId())) {
                    this.calculateConcreteStateInternal(reference, config, true, processedProjects);
                }
            }
        }
    }
    
    private List translateListOfPaths(final List paths, final File basedir) {
        if (paths == null) {
            return null;
        }
        if (basedir == null) {
            return paths;
        }
        final List result = new ArrayList(paths.size());
        for (final String path : paths) {
            final String aligned = this.pathTranslator.alignToBaseDirectory(path, basedir);
            result.add(aligned);
        }
        return result;
    }
    
    public void restoreDynamicState(final MavenProject project, final ProjectBuilderConfiguration config) throws ModelInterpolationException {
        this.restoreDynamicStateInternal(project, config, true, new HashSet());
    }
    
    public void restoreDynamicState(final MavenProject project, final ProjectBuilderConfiguration config, final boolean processProjectReferences) throws ModelInterpolationException {
        this.restoreDynamicStateInternal(project, config, processProjectReferences, processProjectReferences ? new HashSet() : null);
    }
    
    private void restoreDynamicStateInternal(final MavenProject project, final ProjectBuilderConfiguration config, final boolean processProjectReferences, final Set processedProjects) throws ModelInterpolationException {
        if (processProjectReferences) {
            processedProjects.add(project.getId());
        }
        if (project.isConcrete() && this.projectWasChanged(project)) {
            if (project.getParent() != null) {
                this.restoreDynamicStateInternal(project.getParent(), config, processProjectReferences, processedProjects);
            }
            this.restoreBuildRoots(project, config, this.getLogger().isDebugEnabled());
            this.restoreModelBuildSection(project, config, this.getLogger().isDebugEnabled());
            if (project.getExecutionProject() != null) {
                this.restoreDynamicStateInternal(project.getExecutionProject(), config, processProjectReferences, processedProjects);
            }
            project.setConcrete(false);
        }
        if (processProjectReferences) {
            this.restoreDynamicProjectReferences(project, config, processedProjects);
        }
    }
    
    private boolean projectWasChanged(final MavenProject project) {
        if (!this.objectEquals(project.getBasedir(), project.getPreservedBasedir())) {
            return true;
        }
        if (!this.objectEquals(project.getProperties(), project.getPreservedProperties())) {
            return true;
        }
        final Build oBuild = project.getOriginalInterpolatedBuild();
        final Build build = project.getBuild();
        return !this.objectEquals(oBuild.getDirectory(), build.getDirectory()) || !this.objectEquals(oBuild.getOutputDirectory(), build.getOutputDirectory()) || !this.objectEquals(oBuild.getSourceDirectory(), build.getSourceDirectory()) || !this.objectEquals(oBuild.getTestSourceDirectory(), build.getTestSourceDirectory()) || !this.objectEquals(oBuild.getScriptSourceDirectory(), build.getScriptSourceDirectory());
    }
    
    private boolean objectEquals(final Object obj1, final Object obj2) {
        return (obj1 == null) ? (obj2 == null) : (obj2 != null && (obj1 == obj2 || obj1.equals(obj2)));
    }
    
    private void propagateNewPlugins(final MavenProject project) {
        final Build changedBuild = project.getBuild();
        final Build dynamicBuild = project.getDynamicBuild();
        if (changedBuild == null || dynamicBuild == null) {
            return;
        }
        final List changedPlugins = changedBuild.getPlugins();
        final List dynamicPlugins = dynamicBuild.getPlugins();
        if (changedPlugins != null && dynamicPlugins != null && changedPlugins.size() != dynamicPlugins.size()) {
            changedPlugins.removeAll(dynamicPlugins);
            if (!changedPlugins.isEmpty()) {
                for (final Plugin plugin : changedPlugins) {
                    dynamicBuild.addPlugin(plugin);
                }
            }
        }
        dynamicBuild.flushPluginMap();
    }
    
    private void restoreDynamicProjectReferences(final MavenProject project, final ProjectBuilderConfiguration config, final Set processedProjects) throws ModelInterpolationException {
        final Map projectRefs = project.getProjectReferences();
        if (projectRefs != null) {
            for (final MavenProject projectRef : projectRefs.values()) {
                if (!processedProjects.contains(projectRef.getId())) {
                    this.restoreDynamicStateInternal(projectRef, config, true, processedProjects);
                }
            }
        }
    }
    
    private void restoreBuildRoots(final MavenProject project, final ProjectBuilderConfiguration config, final boolean debugMessages) throws ModelInterpolationException {
        project.setCompileSourceRoots(this.restoreListOfStrings(project.getDynamicCompileSourceRoots(), project.getOriginalInterpolatedCompileSourceRoots(), project.getCompileSourceRoots(), project, config, debugMessages));
        project.setTestCompileSourceRoots(this.restoreListOfStrings(project.getDynamicTestCompileSourceRoots(), project.getOriginalInterpolatedTestCompileSourceRoots(), project.getTestCompileSourceRoots(), project, config, debugMessages));
        project.setScriptSourceRoots(this.restoreListOfStrings(project.getDynamicScriptSourceRoots(), project.getOriginalInterpolatedScriptSourceRoots(), project.getScriptSourceRoots(), project, config, debugMessages));
        project.clearRestorableRoots();
    }
    
    private void restoreModelBuildSection(final MavenProject project, final ProjectBuilderConfiguration config, final boolean debugMessages) throws ModelInterpolationException {
        final Build changedBuild = project.getBuild();
        final Build dynamicBuild = project.getDynamicBuild();
        final Build originalInterpolatedBuild = project.getOriginalInterpolatedBuild();
        dynamicBuild.setResources(this.restoreResources(dynamicBuild.getResources(), originalInterpolatedBuild.getResources(), changedBuild.getResources(), project, config, debugMessages));
        dynamicBuild.setTestResources(this.restoreResources(dynamicBuild.getTestResources(), originalInterpolatedBuild.getTestResources(), changedBuild.getTestResources(), project, config, debugMessages));
        dynamicBuild.setFilters(this.restoreListOfStrings(dynamicBuild.getFilters(), originalInterpolatedBuild.getFilters(), changedBuild.getFilters(), project, config, debugMessages));
        dynamicBuild.setFinalName(this.restoreString(dynamicBuild.getFinalName(), originalInterpolatedBuild.getFinalName(), changedBuild.getFinalName(), project, config, debugMessages));
        dynamicBuild.setDefaultGoal(this.restoreString(dynamicBuild.getDefaultGoal(), originalInterpolatedBuild.getDefaultGoal(), changedBuild.getDefaultGoal(), project, config, debugMessages));
        dynamicBuild.setSourceDirectory(this.restoreString(dynamicBuild.getSourceDirectory(), originalInterpolatedBuild.getSourceDirectory(), changedBuild.getSourceDirectory(), project, config, debugMessages));
        dynamicBuild.setTestSourceDirectory(this.restoreString(dynamicBuild.getTestSourceDirectory(), originalInterpolatedBuild.getTestSourceDirectory(), changedBuild.getTestSourceDirectory(), project, config, debugMessages));
        dynamicBuild.setScriptSourceDirectory(this.restoreString(dynamicBuild.getScriptSourceDirectory(), originalInterpolatedBuild.getScriptSourceDirectory(), changedBuild.getScriptSourceDirectory(), project, config, debugMessages));
        dynamicBuild.setOutputDirectory(this.restoreString(dynamicBuild.getOutputDirectory(), originalInterpolatedBuild.getOutputDirectory(), changedBuild.getOutputDirectory(), project, config, debugMessages));
        dynamicBuild.setTestOutputDirectory(this.restoreString(dynamicBuild.getTestOutputDirectory(), originalInterpolatedBuild.getTestOutputDirectory(), changedBuild.getTestOutputDirectory(), project, config, debugMessages));
        dynamicBuild.setDirectory(this.restoreString(dynamicBuild.getDirectory(), originalInterpolatedBuild.getDirectory(), changedBuild.getDirectory(), project, config, debugMessages));
        this.propagateNewPlugins(project);
        project.setBuild(dynamicBuild);
        project.clearRestorableBuild();
    }
    
    private List interpolateListOfStrings(final List originalStrings, final Model model, final File projectDir, final ProjectBuilderConfiguration config, final boolean debugMessages) throws ModelInterpolationException {
        if (originalStrings == null) {
            return null;
        }
        final List result = new ArrayList();
        for (final String original : originalStrings) {
            final String interpolated = this.modelInterpolator.interpolate(original, model, projectDir, config, debugMessages);
            result.add(interpolated);
        }
        return result;
    }
    
    private String restoreString(final String originalString, final String originalInterpolatedString, final String changedString, final MavenProject project, final ProjectBuilderConfiguration config, final boolean debugMessages) throws ModelInterpolationException {
        if (originalString == null) {
            return changedString;
        }
        if (changedString == null) {
            return originalString;
        }
        final Model model = project.getModel();
        String relativeChangedString;
        if (project.getBasedir() != null) {
            relativeChangedString = this.pathTranslator.unalignFromBaseDirectory(changedString, project.getBasedir());
        }
        else {
            relativeChangedString = changedString;
        }
        String interpolatedOriginal = this.modelInterpolator.interpolate(originalString, model, project.getBasedir(), config, debugMessages);
        interpolatedOriginal = this.pathTranslator.unalignFromBaseDirectory(interpolatedOriginal, project.getBasedir());
        String interpolatedOriginal2 = this.modelInterpolator.interpolate(originalInterpolatedString, model, project.getBasedir(), config, debugMessages);
        interpolatedOriginal2 = this.pathTranslator.alignToBaseDirectory(interpolatedOriginal2, project.getBasedir());
        String interpolatedChanged = this.modelInterpolator.interpolate(changedString, model, project.getBasedir(), config, debugMessages);
        interpolatedChanged = this.pathTranslator.alignToBaseDirectory(interpolatedChanged, project.getBasedir());
        final String relativeInterpolatedChanged = this.modelInterpolator.interpolate(relativeChangedString, model, project.getBasedir(), config, debugMessages);
        if (interpolatedOriginal.equals(interpolatedChanged) || interpolatedOriginal2.equals(interpolatedChanged)) {
            return originalString;
        }
        if (interpolatedOriginal.equals(relativeInterpolatedChanged) || interpolatedOriginal2.equals(relativeInterpolatedChanged)) {
            return originalString;
        }
        return relativeChangedString;
    }
    
    private List restoreListOfStrings(final List originalStrings, final List originalInterpolatedStrings, final List changedStrings, final MavenProject project, final ProjectBuilderConfiguration config, final boolean debugMessages) throws ModelInterpolationException {
        if (originalStrings == null) {
            return changedStrings;
        }
        if (changedStrings == null) {
            return originalStrings;
        }
        final List result = new ArrayList();
        final Map orig = new HashMap();
        for (int idx = 0; idx < originalStrings.size(); ++idx) {
            final String[] permutations = { this.pathTranslator.alignToBaseDirectory(originalInterpolatedStrings.get(idx), project.getBasedir()), originalStrings.get(idx) };
            orig.put(permutations[0], permutations);
        }
        for (final String changedString : changedStrings) {
            String relativeChangedString;
            if (project.getBasedir() != null) {
                relativeChangedString = this.pathTranslator.unalignFromBaseDirectory(changedString, project.getBasedir());
            }
            else {
                relativeChangedString = changedString;
            }
            String interpolated = this.modelInterpolator.interpolate(changedString, project.getModel(), project.getBasedir(), config, debugMessages);
            interpolated = this.pathTranslator.alignToBaseDirectory(interpolated, project.getBasedir());
            final String relativeInterpolated = this.modelInterpolator.interpolate(relativeChangedString, project.getModel(), project.getBasedir(), config, debugMessages);
            String[] original = orig.get(interpolated);
            if (original == null) {
                original = orig.get(relativeInterpolated);
            }
            if (original == null) {
                result.add(relativeChangedString);
            }
            else {
                result.add(original[1]);
            }
        }
        return result;
    }
    
    private List restoreResources(final List originalResources, final List originalInterpolatedResources, final List changedResources, final MavenProject project, final ProjectBuilderConfiguration config, final boolean debugMessages) throws ModelInterpolationException {
        if (originalResources == null || changedResources == null) {
            return originalResources;
        }
        final List result = new ArrayList();
        final Map originalResourcesByMergeId = new HashMap();
        for (int idx = 0; idx < originalResources.size(); ++idx) {
            final Resource[] permutations = { originalInterpolatedResources.get(idx), originalResources.get(idx) };
            originalResourcesByMergeId.put(permutations[0].getMergeId(), permutations);
        }
        for (final Resource resource : changedResources) {
            final String mergeId = resource.getMergeId();
            if (mergeId == null || !originalResourcesByMergeId.containsKey(mergeId)) {
                result.add(resource);
            }
            else {
                final Resource originalInterpolatedResource = ((Resource[])originalResourcesByMergeId.get(mergeId))[0];
                final Resource originalResource = ((Resource[])originalResourcesByMergeId.get(mergeId))[1];
                final String dir = this.modelInterpolator.interpolate(resource.getDirectory(), project.getModel(), project.getBasedir(), config, this.getLogger().isDebugEnabled());
                final String oDir = originalInterpolatedResource.getDirectory();
                if (!dir.equals(oDir)) {
                    originalResource.setDirectory(this.pathTranslator.unalignFromBaseDirectory(dir, project.getBasedir()));
                }
                if (resource.getTargetPath() != null) {
                    final String target = this.modelInterpolator.interpolate(resource.getTargetPath(), project.getModel(), project.getBasedir(), config, this.getLogger().isDebugEnabled());
                    final String oTarget = originalInterpolatedResource.getTargetPath();
                    if (!target.equals(oTarget)) {
                        originalResource.setTargetPath(this.pathTranslator.unalignFromBaseDirectory(target, project.getBasedir()));
                    }
                }
                originalResource.setFiltering(resource.isFiltering());
                originalResource.setExcludes(this.collectRestoredListOfPatterns(resource.getExcludes(), originalResource.getExcludes(), originalInterpolatedResource.getExcludes()));
                originalResource.setIncludes(this.collectRestoredListOfPatterns(resource.getIncludes(), originalResource.getIncludes(), originalInterpolatedResource.getIncludes()));
                result.add(originalResource);
            }
        }
        return result;
    }
    
    private List collectRestoredListOfPatterns(final List patterns, final List originalPatterns, final List originalInterpolatedPatterns) {
        final LinkedHashSet collectedPatterns = new LinkedHashSet();
        collectedPatterns.addAll(originalPatterns);
        for (final String pattern : patterns) {
            if (!originalInterpolatedPatterns.contains(pattern)) {
                collectedPatterns.add(pattern);
            }
        }
        return collectedPatterns.isEmpty() ? Collections.EMPTY_LIST : new ArrayList(collectedPatterns);
    }
}
