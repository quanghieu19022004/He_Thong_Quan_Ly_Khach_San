package com.example.hotel;
import com.example.hotel.domain.*;
import com.example.hotel.pattern.factory.*;
import com.example.hotel.pattern.strategy.*;
import com.example.hotel.pattern.adapter.*;
import org.junit.jupiter.api.Test;
import java.math.BigDecimal;import java.time.LocalDate;
import static org.junit.jupiter.api.Assertions.*;
class PatternUnitTests {
 @Test void factoryShouldCreateCorrectRoomType(){
  Room room=new DeluxeRoomFactory().create("B203",new BigDecimal("900000"),3);
  assertEquals(RoomType.DELUXE,room.getType());assertEquals(3,room.getCapacity());
 }
 @Test void builderShouldRejectMissingFields(){assertThrows(IllegalStateException.class,()->Booking.builder().guests(1).build());}
 @Test void strategyShouldApplyFridayAndSaturdaySurcharge(){
  Room room=new Room("A101",RoomType.STANDARD,new BigDecimal("1000000"),2);
  BigDecimal standard=new StandardPricingStrategy().calculate(room,LocalDate.of(2026,10,23),LocalDate.of(2026,10,25));
  BigDecimal weekend=new WeekendPricingStrategy().calculate(room,LocalDate.of(2026,10,23),LocalDate.of(2026,10,25));
  assertEquals(0,new BigDecimal("2000000").compareTo(standard));
  assertEquals(0,new BigDecimal("2400000").compareTo(weekend));
 }
 @Test void strategyRejectsInvalidDates(){Room room=new Room("A101",RoomType.STANDARD,BigDecimal.TEN,2);
  assertThrows(IllegalArgumentException.class,()->new StandardPricingStrategy().calculate(room,LocalDate.of(2026,10,25),LocalDate.of(2026,10,24)));}
 @Test void adapterMapsLegacyClientToGateway(){
  Booking booking=Booking.builder().customer(new AppUser("test","test@mail.com","hash",Role.CUSTOMER))
   .room(new Room("A101",RoomType.STANDARD,new BigDecimal("500000"),2))
   .dates(LocalDate.of(2027,1,1),LocalDate.of(2027,1,2)).guests(1).pricingPlan(PricingPlan.STANDARD).total(new BigDecimal("500000")).build();
  assertTrue(new WalletPaymentAdapter(new LegacyWalletClient()).pay(booking).startsWith("DEMO-WALLET-"));
 }
}
