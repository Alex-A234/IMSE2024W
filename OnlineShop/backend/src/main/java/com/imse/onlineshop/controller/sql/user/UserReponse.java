package com.imse.onlineshop.controller.sql.user;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserReponse {
    private String ssn;
    private String name;
    private String surname;
    private Boolean isCustomer;
}
