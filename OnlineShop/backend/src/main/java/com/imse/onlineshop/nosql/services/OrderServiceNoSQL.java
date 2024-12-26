package com.imse.onlineshop.nosql.services;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.stereotype.Service;
import com.github.javafaker.Faker;
import com.imse.onlineshop.nosql.entities.CustomerNoSQL;
import com.imse.onlineshop.nosql.entities.OrderNoSQL;
import com.imse.onlineshop.nosql.entities.PaymentInformationNoSQL;
import com.imse.onlineshop.nosql.entities.ProducerNoSQL;
import com.imse.onlineshop.nosql.entities.ReturnOrderNoSQL;
import com.imse.onlineshop.nosql.entities.ReturnOrderProductNoSQL;
import com.imse.onlineshop.nosql.entities.ShippingTypeNoSQL;
import com.imse.onlineshop.nosql.entities.UserNoSQL;
import com.imse.onlineshop.nosql.entities.orders.UserOrder;
import com.imse.onlineshop.nosql.entities.orders.UserReturnOrder;
import com.imse.onlineshop.nosql.entities.reports.OrderProductNoSQL;
import com.imse.onlineshop.nosql.repositories.DeliveryCompanyRepositoryNoSQL;
import com.imse.onlineshop.nosql.repositories.OrderRepositoryNoSQL;
import com.imse.onlineshop.nosql.repositories.ShippingTypeRepositoryNoSQL;
import com.imse.onlineshop.nosql.repositories.UserRepositoryNoSQL;
import com.imse.onlineshop.reports.Orders;
import com.imse.onlineshop.sql.services.exceptions.InvalidPaymentInfoException;
import com.imse.onlineshop.sql.services.exceptions.MissingCustomerException;
import com.imse.onlineshop.sql.services.exceptions.MissingOrderException;
import com.imse.onlineshop.sql.services.exceptions.MissingProductException;
import com.imse.onlineshop.sql.services.exceptions.MissingShipmentTypeException;
import com.imse.onlineshop.sql.services.exceptions.OrderInvalidStateException;
import com.imse.onlineshop.sql.services.exceptions.ReturnOrderInvalidStateException;
import com.imse.onlineshop.vo.ReturnProducts;
import lombok.NonNull;

@Service
public class OrderServiceNoSQL {
  private final MongoTemplate mongoTemplate;
  private final OrderRepositoryNoSQL orderRepositoryNoSQL;
  private final UserRepositoryNoSQL userRepositoryNoSQL;
  private final DeliveryCompanyRepositoryNoSQL deliveryCompanyRepositoryNoSQL;
  private final ShippingTypeRepositoryNoSQL shippingTypeRepositoryNoSQL;

  public OrderServiceNoSQL(MongoTemplate mongoTemplate, OrderRepositoryNoSQL orderRepositoryNoSQL,
      UserRepositoryNoSQL userRepositoryNoSQL,
      DeliveryCompanyRepositoryNoSQL deliveryCompanyRepositoryNoSQL,
      ShippingTypeRepositoryNoSQL shippingTypeRepositoryNoSQL) {
    this.mongoTemplate = mongoTemplate;
    this.orderRepositoryNoSQL = orderRepositoryNoSQL;
    this.userRepositoryNoSQL = userRepositoryNoSQL;
    this.deliveryCompanyRepositoryNoSQL = deliveryCompanyRepositoryNoSQL;
    this.shippingTypeRepositoryNoSQL = shippingTypeRepositoryNoSQL;
  }

  public List<Orders> getTop10OrderedProducts() {
    List<Orders> result = new ArrayList<Orders>();
    orderRepositoryNoSQL.getTop10orderedProducts().forEach(row -> {
      for (int i = 0; i < row.getProducts().size(); i++) {
        result.add(new Orders(i + 1, row.getProducts().get(i).getTotalOrdered(),
            row.getProducts().get(i).getProductName(), row.getProducts().get(i).getProducerName(),
            row.getYear()));
      }
    });
    return result;
  }

