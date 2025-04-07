// 
// Decompiled by Procyon v0.5.36
// 

package org.codehaus.plexus.util;

import java.io.File;

public abstract class AbstractScanner implements Scanner
{
    public static final String[] DEFAULTEXCLUDES;
    protected String[] includes;
    protected String[] excludes;
    protected boolean isCaseSensitive;
    
    public AbstractScanner() {
        this.isCaseSensitive = true;
    }
    
    public void setCaseSensitive(final boolean isCaseSensitive) {
        this.isCaseSensitive = isCaseSensitive;
    }
    
    protected static boolean matchPatternStart(final String pattern, final String str) {
        return SelectorUtils.matchPatternStart(pattern, str);
    }
    
    protected static boolean matchPatternStart(final String pattern, final String str, final boolean isCaseSensitive) {
        return SelectorUtils.matchPatternStart(pattern, str, isCaseSensitive);
    }
    
    protected static boolean matchPath(final String pattern, final String str) {
        return SelectorUtils.matchPath(pattern, str);
    }
    
    protected static boolean matchPath(final String pattern, final String str, final boolean isCaseSensitive) {
        return SelectorUtils.matchPath(pattern, str, isCaseSensitive);
    }
    
    public static boolean match(final String pattern, final String str) {
        return SelectorUtils.match(pattern, str);
    }
    
    protected static boolean match(final String pattern, final String str, final boolean isCaseSensitive) {
        return SelectorUtils.match(pattern, str, isCaseSensitive);
    }
    
    public void setIncludes(final String[] includes) {
        if (includes == null) {
            this.includes = null;
        }
        else {
            this.includes = new String[includes.length];
            for (int i = 0; i < includes.length; ++i) {
                String pattern = includes[i].trim();
                if (pattern.endsWith(File.separator)) {
                    pattern += "**";
                }
                this.includes[i] = pattern;
            }
        }
    }
    
    public void setExcludes(final String[] excludes) {
        if (excludes == null) {
            this.excludes = null;
        }
        else {
            this.excludes = new String[excludes.length];
            for (int i = 0; i < excludes.length; ++i) {
                String pattern = excludes[i].trim();
                if (pattern.endsWith(File.separator)) {
                    pattern += "**";
                }
                this.excludes[i] = pattern;
            }
        }
    }
    
    protected boolean isIncluded(final String name) {
        for (int i = 0; i < this.includes.length; ++i) {
            if (matchPath(this.includes[i], name, this.isCaseSensitive)) {
                return true;
            }
        }
        return false;
    }
    
    protected boolean couldHoldIncluded(final String name) {
        for (int i = 0; i < this.includes.length; ++i) {
            if (matchPatternStart(this.includes[i], name, this.isCaseSensitive)) {
                return true;
            }
        }
        return false;
    }
    
    protected boolean isExcluded(final String name) {
        for (int i = 0; i < this.excludes.length; ++i) {
            if (matchPath(this.excludes[i], name, this.isCaseSensitive)) {
                return true;
            }
        }
        return false;
    }
    
    public void addDefaultExcludes() {
        final int excludesLength = (this.excludes == null) ? 0 : this.excludes.length;
        final String[] newExcludes = new String[excludesLength + AbstractScanner.DEFAULTEXCLUDES.length];
        if (excludesLength > 0) {
            System.arraycopy(this.excludes, 0, newExcludes, 0, excludesLength);
        }
        for (int i = 0; i < AbstractScanner.DEFAULTEXCLUDES.length; ++i) {
            newExcludes[i + excludesLength] = AbstractScanner.DEFAULTEXCLUDES[i];
        }
        this.excludes = newExcludes;
    }
    
    protected void setupDefaultFilters() {
        if (this.includes == null) {
            (this.includes = new String[1])[0] = "**";
        }
        if (this.excludes == null) {
            this.excludes = new String[0];
        }
    }
    
    static {
        DEFAULTEXCLUDES = new String[] { "**/*~", "**/#*#", "**/.#*", "**/%*%", "**/._*", "**/CVS", "**/CVS/**", "**/.cvsignore", "**/RCS", "**/RCS/**", "**/SCCS", "**/SCCS/**", "**/vssver.scc", "**/.svn", "**/.svn/**", "**/.arch-ids", "**/.arch-ids/**", "**/.bzr", "**/.bzr/**", "**/.MySCMServerInfo", "**/.DS_Store", "**/.metadata", "**/.metadata/**", "**/.hg", "**/.hg/**", "**/.git", "**/.git/**", "**/BitKeeper", "**/BitKeeper/**", "**/ChangeSet", "**/ChangeSet/**", "**/_darcs", "**/_darcs/**", "**/.darcsrepo", "**/.darcsrepo/**", "**/-darcs-backup*", "**/.darcs-temp-mail" };
    }
}
