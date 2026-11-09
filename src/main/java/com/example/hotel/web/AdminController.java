package com.example.hotel.web;

import com.example.hotel.domain.*;
import com.example.hotel.service.*;
import com.example.hotel.pattern.facade.BookingFacade;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import java.security.Principal;
import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;

@Controller
@RequestMapping("/admin")
public class AdminController {
 private final RoomService rooms;
 private final BookingService bookings;
 private final BookingFacade facade;
 private final PaymentService payments;

 public AdminController(RoomService rooms, BookingService bookings, BookingFacade facade, PaymentService payments){
  this.rooms=rooms; this.bookings=bookings; this.facade=facade; this.payments=payments;
 }
 @GetMapping
 public String home(Model model){
  var all=bookings.all();
  Map<Long,String> paymentByBooking=new HashMap<>();
  for(Booking booking:all){
   Payment payment=payments.findForBooking(booking.getId());
   paymentByBooking.put(booking.getId(),payment==null?"NOT_SELECTED":payment.getStatus().name());
  }
  model.addAttribute("rooms",rooms.allRooms());
  model.addAttribute("bookings",all);
  model.addAttribute("paymentByBooking",paymentByBooking);
  model.addAttribute("pendingCount",all.stream().filter(b->b.getStatus()==BookingStatus.PENDING).count());
  return "admin";
 }
 @PostMapping("/rooms/{id}/toggle")
 public String toggle(@PathVariable Long id,@RequestParam boolean active,RedirectAttributes flash){
  try{rooms.setActive(id,active);flash.addFlashAttribute("notice","Đã thay đổi trạng thái phòng");}
  catch(IllegalArgumentException e){flash.addFlashAttribute("error",e.getMessage());}
  return "redirect:/admin";
 }
 @PostMapping("/rooms")
 public String create(@RequestParam String number,@RequestParam RoomType type,@RequestParam BigDecimal baseRate,@RequestParam int capacity,RedirectAttributes flash){
  try{rooms.add(number,type,baseRate,capacity);flash.addFlashAttribute("notice","Đã thêm phòng");}
  catch(IllegalArgumentException e){flash.addFlashAttribute("error",e.getMessage());}
  return "redirect:/admin";
 }
 @PostMapping("/bookings/{id}/confirm")
 public String confirm(@PathVariable Long id,RedirectAttributes flash){
  try{facade.confirmByAdmin(id);flash.addFlashAttribute("notice","Đã xác nhận đơn #"+id+". Khách hàng đã nhận thông báo.");}
  catch(IllegalArgumentException|IllegalStateException e){flash.addFlashAttribute("error",e.getMessage());}
  return "redirect:/admin";
 }
 @PostMapping("/bookings/{id}/cancel")
 public String cancel(@PathVariable Long id,Principal principal,RedirectAttributes flash){
  try{facade.cancel(principal.getName(),id,true);flash.addFlashAttribute("notice","Đã hủy đơn #"+id+". Nếu đơn đã thanh toán demo, hệ thống ghi nhận hoàn tiền mô phỏng.");}
  catch(IllegalArgumentException|IllegalStateException e){flash.addFlashAttribute("error",e.getMessage());}
  return "redirect:/admin";
 }
}
