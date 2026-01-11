package com.chaostamer.infrastructure.adapter.in.rest.filter;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import com.chaostamer.infrastructure.adapter.out.security.jwt.JwtTokenProvider;
import com.chaostamer.infrastructure.adapter.out.security.service.CustomUserDetailsService;

import java.io.IOException;

@Component
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {

  private final JwtTokenProvider tokenProvider;
  private final CustomUserDetailsService customUserDetailsService;

  @Override
  protected void doFilterInternal(HttpServletRequest request,
                                  HttpServletResponse response,
                                  FilterChain filterChain
                                  ) throws ServletException, IOException {

    // Obtain the token from the header
    String token = getJwtFromRequest(request);

    if (StringUtils.hasText(token) && tokenProvider.validateToken(token)) {
      // Obtain the email of the token
      String email = tokenProvider.getEmailFromToken(token);
      // Load the user from the database
      UserDetails userDetails = customUserDetailsService.loadUserByUsername(email);
      // Create the authentication object
      UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(
        userDetails, null, userDetails.getAuthorities()
      );

      authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));

      // Stablish tje authentication in the security context
      SecurityContextHolder.getContext().setAuthentication(authentication);
    }

    // Continue with rest of the filters
    filterChain.doFilter(request, response);
  }

  // Private method to extract the token from the "bearer"
  private String getJwtFromRequest(HttpServletRequest request) {
    String bearerToken = request.getHeader("Authorization");

    if (StringUtils.hasText(bearerToken) && bearerToken.startsWith("Bearer ")) {
      return bearerToken.substring(7); // Return only the token
    }
    return null;
  }
}
