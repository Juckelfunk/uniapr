// 
// Decompiled by Procyon v0.5.36
// 

package org.pitest.dependency;

import org.pitest.util.Log;
import org.pitest.functional.F2;
import java.util.HashMap;
import org.pitest.functional.SideEffect1;
import org.pitest.functional.Option;
import org.pitest.reloc.asm.ClassVisitor;
import org.pitest.bytecode.NullVisitor;
import java.util.ArrayList;
import org.pitest.reloc.asm.ClassReader;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.HashSet;
import java.util.TreeSet;
import org.pitest.util.Functions;
import java.io.IOException;
import java.util.Set;
import org.pitest.functional.FCollection;
import org.pitest.functional.prelude.Prelude;
import org.pitest.functional.F;
import java.util.Collection;
import org.pitest.functional.predicate.Predicate;
import org.pitest.classinfo.ClassByteArraySource;
import java.util.logging.Logger;

public class DependencyExtractor
{
    private static final Logger LOG;
    private final int depth;
    private final ClassByteArraySource classToBytes;
    
    public DependencyExtractor(final ClassByteArraySource classToBytes, final int depth) {
        this.depth = depth;
        this.classToBytes = classToBytes;
    }
    
    public Collection<String> extractCallDependenciesForPackages(final String clazz, final Predicate<String> targetPackages) throws IOException {
        final Set<String> allDependencies = this.extractCallDependencies(clazz, new IgnoreCoreClasses());
        return (Collection<String>)FCollection.filter(allDependencies, (F<Object, Boolean>)Prelude.and(asJVMNamePredicate(targetPackages), notSuppliedClass(clazz)));
    }
    
    private static F<String, Boolean> notSuppliedClass(final String clazz) {
        return new F<String, Boolean>() {
            @Override
            public Boolean apply(final String a) {
                return !Functions.jvmClassToClassName().apply(a).equals(clazz);
            }
        };
    }
    
    private static F<String, Boolean> asJVMNamePredicate(final Predicate<String> predicate) {
        return new F<String, Boolean>() {
            @Override
            public Boolean apply(final String a) {
                return (Boolean)predicate.apply(Functions.jvmClassToClassName().apply(a));
            }
        };
    }
    
    public Collection<String> extractCallDependenciesForPackages(final String clazz, final Predicate<String> targetPackages, final Predicate<DependencyAccess> doNotTraverse) throws IOException {
        final Set<String> allDependencies = this.extractCallDependencies(clazz, doNotTraverse);
        return (Collection<String>)FCollection.filter(allDependencies, (F<Object, Boolean>)targetPackages);
    }
    
    Set<String> extractCallDependencies(final String clazz, final Predicate<DependencyAccess> filter) throws IOException {
        return this.extractCallDependencies(clazz, new TreeSet<String>(), filter, 0);
    }
    
    public int getMaxDistance() {
        return this.depth;
    }
    
    private Set<String> extractCallDependencies(final String clazz, final TreeSet<String> visited, final Predicate<DependencyAccess> filter, final int currentDepth) throws IOException {
        final Map<String, List<DependencyAccess>> classesToAccesses = this.groupDependenciesByClass(this.extractRelevantDependencies(clazz, filter));
        final Set<String> dependencies = new HashSet<String>(classesToAccesses.keySet());
        dependencies.removeAll(visited);
        visited.addAll(dependencies);
        if (currentDepth < this.depth - 1 || this.depth == 0) {
            dependencies.addAll(this.examineChildDependencies(currentDepth, dependencies, visited, filter));
        }
        return dependencies;
    }
    
    private Set<String> examineChildDependencies(final int currentDepth, final Set<String> classes, final TreeSet<String> visited, final Predicate<DependencyAccess> filter) throws IOException {
        final Set<String> deps = new HashSet<String>();
        for (final String each : classes) {
            final Set<String> childDependencies = this.extractCallDependencies(each, visited, filter, currentDepth + 1);
            deps.addAll(childDependencies);
        }
        return deps;
    }
    
