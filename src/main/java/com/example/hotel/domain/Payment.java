package com.example.hotel.domain;
import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;
@Entity @Table(name="payments")
public class Payment {
 @Id @GeneratedValue(strategy=GenerationType.IDENTITY) private Long id;
 @OneToOne(optional=false,fetch=FetchType.LAZY) @JoinColumn(unique=true) private Booking booking;
 @Enumerated(EnumType.STRING) @Column(nullable=false) private PaymentMethod method;
 @Enumerated(EnumType.STRING) @Column(nullable=false) private PaymentStatus status;
 @Column(nullable=false,length=100) private String reference;
 @Column(nullable=false,precision=14,scale=2) private BigDecimal amount;
 @Column private LocalDateTime paidAt;
 protected Payment(){}
 public Payment(Booking booking,PaymentMethod method,String reference,BigDecimal amount,PaymentStatus status){
  this.booking=booking;this.method=method;this.reference=reference;this.amount=amount;this.status=status;
  this.paidAt=status==PaymentStatus.SIMULATED_PAID?LocalDateTime.now():null;
 }
 public Long getId(){return id;} public Booking getBooking(){return booking;}
 public String getReference(){return reference;} public BigDecimal getAmount(){return amount;}
 public PaymentMethod getMethod(){return method;} public PaymentStatus getStatus(){return status;}
 public LocalDateTime getPaidAt(){return paidAt;}
 public void cancelPendingAtProperty(){
  if(status!=PaymentStatus.DUE_AT_PROPERTY)throw new IllegalStateException("Chỉ hủy khoản phải trả tại khách sạn");
  status=PaymentStatus.CANCELLED;
 }
 /** Chỉ ghi nhận hoàn tiền giả lập, tuyệt đối không kết nối giao dịch ngân hàng. */
 public void refundSimulated(){
  if(status!=PaymentStatus.SIMULATED_PAID)throw new IllegalStateException("Khoản thanh toán không hợp lệ để hoàn tiền demo");
  status=PaymentStatus.SIMULATED_REFUNDED;
 }
}
