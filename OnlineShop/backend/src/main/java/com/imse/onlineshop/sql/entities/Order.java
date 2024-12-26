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
@Table(name = "orders")
public class Order {
    @Id
    @Column(name = "order_id", updatable = false, nullable = false, unique = true)
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long orderId;

    @Column(name = "purchase_amount", nullable = false)
    private Double purchaseAmount;

    @Column(name = "date", nullable = false)
    private Date date;

    @JsonManagedReference
    @ManyToOne
    @JoinColumn(name = "delivery_company_id", nullable = false)
    private DeliveryCompany deliveryCompanies;

    @JsonManagedReference
    @ManyToOne
    @JoinColumn(name = "shipping_type_id", nullable = false)
    private ShippingType shippingType;

    @JsonManagedReference
    @ManyToOne
    @JoinColumn(name = "payment_id", nullable = false)
    private PaymentInformation paymentInformation;

    @JsonBackReference
    @OneToMany(mappedBy = "order")
    private Set<ReturnOrder> returnOrders;

    @JsonManagedReference
    @ManyToOne
    @JoinColumn(name = "customer_ssn", nullable = false)
    private Customer customer;

    @JsonBackReference
    @OneToMany(mappedBy = "order")
    private Set<OrderProducts> orderProducts;
}
