package com.ulbra.metroplanpdfautomation.config.authentication;

import com.ulbra.metroplanpdfautomation.config.authentication.jwt.JwtTokenFilter;
import com.ulbra.metroplanpdfautomation.domain.businessEnums.Roles;
import lombok.*;
import org.springframework.context.annotation.*;
import org.springframework.security.authentication.*;
import org.springframework.security.config.annotation.web.builders.*;
import org.springframework.security.config.annotation.web.configuration.*;
import org.springframework.security.config.http.*;
import org.springframework.security.web.*;
import org.springframework.security.web.authentication.*;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SpringSecurityConfig {

  private final JwtTokenFilter tokenFilter;
  private final AuthenticationProvider authenticationProvider;

  @Bean
  public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
    http.csrf()
        .disable()
        .authorizeHttpRequests()
        .antMatchers("/auth/**", "/h2-console/**", "/user/create**")
        .permitAll()
        .anyRequest()
        .authenticated()
        .and()
        .sessionManagement()
        .sessionCreationPolicy(SessionCreationPolicy.STATELESS)
        .and()
        .authenticationProvider(authenticationProvider)
        .addFilterBefore(tokenFilter, UsernamePasswordAuthenticationFilter.class);

    return http.build();
  }
}
