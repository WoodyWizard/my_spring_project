package com.woody.gdatabase.security;

import com.woody.gdatabase.repository.TokenRepository;
import com.woody.gdatabase.security.service.CustomUserDetailsService;
import com.woody.gdatabase.security.service.JWTService;
import com.woody.mydata.UserDT;
import com.woody.mydata.token.Token;
import io.jsonwebtoken.ExpiredJwtException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.AllArgsConstructor;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.http.ResponseEntity;
import org.springframework.orm.ObjectOptimisticLockingFailureException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Component
@AllArgsConstructor
@Slf4j
public class AuthFilter extends OncePerRequestFilter {


    private final JWTService jwtService;
    private final CustomUserDetailsService userDetailsService;
    private final TokenRepository tokenRepository;

    @Override
    protected void doFilterInternal(
            @NonNull HttpServletRequest request,
            @NonNull HttpServletResponse response,
            @NonNull FilterChain filterChain
    ) throws ServletException, IOException {

        log.info("Auth Filter [Started]");
        final String authorizationHeader = request.getHeader("Authorization");
        String jwtToken;
        String username = null;

        if (authorizationHeader == null || !authorizationHeader.startsWith("Bearer ")) {
            filterChain.doFilter(request, response);
            return;
        }

        jwtToken = authorizationHeader.substring(7);

        if (!tokenRepository.findByToken(jwtToken).isPresent()) {
            log.error("Token is not present in database");
            response.addHeader("JWT_ERROR", jwtToken);
            response.sendError(HttpServletResponse.SC_FORBIDDEN);
            log.info("Sending 403 Response");
        } else if (tokenRepository.findByToken(jwtToken).get().isExpired() &&
                   tokenRepository.findByToken(jwtToken).get().isRevoked() ){
            log.error("Token is not valid");
            response.addHeader("JWT_ERROR", jwtToken);
            response.sendError(HttpServletResponse.SC_FORBIDDEN);
            log.info("Sending 403 Response");
        } else {
            log.info("Token are valid and present in database");
            try {
                log.info("Starting authentication");
                username = jwtService.extractUsername(jwtToken);
                if (username != null && SecurityContextHolder.getContext().getAuthentication() == null) {
                    log.info("Username extraction");
                    UserDetails userDetails = this.userDetailsService.loadUserByUsername(username);
                    log.info("User Details: ", userDetails);
                    var isTokenValid = tokenRepository.findByToken(jwtToken)
                            .map(t -> !t.isExpired() && !t.isRevoked())
                            .orElse(false);
                    if (jwtService.isTokenValid(jwtToken, userDetails.getUsername().toLowerCase()) && isTokenValid) {
                        UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(
                                userDetails,
                                null,
                                userDetails.getAuthorities()
                        );
                        log.info("authtoken Setting details");
                        authToken.setDetails(
                                new WebAuthenticationDetailsSource().buildDetails(request)
                        );
                        log.info("Security Context setting authentication");
                        SecurityContextHolder.getContext().setAuthentication(authToken);
                    }
                }
                log.info("JWT authentication passed");
                log.info("Authentication [End]");
                filterChain.doFilter(request, response);
            } catch (ExpiredJwtException e) {
                log.error("ExpiredJwtException : ", jwtToken);
                Token corrupted_token = tokenRepository.findByToken(jwtToken).get();
                log.info("Setting token as expired");
                corrupted_token.setExpired(true);
                corrupted_token.setRevoked(true);
                log.info("Saving token to database");
                tokenRepository.save(corrupted_token);

                log.info("Sending 403 Response");
                response.addHeader("JWT_ERROR", jwtToken);
                response.sendError(HttpServletResponse.SC_FORBIDDEN);
                log.info("Sending 403 Response");
            } catch (ObjectOptimisticLockingFailureException ex) {
                log.error("ObjectOptimisticLockingFailureException");
                response.addHeader("JWT_ERROR", jwtToken);
                response.sendError(HttpServletResponse.SC_FORBIDDEN);
                log.info("Sending 403 Response");
            }



        }

        log.info("Auth Filter [[End]]");


    }
}
