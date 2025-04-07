// 
// Decompiled by Procyon v0.5.36
// 

package org.pitest.mutationtest.report.csv;

import java.util.Iterator;
import org.pitest.mutationtest.MutationResult;
import org.pitest.mutationtest.ClassMutationResults;
import java.io.IOException;
import org.pitest.util.Unchecked;
import org.pitest.functional.Option;
import org.pitest.util.ResultOutputStrategy;
import java.io.Writer;
import org.pitest.mutationtest.MutationResultListener;

public class CSVReportListener implements MutationResultListener
{
    private final Writer out;
    
    public CSVReportListener(final ResultOutputStrategy outputStrategy) {
        this(outputStrategy.createWriterForFile("mutations.csv"));
    }
    
    public CSVReportListener(final Writer out) {
        this.out = out;
    }
    
    private String createKillingTestDesc(final Option<String> killingTest) {
        if (killingTest.hasSome()) {
            return killingTest.value();
        }
        return "none";
    }
    
    private String makeCsv(final Object... os) {
        final StringBuilder sb = new StringBuilder();
        for (int i = 0; i != os.length; ++i) {
            sb.append(os[i].toString());
            if (i != os.length - 1) {
                sb.append(",");
            }
        }
        return sb.toString();
    }
    
    @Override
    public void runStart() {
    }
    
    @Override
    public void runEnd() {
        try {
            this.out.close();
        }
        catch (IOException e) {
            throw Unchecked.translateCheckedException(e);
        }
    }
    
    @Override
    public void handleMutationResult(final ClassMutationResults metaData) {
        try {
            for (final MutationResult mutation : metaData.getMutations()) {
                this.out.write(this.makeCsv(mutation.getDetails().getFilename(), mutation.getDetails().getClassName().asJavaName(), mutation.getDetails().getMutator(), mutation.getDetails().getMethod(), mutation.getDetails().getLineNumber(), mutation.getStatus(), this.createKillingTestDesc(mutation.getKillingTest())) + System.getProperty("line.separator"));
            }
        }
        catch (IOException ex) {
            throw Unchecked.translateCheckedException(ex);
        }
    }
}
