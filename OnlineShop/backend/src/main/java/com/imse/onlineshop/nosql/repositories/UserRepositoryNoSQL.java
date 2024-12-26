package com.imse.onlineshop.nosql.repositories;

import java.util.List;
import java.util.Optional;
import org.springframework.data.mongodb.repository.Aggregation;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import com.imse.onlineshop.nosql.entities.UserNoSQL;
import com.imse.onlineshop.nosql.entities.orders.UserReturnOrder;
import com.imse.onlineshop.nosql.entities.reports.UserReportNoSQL;

@Repository
public interface UserRepositoryNoSQL extends MongoRepository<UserNoSQL, String> {
  @Aggregation(pipeline = {
      "{ $match: { _class: { $eq: \"com.imse.onlineshop.nosql.entities.CustomerNoSQL\" }, } }",
      "{$unwind: \"$returnedOrders\"}",
      "{ $match: { $expr: { $eq: [{$subtract: [{$year: \"$$NOW\"}, {$year: \"$returnedOrders.date\"}]}, 1] } } }",
      "{$addFields: {\"returnedOrders.date\": {$year: \"$returnedOrders.date\"}}}",
      "{$unwind: \"$returnedOrders.products\"}",
      "{ $lookup: { from: \"users\", let: {product: \"$returnedOrders.products.product\"}, pipeline: [ {$unwind: \"$products\"}, { $match: { $expr: { $eq: [\"$products.productNumber\", \"$$product\"] } } }, { $project: { _id: 0, \"products.productNumber\": 1, \"products.productName\": 1 } } ], as: \"returnedProductProducer\" } }",
      "{$unwind: \"$returnedProductProducer\"}",
      "{ $group: { _id: { name: \"$name\", surname: \"$surname\", productName: \"$returnedProductProducer.products.productName\", year: \"$returnedOrders.date\" }, totalReturned: {$sum: \"$returnedOrders.products.amount\"} } }",
      "{$sort: {\"totalReturned\": -1,}}",
      "{ $group: { _id: { name: \"$_id.name\", surname: \"$_id.surname\", year: \"$_id.year\", }, products: { $push: { productName: \"$_id.productName\", totalReturned: \"$totalReturned\" } } } }",
      "{ $project: { _id: 0, name: \"$_id.name\", surname: \"$_id.surname\", year: \"$_id.year\", products: { $slice: [\"$products\", 0, 5] } } }",
      "{$sort: {\"name\": 1, \"surname\": 1}}"})
  List<UserReportNoSQL> report();

  @Aggregation(
      pipeline = {"{$match: {_class: {$eq: \"com.imse.onlineshop.nosql.entities.CustomerNoSQL\"}}}",
          "{$project: {_id: 0, returnedOrdersLen: {$size: \"$returnedOrders\"}}}",
          "{$group: {_id: null, total: {$sum: \"$returnedOrdersLen\"}}}",
          "{$project: {_id: 0, total: {$add: [\"$total\", 1]}}}",})
  Long nextReturnOrderId();

  @Query(
      value = "{ \"_class\": \"com.imse.onlineshop.nosql.entities.ProducerNoSQL\", \"products.productNumber\": :#{#productId}}",
      fields = "{ _id: 1, _class: 1, products: { $filter: { input: \"$products\", as: \"products\", cond: { $eq: [\"$$products.productNumber\", :#{#productId}] } }, } }")
  Optional<UserNoSQL> findByProductId(@Param("productId") Long productId);

  @Aggregation(
      pipeline = {"{$match: {_class: {$eq: \"com.imse.onlineshop.nosql.entities.CustomerNoSQL\"}}}",
          "{$project: {_id: 0, paymentOptionsLen: {$size: \"$paymentOptions\"}}}",
          "{$group: {_id: null, total: {$sum: \"$paymentOptionsLen\"}}}",
          "{$project: {_id: 0, total: {$add: [\"$total\", 1]}}}",})
  Long nextPaymentId();

