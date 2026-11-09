package com.example.hotel.web;
import com.example.hotel.domain.*;
import com.example.hotel.pattern.facade.BookingFacade;
import com.example.hotel.repository.NotificationRepository;
import com.example.hotel.service.*;
import java.security.Principal;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.math.BigDecimal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
@Controller
public class BookingController {
 private final BookingFacade facade;private final BookingService bookings;
 private final PaymentService payments;private final NotificationRepository notifications;
 public BookingController(BookingFacade facade,BookingService bookings,PaymentService payments,NotificationRepository notifications){
  this.facade=facade;this.bookings=bookings;this.payments=payments;this.notifications=notifications;
 }
 @PostMapping("/bookings") public String reserve(@RequestParam Long roomId,@RequestParam LocalDate checkIn,
  @RequestParam LocalDate checkOut,@RequestParam int guests,@RequestParam PricingPlan pricingPlan,Principal principal,RedirectAttributes flash){
  try{
   Booking b=facade.reserve(principal.getName(),roomId,checkIn,checkOut,guests,pricingPlan);
   flash.addFlashAttribute("notice","Đã tạo đơn #"+b.getId()+" với trạng thái CHỜ XÁC NHẬN. Vui lòng chọn phương thức thanh toán.");
   return "redirect:/bookings/"+b.getId()+"/checkout";
  }catch(IllegalArgumentException|IllegalStateException e){flash.addFlashAttribute("error",e.getMessage());return "redirect:/rooms";}
 }
 @GetMapping("/bookings") public String mine(Principal principal,Model model){
  var mine=bookings.mine(principal.getName());model.addAttribute("bookings",mine);
  java.util.Map<Long,String> statusByBooking=new java.util.HashMap<>();
  for(Booking b:mine){
   Payment payment=payments.findForBooking(b.getId());
   statusByBooking.put(b.getId(),payment==null?"PENDING":payment.getStatus().name());
  }
  model.addAttribute("paymentByBooking",statusByBooking);
  return "bookings";
 }
 @GetMapping("/bookings/{id}/checkout") public String checkout(@PathVariable Long id,Principal principal,Model model,RedirectAttributes flash){
  Booking b=bookings.ownedBy(principal.getName(),id);
  if(b.getStatus()==BookingStatus.CANCELLED || payments.findForBooking(id)!=null)
   return "redirect:/bookings/"+id+"/confirmation";
  addSummary(model,b);
  return "checkout";
 }
 @PostMapping("/bookings/{id}/pay") public String pay(@PathVariable Long id,@RequestParam PaymentMethod method,
  Principal principal,RedirectAttributes flash){
  try{
   Payment p=facade.pay(principal.getName(),id,method);
   flash.addFlashAttribute("notice",p.getStatus()==PaymentStatus.DUE_AT_PROPERTY
    ?"Đã ghi nhận thanh toán tại khách sạn. Đơn vẫn CHỜ ADMIN XÁC NHẬN."
    :"Đã ghi nhận thanh toán DEMO. Đơn vẫn CHỜ ADMIN XÁC NHẬN; không chuyển tiền thật.");
   return "redirect:/bookings/"+id+"/confirmation";
  }catch(IllegalArgumentException|IllegalStateException|SecurityException e){
   flash.addFlashAttribute("error",e.getMessage());return "redirect:/bookings/"+id+"/checkout";
  }
 }
 @GetMapping("/bookings/{id}/confirmation") public String confirmation(@PathVariable Long id,Principal principal,Model model){
  Booking b=bookings.ownedBy(principal.getName(),id);
  if(b.getStatus()==BookingStatus.PENDING && payments.findForBooking(id)==null)return "redirect:/bookings/"+id+"/checkout";
  addSummary(model,b);model.addAttribute("payment",payments.findForBooking(id));return "confirmation";
 }
 private void addSummary(Model model,Booking b){
  long nights=ChronoUnit.DAYS.between(b.getCheckIn(),b.getCheckOut());
  BigDecimal base=b.getRoom().getNightlyRate().multiply(BigDecimal.valueOf(nights));
  model.addAttribute("booking",b);model.addAttribute("nights",nights);
  model.addAttribute("baseTotal",base);model.addAttribute("planAdjustment",b.getTotal().subtract(base));
 }
 @PostMapping("/bookings/{id}/cancel") public String cancel(@PathVariable Long id,Principal principal,RedirectAttributes flash){
  try{facade.cancel(principal.getName(),id,false);flash.addFlashAttribute("notice","Đã hủy đơn #"+id+" thành công. Nếu đã thanh toán demo, hệ thống đã hoàn tiền mô phỏng (không có giao dịch thật).");}
  catch(IllegalArgumentException|IllegalStateException|SecurityException e){flash.addFlashAttribute("error",e.getMessage());}
  return "redirect:/bookings";
 }
 @GetMapping("/notifications") public String notifications(Principal principal,Model model){
  model.addAttribute("notifications",notifications.findTop30ByCustomerUsernameOrderByCreatedAtDesc(principal.getName()));return "notifications";
 }
}
