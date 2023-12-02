package com.ulbra.metroplanpdfautomation.config.authentication.jwt;

import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.ulbra.metroplanpdfautomation.domain.DTOs.TokenDTO;
import java.util.*;
import javax.annotation.*;
import lombok.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.*;
import org.springframework.security.core.*;
import org.springframework.security.core.userdetails.*;
import org.springframework.stereotype.*;

@Service
@RequiredArgsConstructor
@Slf4j
public class JwtTokenService {

  private final UserDetailsService userDetailsService;

  @Value("${security.jwt.token.secretKey}")
  private String secretKey;

  @Value("${security.jwt.token.validityInMilliseconds}")
  private Long validityInMilliseconds;

  @PostConstruct
  public void init() {
    secretKey = Base64.getEncoder().encodeToString(secretKey.getBytes());
  }

  public TokenDTO createTokenVO(String username, List<String> roles) {
    return TokenDTO.builder()
        .username(username)
        .authenticated(true)
        .jwt(generateJWT(username, roles))
        .refreshJWT(generateRefreshJWT(username, roles))
        .build();
  }

  public TokenDTO refreshTokenVO(String refreshJwt) {
    refreshJwt = resolveToken(refreshJwt);
    DecodedJWT decodedJWT = decodedToken(refreshJwt);
    String username = decodedJWT.getSubject();
    List<String> roles = decodedJWT.getClaim("roles").asList(String.class);
    return createTokenVO(username, roles);
  }

  public TokenDTO getTokenInfo(String token) {
    token = resolveToken(token);
    if (!validateToken(token)) {
      return TokenDTO.builder().jwt(token).authenticated(false).build();
    }
    String username = decodedToken(token).getSubject();
    return TokenDTO.builder().jwt(token).authenticated(true).username(username).build();
  }

  private String generateJWT(String username, List<String> roles) {
    Date issuedAt = new Date();
    Date expiresAt = new Date(issuedAt.getTime() + validityInMilliseconds);
    return JWT.create()
        .withClaim("roles", roles)
        .withIssuedAt(issuedAt)
        .withExpiresAt(expiresAt)
        .withSubject(username)
        .sign(Algorithm.HMAC512(secretKey))
        .strip();
  }

  private String generateRefreshJWT(String username, List<String> roles) {
    Date issuedAt = new Date();
    Date expiresAt = new Date(issuedAt.getTime() + (validityInMilliseconds * 4));
    return JWT.create()
        .withClaim("roles", roles)
        .withIssuedAt(issuedAt)
        .withExpiresAt(expiresAt)
        .withSubject(username)
        .sign(Algorithm.HMAC512(secretKey))
        .strip();
  }

  private DecodedJWT decodedToken(String token) {
    Algorithm alg = Algorithm.HMAC512(secretKey.getBytes());
    JWTVerifier verifier = JWT.require(alg).build();
    return verifier.verify(token);
  }

  protected String resolveToken(String authHeader) {
    if (authHeader != null) {
      if (authHeader.startsWith("Bearer ")) {
        return authHeader.substring("Bearer ".length());
      }
      return authHeader;
    }
    return null;
  }

  protected Authentication getAuthentication(String token) {
    DecodedJWT decodedJWT = decodedToken(token);
    UserDetails userDetails = userDetailsService.loadUserByUsername(decodedJWT.getSubject());
    return new UsernamePasswordAuthenticationToken(
        userDetails.getUsername(), "", userDetails.getAuthorities());
  }

  protected Boolean validateToken(String token) {
    try {
      DecodedJWT decodedJWT = decodedToken(token);
      if (decodedJWT.getExpiresAt().before(new Date())) {
        log.warn("Expired Token!");
        return false;
      }
      return true;
    } catch (Exception e) {
      log.error("Invalid Token!");
      return false;
    }
  }
}
