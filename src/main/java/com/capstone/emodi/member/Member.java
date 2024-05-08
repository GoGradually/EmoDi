package com.capstone.emodi.member;

import jakarta.persistence.*;
import jakarta.validation.constraints.Email;

@Entity
@Table(uniqueConstraints = {
        @UniqueConstraint(columnNames = "username")
})
public class Member {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "member_id")
    private Long id;

    @Column(nullable = false, length = 20)
    private String username;

    @Column(nullable = false)
    private String password;


    @Email
    @Column(nullable = false)
    private String email;


    @Column(nullable = false)
    private String tellNumber;



}
