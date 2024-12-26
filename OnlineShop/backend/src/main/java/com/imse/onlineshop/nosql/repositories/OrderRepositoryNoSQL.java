package com.imse.onlineshop.nosql.repositories;

import java.util.List;
import org.springframework.data.mongodb.repository.Aggregation;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import com.imse.onlineshop.nosql.entities.OrderNoSQL;
import com.imse.onlineshop.nosql.entities.orders.UserOrder;
import com.imse.onlineshop.nosql.entities.reports.OrderReportNoSQL;

@Repository
public interface OrderRepositoryNoSQL extends MongoRepository<OrderNoSQL, Long> {
  @Aggregation(pipeline = {"{ $match: {$expr: {$eq: [\"$customerSSN\", :#{#ssn}]}} }",
      "{ $lookup: { from: \"users\", let: {ssn: \"$customerSSN\"}, pipeline: [ {$match: {$expr: {$eq: [\"$_id\", \"$$ssn\"]}}}, {$project: {_id: 1, paymentOptions: 1,}} ], as: \"customer\" } }",
      "{$unwind: \"$customer\"}", "{$unwind: \"$customer.paymentOptions\"}",
      "{ $match: {$expr: {$eq: [\"$customer.paymentOptions.paymentID\", \"$paymentId\"]}} }",
      "{ $project: { _id: 1, purchaseAmount: 1, date: 1, products: 1, cardNumber: \"$customer.paymentOptions.cardNumber\" } }",
      "{$unwind: \"$products\"}",
      "{ $lookup: { from: \"users\", let: {product: \"$products.product\"}, pipeline: [ {$unwind: \"$products\"}, {$match: {$expr: {$eq: [\"$products.productNumber\", \"$$product\"]}}}, { $project: { _id: 0, \"products\": 1, producerName: 1 } } ], as: \"orderedProduct\" } }",
      "{$unwind: \"$orderedProduct\"}",
      "{ $project: { _id: 1, cardNumber: 1, date: 1, purchaseAmount: 1, orderedProduct: { amount: \"$products.amount\", \"product.productNumber\": \"$orderedProduct.products.productNumber\", \"product.pricePerUnit\": \"$orderedProduct.products.pricePerUnit\", \"product.amount\": \"$orderedProduct.products.amount\", \"product.productName\": \"$orderedProduct.products.productName\", \"product.producer.producerName\": \"$orderedProduct.producerName\" } } }",
      "{ $group: { _id: { \"_id\": \"$_id\", \"cardNumber\": \"$cardNumber\", \"date\": \"$date\", \"purchaseAmount\": \"$purchaseAmount\" }, orderedProducts: { $push: \"$orderedProduct\" } } }",
      "{$sort: {\"_id._id\": 1}}",
      "{ $project: { _id: \"$_id._id\", order: \"$_id._id\", cardNumber: \"$_id.cardNumber\", date: \"$_id.date\", purchaseAmount: \"$_id.purchaseAmount\", orderProducts: \"$orderedProducts\" } }"})
  List<UserOrder> findByUser(@Param("ssn") String customer);


  @Aggregation(pipeline = {
      "{ $match: { $expr: { $eq: [{$subtract: [{$year: \"$$NOW\"}, {$year: \"$date\"}]}, 1] } } }",
      "{$addFields: {\"date\": {$year: \"$date\"}}}", "{$unwind: \"$products\"}",
      "{$lookup: { from: \"users\", let: {product: \"$products.product\"}, pipeline: [ {$unwind: \"$products\"}, {$match: {$expr: {$eq: [\"$products.productNumber\", \"$$product\"]}}}], as: \"orderedProduct\" } }",
      "{$unwind: \"$orderedProduct\"}",
      "{ $group: { _id: { productName: \"$orderedProduct.products.productName\", producerName: \"$orderedProduct.producerName\", year: \"$date\" }, totalOrdered: {$sum: \"$products.amount\"} } }",
      "{$sort: {\"totalOrdered\": -1}}",
      "{ $group: { _id: { year: \"$_id.year\"}, products: { $push: { productName: \"$_id.productName\", totalOrdered: \"$totalOrdered\", producerName: \"$_id.producerName\" } } } }",
      "{ $project: { _id: 0, year: \"$_id.year\", products: { $slice: [\"$products\", 0, 10] } } }"})
  List<OrderReportNoSQL> getTop10orderedProducts();
}
