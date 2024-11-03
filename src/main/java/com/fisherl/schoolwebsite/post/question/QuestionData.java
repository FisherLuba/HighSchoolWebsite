package com.fisherl.schoolwebsite.post.question;

import javax.persistence.ElementCollection;
import java.util.List;

public class QuestionData {

    private String title;
    private String content;
    private String category;
    private boolean anonymous;
    private List<String> subtopics;
    private List<String> tags;

    public QuestionData(String title, String content, String category, boolean anonymous, List<String> subtopics, List<String> tags) {
        this.title = title;
        this.content = content;
        this.category = category;
        this.anonymous = anonymous;
        this.subtopics = subtopics;
        this.tags = tags;
    }

    protected QuestionData() {}

    public String getTitle() {
        return title;
    }

    public String getContent() {
        return content;
    }

    public String getCategory() {
        return category;
    }

    public boolean isAnonymous() {
        return anonymous;
    }

    public List<String> getSubtopics() {
        return subtopics;
    }

    public List<String> getTags() {
        return tags;
    }

    @Override
    public String toString() {
        return "QuestionData{" +
                "title='" + title + '\'' +
                ", content='" + content + '\'' +
                ", category='" + category + '\'' +
                ", subtopics=" + subtopics +
                ", tags=" + tags +
                '}';
    }
}
