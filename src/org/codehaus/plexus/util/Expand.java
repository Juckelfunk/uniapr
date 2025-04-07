// 
// Decompiled by Procyon v0.5.36
// 

package org.codehaus.plexus.util;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.util.zip.ZipEntry;
import java.io.IOException;
import java.util.Date;
import java.io.InputStream;
import java.util.zip.ZipInputStream;
import java.io.FileInputStream;
import java.io.File;

public class Expand
{
    private File dest;
    private File source;
    private boolean overwrite;
    
    public Expand() {
        this.overwrite = true;
    }
    
    public void execute() throws Exception {
        this.expandFile(this.source, this.dest);
    }
    
    protected void expandFile(final File srcF, final File dir) throws Exception {
        ZipInputStream zis = null;
        try {
            zis = new ZipInputStream(new FileInputStream(srcF));
            ZipEntry ze = null;
            while ((ze = zis.getNextEntry()) != null) {
                this.extractFile(srcF, dir, zis, ze.getName(), new Date(ze.getTime()), ze.isDirectory());
            }
        }
        catch (IOException ioe) {
            throw new Exception("Error while expanding " + srcF.getPath(), ioe);
        }
        finally {
            if (zis != null) {
                try {
                    zis.close();
                }
                catch (IOException ex) {}
            }
        }
    }
    
    protected void extractFile(final File srcF, final File dir, final InputStream compressedInputStream, final String entryName, final Date entryDate, final boolean isDirectory) throws Exception {
        final File f = FileUtils.resolveFile(dir, entryName);
        try {
            if (!this.overwrite && f.exists() && f.lastModified() >= entryDate.getTime()) {
                return;
            }
            final File dirF = f.getParentFile();
            dirF.mkdirs();
            if (isDirectory) {
                f.mkdirs();
            }
            else {
                final byte[] buffer = new byte[1024];
                int length = 0;
                FileOutputStream fos = null;
                try {
                    fos = new FileOutputStream(f);
                    while ((length = compressedInputStream.read(buffer)) >= 0) {
                        fos.write(buffer, 0, length);
                    }
                    fos.close();
                    fos = null;
                }
                finally {
                    if (fos != null) {
                        try {
                            fos.close();
                        }
                        catch (IOException ex2) {}
                    }
                }
            }
            f.setLastModified(entryDate.getTime());
        }
        catch (FileNotFoundException ex) {
            throw new Exception("Can't extract file " + srcF.getPath(), ex);
        }
    }
    
    public void setDest(final File d) {
        this.dest = d;
    }
    
    public void setSrc(final File s) {
        this.source = s;
    }
    
    public void setOverwrite(final boolean b) {
        this.overwrite = b;
    }
}
