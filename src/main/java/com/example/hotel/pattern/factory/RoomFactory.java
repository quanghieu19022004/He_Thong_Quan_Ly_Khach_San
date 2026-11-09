package com.example.hotel.pattern.factory;
import com.example.hotel.domain.Room;
import java.math.BigDecimal;
public interface RoomFactory { Room create(String roomNumber,BigDecimal baseRate,int capacity); }
