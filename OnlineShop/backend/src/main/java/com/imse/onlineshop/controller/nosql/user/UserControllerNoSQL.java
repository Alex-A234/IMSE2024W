package com.imse.onlineshop.controller.nosql.user;

import java.util.List;
import java.util.stream.Collectors;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import com.imse.onlineshop.controller.sql.user.UserReponse;
import com.imse.onlineshop.controller.sql.user.UserReportResponse;
import com.imse.onlineshop.nosql.entities.CustomerNoSQL;
import com.imse.onlineshop.nosql.services.UserServiceNoSQL;

@RestController
@RequestMapping("/nosql/user")
public class UserControllerNoSQL {
  private final UserServiceNoSQL userServiceNoSQL;

  public UserControllerNoSQL(UserServiceNoSQL userServiceNoSQL) {
    this.userServiceNoSQL = userServiceNoSQL;
  }

  @GetMapping(path = "")
  public List<UserReponse> list() {
    return userServiceNoSQL.findAll().stream().map(user -> new UserReponse(user.getSsn(),
        user.getName(), user.getSurname(), user instanceof CustomerNoSQL))
        .collect(Collectors.toList());
  }

  @GetMapping(path = "/report")
  public UserReportResponse report() {
    return new UserReportResponse(userServiceNoSQL.report());
  }

}
