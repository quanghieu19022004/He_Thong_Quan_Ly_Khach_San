package com.example.hotel.pattern.adapter;
import org.springframework.stereotype.Component;
@Component
public class LegacyWalletClient {
 // Only a SIMULATOR: not connected to a real payment provider.
 public String chargeInMinorUnits(String memo,long cents){
  if(cents<=0)throw new IllegalArgumentException("Số tiền phải lớn hơn 0");
  return "DEMO-WALLET-"+memo;
 }
}
