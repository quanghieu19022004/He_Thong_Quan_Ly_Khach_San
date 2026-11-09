package com.example.hotel.service;
import com.example.hotel.domain.*;
import com.example.hotel.pattern.adapter.PaymentGatewayRouter;
import com.example.hotel.pattern.observer.*;
import com.example.hotel.repository.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
@Service
public class PaymentService {
 private final BookingRepository bookings;private final PaymentRepository payments;
 private final PaymentGatewayRouter gateways;private final BookingEventPublisher publisher;
 public PaymentService(BookingRepository bookings,PaymentRepository payments,PaymentGatewayRouter gateways,BookingEventPublisher publisher){
  this.bookings=bookings;this.payments=payments;this.gateways=gateways;this.publisher=publisher;
 }
 @Transactional
 public Payment pay(Long bookingId,String username,PaymentMethod method){
  Booking booking=bookings.lockById(bookingId).orElseThrow(()->new IllegalArgumentException("Không tìm thấy đơn"));
  if(!booking.getCustomer().getUsername().equals(username))throw new SecurityException("Không có quyền thanh toán đơn này");
  if(booking.getStatus()==BookingStatus.CANCELLED)throw new IllegalArgumentException("Không thể thanh toán đơn đã hủy");
  if(payments.findByBookingId(bookingId).isPresent())throw new IllegalStateException("Đơn đã có phương thức thanh toán");
  String reference=gateways.get(method).pay(booking);
  // Đặt phòng khi nhận phòng KHÔNG đồng nghĩa đã thu tiền.
  PaymentStatus status=method==PaymentMethod.CASH?PaymentStatus.DUE_AT_PROPERTY:PaymentStatus.SIMULATED_PAID;
  Payment saved=payments.save(new Payment(booking,method,reference,booking.getTotal(),status));
  // Thanh toán KHÔNG có quyền tự xác nhận booking. Chỉ admin mới đổi PENDING -> CONFIRMED.
  publisher.publish(new BookingEvent(booking,status==PaymentStatus.DUE_AT_PROPERTY
    ?"đã chọn thanh toán tại khách sạn; trạng thái đặt phòng không thay đổi"
    :"đã ghi nhận thanh toán giả lập; chờ quản trị viên xác nhận đơn (không có giao dịch thật)"));
  return saved;
 }
 @Transactional(readOnly=true)
 public Payment findForBooking(Long bookingId){return payments.findByBookingId(bookingId).orElse(null);}
}
