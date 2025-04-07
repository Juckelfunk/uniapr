// 
// Decompiled by Procyon v0.5.36
// 

package org.codehaus.plexus.util;

import java.io.Writer;
import java.io.OutputStreamWriter;
import java.io.FileWriter;
import java.io.BufferedReader;
import java.io.FileReader;
import java.util.Random;
import java.security.SecureRandom;
import java.text.DecimalFormat;
import java.util.Iterator;
import java.util.ArrayList;
import org.codehaus.plexus.util.io.URLInputStreamFacade;
import org.codehaus.plexus.util.io.InputStreamFacade;
import org.codehaus.plexus.util.io.FileInputStreamFacade;
import java.net.URL;
import java.util.Vector;
import java.io.OutputStream;
import java.io.FileOutputStream;
import java.io.Reader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.File;
import java.util.Arrays;
import java.util.List;

public class FileUtils
{
    public static final int ONE_KB = 1024;
    public static final int ONE_MB = 1048576;
    public static final int ONE_GB = 1073741824;
    public static String FS;
    private static final String[] INVALID_CHARACTERS_FOR_WINDOWS_FILE_NAME;
    
    public static String[] getDefaultExcludes() {
        return DirectoryScanner.DEFAULTEXCLUDES;
    }
    
    public static List getDefaultExcludesAsList() {
        return Arrays.asList(getDefaultExcludes());
    }
    
    public static String getDefaultExcludesAsString() {
        return StringUtils.join(DirectoryScanner.DEFAULTEXCLUDES, ",");
    }
    
    public static String byteCountToDisplaySize(final int size) {
        String displaySize;
        if (size / 1073741824 > 0) {
            displaySize = String.valueOf(size / 1073741824) + " GB";
        }
        else if (size / 1048576 > 0) {
            displaySize = String.valueOf(size / 1048576) + " MB";
        }
        else if (size / 1024 > 0) {
            displaySize = String.valueOf(size / 1024) + " KB";
        }
        else {
            displaySize = String.valueOf(size) + " bytes";
        }
        return displaySize;
    }
    
    public static String dirname(final String filename) {
        final int i = filename.lastIndexOf(File.separator);
        return (i >= 0) ? filename.substring(0, i) : "";
    }
    
    public static String filename(final String filename) {
        final int i = filename.lastIndexOf(File.separator);
        return (i >= 0) ? filename.substring(i + 1) : filename;
    }
    
    public static String basename(final String filename) {
        return basename(filename, extension(filename));
    }
    
    public static String basename(final String filename, final String suffix) {
        final int i = filename.lastIndexOf(File.separator) + 1;
        final int lastDot = (suffix != null && suffix.length() > 0) ? filename.lastIndexOf(suffix) : -1;
        if (lastDot >= 0) {
            return filename.substring(i, lastDot);
        }
        if (i > 0) {
            return filename.substring(i);
        }
        return filename;
    }
    
    public static String extension(final String filename) {
        final int lastSep = filename.lastIndexOf(File.separatorChar);
        int lastDot;
        if (lastSep < 0) {
            lastDot = filename.lastIndexOf(46);
        }
        else {
            lastDot = filename.substring(lastSep + 1).lastIndexOf(46);
            if (lastDot >= 0) {
                lastDot += lastSep + 1;
            }
        }
        if (lastDot >= 0 && lastDot > lastSep) {
            return filename.substring(lastDot + 1);
        }
        return "";
    }
    
    public static boolean fileExists(final String fileName) {
        final File file = new File(fileName);
        return file.exists();
    }
    
    public static String fileRead(final String file) throws IOException {
        return fileRead(file, null);
    }
    
    public static String fileRead(final String file, final String encoding) throws IOException {
        return fileRead(new File(file), encoding);
    }
    
    public static String fileRead(final File file) throws IOException {
        return fileRead(file, null);
    }
    
