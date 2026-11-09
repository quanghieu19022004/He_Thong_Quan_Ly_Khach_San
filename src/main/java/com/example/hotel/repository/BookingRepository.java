package com.example.hotel.repository;
import com.example.hotel.domain.*;
import org.springframework.data.jpa.repository.*;
import org.springframework.data.repository.query.Param;
import java.time.LocalDate;import java.util.List;
public interface BookingRepository extends JpaRepository<Booking,Long> {
 @Lock(jakarta.persistence.LockModeType.PESSIMISTIC_WRITE)
 @Query("select b from Booking b where b.id=:id")
 java.util.Optional<Booking> lockById(@Param("id") Long id);
 @Query("select (count(b)>0) from Booking b where b.room.id=:roomId and b.status<>:cancelled and b.checkIn<:end and b.checkOut>:start")
 boolean existsOverlap(@Param("roomId") Long roomId,@Param("start") LocalDate start,@Param("end") LocalDate end,@Param("cancelled") BookingStatus cancelled);
 @Query("select b from Booking b join fetch b.room join fetch b.customer where b.id=:id")
 java.util.Optional<Booking> findOwnedDetail(@Param("id") Long id);
 @Query("select b from Booking b join fetch b.room where b.customer.username=:username order by b.createdAt desc")
 List<Booking> findMine(@Param("username") String username);
 @Query("select b from Booking b join fetch b.room join fetch b.customer order by b.createdAt desc")
 List<Booking> findAllDetailed();
}
