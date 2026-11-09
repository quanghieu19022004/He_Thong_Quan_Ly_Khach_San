package com.example.hotel;
import com.example.hotel.domain.*;
import com.example.hotel.pattern.adapter.*;
import com.example.hotel.pattern.observer.BookingEventPublisher;
import com.example.hotel.repository.*;
import com.example.hotel.service.PaymentService;
import org.junit.jupiter.api.Test;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Optional;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;
class PaymentServiceUnitTests {
 private Booking sample(){return Booking.builder()
  .customer(new AppUser("demo","demo@example.com","hash",Role.CUSTOMER))
  .room(new Room("A101",RoomType.STANDARD,new BigDecimal("550000"),2))
  .dates(LocalDate.now().plusDays(1),LocalDate.now().plusDays(3))
  .guests(2).pricingPlan(PricingPlan.STANDARD).total(new BigDecimal("1100000")).build();
 }
 @Test void payAtPropertyMustNotBeMarkedPaid(){
  Booking booking=sample();
  BookingRepository bookings=mock(BookingRepository.class);
  PaymentRepository payments=mock(PaymentRepository.class);
  PaymentGatewayRouter router=mock(PaymentGatewayRouter.class);
  PaymentGateway gateway=mock(PaymentGateway.class);
  when(bookings.lockById(8L)).thenReturn(Optional.of(booking));
  when(router.get(PaymentMethod.CASH)).thenReturn(gateway);
  when(gateway.pay(booking)).thenReturn("DEMO-CASH-BOOK-8");
  when(payments.save(any(Payment.class))).thenAnswer(i->i.getArgument(0));
  Payment result=new PaymentService(bookings,payments,router,mock(BookingEventPublisher.class)).pay(8L,"demo",PaymentMethod.CASH);
  assertEquals(PaymentStatus.DUE_AT_PROPERTY,result.getStatus());
  assertNull(result.getPaidAt());
  // Khách đã chọn thanh toán nhưng Booking KHÔNG tự được xác nhận.
  assertEquals(BookingStatus.PENDING,booking.getStatus());
 }
 @Test void demoWalletIsExplicitlyMarkedSimulatedPaid(){
  Booking booking=sample();
  BookingRepository bookings=mock(BookingRepository.class);
  PaymentRepository payments=mock(PaymentRepository.class);
  PaymentGatewayRouter router=mock(PaymentGatewayRouter.class);
  PaymentGateway gateway=mock(PaymentGateway.class);
  when(bookings.lockById(8L)).thenReturn(Optional.of(booking));
  when(router.get(PaymentMethod.MOCK_WALLET)).thenReturn(gateway);
  when(gateway.pay(booking)).thenReturn("DEMO-WALLET-BOOK-8");
  when(payments.save(any(Payment.class))).thenAnswer(i->i.getArgument(0));
  Payment result=new PaymentService(bookings,payments,router,mock(BookingEventPublisher.class)).pay(8L,"demo",PaymentMethod.MOCK_WALLET);
  assertEquals(PaymentStatus.SIMULATED_PAID,result.getStatus());
  assertNotNull(result.getPaidAt());
  assertEquals(BookingStatus.PENDING,booking.getStatus());
 }
 @Test void alreadyConfirmedBookingCanStillChoosePaymentMethod(){
  Booking booking=sample();booking.confirm();
  BookingRepository bookings=mock(BookingRepository.class);
  PaymentRepository payments=mock(PaymentRepository.class);
  PaymentGatewayRouter router=mock(PaymentGatewayRouter.class);
  PaymentGateway gateway=mock(PaymentGateway.class);
  when(bookings.lockById(8L)).thenReturn(Optional.of(booking));
  when(router.get(PaymentMethod.CASH)).thenReturn(gateway);
  when(gateway.pay(booking)).thenReturn("DEMO-CASH-BOOK-8");
  when(payments.save(any(Payment.class))).thenAnswer(i->i.getArgument(0));
  Payment payment=new PaymentService(bookings,payments,router,mock(BookingEventPublisher.class)).pay(8L,"demo",PaymentMethod.CASH);
  assertEquals(PaymentStatus.DUE_AT_PROPERTY,payment.getStatus());
  assertEquals(BookingStatus.CONFIRMED,booking.getStatus());
 }
 @Test void userCannotPaySomeoneElsesBooking(){
  BookingRepository bookings=mock(BookingRepository.class);
  when(bookings.lockById(9L)).thenReturn(Optional.of(sample()));
  PaymentService service=new PaymentService(bookings,mock(PaymentRepository.class),
   mock(PaymentGatewayRouter.class),mock(BookingEventPublisher.class));
  assertThrows(SecurityException.class,()->service.pay(9L,"intruder",PaymentMethod.MOCK_BANK));
 }
}
