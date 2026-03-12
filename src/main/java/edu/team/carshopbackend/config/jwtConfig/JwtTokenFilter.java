package edu.team.carshopbackend.config.jwtConfig;

import com.fasterxml.jackson.databind.ObjectMapper;
import edu.team.carshopbackend.error.ErrorResponse;
import edu.team.carshopbackend.repository.JwtTokenRepository;
import edu.team.carshopbackend.service.impl.UserService;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.JwtException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import org.jspecify.annotations.NullMarked;
import org.springframework.http.HttpHeaders;
import org.springframework.lang.NonNull;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Component
@RequiredArgsConstructor
@NullMarked
public class JwtTokenFilter extends OncePerRequestFilter {
    private final JwtCore jwtCore;
    private final UserService userDetailsService;
    private final JwtTokenRepository jwtTokenRepository;

    /**
     * Filters incoming requests and validates JWT access tokens. If a valid token is
     * present and not revoked, the authenticated user is set in SecurityContext.
     *
     * @param request current HTTP request
     * @param response current HTTP response
     * @param filterChain filter chain to continue
     * @throws ServletException on servlet errors
     * @throws IOException on IO errors
     */
    @Override
    protected void doFilterInternal
            (@NotNull HttpServletRequest request, @NotNull HttpServletResponse response, @NotNull FilterChain filterChain) throws ServletException, IOException {
        String header = request.getHeader(HttpHeaders.AUTHORIZATION);

        if (header == null || !header.startsWith("Bearer ")) {
            filterChain.doFilter(request, response);
            return;
        }

        String jwt = header.substring(7);

        try{
            if (!jwtCore.isAccessToken(jwt)) {
                filterChain.doFilter(request, response);
                return;
            }

            String email = jwtCore.getEmailFromToken(jwt);
            String jti = jwtCore.getJti(jwt);

            if(email != null && SecurityContextHolder.getContext().getAuthentication() == null) {
                var storedToken = jwtTokenRepository.findByJti(jti)
                        .orElseThrow(() -> new JwtException("Token not found"));

                if (storedToken.isRevoked() || storedToken.isExpired()) {
                    handleError(response, "Token revoked");
                    return;
                }

                UserDetails userDetails = userDetailsService.loadUserByUsername(email);
                var authentication =
                            new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
                SecurityContextHolder.getContext().setAuthentication(authentication);
            }

        }catch (ExpiredJwtException e){
            handleError(response, "Expired JWT token");
            return;
        }catch (JwtException e){
            handleError(response, "Invalid JWT token");
            return;
        }

        filterChain.doFilter(request, response);
    }

    private void handleError(HttpServletResponse response, String message) throws IOException {
        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
        ErrorResponse error = new ErrorResponse(401, message);
        response.getWriter().write(new ObjectMapper().writeValueAsString(error));
    }
}
