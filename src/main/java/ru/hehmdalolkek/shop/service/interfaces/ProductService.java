package ru.hehmdalolkek.shop.service.interfaces;

import ru.hehmdalolkek.shop.model.Product;

import java.util.List;

public interface ProductService {

    List<Product> getAllActiveProducts();

    Product getProductById(int productId);

    Product createProduct(Product product);

    Product updateProduct(int productId, Product product);

    void softDeleteProductById(int productId);

    void hardDeleteProductById(int productId);

}
