package com.example.hotel.service;
import com.example.hotel.domain.*;import com.example.hotel.repository.RoomRepository;
import com.example.hotel.pattern.factory.RoomFactoryRegistry;
import org.springframework.stereotype.Service;
import java.util.List;import java.math.BigDecimal;import java.time.LocalDate;
@Service
public class RoomService {
 private final RoomRepository rooms;private final RoomFactoryRegistry factories;private final com.example.hotel.repository.BookingRepository bookings;
 public RoomService(RoomRepository rooms,RoomFactoryRegistry factories,com.example.hotel.repository.BookingRepository bookings){this.rooms=rooms;this.factories=factories;this.bookings=bookings;}
 public List<Room> availableCatalog(){return rooms.findByActiveTrueOrderByNumberAsc();}
 public List<Room> allRooms(){return rooms.findAll(org.springframework.data.domain.Sort.by("number"));}
 public Room findPublic(Long id){return rooms.findById(id).filter(Room::isActive).orElseThrow(()->new IllegalArgumentException("Không tìm thấy phòng đang mở"));}
 public List<Room> search(LocalDate start,LocalDate end,Integer guests){
  if(guests!=null&&guests<1)throw new IllegalArgumentException("Số khách phải lớn hơn 0");
  if(start==null&&end==null) return availableCatalog().stream().filter(r->guests==null||guests<=r.getCapacity()).toList();
  if(start==null||end==null||start.isBefore(LocalDate.now())||!end.isAfter(start))throw new IllegalArgumentException("Ngày nhận/trả không hợp lệ");
  return availableCatalog().stream().filter(r->guests==null||guests<=r.getCapacity())
   .filter(r->!bookings.existsOverlap(r.getId(),start,end,BookingStatus.CANCELLED)).toList();
 }
 public void setActive(Long id,boolean active){Room room=rooms.findById(id).orElseThrow(()->new IllegalArgumentException("Không tìm thấy phòng"));room.setActive(active);rooms.save(room);}
 
 public Room add(String number,RoomType type,BigDecimal baseRate,int capacity){
  if(number==null||!number.matches("[A-Za-z0-9-]{2,12}")||rooms.existsByNumber(number))throw new IllegalArgumentException("Mã phòng không hợp lệ hoặc đã tồn tại");
  if(baseRate==null||baseRate.signum()<=0||capacity<1||capacity>20)throw new IllegalArgumentException("Giá phòng hoặc sức chứa không hợp lệ");
  return rooms.save(factories.get(type).create(number,baseRate,capacity));
 }
}
