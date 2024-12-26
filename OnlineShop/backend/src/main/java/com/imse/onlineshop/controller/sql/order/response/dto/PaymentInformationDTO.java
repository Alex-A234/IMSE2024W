package com.imse.onlineshop.controller.sql.order.response.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.sql.Date;

@Data
@AllArgsConstructor
public class PaymentInformationDTO {
    private Long paymentID;
    private String cardNumber;
    private String nameOnCard;
    private Date expirationDate;
}
