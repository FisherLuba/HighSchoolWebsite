package com.fisherl.schoolwebsite.permission;

import java.util.Map;

public interface Permissible {

    boolean hasPermission(Permission permission);
    void updatePermissions(Map<String, Boolean> permissions);

}
