package com.woody.gdatabase.security.service;

import com.woody.gdatabase.repository.TokenRepository;
import com.woody.mydata.token.Token;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.io.Encoders;
import io.jsonwebtoken.security.Keys;
import jakarta.annotation.PostConstruct;
import lombok.AllArgsConstructor;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;
import io.jsonwebtoken.Claims;
import org.springframework.transaction.annotation.Transactional;

import java.security.*;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
@Slf4j
public class JWTService {

    private final TokenRepository tokenRepository;

    private String privateKey;
    private String publicKey;

    @Value("${application.security.jwt.secret}")
    private String secret;
    @Value("${application.security.jwt.expiration}")
    private Long expiration;
    @Value("${application.security.jwt.refresh-token.expiration}")
    private Long refreshTokenExpiration;

    @Autowired
    public JWTService(TokenRepository tokenRepository) throws NoSuchAlgorithmException, InvalidKeySpecException {
        this.tokenRepository = tokenRepository;

        KeyPairGenerator kpg = KeyPairGenerator.getInstance("RSA");
        kpg.initialize(2048);
        KeyPair kp = kpg.generateKeyPair();
        log.info("KeyPair generated");
        this.privateKey = Encoders.BASE64.encode(kp.getPrivate().getEncoded());
        this.publicKey = Encoders.BASE64.encode(kp.getPublic().getEncoded());



    }

    @Transactional
    @PostConstruct
    private void verifyAllTokens() throws Exception {
        List<Token> tokenList = tokenRepository.findAll();
        List<Token> removeList = new ArrayList<>();

        for (var token: tokenList) {
            try {
                validateToken(token.getToken());
            } catch (ExpiredJwtException e) {
                removeList.add(token);
            } catch (Exception e) {
                removeList.add(token);
            }
        }

        tokenRepository.deleteAll(removeList);
    }

    public <T> T extractClaim(String token, Function<Claims, T> claimsResolver) {
        final Claims claims = extractAllClaims(token);
        return claimsResolver.apply(claims);
    }

    private Claims extractAllClaims(String token) {
        return Jwts
            .parser()
            .setSigningKey(getSignInKeyPublic())
            .build()
            .parseClaimsJws(token)
            .getBody();
    }

    private List<GrantedAuthority> extractAuthorities(Claims claims) {
        return (List<GrantedAuthority>) claims.get("authorities");
    }

    private Key getSignInKey() {
        //byte[] keyBytes = Decoders.BASE64.decode(secret);
        try {
            return generatePrivateKey(privateKey);
        } catch (NoSuchAlgorithmException | InvalidKeySpecException e) {
            throw new RuntimeException("Failed to generate private key", e);
        }
    }

    private Key getSignInKeyPublic() {
        try {
            return generatePublicKey(publicKey);
        } catch (NoSuchAlgorithmException | InvalidKeySpecException e) {
            throw new RuntimeException("Failed to generate private key", e);
        }
    }

    public String getPublicKey() {
        byte [] keyBytes = getSignInKeyPublic().getEncoded();
        if (keyBytes != null) {
            return Encoders.BASE64.encode(keyBytes);
        }
        return null;
    }


    public PrivateKey generatePrivateKey(String jwtPrivateKey) throws NoSuchAlgorithmException, InvalidKeySpecException {
        KeyFactory keyFactory = KeyFactory.getInstance("RSA");
        byte[] keyBytes = Decoders.BASE64.decode(jwtPrivateKey);
        PKCS8EncodedKeySpec pkcs8EncodedKeySpec=new PKCS8EncodedKeySpec(keyBytes);
        //log.info("Private key: {}", pkcs8EncodedKeySpec);
        return keyFactory.generatePrivate(pkcs8EncodedKeySpec);
    }
    public PublicKey generatePublicKey(String jwtPublicKey) throws NoSuchAlgorithmException, InvalidKeySpecException {
        KeyFactory keyFactory = KeyFactory.getInstance("RSA");
        byte[] keyBytes = Decoders.BASE64.decode(jwtPublicKey);
        X509EncodedKeySpec x509EncodedKeySpec=new X509EncodedKeySpec(keyBytes);
        //log.info("Public key: {}", x509EncodedKeySpec);
        return keyFactory.generatePublic(x509EncodedKeySpec);
    }

//    public String getPublicKey() {
//        return publicKey;
//    }

    public String generateToken(Map<String, Object> extraClaims, String username, Collection<? extends GrantedAuthority> authorities) throws Exception {

        log.info("Generating token");
        List<String> authoritiesList = authorities.stream()
                .map(GrantedAuthority::getAuthority)
                .collect(Collectors.toList());
        log.info("Authorities: {}", authoritiesList);

        extraClaims.put("authorities", authoritiesList);
        log.info("Putting extra claims: {}", extraClaims);
        String token = Jwts.builder()
            .setClaims(extraClaims)
            .setSubject(username)
            .setIssuedAt(new java.util.Date(System.currentTimeMillis()))
            .setExpiration(new java.util.Date(System.currentTimeMillis() + expiration * 1000))
            .signWith(SignatureAlgorithm.RS256, getSignInKey())
            .compact();
        log.info("Token is generated " + token);
        Token newToken = new Token();
        newToken.setToken(token);
        newToken.setOwner(username);
        if (!tokenRepository.findByToken(token).isPresent()) {
            log.info("Saving new token");
            tokenRepository.save(newToken);
            log.info("Token is saved");
        } else {
            log.info("Token already exists");
            throw new Exception("Token already exists");
        }
        return token;
    }

    public void validateToken(String token) throws Exception {
        tokenRepository.findByToken(token).orElseThrow(() -> new NoSuchElementException());
        Jwts.parser().setSigningKey(getSignInKeyPublic()).build().parseClaimsJws(token);
    }

    public String extractUsername(String jwtToken) {
        return extractClaim(jwtToken, Claims::getSubject);
    }

    public boolean isTokenValid(String token, String username) {
        final String token_username = extractUsername(token).toLowerCase();
        return (token_username.equals(username)) && !isTokenExpired(token);
    }

    private boolean isTokenExpired(String token) throws ExpiredJwtException {
        return extractExpiration(token).before(new Date());
    }

    private Date extractExpiration(String token) {
        var a = extractClaim(token, Claims::getExpiration);
        return a;
    }
}
