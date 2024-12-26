package com.imse.onlineshop.controller.sql.products;

import com.imse.onlineshop.sql.services.ProductService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/product")
public class ProductController {
    private final ProductService productService;

    public ProductController(ProductService productService) {
        this.productService = productService;
    }

    @GetMapping(
            path = "",
            produces = "application/json"
    )
    public List<ProductResponse> list(@RequestHeader("user-id") String user) {
        return productService.findAllByProducer(user)
                .stream()
                .map(ProductResponse::from)
                .collect(Collectors.toList());
    }
}