    public static String fileRead(final File file, final String encoding) throws IOException {
        final StringBuffer buf = new StringBuffer();
        Reader reader = null;
        try {
            if (encoding != null) {
                reader = new InputStreamReader(new FileInputStream(file), encoding);
            }
            else {
                reader = new InputStreamReader(new FileInputStream(file));
            }
            final char[] b = new char[512];
            int count;
            while ((count = reader.read(b)) > 0) {
                buf.append(b, 0, count);
            }
        }
        finally {
            IOUtil.close(reader);
        }
        return buf.toString();
    }
    
    public static void fileAppend(final String fileName, final String data) throws IOException {
        fileAppend(fileName, null, data);
    }
    
    public static void fileAppend(final String fileName, final String encoding, final String data) throws IOException {
        FileOutputStream out = null;
        try {
            out = new FileOutputStream(fileName, true);
            if (encoding != null) {
                out.write(data.getBytes(encoding));
            }
            else {
                out.write(data.getBytes());
            }
        }
        finally {
            IOUtil.close(out);
        }
    }
    
    public static void fileWrite(final String fileName, final String data) throws IOException {
        fileWrite(fileName, null, data);
    }
    
    public static void fileWrite(final String fileName, final String encoding, final String data) throws IOException {
        FileOutputStream out = null;
        try {
            out = new FileOutputStream(fileName);
            if (encoding != null) {
                out.write(data.getBytes(encoding));
            }
            else {
                out.write(data.getBytes());
            }
        }
        finally {
            IOUtil.close(out);
        }
    }
    
    public static void fileDelete(final String fileName) {
        final File file = new File(fileName);
        file.delete();
    }
    
    public static boolean waitFor(final String fileName, final int seconds) {
        return waitFor(new File(fileName), seconds);
    }
    
    public static boolean waitFor(final File file, final int seconds) {
        int timeout = 0;
        int tick = 0;
        while (!file.exists()) {
            if (tick++ >= 10) {
                tick = 0;
                if (timeout++ > seconds) {
                    return false;
                }
            }
            try {
                Thread.sleep(100L);
            }
            catch (InterruptedException ignore) {}
        }
        return true;
    }
    
    public static File getFile(final String fileName) {
        return new File(fileName);
    }
    
    public static String[] getFilesFromExtension(final String directory, final String[] extensions) {
        Vector files = new Vector();
        final File currentDir = new File(directory);
        final String[] unknownFiles = currentDir.list();
        if (unknownFiles == null) {
            return new String[0];
        }
        for (int i = 0; i < unknownFiles.length; ++i) {
            final String currentFileName = directory + System.getProperty("file.separator") + unknownFiles[i];
            final File currentFile = new File(currentFileName);
            if (currentFile.isDirectory()) {
                if (!currentFile.getName().equals("CVS")) {
                    final String[] fetchFiles = getFilesFromExtension(currentFileName, extensions);
                    files = blendFilesToVector(files, fetchFiles);
                }
            }
            else {
                final String add = currentFile.getAbsolutePath();
                if (isValidFile(add, extensions)) {
                    files.addElement(add);
                }
            }
        }
        final String[] foundFiles = new String[files.size()];
        files.copyInto(foundFiles);
        return foundFiles;
    }
    
    private static Vector blendFilesToVector(final Vector v, final String[] files) {
        for (int i = 0; i < files.length; ++i) {
            v.addElement(files[i]);
        }
        return v;
    }
    
    private static boolean isValidFile(final String file, final String[] extensions) {
        String extension = extension(file);
        if (extension == null) {
            extension = "";
        }
        for (int i = 0; i < extensions.length; ++i) {
            if (extensions[i].equals(extension)) {
                return true;
            }
        }
        return false;
    }
    
