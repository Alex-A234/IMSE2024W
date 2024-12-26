package com.imse.onlineshop.sql.entities;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import java.sql.Date;
import java.util.Set;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "payment_informations")
public class PaymentInformation {
    @Id
    @Column(name = "payment_id", updatable = false, nullable = false, unique = true)
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long paymentID;

    @Column(name = "card_number", nullable = false)
    private String cardNumber;

    @Column(name = "name_on_card", nullable = false)
    private String nameOnCard;

    @Column(name = "expiration_date", nullable = true)
    private Date expirationDate;

    @JsonManagedReference
    @ManyToOne
    @JoinColumn(name = "customer_ssn", nullable = false)
    private Customer customer;

    @JsonBackReference
    @OneToMany(mappedBy = "paymentInformation")
    private Set<Order> order;
}
