package com.imse.onlineshop.sql.services;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.sql.Date;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;
import org.springframework.stereotype.Service;
import com.github.javafaker.Faker;
import com.imse.onlineshop.reports.Orders;
import com.imse.onlineshop.sql.entities.Customer;
import com.imse.onlineshop.sql.entities.Order;
import com.imse.onlineshop.sql.entities.OrderProducts;
import com.imse.onlineshop.sql.entities.PaymentInformation;
import com.imse.onlineshop.sql.entities.Producer;
import com.imse.onlineshop.sql.entities.Product;
import com.imse.onlineshop.sql.entities.ReturnOrder;
import com.imse.onlineshop.sql.entities.ReturnOrderKey;
import com.imse.onlineshop.sql.entities.ReturnOrderProducts;
import com.imse.onlineshop.sql.entities.ShippingType;
import com.imse.onlineshop.sql.repositories.DeliveryCompanyRepository;
import com.imse.onlineshop.sql.repositories.OrderProductsRepository;
import com.imse.onlineshop.sql.repositories.OrderRepository;
import com.imse.onlineshop.sql.repositories.PaymentInformationRepository;
import com.imse.onlineshop.sql.repositories.ProductRepository;
import com.imse.onlineshop.sql.repositories.ReturnOrderProductsRepository;
import com.imse.onlineshop.sql.repositories.ReturnOrderRepository;
import com.imse.onlineshop.sql.repositories.ShippingTypeRepository;
import com.imse.onlineshop.sql.repositories.UserRepository;
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
public class OrderService {
  private final OrderRepository orderRepository;
  private final ReturnOrderRepository returnOrderRepository;
  private final ProductRepository productRepository;
  private final UserRepository userRepository;
  private final ReturnOrderProductsRepository returnOrderProductsRepository;
  private final DeliveryCompanyRepository deliveryCompanyRepository;
  private final ShippingTypeRepository shippingTypeRepository;
  private final PaymentInformationRepository paymentInformationRepository;
  private final OrderProductsRepository orderProductsRepository;

  public OrderService(OrderRepository orderRepository, ReturnOrderRepository returnOrderRepository,
      ProductRepository productRepository, UserRepository userRepository,
      ReturnOrderProductsRepository returnOrderProductsRepository,
      DeliveryCompanyRepository deliveryCompanyRepository,
      ShippingTypeRepository shippingTypeRepository,
      PaymentInformationRepository paymentInformationRepository,
      OrderProductsRepository orderProductsRepository) {
    this.paymentInformationRepository = paymentInformationRepository;
    this.orderRepository = orderRepository;
    this.returnOrderRepository = returnOrderRepository;
    this.productRepository = productRepository;
    this.userRepository = userRepository;
    this.returnOrderProductsRepository = returnOrderProductsRepository;
    this.deliveryCompanyRepository = deliveryCompanyRepository;
    this.shippingTypeRepository = shippingTypeRepository;
    this.orderProductsRepository = orderProductsRepository;
  }

  public List<Orders> getTop10OrderedProducts() {
    return orderRepository
        .findTop10OrderedProducts().stream().map(obj -> new Orders(((BigInteger) obj[0]).intValue(),
            ((BigDecimal) obj[1]).intValue(), (String) obj[2], (String) obj[3], (Integer) obj[4]))
        .collect(Collectors.toList());
  }

  public List<Order> findAllByCustomer(@NonNull String customerSSN) {
    var userOpt = userRepository.findById(customerSSN);
    if (userOpt.isEmpty() || userOpt.get() instanceof Producer) {
      return List.of();
    }

    return orderRepository.findAllByCustomer((Customer) userOpt.get());
  }

  public List<ReturnOrder> findAllReturnedByCustomer(@NonNull String customerSSN) {
    var userOpt = userRepository.findById(customerSSN);
    if (userOpt.isEmpty() || userOpt.get() instanceof Producer) {
      return List.of();
    }

    return returnOrderRepository.findAllByCustomer((Customer) userOpt.get());
  }

