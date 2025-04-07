// 
// Decompiled by Procyon v0.5.36
// 

package org.pitest.mutationtest.report.xml;

import org.pitest.mutationtest.MutationResultListener;
import org.pitest.mutationtest.ListenerArguments;
import java.util.Properties;
import org.pitest.mutationtest.MutationResultListenerFactory;

public class XMLReportFactory implements MutationResultListenerFactory
{
    @Override
    public MutationResultListener getListener(final Properties props, final ListenerArguments args) {
        return new XMLReportListener(args.getOutputStrategy());
    }
    
    @Override
    public String name() {
        return "XML";
    }
    
    @Override
    public String description() {
        return "Default xml report plugin";
    }
}
