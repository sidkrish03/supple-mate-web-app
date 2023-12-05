package com.supplemateservice.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
@CrossOrigin
public class AdminController {
    @GetMapping("/admin")
    public String displayAdminPage() {
        return "admin";
    }
}
