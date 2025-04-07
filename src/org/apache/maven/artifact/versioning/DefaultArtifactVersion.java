// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.maven.artifact.versioning;

import java.util.StringTokenizer;

public class DefaultArtifactVersion implements ArtifactVersion
{
    private Integer majorVersion;
    private Integer minorVersion;
    private Integer incrementalVersion;
    private Integer buildNumber;
    private String qualifier;
    private String unparsed;
    
    public DefaultArtifactVersion(final String version) {
        this.parseVersion(version);
    }
    
    public int compareTo(final Object o) {
        final ArtifactVersion otherVersion = (ArtifactVersion)o;
        int result = this.getMajorVersion() - otherVersion.getMajorVersion();
        if (result == 0) {
            result = this.getMinorVersion() - otherVersion.getMinorVersion();
        }
        if (result == 0) {
            result = this.getIncrementalVersion() - otherVersion.getIncrementalVersion();
        }
        if (result == 0) {
            if (this.qualifier != null) {
                final String otherQualifier = otherVersion.getQualifier();
                if (otherQualifier != null) {
                    if (this.qualifier.length() > otherQualifier.length() && this.qualifier.startsWith(otherQualifier)) {
                        result = -1;
                    }
                    else if (this.qualifier.length() < otherQualifier.length() && otherQualifier.startsWith(this.qualifier)) {
                        result = 1;
                    }
                    else {
                        result = this.qualifier.compareTo(otherQualifier);
                    }
                }
                else {
                    result = -1;
                }
            }
            else if (otherVersion.getQualifier() != null) {
                result = 1;
            }
            else {
                result = this.getBuildNumber() - otherVersion.getBuildNumber();
            }
        }
        return result;
    }
    
    public int getMajorVersion() {
        return (this.majorVersion != null) ? this.majorVersion : 0;
    }
    
    public int getMinorVersion() {
        return (this.minorVersion != null) ? this.minorVersion : 0;
    }
    
    public int getIncrementalVersion() {
        return (this.incrementalVersion != null) ? this.incrementalVersion : 0;
    }
    
    public int getBuildNumber() {
        return (this.buildNumber != null) ? this.buildNumber : 0;
    }
    
    public String getQualifier() {
        return this.qualifier;
    }
    
    public final void parseVersion(final String version) {
        this.unparsed = version;
        final int index = version.indexOf("-");
        String part2 = null;
        String part3;
        if (index < 0) {
            part3 = version;
        }
        else {
            part3 = version.substring(0, index);
            part2 = version.substring(index + 1);
        }
        if (part2 != null) {
            try {
                if (part2.length() == 1 || !part2.startsWith("0")) {
                    this.buildNumber = Integer.valueOf(part2);
                }
                else {
                    this.qualifier = part2;
                }
            }
            catch (NumberFormatException e) {
                this.qualifier = part2;
            }
        }
        if (part3.indexOf(".") < 0 && !part3.startsWith("0")) {
            try {
                this.majorVersion = Integer.valueOf(part3);
            }
            catch (NumberFormatException e) {
                this.qualifier = version;
                this.buildNumber = null;
            }
        }
        else {
            boolean fallback = false;
            final StringTokenizer tok = new StringTokenizer(part3, ".");
            try {
                this.majorVersion = getNextIntegerToken(tok);
                if (tok.hasMoreTokens()) {
                    this.minorVersion = getNextIntegerToken(tok);
                }
                if (tok.hasMoreTokens()) {
                    this.incrementalVersion = getNextIntegerToken(tok);
                }
                if (tok.hasMoreTokens()) {
                    fallback = true;
                }
                if (part3.indexOf("..") >= 0 || part3.startsWith(".") || part3.endsWith(".")) {
                    fallback = true;
                }
            }
            catch (NumberFormatException e2) {
                fallback = true;
            }
            if (fallback) {
                this.qualifier = version;
                this.majorVersion = null;
                this.minorVersion = null;
                this.incrementalVersion = null;
                this.buildNumber = null;
            }
        }
    }
    
    private static Integer getNextIntegerToken(final StringTokenizer tok) {
        final String s = tok.nextToken();
        if (s.length() > 1 && s.startsWith("0")) {
            throw new NumberFormatException("Number part has a leading 0: '" + s + "'");
        }
        return Integer.valueOf(s);
    }
    
    @Override
    public String toString() {
        return this.unparsed;
    }
    
    @Override
    public boolean equals(final Object other) {
        return this == other || (other instanceof ArtifactVersion && 0 == this.compareTo(other));
    }
    
    @Override
    public int hashCode() {
        int result = 1229;
        result = 1223 * result + this.getMajorVersion();
        result = 1223 * result + this.getMinorVersion();
        result = 1223 * result + this.getIncrementalVersion();
        result = 1223 * result + this.getBuildNumber();
        if (null != this.getQualifier()) {
            result = 1223 * result + this.getQualifier().hashCode();
        }
        return result;
    }
}
