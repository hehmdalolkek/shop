package ru.hehmdalolkek.shop.service.impl;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.hehmdalolkek.shop.dao.interfaces.ProductDao;
import ru.hehmdalolkek.shop.model.Product;
import ru.hehmdalolkek.shop.model.exception.ProductIsAlreadyExistsException;
import ru.hehmdalolkek.shop.model.exception.ProductNotFoundException;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ProductServiceImplTest {

    @Mock
    private ProductDao productDao;

    @InjectMocks
    private ProductServiceImpl productService;

    @Test
    @DisplayName("Given two active products, when getAllActiveProducts, then get all active products")
    public void givenTwoActiveProducts_whenGetAllActiveProducts_thenGetAllActiveProducts() {
        // given
        Product p1 = new Product();
        Product p2 = new Product();
        List<Product> activeProducts = List.of(p1, p2);
        when(productDao.getAllActiveProducts()).thenReturn(activeProducts);

        // when
        List<Product> allActiveProducts = productService.getAllActiveProducts();

        // then
        assertThat(allActiveProducts).isEqualTo(activeProducts);
        verify(productDao).getAllActiveProducts();
        verifyNoMoreInteractions(productDao);
    }

    @Test
    @DisplayName("Given existing product id, when getProductById, then get product")
    public void givenExistingProductId_whenGetProductById_thenGetProduct() {
        // given
        Product product = new Product();
        when(productDao.getProductById(anyInt())).thenReturn(Optional.of(product));

        // when
        Product givenProduct = productService.getProductById(1);

        // then
        assertThat(givenProduct).isEqualTo(product);
        verify(productDao).getProductById(anyInt());
        verifyNoMoreInteractions(productDao);
    }

    @Test
    @DisplayName("Given non existing product id, when getProductById, then throw exception")
    public void givenNonExistingProductId_whenGetProductById_thenThrowException() {
        // given
        when(productDao.getProductById(anyInt())).thenReturn(Optional.empty());

        // when
        // then
        assertThatThrownBy(() -> {
            productService.getProductById(1);
        })
                .isInstanceOf(ProductNotFoundException.class)
                .hasMessage("Product with id=1 not found");
        verify(productDao).getProductById(anyInt());
        verifyNoMoreInteractions(productDao);
    }

    @Test
    @DisplayName("Given product with non existing id, when createProduct, then get saved product")
    public void givenProductWithNonExistingId_whenCreateProduct_thenGetSavedProduct() {
        // given
        Product product = new Product();
        product.setId(1);
        product.setTitle("title");
        product.setActive(true);
        when(productDao.getProductById(anyInt())).thenReturn(Optional.empty());
        when(productDao.saveProduct(any(Product.class))).thenReturn(product);

        // when
        Product savedProduct = productService.createProduct(product);

        // then
        assertThat(savedProduct).isEqualTo(product);
        verify(productDao).getProductById(anyInt());
        verify(productDao).saveProduct(any(Product.class));
        verifyNoMoreInteractions(productDao);
    }

    @Test
    @DisplayName("Given product with existing id, when createProduct, then throw exception")
    public void givenProductWithExistingId_whenCreateProduct_thenThrowException() {
        // given
        Product product = new Product();
        product.setId(1);
        product.setTitle("title");
        product.setActive(true);
        when(productDao.getProductById(anyInt())).thenReturn(Optional.of(product));

        // when
        // then
        assertThatThrownBy(() -> {
            productService.createProduct(product);
        })
                .isInstanceOf(ProductIsAlreadyExistsException.class)
                .hasMessage("Product with id=1 is already exists");
        verify(productDao).getProductById(anyInt());
        verifyNoMoreInteractions(productDao);
    }

    @Test
    @DisplayName("Given product and existing id, when updateProduct, then get updated product")
    public void givenProductAndExistingId_whenUpdateProduct_thenGetUpdatedProduct() {
        // given
        int productId = 1;
        Product productToUpdate = new Product();
        productToUpdate.setTitle("title");
        productToUpdate.setActive(true);
        Product foundedProduct = new Product();
        foundedProduct.setId(productId);
        Product updatedProduct = new Product();
        updatedProduct.setId(productId);
        updatedProduct.setTitle("title");
        updatedProduct.setActive(true);
        when(productDao.getProductById(anyInt())).thenReturn(Optional.of(foundedProduct));
        when(productDao.saveProduct(any(Product.class))).thenReturn(updatedProduct);

        // when
        Product updatedProductFromService = productService.updateProduct(productId, productToUpdate);

        // then
        assertThat(updatedProductFromService).isEqualTo(updatedProduct);
        verify(productDao).getProductById(anyInt());
        verify(productDao).saveProduct(any(Product.class));
        verifyNoMoreInteractions(productDao);
    }

    @Test
    @DisplayName("Given product and non existing id, when updateProduct, then throw exception")
    public void givenProductAndNonExistingId_whenUpdateProduct_thenThrowException() {
        // given
        int productId = 1;
        Product productToUpdate = new Product();
        productToUpdate.setTitle("title");
        productToUpdate.setActive(true);
        when(productDao.getProductById(anyInt())).thenReturn(Optional.empty());

        // when
        // then
        assertThatThrownBy(() -> {
            productService.updateProduct(productId, productToUpdate);
        })
                .isInstanceOf(ProductNotFoundException.class)
                .hasMessage("Product with id=1 not found");
        verify(productDao).getProductById(anyInt());
        verifyNoMoreInteractions(productDao);
    }

    @Test
    @DisplayName("Given product id, when softDeleteProduct, then delete product")
    public void givenProductId_whenSoftDeleteProduct_thenDeleteProduct() {
        // given
        int productId = 1;

        // when
        productService.softDeleteProductById(productId);

        // then
        verify(productDao).softDeleteProduct(anyInt());
        verifyNoMoreInteractions(productDao);
    }

    @Test
    @DisplayName("Given product id, when hardDeleteProduct, then delete product")
    public void givenProductId_whenHardDeleteProduct_thenDeleteProduct() {
        // given
        int productId = 1;

        // when
        productService.hardDeleteProductById(productId);

        // then
        verify(productDao).hardDeleteProduct(anyInt());
        verifyNoMoreInteractions(productDao);
    }

}