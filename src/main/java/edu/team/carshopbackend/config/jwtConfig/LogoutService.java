package edu.team.carshopbackend.config.jwtConfig;

import com.fasterxml.jackson.databind.ObjectMapper;
import edu.team.carshopbackend.repository.JwtTokenRepository;
import io.jsonwebtoken.ExpiredJwtException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.jspecify.annotations.NonNull;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.logout.LogoutHandler;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class LogoutService implements LogoutHandler {
    private final JwtTokenRepository jwtTokenRepository;
    private final JwtCore jwtCore;

    /**
     * Logout handler that revokes and expires the JWT token found in Authorization header.
     *
     * @param request current HTTP request
     * @param response current HTTP response
     * @param authentication authentication information (may be null)
     */
    @Override
    public void logout(HttpServletRequest request, @NonNull HttpServletResponse response, Authentication authentication) {
        final String authHeader = request.getHeader("Authorization");
        final String jwt;
        final String jti;
        if (authHeader == null ||  !authHeader.startsWith("Bearer ")) {
            return;
        }
        jwt = authHeader.substring(7);
        jti = jwtCore.getJti(jwt);

        jwtTokenRepository.findByJti(jti).ifPresent(token -> {
            token.setExpired(true);
            token.setRevoked(true);
            jwtTokenRepository.save(token);
        });

        try {
            logoutResponse(response);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private void logoutResponse(HttpServletResponse response) throws IOException {
        response.setStatus(HttpServletResponse.SC_OK);
        response.setContentType("application/json");
        response.getWriter().write(new ObjectMapper().writeValueAsString(Map.of(
                "message", "Logged out successfully"
        )));
    }
}
