package ru.hehmdalolkek.shop.dao.mapper;

import org.springframework.jdbc.core.RowMapper;
import ru.hehmdalolkek.shop.model.Product;

import java.sql.ResultSet;
import java.sql.SQLException;

public class ProductRowMapper implements RowMapper<Product> {
    @Override
    public Product mapRow(ResultSet rs, int rowNum) throws SQLException {
        Product product = new Product();
        product.setId(rs.getInt("product_id"));
        product.setTitle(rs.getString("title"));
        product.setPrice(rs.getDouble("price"));
        product.setActive(rs.getBoolean("active"));
        return product;
    }
}
