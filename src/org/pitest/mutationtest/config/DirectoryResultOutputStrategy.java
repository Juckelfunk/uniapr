// 
// Decompiled by Procyon v0.5.36
// 

package org.pitest.mutationtest.config;

import java.io.IOException;
import org.pitest.util.Unchecked;
import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.Writer;
import java.io.File;
import org.pitest.util.ResultOutputStrategy;

public class DirectoryResultOutputStrategy implements ResultOutputStrategy
{
    private final File reportDir;
    
    public DirectoryResultOutputStrategy(final String baseDir, final ReportDirCreationStrategy dirCreationStrategy) {
        this.reportDir = dirCreationStrategy.createReportDir(baseDir);
    }
    
    @Override
    public Writer createWriterForFile(final String file) {
        try {
            final int fileSepIndex = file.lastIndexOf(File.separatorChar);
            if (fileSepIndex > 0) {
                final String directory = this.reportDir.getAbsolutePath() + File.separatorChar + file.substring(0, fileSepIndex);
                final File directoryFile = new File(directory);
                if (!directoryFile.exists()) {
                    directoryFile.mkdirs();
                }
            }
            return new BufferedWriter(new FileWriter(this.reportDir.getAbsolutePath() + File.separatorChar + file));
        }
        catch (IOException ex) {
            throw Unchecked.translateCheckedException(ex);
        }
    }
}