  public List<UserOrder> findAllByCustomer(@NonNull String customerSSN) {
    return orderRepositoryNoSQL.findByUser(customerSSN);
  }

  public List<UserReturnOrder> findAllReturnedByCustomer(@NonNull String customerSSN) {
    return userRepositoryNoSQL.findAllReturnedOrders(customerSSN);
  }

  public Optional<OrderNoSQL> findById(@NonNull Long id) {
    return Optional.empty();
  }

  public ReturnOrderNoSQL returnProducts(@NonNull String description, @NonNull Long orderId,
      @NonNull String customerSSN, @NonNull Set<ReturnProducts> products)
      throws MissingProductException, MissingOrderException, MissingCustomerException,
      ReturnOrderInvalidStateException {

    // check for any products that doesn't exist.
    checkProducts(products);

    // check if the order exists.
    var order = orderRepositoryNoSQL.findById(orderId);
    if (order.isEmpty()) {
      throw new MissingOrderException(String.format("order with id: %d doesnt exists", orderId));
    }

    // check if the user exists.
    var customerOpt = userRepositoryNoSQL.findById(customerSSN);
    if (customerOpt.isEmpty()) {
      throw new MissingCustomerException(String.format("user with ssn: %s not found", customerSSN));
    }

    // check if the user is-a customer.
    if (customerOpt.get() instanceof ProducerNoSQL) {
      throw new ReturnOrderInvalidStateException(
          String.format("trying to return a product for a producer; ssn: %s", customerSSN));
    }

    // create the return order.
    var returnOrder = new ReturnOrderNoSQL(userRepositoryNoSQL.nextReturnOrderId(), description,
        new Date(System.currentTimeMillis()), orderId,
        products.stream().map(p -> new ReturnOrderProductNoSQL(p.getProduct(), p.getAmount()))
            .collect(Collectors.toSet()));

    validateReturnOrder((CustomerNoSQL) customerOpt.get(), order.get(), returnOrder);

    ((CustomerNoSQL) customerOpt.get()).getReturnedOrders().add(returnOrder);

    userRepositoryNoSQL.save(customerOpt.get());

    // next we need to update the amount on the products
    // with the returned amount.
    returnOrder.getProducts().forEach(returned -> {
      mongoTemplate.updateFirst(
          new Query(Criteria.where("_class").is("com.imse.onlineshop.nosql.entities.ProducerNoSQL")
              .and("products.productNumber").is(returned.getProduct())),
          new Update().inc("products.$.amount", returned.getAmount()), UserNoSQL.class

      );
    });

    return returnOrder;
  }