    public static void mkdir(final String dir) {
        final File file = new File(dir);
        if (Os.isFamily("windows") && !isValidWindowsFileName(file)) {
            throw new IllegalArgumentException("The file (" + dir + ") cannot contain any of the following characters: \n" + StringUtils.join(FileUtils.INVALID_CHARACTERS_FOR_WINDOWS_FILE_NAME, " "));
        }
        if (!file.exists()) {
            file.mkdirs();
        }
    }
    
    public static boolean contentEquals(final File file1, final File file2) throws IOException {
        final boolean file1Exists = file1.exists();
        if (file1Exists != file2.exists()) {
            return false;
        }
        if (!file1Exists) {
            return true;
        }
        if (file1.isDirectory() || file2.isDirectory()) {
            return false;
        }
        InputStream input1 = null;
        InputStream input2 = null;
        try {
            input1 = new FileInputStream(file1);
            input2 = new FileInputStream(file2);
            return IOUtil.contentEquals(input1, input2);
        }
        finally {
            IOUtil.close(input1);
            IOUtil.close(input2);
        }
    }
    
    public static File toFile(final URL url) {
        if (url == null || !url.getProtocol().equalsIgnoreCase("file")) {
            return null;
        }
        String filename = url.getFile().replace('/', File.separatorChar);
        char ch = '\0';
        for (int pos = -1; (pos = filename.indexOf(37, pos + 1)) >= 0; filename = filename.substring(0, pos) + ch + filename.substring(pos + 3)) {
            if (pos + 2 < filename.length()) {
                final String hexStr = filename.substring(pos + 1, pos + 3);
                ch = (char)Integer.parseInt(hexStr, 16);
            }
        }
        return new File(filename);
    }
    
    public static URL[] toURLs(final File[] files) throws IOException {
        final URL[] urls = new URL[files.length];
        for (int i = 0; i < urls.length; ++i) {
            urls[i] = files[i].toURL();
        }
        return urls;
    }
    
    public static String removeExtension(final String filename) {
        final String ext = extension(filename);
        if ("".equals(ext)) {
            return filename;
        }
        final int index = filename.lastIndexOf(ext) - 1;
        return filename.substring(0, index);
    }
    
    public static String getExtension(final String filename) {
        return extension(filename);
    }
    
    public static String removePath(final String filepath) {
        return removePath(filepath, File.separatorChar);
    }
    
    public static String removePath(final String filepath, final char fileSeparatorChar) {
        final int index = filepath.lastIndexOf(fileSeparatorChar);
        if (-1 == index) {
            return filepath;
        }
        return filepath.substring(index + 1);
    }
    
    public static String getPath(final String filepath) {
        return getPath(filepath, File.separatorChar);
    }
    
    public static String getPath(final String filepath, final char fileSeparatorChar) {
        final int index = filepath.lastIndexOf(fileSeparatorChar);
        if (-1 == index) {
            return "";
        }
        return filepath.substring(0, index);
    }
    
    public static void copyFileToDirectory(final String source, final String destinationDirectory) throws IOException {
        copyFileToDirectory(new File(source), new File(destinationDirectory));
    }
    
    public static void copyFileToDirectoryIfModified(final String source, final String destinationDirectory) throws IOException {
        copyFileToDirectoryIfModified(new File(source), new File(destinationDirectory));
    }
    
    public static void copyFileToDirectory(final File source, final File destinationDirectory) throws IOException {
        if (destinationDirectory.exists() && !destinationDirectory.isDirectory()) {
            throw new IllegalArgumentException("Destination is not a directory");
        }
        copyFile(source, new File(destinationDirectory, source.getName()));
    }
    
    public static void copyFileToDirectoryIfModified(final File source, final File destinationDirectory) throws IOException {
        if (destinationDirectory.exists() && !destinationDirectory.isDirectory()) {
            throw new IllegalArgumentException("Destination is not a directory");
        }
        copyFileIfModified(source, new File(destinationDirectory, source.getName()));
    }
    
