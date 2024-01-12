package com.woody.shop.security;

import com.woody.mydata.UserDT;
import com.woody.mydata.UserDTO;
import com.woody.shop.service.ShopService;
import com.woody.shop.service.TokenService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.List;
import java.util.NoSuchElementException;

@Component
@Slf4j
@AllArgsConstructor
public class AuthFilter extends OncePerRequestFilter {

    private ShopService shopService;
    private TokenService tokenService;

    @Override
    protected void doFilterInternal
            (@NotNull HttpServletRequest request,
             @NotNull HttpServletResponse response,
             @NotNull FilterChain filterChain)
            throws ServletException, IOException
    {



        try {
            log.info("Auth Filter [Started]");
            String authorizationHeader = request.getHeader("Authorization");
            System.out.println("FFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFF" + authorizationHeader);
            String jwtToken;

            if (authorizationHeader == null || !authorizationHeader.startsWith("Bearer ")) {
                log.error("Token is not present in request");
                filterChain.doFilter(request, response);
                log.info("Redirect to another filter");
                return;
            }

            jwtToken = authorizationHeader.substring(7);
            log.info("Token extracted");

            if (SecurityContextHolder.getContext().getAuthentication() == null) {
                shopService.getPublicKey();
                log.info("SecurityContextHolder.getContext().getAuthentication() == null");
                jwtToken =  shopService.verifyToken(jwtToken);
                log.info("Token verified");

                String Username = tokenService.extractUsername(jwtToken);
                log.info("Username extracted");
                List<GrantedAuthority> authorities = tokenService.extractAuthorities(jwtToken);

                UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(
                        Username,
                        null,
                        authorities
                );
                log.info("Authentication created");

                authToken.setDetails(
                        new WebAuthenticationDetailsSource().buildDetails(request)
                );

                // Set Authentication in the SecurityContext
                SecurityContextHolder.getContext().setAuthentication(authToken);
                log.info("Authentication set");
            }

            filterChain.doFilter(request, response);

        } catch (NoSuchElementException e) {
            log.error("User not found");
            //response.addHeader("JWT_ERROR", jwtToken);
            response.sendError(HttpServletResponse.SC_FORBIDDEN);
        } catch (Exception e) {
            //response.addHeader("JWT_ERROR", jwtToken);
            response.sendError(HttpServletResponse.SC_FORBIDDEN);
            log.error("Something went wrong with AUTH: " + e.getMessage());
        }
    }
}
