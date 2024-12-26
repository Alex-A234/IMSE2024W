package com.imse.onlineshop.nosql.entities;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.mongodb.core.index.Indexed;

import java.util.Date;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
// Embedded in Customer.
public class PaymentInformationNoSQL {
    @Indexed(name = "payment_ids", unique = true)
    private Long paymentID;
    private String cardNumber;
    private String nameOnCard;
    private Date expirationDate;
}
