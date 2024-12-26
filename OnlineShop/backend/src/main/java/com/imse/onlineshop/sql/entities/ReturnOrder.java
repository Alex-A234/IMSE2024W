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
@Table(name = "return_orders")
public class ReturnOrder {
    @EmbeddedId
    private ReturnOrderKey returnOrderKey;

    @Column(name = "reason_description", nullable = false)
    private String reasonDescription;

    @Column(name = "date", nullable = false)
    private Date date;

    @JsonManagedReference
    @ManyToOne
    @JoinColumn(name = "order_id", nullable = false)
    private Order order;

    @JsonManagedReference
    @ManyToOne
    @MapsId("customerSsn")
    @JoinColumn(name = "customer_ssn", nullable = false)
    private Customer customer;

    @JsonBackReference
    @OneToMany(mappedBy = "returnOrder", cascade = CascadeType.ALL)
    private Set<ReturnOrderProducts> returnOrderProducts;
}
