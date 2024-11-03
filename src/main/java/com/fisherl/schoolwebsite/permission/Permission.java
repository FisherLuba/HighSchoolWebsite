package com.fisherl.schoolwebsite.permission;

import com.fisherl.schoolwebsite.post.Post;
import com.fisherl.schoolwebsite.user.PostUser;
import com.fisherl.schoolwebsite.util.function.TriFunction;
import org.springframework.lang.Nullable;

public enum Permission {

    DELETE_OTHER_QUESTION((permission, user, post) -> user.isAdministrator() || (post != null && user.getId().equalsIgnoreCase(post.getAuthorId())) || user.hasPermission(permission)),
    DELETE_OTHER_ANSWER((permission, user, post) -> user.isAdministrator() || (post != null && user.getId().equalsIgnoreCase(post.getAuthorId())) || user.hasPermission(permission)),
    POST_QUESTION((permission, user, post) -> user.isAdministrator() || user.hasPermission(permission)),
    POST_ANSWER((permission, user, post) -> user.isAdministrator() || user.hasPermission(permission)),
    APPROVE_POST((permission, user, post) -> user.isAdministrator() || user.hasPermission(permission)),
    VIEW_ANONYMOUS_NAME(((permission, user, post) -> user.isAdministrator() || (post != null && user.getId().equals(post.getAuthorId())) || user.hasPermission(permission))),
    EDIT_SELF_QUESTION((permission, user, post) -> user.isAdministrator() || (post != null && user.getId().equals(post.getAuthorId())) && user.hasPermission(permission)),
    EDIT_SELF_ANSWER((permission, user, post) -> user.isAdministrator() || (post != null && user.getId().equals(post.getAuthorId())) && user.hasPermission(permission)),
    EDIT_USER_PERMISSIONS((permission, user, post) -> user.isAdministrator() || user.hasPermission(permission)),
    EDIT_DEFAULT_PERMISSIONS((permission, user, post) -> user.isAdministrator() || user.hasPermission(permission)),
    EDIT_CATEGORIES((permission, user, post) -> user.isAdministrator() || user.hasPermission(permission)),
    ADMINISTRATOR((permission, user, post) -> user.hasPermission(permission));

    private final TriFunction<Permission, PostUser, Post<?>, Boolean> biFunction;

    Permission(TriFunction<Permission, PostUser, Post<?>, Boolean> biFunction) {
        this.biFunction = biFunction;
    }

    public boolean hasPermission(PostUser user, @Nullable Post<?> post) {
        return this.biFunction.apply(this, user, post);
    }

}
