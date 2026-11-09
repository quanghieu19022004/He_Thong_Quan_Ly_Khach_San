package com.example.hotel.pattern.strategy;
import com.example.hotel.domain.*;import java.time.*;import java.math.BigDecimal;
import org.springframework.stereotype.Component;
@Component
public class StandardPricingStrategy implements PricingStrategy {
 public PricingPlan supports(){return PricingPlan.STANDARD;}
 public BigDecimal calculate(Room room,LocalDate start,LocalDate end){long nights=ChronoUnitHelper.nights(start,end);return room.getNightlyRate().multiply(BigDecimal.valueOf(nights));}
}
