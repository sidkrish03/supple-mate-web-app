package com.supplemateservice.controller;

import com.supplemateservice.model.Customers;
import com.supplemateservice.service.CustomerService;
import com.supplemateservice.service.ValidateService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import java.util.HashSet;
import java.util.Set;

public class RegistrationController {
    Logger logger = LoggerFactory.getLogger(this.getClass());

    @Autowired
    CustomerService saveCustomer;

    @Autowired
    ValidateService validateService;

    @Autowired
    private PasswordEncoder encoder;

    private Set<String> violations = new HashSet();

    @GetMapping("/signup")
    public String displaySignUp(Model model){
        if (!violations.isEmpty()){
            model.addAttribute("violations", violations);
        }
        return "signUp";
    }

    @PostMapping("/addCustomer")
    public String addCustomer(HttpServletRequest request, Model model){
        logger.debug("User creation attempt started");
        Customers customer = addService.populateNewUserFromForm(request);
        violations.clear();
        validateService.validateNewAccountSettings(violations, customer, request.getParameter("confirmPassword"));
        if (!violations.isEmpty()){
            logger.error("User creation rule violation");
            return "redirect:/signup";
        }

        // encrypt password
        String unencryptedPassword = customer.getPassword();
        customer.setPassword(encoder.encode(unencryptedPassword));

        addService.createNewAccount(customer);
        logger.info("-- User created --\n ID: {}, NAME: {} {}, USERNAME: {}, EMAIL: {}, CREATION TIME: {}, TIMEZONE: {}",
                customer.getId(), customer.getFirstName(), customer.getLastName(), customer.getUsername(), customer.getEmail(), customer.getCreationTime(), customer.getTimeZone());

        try {
            request.login(customer.getUsername(), unencryptedPassword);
        } catch (ServletException e) {
            logger.error("Login error - ", e.getMessage());
            return "redirect:/signup";
        }

        return "redirect:/content";
    }
}
