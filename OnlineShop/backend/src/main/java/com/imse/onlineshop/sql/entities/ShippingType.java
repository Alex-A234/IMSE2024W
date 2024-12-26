package com.imse.onlineshop.sql.entities;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.imse.onlineshop.vo.ShipmentType;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import java.util.Set;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "shipping_types")
public class ShippingType {
    @Id
    @Column(name = "shipping_type_id", updatable = false, nullable = false, unique = true)
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long shippingTypeId;

    @Column(name = "type", nullable = false)
    private ShipmentType type;

    @Column(name = "descrption", nullable = false)
    private String description;

    @JsonBackReference
    @OneToMany(mappedBy = "shippingType")
    private Set<Order> order;
}
