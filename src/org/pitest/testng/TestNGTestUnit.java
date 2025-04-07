// 
// Decompiled by Procyon v0.5.36
// 

package org.pitest.testng;

import org.testng.IInvokedMethodListener;
import org.testng.ITestListener;
import java.util.Iterator;
import org.testng.xml.XmlInclude;
import java.util.ArrayList;
import org.testng.xml.XmlClass;
import org.testng.xml.XmlTest;
import org.testng.xml.XmlSuite;
import java.util.List;
import java.util.Collections;
import org.pitest.testapi.ResultCollector;
import org.pitest.testapi.Description;
import java.util.Collection;
import org.pitest.testapi.TestGroupConfig;
import org.testng.TestNG;
import org.pitest.testapi.AbstractTestUnit;

public class TestNGTestUnit extends AbstractTestUnit
{
    private static final TestNG TESTNG;
    private static final MutableTestListenerWrapper LISTENER;
    private final Class<?> clazz;
    private final TestGroupConfig config;
    private final Collection<String> includedTestMethods;
    
    public TestNGTestUnit(final Class<?> clazz, final TestGroupConfig config, final Collection<String> includedTestMethods) {
        super(new Description("_", clazz));
        this.clazz = clazz;
        this.config = config;
        this.includedTestMethods = includedTestMethods;
    }
    
    @Override
    public void execute(final ResultCollector rc) {
        synchronized (TestNGTestUnit.TESTNG) {
            this.executeInCurrentLoader(rc);
        }
    }
    
    private void executeInCurrentLoader(final ResultCollector rc) {
        final TestNGAdapter listener = new TestNGAdapter(this.clazz, this.getDescription(), rc);
        final XmlSuite suite = this.createSuite();
        TestNGTestUnit.TESTNG.setDefaultSuiteName(suite.getName());
        TestNGTestUnit.TESTNG.setXmlSuites((List)Collections.singletonList(suite));
        TestNGTestUnit.LISTENER.setChild(listener);
        try {
            TestNGTestUnit.TESTNG.run();
        }
        finally {
            TestNGTestUnit.LISTENER.setChild(null);
        }
    }
    
    private XmlSuite createSuite() {
        final XmlSuite suite = new XmlSuite();
        suite.setName(this.clazz.getName());
        suite.setSkipFailedInvocationCounts(true);
        final XmlTest test = new XmlTest(suite);
        test.setName(this.clazz.getName());
        final XmlClass xclass = new XmlClass(this.clazz.getName());
        test.setXmlClasses((List)Collections.singletonList(xclass));
        if (!this.includedTestMethods.isEmpty()) {
            final List<XmlInclude> xmlIncludedTestMethods = new ArrayList<XmlInclude>();
            for (final String includedTestMethod : this.includedTestMethods) {
                final XmlInclude includedMethod = new XmlInclude(includedTestMethod);
                xmlIncludedTestMethods.add(includedMethod);
            }
            xclass.setIncludedMethods((List)xmlIncludedTestMethods);
        }
        if (!this.config.getExcludedGroups().isEmpty()) {
            suite.setExcludedGroups((List)this.config.getExcludedGroups());
        }
        if (!this.config.getIncludedGroups().isEmpty()) {
            suite.setIncludedGroups((List)this.config.getIncludedGroups());
        }
        return suite;
    }
    
    static {
        TESTNG = new TestNG(false);
        LISTENER = new MutableTestListenerWrapper();
        TestNGTestUnit.TESTNG.addListener((ITestListener)TestNGTestUnit.LISTENER);
        TestNGTestUnit.TESTNG.addInvokedMethodListener((IInvokedMethodListener)new FailFast(TestNGTestUnit.LISTENER));
    }
}
