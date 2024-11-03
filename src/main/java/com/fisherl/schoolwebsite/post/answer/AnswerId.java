package com.fisherl.schoolwebsite.post.answer;

import java.io.Serial;
import java.io.Serializable;
import java.util.Objects;
import java.util.UUID;

//@Embeddable
public class AnswerId implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    private UUID questionId;
    private long answerId;

    protected AnswerId(UUID questionId, long answerId) {
        this.questionId = questionId;
        this.answerId = answerId;
    }

    public static AnswerId of(UUID questionId, long answerId) {
        return new AnswerId(questionId, answerId);
    }

    protected AnswerId() {}

    public UUID getQuestionId() {
        return questionId;
    }

    public long getAnswerId() {
        return answerId;
    }

    protected void setQuestionId(UUID questionId) {
        this.questionId = questionId;
    }

    protected void setAnswerId(long answerId) {
        this.answerId = answerId;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        final AnswerId answerId1 = (AnswerId) o;
        return answerId == answerId1.answerId && Objects.equals(questionId, answerId1.questionId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(questionId, answerId);
    }

    @Override
    public String toString() {
        return "AnswerId{" +
                "questionId=" + questionId +
                ", answerId=" + answerId +
                '}';
    }
}
