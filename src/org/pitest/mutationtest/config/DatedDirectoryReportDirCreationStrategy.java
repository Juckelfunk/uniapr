// 
// Decompiled by Procyon v0.5.36
// 

package org.pitest.mutationtest.config;

import java.util.Date;
import java.text.SimpleDateFormat;
import java.io.File;

public class DatedDirectoryReportDirCreationStrategy implements ReportDirCreationStrategy
{
    @Override
    public File createReportDir(final String base) {
        final SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmm");
        final String timeString = sdf.format(new Date());
        final File reportDir = new File(this.addPathSeparatorIfMissing(base) + timeString);
        reportDir.mkdirs();
        return reportDir;
    }
    
    private String addPathSeparatorIfMissing(final String s) {
        if (!s.endsWith(File.separator)) {
            return s + File.separator;
        }
        return s;
    }
}
