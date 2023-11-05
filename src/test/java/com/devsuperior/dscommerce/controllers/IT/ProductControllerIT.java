package com.devsuperior.dscommerce.controllers.IT;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
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
import org.springframework.transaction.annotation.Transactional;

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
	
	private String productName;
	
	private Long existingId;
	
	 @BeforeEach
	    public void setUp() throws Exception {
		 
		 existingId = 2L;
		 
		 
	    productName = "MacBook"; 
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

}
