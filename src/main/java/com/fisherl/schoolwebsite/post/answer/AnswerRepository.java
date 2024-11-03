package com.fisherl.schoolwebsite.post.answer;

import org.springframework.data.repository.CrudRepository;

import java.util.List;
import java.util.UUID;

public interface AnswerRepository extends CrudRepository<Answer, AnswerId> {

    List<Answer> findByQuestionId(UUID questionId);
    List<Answer> findByQuestionIdAndApprovedByIdIsNotNullOrAuthorIdEquals(UUID questionId, String authorId);
    List<Answer> findByQuestionIdAndDeleteDataIsNull(UUID questionId);
    List<Answer> findByQuestionIdAndDeleteDataIsNullAndApprovedByIdIsNotNullOrAuthorIdEqualsAndDeleteDataIsNull(UUID questionId, String authorId);

}
