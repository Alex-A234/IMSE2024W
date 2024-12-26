package com.imse.onlineshop.sql.entities;

import java.util.Set;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "products")
public class Product {
  @Id
  @Column(name = "product_number", updatable = false, nullable = false, unique = true)
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long productNumber;

  @Column(name = "price_per_unit", nullable = false)
  private Double pricePerUnit;

  @Column(name = "amount", nullable = false)
  private Integer amount;

  @Column(name = "product_name", nullable = false)
  private String productName;

  @JsonManagedReference
  @ManyToOne
  @JoinColumn(name = "producer_ssn", nullable = false)
  private Producer producer;

  @JsonBackReference
  @OneToMany(mappedBy = "product")
  private Set<OrderProducts> orderProducts;

  @JsonBackReference
  @OneToMany(mappedBy = "product")
  private Set<ReturnOrderProducts> returnOrderProducts;
}
