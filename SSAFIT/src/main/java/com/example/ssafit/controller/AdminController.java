package com.example.ssafit.controller;

import com.example.ssafit.model.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api_admin")
@PreAuthorize( "hasRole('ADMIN')")
public class AdminController {

    @Autowired
    private UserService userService;
}
