package com.fisherl.schoolwebsite.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fisherl.schoolwebsite.SchoolWebsiteApplication;
import com.fisherl.schoolwebsite.permission.Permission;
import com.fisherl.schoolwebsite.post.LikeAction;
import com.fisherl.schoolwebsite.post.Post;
import com.fisherl.schoolwebsite.post.answer.*;
import com.fisherl.schoolwebsite.post.category.PostCategories;
import com.fisherl.schoolwebsite.post.question.Question;
import com.fisherl.schoolwebsite.post.question.QuestionData;
import com.fisherl.schoolwebsite.post.question.QuestionManager;
import com.fisherl.schoolwebsite.post.question.QuestionUpdateData;
import com.fisherl.schoolwebsite.user.PostUser;
import com.fisherl.schoolwebsite.user.UserManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.lang.Nullable;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.time.Instant;
import java.util.*;

@Controller
public class ContentController {

    private final UserManager userManager;
    private final QuestionManager questionManager;
    private final AnswerManager answerManager;
    private final PostCategories categories;

    @Autowired
    public ContentController(
            UserManager userManager,
            QuestionManager questionManager,
            AnswerManager answerManager,
            PostCategories categories
    ) {
        this.userManager = userManager;
        this.questionManager = questionManager;
        this.answerManager = answerManager;
        this.categories = categories;
    }

    @GetMapping("/select-category")
    public String selectCategory() {
        return "select-category";
    }

    @GetMapping("/edit-categories")
    public String editCategories(/*@AuthenticationPrincipal OAuth2User principal*/) {
        if (!this.userManager.hasPermission(/*principal,*/Permission.EDIT_CATEGORIES, null)) {
            return "index";
        }
        return "edit-categories";
    }

    @GetMapping("/submit-question")
    public String submitQuestion(/*/*@AuthenticationPrincipal OAuth2User principal,*/ @Nullable @RequestParam("id") UUID questionId) {
        if (questionId == null) return "submit-question";
        return this.questionManager.getQuestion(questionId).map(question -> {
            if (!this.userManager.hasPermission(/*principal,*/
                    Permission.EDIT_SELF_QUESTION, question)) {
                return "error";
            }
            return "submit-question";
        }).orElse("error");
    }

    @GetMapping("/view-category")
    public String viewCategory() {
        return "view-category";
    }

    @GetMapping("/view-question")
    public String viewQuestion() {
        return "view-question";
    }

    @GetMapping("/edit-permissions")
    public String editPermissions(/*@AuthenticationPrincipal OAuth2User principal*/) {
        if (!this.userManager.hasPermission(/*principal,*/
                Permission.EDIT_USER_PERMISSIONS, null)) {
            return "index";
        }
        return "edit-permissions";
    }

    @PostMapping("/api/categories")
    @ResponseBody
    public List<String> categories(/*@AuthenticationPrincipal OAuth2User principal*/) {
        return this.categories.getCategories();
    }

    @PostMapping(value = "/api/question-data", produces = "application/json; charset=UTF-8")
    @ResponseBody
    public ResponseEntity<List<Question>> getQuestions(
            /*@AuthenticationPrincipal OAuth2User principal,*/
            @RequestParam(defaultValue = "recent") String category,
            @Nullable @RequestParam("subtopics") String subtopicsString,
            @RequestParam int totalQuestions,
            @RequestParam int pageNumber
    ) {
        final boolean canSeeUnapproved = this.userManager.hasPermission(/*principal,*/Permission.APPROVE_POST, null);
//        final String userId = this.userManager.getPrincipalId(/*principal*/);
        final String userId = SchoolWebsiteApplication.LOCAL_USER_ID;
        if (Post.UNAPPROVED_CATEGORY.equals(category)) {
            if (!canSeeUnapproved) return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
            return ResponseEntity.ok(this.questionManager.getUnapproved(pageNumber, totalQuestions));
        }
        if (subtopicsString == null) {
            if (canSeeUnapproved)
                return ResponseEntity.ok(this.questionManager.getNotDeletedAndUnapproved(category, pageNumber, totalQuestions));
            return ResponseEntity.ok(this.questionManager.getNotDeleted(userId, category, pageNumber, totalQuestions));
        }
        final List<String> subtopics = Arrays.asList(subtopicsString.split(","));
        if (canSeeUnapproved)
            return ResponseEntity.ok(this.questionManager.getNotDeletedAndUnapproved(userId, category, subtopics, pageNumber, totalQuestions));
        return ResponseEntity.ok(this.questionManager.getNotDeleted(userId, category, subtopics, pageNumber, totalQuestions));
    }

