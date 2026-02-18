package com.revpayproject.revpay.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "passwords")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Password {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String siteName;

    private String siteUrl;

    private String username;

    private String password;

    private String notes;

    // Many passwords belong to one user
    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;
}