    public static void copyFile(final File source, final File destination) throws IOException {
        if (!source.exists()) {
            final String message = "File " + source + " does not exist";
            throw new IOException(message);
        }
        if (source.getCanonicalPath().equals(destination.getCanonicalPath())) {
            return;
        }
        copyStreamToFile(new FileInputStreamFacade(source), destination);
        if (source.length() != destination.length()) {
            final String message = "Failed to copy full contents from " + source + " to " + destination;
            throw new IOException(message);
        }
    }
    
    public static boolean copyFileIfModified(final File source, final File destination) throws IOException {
        if (destination.lastModified() < source.lastModified()) {
            copyFile(source, destination);
            return true;
        }
        return false;
    }
    
    public static void copyURLToFile(final URL source, final File destination) throws IOException {
        copyStreamToFile(new URLInputStreamFacade(source), destination);
    }
    
    public static void copyStreamToFile(final InputStreamFacade source, final File destination) throws IOException {
        if (destination.getParentFile() != null && !destination.getParentFile().exists()) {
            destination.getParentFile().mkdirs();
        }
        if (destination.exists() && !destination.canWrite()) {
            final String message = "Unable to open file " + destination + " for writing.";
            throw new IOException(message);
        }
        InputStream input = null;
        FileOutputStream output = null;
        try {
            input = source.getInputStream();
            output = new FileOutputStream(destination);
            IOUtil.copy(input, output);
        }
        finally {
            IOUtil.close(input);
            IOUtil.close(output);
        }
    }
    
    public static String normalize(final String path) {
        String normalized = path;
        while (true) {
            final int index = normalized.indexOf("//");
            if (index < 0) {
                break;
            }
            normalized = normalized.substring(0, index) + normalized.substring(index + 1);
        }
        while (true) {
            final int index = normalized.indexOf("/./");
            if (index < 0) {
                break;
            }
            normalized = normalized.substring(0, index) + normalized.substring(index + 2);
        }
        while (true) {
            final int index = normalized.indexOf("/../");
            if (index < 0) {
                return normalized;
            }
            if (index == 0) {
                return null;
            }
            final int index2 = normalized.lastIndexOf(47, index - 1);
            normalized = normalized.substring(0, index2) + normalized.substring(index + 3);
        }
    }
    
    public static String catPath(final String lookupPath, final String path) {
        int index = lookupPath.lastIndexOf("/");
        String lookup = lookupPath.substring(0, index);
        String pth;
        for (pth = path; pth.startsWith("../"); pth = pth.substring(index)) {
            if (lookup.length() <= 0) {
                return null;
            }
            index = lookup.lastIndexOf("/");
            lookup = lookup.substring(0, index);
            index = pth.indexOf("../") + 3;
        }
        return lookup + "/" + pth;
    }
    
    public static File resolveFile(final File baseFile, final String filename) {
        String filenm = filename;
        if ('/' != File.separatorChar) {
            filenm = filename.replace('/', File.separatorChar);
        }
        if ('\\' != File.separatorChar) {
            filenm = filename.replace('\\', File.separatorChar);
        }
        if (filenm.startsWith(File.separator) || (Os.isFamily("windows") && filenm.indexOf(":") > 0)) {
            File file = new File(filenm);
            try {
                file = file.getCanonicalFile();
            }
            catch (IOException ex) {}
            return file;
        }
        final char[] chars = filename.toCharArray();
        final StringBuffer sb = new StringBuffer();
        int start = 0;
        if ('\\' == File.separatorChar) {
            sb.append(filenm.charAt(0));
            ++start;
        }
        for (int i = start; i < chars.length; ++i) {
            final boolean doubleSeparator = File.separatorChar == chars[i] && File.separatorChar == chars[i - 1];
            if (!doubleSeparator) {
                sb.append(chars[i]);
            }
        }
        filenm = sb.toString();
        File file2 = new File(baseFile, filenm).getAbsoluteFile();
        try {
            file2 = file2.getCanonicalFile();
        }
        catch (IOException ex2) {}
        return file2;
    }
    
