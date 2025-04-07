// 
// Decompiled by Procyon v0.5.36
// 

package org.codehaus.plexus.util;

import java.util.Iterator;
import java.util.ArrayList;
import java.util.List;
import java.util.Stack;
import java.io.File;

public class DirectoryWalker
{
    private File baseDir;
    private int baseDirOffset;
    private Stack dirStack;
    private List excludes;
    private List includes;
    private boolean isCaseSensitive;
    private List listeners;
    private boolean debugEnabled;
    
    public DirectoryWalker() {
        this.isCaseSensitive = true;
        this.debugEnabled = false;
        this.includes = new ArrayList();
        this.excludes = new ArrayList();
        this.listeners = new ArrayList();
    }
    
    public void addDirectoryWalkListener(final DirectoryWalkListener listener) {
        this.listeners.add(listener);
    }
    
    public void addExclude(final String exclude) {
        this.excludes.add(this.fixPattern(exclude));
    }
    
    public void addInclude(final String include) {
        this.includes.add(this.fixPattern(include));
    }
    
    public void addSCMExcludes() {
        final String[] scmexcludes = DirectoryScanner.DEFAULTEXCLUDES;
        for (int i = 0; i < scmexcludes.length; ++i) {
            this.addExclude(scmexcludes[i]);
        }
    }
    
    private void fireStep(final File file) {
        final DirStackEntry dsEntry = this.dirStack.peek();
        final int percentage = dsEntry.getPercentage();
        for (final DirectoryWalkListener listener : this.listeners) {
            listener.directoryWalkStep(percentage, file);
        }
    }
    
    private void fireWalkFinished() {
        for (final DirectoryWalkListener listener : this.listeners) {
            listener.directoryWalkFinished();
        }
    }
    
    private void fireWalkStarting() {
        for (final DirectoryWalkListener listener : this.listeners) {
            listener.directoryWalkStarting(this.baseDir);
        }
    }
    
    private void fireDebugMessage(final String message) {
        for (final DirectoryWalkListener listener : this.listeners) {
            listener.debug(message);
        }
    }
    
    private String fixPattern(final String pattern) {
        String cleanPattern = pattern;
        if (File.separatorChar != '/') {
            cleanPattern = cleanPattern.replace('/', File.separatorChar);
        }
        if (File.separatorChar != '\\') {
            cleanPattern = cleanPattern.replace('\\', File.separatorChar);
        }
        return cleanPattern;
    }
    
    public void setDebugMode(final boolean debugEnabled) {
        this.debugEnabled = debugEnabled;
    }
    
    public File getBaseDir() {
        return this.baseDir;
    }
    
    public List getExcludes() {
        return this.excludes;
    }
    
    public List getIncludes() {
        return this.includes;
    }
    
    private boolean isExcluded(final String name) {
        return this.isMatch(this.excludes, name);
    }
    
    private boolean isIncluded(final String name) {
        return this.isMatch(this.includes, name);
    }
    
    private boolean isMatch(final List patterns, final String name) {
        for (final String pattern : patterns) {
            if (SelectorUtils.matchPath(pattern, name, this.isCaseSensitive)) {
                return true;
            }
        }
        return false;
    }
    
    private String relativeToBaseDir(final File file) {
        return file.getAbsolutePath().substring(this.baseDirOffset + 1);
    }
    
    public void removeDirectoryWalkListener(final DirectoryWalkListener listener) {
        this.listeners.remove(listener);
    }
    
    public void scan() {
        if (this.baseDir == null) {
            throw new IllegalStateException("Scan Failure.  BaseDir not specified.");
        }
        if (!this.baseDir.exists()) {
            throw new IllegalStateException("Scan Failure.  BaseDir does not exist.");
        }
        if (!this.baseDir.isDirectory()) {
            throw new IllegalStateException("Scan Failure.  BaseDir is not a directory.");
        }
        if (this.includes.isEmpty()) {
            this.addInclude("**");
        }
        if (this.debugEnabled) {
            final StringBuffer dbg = new StringBuffer();
            dbg.append("DirectoryWalker Scan");
            dbg.append("\n  Base Dir: ").append(this.baseDir.getAbsolutePath());
            dbg.append("\n  Includes: ");
            for (final String include : this.includes) {
                dbg.append("\n    - \"").append(include).append("\"");
            }
            dbg.append("\n  Excludes: ");
            for (final String exclude : this.excludes) {
                dbg.append("\n    - \"").append(exclude).append("\"");
            }
            this.fireDebugMessage(dbg.toString());
        }
        this.fireWalkStarting();
        this.dirStack = new Stack();
        this.scanDir(this.baseDir);
        this.fireWalkFinished();
    }
    
    private void scanDir(final File dir) {
        final File[] files = dir.listFiles();
        if (files == null) {
            return;
        }
        final DirStackEntry curStackEntry = new DirStackEntry(dir, files.length);
        if (this.dirStack.isEmpty()) {
            curStackEntry.percentageOffset = 0.0;
            curStackEntry.percentageSize = 100.0;
        }
        else {
            final DirStackEntry previousStackEntry = this.dirStack.peek();
            curStackEntry.percentageOffset = previousStackEntry.getNextPercentageOffset();
            curStackEntry.percentageSize = previousStackEntry.getNextPercentageSize();
        }
        this.dirStack.push(curStackEntry);
        for (int idx = 0; idx < files.length; ++idx) {
            curStackEntry.index = idx;
            final String name = this.relativeToBaseDir(files[idx]);
            if (this.isExcluded(name)) {
                this.fireDebugMessage(name + " is excluded.");
            }
            else if (files[idx].isDirectory()) {
                this.scanDir(files[idx]);
            }
            else if (this.isIncluded(name)) {
                this.fireStep(files[idx]);
            }
        }
        this.dirStack.pop();
    }
    
    public void setBaseDir(final File baseDir) {
        this.baseDir = baseDir;
        this.baseDirOffset = baseDir.getAbsolutePath().length();
    }
    
    public void setExcludes(final List entries) {
        this.excludes.clear();
        if (entries != null) {
            for (final String pattern : entries) {
                this.excludes.add(this.fixPattern(pattern));
            }
        }
    }
    
    public void setIncludes(final List entries) {
        this.includes.clear();
        if (entries != null) {
            for (final String pattern : entries) {
                this.includes.add(this.fixPattern(pattern));
            }
        }
    }
    
    class DirStackEntry
    {
        public int count;
        public File dir;
        public int index;
        public double percentageOffset;
        public double percentageSize;
        
        public DirStackEntry(final File d, final int length) {
            this.dir = d;
            this.count = length;
        }
        
        public double getNextPercentageOffset() {
            return this.percentageOffset + this.index * (this.percentageSize / this.count);
        }
        
        public double getNextPercentageSize() {
            return this.percentageSize / this.count;
        }
        
        public int getPercentage() {
            final double percentageWithinDir = this.index / (double)this.count;
            return (int)Math.floor(this.percentageOffset + percentageWithinDir * this.percentageSize);
        }
        
        public String toString() {
            return "DirStackEntry[dir=" + this.dir.getAbsolutePath() + ",count=" + this.count + ",index=" + this.index + ",percentageOffset=" + this.percentageOffset + ",percentageSize=" + this.percentageSize + ",percentage()=" + this.getPercentage() + ",getNextPercentageOffset()=" + this.getNextPercentageOffset() + ",getNextPercentageSize()=" + this.getNextPercentageSize() + "]";
        }
    }
}
