package com.imse.onlineshop.nosql.services;

import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.stereotype.Service;
import com.imse.onlineshop.nosql.entities.CustomerNoSQL;
import com.imse.onlineshop.nosql.entities.ProducerNoSQL;
import com.imse.onlineshop.nosql.entities.ProductNoSQL;
import com.imse.onlineshop.nosql.entities.ShoppingCartProductNoSQL;
import com.imse.onlineshop.nosql.entities.UserNoSQL;
import com.imse.onlineshop.nosql.repositories.UserRepositoryNoSQL;
import com.imse.onlineshop.reports.Users;
import com.imse.onlineshop.sql.services.exceptions.MissingCustomerException;
import com.imse.onlineshop.sql.services.exceptions.OrderInvalidStateException;

@Service
public class UserServiceNoSQL {
  private final UserRepositoryNoSQL userRepositoryNoSQL;
  private final MongoTemplate mongoTemplate;

  public UserServiceNoSQL(UserRepositoryNoSQL userRepositoryNoSQL, MongoTemplate mongoTemplate) {
    this.mongoTemplate = mongoTemplate;
    this.userRepositoryNoSQL = userRepositoryNoSQL;
  }

  public List<UserNoSQL> findAll() {
    return userRepositoryNoSQL.findAll();
  }

  public List<Users> report() {
    var result = new ArrayList<Users>();

    userRepositoryNoSQL.report().forEach(row -> {
      for (int i = 0; i < row.getProducts().size(); i++) {
        result.add(new Users(i + 1, row.getName(), row.getSurname(),
            row.getProducts().get(i).getProductName(), row.getYear(),
            row.getProducts().get(i).getTotalReturned()));
      }
    });

    return result;
  }

  public void addToShoppingCart(String ssn, Long productNumber)
      throws MissingCustomerException, OrderInvalidStateException {

    // check if the user exists.
    var user = userRepositoryNoSQL.findById(ssn).get();
    if (user == null) {
      throw new MissingCustomerException(String.format("user with ssn: %s not found", ssn));
    }

    // check if the user is-a customer.
    if (user instanceof ProducerNoSQL) {
      throw new OrderInvalidStateException(
          String.format("trying to return a product for a producer; ssn: %s", ssn));
    }

    CustomerNoSQL customer = (CustomerNoSQL) user;

    // check if the specified product exists in the database and save the product in the variable
    // product
    ProductNoSQL product;
    try {
      ProducerNoSQL producer =
          (ProducerNoSQL) userRepositoryNoSQL.findByProductId(productNumber).get();
      product = producer.getProducts().stream()
          .filter(prod -> prod.getProductNumber() == productNumber).findFirst().get();
    } catch (NoSuchElementException e) {
      throw new OrderInvalidStateException(
          "No product with the specified product number: " + productNumber.toString() + " exists");
    }

    var shoppingCart = customer.getShoppingCartProducts();
    ShoppingCartProductNoSQL shoppingCartProduct = new ShoppingCartProductNoSQL(
        product.getProductNumber(), product.getPricePerUnit(), product.getProductName());
    shoppingCart.add(shoppingCartProduct);
    customer.setShoppingCartProducts(shoppingCart);
    userRepositoryNoSQL.save(customer);
  }

  public List<ShoppingCartProductNoSQL> getShoppingCartProducts(String userId)
      throws MissingCustomerException, OrderInvalidStateException {

    // check if the user exists.
    var user = userRepositoryNoSQL.findById(userId).get();
    if (user == null) {
      throw new MissingCustomerException(String.format("user with ssn: %s not found", userId));
    }

    // check if the user is-a customer.
    if (user instanceof ProducerNoSQL) {
      throw new OrderInvalidStateException(
          String.format("trying to get the products for a producer; ssn: %s", userId));
    }

    CustomerNoSQL customer = (CustomerNoSQL) user;

    if (customer.getShoppingCartProducts().isEmpty()) {
      return null;
    } else {
      return customer.getShoppingCartProducts();
    }

  }

}
