package com.example.hotel.service;
import com.example.hotel.domain.*;
import com.example.hotel.pattern.strategy.PricingService;
import com.example.hotel.pattern.observer.*;
import com.example.hotel.repository.*;
import java.time.LocalDate;import java.util.List;
import org.springframework.stereotype.Service;import org.springframework.transaction.annotation.Transactional;
@Service
public class BookingService {
 private final RoomRepository rooms; private final BookingRepository bookings;private final UserRepository users;private final PaymentRepository payments;
 private final PricingService pricing;private final BookingEventPublisher publisher;
 public BookingService(RoomRepository rooms,BookingRepository bookings,UserRepository users,PaymentRepository payments,PricingService pricing,BookingEventPublisher publisher){
  this.rooms=rooms;this.bookings=bookings;this.users=users;this.payments=payments;this.pricing=pricing;this.publisher=publisher;
 }
 @Transactional
 public Booking reserve(String username,Long roomId,LocalDate start,LocalDate end,int guests,PricingPlan plan){
  if(start==null||end==null||start.isBefore(LocalDate.now())||!end.isAfter(start))throw new IllegalArgumentException("Ngày nhận/trả không hợp lệ");
  // Room lock serializes concurrent reservations of the same room.
  Room room=rooms.lockById(roomId).orElseThrow(()->new IllegalArgumentException("Không tìm thấy phòng"));
  if(!room.isActive()||guests<1||guests>room.getCapacity())throw new IllegalArgumentException("Phòng hoặc số khách không hợp lệ");
  if(bookings.existsOverlap(roomId,start,end,BookingStatus.CANCELLED))throw new IllegalArgumentException("Phòng đã có đơn trùng ngày");
  AppUser user=users.findByUsername(username).orElseThrow(()->new IllegalArgumentException("Tài khoản không tồn tại"));
  Booking booking=Booking.builder().customer(user).room(room).dates(start,end).guests(guests)
   .pricingPlan(plan).total(pricing.quote(plan,room,start,end)).build();
  Booking saved=bookings.save(booking);bookings.flush();publisher.publish(new BookingEvent(saved,"đã tạo yêu cầu đặt phòng, đang chờ quản trị viên xác nhận"));return saved;
 }
 // Chỉ được gọi từ luồng /admin/** đã bảo vệ bằng Spring Security.
 @Transactional
 public void confirmByAdmin(Long bookingId){
  Booking booking=bookings.lockById(bookingId)
   .orElseThrow(()->new IllegalArgumentException("Không tìm thấy đơn đặt phòng"));
  if(booking.getStatus()!=BookingStatus.PENDING)
   throw new IllegalStateException("Chỉ có thể xác nhận đơn đang chờ xác nhận");
  booking.confirm();
  publisher.publish(new BookingEvent(booking,"đã được quản trị viên xác nhận"));
 }
 @Transactional(readOnly=true) public List<Booking> mine(String username){return bookings.findMine(username);}
 @Transactional(readOnly=true) public List<Booking> all(){return bookings.findAllDetailed();}
 @Transactional(readOnly=true)
 public Booking ownedBy(String username,Long bookingId){
  Booking b=bookings.findOwnedDetail(bookingId).orElseThrow(()->new IllegalArgumentException("Không tìm thấy đơn đặt phòng"));
  if(!b.getCustomer().getUsername().equals(username))throw new SecurityException("Bạn không có quyền xem đơn này");
  return b;
 }
 @Transactional
 public void cancel(Long bookingId,String username,boolean admin){
  Booking b=bookings.lockById(bookingId).orElseThrow(()->new IllegalArgumentException("Không tìm thấy đơn"));
  if(!admin&&!b.getCustomer().getUsername().equals(username))throw new SecurityException("Không có quyền hủy đơn này");
  if(b.getStatus()==BookingStatus.CANCELLED)throw new IllegalArgumentException("Đơn đã hủy");
  if(!b.isCancellable())throw new IllegalArgumentException("Không thể hủy đơn vào hoặc sau ngày nhận phòng");
  var payment=payments.findByBookingId(bookingId);
  if(payment.isPresent()){
   Payment p=payment.get();
   if(p.getStatus()==PaymentStatus.SIMULATED_PAID){p.refundSimulated();}
   else if(p.getStatus()==PaymentStatus.DUE_AT_PROPERTY){p.cancelPendingAtProperty();}
   else throw new IllegalStateException("Trạng thái thanh toán không phù hợp để hủy đơn");
  }
  b.cancel();
  publisher.publish(new BookingEvent(b, payment.isPresent() && payment.get().getStatus()==PaymentStatus.SIMULATED_REFUNDED
   ? "đã hủy đơn và ghi nhận hoàn tiền DEMO (không có giao dịch thật)" : "đã hủy đơn đặt phòng"));
 }
}
