// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.maven.project.validation;

import org.apache.maven.model.Repository;
import org.apache.maven.model.Reporting;
import org.apache.maven.model.Build;
import org.apache.maven.model.DependencyManagement;
import java.util.Iterator;
import org.apache.maven.model.Parent;
import java.util.List;
import org.apache.maven.model.ReportPlugin;
import org.apache.maven.model.Resource;
import org.apache.maven.model.Plugin;
import java.io.File;
import org.codehaus.plexus.util.StringUtils;
import org.apache.maven.model.Dependency;
import org.apache.maven.model.Model;

public class DefaultModelValidator implements ModelValidator
{
    private static final String ID_REGEX = "[A-Za-z0-9_\\-.]+";
    
    public ModelValidationResult validate(final Model model) {
        final ModelValidationResult result = new ModelValidationResult();
        this.validateStringNotEmpty("modelVersion", result, model.getModelVersion());
        this.validateId("groupId", result, model.getGroupId());
        this.validateId("artifactId", result, model.getArtifactId());
        this.validateStringNotEmpty("packaging", result, model.getPackaging());
        if (!model.getModules().isEmpty() && !"pom".equals(model.getPackaging())) {
            result.addMessage("Packaging '" + model.getPackaging() + "' is invalid. Aggregator projects " + "require 'pom' as packaging.");
        }
        final Parent parent = model.getParent();
        if (parent != null && parent.getGroupId().equals(model.getGroupId()) && parent.getArtifactId().equals(model.getArtifactId())) {
            result.addMessage("The parent element cannot have the same ID as the project.");
        }
        this.validateStringNotEmpty("version", result, model.getVersion());
        for (final Dependency d : model.getDependencies()) {
            this.validateId("dependencies.dependency.artifactId", result, d.getArtifactId());
            this.validateId("dependencies.dependency.groupId", result, d.getGroupId());
            this.validateStringNotEmpty("dependencies.dependency.type", result, d.getType(), d.getManagementKey());
            this.validateStringNotEmpty("dependencies.dependency.version", result, d.getVersion(), d.getManagementKey());
            if ("system".equals(d.getScope())) {
                final String systemPath = d.getSystemPath();
                if (StringUtils.isEmpty(systemPath)) {
                    result.addMessage("For dependency " + d + ": system-scoped dependency must specify systemPath.");
                }
                else {
                    if (new File(systemPath).isAbsolute()) {
                        continue;
                    }
                    result.addMessage("For dependency " + d + ": system-scoped dependency must " + "specify an absolute path systemPath.");
                }
            }
            else {
                if (!StringUtils.isNotEmpty(d.getSystemPath())) {
                    continue;
                }
                result.addMessage("For dependency " + d + ": only dependency with system scope can specify systemPath.");
            }
        }
        final DependencyManagement mgmt = model.getDependencyManagement();
        if (mgmt != null) {
            for (final Dependency d2 : mgmt.getDependencies()) {
                this.validateSubElementStringNotEmpty(d2, "dependencyManagement.dependencies.dependency.artifactId", result, d2.getArtifactId());
                this.validateSubElementStringNotEmpty(d2, "dependencyManagement.dependencies.dependency.groupId", result, d2.getGroupId());
                if ("system".equals(d2.getScope())) {
                    final String systemPath2 = d2.getSystemPath();
                    if (StringUtils.isEmpty(systemPath2)) {
                        result.addMessage("For managed dependency " + d2 + ": system-scoped dependency must specify systemPath.");
                    }
                    else {
                        if (new File(systemPath2).isAbsolute()) {
                            continue;
                        }
                        result.addMessage("For managed dependency " + d2 + ": system-scoped dependency must " + "specify an absolute path systemPath.");
                    }
                }
                else if (StringUtils.isNotEmpty(d2.getSystemPath())) {
                    result.addMessage("For managed dependency " + d2 + ": only dependency with system scope can specify systemPath.");
                }
                else {
                    if (!"import".equals(d2.getScope())) {
                        continue;
                    }
                    if (!"pom".equals(d2.getType())) {
                        result.addMessage("For managed dependency " + d2 + ": dependencies with import scope must have type 'pom'.");
                    }
                    else {
                        if (d2.getClassifier() == null) {
                            continue;
                        }
                        result.addMessage("For managed dependency " + d2 + ": dependencies with import scope must NOT have a classifier.");
                    }
                }
            }
        }
        final Build build = model.getBuild();
        if (build != null) {
            for (final Plugin p : build.getPlugins()) {
                this.validateStringNotEmpty("build.plugins.plugin.artifactId", result, p.getArtifactId());
                this.validateStringNotEmpty("build.plugins.plugin.groupId", result, p.getGroupId());
            }
            for (final Resource r : build.getResources()) {
                this.validateStringNotEmpty("build.resources.resource.directory", result, r.getDirectory());
            }
            for (final Resource r : build.getTestResources()) {
                this.validateStringNotEmpty("build.testResources.testResource.directory", result, r.getDirectory());
            }
        }
        final Reporting reporting = model.getReporting();
        if (reporting != null) {
            for (final ReportPlugin p2 : reporting.getPlugins()) {
                this.validateStringNotEmpty("reporting.plugins.plugin.artifactId", result, p2.getArtifactId());
                this.validateStringNotEmpty("reporting.plugins.plugin.groupId", result, p2.getGroupId());
            }
        }
        this.validateRepositories(result, model.getRepositories(), "repositories.repository");
        this.validateRepositories(result, model.getPluginRepositories(), "pluginRepositories.pluginRepository");
        this.forcePluginExecutionIdCollision(model, result);
        return result;
    }
    