    private Set<DependencyAccess> extractRelevantDependencies(final String clazz, final Predicate<DependencyAccess> filter) throws IOException {
        final List<DependencyAccess> dependencies = this.extract(clazz, filter);
        final Set<DependencyAccess> relevantDependencies = new TreeSet<DependencyAccess>(equalDestinationComparator());
        FCollection.filter(dependencies, (F<Object, Boolean>)filter, (Collection<? super Object>)relevantDependencies);
        return relevantDependencies;
    }
    
    private static Comparator<DependencyAccess> equalDestinationComparator() {
        return new Comparator<DependencyAccess>() {
            @Override
            public int compare(final DependencyAccess o1, final DependencyAccess o2) {
                return o1.getDest().compareTo(o2.getDest());
            }
        };
    }
    
    private List<DependencyAccess> extract(final String clazz, final Predicate<DependencyAccess> filter) throws IOException {
        final Option<byte[]> bytes = this.classToBytes.getBytes(clazz);
        if (bytes.hasNone()) {
            DependencyExtractor.LOG.warning("No bytes found for " + clazz);
            return Collections.emptyList();
        }
        final ClassReader reader = new ClassReader(bytes.value());
        final List<DependencyAccess> dependencies = new ArrayList<DependencyAccess>();
        final SideEffect1<DependencyAccess> se = constructCollectingSideEffectForVisitor(dependencies, (Predicate<DependencyAccess>)Prelude.and(Prelude.not((F<Object, Boolean>)nameIsEqual(clazz)), filter));
        final DependencyClassVisitor dcv = new DependencyClassVisitor(new NullVisitor(), se);
        reader.accept(dcv, 8);
        return dependencies;
    }
    
    private Map<String, List<DependencyAccess>> groupDependenciesByClass(final Set<DependencyAccess> relevantDependencies) {
        final List<DependencyAccess> sortedByClass = new ArrayList<DependencyAccess>(relevantDependencies.size());
        Collections.sort(sortedByClass, classNameComparator());
        return FCollection.fold((F2<HashMap<String, List<DependencyAccess>>, DependencyAccess, HashMap<String, List<DependencyAccess>>>)addDependenciesToMap(), new HashMap<String, List<DependencyAccess>>(), relevantDependencies);
    }
    
    private static F2<Map<String, List<DependencyAccess>>, DependencyAccess, Map<String, List<DependencyAccess>>> addDependenciesToMap() {
        return new F2<Map<String, List<DependencyAccess>>, DependencyAccess, Map<String, List<DependencyAccess>>>() {
            @Override
            public Map<String, List<DependencyAccess>> apply(final Map<String, List<DependencyAccess>> map, final DependencyAccess access) {
                List<DependencyAccess> list = map.get(access.getDest().getOwner());
                if (list == null) {
                    list = new ArrayList<DependencyAccess>();
                }
                list.add(access);
                map.put(access.getDest().getOwner(), list);
                return map;
            }
        };
    }
    
    private static Comparator<DependencyAccess> classNameComparator() {
        return new Comparator<DependencyAccess>() {
            @Override
            public int compare(final DependencyAccess lhs, final DependencyAccess rhs) {
                return lhs.getDest().getOwner().compareTo(rhs.getDest().getOwner());
            }
        };
    }
    
    private static Predicate<DependencyAccess> nameIsEqual(final String clazz) {
        return new Predicate<DependencyAccess>() {
            @Override
            public Boolean apply(final DependencyAccess a) {
                return a.getDest().getOwner().equals(clazz);
            }
        };
    }
    
    private static SideEffect1<DependencyAccess> constructCollectingSideEffectForVisitor(final List<DependencyAccess> dependencies, final Predicate<DependencyAccess> predicate) {
        final SideEffect1<DependencyAccess> se = new SideEffect1<DependencyAccess>() {
            @Override
            public void apply(final DependencyAccess a) {
                if (predicate.apply(a)) {
                    dependencies.add(a);
                }
            }
        };
        return se;
    }
    
    static {
        LOG = Log.getLogger();
    }
}
