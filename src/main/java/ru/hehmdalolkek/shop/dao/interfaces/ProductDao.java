package ru.hehmdalolkek.shop.dao.interfaces;

import ru.hehmdalolkek.shop.model.Product;

import java.util.List;
import java.util.Optional;

public interface ProductDao {

    List<Product> getAllActiveProducts();

    Optional<Product> getProductById(int productId);

    Product saveProduct(Product product);

    void softDeleteProduct(int productId);

    void hardDeleteProduct(int productId);

}