  @Aggregation(pipeline = {
      "{ $match: { _class: {$eq: \"com.imse.onlineshop.nosql.entities.CustomerNoSQL\"}, _id: {$eq: :#{#ssn}} } }",
      "{$unwind: \"$returnedOrders\"}", "{$unwind: \"$returnedOrders.products\"}",
      "{ $lookup: { from: \"orders\", let: {order: \"$returnedOrders.orderId\"}, pipeline: [ {$unwind: \"$products\"}, {$match: {$expr: {$eq: [\"$_id\", \"$$order\"]}}}, {$project: {_id: 0,}} ], as: \"order\" } }",
      "{$unwind: \"$order\"}", "{$unwind: \"$order.products\"}", "{$unwind: \"$paymentOptions\"}",
      "{$match: {$expr: {$eq: [\"$paymentOptions.paymentID\", \"$order.paymentId\"]}}}",
      "{ $lookup: { from: \"users\", let: {product: \"$order.products.product\"}, pipeline: [ {$unwind: \"$products\"}, {$match: {$expr: {$eq: [\"$products.productNumber\", \"$$product\"]}}}, ], as: \"orderProductProducer\" } }",
      "{ $lookup: { from: \"users\", let: {product: \"$returnedOrders.products.product\"}, pipeline: [ {$unwind: \"$products\"}, {$match: {$expr: {$eq: [\"$products.productNumber\", \"$$product\"]}}}, ], as: \"returnOrderProductProducer\" } }",
      "{$unwind: \"$orderProductProducer\"}", "{$unwind: \"$returnOrderProductProducer\"}",
      "{ $project: { \"returnOrderKey\": { \"customerSsn\": \"$_id\", \"returnOrderId\": \"$returnedOrders.returnOrderId\" }, \"reasonDescription\": \"$returnedOrders.reasonDescription\", \"date\": \"$returnedOrders.date\", \"order\": { \"orderId\": \"$returnedOrders.orderId\", \"purchaseAmount\": \"$order.purchaseAmount\", \"date\": \"$order.date\", \"cardNumber\": \"$paymentOptions.cardNumber\", }, \"orderProducts\": { \"product.productNumber\": \"$orderProductProducer.products.productNumber\", \"product.pricePerUnit\": \"$orderProductProducer.products.pricePerUnit\", \"product.productName\": \"$orderProductProducer.products.productName\", \"product.producer\": { \"producerName\": \"$orderProductProducer.producerName\", }, \"amount\": \"$order.products.amount\" }, \"returnOrderProducts\": { \"amount\": \"$returnedOrders.products.amount\", \"product.productNumber\": \"$returnOrderProductProducer.products.productNumber\", \"product.pricePerUnit\": \"$returnOrderProductProducer.products.pricePerUnit\", \"product.productName\": \"$returnOrderProductProducer.products.productName\", \"product.producer\": { \"producerName\": \"$returnOrderProductProducer.producerName\", }, } } }",
      "{ $group: { _id: { _id: \"$_id\", date: \"$date\", order: \"$order\", reasonDescription: \"$reasonDescription\", returnOrderKey: \"$returnOrderKey\", returnOrderProducts: \"$returnOrderProducts\", }, orderProducts: {$push: \"$orderProducts\"}, } }",
      "{ $group: { _id: { _id: \"$_id._id\", date: \"$_id.date\", order: \"$_id.order\", reasonDescription: \"$_id.reasonDescription\", returnOrderKey: \"$_id.returnOrderKey\", orderProducts: \"$orderProducts\", }, returnOrderProducts: {$push: \"$_id.returnOrderProducts\"}, } }",
      "{ $project: { _id: 0, \"returnOrderKey\": \"$_id.returnOrderKey\", \"reasonDescription\": \"$_id.reasonDescription\", \"date\": \"$_id.date\", \"order\": { \"order\": \"$_id.order.orderId\", \"purchaseAmount\": \"$_id.order.purchaseAmount\", \"date\": \"$_id.order.date\", \"cardNumber\": \"$_id.order.cardNumber\", \"orderProducts\": \"$_id.orderProducts\" }, \"returnOrderProducts\": \"$returnOrderProducts\" } }",
      "{$sort: {\"returnOrderKey.returnOrderId\": 1}}"})
  List<UserReturnOrder> findAllReturnedOrders(@Param("ssn") String ssn);
}
