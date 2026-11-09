package com.example.hotel.pattern.adapter;
import com.example.hotel.domain.PaymentMethod;
import org.springframework.stereotype.Component;
@Component
public class PaymentGatewayRouter {
 private final PaymentGateway cash,wallet,bank;
 public PaymentGatewayRouter(CashPaymentGateway cash,WalletPaymentAdapter wallet,BankTransferDemoGateway bank){
  this.cash=cash;this.wallet=wallet;this.bank=bank;
 }
 public PaymentGateway get(PaymentMethod method){
  if(method==null)throw new IllegalArgumentException("Vui lòng chọn phương thức thanh toán");
  return switch(method){case CASH->cash;case MOCK_WALLET->wallet;case MOCK_BANK->bank;};
 }
}