    public static void forceDelete(final String file) throws IOException {
        forceDelete(new File(file));
    }
    
    public static void forceDelete(final File file) throws IOException {
        if (file.isDirectory()) {
            deleteDirectory(file);
        }
        else {
            final boolean filePresent = file.getCanonicalFile().exists();
            if (!deleteFile(file) && filePresent) {
                final String message = "File " + file + " unable to be deleted.";
                throw new IOException(message);
            }
        }
    }
    
    private static boolean deleteFile(File file) throws IOException {
        if (file.isDirectory()) {
            throw new IOException("File " + file + " isn't a file.");
        }
        if (!file.delete()) {
            if (Os.isFamily("windows")) {
                file = file.getCanonicalFile();
                System.gc();
            }
            try {
                Thread.sleep(10L);
                return file.delete();
            }
            catch (InterruptedException ex) {
                return file.delete();
            }
        }
        return true;
    }
    
    public static void forceDeleteOnExit(final File file) throws IOException {
        if (!file.exists()) {
            return;
        }
        if (file.isDirectory()) {
            deleteDirectoryOnExit(file);
        }
        else {
            file.deleteOnExit();
        }
    }
    
    private static void deleteDirectoryOnExit(final File directory) throws IOException {
        if (!directory.exists()) {
            return;
        }
        cleanDirectoryOnExit(directory);
        directory.deleteOnExit();
    }
    
    private static void cleanDirectoryOnExit(final File directory) throws IOException {
        if (!directory.exists()) {
            final String message = directory + " does not exist";
            throw new IllegalArgumentException(message);
        }
        if (!directory.isDirectory()) {
            final String message = directory + " is not a directory";
            throw new IllegalArgumentException(message);
        }
        IOException exception = null;
        final File[] files = directory.listFiles();
        for (int i = 0; i < files.length; ++i) {
            final File file = files[i];
            try {
                forceDeleteOnExit(file);
            }
            catch (IOException ioe) {
                exception = ioe;
            }
        }
        if (null != exception) {
            throw exception;
        }
    }
    
    public static void forceMkdir(final File file) throws IOException {
        if (Os.isFamily("windows") && !isValidWindowsFileName(file)) {
            throw new IllegalArgumentException("The file (" + file.getAbsolutePath() + ") cannot contain any of the following characters: \n" + StringUtils.join(FileUtils.INVALID_CHARACTERS_FOR_WINDOWS_FILE_NAME, " "));
        }
        if (file.exists()) {
            if (file.isFile()) {
                final String message = "File " + file + " exists and is " + "not a directory. Unable to create directory.";
                throw new IOException(message);
            }
        }
        else if (!file.mkdirs()) {
            final String message = "Unable to create directory " + file;
            throw new IOException(message);
        }
    }
    
    public static void deleteDirectory(final String directory) throws IOException {
        deleteDirectory(new File(directory));
    }
    
    public static void deleteDirectory(final File directory) throws IOException {
        if (!directory.exists()) {
            return;
        }
        cleanDirectory(directory);
        if (!directory.delete()) {
            final String message = "Directory " + directory + " unable to be deleted.";
            throw new IOException(message);
        }
    }
    
    public static void cleanDirectory(final String directory) throws IOException {
        cleanDirectory(new File(directory));
    }
    
    public static void cleanDirectory(final File directory) throws IOException {
        if (!directory.exists()) {
            final String message = directory + " does not exist";
            throw new IllegalArgumentException(message);
        }
        if (!directory.isDirectory()) {
            final String message = directory + " is not a directory";
            throw new IllegalArgumentException(message);
        }
        IOException exception = null;
        final File[] files = directory.listFiles();
        if (files == null) {
            return;
        }
        for (int i = 0; i < files.length; ++i) {
            final File file = files[i];
            try {
                forceDelete(file);
            }
            catch (IOException ioe) {
                exception = ioe;
            }
        }
        if (null != exception) {
            throw exception;
        }
    }
    
