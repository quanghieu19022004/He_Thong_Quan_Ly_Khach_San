package com.example.hotel.domain;
import jakarta.persistence.*;
@Entity @Table(name="app_users")
public class AppUser {
 @Id @GeneratedValue(strategy=GenerationType.IDENTITY) private Long id;
 @Column(nullable=false,unique=true,length=40) private String username;
 @Column(nullable=false,unique=true) private String email;
 @Column(nullable=false) private String passwordHash;
 @Enumerated(EnumType.STRING) @Column(nullable=false) private Role role=Role.CUSTOMER;
 public AppUser() {}
 public AppUser(String username,String email,String passwordHash,Role role) {this.username=username;this.email=email;this.passwordHash=passwordHash;this.role=role;}
 public Long getId(){return id;} public String getUsername(){return username;} public String getEmail(){return email;}
 public String getPasswordHash(){return passwordHash;} public Role getRole(){return role;}
}
