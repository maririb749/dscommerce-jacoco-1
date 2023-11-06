package com.devsuperior.dscommerce.controllers.IT;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
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

private Long existingOrderId;
private String  adminUsername, adminPassword;

private String  adminToken;

@BeforeEach
void setUp() throws Exception {
    // Inicializa apenas as variáveis necessárias para o teste
    adminUsername = "alex@gmail.com";
    adminPassword = "123456";
    adminToken = tokenUtil.obtainAccessToken(mockMvc, adminUsername, adminPassword);
    
    existingOrderId = 1L;
    
}

@Test
public void findByIdShouldReturnOrderDTOWhenIdExistsAndAdminLogged() throws Exception {
    ResultActions result =
            mockMvc.perform(get("/orders/{id}", existingOrderId)
                    .header("Authorization", "Bearer " + adminToken)
                    .accept(MediaType.APPLICATION_JSON));

    result.andExpect(status().isOk());
    result.andExpect(jsonPath("$.id").value(1L));
    result.andExpect(jsonPath("$.moment").value("2022-07-25T13:00:00Z"));
    result.andExpect(jsonPath("$.status").value("PAID"));
    result.andExpect(jsonPath("$.client").exists());
    result.andExpect(jsonPath("$.payment").exists());
    result.andExpect(jsonPath("$.items").exists());
    result.andExpect(jsonPath("$.total").exists());
}
}
