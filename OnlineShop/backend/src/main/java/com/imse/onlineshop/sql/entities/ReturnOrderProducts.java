package com.imse.onlineshop.sql.entities;

import com.fasterxml.jackson.annotation.JsonManagedReference;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "return_order_products")
public class ReturnOrderProducts {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long uuid;

    @JsonManagedReference
    @ManyToOne
    @JoinColumn(name = "product_number", nullable = false)
    private Product product;

    @JsonManagedReference
    @ManyToOne
    @JoinColumn(name = "customer_ssn", nullable = false)
    @JoinColumn(name = "return_order_id", nullable = false)
    private ReturnOrder returnOrder;

    @Column(name = "amount", nullable = false)
    private Integer amount;
}
