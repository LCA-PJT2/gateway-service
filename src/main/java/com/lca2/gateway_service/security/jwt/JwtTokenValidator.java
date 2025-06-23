package com.lca2.gateway_service.security.jwt;

import com.lca2.gateway_service.security.jwt.authentication.JwtAuthentication;
import com.lca2.gateway_service.security.jwt.authentication.UserPrincipal;
import com.lca2.gateway_service.security.jwt.props.JwtConfigProperties;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.util.ArrayList;
import java.util.Date;

@Slf4j
@Component
@RequiredArgsConstructor
public class JwtTokenValidator {
    private final JwtConfigProperties configProperties;

    private volatile SecretKey secretKey;

    private SecretKey getSecretKey() {
        if (secretKey == null) {
            synchronized (this) {
                if (secretKey == null) {
                    secretKey = Keys.hmacShaKeyFor(Decoders.BASE64.decode(configProperties.getSecretKey()));
                }
            }
        }

        return secretKey;
    }

    public JwtAuthentication validateToken(String token) {
        String userId = null;

        final Claims claims = this.verifyAndGetClaims(token);
        if (claims == null) {
            log.error("Invalid token1");
            return null;
        }

        Date expirationDate = claims.getExpiration();
        if (expirationDate == null || expirationDate.before(new Date())) {
            log.error("Invalid token2");
            return null;
        }

        userId = claims.get("userId", String.class);
        log.info("userId: {}", userId);

        String tokenType = claims.get("tokenType", String.class);
        if (!"access".equals(tokenType)) {
            log.error("Invalid token3");
            return null;
        }

        UserPrincipal principal = new UserPrincipal(userId);
        return new JwtAuthentication(principal, token, new ArrayList<>());  //권한 목록 빈 컬렉션 전달
    }

    private Claims verifyAndGetClaims(String token) {
        Claims claims;

        try {
            claims = Jwts.parser()
                    .verifyWith(getSecretKey())
                    .build()
                    .parseSignedClaims(token)
                    .getPayload();
        } catch (Exception e) {
            return null;
        }

        return claims;
    }

    /*
    private List<GrantedAuthority> getGrantedAuthorities(String role) {
        ArrayList<GrantedAuthority> grantedAuthorities = new ArrayList<>();
        if (role != null) {
            grantedAuthorities.add(new SimpleGrantedAuthority(role));
        }

        return grantedAuthorities;
    }
    */

    public String getToken(HttpServletRequest request) {
        String authHeader = getAuthHeaderFromHeader(request);
        if (authHeader != null && authHeader.startsWith("Bearer")) {
            log.info("Bearer token found");
            return authHeader.substring(7);
        }

        return null;
    }

    public String getAuthHeaderFromHeader(HttpServletRequest request) {
        return request.getHeader(configProperties.getHeader());
    }
}
