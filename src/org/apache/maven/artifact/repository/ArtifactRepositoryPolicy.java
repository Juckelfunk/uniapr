// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.maven.artifact.repository;

import java.util.Calendar;
import java.util.Date;

public class ArtifactRepositoryPolicy
{
    public static final String UPDATE_POLICY_NEVER = "never";
    public static final String UPDATE_POLICY_ALWAYS = "always";
    public static final String UPDATE_POLICY_DAILY = "daily";
    public static final String UPDATE_POLICY_INTERVAL = "interval";
    public static final String CHECKSUM_POLICY_FAIL = "fail";
    public static final String CHECKSUM_POLICY_WARN = "warn";
    public static final String CHECKSUM_POLICY_IGNORE = "ignore";
    private boolean enabled;
    private String updatePolicy;
    private String checksumPolicy;
    
    public ArtifactRepositoryPolicy() {
        this(true, null, null);
    }
    
    public ArtifactRepositoryPolicy(final boolean enabled, String updatePolicy, String checksumPolicy) {
        this.enabled = enabled;
        if (updatePolicy == null) {
            updatePolicy = "daily";
        }
        this.updatePolicy = updatePolicy;
        if (checksumPolicy == null) {
            checksumPolicy = "warn";
        }
        this.checksumPolicy = checksumPolicy;
    }
    
    public void setEnabled(final boolean enabled) {
        this.enabled = enabled;
    }
    
    public void setUpdatePolicy(final String updatePolicy) {
        this.updatePolicy = updatePolicy;
    }
    
    public void setChecksumPolicy(final String checksumPolicy) {
        this.checksumPolicy = checksumPolicy;
    }
    
    public boolean isEnabled() {
        return this.enabled;
    }
    
    public String getUpdatePolicy() {
        return this.updatePolicy;
    }
    
    public String getChecksumPolicy() {
        return this.checksumPolicy;
    }
    
    public boolean checkOutOfDate(final Date lastModified) {
        boolean checkForUpdates = false;
        if ("always".equals(this.updatePolicy)) {
            checkForUpdates = true;
        }
        else if ("daily".equals(this.updatePolicy)) {
            final Calendar cal = Calendar.getInstance();
            cal.set(11, 0);
            cal.set(12, 0);
            cal.set(13, 0);
            cal.set(14, 0);
            if (cal.getTime().after(lastModified)) {
                checkForUpdates = true;
            }
        }
        else if (this.updatePolicy.startsWith("interval")) {
            final String s = this.updatePolicy.substring("interval".length() + 1);
            final int minutes = Integer.valueOf(s);
            final Calendar cal2 = Calendar.getInstance();
            cal2.add(12, -minutes);
            if (cal2.getTime().after(lastModified)) {
                checkForUpdates = true;
            }
        }
        return checkForUpdates;
    }
}
