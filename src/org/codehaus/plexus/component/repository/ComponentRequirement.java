// 
// Decompiled by Procyon v0.5.36
// 

package org.codehaus.plexus.component.repository;

public final class ComponentRequirement
{
    private String role;
    private String roleHint;
    private String fieldName;
    private String fieldMappingType;
    
    public String getFieldName() {
        return this.fieldName;
    }
    
    public void setFieldName(final String fieldName) {
        this.fieldName = fieldName;
    }
    
    public String getRole() {
        return this.role;
    }
    
    public void setRole(final String role) {
        this.role = role;
    }
    
    public String getRoleHint() {
        return this.roleHint;
    }
    
    public void setRoleHint(final String roleHint) {
        this.roleHint = roleHint;
    }
    
    public String getRequirementKey() {
        if (this.getRoleHint() != null) {
            return this.getRole() + this.getRoleHint();
        }
        return this.getRole();
    }
    
    public String toString() {
        return "ComponentRequirement{role='" + this.role + "'" + ", roleHint='" + this.roleHint + "'" + ", fieldName='" + this.fieldName + "'" + "}";
    }
    
    public String getHumanReadableKey() {
        final StringBuffer key = new StringBuffer();
        key.append("role: '");
        key.append(this.getRole());
        key.append("'");
        if (this.getRoleHint() != null) {
            key.append(", role-hint: '");
            key.append(this.getRoleHint());
            key.append("'. ");
        }
        if (this.getFieldName() != null) {
            key.append(", field name: '");
            key.append(this.getFieldName());
            key.append("' ");
        }
        final String retValue = key.toString();
        return retValue;
    }
    
    public String getFieldMappingType() {
        return this.fieldMappingType;
    }
    
    public void setFieldMappingType(final String fieldType) {
        this.fieldMappingType = fieldType;
    }
    
    public boolean equals(final Object other) {
        if (other instanceof ComponentRequirement) {
            final String myId = this.role + ":" + this.roleHint;
            final ComponentRequirement req = (ComponentRequirement)other;
            final String otherId = req.role + ":" + req.roleHint;
            return myId.equals(otherId);
        }
        return false;
    }
    
    public int hashCode() {
        return (this.role + ":" + this.roleHint).hashCode();
    }
}
