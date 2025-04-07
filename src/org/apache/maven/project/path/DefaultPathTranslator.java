// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.maven.project.path;

import org.apache.maven.model.Reporting;
import java.util.Iterator;
import org.apache.maven.model.Build;
import java.util.List;
import java.util.ArrayList;
import org.apache.maven.model.Resource;
import java.io.File;
import org.apache.maven.model.Model;

public class DefaultPathTranslator implements PathTranslator
{
    private static final String[] BASEDIR_EXPRESSIONS;
    
    public void alignToBaseDirectory(final Model model, final File basedir) {
        if (basedir == null) {
            return;
        }
        final Build build = model.getBuild();
        if (build != null) {
            build.setDirectory(this.alignToBaseDirectory(build.getDirectory(), basedir));
            build.setSourceDirectory(this.alignToBaseDirectory(build.getSourceDirectory(), basedir));
            build.setTestSourceDirectory(this.alignToBaseDirectory(build.getTestSourceDirectory(), basedir));
            for (final Resource resource : build.getResources()) {
                resource.setDirectory(this.alignToBaseDirectory(resource.getDirectory(), basedir));
            }
            for (final Resource resource : build.getTestResources()) {
                resource.setDirectory(this.alignToBaseDirectory(resource.getDirectory(), basedir));
            }
            if (build.getFilters() != null) {
                final List filters = new ArrayList();
                for (final String filter : build.getFilters()) {
                    filters.add(this.alignToBaseDirectory(filter, basedir));
                }
                build.setFilters(filters);
            }
            build.setOutputDirectory(this.alignToBaseDirectory(build.getOutputDirectory(), basedir));
            build.setTestOutputDirectory(this.alignToBaseDirectory(build.getTestOutputDirectory(), basedir));
        }
        final Reporting reporting = model.getReporting();
        if (reporting != null) {
            reporting.setOutputDirectory(this.alignToBaseDirectory(reporting.getOutputDirectory(), basedir));
        }
    }
    
    public String alignToBaseDirectory(final String path, final File basedir) {
        if (basedir == null) {
            return path;
        }
        if (path == null) {
            return null;
        }
        String s = this.stripBasedirToken(path);
        final File file = new File(s);
        if (file.isAbsolute()) {
            s = file.getPath();
        }
        else if (file.getPath().startsWith(File.separator)) {
            s = file.getAbsolutePath();
        }
        else {
            s = new File(new File(basedir, s).toURI().normalize()).getAbsolutePath();
        }
        return s;
    }
    
    private String stripBasedirToken(String s) {
        if (s != null) {
            String basedirExpr = null;
            for (int i = 0; i < DefaultPathTranslator.BASEDIR_EXPRESSIONS.length; ++i) {
                basedirExpr = DefaultPathTranslator.BASEDIR_EXPRESSIONS[i];
                if (s.startsWith(basedirExpr)) {
                    break;
                }
                basedirExpr = null;
            }
            if (basedirExpr != null) {
                if (s.length() > basedirExpr.length()) {
                    s = this.chopLeadingFileSeparator(s.substring(basedirExpr.length()));
                }
                else {
                    s = ".";
                }
            }
        }
        return s;
    }
    
    private String chopLeadingFileSeparator(String path) {
        if (path != null && (path.startsWith("/") || path.startsWith("\\"))) {
            path = path.substring(1);
        }
        return path;
    }
    
    public void unalignFromBaseDirectory(final Model model, final File basedir) {
        if (basedir == null) {
            return;
        }
        final Build build = model.getBuild();
        if (build != null) {
            build.setDirectory(this.unalignFromBaseDirectory(build.getDirectory(), basedir));
            build.setSourceDirectory(this.unalignFromBaseDirectory(build.getSourceDirectory(), basedir));
            build.setTestSourceDirectory(this.unalignFromBaseDirectory(build.getTestSourceDirectory(), basedir));
            build.setScriptSourceDirectory(this.unalignFromBaseDirectory(build.getScriptSourceDirectory(), basedir));
            for (final Resource resource : build.getResources()) {
                resource.setDirectory(this.unalignFromBaseDirectory(resource.getDirectory(), basedir));
            }
            for (final Resource resource : build.getTestResources()) {
                resource.setDirectory(this.unalignFromBaseDirectory(resource.getDirectory(), basedir));
            }
            if (build.getFilters() != null) {
                final List filters = new ArrayList();
                for (final String filter : build.getFilters()) {
                    filters.add(this.unalignFromBaseDirectory(filter, basedir));
                }
                build.setFilters(filters);
            }
            build.setOutputDirectory(this.unalignFromBaseDirectory(build.getOutputDirectory(), basedir));
            build.setTestOutputDirectory(this.unalignFromBaseDirectory(build.getTestOutputDirectory(), basedir));
        }
        final Reporting reporting = model.getReporting();
        if (reporting != null) {
            reporting.setOutputDirectory(this.unalignFromBaseDirectory(reporting.getOutputDirectory(), basedir));
        }
    }
    
    public String unalignFromBaseDirectory(String path, final File basedir) {
        if (basedir == null) {
            return path;
        }
        if (path == null) {
            return null;
        }
        path = path.trim();
        final String base = basedir.getAbsolutePath();
        if (path.startsWith(base)) {
            path = this.chopLeadingFileSeparator(path.substring(base.length()));
        }
        if (!new File(path).isAbsolute()) {
            path = path.replace('\\', '/');
        }
        return path;
    }
    
    static {
        BASEDIR_EXPRESSIONS = new String[] { "${basedir}", "${pom.basedir}", "${project.basedir}" };
    }
}
