package com.imse.onlineshop.controller.sql.user;

import com.imse.onlineshop.reports.Users;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserReportResponse {
    List<Users> rows;
}
