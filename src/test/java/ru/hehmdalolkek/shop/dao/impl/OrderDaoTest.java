package ru.hehmdalolkek.shop.dao.impl;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.springframework.test.context.jdbc.Sql;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import ru.hehmdalolkek.shop.dao.interfaces.OrderDao;
import ru.hehmdalolkek.shop.model.Order;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@Testcontainers
@TestConfiguration(proxyBeanMethods = false)
@SpringBootTest
@Sql(value = "/db/data/create-data.sql", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
@Sql(value = "/db/data/delete-data.sql", executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
public class OrderDaoTest {

    @Container
    @ServiceConnection
    private static final PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>("postgres:15");

    private final OrderDao orderDao;

    @Autowired
    public OrderDaoTest(OrderDao orderDao) {
        this.orderDao = orderDao;
    }

    @Test
    @DisplayName("When getAllOrders then returned all orders")
    public void whenGetAllOrders_thenReturnedAllOrders() {
        // when
        List<Order> orders = this.orderDao.getAllOrders();

        // then
        assertThat(orders).isNotEmpty();
        assertThat(orders.size()).isEqualTo(3);
    }

    @Test
    @DisplayName("Given existing order id, when getOrderById, then returned optional with order")
    public void givenExistingOrderId_whenGetOrderById_thenReturnedOptionalWithOrder() {
        // given
        int orderId = 1;

        // when
        Optional<Order> optionalOrder = this.orderDao.getOrderById(orderId);

        // then
        assertThat(optionalOrder.isPresent()).isTrue();
        assertThat(optionalOrder.get().getId()).isEqualTo(orderId);
    }

    @Test
    @DisplayName("Given non existing order id, when getOrderById, then returned optional without order")
    public void givenNonExistingOrderId_whenGetOrderById_thenReturnedOptionalWithoutOrder() {
        // given
        int orderId = -1;

        // when
        Optional<Order> optionalOrder = this.orderDao.getOrderById(orderId);

        // then
        assertThat(optionalOrder.isPresent()).isFalse();
    }

    @Test
    @DisplayName("Given order without id, when saveOrder, then new order saved")
    public void givenOrderWithoutId_whenSaveOrder_thenNewOrderSaved() {
        // given
        Order order = new Order();
        order.setDate(LocalDateTime.now());
        order.setTotalCost(1000);

        // when
        Order savedOrder = this.orderDao.saveOrder(order);

        // then
        assertThat(savedOrder.getId()).isNotNull();
    }

    @Test
    @DisplayName("Given order with existing id, when saveOrder, then order is updated")
    public void givenOrderWithExistingId_whenSaveOrder_thenNewOrderSaved() {
        // given
        int orderId = 1;
        LocalDateTime time = LocalDateTime.now();
        Order order = new Order();
        order.setId(orderId);
        order.setDate(time);
        order.setTotalCost(1000);

        // when
        Order savedOrder = this.orderDao.saveOrder(order);

        // then
        assertThat(savedOrder.getId()).isEqualTo(orderId);
        assertThat(savedOrder.getDate().truncatedTo(ChronoUnit.MILLIS))
                .isEqualTo(time.truncatedTo(ChronoUnit.MILLIS));
        assertThat(savedOrder.getTotalCost()).isEqualTo(1000);
    }

    @Test
    @DisplayName("Given order with non exising id, when saveOrder, then new order saved")
    public void givenOrderWithNonExistingId_whenSaveOrder_thenNewOrderSaved() {
        // given
        int orderId = 1000;
        LocalDateTime time = LocalDateTime.now();
        Order order = new Order();
        order.setId(orderId);
        order.setDate(time);
        order.setTotalCost(1000);

        // when
        Order savedOrder = this.orderDao.saveOrder(order);

        // then
        assertThat(savedOrder.getId()).isEqualTo(orderId);
        assertThat(savedOrder.getDate().truncatedTo(ChronoUnit.MILLIS))
                .isEqualTo(time.truncatedTo(ChronoUnit.MILLIS));
        assertThat(savedOrder.getTotalCost()).isEqualTo(1000);
    }

}
