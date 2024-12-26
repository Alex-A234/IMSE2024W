package com.imse.onlineshop.controller.sql.order;

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
import com.imse.onlineshop.controller.sql.order.request.ReturnOrderRequest;
import com.imse.onlineshop.controller.sql.order.response.GetOrderResponse;
import com.imse.onlineshop.controller.sql.order.response.ListOrderResponse;
import com.imse.onlineshop.controller.sql.order.response.ListReturnOrderResponse;
import com.imse.onlineshop.controller.sql.order.response.OrderReportResponse;
import com.imse.onlineshop.controller.sql.order.response.ReturnOrderResponse;
import com.imse.onlineshop.sql.services.OrderService;
import com.imse.onlineshop.sql.services.exceptions.InvalidPaymentInfoException;
import com.imse.onlineshop.sql.services.exceptions.MissingCustomerException;
import com.imse.onlineshop.sql.services.exceptions.MissingOrderException;
import com.imse.onlineshop.sql.services.exceptions.MissingProductException;
import com.imse.onlineshop.sql.services.exceptions.MissingShipmentTypeException;
import com.imse.onlineshop.sql.services.exceptions.OrderInvalidStateException;
import com.imse.onlineshop.sql.services.exceptions.ReturnOrderInvalidStateException;

@RestController
@RequestMapping("/order")
public class OrderController {
  private final OrderService orderService;

  public OrderController(OrderService orderService) {
    this.orderService = orderService;
  }

  @GetMapping(path = "", produces = "application/json")
  public List<ListOrderResponse> list(@RequestHeader("user-id") String user) {
    return orderService.findAllByCustomer(user).stream().map(ListOrderResponse::from)
        .collect(Collectors.toList());
  }

  @GetMapping(path = "/report")
  public OrderReportResponse report() {
    return new OrderReportResponse(orderService.getTop10OrderedProducts());
  }

  @GetMapping(path = "/returned", produces = "application/json")
  public List<ListReturnOrderResponse> listReturned(@RequestHeader("user-id") String user) {
    return orderService.findAllReturnedByCustomer(user).stream().map(ListReturnOrderResponse::from)
        .collect(Collectors.toList());
  }

  @GetMapping(path = "/{id}", produces = "application/json")
  public ResponseEntity<GetOrderResponse> get(@PathVariable Long id) {
    return orderService.findById(id).map(GetOrderResponse::from).map(ResponseEntity::ok)
        .orElse(ResponseEntity.notFound().build());
  }

  @PostMapping(path = "/{customerSSN}/add", consumes = "application/json",
      produces = "application/json")
  public ResponseEntity<?> placeOrder(@PathVariable String customerSSN,
      @RequestBody PlaceOrderRequest payload) {

    try {
      orderService.saveOrder(customerSSN, payload.getShippingType(), payload.getCreditCardNumber(),
          payload.getCreditCardName(), payload.getExpirationDate(), payload.getProductSum(),
          payload.getProducts());
    } catch (MissingCustomerException | MissingShipmentTypeException | MissingProductException
        | ReturnOrderInvalidStateException | OrderInvalidStateException
        | InvalidPaymentInfoException e) {
      return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
    }
    return new ResponseEntity<>(HttpStatus.OK);
  }

  @PostMapping(path = "/{id}/return", consumes = "application/json", produces = "application/json")
  public ResponseEntity<ReturnOrderResponse> returnOrder(@PathVariable Long id,
      @RequestBody ReturnOrderRequest payload) {
    try {
      var result = orderService.returnProducts(payload.getDescription(), id, payload.getCustomer(),
          payload.getProducts());

      return new ResponseEntity<>(new ReturnOrderResponse(result.getDate()), HttpStatus.OK);
    } catch (ReturnOrderInvalidStateException | MissingCustomerException | MissingOrderException
        | MissingProductException e) {
      return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
    }
  }
}
