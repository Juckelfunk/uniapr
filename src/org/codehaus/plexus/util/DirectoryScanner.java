// 
// Decompiled by Procyon v0.5.36
// 

package org.codehaus.plexus.util;

import java.io.IOException;
import java.util.Vector;
import java.io.File;

public class DirectoryScanner extends AbstractScanner
{
    protected File basedir;
    protected Vector filesIncluded;
    protected Vector filesNotIncluded;
    protected Vector filesExcluded;
    protected Vector dirsIncluded;
    protected Vector dirsNotIncluded;
    protected Vector dirsExcluded;
    protected Vector filesDeselected;
    protected Vector dirsDeselected;
    protected boolean haveSlowResults;
    private boolean followSymlinks;
    protected boolean everythingIncluded;
    
    public DirectoryScanner() {
        this.haveSlowResults = false;
        this.followSymlinks = true;
        this.everythingIncluded = true;
    }
    
    public void setBasedir(final String basedir) {
        this.setBasedir(new File(basedir.replace('/', File.separatorChar).replace('\\', File.separatorChar)));
    }
    
    public void setBasedir(final File basedir) {
        this.basedir = basedir;
    }
    
    public File getBasedir() {
        return this.basedir;
    }
    
    public void setFollowSymlinks(final boolean followSymlinks) {
        this.followSymlinks = followSymlinks;
    }
    
    public boolean isEverythingIncluded() {
        return this.everythingIncluded;
    }
    
    public void scan() throws IllegalStateException {
        if (this.basedir == null) {
            throw new IllegalStateException("No basedir set");
        }
        if (!this.basedir.exists()) {
            throw new IllegalStateException("basedir " + this.basedir + " does not exist");
        }
        if (!this.basedir.isDirectory()) {
            throw new IllegalStateException("basedir " + this.basedir + " is not a directory");
        }
        this.setupDefaultFilters();
        this.filesIncluded = new Vector();
        this.filesNotIncluded = new Vector();
        this.filesExcluded = new Vector();
        this.filesDeselected = new Vector();
        this.dirsIncluded = new Vector();
        this.dirsNotIncluded = new Vector();
        this.dirsExcluded = new Vector();
        this.dirsDeselected = new Vector();
        if (this.isIncluded("")) {
            if (!this.isExcluded("")) {
                if (this.isSelected("", this.basedir)) {
                    this.dirsIncluded.addElement("");
                }
                else {
                    this.dirsDeselected.addElement("");
                }
            }
            else {
                this.dirsExcluded.addElement("");
            }
        }
        else {
            this.dirsNotIncluded.addElement("");
        }
        this.scandir(this.basedir, "", true);
    }
    
    protected void slowScan() {
        if (this.haveSlowResults) {
            return;
        }
        final String[] excl = new String[this.dirsExcluded.size()];
        this.dirsExcluded.copyInto(excl);
        final String[] notIncl = new String[this.dirsNotIncluded.size()];
        this.dirsNotIncluded.copyInto(notIncl);
        for (int i = 0; i < excl.length; ++i) {
            if (!this.couldHoldIncluded(excl[i])) {
                this.scandir(new File(this.basedir, excl[i]), excl[i] + File.separator, false);
            }
        }
        for (int i = 0; i < notIncl.length; ++i) {
            if (!this.couldHoldIncluded(notIncl[i])) {
                this.scandir(new File(this.basedir, notIncl[i]), notIncl[i] + File.separator, false);
            }
        }
        this.haveSlowResults = true;
    }
    
