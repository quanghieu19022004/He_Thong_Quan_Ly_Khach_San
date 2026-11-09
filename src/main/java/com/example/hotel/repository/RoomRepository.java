package com.example.hotel.repository;
import com.example.hotel.domain.Room;
import org.springframework.data.jpa.repository.*;
import org.springframework.data.repository.query.Param;
import jakarta.persistence.LockModeType;
import java.util.List;import java.util.Optional;
public interface RoomRepository extends JpaRepository<Room,Long> {
 List<Room> findByActiveTrueOrderByNumberAsc();
 boolean existsByNumber(String number);
 // Serialize bookings of same room, preventing concurrent overlapping reservations.
 @Lock(LockModeType.PESSIMISTIC_WRITE)
 @Query("select r from Room r where r.id=:id") Optional<Room> lockById(@Param("id") Long id);
}
