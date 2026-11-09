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

class CustomerBookingCancellationTests {
 private Booking booking(int daysBeforeCheckIn){
  LocalDate date=LocalDate.now().plusDays(daysBeforeCheckIn);
  return Booking.builder()
   .customer(new AppUser("demo","demo@example.com","hash",Role.CUSTOMER))
   .room(new Room("A101",RoomType.STANDARD,new BigDecimal("550000"),2))
   .dates(date,date.plusDays(2)).guests(2).pricingPlan(PricingPlan.STANDARD)
   .total(new BigDecimal("1100000")).build();
 }
 private BookingService service(BookingRepository repo,PaymentRepository payments,BookingEventPublisher publisher){
  return new BookingService(mock(RoomRepository.class),repo,mock(UserRepository.class),payments,mock(PricingService.class),publisher);
 }
 @Test void pendingBookingCanBeCancelledByCustomer(){
  Booking b=booking(5);
  BookingRepository repo=mock(BookingRepository.class);
  PaymentRepository payments=mock(PaymentRepository.class);
  BookingEventPublisher events=mock(BookingEventPublisher.class);
  when(repo.lockById(11L)).thenReturn(Optional.of(b));
  when(payments.findByBookingId(11L)).thenReturn(Optional.empty());
  assertTrue(b.isCancellable());
  service(repo,payments,events).cancel(11L,"demo",false);
  assertEquals(BookingStatus.CANCELLED,b.getStatus());
  assertFalse(b.isCancellable());
  verify(events).publish(any(BookingEvent.class));
 }
 @Test void customerCanCancelConfirmedBookingAndDueAtPropertyPayment(){
  Booking b=booking(5);b.confirm();
  Payment payment=new Payment(b,PaymentMethod.CASH,"CASH-11",b.getTotal(),PaymentStatus.DUE_AT_PROPERTY);
  BookingRepository repo=mock(BookingRepository.class);
  PaymentRepository payments=mock(PaymentRepository.class);
  when(repo.lockById(11L)).thenReturn(Optional.of(b));
  when(payments.findByBookingId(11L)).thenReturn(Optional.of(payment));
  service(repo,payments,mock(BookingEventPublisher.class)).cancel(11L,"demo",false);
  assertEquals(BookingStatus.CANCELLED,b.getStatus());
  assertEquals(PaymentStatus.CANCELLED,payment.getStatus());
 }
 @Test void demoPaymentCanBeCancelledWithSimulatedRefund(){
  Booking b=booking(5);b.confirm();
  Payment payment=new Payment(b,PaymentMethod.MOCK_WALLET,"DEMO-11",b.getTotal(),PaymentStatus.SIMULATED_PAID);
  BookingRepository repo=mock(BookingRepository.class);
  PaymentRepository payments=mock(PaymentRepository.class);
  when(repo.lockById(11L)).thenReturn(Optional.of(b));
  when(payments.findByBookingId(11L)).thenReturn(Optional.of(payment));
  service(repo,payments,mock(BookingEventPublisher.class)).cancel(11L,"demo",false);
  assertEquals(BookingStatus.CANCELLED,b.getStatus());
  assertEquals(PaymentStatus.SIMULATED_REFUNDED,payment.getStatus());
  assertNotNull(payment.getPaidAt()); // lịch sử thanh toán demo được giữ lại
 }
 @Test void cannotCancelSomeoneElsesBooking(){
  Booking b=booking(5);
  BookingRepository repo=mock(BookingRepository.class);
  PaymentRepository payments=mock(PaymentRepository.class);
  BookingEventPublisher events=mock(BookingEventPublisher.class);
  when(repo.lockById(11L)).thenReturn(Optional.of(b));
  assertThrows(SecurityException.class,()->service(repo,payments,events).cancel(11L,"not-owner",false));
  assertEquals(BookingStatus.PENDING,b.getStatus());
  verifyNoInteractions(payments,events);
 }
 @Test void cannotCancelOnCheckInDate(){
  Booking b=booking(0);
  BookingRepository repo=mock(BookingRepository.class);
  BookingEventPublisher events=mock(BookingEventPublisher.class);
  when(repo.lockById(11L)).thenReturn(Optional.of(b));
  assertFalse(b.isCancellable());
  assertThrows(IllegalArgumentException.class,()->service(repo,mock(PaymentRepository.class),events).cancel(11L,"demo",false));
  assertEquals(BookingStatus.PENDING,b.getStatus());
  verifyNoInteractions(events);
 }
 @Test void cannotCancelTwice(){
  Booking b=booking(5);b.cancel();
  BookingRepository repo=mock(BookingRepository.class);
  when(repo.lockById(11L)).thenReturn(Optional.of(b));
  assertThrows(IllegalArgumentException.class,()->service(repo,mock(PaymentRepository.class),mock(BookingEventPublisher.class)).cancel(11L,"demo",false));
 }
}
