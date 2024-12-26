package com.imse.onlineshop.nosql.entities;

import org.springframework.data.mongodb.core.mapping.Document;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.mongodb.core.mapping.MongoId;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Document("users")
public class UserNoSQL {
    @MongoId
    private String ssn;

    private String name;

    private String surname;

    private String address;

    private byte[] password;

    private String email;
}
