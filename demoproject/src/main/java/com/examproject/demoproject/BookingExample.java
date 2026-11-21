package com.examproject.demoproject;

import java.util.HashSet;
import java.util.Set;

// Standalone exercise: bookings exist only in memory and disappear when it ends.
// This is not a Spring service and does not handle simultaneous database bookings.
public class BookingExample
{
    private final String driverId;
    private final int passengerCapacity;
    private final Set<String> passengers;

    public BookingExample()
    {
        this.driverId = "fictional-alex";
        this.passengerCapacity = 2;
        this.passengers = new HashSet<>();
    }

    private int joinTrip(String userId)
    {
        if (this.driverId.equals(userId))
        {
            throw new IllegalStateException("Driver cannot join own trip");
        }

        // Check before adding so the passenger cannot reserve a second seat.
        if (this.passengers.contains(userId))
        {
            throw new IllegalStateException("Already booked");
        }

        // The driver is not counted in the number of passenger seats.
        if (this.passengers.size() >= this.passengerCapacity)
        {
            throw new IllegalStateException("Trip full");
        }

        this.passengers.add(userId);
        return this.passengerCapacity - this.passengers.size();
    }

    private void attempt(String userId, String name, String failureLabel)
    {
        // Catch a rejected booking so the other examples can still run.
        try
        {
            int seatsLeft = this.joinTrip(userId);
            System.out.println(name + " joined. Seats left: " + seatsLeft);
        }
        catch (IllegalStateException error)
        {
            System.out.println(failureLabel + ": " + error.getMessage());
        }
    }

    public static void main(String[] args)
    {
        BookingExample example = new BookingExample();
        example.attempt("fictional-sam", "Sam", "Sam blocked");
        example.attempt("fictional-sam", "Sam", "Duplicate blocked");
        example.attempt("fictional-lee", "Lee", "Lee blocked");
        example.attempt("fictional-morgan", "Morgan", "Extra passenger blocked");
        example.attempt("fictional-alex", "Alex", "Driver blocked");
    }
}