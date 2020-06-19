package com.app.carrent.model;

import lombok.Data;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import javax.persistence.*;
import javax.validation.constraints.*;
import java.util.Collection;
import java.util.Collections;


@Entity
@Data
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
    private boolean isEnabled;
    private boolean isNonLocked = true;

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return Collections.singleton(new SimpleGrantedAuthority(role.getRole()));
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
        return isNonLocked;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return isEnabled;
    }


    public enum Role{
        ADMIN("ROLE_ADMIN"),USER("ROLE_USER");

        Role(String role) {
            this.role = role;
        }
        final String role;

        public String getRole() {
            return role;
        }
    }
}
