package com.example.hotel.pattern.strategy;
import com.example.hotel.domain.*;import java.time.LocalDate;import java.math.BigDecimal;
import java.util.List;import java.util.Map;import java.util.stream.Collectors;
import org.springframework.stereotype.Service;
@Service
public class PricingService {
 private final Map<PricingPlan,PricingStrategy> strategies;
 public PricingService(List<PricingStrategy> strategies){this.strategies=strategies.stream().collect(Collectors.toUnmodifiableMap(PricingStrategy::supports,s->s));}
 public BigDecimal quote(PricingPlan plan,Room room,LocalDate start,LocalDate end){
  PricingStrategy strategy=strategies.get(plan);if(strategy==null)throw new IllegalArgumentException("Chính sách giá không hợp lệ");
  return strategy.calculate(room,start,end);
 }
}
