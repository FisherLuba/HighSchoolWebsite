package com.fisherl.schoolwebsite.post.answer;

import com.fisherl.schoolwebsite.post.Post;
import org.springframework.lang.Nullable;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.IdClass;
import java.time.Instant;
import java.util.List;
import java.util.UUID;

@Entity
@IdClass(AnswerId.class)
public class Answer extends Post<AnswerDeleteData> {

    @Id
    private UUID questionId;
    //    @GeneratedValue(strategy = GenerationType.AUTO)
    @Id
    @GeneratedValue
    private long answerId;

    public Answer(
            String authorId,
            String content,
            Instant timePosted,
            boolean anonymous,
            int likes,
            List<String> tags,
            @Nullable String approvedById,
            @Nullable AnswerDeleteData deleteData,
            UUID questionId,
            long answerId
    ) {
        super(authorId, content, timePosted, anonymous, likes, tags, approvedById, deleteData);
        this.questionId = questionId;
        this.answerId = answerId;
    }

    public Answer(
            String authorId,
            String content,
            Instant timePosted,
            boolean anonymous,
            int likes,
            List<String> tags,
            UUID questionId,
            long answerId
    ) {
        super(authorId, content, timePosted, anonymous, likes, tags);
        this.questionId = questionId;
        this.answerId = answerId;
    }

    public static Answer newAnswer(
            String authorId,
            String content,
            Instant timePosted,
            boolean anonymous,
            List<String> tags,
            UUID questionId
    ) {
        return new Answer(authorId, content, timePosted, anonymous, 0, tags, questionId, 0);
    }

    protected Answer() {
    }

    public void update(AnswerUpdateData data) {
        this.content = data.getContent();
        this.anonymous = data.isAnonymous();
    }

    public UUID getQuestionId() {
        return questionId;
    }

    public long getAnswerId() {
        return answerId;
    }

    @Override
    public void delete(String authorId) {
        this.setDeleteData(new AnswerDeleteData(authorId, Instant.now(), this.questionId, this.answerId));
    }

    @Override
    public String toString() {
        return "Answer{" +
                "post=" + super.toString() +
                ", questionId=" + this.questionId +
                ", answerId=" + this.answerId +
                '}';
    }
}
