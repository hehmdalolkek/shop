package ru.hehmdalolkek.shop.dao.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import ru.hehmdalolkek.shop.dao.interfaces.OrderDao;
import ru.hehmdalolkek.shop.dao.mapper.OrderRowMapper;
import ru.hehmdalolkek.shop.model.Order;

import java.sql.PreparedStatement;
import java.sql.Statement;
import java.sql.Timestamp;
import java.util.List;
import java.util.Optional;

@RequiredArgsConstructor
@Repository
public class OrderDaoJdbcTemplateImpl implements OrderDao {

    private static final String INSERT_INTO_ORDERS = "INSERT INTO orders (order_date, total_cost) " +
            "VALUES (?, ?)";
    private static final String INSERT_OR_UPDATE_ORDER_BY_ID = "INSERT INTO orders (order_id, order_date, total_cost) " +
            "VALUES (?, ?, ?) ON CONFLICT (order_id) DO UPDATE SET order_date = ?, total_cost = ?";
    private static final String SELECT_ALL_ORDERS = "SELECT * FROM orders";
    private static final String SELECT_ORDER_BY_ID = "SELECT * FROM orders WHERE order_id = ?";

    private final JdbcTemplate jdbcTemplate;

    @Transactional(readOnly = true)
    @Override
    public List<Order> getAllOrders() {
        return this.jdbcTemplate.query(SELECT_ALL_ORDERS, new OrderRowMapper());
    }

    @Transactional(readOnly = true)
    @Override
    public Optional<Order> getOrderById(int orderId) {
        List<Order> foundedOrder =
                this.jdbcTemplate.query(SELECT_ORDER_BY_ID, new OrderRowMapper(), orderId);
        if (foundedOrder.isEmpty()) {
            return Optional.empty();
        } else {
            return Optional.of(foundedOrder.get(0));
        }
    }

    @Transactional
    @Override
    public Order saveOrder(Order order) {
        KeyHolder keyHolder = new GeneratedKeyHolder();
        if (order.getId() == null) {
            this.jdbcTemplate.update(connection -> {
                PreparedStatement statement =
                        connection.prepareStatement(INSERT_INTO_ORDERS, Statement.RETURN_GENERATED_KEYS);
                statement.setTimestamp(1, Timestamp.valueOf(order.getDate()));
                statement.setDouble(2, order.getTotalCost());
                return statement;
            }, keyHolder);
        } else {
            this.jdbcTemplate.update(connection -> {
                PreparedStatement statement =
                        connection.prepareStatement(INSERT_OR_UPDATE_ORDER_BY_ID, Statement.RETURN_GENERATED_KEYS);
                statement.setInt(1,order.getId());
                statement.setTimestamp(2, Timestamp.valueOf(order.getDate()));
                statement.setDouble(3, order.getTotalCost());
                statement.setTimestamp(4, Timestamp.valueOf(order.getDate()));
                statement.setDouble(5, order.getTotalCost());
                return statement;
            }, keyHolder);
        }
        Integer orderId = (Integer) keyHolder.getKeys().get("order_id");
        return this.jdbcTemplate.queryForObject(SELECT_ORDER_BY_ID, new OrderRowMapper(), orderId);
    }

}
