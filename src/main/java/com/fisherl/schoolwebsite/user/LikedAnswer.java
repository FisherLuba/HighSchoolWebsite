package com.fisherl.schoolwebsite.user;

import javax.persistence.Embeddable;
import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import java.io.Serializable;
import java.util.Objects;
import java.util.UUID;

@Entity
public class LikedAnswer {

    @EmbeddedId
    private LikedAnswerKey likedAnswerKey;

    public LikedAnswer(String userId, UUID questionId, long answerId) {
        this.likedAnswerKey = new LikedAnswerKey(userId, questionId, answerId);
    }

    public LikedAnswer() {}

    public String getUserId() {
        return this.likedAnswerKey.getUserId();
    }

    public UUID getQuestionId() {
        return this.likedAnswerKey.getQuestionId();
    }

    public long getAnswerId() {
        return this.likedAnswerKey.getAnswerId();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        final LikedAnswer that = (LikedAnswer) o;
        return Objects.equals(likedAnswerKey, that.likedAnswerKey);
    }

    @Override
    public int hashCode() {
        return Objects.hash(likedAnswerKey);
    }

    @Embeddable
    private static class LikedAnswerKey implements Serializable {

        private String userId;
        private UUID questionId;
        private long answerId;

        public LikedAnswerKey(String userId, UUID questionId, long answerId) {
            this.userId = userId;
            this.questionId = questionId;
            this.answerId = answerId;
        }

        protected LikedAnswerKey() {}

        public String getUserId() {
            return userId;
        }

        public UUID getQuestionId() {
            return questionId;
        }

        public long getAnswerId() {
            return answerId;
        }

        protected void setUserId(String userId) {
            this.userId = userId;
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
            final LikedAnswerKey that = (LikedAnswerKey) o;
            return answerId == that.answerId && Objects.equals(userId, that.userId) && Objects.equals(questionId, that.questionId);
        }

        @Override
        public int hashCode() {
            return Objects.hash(userId, questionId, answerId);
        }
    }
}
