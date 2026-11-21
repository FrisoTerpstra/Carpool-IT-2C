package com.examproject.demoproject;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

// Spring finds controllers and configuration in this package and its subpackages.
@SpringBootApplication
public class DemoprojectApplication
{
    public static void main(String[] args)
    {
        // Start Spring, connect the application components and start the web server.
        SpringApplication.run(DemoprojectApplication.class, args);
    }
}