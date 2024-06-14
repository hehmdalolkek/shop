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
import ru.hehmdalolkek.shop.dao.interfaces.OrderProductDao;
import ru.hehmdalolkek.shop.model.OrderProduct;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@Testcontainers
@TestConfiguration(proxyBeanMethods = false)
@SpringBootTest
@Sql(value = "/db/data/create-data.sql", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
@Sql(value = "/db/data/delete-data.sql", executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
class OrderProductTest {

    @Container
    @ServiceConnection
    private static final PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>("postgres:15");

    private final OrderProductDao orderProductDao;

    @Autowired
    OrderProductTest(OrderProductDao orderProductDao) {
        this.orderProductDao = orderProductDao;
    }

    @Test
    @DisplayName("Given order id, when getAllOrdersProducts, then returned all orders products")
    public void givenOrderId_whenGetAllOrdersProducts_thenReturnAllOrdersProducts() {
        // given
        int orderId = 2;

        // when
        List<OrderProduct> orderProducts = this.orderProductDao.getAllOrderProductsByOrderId(orderId);

        // then
        assertThat(orderProducts).isNotEmpty();
        assertThat(orderProducts).allMatch(orderProduct -> orderProduct.getOrderId() == orderId);
    }

    @Test
    @DisplayName("Given order product without id, when saveOrderProduct, then returned saved order product")
    public void givenOrderProductWithoutId_whenSaveOrderProduct_thenReturnSavedOrderProduct() {
        // given
        OrderProduct orderProduct = new OrderProduct();
        orderProduct.setOrderId(1);
        orderProduct.setProductId(1);
        orderProduct.setAmount(1);

        // when
        OrderProduct savedOrderProduct = this.orderProductDao.saveOrderProduct(orderProduct);

        // then
        assertThat(savedOrderProduct).isNotNull();
        assertThat(savedOrderProduct.getId()).isNotNull();
        assertThat(savedOrderProduct.getOrderId()).isEqualTo(1);
        assertThat(savedOrderProduct.getProductId()).isEqualTo(1);
        assertThat(savedOrderProduct.getAmount()).isEqualTo(1);
    }

    @Test
    @DisplayName("Given order product with exists id, when saveOrderProduct, then returned saved order product")
    public void givenOrderProductWithExistsId_whenSaveOrderProduct_thenReturnUpdatedOrderProduct() {
        // given
        OrderProduct orderProduct = new OrderProduct();
        orderProduct.setId(1);
        orderProduct.setOrderId(1);
        orderProduct.setProductId(1);
        orderProduct.setAmount(1);

        // when
        OrderProduct savedOrderProduct = this.orderProductDao.saveOrderProduct(orderProduct);

        // then
        assertThat(savedOrderProduct).isNotNull();
        assertThat(savedOrderProduct.getId()).isEqualTo(1);
        assertThat(savedOrderProduct.getOrderId()).isEqualTo(1);
        assertThat(savedOrderProduct.getProductId()).isEqualTo(1);
        assertThat(savedOrderProduct.getAmount()).isEqualTo(1);
    }

    @Test
    @DisplayName("Given order product with non existing, when saveOrderProduct, then returned saved order product")
    public void givenOrderProductWithNonExisting_whenSaveOrderProduct_thenReturnSavedOrderProduct() {
        // given
        OrderProduct orderProduct = new OrderProduct();
        orderProduct.setId(1000);
        orderProduct.setOrderId(1);
        orderProduct.setProductId(1);
        orderProduct.setAmount(1);

        // when
        OrderProduct savedOrderProduct = this.orderProductDao.saveOrderProduct(orderProduct);

        // then
        assertThat(savedOrderProduct).isNotNull();
        assertThat(savedOrderProduct.getId()).isEqualTo(1000);
        assertThat(savedOrderProduct.getOrderId()).isEqualTo(1);
        assertThat(savedOrderProduct.getProductId()).isEqualTo(1);
        assertThat(savedOrderProduct.getAmount()).isEqualTo(1);
    }

}