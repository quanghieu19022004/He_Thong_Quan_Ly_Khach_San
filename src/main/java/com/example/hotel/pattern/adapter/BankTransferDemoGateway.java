package com.example.hotel.pattern.adapter;
import com.example.hotel.domain.Booking;
import org.springframework.stereotype.Component;
/** DEMO: chỉ sinh mã giao dịch giả lập; KHÔNG gửi yêu cầu tới ngân hàng thật. */
@Component
public class BankTransferDemoGateway implements PaymentGateway {
 public String pay(Booking booking){return "DEMO-BANK-BOOK-"+booking.getId();}
}
