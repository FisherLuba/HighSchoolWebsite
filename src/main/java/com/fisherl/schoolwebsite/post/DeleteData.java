package com.fisherl.schoolwebsite.post;

import javax.persistence.MappedSuperclass;
import java.time.Instant;
import java.util.Objects;

@MappedSuperclass
public abstract class DeleteData {

    private String deleterId;
    private Instant deletedAt;

    public DeleteData(String deleterId, Instant deletedAt) {
        this.deleterId = deleterId;
        this.deletedAt = deletedAt;
    }

    public DeleteData() {
    }

    public String getDeleterId() {
        return deleterId;
    }

    public Instant getDeletedAt() {
        return deletedAt;
    }

    protected void setDeleterId(String deleterId) {
        this.deleterId = deleterId;
    }

    protected void setDeletedAt(Instant deletedAt) {
        this.deletedAt = deletedAt;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        final DeleteData that = (DeleteData) o;
        return Objects.equals(deleterId, that.deleterId) && Objects.equals(deletedAt, that.deletedAt);
    }

    @Override
    public int hashCode() {
        return Objects.hash(deleterId, deletedAt);
    }
}
