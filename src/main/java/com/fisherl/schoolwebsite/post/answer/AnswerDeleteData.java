package com.fisherl.schoolwebsite.post.answer;

import com.fisherl.schoolwebsite.post.DeleteData;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.IdClass;
import java.time.Instant;
import java.util.Objects;
import java.util.UUID;

@Entity
@IdClass(AnswerId.class)
public class AnswerDeleteData extends DeleteData {

    @Id
    private UUID questionId;
    @Id
    private long answerId;

    public AnswerDeleteData(String deleterId, Instant deletedAt, UUID questionId, long answerId) {
        super(deleterId, deletedAt);
        this.questionId = questionId;
        this.answerId = answerId;
    }

    public AnswerDeleteData() {
    }

    public UUID getQuestionId() {
        return questionId;
    }

    protected void setQuestionId(UUID questionId) {
        this.questionId = questionId;
    }

    public long getAnswerId() {
        return answerId;
    }

    protected void setAnswerId(long answerId) {
        this.answerId = answerId;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;
        final AnswerDeleteData that = (AnswerDeleteData) o;
        return answerId == that.answerId && Objects.equals(questionId, that.questionId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), questionId, answerId);
    }
}
