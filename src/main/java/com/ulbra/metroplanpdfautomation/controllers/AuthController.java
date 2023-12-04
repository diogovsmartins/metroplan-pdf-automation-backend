package com.ulbra.metroplanpdfautomation.controllers;

import com.ulbra.metroplanpdfautomation.domain.DTOs.UserEntityDTO;
import com.ulbra.metroplanpdfautomation.services.AuthService;
import lombok.*;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/auth")
public class AuthController {

  private final AuthService authServices;

  @PostMapping(value = "/login")
  public ResponseEntity login(@RequestBody UserEntityDTO data) {
    return ResponseEntity.ok(authServices.login(data));
  }

  @PutMapping(value = "/refresh")
  public ResponseEntity refreshToken(@RequestHeader("Authorization") String refreshToken) {
    return ResponseEntity.ok(authServices.refreshToken(refreshToken));
  }

  @GetMapping(value = "/get-token-info")
  public ResponseEntity getTokenInfo(@RequestHeader("Authorization") String token) {
    return ResponseEntity.ok(authServices.getTokenInfo(token));
  }
}
