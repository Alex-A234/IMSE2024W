package com.imse.onlineshop.sql.services;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import org.springframework.stereotype.Service;
import com.imse.onlineshop.controller.sql.shoppingcart.ShoppingCartController;
import com.imse.onlineshop.nosql.entities.CustomerNoSQL;
import com.imse.onlineshop.nosql.entities.DeliveryCompanyNoSQL;
import com.imse.onlineshop.nosql.entities.OrderNoSQL;
import com.imse.onlineshop.nosql.entities.PaymentInformationNoSQL;
import com.imse.onlineshop.nosql.entities.ProducerNoSQL;
import com.imse.onlineshop.nosql.entities.ProductNoSQL;
import com.imse.onlineshop.nosql.entities.ReturnOrderNoSQL;
import com.imse.onlineshop.nosql.entities.ReturnOrderProductNoSQL;
import com.imse.onlineshop.nosql.entities.ShippingTypeNoSQL;
import com.imse.onlineshop.nosql.entities.ShoppingCartProductNoSQL;
import com.imse.onlineshop.nosql.entities.reports.OrderProductNoSQL;
import com.imse.onlineshop.nosql.repositories.DeliveryCompanyRepositoryNoSQL;
import com.imse.onlineshop.nosql.repositories.OrderRepositoryNoSQL;
import com.imse.onlineshop.nosql.repositories.ShippingTypeRepositoryNoSQL;
import com.imse.onlineshop.nosql.repositories.UserRepositoryNoSQL;
import com.imse.onlineshop.sql.entities.Customer;
import com.imse.onlineshop.sql.entities.Producer;
import com.imse.onlineshop.sql.entities.User;
import com.imse.onlineshop.sql.repositories.DeliveryCompanyRepository;
import com.imse.onlineshop.sql.repositories.OrderRepository;
import com.imse.onlineshop.sql.repositories.ShippingTypeRepository;
import com.imse.onlineshop.sql.repositories.UserRepository;

@Service
public class NoSQLMigrator {
  private final UserRepository userRepository;
  private final DeliveryCompanyRepository deliveryCompanyRepository;
  private final ShippingTypeRepository shippingTypeRepository;
  private final OrderRepository orderRepository;

  private final UserRepositoryNoSQL userRepositoryNoSQL;
  private final DeliveryCompanyRepositoryNoSQL deliveryCompanyRepositoryNoSQL;
  private final ShippingTypeRepositoryNoSQL shippingTypeRepositoryNoSQL;
  private final OrderRepositoryNoSQL orderRepositoryNoSQL;

  public NoSQLMigrator(UserRepository userRepository,
      DeliveryCompanyRepository deliveryCompanyRepository,
      ShippingTypeRepository shippingTypeRepository, OrderRepository orderRepository,
      UserRepositoryNoSQL userRepositoryNoSQL,
      DeliveryCompanyRepositoryNoSQL deliveryCompanyRepositoryNoSQL,
      ShippingTypeRepositoryNoSQL shippingTypeRepositoryNoSQL,
      OrderRepositoryNoSQL orderRepositoryNoSQL) {
    this.userRepository = userRepository;
    this.deliveryCompanyRepository = deliveryCompanyRepository;
    this.shippingTypeRepository = shippingTypeRepository;
    this.orderRepository = orderRepository;
    this.userRepositoryNoSQL = userRepositoryNoSQL;
    this.deliveryCompanyRepositoryNoSQL = deliveryCompanyRepositoryNoSQL;
    this.shippingTypeRepositoryNoSQL = shippingTypeRepositoryNoSQL;
    this.orderRepositoryNoSQL = orderRepositoryNoSQL;
  }

  public void migrateDatabase() {
    migrateUsers();
    migrateDeliveryCompanies();
    migrateShippingTypes();
    migrateOrders();
  }

