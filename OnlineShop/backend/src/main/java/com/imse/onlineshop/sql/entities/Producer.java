package com.imse.onlineshop.sql.entities;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import lombok.*;

import javax.persistence.*;
import java.util.Set;

@Builder
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "producers")
public class Producer extends User {
    @Column(name = "producer_name", nullable = false)
    private String producerName;

    @Column(name = "headquarters", nullable = false)
    private String headquarters;

    @JsonManagedReference
    @JsonBackReference
    @ManyToMany(cascade = {CascadeType.ALL})
    @JoinTable(
            name = "producers_of_producer",
            joinColumns = {@JoinColumn(name = "parent_ssn")},
            inverseJoinColumns = {@JoinColumn(name = "child_ssn")}
    )
    private Set<Producer> producers;

    @JsonBackReference
    @OneToMany(mappedBy = "producer")
    private Set<Product> products;

    public Producer(String SSN, String name, String surname, String address, byte[] password, String email, String producerName, String headquarters) {
        super(SSN, name, surname, address, password, email);
        this.producerName = producerName;
        this.headquarters = headquarters;
    }
}
