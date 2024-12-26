package com.imse.onlineshop.controller.sql.shoppingcart;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import com.imse.onlineshop.controller.sql.products.ProductResponse;
import com.imse.onlineshop.controller.sql.shoppingcart.request.AddToShoppingCartRequest;
import com.imse.onlineshop.sql.entities.Product;
import com.imse.onlineshop.sql.services.ProductService;

@RestController
@RequestMapping("/shoppingCart")
public class ShoppingCartController {

  private ProductService productService;

  public static Map<String, List<Product>> shoppingCart;

  public ShoppingCartController(ProductService productService) {
    this.productService = productService;
    shoppingCart = new HashMap<String, List<Product>>();
  }

  @GetMapping(path = "", produces = "application/json")
  public List<ProductResponse> list(@RequestHeader("user-id") String userId) {
    if (shoppingCart.get(userId) == null) {
      return null;
    }
    return shoppingCart.get(userId).stream().map(ProductResponse::from)
        .collect(Collectors.toList());
  }

  @PostMapping(path = "/{userSSN}/add", consumes = "application/json",
      produces = "application/json")
  public ResponseEntity<?> addToShoppingCart(@PathVariable String userSSN,
      @RequestBody AddToShoppingCartRequest payload) {
    var product = productService.findById(payload.getProduct().getProductNumber());
    System.out.println(product);
    // check if shoppingCart is empty
    if (shoppingCart.containsKey(userSSN)) {
      shoppingCart.get(userSSN).add(product);
    } else {
      shoppingCart.put(userSSN, new ArrayList<Product>());
      shoppingCart.get(userSSN).add(product);
    }
    return new ResponseEntity<>(HttpStatus.OK);
  }
}
