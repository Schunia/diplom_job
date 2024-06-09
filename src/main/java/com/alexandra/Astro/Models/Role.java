package com.alexandra.Astro.Models;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.security.core.GrantedAuthority;

import javax.persistence.*;

public enum Role implements GrantedAuthority {
    ROLE_USER("Пользователь"),
    ROLE_ADMIN("Админ"),
    ROLE_STUDENT("Ученик"),
    ROLE_TEACHER("Учитель");

    private final String displayValue;

    private Role(String displayValue) {
        this.displayValue = displayValue;
    }

    public String getDisplayValue() {
        return displayValue;
    }

    @Override
    public String getAuthority() {
        return name();
    }
}