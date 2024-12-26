package com.imse.onlineshop.controller.nosql.shoppingCart;

import java.util.List;
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
import com.imse.onlineshop.controller.nosql.products.ProductResponseShoppingCart;
import com.imse.onlineshop.controller.sql.shoppingcart.request.AddToShoppingCartRequest;
import com.imse.onlineshop.nosql.entities.ShoppingCartProductNoSQL;
import com.imse.onlineshop.nosql.services.UserServiceNoSQL;
import com.imse.onlineshop.sql.services.exceptions.MissingCustomerException;
import com.imse.onlineshop.sql.services.exceptions.OrderInvalidStateException;

@RestController
@RequestMapping("/nosql/shoppingCart")
public class ShoppingCartControllerNoSQL {

  UserServiceNoSQL userServiceNoSQL;

  public ShoppingCartControllerNoSQL(UserServiceNoSQL userServiceNoSQL) {
    this.userServiceNoSQL = userServiceNoSQL;
  }

  @GetMapping(path = "", produces = "application/json")
  public List<ProductResponseShoppingCart> list(@RequestHeader("user-id") String userId) {
    List<ShoppingCartProductNoSQL> productList = null;
    try {
      productList = userServiceNoSQL.getShoppingCartProducts(userId);
    } catch (MissingCustomerException | OrderInvalidStateException e) {
      // TODO Auto-generated catch block
      e.printStackTrace();
    }
    if (productList == null)
      return null;
    return productList.stream().map(ProductResponseShoppingCart::fromNoSQL)
        .collect(Collectors.toList());
  }

  @PostMapping(path = "/{ssn}/add", consumes = "application/json", produces = "application/json")
  public ResponseEntity<?> addToShoppingCart(@PathVariable String ssn,
      @RequestBody AddToShoppingCartRequest payload) {
    try {
      userServiceNoSQL.addToShoppingCart(ssn, payload.getProduct().getProductNumber());
    } catch (IllegalArgumentException | MissingCustomerException | OrderInvalidStateException e) {
      return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
    }
    return new ResponseEntity<>(HttpStatus.OK);
  }
}
