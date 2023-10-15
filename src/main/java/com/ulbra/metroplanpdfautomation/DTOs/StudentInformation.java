package com.ulbra.metroplanpdfautomation.DTOs;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

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
