package ru.hehmdalolkek.shop.dao.interfaces;

import ru.hehmdalolkek.shop.model.OrderProduct;

import java.util.List;

public interface OrderProductDao {

    List<OrderProduct> getAllOrderProductsByOrderId(int orderId);

    OrderProduct saveOrderProduct(OrderProduct orderProduct);

}
