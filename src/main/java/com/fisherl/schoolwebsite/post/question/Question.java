package com.fisherl.schoolwebsite.post.question;

import com.fisherl.schoolwebsite.post.Post;
import org.hibernate.search.mapper.pojo.mapping.definition.annotation.FullTextField;
import org.hibernate.search.mapper.pojo.mapping.definition.annotation.GenericField;
import org.hibernate.search.mapper.pojo.mapping.definition.annotation.Indexed;
import org.springframework.lang.Nullable;

import javax.persistence.ElementCollection;
import javax.persistence.Entity;
import javax.persistence.Id;
import java.time.Instant;
import java.util.List;
import java.util.UUID;

@Indexed
@Entity
public class Question extends Post<QuestionDeleteData> {

    @Id
    private UUID questionId;
    @FullTextField
    private String title;
    @GenericField
    private String category;
    @GenericField
    @ElementCollection
    private List<String> subtopics;

    public Question(
            String authorId,
            String content,
            Instant timePosted,
            boolean anonymous,
            int likes,
            List<String> tags,
            @Nullable String approvedById,
            @Nullable QuestionDeleteData deleteData,
            UUID questionId,
            String title,
            String category,
            List<String> subtopics
    ) {
        super(authorId, content, timePosted, anonymous, likes, tags, approvedById, deleteData);
        this.questionId = questionId;
        this.title = title;
        this.category = category;
        this.subtopics = subtopics;
    }

    public Question(
            String authorId,
            String content,
            Instant timePosted,
            boolean anonymous,
            int likes,
            List<String> tags,
            UUID questionId,
            String title,
            String category,
            List<String> subtopics
    ) {
        super(authorId, content, timePosted, anonymous, likes, tags);
        this.questionId = questionId;
        this.title = title;
        this.category = category;
        this.subtopics = subtopics;
    }

    public static Question newQuestion(
            String authorId,
            String content,
            Instant timePosted,
            boolean anonymous,
            List<String> tags,
            UUID questionId,
            String title,
            String category,
            List<String> subtopics
    ) {
        return new Question(authorId, content, timePosted, anonymous, 0, tags, questionId, title, category, subtopics);
    }

    protected Question() {}

    public void update(QuestionUpdateData data) {
        this.content = data.getContent();
        this.title = data.getTitle();
        this.anonymous = data.isAnonymous();
        this.subtopics = data.getSubtopics();
    }

    public UUID getQuestionId() {
        return questionId;
    }

    public String getTitle() {
        return title;
    }

    public String getCategory() {
        return category;
    }

    public List<String> getSubtopics() {
        return subtopics;
    }

    @Override
    public void delete(String authorId) {
        this.setDeleteData(new QuestionDeleteData(authorId, Instant.now(), this.questionId));
    }

    @Override
    public String toString() {
        return "Question{" +
                "post=" + super.toString() +
                ", questionId=" + questionId +
                ", title='" + title + '\'' +
                ", category='" + category + '\'' +
                ", subtopics=" + subtopics +
                '}';
    }
}
