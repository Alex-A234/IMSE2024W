package com.imse.onlineshop.nosql.entities.reports;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserReportProductsNoSQL {
    private String productName;
    private Integer totalReturned;
}
