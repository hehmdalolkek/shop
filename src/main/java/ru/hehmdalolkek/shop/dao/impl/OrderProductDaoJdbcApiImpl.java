package ru.hehmdalolkek.shop.dao.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import ru.hehmdalolkek.shop.dao.interfaces.OrderProductDao;
import ru.hehmdalolkek.shop.model.OrderProduct;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

@Repository
public class OrderProductDaoJdbcApiImpl implements OrderProductDao {

    private static final String SELECT_ALL_ORDER_PRODUCTS_BY_ID = "SELECT * FROM orders_products WHERE order_id = ?";
    private static final String INSERT_INTO_ORDERS_PRODUCTS = "INSERT INTO orders_products (order_id, product_id, amount) " +
            "VALUES (?, ?, ?)";
    private static final String INSERT_OR_UPDATE_ORDER_PRODUCT_BY_ID =
            "INSERT INTO orders_products (order_product_id, order_id, product_id, amount) VALUES (?, ?, ?, ?) " +
                    "ON CONFLICT (order_product_id) DO UPDATE SET order_id = ?, product_id = ?, amount = ?";
    private static final String SELECT_ORDER_PRODUCT_BY_ID = "SELECT * FROM orders_products WHERE order_product_id = ?";

    private final DataSource dataSource;

    @Autowired
    public OrderProductDaoJdbcApiImpl(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    @Transactional(readOnly = true)
    @Override
    public List<OrderProduct> getAllOrderProductsByOrderId(int orderId) {
        try (Connection connection = this.dataSource.getConnection()) {
            PreparedStatement statement = connection.prepareStatement(SELECT_ALL_ORDER_PRODUCTS_BY_ID);
            statement.setInt(1, orderId);
            List<OrderProduct> orderProducts = new ArrayList<>();
            try (ResultSet resultSet = statement.executeQuery()) {
                while (resultSet.next()) {
                    orderProducts.add(this.mapResultSetToOrderProduct(resultSet));
                }
            }
            return orderProducts;
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Transactional
    @Override
    public OrderProduct saveOrderProduct(OrderProduct orderProduct) {
        try (Connection connection = this.dataSource.getConnection()) {
            String[] returnId = {"order_product_id"};
            if (orderProduct.getId() == null) {
                PreparedStatement statement =
                        connection.prepareStatement(INSERT_INTO_ORDERS_PRODUCTS, returnId);
                statement.setInt(1, orderProduct.getOrderId());
                statement.setInt(2, orderProduct.getProductId());
                statement.setInt(3, orderProduct.getAmount());
                statement.execute();
                ResultSet resultSet = statement.getGeneratedKeys();
                PreparedStatement selectStatement = connection.prepareStatement(SELECT_ORDER_PRODUCT_BY_ID);
                if (resultSet.next()) {
                    selectStatement.setInt(1, resultSet.getInt(1));
                }
                resultSet.close();
                return this.mapResultSetToOrderProduct(selectStatement.executeQuery());
            } else {
                PreparedStatement statement =
                        connection.prepareStatement(INSERT_OR_UPDATE_ORDER_PRODUCT_BY_ID, returnId);
                statement.setInt(1, orderProduct.getId());
                statement.setInt(2, orderProduct.getOrderId());
                statement.setInt(3, orderProduct.getProductId());
                statement.setInt(4, orderProduct.getAmount());
                statement.setInt(5, orderProduct.getOrderId());
                statement.setInt(6, orderProduct.getProductId());
                statement.setInt(7, orderProduct.getAmount());
                statement.execute();
                ResultSet resultSet = statement.getGeneratedKeys();
                PreparedStatement selectStatement = connection.prepareStatement(SELECT_ORDER_PRODUCT_BY_ID);
                if (resultSet.next()) {
                    selectStatement.setInt(1, resultSet.getInt(1));
                }
                resultSet.close();
                return this.mapResultSetToOrderProduct(selectStatement.executeQuery());
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    protected OrderProduct mapResultSetToOrderProduct(ResultSet resultSet) throws SQLException {
        OrderProduct orderProduct = new OrderProduct();
        if (resultSet.next()) {
            orderProduct.setId(resultSet.getInt("order_product_id"));
            orderProduct.setOrderId(resultSet.getInt("order_id"));
            orderProduct.setProductId(resultSet.getInt("product_id"));
            orderProduct.setAmount(resultSet.getInt("amount"));
        }
        return orderProduct;
    }
}