  public Optional<Order> findById(@NonNull Long id) {
    return orderRepository.findById(id);
  }


  public void checkProducts(@NonNull Set<ReturnProducts> products) throws MissingProductException {

    if (products == null) {
      throw new MissingProductException("The user didnt enter any products!");
    }

    var missingProducts =
        products.stream().filter(p -> productRepository.findById(p.getProduct()).isEmpty())
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

  public ReturnOrder returnProducts(@NonNull String description, @NonNull Long orderId,
      @NonNull String customerSSN, @NonNull Set<ReturnProducts> products)
      throws ReturnOrderInvalidStateException, MissingProductException, MissingOrderException,
      MissingCustomerException {

    // check for any products that doesn't exist.
    checkProducts(products);


    // check if the order exists.
    var orderOpt = orderRepository.findById(orderId);
    if (orderOpt.isEmpty()) {
      throw new MissingOrderException(String.format("order with id: %d doesnt exists", orderId));
    }

    // check if the user exists.
    var customerOpt = userRepository.findById(customerSSN);
    if (customerOpt.isEmpty()) {
      throw new MissingCustomerException(String.format("user with ssn: %s not found", customerSSN));
    }

    // check if the user is-a customer.
    if (customerOpt.get() instanceof Producer) {
      throw new ReturnOrderInvalidStateException(
          String.format("trying to return a product for a producer; ssn: %s", customerSSN));
    }

    // create the return order.
    var returnOrder =
        new ReturnOrder(new ReturnOrderKey(customerSSN, returnOrderRepository.nextId()),
            description, new Date(System.currentTimeMillis()), orderOpt.get(),
            (Customer) customerOpt.get(), new HashSet<>());

    // we know that each product exists in the DB.
    // by the above check.
    products.forEach(product -> {
      var productOpt = productRepository.findById(product.getProduct()).get();
      returnOrder.getReturnOrderProducts().add(new ReturnOrderProducts(null, // auto-generated key.
          productOpt, returnOrder, product.getAmount()));
    });

    validateReturnOrder(returnOrder);

    // first save the return order.
    // this will populate the return order table
    // and the return_order_products table.
    var result = returnOrderRepository.save(returnOrder);

    // next we need to update the amount on the products
    // with the returned amount.
    returnOrder.getReturnOrderProducts().forEach(returned -> {
      var product = productRepository.getById(returned.getProduct().getProductNumber());

      product.setAmount(product.getAmount() + returned.getAmount());

      productRepository.save(product);
    });

    return result;
  }

  private void validateReturnOrder(ReturnOrder returnOrder)
      throws ReturnOrderInvalidStateException, MissingOrderException {
    if (returnOrder.getCustomer() == null) {
      throw new ReturnOrderInvalidStateException("missing customer");
    }

    if (!returnOrder.getOrder().getCustomer().getSSN().equals(returnOrder.getCustomer().getSSN())) {
      throw new ReturnOrderInvalidStateException(
          "cannot return items for an order made by another person");
    }

    var invalidAmount = returnOrder.getReturnOrderProducts().stream()
        .anyMatch(returnOrderProducts -> returnOrderProducts.getAmount() == 0);
    if (invalidAmount) {
      throw new ReturnOrderInvalidStateException("cannot return 0 products");
    }

    if (returnOrder.getDate() == null) {
      throw new ReturnOrderInvalidStateException("missing date from return order");
    }

    if (returnOrder.getCustomer() == null) {
      throw new ReturnOrderInvalidStateException("missing customer from order");
    }

    if (!returnOrder.getCustomer().getSSN()
        .equals(returnOrder.getReturnOrderKey().getCustomerSsn())) {
      throw new ReturnOrderInvalidStateException(
          "Customer SSN is not the same as the Key customer SSN");
    }

    if (returnOrder.getReasonDescription() == null) {
      throw new ReturnOrderInvalidStateException("missing return order description");
    }

    var invalidReturnedProducts =
        returnOrder.getReturnOrderProducts().stream().filter(returnedProduct -> {
          var isPresent = new AtomicBoolean(false);

          returnOrder.getOrder().getOrderProducts().forEach(orderedProduct -> {
            if (returnedProduct.getProduct().getProductNumber()
                .equals(orderedProduct.getProduct().getProductNumber())) {
              isPresent.set(true);
            }
          });

          return !isPresent.get();
        }).collect(Collectors.toList());

    if (invalidReturnedProducts.size() > 0) {
      var builder = new StringBuilder();

      for (var invalidReturnedProduct : invalidReturnedProducts) {
        builder.append(invalidReturnedProduct.getProduct().getProductNumber());
        builder.append(" ");
      }

      throw new ReturnOrderInvalidStateException(String.format(
          "the following returned products: %s aren't part of the original order", builder));
    }

    var alreadyReturned =
        returnOrderProductsRepository.findAllByOrderId(returnOrder.getOrder().getOrderId());
    var cantReturn = returnOrder.getReturnOrderProducts().stream().filter(returnedProduct -> {
      var returnedCount = new AtomicInteger(returnedProduct.getAmount());

      alreadyReturned.forEach(ar -> {
        if (returnedProduct.getProduct().getProductNumber()
            .equals(ar.getProduct().getProductNumber())) {
          returnedCount.addAndGet(ar.getAmount());
        }
      });

      var canReturn = new AtomicBoolean(false);
      returnOrder.getOrder().getOrderProducts().forEach(orderedProduct -> {
        if (returnedProduct.getProduct().getProductNumber()
            .equals(orderedProduct.getProduct().getProductNumber())) {
          returnedCount.getAndAdd(-orderedProduct.getAmount());
          canReturn.set(returnedCount.get() <= 0);
        }
      });

      return !canReturn.get();
    }).collect(Collectors.toList());

    if (cantReturn.size() > 0) {
      var builder = new StringBuilder();

      for (var p : cantReturn) {
        builder.append(p.getProduct().getProductNumber());
        builder.append(" ");
      }

      throw new ReturnOrderInvalidStateException(String
          .format("the returned amount for the following products doens't add up: %s", builder));
    }
  }

  public void saveOrder(@NonNull String customerSSN, @NonNull String shipmentType,
      @NonNull String creditCardNr, @NonNull String creditCardName, @NonNull String expirationDate,
      @NonNull Double sum, @NonNull Set<ReturnProducts> productList)
      throws MissingCustomerException, MissingShipmentTypeException, MissingProductException,
      ReturnOrderInvalidStateException, OrderInvalidStateException, InvalidPaymentInfoException {

    if (creditCardNr == null) {
      throw new InvalidPaymentInfoException("credit card nr is null");
    }

    if (shipmentType == null) {
      throw new MissingShipmentTypeException("missing shipmentType");
    }

    if (creditCardName == null) {
      throw new InvalidPaymentInfoException("credit card name is null");
    }

    if (expirationDate == null) {
      throw new InvalidPaymentInfoException("expiration date is null");
    }

    if (sum == null) {
      throw new MissingProductException("Total amount of ordered product prices is null");
    }

    if (productList == null) {
      throw new MissingProductException("Missing products");
    }

    var faker = Faker.instance();
    // check for any products that doesn't exist.
    checkProducts(productList);

    // check if the user exists.
    var customerOpt = userRepository.findById(customerSSN).get();
    if (customerOpt == null) {
      throw new MissingCustomerException(String.format("user with ssn: %s not found", customerSSN));
    }

    // check if the user is-a customer.
    if (customerOpt instanceof Producer) {
      throw new ReturnOrderInvalidStateException(
          String.format("trying to return a product for a producer; ssn: %s", customerSSN));
    }

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


    var deliveryCompanies = deliveryCompanyRepository.findAll();
    var deliveryCompany =
        deliveryCompanies.get(faker.random().nextInt(0, deliveryCompanies.size() - 1));

    var shippingTypes = shippingTypeRepository.findAll();
    ShippingType shippingType = null;
    try {
      shippingType = shippingTypes.get(0);
      if (shippingType.getType().toString() != shipmentType) {
        shippingType = shippingTypes.get(1);
      }
    } catch (IllegalArgumentException e) {
      System.out.println("Missing ShippingType- null!");
    }

    // creating the order
    var newOrder = new Order(null, sum, new Date(System.currentTimeMillis()), deliveryCompany,
        shippingType, null, null, (Customer) customerOpt, new HashSet<>());

    // create and validate PaymentInfoObject
    PaymentInformation paymentInfo = new PaymentInformation(null, creditCardNr, creditCardName,
        sqlExpirationDate, (Customer) customerOpt, new HashSet<>());
    validatePaymentInfo(paymentInfo);
    paymentInfo.getOrder().add(newOrder);
    paymentInformationRepository.save(paymentInfo);

    // setting PaymentInfo in order
    newOrder.setPaymentInformation(paymentInfo);

    // setting orderProducts in the order class
    Product productOpt;
    Set<OrderProducts> orderProducts = newOrder.getOrderProducts();
    for (ReturnProducts prod : productList) {
      productOpt = productRepository.findById(prod.getProduct()).get();
      orderProducts.add(new OrderProducts(null, newOrder, productOpt, prod.getAmount()));
    }
    System.out.println("OrderProducts:");
    System.out.println(orderProducts.toString());
    // saving the order in database
    newOrder.setOrderProducts(orderProducts);

    // validate the order
    validateOrder(newOrder);
    orderRepository.save(newOrder);

    // save the created OrderProducts
    orderProducts.forEach(orderedProd -> {
      orderProductsRepository.save(orderedProd);
    });

    // next we need to update the amount on the products
    // with the returned amount.
    newOrder.getOrderProducts().forEach(orderedProduct -> {
      var product = productRepository.getById(orderedProduct.getProduct().getProductNumber());
      product.setAmount(product.getAmount() - orderedProduct.getAmount());

      productRepository.save(product);
    });


  }

  private void validatePaymentInfo(@NonNull PaymentInformation paymentInfo)
      throws InvalidPaymentInfoException {
    if (paymentInfo.getCardNumber() == null) {
      throw new InvalidPaymentInfoException("card nr is null!");
    }

    if (paymentInfo.getCustomer() == null) {
      throw new InvalidPaymentInfoException("customer is null- missing customer");
    }

    if (paymentInfo.getExpirationDate() == null) {
      throw new InvalidPaymentInfoException("Date is null - missing Date in paymentInfo");
    }

    if (paymentInfo.getNameOnCard() == null) {
      throw new InvalidPaymentInfoException("Name is null - missing name in paymentInfo");
    }

    if (paymentInfo.getOrder() == null) {
      throw new InvalidPaymentInfoException("missing Orders in paymentInfo");
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

  private void validateOrder(Order newOrder) throws OrderInvalidStateException {

    if (newOrder.getDate() == null) {
      throw new OrderInvalidStateException("missing date from return order");
    }

    var invalidAmount = newOrder.getOrderProducts().stream()
        .anyMatch(returnOrderProducts -> returnOrderProducts.getAmount() == 0);
    if (invalidAmount) {
      throw new OrderInvalidStateException("cannot return 0 products");
    }

    if (newOrder.getCustomer() == null) {
      throw new OrderInvalidStateException("missing customer");
    }

    if (newOrder.getDate() == null) {
      throw new OrderInvalidStateException("missing date from return order");
    }

    if (newOrder.getCustomer() == null) {
      throw new OrderInvalidStateException("missing customer from order");
    }

  }
}
