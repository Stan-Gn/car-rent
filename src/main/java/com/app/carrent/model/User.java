package com.app.carrent.model;

import lombok.Getter;
import lombok.NonNull;
import lombok.Setter;

import javax.persistence.*;
import javax.validation.constraints.*;


@Entity
@Getter
@Setter
public class User {
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

    public enum Role{
        ADMIN, USER;
    }
}
