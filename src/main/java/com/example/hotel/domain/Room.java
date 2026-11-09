package com.example.hotel.domain;
import jakarta.persistence.*;
import java.math.BigDecimal;
@Entity @Table(name="rooms",uniqueConstraints=@UniqueConstraint(columnNames="room_number"))
public class Room {
 @Id @GeneratedValue(strategy=GenerationType.IDENTITY) private Long id;
 @Column(name="room_number",nullable=false,length=12) private String number;
 @Enumerated(EnumType.STRING) @Column(nullable=false) private RoomType type;
 @Column(nullable=false,precision=14,scale=2) private BigDecimal nightlyRate;
 @Column(nullable=false) private int capacity;
 @Column(nullable=false) private boolean active=true;
 public Room(){}
 public Room(String number,RoomType type,BigDecimal nightlyRate,int capacity){this.number=number;this.type=type;this.nightlyRate=nightlyRate;this.capacity=capacity;}
 public Long getId(){return id;} public String getNumber(){return number;} public RoomType getType(){return type;}
 public BigDecimal getNightlyRate(){return nightlyRate;} public int getCapacity(){return capacity;} public boolean isActive(){return active;}
 public void setActive(boolean active){this.active=active;}
 @Transient public String getAmenities(){return switch(type){case STANDARD->"Wi-Fi, điều hòa, TV";case DELUXE->"Wi-Fi, điều hòa, TV, ban công";case SUITE->"Wi-Fi, điều hòa, phòng khách, ban công";};}
}
