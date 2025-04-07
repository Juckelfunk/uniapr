// 
// Decompiled by Procyon v0.5.36
// 

package org.codehaus.plexus.interpolation.os;

import java.util.Collection;
import java.util.Iterator;
import java.util.Locale;
import java.util.HashSet;
import java.util.Set;

public class Os
{
    public static final String FAMILY_DOS = "dos";
    public static final String FAMILY_MAC = "mac";
    public static final String FAMILY_NETWARE = "netware";
    public static final String FAMILY_OS2 = "os/2";
    public static final String FAMILY_TANDEM = "tandem";
    public static final String FAMILY_UNIX = "unix";
    public static final String FAMILY_WINDOWS = "windows";
    public static final String FAMILY_WIN9X = "win9x";
    public static final String FAMILY_ZOS = "z/os";
    public static final String FAMILY_OS400 = "os/400";
    public static final String FAMILY_OPENVMS = "openvms";
    private static final Set validFamilies;
    private static final String PATH_SEP;
    public static final String OS_NAME;
    public static final String OS_ARCH;
    public static final String OS_VERSION;
    public static final String OS_FAMILY;
    private String family;
    private String name;
    private String version;
    private String arch;
    
    public Os() {
    }
    
    public Os(final String family) {
        this.setFamily(family);
    }
    
    private static Set setValidFamilies() {
        final Set valid = new HashSet();
        valid.add("dos");
        valid.add("mac");
        valid.add("netware");
        valid.add("os/2");
        valid.add("tandem");
        valid.add("unix");
        valid.add("windows");
        valid.add("win9x");
        valid.add("z/os");
        valid.add("os/400");
        valid.add("openvms");
        return valid;
    }
    
    public void setFamily(final String f) {
        this.family = f.toLowerCase(Locale.US);
    }
    
    public void setName(final String name) {
        this.name = name.toLowerCase(Locale.US);
    }
    
    public void setArch(final String arch) {
        this.arch = arch.toLowerCase(Locale.US);
    }
    
    public void setVersion(final String version) {
        this.version = version.toLowerCase(Locale.US);
    }
    
    public boolean eval() throws Exception {
        return isOs(this.family, this.name, this.arch, this.version);
    }
    
    public static boolean isFamily(final String family) {
        return isOs(family, null, null, null);
    }
    
    public static boolean isName(final String name) {
        return isOs(null, name, null, null);
    }
    
    public static boolean isArch(final String arch) {
        return isOs(null, null, arch, null);
    }
    
    public static boolean isVersion(final String version) {
        return isOs(null, null, null, version);
    }
    
    public static boolean isOs(final String family, final String name, final String arch, final String version) {
        boolean retValue = false;
        if (family != null || name != null || arch != null || version != null) {
            boolean isFamily = true;
            boolean isName = true;
            boolean isArch = true;
            boolean isVersion = true;
            if (family != null) {
                if (family.equalsIgnoreCase("windows")) {
                    isFamily = (Os.OS_NAME.indexOf("windows") > -1);
                }
                else if (family.equalsIgnoreCase("os/2")) {
                    isFamily = (Os.OS_NAME.indexOf("os/2") > -1);
                }
                else if (family.equalsIgnoreCase("netware")) {
                    isFamily = (Os.OS_NAME.indexOf("netware") > -1);
                }
                else if (family.equalsIgnoreCase("dos")) {
                    isFamily = (Os.PATH_SEP.equals(";") && !isFamily("netware"));
                }
                else if (family.equalsIgnoreCase("mac")) {
                    isFamily = (Os.OS_NAME.indexOf("mac") > -1);
                }
                else if (family.equalsIgnoreCase("tandem")) {
                    isFamily = (Os.OS_NAME.indexOf("nonstop_kernel") > -1);
                }
                else if (family.equalsIgnoreCase("unix")) {
                    isFamily = (Os.PATH_SEP.equals(":") && !isFamily("openvms") && (!isFamily("mac") || Os.OS_NAME.endsWith("x")));
                }
                else if (family.equalsIgnoreCase("win9x")) {
                    isFamily = (isFamily("windows") && (Os.OS_NAME.indexOf("95") >= 0 || Os.OS_NAME.indexOf("98") >= 0 || Os.OS_NAME.indexOf("me") >= 0 || Os.OS_NAME.indexOf("ce") >= 0));
                }
                else if (family.equalsIgnoreCase("z/os")) {
                    isFamily = (Os.OS_NAME.indexOf("z/os") > -1 || Os.OS_NAME.indexOf("os/390") > -1);
                }
                else if (family.equalsIgnoreCase("os/400")) {
                    isFamily = (Os.OS_NAME.indexOf("os/400") > -1);
                }
                else if (family.equalsIgnoreCase("openvms")) {
                    isFamily = (Os.OS_NAME.indexOf("openvms") > -1);
                }
                else {
                    isFamily = (Os.OS_NAME.indexOf(family.toLowerCase()) > -1);
                }
            }
            if (name != null) {
                isName = name.toLowerCase(Locale.US).equals(Os.OS_NAME);
            }
            if (arch != null) {
                isArch = arch.toLowerCase(Locale.US).equals(Os.OS_ARCH);
            }
            if (version != null) {
                isVersion = version.toLowerCase(Locale.US).equals(Os.OS_VERSION);
            }
            retValue = (isFamily && isName && isArch && isVersion);
        }
        return retValue;
    }
    
    private static String getOsFamily() {
        Set families = null;
        if (!Os.validFamilies.isEmpty()) {
            families = Os.validFamilies;
        }
        else {
            families = setValidFamilies();
        }
        for (final String fam : families) {
            if (isFamily(fam)) {
                return fam;
            }
        }
        return null;
    }
    
    public static boolean isValidFamily(final String theFamily) {
        return Os.validFamilies.contains(theFamily);
    }
    
    public static Set getValidFamilies() {
        return new HashSet(Os.validFamilies);
    }
    
    static {
        validFamilies = setValidFamilies();
        PATH_SEP = System.getProperty("path.separator");
        OS_NAME = System.getProperty("os.name").toLowerCase(Locale.US);
        OS_ARCH = System.getProperty("os.arch").toLowerCase(Locale.US);
        OS_VERSION = System.getProperty("os.version").toLowerCase(Locale.US);
        OS_FAMILY = getOsFamily();
    }
}
