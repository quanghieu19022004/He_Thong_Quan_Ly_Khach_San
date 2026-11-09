package com.example.hotel.pattern.observer;
import com.example.hotel.domain.Booking;
public record BookingEvent(Booking booking,String action) {}
