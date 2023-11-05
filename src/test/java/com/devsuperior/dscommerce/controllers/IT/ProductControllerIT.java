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
	
	 @BeforeEach
	    public void setUp() throws Exception {
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

}
