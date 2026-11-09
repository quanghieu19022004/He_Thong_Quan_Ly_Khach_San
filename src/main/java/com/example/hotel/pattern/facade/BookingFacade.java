package com.example.hotel.pattern.facade;
import com.example.hotel.domain.*;
import com.example.hotel.service.*;
import org.springframework.stereotype.Service;import java.time.LocalDate;
@Service
public class BookingFacade {
 private final BookingService bookingService;private final PaymentService paymentService;
 public BookingFacade(BookingService bookings,PaymentService payments){this.bookingService=bookings;this.paymentService=payments;}
 public Booking reserve(String username,Long roomId,LocalDate from,LocalDate to,int guests,PricingPlan plan){return bookingService.reserve(username,roomId,from,to,guests,plan);}
 public Payment pay(String username,Long bookingId,PaymentMethod method){return paymentService.pay(bookingId,username,method);}
 public void cancel(String username,Long bookingId,boolean admin){bookingService.cancel(bookingId,username,admin);}
 public void confirmByAdmin(Long bookingId){bookingService.confirmByAdmin(bookingId);}
}
