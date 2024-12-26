package com.imse.onlineshop.controller.sql.user;

import com.imse.onlineshop.sql.entities.Customer;
import com.imse.onlineshop.sql.services.UserService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/user")
public class UserController {
    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping(path = "")
    public List<UserReponse> list() {
        return userService
                .findAll()
                .stream()
                .map(user -> new UserReponse(
                        user.getSSN(),
                        user.getName(),
                        user.getSurname(),
                        user instanceof Customer
                )).collect(Collectors.toList());
    }

    @GetMapping(path = "/report")
    public UserReportResponse report() {
        return new UserReportResponse(userService.report());
    }
}
