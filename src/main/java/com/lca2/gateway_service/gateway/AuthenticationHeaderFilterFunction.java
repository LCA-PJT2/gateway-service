package com.lca2.gateway_service.gateway;

import com.lca2.gateway_service.security.jwt.authentication.UserPrincipal;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.servlet.function.ServerRequest;

import java.util.function.Function;

@Slf4j
public class AuthenticationHeaderFilterFunction {
    public static Function<ServerRequest, ServerRequest> addHeader() {
        return request -> {
            ServerRequest.Builder requestBuilder = ServerRequest.from(request);
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

            if (authentication != null && authentication.isAuthenticated()) {
                log.debug("Authentication is present and authenticated.");
                Object principal = authentication.getPrincipal();

                if (principal instanceof UserPrincipal userPrincipal) {
                    log.debug("Principal is UserPrincipal. User ID: {}", userPrincipal.userId());
                    requestBuilder.header("X-Auth-UserId", userPrincipal.userId());
                } else {
                    log.warn("Principal is not UserPrincipal. Actual type: {}", principal.getClass().getName());
                }
            } else {
                log.warn("Authentication is null or not authenticated. No X-Auth-UserId header added.");
            }

            return requestBuilder.build();
        };
    }
}
