package com.imse.onlineshop.controller.nosql.products;

import com.imse.onlineshop.controller.sql.products.ProductResponse;
import com.imse.onlineshop.nosql.services.ProductServiceNoSQL;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/nosql/product")
public class ProductControllerNoSQL {
    private final ProductServiceNoSQL productServiceNoSQL;

    public ProductControllerNoSQL(ProductServiceNoSQL productServiceNoSQL) {
        this.productServiceNoSQL = productServiceNoSQL;
    }

    @GetMapping(
            path = "",
            produces = "application/json"
    )
    public List<ProductResponse> list(@RequestHeader("user-id") String user) {
        return productServiceNoSQL.findAllByProducer(user)
                .stream()
                .map(ProductResponse::fromNoSQL)
                .collect(Collectors.toList());
    }
}
