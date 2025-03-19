package org.AFM.rssbridge.util;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import lombok.Getter;
import org.AFM.rssbridge.user.model.User;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

@Component
public class JwtTokenUtil {

    @Value("${jwt.secret}")
    private String secretKey;

    @Getter
    @Value("${jwt.access.token.expiration}")
    private long accessTokenExpiration;

    @Getter
    @Value("${jwt.refresh.token.expiration}")
    private long refreshTokenExpiration;
    private static final Logger LOGGER = LoggerFactory.getLogger(JwtRequestFilter.class);
    public String extractUsername(String token) {
        return extractClaim(token, Claims::getSubject);
    }

    public Date extractExpiration(String token) {
        return extractClaim(token, Claims::getExpiration);
    }

    public <T> T extractClaim(String token, Function<Claims, T> claimsResolver) {
        final Claims claims = extractAllClaims(token);
        return claimsResolver.apply(claims);
    }

    private Claims extractAllClaims(String token) {
        return Jwts.parser()
                .setSigningKey(secretKey.getBytes())
                .build()
                .parseClaimsJws(token)
                .getBody();
    }

    private Boolean isTokenExpired(String token) {
        return extractExpiration(token).before(new Date());
    }

    public String generateAccessToken(UserDetails userDetails) {
        if (!(userDetails instanceof User user)) {
            throw new IllegalArgumentException("UserDetails must be an instance of User");
        }

        Map<String, Object> claims = new HashMap<>();
        claims.put("roles", userDetails.getAuthorities());
        claims.put("name", user.getName());
        claims.put("surname", user.getSurname());
        claims.put("fathername", user.getFathername());
        return generateToken(claims, userDetails);
    }

    private String generateToken(Map<String, Object> claims, UserDetails userDetails) {
        String iin = ((User) userDetails).getIin();
        return Jwts.builder().
                setClaims(claims).
                setSubject(iin).
                setIssuedAt(new Date(System.currentTimeMillis())).
                setExpiration(new Date(System.currentTimeMillis() + accessTokenExpiration)).
                signWith(SignatureAlgorithm.HS256, secretKey.getBytes()).
                compact();
    }

    public Boolean validateAuthToken(String token, UserDetails userDetails) {
        final String username = extractUsername(token);
        LOGGER.warn("Extracted username: {}", username);
        LOGGER.warn("Expected username: {}", userDetails.getUsername());
        LOGGER.warn("Is token expired: {}", isTokenExpired(token));
        return (username.equals(userDetails.getUsername()) && !isTokenExpired(token));
    }
    public String generateRefreshToken(UserDetails userDetails) {
        return generateToken(new HashMap<>(), userDetails, refreshTokenExpiration);
    }
    private String generateToken(Map<String, Object> claims, UserDetails userDetails, long expiration) {
        String iin = ((User) userDetails).getIin();
        return Jwts.builder()
                .setClaims(claims)
                .setSubject(iin)
                .setIssuedAt(new Date(System.currentTimeMillis()))
                .setExpiration(new Date(System.currentTimeMillis() + expiration))
                .signWith(SignatureAlgorithm.HS256, secretKey.getBytes())
                .compact();
    }
    public Boolean validateRefreshToken(String token) {
        try {
            return !isTokenExpired(token);
        } catch (Exception e) {
            return false;
        }
    }
}
