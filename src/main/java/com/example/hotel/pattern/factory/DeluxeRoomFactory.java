package com.example.hotel.pattern.factory;
import com.example.hotel.domain.*;import java.math.BigDecimal;
import org.springframework.stereotype.Component;
@Component("deluxeRoomFactory")
public class DeluxeRoomFactory implements RoomFactory {
 public Room create(String number,BigDecimal baseRate,int capacity){return new Room(number,RoomType.DELUXE,baseRate,capacity);}
}