    private boolean validateId(final String fieldName, final ModelValidationResult result, final String id) {
        if (!this.validateStringNotEmpty(fieldName, result, id)) {
            return false;
        }
        final boolean match = id.matches("[A-Za-z0-9_\\-.]+");
        if (!match) {
            result.addMessage("'" + fieldName + "' with value '" + id + "' does not match a valid id pattern.");
        }
        return match;
    }
    
    private void validateRepositories(final ModelValidationResult result, final List repositories, final String prefix) {
        for (final Repository repository : repositories) {
            this.validateStringNotEmpty(prefix + ".id", result, repository.getId());
            this.validateStringNotEmpty(prefix + ".url", result, repository.getUrl());
        }
    }
    
    private void forcePluginExecutionIdCollision(final Model model, final ModelValidationResult result) {
        final Build build = model.getBuild();
        if (build != null) {
            final List plugins = build.getPlugins();
            if (plugins != null) {
                for (final Plugin plugin : plugins) {
                    try {
                        plugin.getExecutionsAsMap();
                    }
                    catch (IllegalStateException collisionException) {
                        result.addMessage(collisionException.getMessage());
                    }
                }
            }
        }
    }
    
    private boolean validateStringNotEmpty(final String fieldName, final ModelValidationResult result, final String string) {
        return this.validateStringNotEmpty(fieldName, result, string, null);
    }
    
    private boolean validateStringNotEmpty(final String fieldName, final ModelValidationResult result, final String string, final String sourceHint) {
        if (!this.validateNotNull(fieldName, result, string, sourceHint)) {
            return false;
        }
        if (string.length() > 0) {
            return true;
        }
        if (sourceHint != null) {
            result.addMessage("'" + fieldName + "' is missing for " + sourceHint);
        }
        else {
            result.addMessage("'" + fieldName + "' is missing.");
        }
        return false;
    }
    
    private boolean validateSubElementStringNotEmpty(final Object subElementInstance, final String fieldName, final ModelValidationResult result, final String string) {
        if (!this.validateSubElementNotNull(subElementInstance, fieldName, result, string)) {
            return false;
        }
        if (string.length() > 0) {
            return true;
        }
        result.addMessage("In " + subElementInstance + ":\n\n       -> '" + fieldName + "' is missing.");
        return false;
    }
    
    private boolean validateNotNull(final String fieldName, final ModelValidationResult result, final Object object, final String sourceHint) {
        if (object != null) {
            return true;
        }
        if (sourceHint != null) {
            result.addMessage("'" + fieldName + "' is missing for " + sourceHint);
        }
        else {
            result.addMessage("'" + fieldName + "' is missing.");
        }
        return false;
    }
    
    private boolean validateSubElementNotNull(final Object subElementInstance, final String fieldName, final ModelValidationResult result, final Object object) {
        if (object != null) {
            return true;
        }
        result.addMessage("In " + subElementInstance + ":\n\n       -> '" + fieldName + "' is missing.");
        return false;
    }
}
