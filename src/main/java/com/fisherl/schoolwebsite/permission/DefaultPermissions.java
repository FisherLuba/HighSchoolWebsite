package com.fisherl.schoolwebsite.permission;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.fisherl.schoolwebsite.post.Post;
import com.fisherl.schoolwebsite.user.PostUser;
import org.springframework.boot.json.JsonParser;
import org.springframework.boot.json.JsonParserFactory;
import org.springframework.lang.Nullable;

import javax.persistence.ElementCollection;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Id;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;
import java.util.stream.Collectors;

@Entity
public class DefaultPermissions implements Permissible {

    private static final Path FILE_PATH = Path.of("permissions.json");
    public static final int STATIC_ID = 1;

    @Id
    private int id = STATIC_ID;
    @ElementCollection(fetch = FetchType.EAGER)
    private Map<String, Boolean> permissions;

    private DefaultPermissions(Map<String, Boolean> permissions) {
        this.permissions = permissions;
    }

    protected DefaultPermissions() {
    }

    public static DefaultPermissions loadDefaultPermissions() throws IOException {
        final File file = FILE_PATH.toFile();
        if (!file.exists()) file.createNewFile();
        final JsonParser parser = JsonParserFactory.getJsonParser();
        final String json = Files.readString(FILE_PATH);
        if (json.isBlank()) {
            final Map<String, Boolean> defaultPermissions = Arrays.stream(Permission.values()).
//                    map(permission -> Map.entry(permission.toString().toUpperCase(Locale.ROOT), false)).
        map(permission -> Map.entry(permission.toString().toUpperCase(Locale.ROOT),
        true)).
                    collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));
            final DefaultPermissions permissions = new DefaultPermissions(defaultPermissions);
            permissions.save();
            return permissions;
        }
        final Map<String, Boolean> permissions = parser.parseMap(json).
                entrySet().
                stream().
                map(entry -> {
                    if (!(entry.getValue() instanceof Boolean value)) return null;
                    return Map.entry(entry.getKey().toUpperCase(), value);
                }).filter(entry -> entry != null).
                collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));
        return new DefaultPermissions(permissions);
    }

    @Override
    public void updatePermissions(Map<String, Boolean> permissions) {
        this.permissions.putAll(permissions);
        try {
            this.save();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void save() throws IOException {
        final ObjectMapper mapper = new ObjectMapper();
        final ObjectNode objectNode = mapper.createObjectNode();
        for (var entry : this.permissions.entrySet()) {
            objectNode.put(entry.getKey(), entry.getValue());
        }
        Files.writeString(FILE_PATH, objectNode.toPrettyString());
    }

    @Override
    public boolean hasPermission(Permission permission) {
        return this.hasPermission("", permission, null);
    }

    public boolean hasPermission(String userId, Permission permission, @Nullable Post<?> post) {
        return permission.hasPermission(
                PostUser.withPermissions(
                        userId,
                        new HashMap<>(this.permissions)
                ),
                post);
    }

    protected int getId() {
        return id;
    }

    protected void setId(int id) {
        this.id = id;
    }

    public Map<String, Boolean> getPermissions() {
        return permissions;
    }

    protected void setPermissions(Map<String, Boolean> permissions) {
        this.permissions = permissions;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        final DefaultPermissions that = (DefaultPermissions) o;
        return Objects.equals(permissions, that.permissions);
    }

    @Override
    public int hashCode() {
        return Objects.hash(permissions);
    }
}
