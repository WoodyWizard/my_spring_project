package com.woody.gdatabase.security.service;

import com.woody.gdatabase.repository.TokenRepository;
import com.woody.mydata.token.Token;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import lombok.AllArgsConstructor;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;
import io.jsonwebtoken.Claims;

import java.security.Key;
import java.util.Date;
import java.util.Map;
import java.util.function.Function;

@Service
@RequiredArgsConstructor
public class JWTService {

    private final TokenRepository tokenRepository;

    @Value("${application.security.jwt.secret}")
    private String secret;
    @Value("${application.security.jwt.expiration}")
    private Long expiration;
    @Value("${application.security.jwt.refresh-token.expiration}")
    private Long refreshTokenExpiration;

    public <T> T extractClaim(String token, Function<Claims, T> claimsResolver) {
        final Claims claims = extractAllClaims(token);
        return claimsResolver.apply(claims);
    }

    private Claims extractAllClaims(String token) {
        return Jwts
            .parser()
            .setSigningKey(getSignInKey())
            .build()
            .parseClaimsJws(token)
            .getBody();
    }

    private Key getSignInKey() {
        byte[] keyBytes = Decoders.BASE64.decode(secret);
        return Keys.hmacShaKeyFor(keyBytes);
    }

    public String generateToken(Map<String, Object> extraClaims, String username) {
        String token = Jwts.builder()
            .setClaims(extraClaims)
            .setSubject(username)
            .setIssuedAt(new java.util.Date(System.currentTimeMillis()))
            .setExpiration(new java.util.Date(System.currentTimeMillis() + expiration * 1000))
            .signWith(getSignInKey(), io.jsonwebtoken.SignatureAlgorithm.HS256)
            .compact();
        Token newToken = new Token();
        newToken.setToken(token);
        tokenRepository.save(newToken);
        return token;
    }

    public void validateToken(String token) {
        Jwts.parser().setSigningKey(getSignInKey()).build().parseClaimsJws(token);
    }

    public String extractUsername(String jwtToken) {
        return extractClaim(jwtToken, Claims::getSubject);
    }

    public boolean isTokenValid(String token, String username) {
        final String token_username = extractUsername(token).toLowerCase();
        return (token_username.equals(username)) && !isTokenExpired(token);
    }

    private boolean isTokenExpired(String token) {
        return extractExpiration(token).before(new Date());
    }

    private Date extractExpiration(String token) {
        return extractClaim(token, Claims::getExpiration);
    }
}
