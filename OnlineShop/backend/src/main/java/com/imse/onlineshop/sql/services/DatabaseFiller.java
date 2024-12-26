package com.imse.onlineshop.sql.services;

import com.github.javafaker.Faker;
import com.imse.onlineshop.sql.entities.*;
import com.imse.onlineshop.sql.repositories.*;
import com.imse.onlineshop.vo.ShipmentType;
import org.springframework.stereotype.Service;

import java.sql.Date;
import java.util.*;
import java.util.concurrent.atomic.AtomicReference;

@Service
public class DatabaseFiller {
    private static final int USER_LIMIT = 30;

    private final UserRepository userRepository;
    private final CustomerRepository customerRepository;
    private final PaymentInformationRepository paymentInformationRepository;
    private final ProducerRepository producerRepository;
    private final DeliveryCompanyRepository deliveryCompanyRepository;
    private final ProductRepository productRepository;
    private final ShippingTypeRepository shippingTypeRepository;
    private final OrderRepository orderRepository;
    private final OrderProductsRepository orderProductsRepository;
    private final ReturnOrderRepository returnOrderRepository;

    public DatabaseFiller(
            UserRepository userRepository,
            CustomerRepository customerRepository,
            PaymentInformationRepository paymentInformationRepository,
            ProducerRepository producerRepository,
            DeliveryCompanyRepository deliveryCompanyRepository,
            ProductRepository productRepository,
            ShippingTypeRepository shippingTypeRepository,
            OrderRepository orderRepository,
            OrderProductsRepository orderProductsRepository,
            ReturnOrderRepository returnOrderRepository
    ) {
        this.userRepository = userRepository;
        this.customerRepository = customerRepository;
        this.paymentInformationRepository = paymentInformationRepository;
        this.producerRepository = producerRepository;
        this.deliveryCompanyRepository = deliveryCompanyRepository;
        this.productRepository = productRepository;
        this.shippingTypeRepository = shippingTypeRepository;
        this.orderRepository = orderRepository;
        this.orderProductsRepository = orderProductsRepository;
        this.returnOrderRepository = returnOrderRepository;
    }

    public void fillDatabase() {
        insertUsers();
        insertProducersOfProducers();
        insertPaymentInformation();
        insertDeliveryCompany();
        insertProducts();
        insertShippingType();
        insertOrders();
        insertReturnOrders();
    }

    private void insertReturnOrders() {
        var returnOrderId = new AtomicReference<>(1L);

        orderRepository.findAll()
                .forEach(order -> {
                    var faker = Faker.instance();

                    if (faker.random().nextBoolean()) {
                        var returnOrder = new ReturnOrder(
                                new ReturnOrderKey(
                                        order.getCustomer().getSSN(),
                                        returnOrderId.get()
                                ),
                                "unsatisfied with the product",
                                Date.valueOf(order.getDate().toLocalDate().plusDays(faker.random().nextInt(7, 31))),
                                order,
                                order.getCustomer(),
                                new HashSet<>()
                        );

                        order.getOrderProducts().forEach(orderProducts -> {
                            if (faker.random().nextBoolean()) {
                                var returnAmount = orderProducts.getAmount() - faker.random().nextInt(0, 2);

                                if (returnAmount > 0) {
                                    var returnOrderProducts = new ReturnOrderProducts(
                                            null,
                                            orderProducts.getProduct(),
                                            returnOrder,
                                            returnAmount
                                    );

                                    returnOrder.getReturnOrderProducts().add(returnOrderProducts);
                                }
                            }
                        });

                        if (returnOrder.getReturnOrderProducts().size() > 0) {
                            returnOrderRepository.save(returnOrder);
                            returnOrderId.updateAndGet(v -> v + 1);
                        }
                    }
                });
    }

    private void insertOrders() {
        var products = productRepository.findAll();
        var shippingTypes = shippingTypeRepository.findAll();
        var deliveryCompanies = deliveryCompanyRepository.findAll();

        var faker = Faker.instance();

        customerRepository.findAll()
                .stream()
                .peek(customer -> {
                    var orderCount = faker.random().nextInt(1, 35);
                    for (int i = 0; i < orderCount; i++) {
                        // select random products
                        var productCount = faker.random().nextInt(3, 5);
                        var customerProducts = new Product[productCount];
                        var customerProductsAmount = new Integer[productCount];
                        var totalAmount = 0.0;
                        for (int j = 0; j < productCount; j++) {
                            customerProducts[j] = products.get(faker.random().nextInt(0, products.size() - 1));
                            customerProductsAmount[j] = faker.random().nextInt(1, 3);
                            totalAmount += customerProductsAmount[j] * customerProducts[j].getPricePerUnit();
                        }

                        var currentYear = faker.random().nextBoolean() ? 2022 : 2021;
                        var currentMonth = faker.random().nextInt(Calendar.JANUARY, Calendar.DECEMBER);
                        var currentDay = faker.random().nextInt(1, 15);

                        // create order
                        var order = new Order(
                                null,
                                totalAmount,
                                new Date(
                                        new GregorianCalendar(
                                                currentYear,
                                                currentMonth,
                                                currentDay
                                        ).getTime().getTime()
                                ),
                                deliveryCompanies.get(faker.random().nextInt(0, deliveryCompanies.size() - 1)),
                                shippingTypes.get(faker.random().nextInt(0, shippingTypes.size() - 1)),
                                customer.getPaymentInformation().stream().findAny().get(),
                                null,
                                customer,
                                new HashSet<>()
                        );

                        for (int j = 0; j < productCount; j++) {
                            order.getOrderProducts().add(new OrderProducts(
                                    null,
                                    order,
                                    customerProducts[j],
                                    customerProductsAmount[j]
                            ));
                        }

                        orderRepository.save(order);
                        orderProductsRepository.saveAll(order.getOrderProducts());
                    }
                }).forEach(customerRepository::save);
    }

