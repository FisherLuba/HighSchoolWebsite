package com.fisherl.schoolwebsite.post.answer;

import com.fisherl.schoolwebsite.user.LikedAnswer;
import com.fisherl.schoolwebsite.user.PostUser;
import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

@Component
public class AnswerManager {

    private final AnswerRepository answerRepository;

    private final Cache<AnswerId, Answer> cache = Caffeine.newBuilder()
            .expireAfterWrite(1, TimeUnit.MINUTES)
            .build();

    @Autowired
    public AnswerManager(AnswerRepository answerRepository) {
        this.answerRepository = answerRepository;
    }

    public void saveAnswer(Answer answer) {
        this.cache.put(AnswerId.of(answer.getQuestionId(), answer.getAnswerId()), answer);
        this.answerRepository.save(answer);
    }

//    public void addQuestion(Question question) {
//        this.
//    }

    public void deleteAnswer(Answer answer) {
        this.answerRepository.delete(answer);
    }

    public void deleteAnswer(AnswerId answerId) {
        this.answerRepository.deleteById(answerId);
    }

    public List<Answer> getAnswers() {
        return StreamSupport.stream(this.answerRepository.findAll().spliterator(), false).toList();
    }

    public List<Answer> getLikedBy(PostUser postUser, UUID questionId) {
        return this.answerRepository.findByQuestionId(questionId).
                stream().
                filter(answer -> postUser.getLikedAnswers().stream().map(LikedAnswer::getAnswerId).collect(Collectors.toSet()).contains(answer.getAnswerId())).
                toList();
    }

    public List<Answer> getNotDeleted(UUID questionId, String authorId) {
        return this.answerRepository.findByQuestionIdAndDeleteDataIsNullAndApprovedByIdIsNotNullOrAuthorIdEqualsAndDeleteDataIsNull(questionId, authorId);
    }

    public List<Answer> getNotDeletedAndUnapproved(UUID questionId) {
        return this.answerRepository.findByQuestionIdAndDeleteDataIsNull(questionId);
    }


    public Optional<Answer> getAnswer(AnswerId answerId) {
        final Answer answer = this.cache.get(answerId, k -> null);
        if (answer != null) return Optional.of(answer);
        return this.answerRepository.findById(answerId);
    }

    public AnswerRepository getAnswerRepository() {
        return answerRepository;
    }
}
