package ru.hehmdalolkek.shop.dao;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.springframework.test.context.jdbc.Sql;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import ru.hehmdalolkek.shop.dao.interfaces.ProductDao;
import ru.hehmdalolkek.shop.model.Product;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@Testcontainers
@TestConfiguration(proxyBeanMethods = false)
@SpringBootTest
@Sql(value = "/db/data/create-data.sql", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
@Sql(value = "/db/data/delete-data.sql", executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
class ProductDaoTest {

    @Container
    @ServiceConnection
    private static final PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>("postgres:15");

    private final ProductDao productDao;

    @Autowired
    ProductDaoTest(ProductDao productDao) {
        this.productDao = productDao;
    }

    @Test
    @DisplayName("When getAllActiveProducts, then returned list of active products")
    void whenGetAllActiveProducts_thenReturnActiveProducts() {
        // when
        List<Product> products = productDao.getAllActiveProducts();

        // then
        assertThat(products).isNotEmpty();
        assertThat(products).size().isEqualTo(4);
        assertThat(products).allMatch(Product::isActive);
    }

    @Test
    @DisplayName("Given existing id, when getProductById, then returned product with the passed id")
    void givenExistingProductId_whenGetProductById_thenReturnProductWithThePassedId() {
        // given
        int productId = 1;

        // when
        Optional<Product> optionalProduct = productDao.getProductById(productId);

        // then
        assertThat(optionalProduct).isPresent();
        assertThat(optionalProduct.get().getId()).isEqualTo(productId);
    }

    @Test
    @DisplayName("Given non-existing id, when getProductById, then returned empty optional")
    void givenNonExistingProductId_whenGetProductById_thenReturnEmptyOptional() {
        // given
        int productId = -1;

        // when
        Optional<Product> optionalProduct = productDao.getProductById(productId);

        // then
        assertThat(optionalProduct).isNotPresent();
    }

    @Test
    @DisplayName("Given product without id, when saveProduct, then returned saved product")
    void givenProductWithoutId_whenSaveProduct_thenReturnSavedProduct() {
        // given
        Product product = new Product();
        product.setTitle("Title");
        product.setPrice(1.0);

        // when
        Product createdProduct = productDao.saveProduct(product);

        // then
        assertThat(createdProduct).isNotNull();
        assertThat(createdProduct.getId()).isNotNull();
        assertThat(createdProduct.isActive()).isTrue();
        assertThat(createdProduct.getTitle()).isEqualTo(product.getTitle());
        assertThat(createdProduct.getPrice()).isEqualTo(product.getPrice());
    }

    @Test
    @DisplayName("Given product with existing id, when saveProduct, then returned saved product")
    void givenProductWithExistingId_whenSaveProduct_thenReturnedSavedProduct() {
        // given
        Product product = new Product();
        product.setId(1);
        product.setTitle("Title");
        product.setPrice(1.0);
        product.setActive(false);

        // when
        Product updatedProduct = productDao.saveProduct(product);

        // then
        assertThat(updatedProduct).isNotNull();
        assertThat(updatedProduct.getId()).isEqualTo(product.getId());
        assertThat(updatedProduct.getTitle()).isEqualTo(product.getTitle());
        assertThat(updatedProduct.getPrice()).isEqualTo(product.getPrice());
        assertThat(updatedProduct.isActive()).isEqualTo(product.isActive());
    }

    @Test
    @DisplayName("Given product with non existing id, when saveProduct, then returned saved product")
    void givenProductWithNonExistingId_whenSaveProduct_thenReturnedSavedProduct() {
        // given
        Product product = new Product();
        product.setId(100);
        product.setTitle("Title");
        product.setPrice(1.0);
        product.setActive(false);

        // when
        Product updatedProduct = productDao.saveProduct(product);

        // then
        assertThat(updatedProduct).isNotNull();
        assertThat(updatedProduct.getId()).isEqualTo(product.getId());
        assertThat(updatedProduct.getTitle()).isEqualTo(product.getTitle());
        assertThat(updatedProduct.getPrice()).isEqualTo(product.getPrice());
        assertThat(updatedProduct.isActive()).isEqualTo(product.isActive());
    }

    @Test
    @DisplayName("Given productId, when softDeleteProduct, then set active false in product")
    void givenProductId_whenHardDeleteProduct_thenSetActiveFalseInProduct() {
        // given
        int productId = 1;

        // when
        productDao.softDeleteProduct(productId);
        Optional<Product> product = productDao.getProductById(productId);

        // then
        assertThat(product).isPresent();
        assertThat(product.get().isActive()).isFalse();
    }

    @Test
    @DisplayName("Given productId, when hardDeleteProduct, then product deleted from table")
    void givenProductId_whenHardDeleteProduct_thenProductDeletedFromTable() {
        // given
        int productId = 1;

        // when
        productDao.hardDeleteProduct(productId);
        Optional<Product> product = productDao.getProductById(productId);

        // then
        assertThat(product).isNotPresent();
    }


}