    @PostMapping(value = "/api/question-data/search", produces = "application/json; charset=UTF-8")
    @ResponseBody
    public List<Question> searchQuestions(
            /*@AuthenticationPrincipal OAuth2User principal,*/
            @RequestParam(defaultValue = "recent") String category,
            @Nullable @RequestParam String subtopics,
            @RequestParam String query,
            @RequestParam int totalQuestions,
            @RequestParam int pageNumber
    ) {
        final boolean canSeeUnapproved = this.userManager.hasPermission(/*principal,*/Permission.APPROVE_POST, null);
        final List<String> subtopicsList = subtopics == null ? Collections.emptyList() : Arrays.asList(subtopics.split(","));
        return this.questionManager.search(category, subtopicsList, query, !canSeeUnapproved, pageNumber, totalQuestions);
    }


    @PostMapping("/api/question/new")
    @ResponseBody
    public ResponseEntity<String> newQuestion(/*@AuthenticationPrincipal OAuth2User principal,*/ @RequestBody QuestionData questionData) {
        final UUID uuid = UUID.randomUUID();
        final Question question = Question.newQuestion(
                this.userManager.getPrincipalId(/*principal*/),
                questionData.getContent(),
                Instant.now(),
                questionData.isAnonymous(),
                questionData.getTags(),
                uuid,
                questionData.getTitle(),
                questionData.getCategory(),
                questionData.getSubtopics()
        );
        if (!this.userManager.hasPermission(/*principal,*/Permission.POST_QUESTION, question)) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("No permission to post questions");
        }
        this.questionManager.saveQuestion(question);
        this.userManager.getOrCreateUser(/*principal*/);
        return ResponseEntity.ok().body("{" +
                "\"id\": \"" + uuid + "\"" +
                "}");
    }

    @PostMapping(value = "/api/answer/new", produces = "application/json; charset=UTF-8")
    @ResponseBody
    public ResponseEntity<AnswerId> newAnswer(/*@AuthenticationPrincipal OAuth2User principal,*/ @RequestBody AnswerData answerData) {
        final String id = this.userManager.getPrincipalId(/*principal*/);
        final Answer answer = Answer.newAnswer(
                id,
                answerData.getContent(),
                Instant.now(),
                answerData.isAnonymous(),
                answerData.getTags(),
                answerData.getQuestionId()
        );
        if (!this.userManager.hasPermission(this.userManager.getOrCreateUser(/*principal*/), Permission.POST_ANSWER, answer)) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(null);
        }
        this.answerManager.saveAnswer(answer);
        return ResponseEntity.ok().body(AnswerId.of(answer.getQuestionId(), answer.getAnswerId()));
    }

    @PostMapping(value = "/api/answer/update", produces = "text/plain")
    @ResponseBody
    public ResponseEntity<String> updateAnswer(/*@AuthenticationPrincipal OAuth2User principal,*/ @RequestBody AnswerUpdateData updateData) {
        final AnswerId answerId = AnswerId.of(updateData.getQuestionId(), updateData.getAnswerId());
        return this.answerManager.getAnswer(answerId).map(answer -> {
            if (!this.userManager.hasPermission(/*principal,*/Permission.EDIT_SELF_ANSWER, answer)) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body((String) null);
            }
            answer.update(updateData);
            this.answerManager.saveAnswer(answer);
            return ResponseEntity.ok().body("true");
        }).orElse(ResponseEntity.status(HttpStatus.NOT_FOUND).body(null));
    }

    @PostMapping(value = "/api/question/update", produces = "text/plain")
    @ResponseBody
    public ResponseEntity<String> updateQuestion(/*@AuthenticationPrincipal OAuth2User principal,*/ @RequestBody QuestionUpdateData updateData) {
        return this.questionManager.getQuestion(updateData.getQuestionId()).map(question -> {
            if (!this.userManager.hasPermission(/*principal,*/Permission.EDIT_SELF_QUESTION, question)) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body((String) null);
            }
            question.update(updateData);
            this.questionManager.saveQuestion(question);
            return ResponseEntity.ok().body("true");
        }).orElse(ResponseEntity.status(HttpStatus.NOT_FOUND).body(null));
    }

    @PostMapping(value = "/api/question", produces = "application/json; charset=UTF-8")
    @ResponseBody
    public Question getQuestion(/*@AuthenticationPrincipal OAuth2User principal,*/ @RequestParam Map<String, String> params) throws JsonProcessingException {
        final String id = params.get("id");
        try {
            return this.questionManager.getQuestion(UUID.fromString(id)).orElse(null);
        } catch (IllegalArgumentException e) {
            return null;
        }
    }

    /**
     * //     * @param principal
     *
     * @param category
     * @param subtopics String of subtopics separated by ","
     * @return
     */
    @PostMapping(value = "/api/category/edit", produces = MediaType.TEXT_PLAIN_VALUE)
    @ResponseBody
    public ResponseEntity<String> editCategory(
            /*@AuthenticationPrincipal OAuth2User principal,*/
            @RequestParam String category,
            @Nullable @RequestParam String subtopics
    ) {
        if (!this.userManager.hasPermission(/*principal,*/Permission.EDIT_CATEGORIES, null)) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
        if (subtopics == null) subtopics = "";
        this.categories.setSubtopics(category, Arrays.stream(subtopics.toLowerCase().split(",")).toList());
        return ResponseEntity.ok().build();
    }

    @PostMapping(value = "/api/category/delete", produces = MediaType.TEXT_PLAIN_VALUE)
    @ResponseBody
    public ResponseEntity<String> deleteCategory(
            /*@AuthenticationPrincipal OAuth2User principal,*/
            @RequestParam String category
    ) {
        if (!this.userManager.hasPermission(/*principal,*/Permission.EDIT_CATEGORIES, null)) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
        this.categories.deleteCategory(category);
        return ResponseEntity.ok().build();
    }

    @PostMapping(value = "/api/category/subtopics", produces = "application/json; charset=UTF-8")
    @ResponseBody
    public List<String> getTopics(/*@AuthenticationPrincipal OAuth2User principal,*/ @RequestParam(defaultValue = "recent") String category) {
        return this.categories.getSubtopics(category).stream().toList();
    }

    @PostMapping(value = "/api/answer-data", produces = "application/json; charset=UTF-8")
    @ResponseBody
    public List<Answer> getAnswers(/*@AuthenticationPrincipal OAuth2User principal,*/ @RequestParam UUID questionId) {
        final boolean canSeeUnapproved = this.userManager.hasPermission(/*principal,*/Permission.APPROVE_POST, null);
        if (canSeeUnapproved) return this.answerManager.getNotDeletedAndUnapproved(questionId);
        return this.answerManager.getNotDeleted(questionId, this.userManager.getPrincipalId(/*principal*/));
    }

    @PostMapping(value = "/api/liked-answers", produces = "application/json; charset=UTF-8")
    @ResponseBody
    public List<Answer> getLikedAnswers(/*@AuthenticationPrincipal OAuth2User principal,*/ @RequestParam UUID questionId) {
        return this.userManager.getUser(/*principal*/).
                map(postUser -> this.answerManager.getLikedBy(postUser, questionId)).orElse(Collections.emptyList());
    }

    @PostMapping(value = "/api/liked-questions", produces = "application/json; charset=UTF-8")
    @ResponseBody
    public List<Question> getLikedQuestions(/*@AuthenticationPrincipal OAuth2User principal*/) {
        return this.userManager.getUser(/*principal*/).
                map(this.questionManager::getLikedBy).orElse(Collections.emptyList());
    }

    @PostMapping(value = "/api/approve-post", produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public ResponseEntity<Post<?>> approvePost(
            /*@AuthenticationPrincipal OAuth2User principal,*/
            @RequestParam UUID questionId,
            @Nullable @RequestParam Long answerId
    ) {
        final Optional<? extends Post<?>> optionalPost;
        if (answerId != null) {
            optionalPost = this.answerManager.getAnswer(AnswerId.of(questionId, answerId));
        } else {
            optionalPost = this.questionManager.getQuestion(questionId);
        }
        if (optionalPost.isEmpty()) {
            return ResponseEntity.notFound().build();
        }
        final Post<?> post = optionalPost.get();
        if (!userManager.hasPermission(/*principal,*/Permission.APPROVE_POST, post)) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
        post.approve(this.userManager.getPrincipalId(/*principal*/));
        if (post instanceof Question question) this.questionManager.saveQuestion(question);
        if (post instanceof Answer answer) this.answerManager.saveAnswer(answer);
        return ResponseEntity.status(HttpStatus.OK).body(optionalPost.get());
    }

    @PostMapping(value = "/api/unapprove-post", produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public ResponseEntity<Post<?>> unapprovePost(
            /*@AuthenticationPrincipal OAuth2User principal,*/
            @RequestParam UUID questionId,
            @Nullable @RequestParam Long answerId
    ) {
        final Optional<? extends Post<?>> optionalPost;
        if (answerId != null) {
            optionalPost = this.answerManager.getAnswer(AnswerId.of(questionId, answerId));
        } else {
            optionalPost = this.questionManager.getQuestion(questionId);
        }
        if (optionalPost.isEmpty()) {
            return ResponseEntity.notFound().build();
        }
        final Post<?> post = optionalPost.get();
        if (!userManager.hasPermission(/*principal,*/Permission.APPROVE_POST, post)) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
        post.unApprove();
        if (post instanceof Question question) this.questionManager.saveQuestion(question);
        if (post instanceof Answer answer) this.answerManager.saveAnswer(answer);
        return ResponseEntity.status(HttpStatus.OK).body(optionalPost.get());
    }

    @PostMapping(value = "/api/author-name", produces = "text/plain")
    @ResponseBody
    public ResponseEntity<String> getAuthorName(
            /*@AuthenticationPrincipal OAuth2User principal,*/
            @Nullable @RequestParam UUID questionId,
            @Nullable @RequestParam Long answerId,
            @RequestParam String authorId
    ) {
        final Optional<? extends Post<?>> optionalPost = this.getOptionalPost(questionId, answerId);
        final ResponseEntity<String> notFoundResponse = ResponseEntity.status(HttpStatus.NOT_FOUND).body("Not Found");
        if (optionalPost.isEmpty()) return notFoundResponse;
        final ResponseEntity<String> ok = ResponseEntity.ok().
                body(this.userManager.getUser(authorId).map(PostUser::getName).orElse("Not Found"));
        return optionalPost.map(post -> {
            if (!post.isAnonymous()) return ok;
            if (this.userManager.hasPermission(/*principal,*/Permission.VIEW_ANONYMOUS_NAME, post)) {
                return ok;
            }
            return ResponseEntity.ok().body("Anonymous");
        }).orElse(notFoundResponse);
    }

    @PostMapping(value = "/api/delete-question", produces = "text/plain")
    @ResponseBody
    public ResponseEntity<String> deleteQuestion(/*@AuthenticationPrincipal OAuth2User principal,*/ @RequestParam UUID questionId) {
        final Optional<? extends Post<?>> toDelete = this.questionManager.getQuestion(questionId);
        if (toDelete.isEmpty()) return ResponseEntity.status(HttpStatus.NOT_FOUND).body("");
        if (!this.userManager.hasPermission(/*principal,*/Permission.DELETE_OTHER_QUESTION, toDelete.get())) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("");
        }
        return ResponseEntity.ok().body(
                String.valueOf(this.questionManager.getQuestion(questionId).map(question -> {
                    question.delete(this.userManager.getPrincipalId(/*principal*/));
                    this.questionManager.saveQuestion(question);
                    return true;
                }).orElse(false))
        );
    }

    @PostMapping(value = "/api/delete-answer", produces = "text/plain")
    @ResponseBody
    public ResponseEntity<String> deleteAnswer(/*@AuthenticationPrincipal OAuth2User principal,*/ @RequestParam UUID questionId, @RequestParam long answerId) throws JsonProcessingException {
        final Optional<? extends Post<?>> toDelete = this.answerManager.getAnswer(AnswerId.of(questionId, answerId));
        if (toDelete.isEmpty()) return ResponseEntity.status(HttpStatus.NOT_FOUND).body("");
        if (!this.userManager.hasPermission(/*principal,*/Permission.DELETE_OTHER_ANSWER, toDelete.get())) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("");
        }
        return ResponseEntity.ok().body(
                String.valueOf(this.answerManager.getAnswer(AnswerId.of(questionId, answerId)).map(answer -> {
                    answer.delete(this.userManager.getPrincipalId(/*principal*/));
                    this.answerManager.saveAnswer(answer);
                    return true;
                }).orElse(false))
        );
    }

    @PostMapping(value = "/api/change-answer-likes", produces = "application/json; charset=UTF-8")
    @ResponseBody
    public boolean getAnswers(
            /*@AuthenticationPrincipal OAuth2User principal,*/
            @RequestParam UUID questionId,
            @RequestParam long answerId,
            @RequestParam LikeAction likeAction
    ) {
        final PostUser user = this.userManager.getOrCreateUser(/*principal*/);
        return this.answerManager.getAnswer(AnswerId.of(questionId, answerId)).
                map(answer -> {
                    if (user.hasLikedAnswer(answer) && likeAction == LikeAction.ADD) return null;
                    answer.setLikes(likeAction.apply(answer.getLikes()));
                    this.answerManager.getAnswerRepository().save(answer);
                    return answer;
                }).map(answer -> {
                    if (answer == null) return false;
                    final PostUser postUser = this.userManager.getOrCreateUser(/*principal*/);
                    switch (likeAction) {
                        case ADD -> postUser.addLikedAnswer(answer);
                        case SUBTRACT -> postUser.removeLikedAnswer(answer);
                    }
                    this.userManager.getUserRepository().save(postUser);
                    return true;
                }).orElse(false);
    }

    @PostMapping(value = "/api/change-question-likes", produces = "application/json; charset=UTF-8")
    @ResponseBody
    public boolean getAnswers(
            /*@AuthenticationPrincipal OAuth2User principal,*/
            @RequestParam UUID questionId,
            @RequestParam LikeAction likeAction
    ) {
        final PostUser user = this.userManager.getOrCreateUser(/*principal*/);
        return this.questionManager.getQuestion(questionId).
                map(question -> {
                    if (user.hasLikedQuestion(questionId) && likeAction == LikeAction.ADD)
                        return null;
                    question.setLikes(likeAction.apply(question.getLikes()));
                    this.questionManager.saveQuestion(question);
                    return question;
                }).map(question -> {
                    if (question == null) return false;
                    final PostUser postUser = this.userManager.getOrCreateUser(/*principal*/);
                    switch (likeAction) {
                        case ADD -> postUser.addLikedQuestion(question);
                        case SUBTRACT -> postUser.removeLikedQuestion(question);
                    }
                    this.userManager.getUserRepository().save(postUser);
                    return true;
                }).orElse(false);
    }

    @PostMapping(value = "/api/user-id")
    @ResponseBody
    public String getUserId(/*@AuthenticationPrincipal OAuth2User principal*/) {
        return this.userManager.getPrincipalId(/*principal*/);
    }

    @PostMapping(value = "/api/has-permission", produces = "text/plain")
    @ResponseBody
    public ResponseEntity<Boolean> hasPermission(/*@AuthenticationPrincipal OAuth2User principal,*/ @RequestParam String permission) {
        try {
            return ResponseEntity.ok().body(this.userManager.hasPermission(/*principal,*/Permission.valueOf(permission.toUpperCase()), null));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(false);
        }
    }

    @PostMapping(value = "/api/permissions", produces = "application/json; charset=UTF-8")
    @ResponseBody
    public ResponseEntity<Map<String, Boolean>> getPermissions(
            /*@AuthenticationPrincipal OAuth2User principal,*/
            @Nullable @RequestParam String userEmail,
            @Nullable @RequestParam UUID questionId,
            @Nullable @RequestParam Long answerId
    ) {

        return ResponseEntity.ok().body(this.userManager.getPermissions().entrySet()
                .stream()
                .map(entry -> Map.entry(entry.getKey().toLowerCase(), entry.getValue()))
                .collect(HashMap::new, (map, entry) -> map.put(entry.getKey(), entry.getValue()), HashMap::putAll)
        );
//        final Optional<? extends Post<?>> optionalPost = this.getOptionalPost(questionId, answerId);
//        try {
//            if (userEmail != null) {
//                return this.userManager.getUserByEmail(userEmail).
//                        map(user -> ResponseEntity.ok().body(this.userManager.getPermissions(
//                                user.getId(),
//                                optionalPost.orElse(null))
//                        )).orElse(ResponseEntity.notFound().build());
//            }
//            return ResponseEntity.ok().body(this.userManager.getPermissions(
////                    principal,
//                            optionalPost.orElse(null))
//            );
//        } catch (IllegalArgumentException e) {
//            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Collections.emptyMap());
//        }
    }

    @PostMapping(value = "/api/default-permissions", produces = "application/json; charset=UTF-8")
    @ResponseBody
    public ResponseEntity<Map<String, Boolean>> getPermissions(
//            @AuthenticationPrincipal OAuth2User principal
    ) {
        if (!this.userManager.hasPermission(/*principal,*/Permission.EDIT_DEFAULT_PERMISSIONS, null)) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(null);
        }
        return ResponseEntity.ok().body(this.userManager.getDefaultPermissions());
    }

    @PostMapping(value = "/api/set-permissions", consumes = "application/json; charset=UTF-8", produces = "text/plain")
    @ResponseBody
    public ResponseEntity<String> setPermissions(
            /*@AuthenticationPrincipal OAuth2User principal,*/
            @Nullable @RequestParam("default-permissions") Boolean defaultPermissions,
            @Nullable @RequestBody List<String> userEmails,
            @RequestParam("permissions") String permissionsStr
    ) {
        final Map<String, Boolean> permissions = new HashMap<>();
        Arrays.stream(permissionsStr.toUpperCase().split(",")).forEach(perm -> {
            final String[] parts = perm.split(":");
            if (parts.length != 2) return;
            permissions.put(parts[0], Boolean.valueOf(parts[1]));
        });
        if (!this.userManager.hasPermission(/*principal,*/Permission.ADMINISTRATOR, null)) {
            permissions.remove(Permission.ADMINISTRATOR.toString().toUpperCase(Locale.ROOT));
        }
        if (defaultPermissions == null) defaultPermissions = false;
        final ResponseEntity<String> notAuthorized = ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        final ResponseEntity<String> updated = ResponseEntity.status(HttpStatus.OK).build();
        if (defaultPermissions) {
            if (!userManager.hasPermission(/*principal,*/Permission.EDIT_DEFAULT_PERMISSIONS, null)) {
                return notAuthorized;
            }
            this.userManager.updateDefaultPermissions(permissions);
            return updated;
        }
        if (userEmails == null || userEmails.isEmpty()) return ResponseEntity.badRequest().build();
        userEmails.removeIf(this.userManager.getUserEmail(/*principal*/)::equalsIgnoreCase);
        if (!this.userManager.hasPermission(/*principal,*/Permission.EDIT_USER_PERMISSIONS, null)) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
        this.userManager.updatePermissionsByEmails(userEmails, permissions);
        return ResponseEntity.ok().build();
    }

    public Optional<? extends Post<?>> getOptionalPost(@Nullable UUID questionId, @Nullable Long answerId) {
        if (questionId == null) return Optional.empty();
        if (answerId != null) {
            return this.answerManager.getAnswer(AnswerId.of(questionId, answerId));
        }
        return this.questionManager.getQuestion(questionId);
    }
}
