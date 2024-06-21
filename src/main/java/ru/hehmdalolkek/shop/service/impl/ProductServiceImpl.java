package ru.hehmdalolkek.shop.service.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.hehmdalolkek.shop.dao.interfaces.ProductDao;
import ru.hehmdalolkek.shop.model.Product;
import ru.hehmdalolkek.shop.model.exception.ProductIsAlreadyExistsException;
import ru.hehmdalolkek.shop.model.exception.ProductNotFoundException;
import ru.hehmdalolkek.shop.service.interfaces.ProductService;
import ru.hehmdalolkek.shop.web.dto.ProductDto;
import ru.hehmdalolkek.shop.web.mapper.ProductMapper;

import java.util.ArrayList;
import java.util.List;

@RequiredArgsConstructor
@Service
public class ProductServiceImpl implements ProductService {

    private final ProductDao productDao;

    @Override
    @Transactional(readOnly = true)
    public List<ProductDto> getAllActiveProducts() {
        List<Product> products = this.productDao.getAllActiveProducts();
        List<ProductDto> productDtos = new ArrayList<>();
        for (Product product : products) {
            productDtos.add(ProductMapper.INSTANCE.productToProductDto(product));
        }
        return productDtos;
    }

    @Override
    @Transactional(readOnly = true)
    public ProductDto getProductById(int productId) {
        Product product = this.productDao.getProductById(productId)
                .orElseThrow(() ->
                        new ProductNotFoundException(String.format("Product with id=%d not found", productId)));
        return ProductMapper.INSTANCE.productToProductDto(product);
    }

    @Override
    @Transactional(rollbackFor = ProductIsAlreadyExistsException.class)
    public ProductDto createProduct(ProductDto productDto) {
        Product product = ProductMapper.INSTANCE.productDtoToProduct(productDto);
        if (product.getId() != null
                && this.productDao.getProductById(product.getId()).isPresent()) {
            throw new ProductIsAlreadyExistsException(
                    String.format("Product with id=%d is already exists", product.getId()));
        }
        Product savedProduct = this.productDao.saveProduct(product);
        return ProductMapper.INSTANCE.productToProductDto(savedProduct);
    }

    @Override
    @Transactional(rollbackFor = ProductNotFoundException.class)
    public ProductDto updateProduct(int productId, ProductDto productDto) {
        Product foundedProduct = this.productDao.getProductById(productId)
                .orElseThrow(() ->
                        new ProductNotFoundException(String.format("Product with id=%d not found", productId)));
        Product product = ProductMapper.INSTANCE.productDtoToProduct(productDto);
        foundedProduct.setTitle(product.getTitle());
        foundedProduct.setActive(product.isActive());
        foundedProduct.setPrice(product.getPrice());
        Product savedProduct = this.productDao.saveProduct(product);
        return ProductMapper.INSTANCE.productToProductDto(savedProduct);
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