    public static long sizeOfDirectory(final String directory) {
        return sizeOfDirectory(new File(directory));
    }
    
    public static long sizeOfDirectory(final File directory) {
        if (!directory.exists()) {
            final String message = directory + " does not exist";
            throw new IllegalArgumentException(message);
        }
        if (!directory.isDirectory()) {
            final String message = directory + " is not a directory";
            throw new IllegalArgumentException(message);
        }
        long size = 0L;
        final File[] files = directory.listFiles();
        for (int i = 0; i < files.length; ++i) {
            final File file = files[i];
            if (file.isDirectory()) {
                size += sizeOfDirectory(file);
            }
            else {
                size += file.length();
            }
        }
        return size;
    }
    
    public static List getFiles(final File directory, final String includes, final String excludes) throws IOException {
        return getFiles(directory, includes, excludes, true);
    }
    
    public static List getFiles(final File directory, final String includes, final String excludes, final boolean includeBasedir) throws IOException {
        final List fileNames = getFileNames(directory, includes, excludes, includeBasedir);
        final List files = new ArrayList();
        final Iterator i = fileNames.iterator();
        while (i.hasNext()) {
            files.add(new File(i.next()));
        }
        return files;
    }
    
    public static List getFileNames(final File directory, final String includes, final String excludes, final boolean includeBasedir) throws IOException {
        return getFileNames(directory, includes, excludes, includeBasedir, true);
    }
    
    public static List getFileNames(final File directory, final String includes, final String excludes, final boolean includeBasedir, final boolean isCaseSensitive) throws IOException {
        return getFileAndDirectoryNames(directory, includes, excludes, includeBasedir, isCaseSensitive, true, false);
    }
    
    public static List getDirectoryNames(final File directory, final String includes, final String excludes, final boolean includeBasedir) throws IOException {
        return getDirectoryNames(directory, includes, excludes, includeBasedir, true);
    }
    
    public static List getDirectoryNames(final File directory, final String includes, final String excludes, final boolean includeBasedir, final boolean isCaseSensitive) throws IOException {
        return getFileAndDirectoryNames(directory, includes, excludes, includeBasedir, isCaseSensitive, false, true);
    }
    
    public static List getFileAndDirectoryNames(final File directory, final String includes, final String excludes, final boolean includeBasedir, final boolean isCaseSensitive, final boolean getFiles, final boolean getDirectories) throws IOException {
        final DirectoryScanner scanner = new DirectoryScanner();
        scanner.setBasedir(directory);
        if (includes != null) {
            scanner.setIncludes(StringUtils.split(includes, ","));
        }
        if (excludes != null) {
            scanner.setExcludes(StringUtils.split(excludes, ","));
        }
        scanner.setCaseSensitive(isCaseSensitive);
        scanner.scan();
        final List list = new ArrayList();
        if (getFiles) {
            final String[] files = scanner.getIncludedFiles();
            for (int i = 0; i < files.length; ++i) {
                if (includeBasedir) {
                    list.add(directory + FileUtils.FS + files[i]);
                }
                else {
                    list.add(files[i]);
                }
            }
        }
        if (getDirectories) {
            final String[] directories = scanner.getIncludedDirectories();
            for (int i = 0; i < directories.length; ++i) {
                if (includeBasedir) {
                    list.add(directory + FileUtils.FS + directories[i]);
                }
                else {
                    list.add(directories[i]);
                }
            }
        }
        return list;
    }
    
    public static void copyDirectory(final File sourceDirectory, final File destinationDirectory) throws IOException {
        copyDirectory(sourceDirectory, destinationDirectory, "**", null);
    }
    