  private void validateReturnOrder(CustomerNoSQL customerNoSQL, OrderNoSQL orderNoSQL,
      ReturnOrderNoSQL returnOrderNoSQL) throws ReturnOrderInvalidStateException {
    var invalidAmount = returnOrderNoSQL.getProducts().stream().anyMatch(p -> p.getAmount() == 0);
    if (invalidAmount) {
      throw new ReturnOrderInvalidStateException("cannot return 0 products");
    }

    if (returnOrderNoSQL.getDate() == null) {
      throw new ReturnOrderInvalidStateException("missing date from return order");
    }

    if (returnOrderNoSQL.getReasonDescription() == null) {
      throw new ReturnOrderInvalidStateException("missing return order description");
    }

    var invalidReturnedProducts =
        returnOrderNoSQL.getProducts().stream().filter(returnedProduct -> {
          var isPresent = new AtomicBoolean(false);

          orderNoSQL.getProducts().forEach(orderedProduct -> {
            if (returnedProduct.getProduct().equals(orderedProduct.getProduct())) {
              isPresent.set(true);
            }
          });

          return !isPresent.get();
        }).collect(Collectors.toList());

    if (invalidReturnedProducts.size() > 0) {
      var builder = new StringBuilder();

      for (var invalidReturnedProduct : invalidReturnedProducts) {
        builder.append(invalidReturnedProduct.getProduct());
        builder.append(" ");
      }

      throw new ReturnOrderInvalidStateException(String.format(
          "the following returned products: %s aren't part of the original order", builder));
    }

    var alreadyReturned = customerNoSQL.getReturnedOrders().stream()
        .filter(p -> p.getOrderId().equals(orderNoSQL.getOrderId()))
        .map(ReturnOrderNoSQL::getProducts).flatMap(Collection::stream)
        .collect(Collectors.toList());

    var cantReturn = returnOrderNoSQL.getProducts().stream().filter(returnedProduct -> {
      var returnedCount = new AtomicInteger(returnedProduct.getAmount());

      alreadyReturned.forEach(ar -> {
        if (returnedProduct.getProduct().equals(ar.getProduct())) {
          returnedCount.addAndGet(ar.getAmount());
        }
      });

      var canReturn = new AtomicBoolean(false);
      orderNoSQL.getProducts().forEach(orderedProduct -> {
        if (returnedProduct.getProduct().equals(orderedProduct.getProduct())) {
          returnedCount.getAndAdd(-orderedProduct.getAmount());
          canReturn.set(returnedCount.get() <= 0);
        }
      });

      return !canReturn.get();
    }).collect(Collectors.toList());

    if (cantReturn.size() > 0) {
      var builder = new StringBuilder();

      for (var p : cantReturn) {
        builder.append(p.getProduct());
        builder.append(" ");
      }

      throw new ReturnOrderInvalidStateException(String
          .format("the returned amount for the following products doens't add up: %s", builder));
    }
  }

  private void checkProducts(@NonNull Set<ReturnProducts> products) throws MissingProductException {
    var missingProducts =
        products.stream().filter(p -> userRepositoryNoSQL.findByProductId(p.getProduct()).isEmpty())
            .collect(Collectors.toList());

    if (missingProducts.size() > 0) {
      var builder = new StringBuilder();

      for (var missingProduct : missingProducts) {
        builder.append(missingProduct.getProduct());
        builder.append(" ");
      }

      throw new MissingProductException(
          String.format("the following products don't exists: %s", builder));
    }
  }

  private void validatePaymentInfo(@NonNull PaymentInformationNoSQL paymentInfo)
      throws InvalidPaymentInfoException {
    if (paymentInfo.getCardNumber() == null) {
      throw new InvalidPaymentInfoException("card nr is null!");
    }

    if (paymentInfo.getExpirationDate() == null) {
      throw new InvalidPaymentInfoException("Date is null - missing Date in paymentInfo");
    }

    if (paymentInfo.getNameOnCard() == null) {
      throw new InvalidPaymentInfoException("Name is null - missing name in paymentInfo");
    }

    // check if credit card number contains letter
    char[] chars = paymentInfo.getCardNumber().toCharArray();
    boolean InvalidCreditCardNr = false;
    for (char c : chars) {
      if (!Character.isDigit(c)) {
        InvalidCreditCardNr = true;
      }
    }
    if (InvalidCreditCardNr) {
      throw new InvalidPaymentInfoException("credit card nr contains non-numeric elements");
    }

    // check if expiration date is valid
    if (paymentInfo.getExpirationDate().before(new Date(System.currentTimeMillis()))) {
      throw new InvalidPaymentInfoException(
          "The credit card is expired - please enter another credit card!");
    }
  }

  private void validateOrder(OrderNoSQL newOrder) throws OrderInvalidStateException {

    if (newOrder.getDate() == null) {
      throw new OrderInvalidStateException("missing date from order");
    }

    var invalidAmount = newOrder.getProducts().stream()
        .anyMatch(returnOrderProducts -> returnOrderProducts.getAmount() == 0);
    if (invalidAmount) {
      throw new OrderInvalidStateException("cannot order 0 products");
    }

  }

