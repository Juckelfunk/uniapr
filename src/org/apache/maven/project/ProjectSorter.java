// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.maven.project;

import java.io.File;
import org.codehaus.plexus.util.dag.Vertex;
import java.util.Set;
import java.util.Collection;
import java.util.HashSet;
import java.util.Arrays;
import java.util.Iterator;
import java.util.Collections;
import org.codehaus.plexus.util.dag.TopologicalSorter;
import java.util.ArrayList;
import org.apache.maven.model.Extension;
import org.apache.maven.model.ReportPlugin;
import org.apache.maven.model.Plugin;
import org.apache.maven.model.Dependency;
import org.apache.maven.artifact.ArtifactUtils;
import java.util.HashMap;
import org.codehaus.plexus.util.dag.CycleDetectedException;
import java.util.List;
import java.util.Map;
import org.codehaus.plexus.util.dag.DAG;

public class ProjectSorter
{
    private final DAG dag;
    private final Map projectMap;
    private final List<MavenProject> sortedProjects;
    private MavenProject topLevelProject;
    
    public ProjectSorter(final List projects) throws CycleDetectedException, DuplicateProjectException, MissingProjectException {
        this(projects, null, null, false, false);
    }
    
    public ProjectSorter(final List projects, final List selectedProjectNames, final String resumeFrom, final boolean make, final boolean makeDependents) throws CycleDetectedException, DuplicateProjectException, MissingProjectException {
        this.dag = new DAG();
        this.projectMap = new HashMap();
        for (final MavenProject project : projects) {
            final String id = ArtifactUtils.versionlessKey(project.getGroupId(), project.getArtifactId());
            if (this.dag.getVertex(id) != null) {
                throw new DuplicateProjectException("Project '" + id + "' is duplicated in the reactor");
            }
            this.dag.addVertex(id);
            this.projectMap.put(id, project);
        }
        for (final MavenProject project : projects) {
            final String id = ArtifactUtils.versionlessKey(project.getGroupId(), project.getArtifactId());
            for (final Dependency dependency : project.getDependencies()) {
                final String dependencyId = ArtifactUtils.versionlessKey(dependency.getGroupId(), dependency.getArtifactId());
                if (this.dag.getVertex(dependencyId) != null) {
                    project.addProjectReference(this.projectMap.get(dependencyId));
                    this.dag.addEdge(id, dependencyId);
                }
            }
            final MavenProject parent = project.getParent();
            if (parent != null) {
                final String parentId = ArtifactUtils.versionlessKey(parent.getGroupId(), parent.getArtifactId());
                if (this.dag.getVertex(parentId) != null) {
                    if (this.dag.hasEdge(parentId, id)) {
                        this.dag.removeEdge(parentId, id);
                    }
                    this.dag.addEdge(id, parentId);
                }
            }
            final List buildPlugins = project.getBuildPlugins();
            if (buildPlugins != null) {
                for (final Plugin plugin : buildPlugins) {
                    final String pluginId = ArtifactUtils.versionlessKey(plugin.getGroupId(), plugin.getArtifactId());
                    if (this.dag.getVertex(pluginId) != null && !pluginId.equals(id)) {
                        this.addEdgeWithParentCheck(this.projectMap, pluginId, project, id);
                    }
                    if (!pluginId.equals(id)) {
                        for (final Dependency dependency2 : plugin.getDependencies()) {
                            final String dependencyId2 = ArtifactUtils.versionlessKey(dependency2.getGroupId(), dependency2.getArtifactId());
                            if (this.dag.getVertex(dependencyId2) != null && !id.equals(dependencyId2)) {
                                project.addProjectReference(this.projectMap.get(dependencyId2));
                                this.addEdgeWithParentCheck(this.projectMap, dependencyId2, project, id);
                            }
                        }
                    }
                }
            }
            final List reportPlugins = project.getReportPlugins();
            if (reportPlugins != null) {
                for (final ReportPlugin plugin2 : reportPlugins) {
                    final String pluginId2 = ArtifactUtils.versionlessKey(plugin2.getGroupId(), plugin2.getArtifactId());
                    if (this.dag.getVertex(pluginId2) != null && !pluginId2.equals(id)) {
                        this.addEdgeWithParentCheck(this.projectMap, pluginId2, project, id);
                    }
                }
            }
            for (final Extension extension : project.getBuildExtensions()) {
                final String extensionId = ArtifactUtils.versionlessKey(extension.getGroupId(), extension.getArtifactId());
                if (this.dag.getVertex(extensionId) != null) {
                    this.addEdgeWithParentCheck(this.projectMap, extensionId, project, id);
                }
            }
        }
        List sortedProjects = new ArrayList();
        Iterator i2 = TopologicalSorter.sort(this.dag).iterator();
        while (i2.hasNext()) {
            final String id = i2.next();
            sortedProjects.add(this.projectMap.get(id));
        }
        i2 = sortedProjects.iterator();
        while (i2.hasNext() && this.topLevelProject == null) {
            final MavenProject project2 = i2.next();
            if (project2.isExecutionRoot()) {
                this.topLevelProject = project2;
            }
        }
        sortedProjects = applyMakeFilter(sortedProjects, this.dag, this.projectMap, this.topLevelProject, selectedProjectNames, make, makeDependents);
        resumeFrom(resumeFrom, sortedProjects, this.projectMap, this.topLevelProject);
        this.sortedProjects = Collections.unmodifiableList((List<? extends MavenProject>)sortedProjects);
    }
    