    public static void copyDirectory(final File sourceDirectory, final File destinationDirectory, final String includes, final String excludes) throws IOException {
        if (!sourceDirectory.exists()) {
            return;
        }
        final List files = getFiles(sourceDirectory, includes, excludes);
        for (final File file : files) {
            copyFileToDirectory(file, destinationDirectory);
        }
    }
    
    public static void copyDirectoryLayout(final File sourceDirectory, final File destinationDirectory, final String[] includes, final String[] excludes) throws IOException {
        if (sourceDirectory == null) {
            throw new IOException("source directory can't be null.");
        }
        if (destinationDirectory == null) {
            throw new IOException("destination directory can't be null.");
        }
        if (sourceDirectory.equals(destinationDirectory)) {
            throw new IOException("source and destination are the same directory.");
        }
        if (!sourceDirectory.exists()) {
            throw new IOException("Source directory doesn't exists (" + sourceDirectory.getAbsolutePath() + ").");
        }
        final DirectoryScanner scanner = new DirectoryScanner();
        scanner.setBasedir(sourceDirectory);
        if (includes != null && includes.length >= 1) {
            scanner.setIncludes(includes);
        }
        else {
            scanner.setIncludes(new String[] { "**" });
        }
        if (excludes != null && excludes.length >= 1) {
            scanner.setExcludes(excludes);
        }
        scanner.addDefaultExcludes();
        scanner.scan();
        final List includedDirectories = Arrays.asList(scanner.getIncludedDirectories());
        for (final String name : includedDirectories) {
            final File source = new File(sourceDirectory, name);
            if (source.equals(sourceDirectory)) {
                continue;
            }
            final File destination = new File(destinationDirectory, name);
            destination.mkdirs();
        }
    }
    
    public static void copyDirectoryStructure(final File sourceDirectory, final File destinationDirectory) throws IOException {
        copyDirectoryStructure(sourceDirectory, destinationDirectory, destinationDirectory, false);
    }
    
    public static void copyDirectoryStructureIfModified(final File sourceDirectory, final File destinationDirectory) throws IOException {
        copyDirectoryStructure(sourceDirectory, destinationDirectory, destinationDirectory, true);
    }
    
    private static void copyDirectoryStructure(final File sourceDirectory, final File destinationDirectory, final File rootDestinationDirectory, final boolean onlyModifiedFiles) throws IOException {
        if (sourceDirectory == null) {
            throw new IOException("source directory can't be null.");
        }
        if (destinationDirectory == null) {
            throw new IOException("destination directory can't be null.");
        }
        if (sourceDirectory.equals(destinationDirectory)) {
            throw new IOException("source and destination are the same directory.");
        }
        if (!sourceDirectory.exists()) {
            throw new IOException("Source directory doesn't exists (" + sourceDirectory.getAbsolutePath() + ").");
        }
        final File[] files = sourceDirectory.listFiles();
        final String sourcePath = sourceDirectory.getAbsolutePath();
        for (int i = 0; i < files.length; ++i) {
            final File file = files[i];
            if (!file.equals(rootDestinationDirectory)) {
                String dest = file.getAbsolutePath();
                dest = dest.substring(sourcePath.length() + 1);
                File destination = new File(destinationDirectory, dest);
                if (file.isFile()) {
                    destination = destination.getParentFile();
                    if (onlyModifiedFiles) {
                        copyFileToDirectoryIfModified(file, destination);
                    }
                    else {
                        copyFileToDirectory(file, destination);
                    }
                }
                else {
                    if (!file.isDirectory()) {
                        throw new IOException("Unknown file type: " + file.getAbsolutePath());
                    }
                    if (!destination.exists() && !destination.mkdirs()) {
                        throw new IOException("Could not create destination directory '" + destination.getAbsolutePath() + "'.");
                    }
                    copyDirectoryStructure(file, destination, rootDestinationDirectory, onlyModifiedFiles);
                }
            }
        }
    }
    
