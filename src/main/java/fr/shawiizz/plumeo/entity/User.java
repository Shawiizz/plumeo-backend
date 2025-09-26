package fr.shawiizz.plumeo.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.Instant;
import java.util.LinkedHashSet;
import java.util.Set;

@Entity
@Getter
@Setter
@Table(name = "users")
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(nullable = false)
    private Long id;

    @Column(name = "username")
    private String username;

    @Column(name = "email")
    private String email;

    @Column(name = "password")
    private String password;

    @OneToMany
    private Set<User> followers = new LinkedHashSet<>();

    @OneToMany
    private Set<User> following = new LinkedHashSet<>();

    @OneToMany
    private Set<User> blockedUsers = new LinkedHashSet<>();

    @Column(name = "password_change_token")
    private String passwordChangeToken;

    @Column(name = "password_change_token_created_at")
    private Instant passwordChangeTokenCreatedAt;

    @Column(name = "account_verify_token")
    private String accountVerifyToken;

    @Column(name = "language")
    private String language;

    @Column(name = "last_login")
    private Instant lastLogin;

    @Column(name = "created_at")
    private Instant createdAt = Instant.now();

}
