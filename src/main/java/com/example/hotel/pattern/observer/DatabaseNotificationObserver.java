package com.example.hotel.pattern.observer;
import com.example.hotel.domain.Notification;import com.example.hotel.repository.NotificationRepository;
import org.springframework.stereotype.Component;
@Component
public class DatabaseNotificationObserver implements BookingEventListener {
 private final NotificationRepository repository;
 public DatabaseNotificationObserver(NotificationRepository repository){this.repository=repository;}
 public void onBookingChanged(BookingEvent event){repository.save(new Notification(event.booking().getCustomer(),
  "Đơn #"+event.booking().getId()+": "+event.action()));}
}
