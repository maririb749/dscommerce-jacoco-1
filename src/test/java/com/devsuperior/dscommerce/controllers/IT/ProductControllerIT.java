package com.devsuperior.dscommerce.controllers.IT;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.result.MockMvcResultHandlers;
import org.springframework.transaction.annotation.Transactional;

import com.devsuperior.dscommerce.dto.ProductDTO;
import com.devsuperior.dscommerce.entities.Category;
import com.devsuperior.dscommerce.entities.Product;
import com.devsuperior.dscommerce.tests.TokenUtil;
import com.fasterxml.jackson.databind.ObjectMapper;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
public class ProductControllerIT {
	
	@Autowired
	private MockMvc mockMvc;
	
	@Autowired
	private TokenUtil tokenUtil;
	
	@Autowired
	private ObjectMapper objectMapper;
	
	private String  adminToken;
	
	private String adminUsername, adminPassword;
	
	private String productName;
	
	private Long existingId,nonExistingId;
	
	private ProductDTO productDTO;
	private Product product;
	
	 @BeforeEach
	    public void setUp() throws Exception {
			 
		 existingId = 2L;
		 nonExistingId = 100L;
		 
	     productName = "MacBook"; 
	     
	    adminUsername = "alex@gmail.com";
	 	adminPassword = "123456";
	     
	     adminToken = tokenUtil.obtainAccessToken(mockMvc, adminUsername, adminPassword);
	     
	     Category category = new Category(2L, null);
			product = new Product(null, "Console PlayStation 5", "Lorem ipsum dolor sit amet, consectetur adipiscing elit", 3999.90, "https://raw.githubusercontent.com/devsuperior/dscatalog-resources/master/backend/img/1-big.jpg");
			product.getCategories().add(category);
			productDTO = new ProductDTO(product);
	    
	    }
	 
	 @Test
		public void findAllShouldReturnPageWhenNameParamIsNotEmpty() throws Exception {
			ResultActions result = mockMvc
					.perform(get("/products?name={productName}", productName).accept(MediaType.APPLICATION_JSON));

			result.andExpect(status().isOk());
			result.andExpect(jsonPath("$.content[0].id").value(3L));
			result.andExpect(jsonPath("$.content[0].name").value("Macbook Pro"));
			result.andExpect(jsonPath("$.content[0].price").value(1250.0));
			result.andExpect(jsonPath("$.content[0].imgUrl").value(
					"https://raw.githubusercontent.com/devsuperior/dscatalog-resources/master/backend/img/3-big.jpg"));
		}
	 
	 @Test
		public void findAllShouldReturnPageWhenNameParamIsEmpty() throws Exception {
			ResultActions result = mockMvc.perform(get("/products").accept(MediaType.APPLICATION_JSON));

			result.andExpect(status().isOk());
			result.andExpect(jsonPath("$.content[0].id").value(1L));
			result.andExpect(jsonPath("$.content[0].name").value("The Lord of the Rings"));
			result.andExpect(jsonPath("$.content[0].price").value(90.5));
			result.andExpect(jsonPath("$.content[0].imgUrl").value(
					"https://raw.githubusercontent.com/devsuperior/dscatalog-resources/master/backend/img/1-big.jpg"));
		}
		@Test
		public void findByIdShouldReturnProductDTOWhenIdExists() throws Exception {
			
			ResultActions result = 
					mockMvc.perform(get("/products/{id}", existingId)
						.accept(MediaType.APPLICATION_JSON));
			
			result.andExpect(status().isOk());
			result.andExpect(jsonPath("$.id").value(2L));
			result.andExpect(jsonPath("$.name").value("Smart TV"));
			result.andExpect(jsonPath("$.description").value("Lorem ipsum dolor sit amet, consectetur adipiscing elit, sed do eiusmod tempor incididunt ut labore et dolore magna aliqua. Ut enim ad minim veniam, quis nostrud exercitation ullamco laboris nisi ut aliquip ex ea commodo consequat. Duis aute irure dolor in reprehenderit in voluptate velit esse cillum dolore eu fugiat nulla pariatur. Excepteur sint occaecat cupidatat non proident, sunt in culpa qui officia deserunt mollit anim id est laborum."));
			result.andExpect(jsonPath("$.price").value(2190.0));
			result.andExpect(jsonPath("$.categories").exists());
		}
		
		@Test
		public void findByIdShouldReturnNotFoundWhenIdDoesNotExist() throws Exception {
			
			ResultActions result = 
					mockMvc.perform(get("/products/{id}", nonExistingId)
						.accept(MediaType.APPLICATION_JSON));
			
			result.andExpect(status().isNotFound());
		}
		
		@Test
		public void insertShouldReturnProductDTOCreatedWhenLoggedAsAdmin() throws Exception {
			
			String jsonBody = objectMapper.writeValueAsString(productDTO);
			
			ResultActions result = 
					mockMvc.perform(post("/products")
						.header("Authorization", "Bearer " + adminToken)
						.content(jsonBody)
						.contentType(MediaType.APPLICATION_JSON)
						.accept(MediaType.APPLICATION_JSON))
						.andDo(MockMvcResultHandlers.print());
			
			result.andExpect(status().isCreated());
			result.andExpect(jsonPath("$.id").value(26L));
			result.andExpect(jsonPath("$.name").value("Console PlayStation 5"));
			result.andExpect(jsonPath("$.description").value("Lorem ipsum dolor sit amet, consectetur adipiscing elit"));
			result.andExpect(jsonPath("$.price").value(3999.90));
			result.andExpect(jsonPath("$.imgUrl").value("https://raw.githubusercontent.com/devsuperior/dscatalog-resources/master/backend/img/1-big.jpg"));
			result.andExpect(jsonPath("$.categories[0].id").value(2L));
		}
		
		@Test
		public void insertShouldReturnUnprocessableEntityWhenAdminLoggedAndInvalidName() throws Exception {
			
			product.setName("ab");
			productDTO = new ProductDTO(product);
			
			String jsonBody = objectMapper.writeValueAsString(productDTO);
			
			ResultActions result = 
					mockMvc.perform(post("/products")
						.header("Authorization", "Bearer " + adminToken)
						.content(jsonBody)
						.contentType(MediaType.APPLICATION_JSON)
						.accept(MediaType.APPLICATION_JSON));
			
			result.andExpect(status().isUnprocessableEntity());
		}
	

}
