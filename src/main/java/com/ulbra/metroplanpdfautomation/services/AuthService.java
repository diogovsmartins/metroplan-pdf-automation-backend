package com.ulbra.metroplanpdfautomation.services;

import com.ulbra.metroplanpdfautomation.config.authentication.jwt.JwtTokenService;
import com.ulbra.metroplanpdfautomation.domain.DTOs.TokenDTO;
import com.ulbra.metroplanpdfautomation.domain.DTOs.UserEntityDTO;
import com.ulbra.metroplanpdfautomation.domain.entities.UserEntity;
import com.ulbra.metroplanpdfautomation.repositories.UserRepository;
import java.util.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.*;
import org.springframework.security.authentication.*;
import org.springframework.stereotype.*;

@Service
@Slf4j
public class AuthService {
  private final AuthenticationManager authenticationManager;
  private final JwtTokenService jwtTokenService;
  private final UserRepository userRepository;

  @Autowired
  public AuthService(
      final AuthenticationManager authenticationManager,
      final JwtTokenService jwtTokenService,
      final UserRepository userRepository) {
    this.authenticationManager = authenticationManager;
    this.jwtTokenService = jwtTokenService;
    this.userRepository = userRepository;
  }

  public TokenDTO login(UserEntityDTO data) {
    String email = data.getEmail();
    String password = data.getPassword();

    try {
      authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(email, password));
    } catch (Exception e) {
      log.error("Invalid email/password supplied!");
      throw new BadCredentialsException("Invalid email/password supplied!");
    }

    UserEntity userEntity =
        userRepository.findByEmail(email).orElseThrow(() -> new NullPointerException());
    List<String> roles = List.of(userEntity.getRoles().split(", "));

    return jwtTokenService.createTokenVO(email, roles);
  }

  public TokenDTO refreshToken(String refreshJwt) {
    try {
      return jwtTokenService.refreshTokenVO(refreshJwt);
    } catch (Exception e) {
      log.error("Invalid token supplied!");
      throw new IllegalArgumentException("Invalid token supplied!");
    }
  }

  public TokenDTO getTokenInfo(String token) {
    return jwtTokenService.getTokenInfo(token);
  }
}
