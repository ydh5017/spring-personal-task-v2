package com.sparta.springpersonaltaskv2.entity;

import com.sparta.springpersonaltaskv2.enums.UserRoleType;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor
@Table(name = "users")
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String username;

    @Column(nullable = false)
    private String password;

    @Column(nullable = false, unique = true)
    private String email;

    @Column(nullable = false)
    @Enumerated(value = EnumType.STRING)
    private UserRoleType role;

    public User(String username, String password, String email, UserRoleType role) {
        this.username = username;
        this.password = password;
        this.email = email;
        this.role = role;
    }
}