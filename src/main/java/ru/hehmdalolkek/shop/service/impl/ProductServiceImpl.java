package ru.hehmdalolkek.shop.service.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.hehmdalolkek.shop.dao.interfaces.ProductDao;
import ru.hehmdalolkek.shop.model.Product;
import ru.hehmdalolkek.shop.model.exception.ProductIsAlreadyExistsException;
import ru.hehmdalolkek.shop.model.exception.ProductNotFoundException;
import ru.hehmdalolkek.shop.service.interfaces.ProductService;

import java.util.List;

@RequiredArgsConstructor
@Service
public class ProductServiceImpl implements ProductService {

    private final ProductDao productDao;

    @Override
    @Transactional(readOnly = true)
    public List<Product> getAllActiveProducts() {
        return this.productDao.getAllActiveProducts();
    }

    @Override
    @Transactional(readOnly = true)
    public Product getProductById(int productId) {
        return this.productDao.getProductById(productId)
                .orElseThrow(() ->
                        new ProductNotFoundException(String.format("Product with id=%d not found", productId)));
    }

    @Override
    @Transactional(rollbackFor = ProductIsAlreadyExistsException.class)
    public Product createProduct(Product product) {
        if (product.getId() != null
                && this.productDao.getProductById(product.getId()).isPresent()) {
            throw new ProductIsAlreadyExistsException(
                    String.format("Product with id=%d is already exists", product.getId()));
        }
        return this.productDao.saveProduct(product);
    }

    @Override
    @Transactional(rollbackFor = ProductNotFoundException.class)
    public Product updateProduct(int productId, Product product) {
        Product foundedProduct = this.productDao.getProductById(productId)
                .orElseThrow(() ->
                        new ProductNotFoundException(String.format("Product with id=%d not found", productId)));
        foundedProduct.setTitle(product.getTitle());
        foundedProduct.setActive(product.isActive());
        foundedProduct.setPrice(product.getPrice());
        return this.productDao.saveProduct(foundedProduct);
    }

    @Override
    @Transactional
    public void softDeleteProductById(int productId) {
        this.productDao.softDeleteProduct(productId);
    }

    @Override
    @Transactional
    public void hardDeleteProductById(int productId) {
        this.productDao.hardDeleteProduct(productId);
    }

}
