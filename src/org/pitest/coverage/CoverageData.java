// 
// Decompiled by Procyon v0.5.36
// 

package org.pitest.coverage;

import org.pitest.util.Log;
import org.pitest.functional.predicate.Predicate;
import org.pitest.functional.Option;
import org.pitest.testapi.Description;
import org.pitest.functional.F2;
import java.util.HashSet;
import java.util.List;
import java.math.BigInteger;
import java.util.Iterator;
import org.pitest.functional.F;
import java.util.Comparator;
import java.util.TreeSet;
import java.util.Collections;
import org.pitest.functional.FCollection;
import java.util.LinkedHashMap;
import org.pitest.classpath.CodeSource;
import org.pitest.classinfo.ClassInfo;
import java.util.Collection;
import org.pitest.classinfo.ClassName;
import java.util.Set;
import java.util.Map;
import java.util.logging.Logger;

public class CoverageData implements CoverageDatabase
{
    private static final Logger LOG;
    private final Map<BlockLocation, Set<TestInfo>> blockCoverage;
    private final Map<BlockLocation, Set<Integer>> blocksToLines;
    private final Map<ClassName, Map<ClassLine, Set<TestInfo>>> lineCoverage;
    private final Map<String, Collection<ClassInfo>> classesForFile;
    private final CodeSource code;
    private final LineMap lm;
    private boolean hasFailedTest;
    
    public CoverageData(final CodeSource code, final LineMap lm) {
        this(code, lm, new LinkedHashMap<BlockLocation, Set<TestInfo>>());
    }
    
    public CoverageData(final CodeSource code, final LineMap lm, final Map<BlockLocation, Set<TestInfo>> blockCoverage) {
        this.blocksToLines = new LinkedHashMap<BlockLocation, Set<Integer>>();
        this.lineCoverage = new LinkedHashMap<ClassName, Map<ClassLine, Set<TestInfo>>>();
        this.hasFailedTest = false;
        this.blockCoverage = blockCoverage;
        this.code = code;
        this.lm = lm;
        this.classesForFile = FCollection.bucket(this.code.getCode(), keyFromClassInfo());
    }
    
    @Override
    public Collection<TestInfo> getTestsForClassLine(final ClassLine classLine) {
        final Collection<TestInfo> result = this.getTestsForClassName(classLine.getClassName()).get(classLine);
        if (result == null) {
            return (Collection<TestInfo>)Collections.emptyList();
        }
        return result;
    }
    
    public boolean allTestsGreen() {
        return !this.hasFailedTest;
    }
    
    @Override
    public Collection<ClassInfo> getClassInfo(final Collection<ClassName> classes) {
        return this.code.getClassInfo(classes);
    }
    
    @Override
    public int getNumberOfCoveredLines(final Collection<ClassName> mutatedClass) {
        return FCollection.fold(this.numberCoveredLines(), 0, mutatedClass);
    }
    
    @Override
    public Collection<TestInfo> getTestsForClass(final ClassName clazz) {
        final Set<TestInfo> tis = new TreeSet<TestInfo>(new TestInfoNameComparator());
        tis.addAll((Collection<? extends TestInfo>)FCollection.filter(this.blockCoverage.entrySet(), (F<Map.Entry<BlockLocation, Set<TestInfo>>, Boolean>)this.isFor(clazz)).flatMap((F<Map.Entry<BlockLocation, Set<TestInfo>>, ? extends Iterable<Object>>)this.toTests()));
        return tis;
    }
    
    public void calculateClassCoverage(final CoverageResult cr) {
        this.checkForFailedTest(cr);
        final TestInfo ti = this.createTestInfo(cr.getTestUnitDescription(), cr.getExecutionTime(), cr.getNumberOfCoveredBlocks());
        for (final BlockLocation each : cr.getCoverage()) {
            this.addTestsToBlockMap(ti, each);
        }
    }
    
    private void addTestsToBlockMap(final TestInfo ti, final BlockLocation each) {
        Set<TestInfo> tests = this.blockCoverage.get(each);
        if (tests == null) {
            tests = new TreeSet<TestInfo>(new TestInfoNameComparator());
            this.blockCoverage.put(each, tests);
        }
        tests.add(ti);
    }
    
