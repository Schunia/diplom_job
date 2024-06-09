package com.alexandra.Astro.Models;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import javax.persistence.*;
import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

@Entity
@NoArgsConstructor
@Getter
@Setter
public class User implements UserDetails{
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
//    @NotBlank
//    @Size(min=2, max=20, message = "Логин должен содержать от 2 до 20 символов")
//    private String username;
//    @NotBlank
//    @Size(min=2, max=20, message = "Фамилия должна содержать от 2 до 20 символов")
//    private String lastName;
//    @NotBlank
//    @Size(min=2, max=20, message = "Имя должно содержать от 2 до 20 символов")
//    private String firstName;
    @NotBlank
    @Size(min=2, max=20, message = "Логин должен содержать от 2 до 20 символов")
    @Column(unique = true)
    private String login;
    @NotBlank
    @Email(message = "Email не корректен")
    @Column(unique = true, updatable = false)
    private String email;
    @NotBlank
    @NotNull(message = "Пароль должен содержать от 6 до 12 символов")
    private String password;
    @Transient
    private String passwordConfirm;
    private boolean active;
    //private String activationCode;
    private String pathPhoto;
    private Long teacherId;

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL)
    private Set<UserElectedMeeting> userElectedMeetings = new HashSet<>();

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL)
    private List<Observation> observations;

    @ElementCollection(targetClass = Role.class, fetch = FetchType.EAGER)
    @CollectionTable(name = "user_role",
            joinColumns = @JoinColumn(name = "user_id"))
    @Enumerated(EnumType.STRING)
    private Set<Role> roles = new HashSet<>();

    public boolean isAdmin() {
        return roles.contains(Role.ROLE_ADMIN);
    }

    public User(Long id, String email){
        this.id = id;
        this.email = email;
    }

    public User(String login, String email, String password, boolean active, String pathPhoto, Long teacherId, Set<Role> roles, UserElectedMeeting... userElectedMeetings) {
        this.login = login;
        this.email = email;
        this.password = password;
        this.active = active;
        this.pathPhoto = pathPhoto;
        this.teacherId = teacherId;
        this.roles = roles;
        for(UserElectedMeeting userElectedMeeting : userElectedMeetings) userElectedMeeting.setUser(this);
        this.userElectedMeetings = Stream.of(userElectedMeetings).collect(Collectors.toSet());
    }

    // security config

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return roles;
    }

    @Override
    public String getPassword() {
        return password;
    }

    @Override
    public String getUsername() {
        return email;
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
        return active;
    }
}
