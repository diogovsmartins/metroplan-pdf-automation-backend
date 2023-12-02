package com.ulbra.metroplanpdfautomation.config.authentication.jwt;

import java.io.*;
import javax.servlet.*;
import javax.servlet.http.*;
import lombok.*;
import org.springframework.security.core.*;
import org.springframework.security.core.context.*;
import org.springframework.stereotype.*;
import org.springframework.web.filter.*;

@Component
@RequiredArgsConstructor
public class JwtTokenFilter extends OncePerRequestFilter {

  private final JwtTokenService tokenService;

  @Override
  protected void doFilterInternal(
      HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
      throws ServletException, IOException {
    final String token = tokenService.resolveToken((request.getHeader("Authorization")));
    if (token != null && tokenService.validateToken(token)) {
      Authentication auth = tokenService.getAuthentication(token);
      if (auth != null) {
        SecurityContextHolder.getContext().setAuthentication(auth);
      }
    }
    filterChain.doFilter(request, response);
  }
}
