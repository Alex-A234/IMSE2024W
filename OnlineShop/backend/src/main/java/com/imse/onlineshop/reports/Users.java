package com.imse.onlineshop.reports;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Users {
    private Integer ranking;
    private String name;
    private String surname;
    private String productName;
    private Integer year;
    private Integer totalReturned;
}
