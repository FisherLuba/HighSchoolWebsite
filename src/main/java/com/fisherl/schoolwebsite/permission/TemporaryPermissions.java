package com.fisherl.schoolwebsite.permission;

import javax.persistence.ElementCollection;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Id;
import java.util.Map;
import java.util.Objects;

@Entity
public class TemporaryPermissions {

    @Id
    private String email;
    @ElementCollection(fetch = FetchType.EAGER)
    private Map<String, Boolean> permissions;

    public TemporaryPermissions(String email, Map<String, Boolean> permissions) {
        this.email = email;
        this.permissions = permissions;
    }

    public static TemporaryPermissions of(String email, Map<String, Boolean> permissions) {
        return new TemporaryPermissions(email, permissions);
    }

    protected TemporaryPermissions() {}

    public String getEmail() {
        return email;
    }

    protected void setEmail(String email) {
        this.email = email;
    }

    public Map<String, Boolean> getPermissions() {
        return permissions;
    }

    protected void setPermissions(Map<String, Boolean> permissions) {
        this.permissions = permissions;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        final TemporaryPermissions that = (TemporaryPermissions) o;
        return Objects.equals(email, that.email) && Objects.equals(permissions, that.permissions);
    }

    @Override
    public int hashCode() {
        return Objects.hash(email, permissions);
    }
}
