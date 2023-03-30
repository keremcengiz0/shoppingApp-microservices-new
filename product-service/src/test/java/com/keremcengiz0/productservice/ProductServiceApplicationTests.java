package com.keremcengiz0.productservice;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.keremcengiz0.productservice.dto.ProductCreateRequest;
import com.keremcengiz0.productservice.dto.ProductResponse;
import com.keremcengiz0.productservice.model.Product;
import com.keremcengiz0.productservice.repository.ProductRepository;
import com.keremcengiz0.productservice.service.ProductService;
import org.jetbrains.annotations.NotNull;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.testcontainers.containers.MongoDBContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@Testcontainers
@AutoConfigureMockMvc
class ProductServiceApplicationTests {

	@Container
	static MongoDBContainer mongoDBContainer = new MongoDBContainer("mongo:4.4.2");

	@Autowired
	private MockMvc mockMvc;

	@Autowired
	private ObjectMapper objectMapper;

	@Autowired
	private ProductRepository productRepository;

	@Autowired
	private ModelMapper modelMapper;

	@Autowired
	private ProductService productService;

	@DynamicPropertySource
	static void setProperties(@NotNull DynamicPropertyRegistry dynamicPropertyRegistry) {
		dynamicPropertyRegistry.add("spring.data.mongodb.uri", mongoDBContainer::getReplicaSetUrl);
	}

	@BeforeEach
	void setUp() {
		productRepository.deleteAll();
	}

	@Test
	void shouldCreateProduct() throws Exception {
		ProductCreateRequest productCreateRequest = getProductCreateRequest();
		String productCreateRequestString = objectMapper.writeValueAsString(productCreateRequest);

		mockMvc.perform(MockMvcRequestBuilders.post("/api/product")
				.contentType(MediaType.APPLICATION_JSON)
				.content(productCreateRequestString))
				.andExpect(status().isCreated());

		Assertions.assertEquals(1, productRepository.findAll().size());
	}

	private ProductCreateRequest getProductCreateRequest() {
		return ProductCreateRequest.builder()
				.name("iphone 13")
				.description("iphone 13")
				.price(BigDecimal.valueOf(1200))
				.build();
	}

	@Test
	void shouldGetAllProducts() throws Exception {
		Product product1 = new Product();
		product1.setName("iphone 13");
		product1.setDescription("iphone 13");
		product1.setPrice(BigDecimal.valueOf(1200));
		productRepository.save(product1);

		Product product2 = new Product();
		product2.setName("Samsung Galaxy S21");
		product2.setDescription("Samsung Galaxy S21");
		product2.setPrice(BigDecimal.valueOf(1000));
		productRepository.save(product2);

		List<ProductResponse> expectedProducts = Arrays.asList(
				modelMapper.map(product1, ProductResponse.class),
				modelMapper.map(product2, ProductResponse.class));

		String responseString = mockMvc.perform(MockMvcRequestBuilders.get("/api/product")
						.contentType(MediaType.APPLICATION_JSON))
				.andExpect(status().isOk())
				.andReturn().getResponse().getContentAsString();

		List<ProductResponse> actualProducts = objectMapper.readValue(responseString, new TypeReference<List<ProductResponse>>() {});

		Assertions.assertEquals(expectedProducts.size(), actualProducts.size());

		for (int i = 0; i < expectedProducts.size(); i++) {
			Assertions.assertEquals(expectedProducts.get(i), actualProducts.get(i));
		}
	}

}
