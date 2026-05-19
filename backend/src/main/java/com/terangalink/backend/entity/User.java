package com.terangalink.backend.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@Table(name = "users")

@Getter
@Setter
@NoArgsConstructor

public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String firstName;

    private String lastName;

    @Column(unique = true, nullable = false)
    private String email;

    private String password;

    private String university;

    private String fieldOfStudy;

    private String city;

    private String role;

    private LocalDateTime createdAt = LocalDateTime.now();

}
