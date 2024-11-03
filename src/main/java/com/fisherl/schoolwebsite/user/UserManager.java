package com.fisherl.schoolwebsite.user;

import com.fisherl.schoolwebsite.SchoolWebsiteApplication;
import com.fisherl.schoolwebsite.post.Post;
import com.fisherl.schoolwebsite.permission.DefaultPermissions;
import com.fisherl.schoolwebsite.permission.Permission;
import com.fisherl.schoolwebsite.permission.TemporaryPermissions;
import com.fisherl.schoolwebsite.permission.TemporaryPermissionsRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.lang.Nullable;
//import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.StreamSupport;

@Component
public class UserManager {

    private final UserRepository userRepository;
    private final TemporaryPermissionsRepository temporaryPermissionsRepository;
    private final DefaultPermissions defaultPermissions;

    @Autowired
    public UserManager(UserRepository userRepository, TemporaryPermissionsRepository temporaryPermissionsRepository) {
        this.userRepository = userRepository;
        this.temporaryPermissionsRepository = temporaryPermissionsRepository;
        try {
            this.defaultPermissions = DefaultPermissions.loadDefaultPermissions();
        } catch (IOException e) {
            e.printStackTrace();
            throw new RuntimeException("Could not load default permissions");
        }
    }

    public void submitUser(PostUser postUser) {
        this.userRepository.save(postUser);
    }

//    public void addUser(PostUser user) {
//        this.
//    }

    public Map<String, Boolean> getDefaultPermissions() {
        return this.defaultPermissions.getPermissions();
    }

    public void updateDefaultPermissions(Map<String, Boolean> permissions) {
        this.defaultPermissions.updatePermissions(permissions);
    }

    public void deleteUser(PostUser postUser) {
        this.userRepository.delete(postUser);
    }

    public void deleteUser(String id) {
        this.userRepository.deleteById(id);
    }

    public List<PostUser> getUsers() {
        return StreamSupport.stream(this.userRepository.findAll().spliterator(), false).toList();
    }

    public Optional<PostUser> getUser(String id) {
        return this.userRepository.findById(id);
    }

    public Optional<PostUser> getUserByEmail(String email) {
        return this.userRepository.findByEmail(email);
    }


    public Optional<PostUser> getUser(/*OAuth2User principal*/) {
        return this.getUser(this.getPrincipalId(/*principal*/));
    }

    public String getUserEmail(/*OAuth2User principal*/) {
//        return principal.getAttribute("email");
        return SchoolWebsiteApplication.LOCAL_USER_EMAIL;
    }

    public PostUser getOrCreateUser(/*OAuth2User principal*/) {
        final String id = this.getPrincipalId(/*principal*/);
        return this.getUser(id).map(user -> {
            for (var entry : this.defaultPermissions.getPermissions().entrySet()) {
                if (!user.hasSetPermission(entry.getKey())) user.setPermission(entry.getKey(), entry.getValue());
            }
            this.userRepository.save(user);
            return user;
        }).orElseGet(() -> {
            final Optional<TemporaryPermissions> temporaryPermissions = this.temporaryPermissionsRepository.findById(this.getUserEmail(/*principal*/));
//            final String givenName = principal.getAttribute("given_name");
//            final String familyName = principal.getAttribute("family_name");
//            final String name = givenName + (familyName == null ? "" : " " + familyName);
            
            final PostUser user = new PostUser(
                    id,
//                    name,
                    SchoolWebsiteApplication.LOCAL_USER_NAME,
//                    principal.getAttribute("email"),
                    SchoolWebsiteApplication.LOCAL_USER_EMAIL,
                    new HashSet<>(),
                    new HashSet<>(),
                    temporaryPermissions.map(perms -> {
                        final Map<String, Boolean> permissions = new HashMap<>(perms.getPermissions());
                        this.temporaryPermissionsRepository.delete(perms);
                        return permissions;
                    }).orElse(new HashMap<>(this.defaultPermissions.getPermissions()))
            );
            this.userRepository.save(user);
            return user;
        });
    }

    public Map<String, Boolean> getPermissions(/*OAuth2User principal,*/@Nullable Post<?> post) {
        return this.getPermissions(this.getPrincipalId(/*principal*/), post);
    }

    public Map<String, Boolean> getPermissions(String id, @Nullable Post<?> post) {
        return this.getUser(id).map(user -> user.getPermissions(post)).orElse(this.defaultPermissions.getPermissions());
    }

    public Map<String, Boolean> getPermissions() {
        return this.defaultPermissions.getPermissions();
    }

    public boolean hasPermission(/*OAuth2User principal,*/Permission permission,
                                 @Nullable Post<?> post) {
//        return this.hasPermission(this.getPrincipalId(/*principal*/), permission, post);
        return true;
    }

    public boolean hasPermission(String id, Permission permission, @Nullable Post<?> post) {
        return this.getUser(id).map(user -> this.hasPermission(user, permission, post)).
                orElse(this.defaultPermissions.hasPermission(id, permission, post));
    }

    public boolean hasPermission(PostUser user, Permission permission, @Nullable Post<?> post) {
        return permission.hasPermission(user, post);
    }

    public String getPrincipalId(/*OAuth2User principal*/) {
//        return principal.getAttribute("sub");
        return SchoolWebsiteApplication.LOCAL_USER_ID;
    }

    public UserRepository getUserRepository() {
        return userRepository;
    }

    public void updatePermissionsByEmails(List<String> emails, Map<String, Boolean> permissions) {
        emails.forEach(email -> this.getUserByEmail(email).ifPresentOrElse(user -> {
            user.updatePermissions(permissions);
//            this.userRepository.save(user);
        }, () -> {
//            final TemporaryPermissions temporaryPermissions = new TemporaryPermissions(email, permissions);
//            this.temporaryPermissionsRepository.save(temporaryPermissions);
        }));
    }
}
