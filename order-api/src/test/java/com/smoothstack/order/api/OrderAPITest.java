package com.smoothstack.order.api;

import com.database.ormlibrary.food.MenuItemEntity;
import com.database.ormlibrary.food.RestaurantEntity;
import com.database.ormlibrary.order.FoodOrderEntity;
import com.database.ormlibrary.order.OrderEntity;
import com.database.ormlibrary.order.OrderTimeEntity;
import com.database.ormlibrary.order.PriceEntity;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.smoothstack.order.model.Order;
import com.smoothstack.order.repo.*;
import com.smoothstack.order.service.OrderService;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
public class OrderAPITest {

    @Autowired
    MockMvc mockMvc;
    ObjectMapper objectMapper = new ObjectMapper();
    @MockBean
    private MenuItemRepo menuItemRepo;
    @MockBean
    private OrderRepo orderRepo;
    @MockBean
    private DriverRepo driverRepo;
    @MockBean
    private FoodOrderRepo foodOrderRepo;
    @MockBean
    private RestaurantRepo restaurantRepo;
    @Autowired
    private OrderService orderService;

    @Test
    void apiTest() throws Exception {
        OrderEntity orderEntity = getSampleOrder();

        Mockito.when (restaurantRepo.findById(Mockito.any())).thenReturn(Optional.of (new RestaurantEntity().setName("Sample Restaurant")));

        Order orderDTO = orderService.convertToDTO(orderEntity);

        mockMvc.perform(get("/order"))
                .andExpect(status().isOk());

        mockMvc.perform(put("/order").content(objectMapper
                        .writeValueAsString(orderDTO)).contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
    }

    public OrderEntity getSampleOrder() {
        OrderTimeEntity orderTimeEntity = new OrderTimeEntity().setDeliverySlot(ZonedDateTime.parse("2011-12-03T10:35:30+01:00"))
                .setRestaurantAccept(ZonedDateTime.parse("2011-12-03T10:15:30+01:00"));

        List<MenuItemEntity> orderItemsEntities = new ArrayList<>();
        MenuItemEntity menuItemEntity1 = new MenuItemEntity().setName("Sample Item 1").setId(1L);
        MenuItemEntity menuItemEntity2 = new MenuItemEntity().setName("Sample Item 2").setId(2L);
        orderItemsEntities.add(menuItemEntity1);
        orderItemsEntities.add(menuItemEntity2);

        FoodOrderEntity foodOrderEntity = new FoodOrderEntity().setId(1L).setOrderItems(orderItemsEntities).setRestaurantId(1L);
        List<FoodOrderEntity> foodOrderEntities = new ArrayList<>();
        foodOrderEntities.add(foodOrderEntity);

        PriceEntity priceEntity = new PriceEntity().setFood(23.09f);

        return new OrderEntity()
                .setDelivery(true).setRefunded(false)
                .setAddress("123 Street St").setOrderTimeEntity(orderTimeEntity)
                .setItems(foodOrderEntities).setPriceEntity(priceEntity);
    }

}
