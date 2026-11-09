package com.example.hotel.domain;
import jakarta.persistence.*;
import java.time.LocalDateTime;
@Entity @Table(name="notifications")
public class Notification {
 @Id @GeneratedValue(strategy=GenerationType.IDENTITY) private Long id;
 @ManyToOne(optional=false,fetch=FetchType.LAZY) private AppUser customer;
 @Column(nullable=false,length=500) private String message;
 @Column(nullable=false) private LocalDateTime createdAt=LocalDateTime.now();
 protected Notification(){} public Notification(AppUser customer,String message){this.customer=customer;this.message=message;}
 public Long getId(){return id;} public String getMessage(){return message;} public LocalDateTime getCreatedAt(){return createdAt;}
}
