package com.sms.netmapper.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class HelloController {

    @GetMapping("/hello")
    public String hello() {
        return "Hello from NetMapper!";
    }
    
    @GetMapping("/")
    public String home() {
        return "NetMapper API is running! Frontend: http://localhost:5000/api/index.html";
    }
}