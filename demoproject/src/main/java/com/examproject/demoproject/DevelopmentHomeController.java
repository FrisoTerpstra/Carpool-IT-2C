package com.examproject.demoproject;

import org.springframework.context.annotation.Profile;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

// A RestController writes the return value directly into the HTTP response.
@RestController
// Use this controller only during development, never when prod is active.
@Profile("dev & !prod")
public class DevelopmentHomeController
{
    // A GET request to / calls this method and receives plain text.
    @GetMapping(value = "/", produces = "text/plain")
    public String home()
    {
        return "Carpool backend is running. Local development access is open; "
                + "Microsoft sign-in is not configured yet.";
    }
}