package com.devsuperior.dscommerce.controllers.IT;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.time.Instant;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.result.MockMvcResultHandlers;

import com.devsuperior.dscommerce.dto.OrderDTO;
import com.devsuperior.dscommerce.entities.Order;
import com.devsuperior.dscommerce.entities.OrderItem;
import com.devsuperior.dscommerce.entities.OrderStatus;
import com.devsuperior.dscommerce.entities.Product;
import com.devsuperior.dscommerce.entities.User;
import com.devsuperior.dscommerce.tests.ProductFactory;
import com.devsuperior.dscommerce.tests.TokenUtil;
import com.devsuperior.dscommerce.tests.UserFactory;
import com.fasterxml.jackson.databind.ObjectMapper;

@SpringBootTest
@AutoConfigureMockMvc
public class OrderControllerIT {

@Autowired
private MockMvc mockMvc;

@SuppressWarnings("unused")
@Autowired
private ObjectMapper objectMapper;

@Autowired
private TokenUtil tokenUtil;

private Long existingOrderId,nonExistingOrderId;
private String  adminUsername, adminPassword;
private String  clientUsername, clientPassword;

private String  adminToken, clientToken,invalidToken;


private Order order;
private OrderDTO orderDTO;
private User user;



@BeforeEach
void setUp() throws Exception {
   
    adminUsername = "alex@gmail.com";
    adminPassword = "123456";
    clientUsername = "maria@gmail.com";
    clientPassword = "123456";
    
    adminToken = tokenUtil.obtainAccessToken(mockMvc, adminUsername, adminPassword);
    clientToken = tokenUtil.obtainAccessToken(mockMvc, clientUsername, clientPassword);
	invalidToken = adminToken + "xpto";
    
    existingOrderId = 1L;
    nonExistingOrderId =100L;
    
    user = UserFactory.createClientUser();
    order = new Order(null, Instant.now(), OrderStatus.WAITING_PAYMENT, user, null);
    Product product = ProductFactory.createProduct();
	OrderItem orderItem = new OrderItem(order, product, 2, 10.0);
	order.getItems().add(orderItem);
	
    orderDTO = new OrderDTO(order);
    
}

@Test
public void findByIdShouldReturnOrderDTOWhenIdExistsAndAdminLogged() throws Exception {
    ResultActions result =  mockMvc
                    .perform(get("/orders/{id}", existingOrderId)
                    .header("Authorization", "Bearer " + adminToken)
                    .accept(MediaType.APPLICATION_JSON))
                    .andDo(MockMvcResultHandlers.print());

    result.andExpect(status().isOk());
    result.andExpect(jsonPath("$.id").value(1L));
    result.andExpect(jsonPath("$.moment").value("2022-07-25T13:00:00Z"));
    result.andExpect(jsonPath("$.status").value("PAID"));
    result.andExpect(jsonPath("$.client").exists());
    result.andExpect(jsonPath("$.client.name").value("Maria Brown"));
    result.andExpect(jsonPath("$.payment").exists());
    result.andExpect(jsonPath("$.items").exists());
    result.andExpect(jsonPath("$.items[1].name").value("Macbook Pro"));
    result.andExpect(jsonPath("$.total").exists());
 }



@Test
public void findByIdShouldReturnOrderDTOWhenIdExistsAndClientLogged() throws Exception {
	
	ResultActions result = 
			mockMvc.perform(get("/orders/{id}", existingOrderId)
				.header("Authorization", "Bearer " + clientToken)
				.accept(MediaType.APPLICATION_JSON))
	            .andDo(MockMvcResultHandlers.print());
	
	result.andExpect(status().isOk());
	result.andExpect(jsonPath("$.id").value(1L));
	result.andExpect(jsonPath("$.moment").value("2022-07-25T13:00:00Z"));
	result.andExpect(jsonPath("$.status").value("PAID"));
	result.andExpect(jsonPath("$.client").exists());
	result.andExpect(jsonPath("$.client.name").value("Maria Brown"));
	result.andExpect(jsonPath("$.payment").exists());
	result.andExpect(jsonPath("$.items").exists());
	result.andExpect(jsonPath("$.items[1].name").value("Macbook Pro"));
	result.andExpect(jsonPath("$.total").exists());
}

@Test
public void findByIdShouldReturnForbiddenWhenIdExistsAndClientLoggedAndOrderDoesNotBelongUser() throws Exception {
	
	Long otherOrderId = 2L;
	ResultActions result = 
			mockMvc.perform(get("/orders/{id}", otherOrderId)
				.header("Authorization", "Bearer " + clientToken)
				.accept(MediaType.APPLICATION_JSON))
	            .andDo(MockMvcResultHandlers.print());
	
	result.andExpect(status().isForbidden());
	
}
@Test
public void findByIdShouldReturnNotFoundWhenIdDoesNotExistsAndAdminLogged() throws Exception {
	
	ResultActions result = 
			mockMvc.perform(get("/orders/{id}", nonExistingOrderId)
				.header("Authorization", "Bearer " + adminToken)
				.accept(MediaType.APPLICATION_JSON))
	            .andDo(MockMvcResultHandlers.print());
	
	result.andExpect(status().isNotFound());
	
  }
@Test
public void findByIdShouldReturnNotFoundWhenIdDoesNotExistsAndClientLogged() throws Exception {
	
	ResultActions result = 
			mockMvc.perform(get("/orders/{id}", nonExistingOrderId)
				.header("Authorization", "Bearer " + clientToken)
				.accept(MediaType.APPLICATION_JSON))
	            .andDo(MockMvcResultHandlers.print());
	
	result.andExpect(status().isNotFound());
	
}

@Test
public void findByIdShouldReturnUnautorizedWhenIdExistsAndInvalidToken() throws Exception {
	
	ResultActions result = 
			mockMvc.perform(get("/orders/{id}", existingOrderId)
				.header("Authorization", "Bearer " +invalidToken)
				.accept(MediaType.APPLICATION_JSON))
	            .andDo(MockMvcResultHandlers.print());
	
	result.andExpect(status().isUnauthorized());
	
}
@Test
public void insertShouldReturnUnprocessableEntityWhenClientLoggedAndOrderHasNoItem() throws Exception {
	
	orderDTO.getItems().clear();

	String jsonBody = objectMapper.writeValueAsString(orderDTO);
	
	ResultActions result = 
			mockMvc.perform(post("/orders")
				.header("Authorization", "Bearer " + clientToken)
				.content(jsonBody)
				.contentType(MediaType.APPLICATION_JSON)
				.accept(MediaType.APPLICATION_JSON))
				.andDo(MockMvcResultHandlers.print());
	
	result.andExpect(status().isUnprocessableEntity());
}

@Test
public void insertShouldReturnOrderDTOCreatedWhenClientLogged() throws Exception {

	String jsonBody = objectMapper.writeValueAsString(orderDTO);
	
	ResultActions result = 
			mockMvc.perform(post("/orders")
				.header("Authorization", "Bearer " + clientToken)
				.content(jsonBody)
				.contentType(MediaType.APPLICATION_JSON)
				.accept(MediaType.APPLICATION_JSON))
				.andDo(MockMvcResultHandlers.print());
	
	result.andExpect(status().isCreated());
	result.andExpect(jsonPath("$.id").value(4L));
	result.andExpect(jsonPath("$.moment").exists());
	result.andExpect(jsonPath("$.status").value("WAITING_PAYMENT"));
	result.andExpect(jsonPath("$.client").exists());
	result.andExpect(jsonPath("$.items").exists());
	result.andExpect(jsonPath("$.total").exists());
}



}
