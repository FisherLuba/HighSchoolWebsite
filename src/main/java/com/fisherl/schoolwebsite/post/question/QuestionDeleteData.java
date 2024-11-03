package com.fisherl.schoolwebsite.post.question;

import com.fisherl.schoolwebsite.post.DeleteData;

import javax.persistence.Entity;
import javax.persistence.Id;
import java.time.Instant;
import java.util.Objects;
import java.util.UUID;

@Entity
public class QuestionDeleteData extends DeleteData {

    @Id
    private UUID questionId;

    public QuestionDeleteData(String deleterId, Instant deletedAt, UUID questionId) {
        super(deleterId, deletedAt);
        this.questionId = questionId;
    }

    public QuestionDeleteData() {}

    public UUID getQuestionId() {
        return questionId;
    }

    protected void setQuestionId(UUID questionId) {
        this.questionId = questionId;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;
        final QuestionDeleteData that = (QuestionDeleteData) o;
        return Objects.equals(questionId, that.questionId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), questionId);
    }
}
