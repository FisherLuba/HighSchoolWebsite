package com.fisherl.schoolwebsite.post.category;

import java.util.Collection;
import java.util.LinkedHashSet;

public class Category {

    private final LinkedHashSet<String> subtopics;

    protected Category(LinkedHashSet<String> subtopics) {
        this.subtopics = subtopics;
    }

    public LinkedHashSet<String> getSubtopics() {
        return subtopics;
    }

    public void addSubtopics(Collection<String> subtopics) {
        this.subtopics.addAll(subtopics);
    }

    public void setSubtopics(Collection<String> subtopics) {
        this.subtopics.clear();
        this.subtopics.addAll(subtopics);
    }

    @Override
    public String toString() {
        return "Category{" +
                "subtopics=" + subtopics +
                '}';
    }
}
