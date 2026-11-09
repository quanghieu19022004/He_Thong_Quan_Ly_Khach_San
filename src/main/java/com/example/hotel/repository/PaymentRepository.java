package com.example.hotel.repository;
import com.example.hotel.domain.Payment;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;
public interface PaymentRepository extends JpaRepository<Payment,Long> {
 Optional<Payment> findByBookingId(Long bookingId);
}
