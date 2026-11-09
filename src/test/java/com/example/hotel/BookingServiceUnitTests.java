package com.example.hotel;
import com.example.hotel.domain.*;import com.example.hotel.pattern.strategy.PricingService;
import com.example.hotel.pattern.observer.BookingEventPublisher;
import com.example.hotel.repository.*;import com.example.hotel.service.BookingService;
import org.junit.jupiter.api.Test;import java.math.BigDecimal;import java.time.LocalDate;
import java.util.Optional;
import static org.mockito.Mockito.*;import static org.junit.jupiter.api.Assertions.*;
class BookingServiceUnitTests {
 @Test void overlapsAreRejected(){
  RoomRepository rooms=mock(RoomRepository.class);BookingRepository bookings=mock(BookingRepository.class);
  UserRepository users=mock(UserRepository.class);PricingService price=mock(PricingService.class);
  BookingEventPublisher events=mock(BookingEventPublisher.class);
  BookingService service=new BookingService(rooms,bookings,users,mock(PaymentRepository.class),price,events);
  Room room=new Room("A101",RoomType.STANDARD,new BigDecimal("500000"),2);
  LocalDate from=LocalDate.now().plusDays(1),to=from.plusDays(2);
  when(rooms.lockById(1L)).thenReturn(Optional.of(room));
  when(bookings.existsOverlap(1L,from,to,BookingStatus.CANCELLED)).thenReturn(true);
  assertThrows(IllegalArgumentException.class,()->service.reserve("demo",1L,from,to,2,PricingPlan.STANDARD));
  verify(bookings,never()).save(any());
 }
 @Test void guestsExceedingCapacityAreRejected(){
  RoomRepository rooms=mock(RoomRepository.class);BookingRepository bookings=mock(BookingRepository.class);
  BookingService service=new BookingService(rooms,bookings,mock(UserRepository.class),mock(PaymentRepository.class),mock(PricingService.class),mock(BookingEventPublisher.class));
  when(rooms.lockById(1L)).thenReturn(Optional.of(new Room("A101",RoomType.STANDARD,BigDecimal.TEN,2)));
  LocalDate from=LocalDate.now().plusDays(1);
  assertThrows(IllegalArgumentException.class,()->service.reserve("demo",1L,from,from.plusDays(1),3,PricingPlan.STANDARD));
  verify(bookings,never()).save(any());
 }
}
