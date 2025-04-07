// 
// Decompiled by Procyon v0.5.36
// 

package org.pitest.mutationtest.report.csv;

import org.pitest.mutationtest.MutationResultListener;
import org.pitest.mutationtest.ListenerArguments;
import java.util.Properties;
import org.pitest.mutationtest.MutationResultListenerFactory;

public class CSVReportFactory implements MutationResultListenerFactory
{
    @Override
    public MutationResultListener getListener(final Properties props, final ListenerArguments args) {
        return new CSVReportListener(args.getOutputStrategy());
    }
    
    @Override
    public String name() {
        return "CSV";
    }
    
    @Override
    public String description() {
        return "Default csv report plugin";
    }
}
