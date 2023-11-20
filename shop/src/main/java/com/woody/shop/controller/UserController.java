package com.woody.shop.controller;


import com.woody.mydata.User;
import com.woody.shop.service.UserService;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
@AllArgsConstructor
public class UserController {

    private UserService userService;


    @GetMapping("/hello/user")
    public String hello() {
        return "hello user";
    }

    @GetMapping("/user/{id}")
    public User getUserById(Long id) {
        return userService.getUser(id);
    }

    @PostMapping("/user")
    public User saveUser(@RequestBody User user) {
        return userService.saveUser(user);
    }
}
