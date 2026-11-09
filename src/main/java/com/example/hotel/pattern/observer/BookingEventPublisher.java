package com.example.hotel.pattern.observer;
import org.springframework.stereotype.Component;import java.util.List;
@Component
public class BookingEventPublisher {
 private final List<BookingEventListener> listeners;
 public BookingEventPublisher(List<BookingEventListener> listeners){this.listeners=listeners;}
 public void publish(BookingEvent event){for(BookingEventListener listener:listeners)listener.onBookingChanged(event);}
}
