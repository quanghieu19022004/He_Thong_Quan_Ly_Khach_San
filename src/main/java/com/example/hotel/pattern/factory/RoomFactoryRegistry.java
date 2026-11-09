package com.example.hotel.pattern.factory;
import com.example.hotel.domain.RoomType;
import org.springframework.stereotype.Component;
import java.util.Map;
@Component
public class RoomFactoryRegistry {
 private final Map<RoomType,RoomFactory> factories;
 public RoomFactoryRegistry(StandardRoomFactory a,DeluxeRoomFactory b,SuiteRoomFactory c){
  factories=Map.of(RoomType.STANDARD,a,RoomType.DELUXE,b,RoomType.SUITE,c);}
 public RoomFactory get(RoomType type){ RoomFactory factory=factories.get(type);if(factory==null)throw new IllegalArgumentException("Loại phòng không hợp lệ");return factory;}
}
