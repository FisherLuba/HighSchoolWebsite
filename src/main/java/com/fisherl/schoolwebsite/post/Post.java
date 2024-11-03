package com.fisherl.schoolwebsite.post;

import org.hibernate.search.mapper.pojo.mapping.definition.annotation.FullTextField;
import org.springframework.lang.Nullable;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.ElementCollection;
import javax.persistence.FetchType;
import javax.persistence.MappedSuperclass;
import javax.persistence.OneToOne;
import java.time.Instant;
import java.util.List;

@MappedSuperclass
public abstract class Post<T extends DeleteData> {

    public static final String RECENT_CATEGORY = "recent";
    public static final String UNAPPROVED_CATEGORY = "unapproved";

    private String authorId;
    @FullTextField
    @Column(length = 5000)
    protected String content;
    private Instant timePosted;
    protected boolean anonymous;
    private int likes;
    @ElementCollection(fetch = FetchType.EAGER)
    private List<String> tags;
    @Nullable
    private String approvedById;
    @Nullable
    @OneToOne(cascade = CascadeType.ALL)
    private T deleteData;

    public Post(
            String authorId,
            String content,
            Instant timePosted,
            boolean anonymous,
            int likes,
            List<String> tags,
            @Nullable String approvedById,
            @Nullable T deleteData

    ) {
        this.authorId = authorId;
        this.content = content;
        this.timePosted = timePosted;
        this.anonymous = anonymous;
        this.likes = likes;
        this.tags = tags;
        this.approvedById = approvedById;
        this.deleteData = deleteData;
    }

    public Post(
            String authorId,
            String content,
            Instant timePosted,
            boolean anonymous,
            int likes,
            List<String> tags
    ) {
        this.authorId = authorId;
        this.content = content;
        this.timePosted = timePosted;
        this.anonymous = anonymous;
        this.likes = likes;
        this.tags = tags;
    }

    protected Post() {}

    public String getAuthorId() {
        return authorId;
    }

    public String getContent() {
        return content;
    }

    public Instant getTimePosted() {
        return timePosted;
    }

    public boolean isAnonymous() {
        return anonymous;
    }

    public int getLikes() {
        return likes;
    }

    public List<String> getTags() {
        return tags;
    }

    public void setLikes(int likes) {
        this.likes = likes;
    }

    @Nullable
    public String getApprovedById() {
        return approvedById;
    }

    @Nullable
    public T getDeleteData() {
        return deleteData;
    }

    protected void setDeleteData(@Nullable T deleteData) {
        this.deleteData = deleteData;
    }

    public abstract void delete(String authorId);

    public void approve(String id) {
        this.approvedById = id;
    }

    public void unApprove() {
        this.approvedById = null;
    }

    @Override
    public String toString() {
        return "Post{" +
                "authorId='" + authorId + '\'' +
                ", content='" + content + '\'' +
                ", timePosted=" + timePosted +
                ", anonymous=" + anonymous +
                ", likes=" + likes +
                ", tags=" + tags +
                ", approvedById='" + approvedById + '\'' +
                ", deleteData=" + deleteData +
                '}';
    }
}