    private void insertShippingType() {
        shippingTypeRepository.saveAll(List.of(
                new ShippingType(
                        null,
                        ShipmentType.EXPRESS,
                        "express delivery within 1 week",
                        null
                ),
                new ShippingType(
                        null,
                        ShipmentType.NORMAL,
                        "normal delivery within 2-3 weeks",
                        null
                )
        ));
    }

    private void insertPaymentInformation() {
        var random = Faker.instance().random();
        customerRepository.findAll()
                .stream()
                .peek(customer -> {
                    var paymentMethodsCount = random.nextInt(2, 4);
                    var payments = new PaymentInformation[paymentMethodsCount];
                    for (int i = 0; i < paymentMethodsCount; i++) {
                        var payment = new PaymentInformation(
                                null,
                                String.format(
                                        "%d%d%d%d-%d%d%d%d-%d%d%d%d-%d%d%d%d",
                                        random.nextInt(0, 9),
                                        random.nextInt(0, 9),
                                        random.nextInt(0, 9),
                                        random.nextInt(0, 9),
                                        random.nextInt(0, 9),
                                        random.nextInt(0, 9),
                                        random.nextInt(0, 9),
                                        random.nextInt(0, 9),
                                        random.nextInt(0, 9),
                                        random.nextInt(0, 9),
                                        random.nextInt(0, 9),
                                        random.nextInt(0, 9),
                                        random.nextInt(0, 9),
                                        random.nextInt(0, 9),
                                        random.nextInt(0, 9),
                                        random.nextInt(0, 9)
                                ),
                                customer.getName() + " " + customer.getSurname(),
                                new Date(new GregorianCalendar(2023 + random.nextInt(1, 2), Calendar.DECEMBER, 1).getTime().getTime()),
                                customer,
                                null
                        );

                        paymentInformationRepository.save(payment);
                        payments[i] = payment;
                    }

                    customer.setPaymentInformation(new HashSet<>(Set.of(payments)));
                }).forEach(userRepository::save);
    }

    private void insertProducersOfProducers() {
        var producers = producerRepository.findAll();
        for (int i = 0; i < producers.size() / 2; i += 5) {
            producers.get(i).setProducers(new HashSet<>(Set.of(
                    producers.get(i + 1),
                    producers.get(i + 2),
                    producers.get(i + 3),
                    producers.get(i + 4)
            )));
        }

        producerRepository.saveAll(producers);
    }

    private void insertUsers() {
        var domains = new String[]{
                "gmail.com",
                "protonmail.com",
                "icloud.com"
        };

        var users = new User[USER_LIMIT];

        for (int i = 0; i < USER_LIMIT; i++) {
            var n = Faker.instance().name();
            var firstName = n.firstName();
            var lastName = n.lastName();
            var address = Faker.instance().address();
            var psswd = Faker.instance().random().hex().getBytes();
            var email = firstName + "." + lastName + "@" + domains[i % domains.length];

            if (i < USER_LIMIT / 2) {
                var age = Faker.instance().random().nextInt(20, 55);
                var phone = Faker.instance().phoneNumber();

                users[i] = new Customer(
                        UUID.randomUUID().toString(),
                        firstName,
                        lastName,
                        address.fullAddress(),
                        psswd,
                        email,
                        age,
                        phone.phoneNumber()
                );
            } else {
                var company = Faker.instance().company();
                var hq = Faker.instance().country();

                users[i] = new Producer(
                        UUID.randomUUID().toString(),
                        firstName,
                        lastName,
                        address.fullAddress(),
                        psswd,
                        email,
                        company.name(),
                        hq.capital()
                );
            }
        }

        userRepository.saveAll(List.of(users));
    }

    private void insertProducts() {
        producerRepository.findAll()
                .stream()
                .peek(producer -> {
                    var faker = Faker.instance();
                    var productCount = faker.random().nextInt(3, 10);

                    var products = new Product[productCount];
                    for (int i = 0; i < productCount; i++) {
                        products[i] = new Product(
                                null,
                                1 + faker.random().nextInt(8, 17) * faker.random().nextDouble(),
                                faker.random().nextInt(7500, 10000),
                                faker.commerce().productName(),
                                producer,
                                null,
                                null
                        );
                    }

                    productRepository.saveAll(List.of(products));
                }).forEach(producerRepository::save);
    }

    private void insertDeliveryCompany() {
        var deliveryCompanies = new DeliveryCompany[10];

        for (int i = 0; i < deliveryCompanies.length; i++) {
            var random = Faker.instance();
            deliveryCompanies[i] = new DeliveryCompany(
                    null,
                    random.company().name(),
                    random.country().capital(),
                    null
            );
        }

        deliveryCompanyRepository.saveAll(List.of(deliveryCompanies));
    }
}