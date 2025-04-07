// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.maven.wagon;

import java.util.LinkedList;
import org.apache.maven.wagon.authorization.AuthorizationException;
import java.io.IOException;
import org.codehaus.plexus.util.FileUtils;
import java.io.File;

public final class WagonUtils
{
    private WagonUtils() {
    }
    
    public static String toString(final String resource, final Wagon wagon) throws IOException, TransferFailedException, ResourceDoesNotExistException, AuthorizationException {
        File file = null;
        try {
            file = File.createTempFile("wagon", "tmp");
            wagon.get(resource, file);
            final String retValue = FileUtils.fileRead(file);
            return retValue;
        }
        finally {
            if (file != null) {
                final boolean deleted = file.delete();
                if (!deleted) {
                    file.deleteOnExit();
                }
            }
        }
    }
    
    public static void putDirectory(final File dir, final Wagon wagon, final boolean includeBasdir) throws ResourceDoesNotExistException, TransferFailedException, AuthorizationException {
        final LinkedList queue = new LinkedList();
        if (includeBasdir) {
            queue.add(dir.getName());
        }
        else {
            queue.add("");
        }
        while (!queue.isEmpty()) {
            final String path = queue.removeFirst();
            final File currentDir = new File(dir, path);
            final File[] files = currentDir.listFiles();
            for (int i = 0; i < files.length; ++i) {
                final File file = files[i];
                String resource;
                if (path.length() > 0) {
                    resource = path + "/" + file.getName();
                }
                else {
                    resource = file.getName();
                }
                if (file.isDirectory()) {
                    queue.add(resource);
                }
                else {
                    wagon.put(file, resource);
                }
            }
        }
    }
}
