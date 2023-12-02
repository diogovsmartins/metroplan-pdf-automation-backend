package com.ulbra.metroplanpdfautomation.config.authentication.entities;

import com.ulbra.metroplanpdfautomation.domain.entities.UserEntity;
import java.util.*;
import java.util.stream.Collectors;
import lombok.*;
import org.springframework.security.core.*;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.*;

@AllArgsConstructor
public class CustomUserDetails implements UserDetails, GrantedAuthority {

  private UserEntity user;

  @Override
  public Collection<? extends GrantedAuthority> getAuthorities() {
    // Retorna a autoridade associada ao usuário
    return Arrays.stream(user.getRoles().split(","))
        .map(SimpleGrantedAuthority::new)
        .collect(Collectors.toList());
  }

  @Override
  public String getAuthority() {
    // Retorna a autoridade associada ao usuário
    return Arrays.stream(user.getRoles().split(","))
        .map(SimpleGrantedAuthority::new)
        .collect(Collectors.toList())
        .toString();
  }

  @Override
  public String getPassword() {
    return user.getPassword();
  }

  @Override
  public String getUsername() {
    return user.getEmail();
  }

  @Override
  public boolean isAccountNonExpired() {
    return true;
  }

  @Override
  public boolean isAccountNonLocked() {
    return true;
  }

  @Override
  public boolean isCredentialsNonExpired() {
    return true;
  }

  @Override
  public boolean isEnabled() {
    return true;
  }
}
