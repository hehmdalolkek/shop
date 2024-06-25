package ru.hehmdalolkek.shop.web.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.hamcrest.CoreMatchers;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.RequestBuilder;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.result.MockMvcResultHandlers;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import ru.hehmdalolkek.shop.web.dto.ProductDto;

import static java.lang.String.format;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.httpBasic;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;


@ActiveProfiles("test")
@AutoConfigureMockMvc
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Testcontainers
@Sql(value = "/db/data/create-data.sql", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
@Sql(value = "/db/data/delete-data.sql", executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
public class ProductControllerIntegrationTest {

    @Container
    @ServiceConnection
    private final static PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>("postgres:15");

    @Autowired
    MockMvc mockMvc;

    @Autowired
    ObjectMapper objectMapper;

    private static String SECURITY_USERNAME;

    private static String SECURITY_PASSWORD;

    @BeforeAll
    public static void beforeAll(@Value("${security.username}") String username,
                                 @Value("${security.password}") String password) {
        SECURITY_USERNAME = username;
        SECURITY_PASSWORD = password;
    }

    @Test
    @DisplayName("Test get all active products functionality")
    public void givenRequest_whenGetAllActiveProducts_thenSuccessResponse() throws Exception {
        // given
        RequestBuilder request = get("/api/v1/products")
                .with(httpBasic(SECURITY_USERNAME, SECURITY_PASSWORD));

        // when
        ResultActions result = this.mockMvc.perform(request);

        // then
        result
                .andDo(MockMvcResultHandlers.print())
                .andExpectAll(
                        MockMvcResultMatchers.status().isOk(),
                        MockMvcResultMatchers.content().contentType(MediaType.APPLICATION_JSON),
                        MockMvcResultMatchers.content().json("""
                                [
                                    {
                                        "productId": 1,
                                        "title": "Apple",
                                        "price": 15.99,
                                        "active": true
                                    },
                                    {
                                        "productId": 2,
                                        "title": "Banana",
                                        "price": 30.0,
                                        "active": true
                                    },
                                    {
                                        "productId": 3,
                                        "title": "Kiwi",
                                        "price": 99.23,
                                        "active": true
                                    },
                                    {
                                        "productId": 4,
                                        "title": "Orange",
                                        "price": 45.22,
                                        "active": true
                                    }
                                ]
                                """)
                );
    }

    @Test
    @DisplayName("Test get exists product by id functionality")
    public void givenRequest_whenGetProductById_thenSuccessResponse() throws Exception {
        // given
        int productId = 1;
        RequestBuilder request = get("/api/v1/products/" + productId)
                .with(httpBasic(SECURITY_USERNAME, SECURITY_PASSWORD));

        // when
        ResultActions result = this.mockMvc.perform(request);

        // then
        result
                .andDo(MockMvcResultHandlers.print())
                .andExpectAll(
                        MockMvcResultMatchers.status().isOk(),
                        MockMvcResultMatchers.content().contentType(MediaType.APPLICATION_JSON),
                        MockMvcResultMatchers.content().json("""
                                {
                                    "productId": 1,
                                    "title": "Apple",
                                    "price": 15.99,
                                    "active": true
                                }
                                """)
                );
    }

    @Test
    @DisplayName("Test get non exists product by id functionality")
    public void givenRequest_whenGetProductById_thenNotFoundResponse() throws Exception {
        // given
        int productId = 1000;
        RequestBuilder request = get("/api/v1/products/" + productId)
                .with(httpBasic(SECURITY_USERNAME, SECURITY_PASSWORD));

        // when
        ResultActions result = this.mockMvc.perform(request);

        // then
        result
                .andDo(MockMvcResultHandlers.print())
                .andExpectAll(
                        MockMvcResultMatchers.status().isNotFound(),
                        MockMvcResultMatchers.content().contentType(MediaType.APPLICATION_JSON),
                        MockMvcResultMatchers.jsonPath("$.status", CoreMatchers.is(404)),
                        MockMvcResultMatchers.jsonPath("$.detail",
                                CoreMatchers.is(format("Product with id=%d not found", productId)))
                );
    }

    @Test
    @DisplayName("Test create product functionality")
    public void givenRequest_whenCreateProduct_thenCreatedResponse() throws Exception {
        // given
        ProductDto productDto = ProductDto.builder()
                .title("Title")
                .price(100.99)
                .build();
        RequestBuilder request = post("/api/v1/products")
                .with(httpBasic(SECURITY_USERNAME, SECURITY_PASSWORD))
                .contentType(MediaType.APPLICATION_JSON)
                .content(this.objectMapper.writeValueAsString(productDto));

        // when
        ResultActions result = this.mockMvc.perform(request);

        // then
        result
                .andDo(MockMvcResultHandlers.print())
                .andExpectAll(
                        MockMvcResultMatchers.status().isCreated(),
                        MockMvcResultMatchers.content().contentType(MediaType.APPLICATION_JSON),
                        MockMvcResultMatchers.jsonPath("$.productId", CoreMatchers.notNullValue()),
                        MockMvcResultMatchers.jsonPath("$.title", CoreMatchers.is("Title")),
                        MockMvcResultMatchers.jsonPath("$.price", CoreMatchers.is(100.99)),
                        MockMvcResultMatchers.jsonPath("$.active", CoreMatchers.is(true))
                );
    }

    @Test
    @DisplayName("Test create product with invalid data functionality")
    public void givenRequest_whenCreateProduct_thenBadRequestResponse() throws Exception {
        // given
        ProductDto productDto = ProductDto.builder()
                .title("")
                .price(null)
                .build();
        RequestBuilder request = post("/api/v1/products")
                .with(httpBasic(SECURITY_USERNAME, SECURITY_PASSWORD))
                .contentType(MediaType.APPLICATION_JSON)
                .content(this.objectMapper.writeValueAsString(productDto));

        // when
        ResultActions result = this.mockMvc.perform(request);

        // then
        result
                .andDo(MockMvcResultHandlers.print())
                .andExpectAll(
                        MockMvcResultMatchers.status().isBadRequest(),
                        MockMvcResultMatchers.content().contentType(MediaType.APPLICATION_JSON),
                        MockMvcResultMatchers.jsonPath("$.status", CoreMatchers.is(400)),
                        MockMvcResultMatchers.jsonPath("$.errors.price",
                                CoreMatchers.is("Price must not be null")),
                        MockMvcResultMatchers.jsonPath("$.errors.title",
                                CoreMatchers.is("The size must be greater than 1 and less than 256 characters"))
                );
    }

    @Test
    @DisplayName("Test create product with duplicate id functionality")
    public void givenRequest_whenCreateProductWithDuplicateId_thenConflictResponse() throws Exception {
        // given
        int productId = 1;
        ProductDto productDto = ProductDto.builder()
                .productId(productId)
                .title("Title")
                .price(99.0)
                .build();
        RequestBuilder request = post("/api/v1/products")
                .with(httpBasic(SECURITY_USERNAME, SECURITY_PASSWORD))
                .contentType(MediaType.APPLICATION_JSON)
                .content(this.objectMapper.writeValueAsString(productDto));

        // when
        ResultActions result = this.mockMvc.perform(request);

        // then
        result
                .andDo(MockMvcResultHandlers.print())
                .andExpectAll(
                        MockMvcResultMatchers.status().isConflict(),
                        MockMvcResultMatchers.content().contentType(MediaType.APPLICATION_JSON),
                        MockMvcResultMatchers.jsonPath("$.status", CoreMatchers.is(409)),
                        MockMvcResultMatchers.jsonPath("$.detail", CoreMatchers.is(
                                format("Product with id=%d is already exists", productId)))
                );
    }

    @Test
    @DisplayName("Test create product with duplicate title functionality")
    public void givenRequest_whenCreateProductWithDuplicateTitle_thenConflictResponse() throws Exception {
        // given
        String title = "Apple";
        ProductDto productDto = ProductDto.builder()
                .title(title)
                .price(99.0)
                .build();
        RequestBuilder request = post("/api/v1/products")
                .with(httpBasic(SECURITY_USERNAME, SECURITY_PASSWORD))
                .contentType(MediaType.APPLICATION_JSON)
                .content(this.objectMapper.writeValueAsString(productDto));

        // when
        ResultActions result = this.mockMvc.perform(request);

        // then
        result
                .andDo(MockMvcResultHandlers.print())
                .andExpectAll(
                        MockMvcResultMatchers.status().isConflict(),
                        MockMvcResultMatchers.content().contentType(MediaType.APPLICATION_JSON),
                        MockMvcResultMatchers.jsonPath("$.status", CoreMatchers.is(409)),
                        MockMvcResultMatchers.jsonPath("$.detail", CoreMatchers.is(
                                format("Product with title=%s is already exists", title)))
                );
    }

    @Test
    @DisplayName("Test update product functionality")
    public void givenRequest_whenUpdateProduct_thenSuccessResponse() throws Exception {
        // given
        int productId = 1;
        ProductDto productDto = ProductDto.builder()
                .title("Title")
                .price(100.99)
                .active(true)
                .build();
        RequestBuilder request = put("/api/v1/products/" + productId)
                .with(httpBasic(SECURITY_USERNAME, SECURITY_PASSWORD))
                .contentType(MediaType.APPLICATION_JSON)
                .content(this.objectMapper.writeValueAsString(productDto));

        // when
        ResultActions result = this.mockMvc.perform(request);

        // then
        result
                .andDo(MockMvcResultHandlers.print())
                .andExpectAll(
                        MockMvcResultMatchers.status().isOk(),
                        MockMvcResultMatchers.content().contentType(MediaType.APPLICATION_JSON),
                        MockMvcResultMatchers.jsonPath("$.productId", CoreMatchers.is(productId)),
                        MockMvcResultMatchers.jsonPath("$.title", CoreMatchers.is("Title")),
                        MockMvcResultMatchers.jsonPath("$.price", CoreMatchers.is(100.99)),
                        MockMvcResultMatchers.jsonPath("$.active", CoreMatchers.is(true))
                );
    }

    @Test
    @DisplayName("Test update product by non exists id functionality")
    public void givenRequest_whenUpdateProductByNonExistsId_thenNotFoundResponse() throws Exception {
        // given
        int productId = 1000;
        ProductDto productDto = ProductDto.builder()
                .title("Title")
                .price(100.99)
                .active(true)
                .build();
        RequestBuilder request = put("/api/v1/products/" + productId)
                .with(httpBasic(SECURITY_USERNAME, SECURITY_PASSWORD))
                .contentType(MediaType.APPLICATION_JSON)
                .content(this.objectMapper.writeValueAsString(productDto));

        // when
        ResultActions result = this.mockMvc.perform(request);

        // then
        result
                .andDo(MockMvcResultHandlers.print())
                .andExpectAll(
                        MockMvcResultMatchers.status().isNotFound(),
                        MockMvcResultMatchers.content().contentType(MediaType.APPLICATION_JSON),
                        MockMvcResultMatchers.content().contentType(MediaType.APPLICATION_JSON),
                        MockMvcResultMatchers.jsonPath("$.status", CoreMatchers.is(404)),
                        MockMvcResultMatchers.jsonPath("$.detail",
                                CoreMatchers.is(format("Product with id=%d not found", productId)))
                );
    }

    @Test
    @DisplayName("Test update product with invalid data functionality")
    public void givenRequest_whenUpdateProduct_thenBadRequestResponse() throws Exception {
        // given
        int productId = 1;
        ProductDto productDto = ProductDto.builder()
                .title("")
                .price(null)
                .build();
        RequestBuilder request = put("/api/v1/products/" + productId)
                .with(httpBasic(SECURITY_USERNAME, SECURITY_PASSWORD))
                .contentType(MediaType.APPLICATION_JSON)
                .content(this.objectMapper.writeValueAsString(productDto));

        // when
        ResultActions result = this.mockMvc.perform(request);

        // then
        result
                .andDo(MockMvcResultHandlers.print())
                .andExpectAll(
                        MockMvcResultMatchers.status().isBadRequest(),
                        MockMvcResultMatchers.content().contentType(MediaType.APPLICATION_JSON),
                        MockMvcResultMatchers.jsonPath("$.status", CoreMatchers.is(400)),
                        MockMvcResultMatchers.jsonPath("$.errors.price",
                                CoreMatchers.is("Price must not be null")),
                        MockMvcResultMatchers.jsonPath("$.errors.title",
                                CoreMatchers.is("The size must be greater than 1 and less than 256 characters"))
                );
    }

    @Test
    @DisplayName("Test update product with duplicate title functionality")
    public void givenRequest_whenUpdateProduct_thenConflictResponse() throws Exception {
        // given
        String title = "Apple";
        int productId = 2;
        ProductDto productDto = ProductDto.builder()
                .title(title)
                .price(99.0)
                .build();
        RequestBuilder request = put("/api/v1/products/" + productId)
                .with(httpBasic(SECURITY_USERNAME, SECURITY_PASSWORD))
                .contentType(MediaType.APPLICATION_JSON)
                .content(this.objectMapper.writeValueAsString(productDto));

        // when
        ResultActions result = this.mockMvc.perform(request);

        // then
        result
                .andDo(MockMvcResultHandlers.print())
                .andExpectAll(
                        MockMvcResultMatchers.status().isConflict(),
                        MockMvcResultMatchers.content().contentType(MediaType.APPLICATION_JSON),
                        MockMvcResultMatchers.jsonPath("$.status", CoreMatchers.is(409)),
                        MockMvcResultMatchers.jsonPath("$.detail", CoreMatchers.is(
                                format("Product with title=%s is already exists", title)))
                );
    }

    @Test
    @DisplayName("Test soft delete product functionality")
    public void givenRequest_whenSoftDeleteProduct_thenSuccessResponse() throws Exception {
        // given
        int productId = 1;
        RequestBuilder request = delete("/api/v1/products/" + productId)
                .with(httpBasic(SECURITY_USERNAME, SECURITY_PASSWORD));

        // when
        ResultActions result = this.mockMvc.perform(request);

        // then
        result
                .andDo(MockMvcResultHandlers.print())
                .andExpect(MockMvcResultMatchers.status().isOk());
    }

    @Test
    @DisplayName("Test hard delete product functionality")
    public void givenRequest_whenHardDeleteProduct_thenSuccessResponse() throws Exception {
        // given
        int productId = 1;
        RequestBuilder request = delete("/api/v1/products/" + productId)
                .with(httpBasic(SECURITY_USERNAME, SECURITY_PASSWORD))
                .queryParam("isHard", "true");

        // when
        ResultActions result = this.mockMvc.perform(request);

        // then
        result
                .andDo(MockMvcResultHandlers.print())
                .andExpect(MockMvcResultMatchers.status().isOk());
    }

}
