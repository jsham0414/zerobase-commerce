package com.zerobase.commerce.database.domain;

import jakarta.persistence.*;

@Entity
@Table(name = "AUTHORITY")
public class Authority {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "user_id")
    private String userId;

    @Column(name = "role")
    private String role;
}
