package com.imse.onlineshop.sql.entities;

import com.fasterxml.jackson.annotation.JsonBackReference;
import lombok.*;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import java.util.Set;

@Builder
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "customers")
public class Customer extends User {
    @Column(name = "age", nullable = false)
    private Integer age;

    @Column(name = "phone_number")
    private String phoneNumber;

    @JsonBackReference
    @OneToMany(mappedBy = "customer")
    private Set<PaymentInformation> paymentInformation;

    @JsonBackReference
    @OneToMany(mappedBy = "customer")
    private Set<Order> orders;

    @JsonBackReference
    @OneToMany(mappedBy = "customer", orphanRemoval = true) // weak entity; remove all.
    private Set<ReturnOrder> returnOrders;

    public Customer(String SSN, String name, String surname, String address, byte[] password, String email, Integer age, String phoneNumber) {
        super(SSN, name, surname, address, password, email);
        this.age = age;
        this.phoneNumber = phoneNumber;
    }
}
