// 
// Decompiled by Procyon v0.5.36
// 

package org.pitest.coverage.export;

import java.io.IOException;
import org.pitest.util.Unchecked;
import java.util.List;
import org.pitest.mutationtest.engine.Location;
import java.util.Collections;
import java.util.ArrayList;
import org.pitest.util.StringUtil;
import java.util.Iterator;
import java.io.Writer;
import org.pitest.coverage.BlockCoverage;
import java.util.Collection;
import org.pitest.util.ResultOutputStrategy;
import org.pitest.coverage.CoverageExporter;

public class DefaultCoverageExporter implements CoverageExporter
{
    private final ResultOutputStrategy outputStrategy;
    
    public DefaultCoverageExporter(final ResultOutputStrategy outputStrategy) {
        this.outputStrategy = outputStrategy;
    }
    
    @Override
    public void recordCoverage(final Collection<BlockCoverage> coverage) {
        final Writer out = this.outputStrategy.createWriterForFile("linecoverage.xml");
        this.writeHeader(out);
        for (final BlockCoverage each : coverage) {
            this.writeLineCoverage(each, out);
        }
        this.writeFooterAndClose(out);
    }
    
    private void writeHeader(final Writer out) {
        this.write(out, "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n");
        this.write(out, "<coverage>\n");
    }
    
    private void writeLineCoverage(final BlockCoverage each, final Writer out) {
        final Location l = each.getBlock().getLocation();
        this.write(out, "<block classname='" + l.getClassName().asJavaName() + "' method='" + StringUtil.escapeBasicHtmlChars(l.getMethodName().name()) + StringUtil.escapeBasicHtmlChars(l.getMethodDesc()) + "' number='" + each.getBlock().getBlock() + "'>");
        this.write(out, "<tests>\n");
        final List<String> ts = new ArrayList<String>(each.getTests());
        Collections.sort(ts);
        for (final String test : ts) {
            this.write(out, "<test name='" + test + "'/>\n");
        }
        this.write(out, "</tests>\n");
        this.write(out, "</block>\n");
    }
    
    private void writeFooterAndClose(final Writer out) {
        try {
            this.write(out, "</coverage>\n");
            out.close();
        }
        catch (IOException e) {
            throw Unchecked.translateCheckedException(e);
        }
    }
    
    private void write(final Writer out, final String value) {
        try {
            out.write(value);
        }
        catch (IOException e) {
            throw Unchecked.translateCheckedException(e);
        }
    }
}
