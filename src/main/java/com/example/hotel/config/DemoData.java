package com.example.hotel.config;
import com.example.hotel.domain.*;
import com.example.hotel.repository.*;
import com.example.hotel.service.RoomService;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;
import java.math.BigDecimal;
@Configuration
public class DemoData {
 @Bean CommandLineRunner seed(UserRepository users,RoomRepository rooms,RoomService roomService,PasswordEncoder encoder){return args->{
  // DEMO ONLY! Change credentials before any public deployment.
  if(!users.existsByUsername("admin"))users.save(new AppUser("admin","admin@hotel.local",encoder.encode("Admin@12345"),Role.ADMIN));
  if(!users.existsByUsername("demo"))users.save(new AppUser("demo","demo@hotel.local",encoder.encode("Demo@12345"),Role.CUSTOMER));
  if(rooms.count()==0){
   roomService.add("A101",RoomType.STANDARD,new BigDecimal("550000"),2);
   roomService.add("A102",RoomType.STANDARD,new BigDecimal("600000"),2);
   roomService.add("A103",RoomType.STANDARD,new BigDecimal("650000"),2);
   roomService.add("A104",RoomType.STANDARD,new BigDecimal("690000"),2);
   roomService.add("B201",RoomType.DELUXE,new BigDecimal("950000"),3);
   roomService.add("B202",RoomType.DELUXE,new BigDecimal("1050000"),3);
   roomService.add("B203",RoomType.DELUXE,new BigDecimal("1150000"),3);
   roomService.add("C301",RoomType.SUITE,new BigDecimal("1500000"),4);
   roomService.add("C302",RoomType.SUITE,new BigDecimal("1700000"),4);
  }
 };}
}
