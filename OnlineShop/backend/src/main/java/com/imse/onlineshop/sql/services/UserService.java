package com.imse.onlineshop.sql.services;

import com.imse.onlineshop.sql.entities.User;
import com.imse.onlineshop.reports.Users;
import com.imse.onlineshop.sql.repositories.UserRepository;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class UserService {
    private final UserRepository userRepository;

    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public List<User> findAll() {
        return userRepository.findAll();
    }

    public List<Users> report() {
        return userRepository.top5MostReturnedProducts()
                .stream()
                .map(obj -> new Users(
                        ((BigInteger) obj[0]).intValue(),
                        (String) obj[1],
                        (String) obj[2],
                        (String) obj[3],
                        (Integer) obj[4],
                        ((BigDecimal) obj[5]).intValue()
                )).collect(Collectors.toList());
    }
}
