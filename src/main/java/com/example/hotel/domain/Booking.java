package com.example.hotel.domain;
import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
@Entity @Table(name="bookings")
public class Booking {
 @Id @GeneratedValue(strategy=GenerationType.IDENTITY) private Long id;
 @ManyToOne(optional=false,fetch=FetchType.LAZY) private AppUser customer;
 @ManyToOne(optional=false,fetch=FetchType.LAZY) private Room room;
 @Column(nullable=false) private LocalDate checkIn;
 @Column(nullable=false) private LocalDate checkOut;
 @Column(nullable=false) private int guests;
 @Enumerated(EnumType.STRING) @Column(nullable=false) private PricingPlan pricingPlan;
 @Column(nullable=false,precision=14,scale=2) private BigDecimal total;
 @Enumerated(EnumType.STRING) @Column(nullable=false) private BookingStatus status=BookingStatus.PENDING;
 @Column(nullable=false) private LocalDateTime createdAt=LocalDateTime.now();
 protected Booking(){}
 // BUILDER: gom tham số tùy biến, xác nhận đối tượng hoàn chỉnh trước khi lưu.
 private Booking(Builder b){this.customer=b.customer;this.room=b.room;this.checkIn=b.checkIn;this.checkOut=b.checkOut;
 this.guests=b.guests;this.pricingPlan=b.pricingPlan;this.total=b.total;}
 public static Builder builder(){return new Builder();}
 public static class Builder {
  private AppUser customer; private Room room; private LocalDate checkIn,checkOut;
  private int guests; private PricingPlan pricingPlan; private BigDecimal total;
  public Builder customer(AppUser v){customer=v;return this;} public Builder room(Room v){room=v;return this;}
  public Builder dates(LocalDate start,LocalDate end){checkIn=start;checkOut=end;return this;}
  public Builder guests(int v){guests=v;return this;} public Builder pricingPlan(PricingPlan v){pricingPlan=v;return this;}
  public Builder total(BigDecimal v){total=v;return this;}
  public Booking build(){if(customer==null||room==null||checkIn==null||checkOut==null||pricingPlan==null||total==null||guests<1||!checkOut.isAfter(checkIn)) throw new IllegalStateException("Booking Builder missing/invalid field");return new Booking(this);}
 }
 public Long getId(){return id;} public AppUser getCustomer(){return customer;} public Room getRoom(){return room;}
 public LocalDate getCheckIn(){return checkIn;} public LocalDate getCheckOut(){return checkOut;} public int getGuests(){return guests;}
 public PricingPlan getPricingPlan(){return pricingPlan;} public BigDecimal getTotal(){return total;}
 public BookingStatus getStatus(){return status;} public LocalDateTime getCreatedAt(){return createdAt;}
 /** Trả về true nếu đơn còn hiệu lực và chưa đến ngày nhận phòng. */
 @Transient public boolean isCancellable(){return status!=BookingStatus.CANCELLED && checkIn.isAfter(LocalDate.now());}
 public void confirm(){if(status!=BookingStatus.PENDING) throw new IllegalStateException("Chỉ xác nhận đơn đang chờ"); status=BookingStatus.CONFIRMED;}
 public void cancel(){if(status==BookingStatus.CANCELLED) throw new IllegalStateException("Đơn đã hủy");status=BookingStatus.CANCELLED;}
}
