package com.example.hotel.pattern.adapter;
import com.example.hotel.domain.Booking;import org.springframework.stereotype.Component;
@Component("cashPaymentGateway")
public class CashPaymentGateway implements PaymentGateway {
 public String pay(Booking booking){return "DEMO-CASH-BOOK-"+booking.getId();}
}
