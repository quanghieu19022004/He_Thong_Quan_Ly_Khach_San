package com.example.hotel.pattern.strategy;
import java.time.LocalDate;import java.time.temporal.ChronoUnit;
final class ChronoUnitHelper {
 private ChronoUnitHelper(){}
 static long nights(LocalDate start,LocalDate end){if(start==null||end==null||!end.isAfter(start))throw new IllegalArgumentException("Ngày trả phải sau ngày nhận");return ChronoUnit.DAYS.between(start,end);}
}
