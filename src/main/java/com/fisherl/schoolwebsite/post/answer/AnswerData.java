package com.fisherl.schoolwebsite.post.answer;

import java.util.List;
import java.util.UUID;

public class AnswerData {

    private UUID questionId;
    private String content;
    private boolean anonymous;
    private List<String> tags;

    public AnswerData(UUID questionId, String content, boolean anonymous, List<String> tags) {
        this.questionId = questionId;
        this.content = content;
        this.anonymous = anonymous;
        this.tags = tags;
    }

    protected AnswerData() {}

    public UUID getQuestionId() {
        return questionId;
    }

    public String getContent() {
        return content;
    }

    public boolean isAnonymous() {
        return anonymous;
    }

    public List<String> getTags() {
        return tags;
    }
}
