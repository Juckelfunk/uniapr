// 
// Decompiled by Procyon v0.5.36
// 

package org.pitest.junit.adapter;

import org.pitest.util.Log;
import org.junit.internal.builders.AllDefaultPossibilitiesBuilder;
import org.junit.runners.model.RunnerBuilder;
import org.junit.runner.manipulation.NoTestsRemainException;
import org.junit.runner.manipulation.Filterable;
import org.junit.internal.runners.ErrorReportingRunner;
import org.junit.runner.Runner;
import org.pitest.util.Unchecked;
import java.util.logging.Level;
import org.pitest.testapi.ResultCollector;
import org.pitest.testapi.Description;
import org.junit.runner.manipulation.Filter;
import org.pitest.functional.Option;
import java.util.logging.Logger;
import org.pitest.testapi.AbstractTestUnit;

public class AdaptedJUnitTestUnit extends AbstractTestUnit
{
    private static final Logger LOG;
    private final Class<?> clazz;
    private final Option<Filter> filter;
    
    public AdaptedJUnitTestUnit(final Class<?> clazz, final Option<Filter> filter) {
        super(new Description(createName(clazz, filter), clazz));
        this.clazz = clazz;
        this.filter = filter;
    }
    
    private static String createName(final Class<?> clazz, final Option<Filter> filter) {
        if (filter.hasSome()) {
            return filter.value().describe();
        }
        return clazz.getName();
    }
    
    @Override
    public void execute(final ResultCollector rc) {
        final Runner runner = createRunner(this.clazz);
        this.checkForErrorRunner(runner);
        this.filterIfRequired(rc, runner);
        try {
            final CustomRunnerExecutor nativeCe = new CustomRunnerExecutor(this.getDescription(), runner, rc);
            nativeCe.run();
        }
        catch (Exception e) {
            AdaptedJUnitTestUnit.LOG.log(Level.SEVERE, "Error while running adapter JUnit fixture " + this.clazz + " with filter " + this.filter, e);
            throw Unchecked.translateCheckedException(e);
        }
    }
    
    private void checkForErrorRunner(final Runner runner) {
        if (runner instanceof ErrorReportingRunner) {
            AdaptedJUnitTestUnit.LOG.warning("JUnit error for class " + this.clazz + " : " + runner.getDescription());
        }
    }
    
    private void filterIfRequired(final ResultCollector rc, final Runner runner) {
        if (this.filter.hasSome()) {
            if (!(runner instanceof Filterable)) {
                AdaptedJUnitTestUnit.LOG.warning("Not able to filter " + runner.getDescription() + ". Mutation may have prevented JUnit from constructing test");
                return;
            }
            final Filterable f = (Filterable)runner;
            try {
                f.filter(this.filter.value());
            }
            catch (NoTestsRemainException e1) {
                rc.notifySkipped(this.getDescription());
            }
        }
    }
    
    public static Runner createRunner(final Class<?> clazz) {
        final RunnerBuilder builder = createRunnerBuilder();
        try {
            return builder.runnerForClass(clazz);
        }
        catch (Throwable ex) {
            AdaptedJUnitTestUnit.LOG.log(Level.SEVERE, "Error while creating runner for " + clazz, ex);
            throw Unchecked.translateCheckedException(ex);
        }
    }
    
    private static RunnerBuilder createRunnerBuilder() {
        return new AllDefaultPossibilitiesBuilder(true);
    }
    
    @Override
    public String toString() {
        return "AdaptedJUnitTestUnit [clazz=" + this.clazz + ", filter=" + this.filter + "]";
    }
    
    static {
        LOG = Log.getLogger();
    }
}
