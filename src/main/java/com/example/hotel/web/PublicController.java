package com.example.hotel.web;
import com.example.hotel.service.*;
import com.example.hotel.domain.RoomType;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import java.time.LocalDate;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Comparator;
@Controller
public class PublicController {
 private final RoomService rooms;private final AccountService accounts;
 public PublicController(RoomService rooms,AccountService accounts){this.rooms=rooms;this.accounts=accounts;}
 @GetMapping("/") public String home(){return "redirect:/rooms";}
 @GetMapping("/rooms") public String rooms(@RequestParam(required=false) LocalDate checkIn,
  @RequestParam(required=false) LocalDate checkOut,@RequestParam(required=false) Integer guests,
  @RequestParam(required=false) RoomType type,@RequestParam(required=false) Long maxPrice,
  @RequestParam(defaultValue="recommended") String sort,Model model){
  java.util.List<com.example.hotel.domain.Room> matches;
  try{matches=new ArrayList<>(rooms.search(checkIn,checkOut,guests));}
  catch(IllegalArgumentException e){model.addAttribute("error",e.getMessage());matches=new ArrayList<>(rooms.availableCatalog());}
  if(type!=null)matches.removeIf(r->r.getType()!=type);
  if(maxPrice!=null && maxPrice>0)matches.removeIf(r->r.getNightlyRate().compareTo(BigDecimal.valueOf(maxPrice))>0);
  switch(sort){
   case "price-asc" -> matches.sort(Comparator.comparing(com.example.hotel.domain.Room::getNightlyRate));
   case "price-desc" -> matches.sort(Comparator.comparing(com.example.hotel.domain.Room::getNightlyRate).reversed());
   case "capacity" -> matches.sort(Comparator.comparingInt(com.example.hotel.domain.Room::getCapacity).reversed());
   default -> { /* Keep catalog order */ }
  }
  model.addAttribute("rooms",matches);
  model.addAttribute("requestedDates",checkIn!=null && checkOut!=null);
  model.addAttribute("searchFrom",checkIn==null?LocalDate.now().plusDays(1):checkIn);
  model.addAttribute("searchTo",checkOut==null?(checkIn==null?LocalDate.now().plusDays(2):checkIn.plusDays(1)):checkOut);
  model.addAttribute("searchGuests",guests==null?2:guests);
  model.addAttribute("selectedType",type);
  model.addAttribute("selectedMaxPrice",maxPrice);
  model.addAttribute("selectedSort",sort);
  return "rooms";
 }
 @GetMapping("/rooms/{id}") public String detail(@PathVariable Long id,@RequestParam(required=false) LocalDate checkIn,
  @RequestParam(required=false) LocalDate checkOut,@RequestParam(required=false) Integer guests,Model model){
  com.example.hotel.domain.Room room;
  try{room=rooms.findPublic(id);}
  catch(IllegalArgumentException e){return "redirect:/rooms";}
  model.addAttribute("room",room);
  model.addAttribute("searchFrom",checkIn==null?LocalDate.now().plusDays(1):checkIn);
  model.addAttribute("searchTo",checkOut==null?(checkIn==null?LocalDate.now().plusDays(2):checkIn.plusDays(1)):checkOut);
  model.addAttribute("searchGuests",guests==null||guests>room.getCapacity()?Math.min(2,room.getCapacity()):guests);
  return "room-detail";
 }
 @GetMapping("/profile") public String profile(java.security.Principal principal,Model model){
  model.addAttribute("account",accounts.findProfile(principal.getName()));return "profile";
 }
 @GetMapping("/login") public String login(java.security.Principal principal){return principal!=null?"redirect:/rooms":"login";}
 @GetMapping("/register") public String register(){return "register";}
 @PostMapping("/register") public String createAccount(@RequestParam String username,@RequestParam String email,@RequestParam String password,Model model){
  try{accounts.register(username,email,password);return "redirect:/login?registered";}
  catch(IllegalArgumentException e){model.addAttribute("error",e.getMessage());return "register";}
 }
}
