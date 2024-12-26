package com.imse.onlineshop.sql.entities;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@ToString
@Table(name = "order_products")
public class OrderProducts {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long uuid;

  @JsonManagedReference
  @ManyToOne
  @JoinColumn(name = "order_id", nullable = false)
  private Order order;

  @JsonManagedReference
  @ManyToOne
  @JoinColumn(name = "product_number", nullable = false)
  private Product product;

  @Column(name = "amount", nullable = false)
  private Integer amount;
}
