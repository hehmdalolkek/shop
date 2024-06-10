package ru.hehmdalolkek.shop.dao.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcOperations;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import ru.hehmdalolkek.shop.dao.interfaces.ProductDao;
import ru.hehmdalolkek.shop.model.Product;
import ru.hehmdalolkek.shop.model.mapper.ProductRowMapper;

import java.util.List;
import java.util.Optional;

@RequiredArgsConstructor
@Repository
public class ProductDaoNamedParameterJdbcOperationsImpl implements ProductDao {

    private static final String SELECT_PRODUCT_BY_ID = "SELECT * FROM products WHERE product_id = :productId";
    private static final String DELETE_PRODUCT_BY_ID = "DELETE FROM products WHERE product_id = :productId";
    private static final String SELECT_ALL_ACTIVE_PRODUCTS = "SELECT * FROM products WHERE active = true";
    private static final String INSERT_INTO_PRODUCTS = "INSERT INTO products (title, price, active) " +
            "VALUES (:title, :price, true)";
    private static final String INSERT_OR_UPDATE_PRODUCT_BY_ID = "INSERT INTO products (product_id, title, price, active) " +
            "VALUES (:productId, :title, :price, :active) " +
            "ON CONFLICT (product_id) " +
            "DO UPDATE SET title = EXCLUDED.title, price = EXCLUDED.price, active = EXCLUDED.active";
    private static final String UPDATE_PRODUCT_SET_ACTIVE_IS_FALSE_BY_ID = "UPDATE products SET active = false " +
            "WHERE product_id = :productId";

    private final NamedParameterJdbcOperations namedJdbcOperations;

    @Transactional(readOnly = true)
    @Override
    public List<Product> getAllActiveProducts() {
        return this.namedJdbcOperations.query(SELECT_ALL_ACTIVE_PRODUCTS, new ProductRowMapper());
    }

    @Transactional(readOnly = true)
    @Override
    public Optional<Product> getProductById(int productId) {
        SqlParameterSource params = new MapSqlParameterSource("productId", productId);
        List<Product> products =
                this.namedJdbcOperations.query(SELECT_PRODUCT_BY_ID, params, new ProductRowMapper());
        if (products.isEmpty()) {
            return Optional.empty();
        } else {
            return Optional.of(products.get(0));
        }
    }

    @Transactional
    @Override
    public Product saveProduct(Product product) {
        SqlParameterSource params = new MapSqlParameterSource()
                .addValue("productId", product.getId())
                .addValue("title", product.getTitle())
                .addValue("price", product.getPrice())
                .addValue("active", product.isActive());
        KeyHolder keyHolder = new GeneratedKeyHolder();
        if (product.getId() != null) {
            this.namedJdbcOperations.update(
                    INSERT_OR_UPDATE_PRODUCT_BY_ID,
                    params,
                    keyHolder,
                    new String[]{"product_id"}
            );
        } else {
            this.namedJdbcOperations.update(
                    INSERT_INTO_PRODUCTS,
                    params,
                    keyHolder,
                    new String[]{"product_id"}
            );
        }
        int productId = keyHolder.getKey().intValue();
        return this.namedJdbcOperations.queryForObject(
                SELECT_PRODUCT_BY_ID,
                new MapSqlParameterSource("productId", productId),
                new ProductRowMapper()
        );
    }

    @Transactional
    @Override
    public void softDeleteProduct(int productId) {
        SqlParameterSource params = new MapSqlParameterSource("productId", productId);
        namedJdbcOperations.update(UPDATE_PRODUCT_SET_ACTIVE_IS_FALSE_BY_ID, params);
    }

    @Transactional
    @Override
    public void hardDeleteProduct(int productId) {
        SqlParameterSource params = new MapSqlParameterSource("productId", productId);
        namedJdbcOperations.update(DELETE_PRODUCT_BY_ID, params);
    }

}
