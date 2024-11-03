package com.fisherl.schoolwebsite.post.question;

import com.fisherl.schoolwebsite.post.Post;
import com.fisherl.schoolwebsite.user.PostUser;
import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Component;

import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

@Component
public class QuestionManager {

    private final QuestionRepository questionRepository;

    private final Cache<UUID, Question> cache = Caffeine.newBuilder()
            .expireAfterWrite(1, TimeUnit.MINUTES)
            .build();

    @Autowired
    public QuestionManager(QuestionRepository questionRepository) {
        this.questionRepository = questionRepository;
    }

    public void saveQuestion(Question question) {
        this.cache.put(question.getQuestionId(), question);
        this.questionRepository.save(question);
    }

    public void deleteQuestion(Question question) {
        this.questionRepository.delete(question);
    }

    public void deleteQuestion(UUID id) {
        this.questionRepository.deleteById(id);
    }

    public List<Question> getNotDeleted(String userId, String category, int pageNumber, int amount) {
        if (category.equals(Post.RECENT_CATEGORY)) {
            return this.questionRepository.findAllByDeleteDataIsNullAndApprovedByIdIsNotNullOrAuthorIdEqualsAndDeleteDataIsNullOrderByTimePostedDesc(userId, Pageable.ofSize(amount).withPage(pageNumber));
        }
        return this.questionRepository.findByCategoryAndDeleteDataIsNullAndApprovedByIdIsNotNullOrAuthorIdEquals(userId, category, Pageable.ofSize(amount).withPage(pageNumber));
    }

    public List<Question> getNotDeletedAndUnapproved(String category, int pageNumber, int amount) {
        if (category.equals(Post.RECENT_CATEGORY)) {
            return this.questionRepository.findAllByDeleteDataIsNullOrderByTimePostedDesc(Pageable.ofSize(amount).withPage(pageNumber));
        }
        return this.questionRepository.findByCategoryAndDeleteDataIsNull(category, Pageable.ofSize(amount).withPage(pageNumber));
    }

    public List<Question> getNotDeleted(String userId, String category, Collection<String> subtopics, int pageNumber, int amount) {
        if (category.equals(Post.RECENT_CATEGORY)) {
            return this.questionRepository.findAllByDeleteDataIsNullAndApprovedByIdIsNotNullOrAuthorIdEqualsAndDeleteDataIsNullOrderByTimePostedDesc(userId, Pageable.ofSize(amount).withPage(pageNumber));
        }
        if (subtopics.isEmpty()) return this.getNotDeleted(userId, category, pageNumber, amount);
        return this.questionRepository.findDistinctByCategoryAndSubtopicsIsInAndDeleteDataIsNullAndApprovedByIdIsNotNullOrAuthorIdEqualsAndSubtopicsIsInAndDeleteDataIsNullOrderByTimePostedDesc(category, subtopics, userId, subtopics, Pageable.ofSize(amount).withPage(pageNumber));
    }

    public List<Question> getNotDeletedAndUnapproved(String userId, String category, Collection<String> subtopics, int pageNumber, int amount) {
        if (category.equals(Post.RECENT_CATEGORY)) {
            return this.questionRepository.findAllByDeleteDataIsNullOrderByTimePostedDesc(Pageable.ofSize(amount).withPage(pageNumber));
        }
        if (subtopics.isEmpty()) return this.getNotDeleted(userId, category, pageNumber, amount);
        return this.questionRepository.findDistinctByCategoryAndSubtopicsIsInAndDeleteDataIsNullOrderByTimePostedDesc(category, subtopics, Pageable.ofSize(amount).withPage(pageNumber));
    }

    public List<Question> getUnapproved(int pageNumber, int amount) {
        return this.questionRepository.findAllByDeleteDataIsNullAndApprovedByIdIsNullOrderByTimePostedAsc(Pageable.ofSize(amount).withPage(pageNumber));
    }

    public Optional<Question> getQuestion(UUID uuid) {
        final Question question = this.cache.get(uuid, k -> null);
        if (question != null) return Optional.of(question);
        return this.questionRepository.findById(uuid);
    }

    public List<Question> getLikedBy(PostUser postUser) {
        return this.questionRepository.findCategoryByQuestionIdIsIn(postUser.getLikedQuestions());
    }

    private static final String[] SEARCH_FIELDS = new String[]{"title", "content"};

    public List<Question> search(
            String category,
            List<String> subtopics,
            String query,
            boolean approvedOnly,
            int pageNumber,
            int amount
    ) {
        if (approvedOnly) return this.questionRepository.searchByApproved(category, subtopics, query, pageNumber, amount, SEARCH_FIELDS);
        return this.questionRepository.searchBy(category, subtopics, query, pageNumber, amount, SEARCH_FIELDS);
    }

}
