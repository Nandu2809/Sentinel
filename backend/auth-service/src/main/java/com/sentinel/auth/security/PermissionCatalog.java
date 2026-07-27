package com.sentinel.auth.security;

import java.util.Arrays;
import java.util.List;

public enum PermissionCatalog {
    USER_READ("USER_READ", "users", "read"),
    USER_WRITE("USER_WRITE", "users", "write"),
    USER_DELETE("USER_DELETE", "users", "delete"),
    ROLE_READ("ROLE_READ", "roles", "read"),
    ROLE_WRITE("ROLE_WRITE", "roles", "write"),
    ROLE_DELETE("ROLE_DELETE", "roles", "delete"),
    PERMISSION_READ("PERMISSION_READ", "permissions", "read"),
    PERMISSION_WRITE("PERMISSION_WRITE", "permissions", "write"),
    PERMISSION_DELETE("PERMISSION_DELETE", "permissions", "delete"),
    AUTH_SELF("AUTH_SELF", "auth", "self");

    private final String name;
    private final String resource;
    private final String action;

    PermissionCatalog(String name, String resource, String action) {
        this.name = name;
        this.resource = resource;
        this.action = action;
    }

    public String permissionName() {
        return name;
    }

    public String resource() {
        return resource;
    }

    public String action() {
        return action;
    }

    public static List<PermissionCatalog> all() {
        return Arrays.asList(values());
    }
}

