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

    public String generateToken(UserDetails userDetails) {
        UserDetailsImpl user = (UserDetailsImpl) userDetails;

        Map<String, Object> claims = new HashMap<>();
        claims.put("user_profile_id", user.getProfile().getId());
        claims.put("typ", "access");

        return buildToken(claims, user, jwtExpiration);
    }
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

    public Claims extractAllClaims(final String token) {

        return Jwts.parser()
                .verifyWith(secretKey)
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }

    public String getEmailFromToken(final String token) {
        Claims claims = extractAllClaims(token);
        return claims.getSubject();
    }

    public boolean isRefreshToken(final String token) {
        Claims claims = extractAllClaims(token);
        return claims.get("typ", String.class).equals("refresh");
    }

    public boolean isAccessToken(final String token) {
        Claims claims = extractAllClaims(token);
        return claims.get("typ", String.class).equals("access");
    }

    public String getJti(String token) {
        try {
            return extractAllClaims(token).getId();
        } catch (ExpiredJwtException e) {
            return e.getClaims().getId();
        }
    }
}