    private static List applyMakeFilter(final List sortedProjects, final DAG dag, final Map projectMap, final MavenProject topLevelProject, final List selectedProjectNames, final boolean make, final boolean makeDependents) throws MissingProjectException {
        if (selectedProjectNames == null) {
            return sortedProjects;
        }
        final MavenProject[] selectedProjects = new MavenProject[selectedProjectNames.size()];
        for (int i = 0; i < selectedProjects.length; ++i) {
            selectedProjects[i] = findProject(selectedProjectNames.get(i), projectMap, topLevelProject);
        }
        final Set projectsToMake = new HashSet(Arrays.asList(selectedProjects));
        for (int j = 0; j < selectedProjects.length; ++j) {
            final MavenProject project = selectedProjects[j];
            final String id = ArtifactUtils.versionlessKey(project.getGroupId(), project.getArtifactId());
            final Vertex v = dag.getVertex(id);
            if (make) {
                gatherDescendents(v, projectMap, projectsToMake, new HashSet());
            }
            if (makeDependents) {
                gatherAncestors(v, projectMap, projectsToMake, new HashSet());
            }
        }
        final Iterator k = sortedProjects.iterator();
        while (k.hasNext()) {
            final MavenProject project = k.next();
            if (!projectsToMake.contains(project)) {
                k.remove();
            }
        }
        return sortedProjects;
    }
    
    private static void resumeFrom(final String resumeFrom, final List sortedProjects, final Map projectMap, final MavenProject topLevelProject) throws MissingProjectException {
        if (resumeFrom == null) {
            return;
        }
        final MavenProject resumeFromProject = findProject(resumeFrom, projectMap, topLevelProject);
        final Iterator i = sortedProjects.iterator();
        while (i.hasNext()) {
            final MavenProject project = i.next();
            if (resumeFromProject.equals(project)) {
                break;
            }
            i.remove();
        }
        if (sortedProjects.isEmpty()) {
            throw new MissingProjectException("Couldn't resume, project was not scheduled to run: " + resumeFrom);
        }
    }
    
    private static MavenProject findProject(final String projectName, final Map projectMap, final MavenProject topLevelProject) throws MissingProjectException {
        MavenProject project = projectMap.get(projectName);
        if (project != null) {
            return project;
        }
        File baseDir;
        if (topLevelProject == null) {
            baseDir = new File(System.getProperty("user.dir"));
        }
        else {
            baseDir = topLevelProject.getBasedir();
        }
        final File projectDir = new File(baseDir, projectName);
        if (!projectDir.exists()) {
            throw new MissingProjectException("Couldn't find specified project dir: " + projectDir.getAbsolutePath());
        }
        if (!projectDir.isDirectory()) {
            throw new MissingProjectException("Couldn't find specified project dir (not a directory): " + projectDir.getAbsolutePath());
        }
        final Iterator i = projectMap.values().iterator();
        while (i.hasNext()) {
            project = i.next();
            if (projectDir.equals(project.getFile().getParentFile())) {
                return project;
            }
        }
        throw new MissingProjectException("Couldn't find specified project in module list: " + projectDir.getAbsolutePath());
    }
    
    private static void gatherDescendents(final Vertex v, final Map projectMap, final Set out, final Set visited) {
        if (visited.contains(v)) {
            return;
        }
        visited.add(v);
        out.add(projectMap.get(v.getLabel()));
        for (final Vertex child : v.getChildren()) {
            gatherDescendents(child, projectMap, out, visited);
        }
    }
    
    private static void gatherAncestors(final Vertex v, final Map projectMap, final Set out, final Set visited) {
        if (visited.contains(v)) {
            return;
        }
        visited.add(v);
        out.add(projectMap.get(v.getLabel()));
        for (final Vertex parent : v.getParents()) {
            gatherAncestors(parent, projectMap, out, visited);
        }
    }
    
    private void addEdgeWithParentCheck(final Map projectMap, final String projectRefId, final MavenProject project, final String id) throws CycleDetectedException {
        final MavenProject extProject = projectMap.get(projectRefId);
        if (extProject == null) {
            return;
        }
        project.addProjectReference(extProject);
        final MavenProject extParent = extProject.getParent();
        if (extParent != null) {
            final String parentId = ArtifactUtils.versionlessKey(extParent.getGroupId(), extParent.getArtifactId());
            if (!this.dag.hasEdge(projectRefId, id) || !parentId.equals(id)) {
                this.dag.addEdge(id, projectRefId);
            }
        }
    }
    
    public MavenProject getTopLevelProject() {
        return this.topLevelProject;
    }
    
    public List<MavenProject> getSortedProjects() {
        return this.sortedProjects;
    }
    
    public boolean hasMultipleProjects() {
        return this.sortedProjects.size() > 1;
    }
    
    public List getDependents(final String id) {
        return this.dag.getParentLabels(id);
    }
    
    public DAG getDAG() {
        return this.dag;
    }
    
    public Map getProjectMap() {
        return this.projectMap;
    }
}
