package com.imse.onlineshop.controller.nosql.order;

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
import com.imse.onlineshop.controller.sql.order.PlaceOrderRequest;
import com.imse.onlineshop.controller.sql.order.request.ReturnOrderRequest;
import com.imse.onlineshop.controller.sql.order.response.ListOrderResponse;
import com.imse.onlineshop.controller.sql.order.response.ListReturnOrderResponse;
import com.imse.onlineshop.controller.sql.order.response.OrderReportResponse;
import com.imse.onlineshop.controller.sql.order.response.ReturnOrderResponse;
import com.imse.onlineshop.nosql.services.OrderServiceNoSQL;
import com.imse.onlineshop.sql.services.exceptions.InvalidPaymentInfoException;
import com.imse.onlineshop.sql.services.exceptions.MissingCustomerException;
import com.imse.onlineshop.sql.services.exceptions.MissingOrderException;
import com.imse.onlineshop.sql.services.exceptions.MissingProductException;
import com.imse.onlineshop.sql.services.exceptions.MissingShipmentTypeException;
import com.imse.onlineshop.sql.services.exceptions.OrderInvalidStateException;
import com.imse.onlineshop.sql.services.exceptions.ReturnOrderInvalidStateException;

@RestController
@RequestMapping("/nosql/order")
public class OrderControllerNoSQL {
  private final OrderServiceNoSQL orderServiceNoSQL;

  public OrderControllerNoSQL(OrderServiceNoSQL orderServiceNoSQL) {
    this.orderServiceNoSQL = orderServiceNoSQL;
  }

  @GetMapping(path = "", produces = "application/json")
  public List<ListOrderResponse> list(@RequestHeader("user-id") String user) {
    return orderServiceNoSQL.findAllByCustomer(user).stream().map(ListOrderResponse::fromNoSQL)
        .collect(Collectors.toList());
  }

  @GetMapping(path = "/report")
  public OrderReportResponse report() {
    return new OrderReportResponse(orderServiceNoSQL.getTop10OrderedProducts());
  }

  @GetMapping(path = "/returned", produces = "application/json")
  public List<ListReturnOrderResponse> listReturned(@RequestHeader("user-id") String user) {
    return orderServiceNoSQL.findAllReturnedByCustomer(user).stream()
        .map(ListReturnOrderResponse::fromNoSQL).collect(Collectors.toList());
  }

  @PostMapping(path = "/{id}/return", consumes = "application/json", produces = "application/json")
  public ResponseEntity<ReturnOrderResponse> returnOrder(@PathVariable Long id,
      @RequestBody ReturnOrderRequest payload) {
    try {
      var result = orderServiceNoSQL.returnProducts(payload.getDescription(), id,
          payload.getCustomer(), payload.getProducts());

      return new ResponseEntity<>(new ReturnOrderResponse(result.getDate()), HttpStatus.OK);
    } catch (MissingProductException | MissingOrderException | MissingCustomerException
        | ReturnOrderInvalidStateException e) {
      return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
    }
  }

  @PostMapping(path = "/{customerSSN}/add", consumes = "application/json",
      produces = "application/json")
  public ResponseEntity<?> placeOrder(@PathVariable String customerSSN,
      @RequestBody PlaceOrderRequest payload) {

    try {
      orderServiceNoSQL.saveOrder(customerSSN, payload.getShippingType(),
          payload.getCreditCardNumber(), payload.getCreditCardName(), payload.getExpirationDate(),
          payload.getProductSum(), payload.getProducts());
    } catch (MissingShipmentTypeException | MissingProductException | OrderInvalidStateException
        | InvalidPaymentInfoException e) {
      System.out.println(e.getMessage());
      return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
    }
    return new ResponseEntity<>(HttpStatus.OK);
  }

}
