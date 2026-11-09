package com.example.hotel.pattern.strategy;
import com.example.hotel.domain.PricingPlan;import com.example.hotel.domain.Room;
import java.math.BigDecimal;import java.time.LocalDate;
public interface PricingStrategy {
 PricingPlan supports();
 BigDecimal calculate(Room room,LocalDate checkIn,LocalDate checkOut);
}
