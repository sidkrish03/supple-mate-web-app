package com.supplemateservice.controller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class SuppleMateController {

    @RequestMapping
    public String SuppleMateIntro() {
        return "Welcome to SuppleMate! Your friendly supplement tracker";
    }

    @RequestMapping("/pre-registration")
    public String SuppleMatePreRegistration() {
        return "Please enter the details below to create an account and register with us.";
    }

    @RequestMapping("/post-registration")
    public String SuppleMatePostRegistration() {
        return "You have successfully registered with us!";
    }

    @RequestMapping("/login")
    public String SuppleMateLogin() {
        return "Please enter your user name and password to login.";
    }

    @RequestMapping("/home")
    public String SuppleMateHome() {
        return "Welcome to SuppleMate Home Page.";
    }

    @RequestMapping("/thank-you")
    public String SuppleMatePostLogout() {
        return "Thank you for using SuppleMate! Hope to see you soon";
    }
}
