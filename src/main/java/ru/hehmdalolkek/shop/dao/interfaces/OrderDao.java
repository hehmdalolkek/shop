package ru.hehmdalolkek.shop.dao.interfaces;

import ru.hehmdalolkek.shop.model.Order;

import java.util.List;
import java.util.Optional;

public interface OrderDao {

    List<Order> getAllOrders();

    Optional<Order> getOrderById(int orderId);

    Order saveOrder(Order order);

}
