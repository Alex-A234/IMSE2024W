package com.imse.onlineshop.nosql.entities.reports;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserReportNoSQL {
    private String name;
    private String surname;
    private Integer year;
    private List<UserReportProductsNoSQL> products;
}
