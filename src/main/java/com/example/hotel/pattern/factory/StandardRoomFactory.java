package com.example.hotel.pattern.factory;
import com.example.hotel.domain.*;import java.math.BigDecimal;
import org.springframework.stereotype.Component;
@Component("standardRoomFactory")
public class StandardRoomFactory implements RoomFactory {
 public Room create(String number,BigDecimal baseRate,int capacity){return new Room(number,RoomType.STANDARD,baseRate,capacity);}
}
