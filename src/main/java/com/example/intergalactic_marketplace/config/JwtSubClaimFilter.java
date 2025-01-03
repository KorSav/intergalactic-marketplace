package com.example.intergalactic_marketplace.config;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.server.resource.web.BearerTokenAuthenticationEntryPoint;
import org.springframework.security.web.authentication.AuthenticationEntryPointFailureHandler;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;
import org.springframework.web.filter.OncePerRequestFilter;

public class JwtSubClaimFilter extends OncePerRequestFilter {

  private static final String MISSING_SUB_CLAIM_MESSAGE = "Missing 'customerId' claim in JWT";

  private final BearerTokenAuthenticationEntryPoint authenticationEntryPoint =
      new BearerTokenAuthenticationEntryPoint();

  private final AuthenticationFailureHandler authenticationFailureHandler =
      new AuthenticationEntryPointFailureHandler(authenticationEntryPoint);

  private final JwtDecoder jwtDecoder;

  public JwtSubClaimFilter(JwtDecoder jwtDecoder) {
    this.jwtDecoder = jwtDecoder;
  }

  @Override
  protected void doFilterInternal(
      HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
      throws ServletException, IOException {
    String token = request.getHeader("Authorization");

    // Skip the filter if the request is publicly accessible
    if (SecurityContextHolder.getContext().getAuthentication() == null || token == null) {
      // This means the request hasn't been authenticated, so it's public
      filterChain.doFilter(request, response);
      return;
    }

    if (token != null && token.startsWith("Bearer ")) {
      token = token.substring(7); // Remove "Bearer " prefix
    }
    if (token == null) {
      logger.trace("No bearer token present");
      authenticationEntryPoint.commence(request, response, null);
      return;
    }

    try {
      Jwt jwt = jwtDecoder.decode(token);
      Object subClaim = jwt.getClaim("customerId");

      // Check if the 'sub' claim is present
      if (subClaim == null) {
        logger.trace(MISSING_SUB_CLAIM_MESSAGE);
        authenticationFailureHandler.onAuthenticationFailure(
            request, response, new OAuth2AuthenticationException(MISSING_SUB_CLAIM_MESSAGE));
        return;
      }

      // Check if 'sub' is already a number
      if (subClaim instanceof Number) {
        Long customerId = ((Number) subClaim).longValue();
        logger.trace(
            String.format(
                "Successfully validated 'customerId' claim with customer ID: %s", customerId));
      }
      // Validate that 'sub' is a string and can be parsed into a Long
      else if (subClaim instanceof String) {
        try {
          Long customerId = Long.parseLong((String) subClaim);
          logger.trace(
              String.format(
                  "Successfully validated 'customerId' claim with customer ID: %s", customerId));
        } catch (NumberFormatException e) {
          logger.trace("Invalid 'customerId' claim: not a valid number", e);
          authenticationFailureHandler.onAuthenticationFailure(
              request, response, new OAuth2AuthenticationException("Invalid 'customerId' claim"));
          return;
        }
      } else {
        logger.trace("Invalid 'customerId' claim: not a valid number or string");
        authenticationFailureHandler.onAuthenticationFailure(
            request, response, new OAuth2AuthenticationException("Invalid 'customerId' claim"));
        return;
      }

      logger.trace("Successfully validated 'customerId' claim in JWT");
      filterChain.doFilter(request, response);

    } catch (AuthenticationException ex) {
      logger.trace("Failed to validate JWT", ex);
      authenticationFailureHandler.onAuthenticationFailure(request, response, ex);
    }
  }
}