    @Override
    public BigInteger getCoverageIdForClass(final ClassName clazz) {
        final Map<ClassLine, Set<TestInfo>> coverage = this.getTestsForClassName(clazz);
        if (coverage.isEmpty()) {
            return BigInteger.ZERO;
        }
        return this.generateCoverageNumber(coverage);
    }
    
    public List<BlockCoverage> createCoverage() {
        return FCollection.map(this.blockCoverage.entrySet(), toBlockCoverage());
    }
    
    private static F<Map.Entry<BlockLocation, Set<TestInfo>>, BlockCoverage> toBlockCoverage() {
        return new F<Map.Entry<BlockLocation, Set<TestInfo>>, BlockCoverage>() {
            @Override
            public BlockCoverage apply(final Map.Entry<BlockLocation, Set<TestInfo>> a) {
                return new BlockCoverage(a.getKey(), FCollection.map(a.getValue(), TestInfo.toName()));
            }
        };
    }
    
    @Override
    public Collection<ClassInfo> getClassesForFile(final String sourceFile, final String packageName) {
        final Collection<ClassInfo> value = this.getClassesForFileCache().get(keyFromSourceAndPackage(sourceFile, packageName));
        if (value == null) {
            return (Collection<ClassInfo>)Collections.emptyList();
        }
        return value;
    }
    
    private Map<String, Collection<ClassInfo>> getClassesForFileCache() {
        return this.classesForFile;
    }
    
    @Override
    public CoverageSummary createSummary() {
        return new CoverageSummary(this.numberOfLines(), this.coveredLines());
    }
    
    private BigInteger generateCoverageNumber(final Map<ClassLine, Set<TestInfo>> coverage) {
        BigInteger coverageNumber = BigInteger.ZERO;
        final Set<ClassName> testClasses = new HashSet<ClassName>();
        FCollection.flatMapTo((Iterable<? extends Set<TestInfo>>)coverage.values(), (F<Set<TestInfo>, ? extends Iterable<Object>>)this.testsToClassName(), (Collection<? super Object>)testClasses);
        for (final ClassInfo each : this.code.getClassInfo(testClasses)) {
            coverageNumber = coverageNumber.add(each.getDeepHash());
        }
        return coverageNumber;
    }
    
    private F<Set<TestInfo>, Iterable<ClassName>> testsToClassName() {
        return new F<Set<TestInfo>, Iterable<ClassName>>() {
            @Override
            public Iterable<ClassName> apply(final Set<TestInfo> a) {
                return FCollection.map(a, TestInfo.toDefiningClassName());
            }
        };
    }
    
    private static F<ClassInfo, String> keyFromClassInfo() {
        return new F<ClassInfo, String>() {
            @Override
            public String apply(final ClassInfo c) {
                return keyFromSourceAndPackage(c.getSourceFileName(), c.getName().getPackage().asJavaName());
            }
        };
    }
    
    private static String keyFromSourceAndPackage(final String sourceFile, final String packageName) {
        return packageName + " " + sourceFile;
    }
    
    private Collection<ClassName> allClasses() {
        return this.code.getCodeUnderTestNames();
    }
    
    private int numberOfLines() {
        return FCollection.fold(this.numberLines(), 0, this.code.getClassInfo(this.allClasses()));
    }
    
    private int coveredLines() {
        return FCollection.fold(this.numberCoveredLines(), 0, this.allClasses());
    }
    
    private F2<Integer, ClassInfo, Integer> numberLines() {
        return new F2<Integer, ClassInfo, Integer>() {
            @Override
            public Integer apply(final Integer a, final ClassInfo clazz) {
                return a + clazz.getNumberOfCodeLines();
            }
        };
    }
    
    private void checkForFailedTest(final CoverageResult cr) {
        if (!cr.isGreenTest()) {
            this.recordTestFailure();
            CoverageData.LOG.severe(cr.getTestUnitDescription() + " did not pass without mutation.");
        }
    }
    
