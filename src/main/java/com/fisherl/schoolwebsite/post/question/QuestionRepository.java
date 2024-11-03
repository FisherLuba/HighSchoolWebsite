package com.fisherl.schoolwebsite.post.question;

import com.fisherl.schoolwebsite.util.SearchRepository;
import org.springframework.data.domain.Pageable;

import java.util.Collection;
import java.util.List;
import java.util.UUID;

public interface QuestionRepository extends SearchRepository<Question, UUID> {


    List<Question> findDistinctByCategoryAndSubtopicsIsInAndDeleteDataIsNullOrderByTimePostedDesc(String category, Collection<String> tag, Pageable pageable);
    List<Question> findDistinctByCategoryAndSubtopicsIsInAndDeleteDataIsNullAndApprovedByIdIsNotNullOrAuthorIdEqualsAndSubtopicsIsInAndDeleteDataIsNullOrderByTimePostedDesc(String category, Collection<String> tags, String authorId, Collection<String> subtopics, Pageable pageable);
    List<Question> findAllByDeleteDataIsNullAndApprovedByIdIsNullOrderByTimePostedAsc(Pageable pageable);
    List<Question> findByCategoryAndDeleteDataIsNull(String category, Pageable pageable);
    List<Question> findByCategoryAndDeleteDataIsNullAndApprovedByIdIsNotNullOrAuthorIdEquals(String category, String authorId, Pageable pageable);
    List<Question> findCategoryByQuestionIdIsIn(Collection<UUID> questionIds);
    List<Question> findAllByOrderByTimePostedDesc(Pageable pageable);
    List<Question> findAllByDeleteDataIsNullOrderByTimePostedDesc(Pageable pageable);
    List<Question> findAllByDeleteDataIsNullAndApprovedByIdIsNotNullOrAuthorIdEqualsAndDeleteDataIsNullOrderByTimePostedDesc(String authorId, Pageable pageable);

}
