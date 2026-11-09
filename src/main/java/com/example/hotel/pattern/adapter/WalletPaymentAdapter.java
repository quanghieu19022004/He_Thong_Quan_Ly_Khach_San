package com.example.hotel.pattern.adapter;
import com.example.hotel.domain.Booking;import org.springframework.stereotype.Component;
@Component("walletPaymentAdapter")
public class WalletPaymentAdapter implements PaymentGateway {
 private final LegacyWalletClient legacy;
 public WalletPaymentAdapter(LegacyWalletClient legacy){this.legacy=legacy;}
 public String pay(Booking booking){
  // VND -> integer minor units in this demo gateway (a simulated contract).
  long amount=booking.getTotal().longValueExact();
  return legacy.chargeInMinorUnits("BOOK-"+booking.getId(),amount);
 }
}
