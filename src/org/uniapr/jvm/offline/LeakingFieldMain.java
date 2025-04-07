// 
// Decompiled by Procyon v0.5.36
// 

package org.uniapr.jvm.offline;

import java.util.HashSet;
import java.util.Enumeration;
import java.util.zip.ZipEntry;
import java.util.jar.JarEntry;
import java.io.InputStream;
import org.objectweb.asm.ClassVisitor;
import org.objectweb.asm.ClassReader;
import java.io.FileInputStream;
import java.util.ArrayList;
import java.io.Reader;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.File;
import java.util.HashMap;
import java.util.Iterator;
import java.io.Writer;
import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.PrintStream;
import java.io.IOException;
import java.util.jar.JarFile;
import java.util.List;
import java.util.Set;
import java.util.Map;

public class LeakingFieldMain
{
    public static Map<String, Boolean> classes;
    public static Set<String> visitedAndSafe;
    public static Map<String, String> completeParentMap;
    static Map<String, List<String>> prunedInheritanceMap;
    public static int counter;
    public static String CLASSLOG;
    public static String INHERILOG;
    public static String out;
    
    public static void invoke(final String path) {
        final PrintStream printer = System.out;
        final long time1 = System.currentTimeMillis();
        final String[] arr$;
        final String[] items = arr$ = path.split(":");
        for (final String item : arr$) {
            if (item.endsWith(".jar")) {
                try {
                    analyze(new JarFile(item));
                }
                catch (IOException e) {
                    e.printStackTrace();
                }
            }
            else {
                analyze(item);
            }
        }
        final long time2 = System.currentTimeMillis();
        final int serialized = serialize(LeakingFieldMain.CLASSLOG, LeakingFieldMain.INHERILOG);
        printer.println("Analyzed: " + LeakingFieldMain.counter + " files, Leaking: " + LeakingFieldMain.classes.size() + " files, After filtering: " + serialized + " files stored!");
        printer.println("Offline analysis cost: " + (time2 - time1) + "ms");
    }
    
    public static int serialize(final String cf, final String inf) {
        int serialized = 0;
        try {
            final BufferedWriter writer = new BufferedWriter(new FileWriter(cf));
            for (final String clazz : LeakingFieldMain.classes.keySet()) {
                ++serialized;
                writer.write(clazz + " " + LeakingFieldMain.classes.get(clazz) + "\n");
            }
            writer.flush();
            writer.close();
            final BufferedWriter writer2 = new BufferedWriter(new FileWriter(inf));
            for (final String clazz2 : LeakingFieldMain.completeParentMap.keySet()) {
                writer2.write(clazz2 + " " + LeakingFieldMain.completeParentMap.get(clazz2));
                writer2.write("\n");
            }
            writer2.flush();
            writer2.close();
        }
        catch (Exception ex) {}
        return serialized;
    }
    
    public static Map<String, Boolean> deSerializeClasses(final String cf) throws IOException {
        final Map<String, Boolean> map = new HashMap<String, Boolean>();
        final File file = new File(cf);
        if (!file.exists()) {
            return map;
        }
        final BufferedReader reader = new BufferedReader(new FileReader(cf));
        for (String line = reader.readLine(); line != null; line = reader.readLine()) {
            final String[] items = line.split(" ");
            map.put(items[0], Boolean.valueOf(items[1]));
        }
        reader.close();
        return map;
    }
    
    public static Map<String, String> deSerializeInheritance(final String inf) throws IOException {
        final Map<String, String> map = new HashMap<String, String>();
        final File file = new File(inf);
        if (!file.exists()) {
            return map;
        }
        final BufferedReader reader = new BufferedReader(new FileReader(inf));
        for (String line = reader.readLine(); line != null; line = reader.readLine()) {
            final String[] items = line.split(" ");
            map.put(items[0], items[1]);
        }
        reader.close();
        return map;
    }
    
    public static void analyze(final String dir) {
        final File dirFile = new File(dir);
        final List<File> workList = new ArrayList<File>();
        workList.add(dirFile);
        while (!workList.isEmpty()) {
            final File curF = workList.remove(0);
            if (curF.getName().endsWith(".class")) {
                try {
                    final InputStream classFileInputStream = new FileInputStream(curF);
                    final ClassReader cr = new ClassReader(classFileInputStream);
                    final LeakingFieldsVisitor ca = new LeakingFieldsVisitor();
                    cr.accept(ca, 0);
                    classFileInputStream.close();
                }
                catch (Exception ex) {}
            }
            else {
                if (!curF.isDirectory()) {
                    continue;
                }
                for (final File f : curF.listFiles()) {
                    workList.add(f);
                }
            }
        }
    }
    
    public static void analyze(final JarFile f) {
        final Enumeration<JarEntry> entries = f.entries();
        while (entries.hasMoreElements()) {
            final JarEntry entry = entries.nextElement();
            final String entryName = entry.getName();
            if (entryName.endsWith(".class")) {
                try {
                    final InputStream classFileInputStream = f.getInputStream(entry);
                    final ClassReader cr = new ClassReader(classFileInputStream);
                    final LeakingFieldsVisitor ca = new LeakingFieldsVisitor();
                    cr.accept(ca, 0);
                    classFileInputStream.close();
                }
                catch (Exception ex) {}
            }
        }
    }
    
    static {
        LeakingFieldMain.classes = new HashMap<String, Boolean>();
        LeakingFieldMain.visitedAndSafe = new HashSet<String>();
        LeakingFieldMain.completeParentMap = new HashMap<String, String>();
        LeakingFieldMain.prunedInheritanceMap = new HashMap<String, List<String>>();
        LeakingFieldMain.counter = 0;
        LeakingFieldMain.CLASSLOG = "classes.log";
        LeakingFieldMain.INHERILOG = "inheritance.log";
        LeakingFieldMain.out = "/Users/lingmingzhang/Research/data/defects4j-source/lang_1_buggy" + File.separator + LeakingFieldMain.CLASSLOG;
    }
}
