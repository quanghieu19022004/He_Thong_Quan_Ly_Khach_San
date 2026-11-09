package com.example.hotel.pattern.observer;
import org.slf4j.Logger;import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
@Component
public class LogBookingObserver implements BookingEventListener {
 private static final Logger LOG=LoggerFactory.getLogger(LogBookingObserver.class);
 public void onBookingChanged(BookingEvent event){LOG.info("Booking #{} changed: {}",event.booking().getId(),event.action());}
}
