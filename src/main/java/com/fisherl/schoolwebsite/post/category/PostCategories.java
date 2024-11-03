package com.fisherl.schoolwebsite.post.category;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.springframework.boot.json.JsonParser;
import org.springframework.boot.json.JsonParserFactory;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

@Component
public class PostCategories {

    private static final Path FILE_PATH = Path.of("categories.json");

    private Map<String, Category> categoryMap;

    protected PostCategories() {
    }

    @PostConstruct
    protected void load() {
        final File file = FILE_PATH.toFile();
        try {
            if (!file.exists()) file.createNewFile();
            final JsonParser parser = JsonParserFactory.getJsonParser();
            final String json = Files.readString(FILE_PATH);
            if (json.isBlank()) {
                this.categoryMap = new HashMap<>();
                this.addDefaultCategory("art", new ArrayList<>());
                this.addDefaultCategory("business", new ArrayList<>());
                this.addDefaultCategory("english", new ArrayList<>());
                this.addDefaultCategory("history", new ArrayList<>());
                this.addDefaultCategory("math", new ArrayList<>());
                this.addDefaultCategory("music", new ArrayList<>());
                this.addDefaultCategory("science", new ArrayList<>());
                this.save();
                return;
            }
            final ObjectMapper mapper = new ObjectMapper();
            this.categoryMap = parser.parseMap(json).
                    entrySet().
                    stream().
                    map(entry -> {
                        try {
                            final List<String> list = mapper.readValue((String) entry.getValue(), List.class);
                            final LinkedHashSet<String> subtopics = list.stream().
                                    filter(Objects::nonNull).
                                    map(String::valueOf).
                                    collect(Collectors.toCollection(LinkedHashSet::new));

                            return Map.entry(entry.getKey(), new Category(subtopics));
                        } catch (JsonProcessingException e) {
                            return null;
                        }
                    }).filter(entry -> entry != null).
                    collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));
            this.save();
        } catch (IOException e) {
            throw new RuntimeException("Could not load categories");
        }
    }

    private void addDefaultCategory(String categoryName, List<String> subtopics) {
        final Category category = new Category(new LinkedHashSet<>());
        category.addSubtopics(subtopics);
        this.categoryMap.put(categoryName, category);
    }

    public void save() {
        final ObjectMapper mapper = new ObjectMapper();
        final ObjectNode objectNode = mapper.createObjectNode();

        for (var entry : this.categoryMap.entrySet()) {
            final ArrayNode subtopicsNode = mapper.createArrayNode();
            for (var subtopic : entry.getValue().getSubtopics()) {
                subtopicsNode.add(subtopic);
            }
            objectNode.put(entry.getKey(), subtopicsNode.toPrettyString());
        }
        try {
            Files.writeString(FILE_PATH, objectNode.toPrettyString());
        } catch (IOException e) {
            throw new RuntimeException("Could not save categories");
        }
    }

    public List<String> getCategories() {
        return this.categoryMap.keySet().stream().toList();
    }

    public void deleteCategory(String category) {
         this.categoryMap.remove(category);
         this.save();
    }

    public LinkedHashSet<String> getSubtopics(String category) {
        return Optional.ofNullable(this.categoryMap.get(category)).
                map(Category::getSubtopics).
                orElse(new LinkedHashSet<>());
    }

    public void addSubtopics(String category, List<String> subtopics) {
        this.categoryMap.
                computeIfAbsent(category, k -> new Category(new LinkedHashSet<>())).
                addSubtopics(subtopics);
        this.save();
    }

    public void setSubtopics(String category, List<String> subtopics) {
        this.categoryMap.
                computeIfAbsent(category, k -> new Category(new LinkedHashSet<>())).
                setSubtopics(subtopics);
        this.save();
    }

}
