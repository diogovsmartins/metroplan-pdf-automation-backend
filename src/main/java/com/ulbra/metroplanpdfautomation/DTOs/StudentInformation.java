package com.ulbra.metroplanpdfautomation.DTOs;

import java.util.List;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Builder
@Getter
@Setter
public class StudentInformation {

  private final String name;
  private final String email;
  private final List<String> presencialDays;

  @Override
  public String toString() {
    return getPresencialDays().toString().replace("[", "").replace("]", "");
  }
}
