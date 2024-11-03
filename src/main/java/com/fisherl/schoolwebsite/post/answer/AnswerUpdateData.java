package com.fisherl.schoolwebsite.post.answer;

import java.util.Objects;
import java.util.UUID;

public class AnswerUpdateData {

    private UUID questionId;
    private long answerId;
    private String content;
    private boolean anonymous;

    public AnswerUpdateData(UUID questionId, long answerId, String content, boolean anonymous) {
        this.questionId = questionId;
        this.answerId = answerId;
        this.content = content;
        this.anonymous = anonymous;
    }

    protected AnswerUpdateData() {}

    public UUID getQuestionId() {
        return questionId;
    }

    public long getAnswerId() {
        return answerId;
    }

    public String getContent() {
        return content;
    }

    public boolean isAnonymous() {
        return anonymous;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        final AnswerUpdateData that = (AnswerUpdateData) o;
        return answerId == that.answerId && anonymous == that.anonymous && Objects.equals(questionId, that.questionId) && Objects.equals(content, that.content);
    }

    @Override
    public int hashCode() {
        return Objects.hash(questionId, answerId, content, anonymous);
    }
}
