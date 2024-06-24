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
import ru.hehmdalolkek.shop.web.dto.ProductDto;

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
        List<ProductDto> allActiveProducts = productService.getAllActiveProducts();

        // then
        assertThat(allActiveProducts).isNotEmpty();
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
        ProductDto givenProduct = productService.getProductById(1);

        // then
        assertThat(givenProduct).isNotNull();
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
        ProductDto productDto = ProductDto.builder()
                .productId(1)
                .title("title")
                .active(true)
                .build();
        when(productDao.getProductById(anyInt())).thenReturn(Optional.empty());
        when(productDao.saveProduct(any(Product.class))).thenReturn(product);

        // when
        ProductDto savedProduct = productService.createProduct(productDto);

        // then
        assertThat(savedProduct).isNotNull();
        verify(productDao).getProductById(anyInt());
        verify(productDao).productExistsByTitle(anyString());
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
        ProductDto productDto = ProductDto.builder()
                .productId(1)
                .title("title")
                .active(true)
                .build();
        when(productDao.getProductById(anyInt())).thenReturn(Optional.of(product));

        // when
        // then
        assertThatThrownBy(() -> {
            productService.createProduct(productDto);
        })
                .isInstanceOf(ProductIsAlreadyExistsException.class)
                .hasMessage("Product with id=1 is already exists");
        verify(productDao).getProductById(anyInt());
        verifyNoMoreInteractions(productDao);
    }

    @Test
    @DisplayName("Given product with non existing title, when createProduct, then get saved product")
    public void givenProductWithNonExistingTitle_whenCreateProduct_thenGetSavedProduct() {
        // given
        Product product = new Product();
        product.setId(1);
        product.setTitle("1234567");
        product.setActive(true);
        ProductDto productDto = ProductDto.builder()
                .title("1234567")
                .active(true)
                .build();
        when(productDao.productExistsByTitle(anyString())).thenReturn(false);
        when(productDao.saveProduct(any(Product.class))).thenReturn(product);

        // when
        ProductDto savedProduct = productService.createProduct(productDto);

        // then
        assertThat(savedProduct).isNotNull();
        verify(productDao).productExistsByTitle(anyString());
        verify(productDao).saveProduct(any(Product.class));
        verifyNoMoreInteractions(productDao);
    }

    @Test
    @DisplayName("Given product with existing title, when createProduct, then throw exception")
    public void givenProductWithExistingTitle_whenCreateProduct_thenThrowException() {
        // given
        Product product = new Product();
        product.setId(1);
        product.setTitle("1234567");
        product.setActive(true);
        ProductDto productDto = ProductDto.builder()
                .title("1234567")
                .active(true)
                .build();
        when(productDao.productExistsByTitle(anyString())).thenReturn(true);

        // when
        // then
        assertThatThrownBy(() -> {
            productService.createProduct(productDto);
        })
                .isInstanceOf(ProductIsAlreadyExistsException.class)
                .hasMessage("Product with title=1234567 is already exists");
        verify(productDao).productExistsByTitle(anyString());
        verifyNoMoreInteractions(productDao);
    }

    @Test
    @DisplayName("Given product and existing id, when updateProduct, then get updated product")
    public void givenProductAndExistingId_whenUpdateProduct_thenGetUpdatedProduct() {
        // given
        int productId = 1;
        Product foundedProduct = new Product();
        foundedProduct.setId(productId);
        Product updatedProduct = new Product();
        updatedProduct.setId(productId);
        updatedProduct.setTitle("title");
        updatedProduct.setActive(true);
        ProductDto productToUpdate = ProductDto.builder()
                .productId(1)
                .title("title")
                .active(true)
                .build();
        when(productDao.getProductById(anyInt())).thenReturn(Optional.of(foundedProduct));
        when(productDao.saveProduct(any(Product.class))).thenReturn(updatedProduct);

        // when
        ProductDto updatedProductFromService = productService.updateProduct(productId, productToUpdate);

        // then
        assertThat(updatedProductFromService).isNotNull();
        verify(productDao).getProductById(anyInt());
        verify(productDao).productExistsByTitle(anyString());
        verify(productDao).saveProduct(any(Product.class));
        verifyNoMoreInteractions(productDao);
    }

    @Test
    @DisplayName("Given product and non existing id, when updateProduct, then throw exception")
    public void givenProductAndNonExistingId_whenUpdateProduct_thenThrowException() {
        // given
        int productId = 1;
        ProductDto productToUpdate = ProductDto.builder()
                .title("title")
                .active(true)
                .build();
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
    @DisplayName("Given product with non existing title, when updateProduct, then get saved product")
    public void givenProductWithNonExistingTitle_whenUpdateProduct_thenGetSavedProduct() {
        // given
        int productId = 1;
        Product product = new Product();
        product.setId(productId);
        product.setTitle("1234567");
        product.setActive(true);
        Product foundedProduct = new Product();
        foundedProduct.setId(productId);
        ProductDto productDto = ProductDto.builder()
                .productId(productId)
                .title("1234567")
                .active(true)
                .build();
        when(productDao.getProductById(anyInt())).thenReturn(Optional.of(foundedProduct));
        when(productDao.productExistsByTitle(anyString())).thenReturn(false);
        when(productDao.saveProduct(any(Product.class))).thenReturn(product);

        // when
        ProductDto updateProduct = productService.updateProduct(productId, productDto);

        // then
        assertThat(updateProduct).isNotNull();
        verify(productDao).getProductById(anyInt());
        verify(productDao).productExistsByTitle(anyString());
        verify(productDao).saveProduct(any(Product.class));
        verifyNoMoreInteractions(productDao);
    }

    @Test
    @DisplayName("Given product with existing title, when updateProduct, then throws exception")
    public void givenProductWithExistingTitle_whenUpdateProduct_thenThrowsException() {
        // given
        int productId = 1;
        Product product = new Product();
        product.setId(productId);
        product.setTitle("1234567");
        product.setActive(true);
        Product foundedProduct = new Product();
        foundedProduct.setId(productId);
        ProductDto productDto = ProductDto.builder()
                .productId(productId)
                .title("1234567")
                .active(true)
                .build();
        when(productDao.getProductById(anyInt())).thenReturn(Optional.of(foundedProduct));
        when(productDao.productExistsByTitle(anyString())).thenReturn(true);

        // when
        // then
        assertThatThrownBy(() -> {
            productService.updateProduct(productId, productDto);
        })
                .isInstanceOf(ProductIsAlreadyExistsException.class)
                .hasMessage("Product with title=1234567 is already exists");
        verify(productDao).getProductById(anyInt());
        verify(productDao).productExistsByTitle(anyString());
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