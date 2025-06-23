package com.lca2.gateway_service.gateway;

import com.lca2.gateway_service.security.jwt.authentication.UserPrincipal;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.servlet.function.ServerRequest;

import java.util.function.Function;

public class AuthenticationHeaderFilterFunction {
    public static Function<ServerRequest, ServerRequest> addHeader() {
        return request -> {
            ServerRequest.Builder requestBuilder = ServerRequest.from(request);
            Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();

            if (principal instanceof UserPrincipal userPrincipal) {
                requestBuilder.header("X-Auth-UserId", userPrincipal.userId());
            }

            return requestBuilder.build();
        };
    }
}
