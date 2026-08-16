package edu.team.carshopbackend.config.jwtConfig;

import edu.team.carshopbackend.entity.impl.UserDetailsImpl;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;

import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;


@Component
public class JwtCore {
    private final long jwtExpiration;
    private final long refreshExpiration;

    private final SecretKey secretKey;

    public JwtCore(@Value("${jwt.secret-base64}") final String secret,
                   @Value("${jwt.lifetime}") final long jwtExpiration,
                   @Value("${jwt.refresh-token.lifetime}") final long refreshExpiration) {
        byte[] keyBytes = Decoders.BASE64.decode(secret);
        this.secretKey = Keys.hmacShaKeyFor(keyBytes);
        this.jwtExpiration = jwtExpiration;
        this.refreshExpiration = refreshExpiration;
    }

    /**
     * Generates a JWT access token for the authenticated user.
     *
     * @param userDetails the authenticated user details
     * @return generated JWT token as a String
     */
    public String generateToken(UserDetails userDetails) {
        UserDetailsImpl user = (UserDetailsImpl) userDetails;

        Map<String, Object> claims = new HashMap<>();
        claims.put("typ", "access");

        return buildToken(claims, user, jwtExpiration);
    }

    /**
     * Generates a refresh token for the provided user details.
     *
     * @param user user details
     * @return refresh JWT string
     */
    public String generateRefreshToken(final UserDetails user) {
        Map<String, Object> claims = new HashMap<>();
        claims.put("typ", "refresh");

        return buildToken(claims, user, refreshExpiration);
    }


    private String buildToken(final Map<String, Object> extraClaims, final UserDetails user, final long lifetime) {
        return Jwts.builder()
                .subject(user.getUsername())
                .id(UUID.randomUUID().toString())
                .claims(extraClaims)
                .issuedAt(new Date())
                .expiration(new Date(new Date().getTime() + lifetime))
                .signWith(secretKey)
                .compact();
    }

    /**
     * Extracts all claims from the provided token without validating expiration.
     *
     * @param token JWT string
     * @return parsed Claims object
     */
    public Claims extractAllClaims(final String token) {
        return Jwts.parser()
                .verifyWith(secretKey)
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }

    /**
     * Returns subject (email) from the token claims.
     *
     * @param token JWT string
     * @return email stored in token subject
     */
    public String getEmailFromToken(final String token) {
        Claims claims = extractAllClaims(token);
        return claims.getSubject();
    }

    /**
     * Checks whether the token is a refresh token.
     *
     * @param token JWT string
     * @return true if token type equals "refresh"
     */
    public boolean isRefreshToken(final String token) {
        Claims claims = extractAllClaims(token);
        return claims.get("typ", String.class).equals("refresh");
    }

    /**
     * Checks whether the token is an access token.
     *
     * @param token JWT string
     * @return true if token type equals "access"
     */
    public boolean isAccessToken(final String token) {
        Claims claims = extractAllClaims(token);
        return claims.get("typ", String.class).equals("access");
    }

    /**
     * Returns the token identifier (jti). Works also for expired tokens.
     *
     * @param token JWT string
     * @return jti value
     */
    public String getJti(String token) {
        try {
            return extractAllClaims(token).getId();
        } catch (ExpiredJwtException e) {
            return e.getClaims().getId();
        }
    }
}
