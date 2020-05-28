package com.app.carrent.model;

import lombok.Getter;
import lombok.NonNull;
import lombok.Setter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import javax.persistence.*;
import javax.validation.constraints.*;
import java.util.Collection;


@Entity
@Getter
@Setter
public class User implements UserDetails {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private long id;
    @NotBlank(message = "First name required field")
    private String name;
    @NotBlank(message ="Surname required field" )
    private String surname;
    @Size(min = 6,message = "The password should contain more than 5 characters")
    private String password;
    @Email(message = "Email should be valid")
    private String email;
    @Enumerated(EnumType.STRING)
    private Role role;
    private boolean isEnable;

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return null;
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
        return isEnable;
    }

    public enum Role{
        ADMIN, USER;
    }
}
