package com.imse.onlineshop.sql.entities;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.Column;
import javax.persistence.Embeddable;
import java.io.Serializable;
import java.util.Objects;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Embeddable
public class ReturnOrderKey implements Serializable {
    @Column(name = "customer_ssn")
    private String customerSsn;

    @Column(name = "return_order_id")
    private Long returnOrderId;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ReturnOrderKey that = (ReturnOrderKey) o;
        return Objects.equals(customerSsn, that.customerSsn) && Objects.equals(returnOrderId, that.returnOrderId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(customerSsn, returnOrderId);
    }
}
