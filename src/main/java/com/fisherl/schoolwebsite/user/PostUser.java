package com.fisherl.schoolwebsite.user;

import com.fisherl.schoolwebsite.post.Post;
import com.fisherl.schoolwebsite.post.answer.Answer;
import com.fisherl.schoolwebsite.permission.Permissible;
import com.fisherl.schoolwebsite.permission.Permission;
import com.fisherl.schoolwebsite.post.question.Question;
import org.hibernate.annotations.Immutable;
import org.springframework.lang.Nullable;

import javax.persistence.CascadeType;
import javax.persistence.ElementCollection;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import java.util.Collections;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

@Entity
public class PostUser implements Permissible {

    @Id
    private String id;
    private String name;
    private String email;
    @ElementCollection(fetch = FetchType.EAGER)
    private Map<String, Boolean> permissions;
    @ElementCollection(fetch = FetchType.EAGER)
    private Set<UUID> likedQuestions;

    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.EAGER)
    private Set<LikedAnswer> likedAnswers;

    public PostUser(String id, String name, String email, Set<LikedAnswer> likedAnswers, Set<UUID> likedQuestions, Map<String, Boolean> permissions) {
        this.id = id;
        this.name = name;
        this.email = email;
        this.likedQuestions = likedQuestions;
        this.likedAnswers = likedAnswers;
        this.permissions = permissions;
    }

    protected PostUser() {
    }

    @Immutable
    public static PostUser withPermissions(String id, Map<String, Boolean> permissions) {
        return new PostUser(id, null, null, Collections.emptySet(), Collections.emptySet(), permissions);
    }

    @Override
    public void updatePermissions(Map<String, Boolean> permissions) {
        this.permissions.putAll(permissions);
    }

    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getEmail() {
        return email;
    }

    public Set<UUID> getLikedQuestions() {
        return likedQuestions;
    }

    public Set<LikedAnswer> getLikedAnswers() {
        return likedAnswers;
    }

    public Map<String, Boolean> getPermissions(@Nullable Post<?> post) {
        return this.permissions.keySet().stream().
                map(key -> {
                    final Permission permission = Permission.valueOf(key);
                    return Map.entry(key.toLowerCase(), post == null ? this.permissions.get(key) : permission.hasPermission(this, post));
                }).collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));
    }

    public boolean hasSetPermission(Permission permission) {
        return this.hasSetPermission(permission.toString());
    }

    public boolean hasSetPermission(String permission) {
        return this.permissions.containsKey(permission);
    }

    public void setPermission(String permission, boolean value) {
        this.permissions.put(permission, value);
    }

    public void addLikedAnswer(Answer answer) {
        this.likedAnswers.add(new LikedAnswer(this.id, answer.getQuestionId(), answer.getAnswerId()));
    }

    public void removeLikedAnswer(Answer answer) {
        this.likedAnswers.remove(new LikedAnswer(this.id, answer.getQuestionId(), answer.getAnswerId()));
    }

    public boolean hasLikedAnswer(Answer answer) {
        return this.likedAnswers.contains(new LikedAnswer(this.id, answer.getQuestionId(), answer.getAnswerId()));
    }

    public void addLikedQuestion(Question question) {
        this.addLikedQuestion(question.getQuestionId());
    }

    public void addLikedQuestion(UUID questionId) {
        this.likedQuestions.add(questionId);
    }

    public void removeLikedQuestion(Question question) {
        this.removeLikedQuestion(question.getQuestionId());
    }

    public void removeLikedQuestion(UUID questionId) {
        this.likedQuestions.remove(questionId);
    }

    public boolean hasLikedQuestion(Question question) {
        return this.hasLikedQuestion(question.getQuestionId());
    }

    public boolean hasLikedQuestion(UUID questionId) {
        return this.likedQuestions.contains(questionId);
    }

    @Override
    public boolean hasPermission(Permission permission) {
        return this.isAdministrator() || this.permissions.getOrDefault(permission.toString(), false);
    }

    public boolean isAdministrator() {
        return this.permissions.getOrDefault(Permission.ADMINISTRATOR.toString(), false);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        final PostUser postUser = (PostUser) o;
        return Objects.equals(id, postUser.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    @Override
    public String toString() {
        return "PostUser{" +
                "id='" + id + '\'' +
                ", name='" + name + '\'' +
                ", permissions=" + permissions +
                ", likedAnswers=" + likedAnswers +
                '}';
    }
}