  public void saveOrder(String customerSSN, String shippingType, String creditCardNumber,
      String creditCardName, String expirationDate, Double productSum, Set<ReturnProducts> products)
      throws InvalidPaymentInfoException, MissingShipmentTypeException, MissingProductException,
      OrderInvalidStateException {

    if (creditCardNumber == null) {
      throw new InvalidPaymentInfoException("credit card nr is null");
    }

    if (shippingType == null) {
      throw new MissingShipmentTypeException("missing shipmentType");
    }

    if (creditCardName == null) {
      throw new InvalidPaymentInfoException("credit card name is null");
    }

    if (expirationDate == null) {
      throw new InvalidPaymentInfoException("expiration date is null");
    }

    if (productSum == null) {
      throw new MissingProductException("Total amount of ordered product prices is null");
    }

    if (products == null) {
      throw new MissingProductException("Missing products");
    }

    var faker = Faker.instance();
    // check for any products that doesn't exist.
    checkProducts(products);

    // create SQL Date object with expiration date
    SimpleDateFormat sdf1 = new SimpleDateFormat("yyyy-MM-dd");
    java.util.Date date = null;
    try {
      date = sdf1.parse(expirationDate);
    } catch (ParseException e1) {
      throw new InvalidPaymentInfoException(
          "expiration date has invalid format - expected: YYYY-MM-DD");
    }
    java.sql.Date sqlExpirationDate = new java.sql.Date(date.getTime());

    // get random delivery company
    var deliveryCompanies = deliveryCompanyRepositoryNoSQL.findAll();
    var deliveryCompany =
        deliveryCompanies.get(faker.random().nextInt(0, deliveryCompanies.size() - 1));

    var shippingTypes = shippingTypeRepositoryNoSQL.findAll();
    ShippingTypeNoSQL newShippingType = null;
    try {
      newShippingType = shippingTypes.get(0);
      if (newShippingType.getType().toString() != shippingType) {
        newShippingType = shippingTypes.get(1);
      }
    } catch (IllegalArgumentException e) {
      System.out.println("Missing ShippingType- null!");
    }

    var customer = (CustomerNoSQL) userRepositoryNoSQL.findById(customerSSN).get();

    // create nosql paymentInformation
    PaymentInformationNoSQL paymentInformationNoSQL = new PaymentInformationNoSQL(
        userRepositoryNoSQL.nextPaymentId(), creditCardNumber, creditCardName, sqlExpirationDate);

    mongoTemplate.updateFirst(
        new Query(Criteria.where("_class").is("com.imse.onlineshop.nosql.entities.CustomerNoSQL")
            .and("_id").is(customer.getSsn())),
        new Update().addToSet("paymentOptions", paymentInformationNoSQL), UserNoSQL.class);


    validatePaymentInfo(paymentInformationNoSQL);

    long orderId = orderRepositoryNoSQL.findAll().size() + 1;

    var newOrder = new OrderNoSQL(orderId, productSum, new Date(System.currentTimeMillis()),
        deliveryCompany.getDeliveryCompanyId(), newShippingType.getShippingTypeId(),
        paymentInformationNoSQL.getPaymentID(), customerSSN,
        products.stream().map(p -> new OrderProductNoSQL(p.getProduct(), p.getAmount()))
            .collect(Collectors.toSet()));

    System.out.println(newOrder);

    validateOrder(newOrder);

    orderRepositoryNoSQL.save(newOrder);

    // updating the amount of total products left for each producer in ProducerNoSQL due to the
    // newlycreated Order
    newOrder.getProducts().forEach(orderedProduct -> {
      mongoTemplate.updateFirst(
          new Query(Criteria.where("_class").is("com.imse.onlineshop.nosql.entities.ProducerNoSQL")
              .and("products.productNumber").is(orderedProduct.getProduct())),
          new Update().inc("products.$.amount", -orderedProduct.getAmount()), UserNoSQL.class);
    });

  }
}