    protected void scandir(final File dir, final String vpath, final boolean fast) {
        String[] newfiles = dir.list();
        if (newfiles == null) {
            newfiles = new String[0];
        }
        if (!this.followSymlinks) {
            final Vector noLinks = new Vector();
            for (int i = 0; i < newfiles.length; ++i) {
                try {
                    if (this.isSymbolicLink(dir, newfiles[i])) {
                        final String name = vpath + newfiles[i];
                        final File file = new File(dir, newfiles[i]);
                        if (file.isDirectory()) {
                            this.dirsExcluded.addElement(name);
                        }
                        else {
                            this.filesExcluded.addElement(name);
                        }
                    }
                    else {
                        noLinks.addElement(newfiles[i]);
                    }
                }
                catch (IOException ioe) {
                    final String msg = "IOException caught while checking for links, couldn't get cannonical path!";
                    System.err.println(msg);
                    noLinks.addElement(newfiles[i]);
                }
            }
            newfiles = new String[noLinks.size()];
            noLinks.copyInto(newfiles);
        }
        for (int j = 0; j < newfiles.length; ++j) {
            final String name2 = vpath + newfiles[j];
            final File file2 = new File(dir, newfiles[j]);
            if (file2.isDirectory()) {
                if (this.isIncluded(name2)) {
                    if (!this.isExcluded(name2)) {
                        if (this.isSelected(name2, file2)) {
                            this.dirsIncluded.addElement(name2);
                            if (fast) {
                                this.scandir(file2, name2 + File.separator, fast);
                            }
                        }
                        else {
                            this.everythingIncluded = false;
                            this.dirsDeselected.addElement(name2);
                            if (fast && this.couldHoldIncluded(name2)) {
                                this.scandir(file2, name2 + File.separator, fast);
                            }
                        }
                    }
                    else {
                        this.everythingIncluded = false;
                        this.dirsExcluded.addElement(name2);
                        if (fast && this.couldHoldIncluded(name2)) {
                            this.scandir(file2, name2 + File.separator, fast);
                        }
                    }
                }
                else {
                    this.everythingIncluded = false;
                    this.dirsNotIncluded.addElement(name2);
                    if (fast && this.couldHoldIncluded(name2)) {
                        this.scandir(file2, name2 + File.separator, fast);
                    }
                }
                if (!fast) {
                    this.scandir(file2, name2 + File.separator, fast);
                }
            }
            else if (file2.isFile()) {
                if (this.isIncluded(name2)) {
                    if (!this.isExcluded(name2)) {
                        if (this.isSelected(name2, file2)) {
                            this.filesIncluded.addElement(name2);
                        }
                        else {
                            this.everythingIncluded = false;
                            this.filesDeselected.addElement(name2);
                        }
                    }
                    else {
                        this.everythingIncluded = false;
                        this.filesExcluded.addElement(name2);
                    }
                }
                else {
                    this.everythingIncluded = false;
                    this.filesNotIncluded.addElement(name2);
                }
            }
        }
    }
    
    protected boolean isSelected(final String name, final File file) {
        return true;
    }
    
    public String[] getIncludedFiles() {
        final String[] files = new String[this.filesIncluded.size()];
        this.filesIncluded.copyInto(files);
        return files;
    }
    
    public String[] getNotIncludedFiles() {
        this.slowScan();
        final String[] files = new String[this.filesNotIncluded.size()];
        this.filesNotIncluded.copyInto(files);
        return files;
    }
    
    public String[] getExcludedFiles() {
        this.slowScan();
        final String[] files = new String[this.filesExcluded.size()];
        this.filesExcluded.copyInto(files);
        return files;
    }
    
    public String[] getDeselectedFiles() {
        this.slowScan();
        final String[] files = new String[this.filesDeselected.size()];
        this.filesDeselected.copyInto(files);
        return files;
    }
    
    public String[] getIncludedDirectories() {
        final String[] directories = new String[this.dirsIncluded.size()];
        this.dirsIncluded.copyInto(directories);
        return directories;
    }
    
    public String[] getNotIncludedDirectories() {
        this.slowScan();
        final String[] directories = new String[this.dirsNotIncluded.size()];
        this.dirsNotIncluded.copyInto(directories);
        return directories;
    }
    
    public String[] getExcludedDirectories() {
        this.slowScan();
        final String[] directories = new String[this.dirsExcluded.size()];
        this.dirsExcluded.copyInto(directories);
        return directories;
    }
    
    public String[] getDeselectedDirectories() {
        this.slowScan();
        final String[] directories = new String[this.dirsDeselected.size()];
        this.dirsDeselected.copyInto(directories);
        return directories;
    }
    
    public boolean isSymbolicLink(final File parent, final String name) throws IOException {
        final File resolvedParent = new File(parent.getCanonicalPath());
        final File toTest = new File(resolvedParent, name);
        return !toTest.getAbsolutePath().equals(toTest.getCanonicalPath());
    }
}
