package ru.hehmdalolkek.shop.dao.mapper;

import org.springframework.jdbc.core.RowMapper;
import ru.hehmdalolkek.shop.model.Order;

import java.sql.ResultSet;
import java.sql.SQLException;

public class OrderRowMapper implements RowMapper<Order> {
    @Override
    public Order mapRow(ResultSet rs, int rowNum) throws SQLException {
        Order order = new Order();
        order.setId(rs.getInt("order_id"));
        order.setDate(rs.getTimestamp("order_date").toLocalDateTime());
        order.setTotalCost(rs.getDouble("total_cost"));
        return order;
    }
}
