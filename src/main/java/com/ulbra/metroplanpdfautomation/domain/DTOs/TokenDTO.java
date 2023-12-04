package com.ulbra.metroplanpdfautomation.domain.DTOs;

import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TokenDTO {
  private String username;
  private Boolean authenticated;
  private String jwt;
  private String refreshJWT;
  private List<String> roles;
}
