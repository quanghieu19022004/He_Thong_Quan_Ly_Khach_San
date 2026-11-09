package com.example.hotel.pattern.strategy;
import com.example.hotel.domain.*;import java.time.*;import java.math.BigDecimal;
import org.springframework.stereotype.Component;
@Component
public class WeekendPricingStrategy implements PricingStrategy {
 public PricingPlan supports(){return PricingPlan.WEEKEND;}
 public BigDecimal calculate(Room room,LocalDate start,LocalDate end){
  long nights=ChronoUnitHelper.nights(start,end);BigDecimal total=BigDecimal.ZERO;
  for(int i=0;i<nights;i++){
   DayOfWeek day=start.plusDays(i).getDayOfWeek();
   total=total.add((day==DayOfWeek.FRIDAY||day==DayOfWeek.SATURDAY)
    ?room.getNightlyRate().multiply(new BigDecimal("1.20")):room.getNightlyRate());
  }
  return total;
 }
}
