package com.lca2.gateway_service.security.jwt.authentication;

import java.security.Principal;
import java.util.Objects;

public record UserPrincipal(String userId) implements Principal {
    public boolean hasName() {
        return userId != null;
    }

    public boolean hasMandatory() {
        return userId != null;
    }

    @Override
    public String toString() {
        return getName();
    }

    @Override
    public String getName() {
        return userId;
    }

    @Override
    public boolean equals(Object another) {
        if (this == another) return true;
        if (another == null) return false;
        if (!getClass().isAssignableFrom(another.getClass())) return false;

        UserPrincipal principal = (UserPrincipal) another;

        return !Objects.equals(userId, principal.userId);
    }

}