    public static void rename(final File from, final File to) throws IOException {
        if (to.exists() && !to.delete()) {
            throw new IOException("Failed to delete " + to + " while trying to rename " + from);
        }
        final File parent = to.getParentFile();
        if (parent != null && !parent.exists() && !parent.mkdirs()) {
            throw new IOException("Failed to create directory " + parent + " while trying to rename " + from);
        }
        if (!from.renameTo(to)) {
            copyFile(from, to);
            if (!from.delete()) {
                throw new IOException("Failed to delete " + from + " while trying to rename it.");
            }
        }
    }
    
    public static File createTempFile(final String prefix, final String suffix, final File parentDir) {
        File result = null;
        String parent = System.getProperty("java.io.tmpdir");
        if (parentDir != null) {
            parent = parentDir.getPath();
        }
        final DecimalFormat fmt = new DecimalFormat("#####");
        final SecureRandom secureRandom = new SecureRandom();
        final long secureInitializer = secureRandom.nextLong();
        final Random rand = new Random(secureInitializer + Runtime.getRuntime().freeMemory());
        synchronized (rand) {
            do {
                result = new File(parent, prefix + fmt.format(Math.abs(rand.nextInt())) + suffix);
            } while (result.exists());
        }
        return result;
    }
    
    public static void copyFile(final File from, final File to, final String encoding, final FilterWrapper[] wrappers) throws IOException {
        copyFile(from, to, encoding, wrappers, false);
    }
    
    public static void copyFile(final File from, final File to, final String encoding, final FilterWrapper[] wrappers, final boolean overwrite) throws IOException {
        if (wrappers != null && wrappers.length > 0) {
            Reader fileReader = null;
            Writer fileWriter = null;
            try {
                if (encoding == null || encoding.length() < 1) {
                    fileReader = new BufferedReader(new FileReader(from));
                    fileWriter = new FileWriter(to);
                }
                else {
                    final FileInputStream instream = new FileInputStream(from);
                    final FileOutputStream outstream = new FileOutputStream(to);
                    fileReader = new BufferedReader(new InputStreamReader(instream, encoding));
                    fileWriter = new OutputStreamWriter(outstream, encoding);
                }
                Reader reader = fileReader;
                for (int i = 0; i < wrappers.length; ++i) {
                    final FilterWrapper wrapper = wrappers[i];
                    reader = wrapper.getReader(reader);
                }
                IOUtil.copy(reader, fileWriter);
            }
            finally {
                IOUtil.close(fileReader);
                IOUtil.close(fileWriter);
            }
        }
        else if (to.lastModified() < from.lastModified() || overwrite) {
            copyFile(from, to);
        }
    }
    
    public static List loadFile(final File file) throws IOException {
        final List lines = new ArrayList();
        if (file.exists()) {
            final BufferedReader reader = new BufferedReader(new FileReader(file));
            for (String line = reader.readLine(); line != null; line = reader.readLine()) {
                line = line.trim();
                if (!line.startsWith("#") && line.length() != 0) {
                    lines.add(line);
                }
            }
            reader.close();
        }
        return lines;
    }
    
    public static boolean isValidWindowsFileName(final File f) {
        if (Os.isFamily("windows")) {
            if (StringUtils.indexOfAny(f.getName(), FileUtils.INVALID_CHARACTERS_FOR_WINDOWS_FILE_NAME) != -1) {
                return false;
            }
            if (f.getParentFile() != null) {
                return isValidWindowsFileName(f.getParentFile());
            }
        }
        return true;
    }
    
    static {
        FileUtils.FS = System.getProperty("file.separator");
        INVALID_CHARACTERS_FOR_WINDOWS_FILE_NAME = new String[] { ":", "*", "?", "\"", "<", ">", "|" };
    }
    
    public abstract static class FilterWrapper
    {
        public abstract Reader getReader(final Reader p0);
    }
}