  private void migrateUsers() {



    userRepository.findAll().forEach(user -> {
      if (user instanceof Customer) {
        var customer = (Customer) user;
        List<ShoppingCartProductNoSQL> shoppingCartProducts =
            new ArrayList<ShoppingCartProductNoSQL>();
        // check if customer has a shoppingCart with products
        if (ShoppingCartController.shoppingCart.containsKey(user.getSSN())) {
          List<com.imse.onlineshop.sql.entities.Product> sqlProducts =
              ShoppingCartController.shoppingCart.get(user.getSSN());
          for (com.imse.onlineshop.sql.entities.Product sqlProduct : sqlProducts) {
            shoppingCartProducts.add(new ShoppingCartProductNoSQL(sqlProduct.getProductNumber(),
                sqlProduct.getPricePerUnit(), sqlProduct.getProductName()));
          }
        }
        userRepositoryNoSQL.save(new CustomerNoSQL(customer.getSSN(), customer.getName(),
            customer.getSurname(), customer.getAddress(), customer.getPassword(),
            customer.getEmail(), customer.getAge(), customer.getPhoneNumber(),
            customer.getPaymentInformation().stream()
                .map(paymentInformation -> new PaymentInformationNoSQL(
                    paymentInformation.getPaymentID(), paymentInformation.getCardNumber(),
                    paymentInformation.getNameOnCard(), paymentInformation.getExpirationDate()))
                .collect(Collectors.toSet()),
            customer.getReturnOrders().stream()
                .map(returnOrder -> new ReturnOrderNoSQL(
                    returnOrder.getReturnOrderKey().getReturnOrderId(),
                    returnOrder.getReasonDescription(), returnOrder.getDate(),
                    returnOrder.getOrder().getOrderId(),
                    returnOrder.getReturnOrderProducts().stream()
                        .map(returnOrderProduct -> new ReturnOrderProductNoSQL(
                            returnOrderProduct.getProduct().getProductNumber(),
                            returnOrderProduct.getAmount()))
                        .collect(Collectors.toSet())))
                .collect(Collectors.toSet()),
            shoppingCartProducts));
      } else {
        var producer = (Producer) user;
        userRepositoryNoSQL.save(new ProducerNoSQL(producer.getSSN(), producer.getName(),
            producer.getSurname(), producer.getAddress(), producer.getPassword(),
            producer.getEmail(), producer.getProducerName(), producer.getHeadquarters(),
            producer.getProducts().stream()
                .map(product -> new ProductNoSQL(product.getProductNumber(),
                    product.getPricePerUnit(), product.getAmount(), product.getProductName()))
                .collect(Collectors.toSet()),
            producer.getProducers().stream().map(User::getSSN).collect(Collectors.toSet())));
      }
    });
  }

  private void migrateDeliveryCompanies() {
    deliveryCompanyRepository.findAll().forEach(deliveryCompany -> {
      deliveryCompanyRepositoryNoSQL
          .save(new DeliveryCompanyNoSQL(deliveryCompany.getDeliveryCompanyId(),
              deliveryCompany.getName(), deliveryCompany.getCity()));
    });
  }

  private void migrateShippingTypes() {
    shippingTypeRepository.findAll().forEach(shippingType -> {
      shippingTypeRepositoryNoSQL.save(new ShippingTypeNoSQL(shippingType.getShippingTypeId(),
          shippingType.getType(), shippingType.getDescription()));
    });
  }

  private void migrateOrders() {
    orderRepository.findAll().forEach(order -> {
      orderRepositoryNoSQL.save(new OrderNoSQL(order.getOrderId(), order.getPurchaseAmount(),
          order.getDate(), order.getDeliveryCompanies().getDeliveryCompanyId(),
          order.getShippingType().getShippingTypeId(), order.getPaymentInformation().getPaymentID(),
          order.getCustomer().getSSN(),
          order.getOrderProducts().stream()
              .map(orderedProducts -> new OrderProductNoSQL(
                  orderedProducts.getProduct().getProductNumber(), orderedProducts.getAmount()))
              .collect(Collectors.toSet())));
    });
  }
}
