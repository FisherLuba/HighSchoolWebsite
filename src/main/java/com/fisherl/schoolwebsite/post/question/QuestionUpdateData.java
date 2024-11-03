package com.fisherl.schoolwebsite.post.question;

import java.util.List;
import java.util.UUID;

public class QuestionUpdateData {

    private UUID questionId;
    private String content;
    private String title;
    private List<String> subtopics;
    private boolean anonymous;

    public QuestionUpdateData(UUID questionId, String content, String title, List<String> subtopics, boolean anonymous) {
        this.questionId = questionId;
        this.content = content;
        this.title = title;
        this.subtopics = subtopics;
        this.anonymous = anonymous;
    }

    protected QuestionUpdateData() {}

    public UUID getQuestionId() {
        return questionId;
    }

    public String getContent() {
        return content;
    }

    public String getTitle() {
        return title;
    }

    public List<String> getSubtopics() {
        return subtopics;
    }

    public boolean isAnonymous() {
        return anonymous;
    }
}