    private TestInfo createTestInfo(final Description description, final int executionTime, final int linesCovered) {
        final Option<ClassName> testee = this.code.findTestee(description.getFirstTestClass());
        return new TestInfo(description.getFirstTestClass(), description.getQualifiedName(), executionTime, testee, linesCovered);
    }
    
    private F2<Integer, ClassName, Integer> numberCoveredLines() {
        return new F2<Integer, ClassName, Integer>() {
            @Override
            public Integer apply(final Integer a, final ClassName clazz) {
                return a + CoverageData.this.getNumberOfCoveredLines(clazz);
            }
        };
    }
    
    private int getNumberOfCoveredLines(final ClassName clazz) {
        final Map<ClassLine, Set<TestInfo>> map = this.getTestsForClassName(clazz);
        if (map != null) {
            return map.size();
        }
        return 0;
    }
    
    private Map<ClassLine, Set<TestInfo>> getTestsForClassName(final ClassName clazz) {
        final Map<ClassLine, Set<TestInfo>> map = this.lineCoverage.get(clazz);
        if (map != null) {
            return map;
        }
        return this.convertBlockCoverageToLineCoverageForClass(clazz);
    }
    
    private Map<ClassLine, Set<TestInfo>> convertBlockCoverageToLineCoverageForClass(final ClassName clazz) {
        final List<Map.Entry<BlockLocation, Set<TestInfo>>> tests = (List<Map.Entry<BlockLocation, Set<TestInfo>>>)FCollection.filter(this.blockCoverage.entrySet(), (F<Object, Boolean>)this.isFor(clazz));
        final Map<ClassLine, Set<TestInfo>> linesToTests = new LinkedHashMap<ClassLine, Set<TestInfo>>(0);
        for (final Map.Entry<BlockLocation, Set<TestInfo>> each : tests) {
            for (final int line : this.getLinesForBlock(each.getKey())) {
                final Set<TestInfo> tis = getLineTestSet(clazz, linesToTests, each, line);
                tis.addAll(each.getValue());
            }
        }
        this.lineCoverage.put(clazz, linesToTests);
        return linesToTests;
    }
    
    private static Set<TestInfo> getLineTestSet(final ClassName clazz, final Map<ClassLine, Set<TestInfo>> linesToTests, final Map.Entry<BlockLocation, Set<TestInfo>> each, final int line) {
        final ClassLine cl = new ClassLine(clazz, line);
        Set<TestInfo> tis = linesToTests.get(cl);
        if (tis == null) {
            tis = new TreeSet<TestInfo>(new TestInfoNameComparator());
            tis.addAll(each.getValue());
            linesToTests.put(new ClassLine(clazz, line), tis);
        }
        return tis;
    }
    
    private Set<Integer> getLinesForBlock(final BlockLocation bl) {
        Set<Integer> lines = this.blocksToLines.get(bl);
        if (lines == null) {
            this.calculateLinesForBlocks(bl.getLocation().getClassName());
            lines = this.blocksToLines.get(bl);
            if (lines == null) {
                lines = Collections.emptySet();
            }
        }
        return lines;
    }
    
    private void calculateLinesForBlocks(final ClassName className) {
        final Map<BlockLocation, Set<Integer>> lines = this.lm.mapLines(className);
        this.blocksToLines.putAll(lines);
    }
    
    private void recordTestFailure() {
        this.hasFailedTest = true;
    }
    
    private F<Map.Entry<BlockLocation, Set<TestInfo>>, Iterable<TestInfo>> toTests() {
        return new F<Map.Entry<BlockLocation, Set<TestInfo>>, Iterable<TestInfo>>() {
            @Override
            public Iterable<TestInfo> apply(final Map.Entry<BlockLocation, Set<TestInfo>> a) {
                return a.getValue();
            }
        };
    }
    
    private Predicate<Map.Entry<BlockLocation, Set<TestInfo>>> isFor(final ClassName clazz) {
        return new Predicate<Map.Entry<BlockLocation, Set<TestInfo>>>() {
            @Override
            public Boolean apply(final Map.Entry<BlockLocation, Set<TestInfo>> a) {
                return a.getKey().isFor(clazz);
            }
        };
    }
    
    static {
        LOG = Log.getLogger();
    }
}
