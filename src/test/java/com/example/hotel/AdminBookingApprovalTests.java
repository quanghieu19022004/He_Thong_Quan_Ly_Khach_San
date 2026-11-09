package com.example.hotel;

import com.example.hotel.domain.*;
import com.example.hotel.pattern.observer.BookingEvent;
import com.example.hotel.pattern.observer.BookingEventPublisher;
import com.example.hotel.pattern.strategy.PricingService;
import com.example.hotel.repository.*;
import com.example.hotel.service.BookingService;
import org.junit.jupiter.api.Test;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Optional;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class AdminBookingApprovalTests {
 private Booking sample(){return Booking.builder()
  .customer(new AppUser("demo","demo@example.com","hash",Role.CUSTOMER))
  .room(new Room("A101",RoomType.STANDARD,new BigDecimal("550000"),2))
  .dates(LocalDate.now().plusDays(1),LocalDate.now().plusDays(3))
  .guests(2).pricingPlan(PricingPlan.STANDARD).total(new BigDecimal("1100000")).build();
 }
 private BookingService service(BookingRepository repo,BookingEventPublisher publisher){
  return new BookingService(mock(RoomRepository.class),repo,mock(UserRepository.class),
   mock(PaymentRepository.class),mock(PricingService.class),publisher);
 }
 @Test void newBookingMustStartPending(){
  assertEquals(BookingStatus.PENDING,sample().getStatus());
 }
 @Test void adminApprovalChangesPendingToConfirmedAndPublishesEvent(){
  Booking booking=sample();
  BookingRepository repo=mock(BookingRepository.class);
  BookingEventPublisher publisher=mock(BookingEventPublisher.class);
  when(repo.lockById(10L)).thenReturn(Optional.of(booking));
  service(repo,publisher).confirmByAdmin(10L);
  assertEquals(BookingStatus.CONFIRMED,booking.getStatus());
  verify(publisher).publish(any(BookingEvent.class));
 }
 @Test void adminCannotApproveAnAlreadyConfirmedBooking(){
  Booking booking=sample(); booking.confirm();
  BookingRepository repo=mock(BookingRepository.class);
  BookingEventPublisher publisher=mock(BookingEventPublisher.class);
  when(repo.lockById(10L)).thenReturn(Optional.of(booking));
  assertThrows(IllegalStateException.class,()->service(repo,publisher).confirmByAdmin(10L));
  verifyNoInteractions(publisher);
 }
 @Test void adminCannotApproveACancelledBooking(){
  Booking booking=sample(); booking.cancel();
  BookingRepository repo=mock(BookingRepository.class);
  BookingEventPublisher publisher=mock(BookingEventPublisher.class);
  when(repo.lockById(10L)).thenReturn(Optional.of(booking));
  assertThrows(IllegalStateException.class,()->service(repo,publisher).confirmByAdmin(10L));
  verifyNoInteractions(publisher);
 }
}
