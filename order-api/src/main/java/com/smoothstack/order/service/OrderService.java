package com.smoothstack.order.service;

import com.database.ormlibrary.food.MenuItemEntity;
import com.database.ormlibrary.order.FoodOrderEntity;
import com.database.ormlibrary.order.OrderEntity;
import com.database.ormlibrary.order.OrderTimeEntity;
import com.database.ormlibrary.order.PriceEntity;
import com.database.ormlibrary.user.UserEntity;
import com.smoothstack.order.api.OrderApi;
import com.smoothstack.order.exception.OrderTimeException;
import com.smoothstack.order.exception.UserNotFoundException;
import com.smoothstack.order.model.*;
import com.smoothstack.order.repo.*;
import io.swagger.annotations.ApiParam;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Import;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.context.request.NativeWebRequest;

import javax.validation.Valid;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class OrderService {

    private final OrderRepo orderRepo;
    private final DriverRepo driverRepo;
    private final MenuItemRepo menuItemRepo;
    private final FoodOrderRepo foodOrderRepo;
    private final RestaurantRepo restaurantRepo;
    private final UserRepo userRepo;

    private final ModelMapper modelMapper;

    public OrderService(OrderRepo orderRepo, DriverRepo driverRepo, MenuItemRepo menuItemRepo, FoodOrderRepo foodOrderRepo, RestaurantRepo restaurantRepo, UserRepo userRepo) {
        this.orderRepo = orderRepo;
        this.driverRepo = driverRepo;
        this.menuItemRepo = menuItemRepo;
        this.foodOrderRepo = foodOrderRepo;
        this.restaurantRepo = restaurantRepo;
        this.userRepo = userRepo;
        this.modelMapper = new ModelMapper();
    }


    public ResponseEntity<Void> deleteOrder(Long id){
        OrderEntity orderEntity = orderRepo.findById(id).isPresent() ?
                orderRepo.findById(id).get() : null;
        if (orderEntity == null) return new ResponseEntity<>(HttpStatus.OK);
        List <FoodOrderEntity> foodOrderEntityList = orderEntity.getItems();
        for (FoodOrderEntity foodOrderEntity : foodOrderEntityList)
            foodOrderRepo.delete(foodOrderEntity);
        orderRepo.deleteById(id);

        return new ResponseEntity<>(HttpStatus.OK);
    }

    public ResponseEntity<CreateResponse> createOrder(Order orderDTO,
                                                      Long userId) throws OrderTimeException, UserNotFoundException {
        OrderEntity orderEntity = convertToEntity(orderDTO);
        orderEntity.setActive(true);
        if (orderEntity.getDelivery()) {
            ZonedDateTime deliverySlot = orderEntity.getOrderTimeEntity().getDeliverySlot();
            ZonedDateTime restaurantAccept = orderEntity.getOrderTimeEntity().getRestaurantAccept();
            long diff = ChronoUnit.MINUTES.between(restaurantAccept, deliverySlot);
            if (diff < 15) throw new OrderTimeException("Time slot too early");
        }


        Optional<UserEntity> userEntityOptional = userRepo.findById(userId);
        if (userEntityOptional.isPresent()) {
            userEntityOptional.get().getOrderList().add(orderEntity);
        } else {
            throw new UserNotFoundException("User not found.");
        }
        orderRepo.save(orderEntity);
        return ResponseEntity.ok(new CreateResponse().type(CreateResponse.TypeEnum.STRIPE)
                .id(String.valueOf(orderEntity.getId())).setAddress(orderEntity.getAddress()));
    }

    public List<Order> getUserOrders(Long userId) throws UserNotFoundException {
        Optional<UserEntity> userEntityOptional = userRepo.findById(userId);
        if (userEntityOptional.isPresent()) {
            List<Order> orderList = new ArrayList<>();
            userEntityOptional.get().getOrderList().forEach(orderEntity -> orderList.add(convertToDTO(orderEntity)));
            return orderList;
        }
        throw new UserNotFoundException("User not found!");
    }

    public Order convertToDTO(OrderEntity orderEntity) {
        Order orderDTO = modelMapper.map(orderEntity, Order.class);

        orderDTO.setOrderTime(convertTimeToDTO(orderEntity.getOrderTimeEntity()));

        orderDTO.setOrderType((orderEntity.getDelivery() ? Order.OrderTypeEnum.DELIVERY : Order.OrderTypeEnum.PICKUP));
        if (orderEntity.getDriver() != null) orderDTO.setDriverId(String.valueOf(orderEntity.getDriver().getId()));
        List<FoodOrderEntity> foodOrderEntities = orderEntity.getItems();
        List<OrderFood> orderFoodList = new ArrayList<>();
        List<OrderItems> orderItemsList;

        for (FoodOrderEntity foodOrderEntity : foodOrderEntities) {
            orderItemsList = new ArrayList<>();
            OrderFood orderFood = modelMapper.map(foodOrderEntity, OrderFood.class);
            orderFood.setRestaurantId(String.valueOf(foodOrderEntity.getRestaurantId()));
            orderFood.setRestaurantName(restaurantRepo.findById(foodOrderEntity.getRestaurantId()).get().getName());
            orderFoodList.add(orderFood);
            for (MenuItemEntity menuItemEntity : foodOrderEntity.getOrderItems()) {
                OrderItems orderItems = modelMapper.map(menuItemEntity, OrderItems.class);
                orderItems.setId(String.valueOf(menuItemEntity.getId()));
                orderItemsList.add(orderItems);
            }
            orderFood.setItems(orderItemsList);
        }

        OrderPrice orderPrice = modelMapper.map(orderEntity.getPriceEntity(), OrderPrice.class);
        orderDTO.setPrice(orderPrice);

        orderDTO.setFood(orderFoodList);

        return orderDTO;

    }

    public OrderOrderTime convertTimeToDTO(OrderTimeEntity orderTimeEntity) {

        OrderOrderTime orderTimeDTO = new OrderOrderTime();
        if (orderTimeEntity.getOrderComplete() != null) {
            String orderComplete = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'").format(
                    orderTimeEntity.getOrderComplete());
            orderTimeDTO.setDelivered(orderComplete);
        }
        orderTimeDTO.setDeliverySlot(DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'").format(
                orderTimeEntity.getDeliverySlot()));
        orderTimeDTO.setRestaurantAccept(DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'").format(
                orderTimeEntity.getRestaurantAccept()));

        return orderTimeDTO;
    }

    public OrderEntity convertToEntity(Order orderDTO) {
        OrderEntity orderEntity = modelMapper.map(orderDTO, OrderEntity.class);

        if (orderDTO.getDriverId() != null) {
            if (driverRepo.findById(Long.parseLong(orderDTO.getDriverId())).isPresent())
                orderEntity.setDriver(driverRepo.findById(Long.parseLong(orderDTO.getDriverId())).get());
        }

        orderEntity.setDelivery(orderDTO.getOrderType().equals(Order.OrderTypeEnum.DELIVERY));

       /* OrderOrderTime orderTimeDTO =
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyy-MM-dd'T'HH:mm:ss.SSSX");*/

        List<OrderFood> orderFoodList = orderDTO.getFood();
        List<FoodOrderEntity> foodOrderEntities = new ArrayList<>();

        if (orderFoodList != null && orderFoodList.size() > 0) {
            //finds order lists from all restaurants

            for (OrderFood orderFood : orderFoodList) {
                List<MenuItemEntity> itemEntities = new ArrayList<>();
                //populates a specific order list with items

                for (OrderItems orderItemDTO : orderFood.getItems()) {

                    if (menuItemRepo.findById(Long.parseLong(orderItemDTO.getId())).isPresent()) {
                        itemEntities.add(menuItemRepo.findById(Long.parseLong(orderItemDTO.getId())).get());
                    }

                }
                FoodOrderEntity foodOrderEntity = new FoodOrderEntity();
                foodOrderEntity.setOrderItems(itemEntities);
                foodOrderEntity.setRestaurantId(Long.parseLong(orderFood.getRestaurantId()));
                foodOrderEntities.add(foodOrderEntity);
            }
            orderEntity.setItems(foodOrderEntities);
            orderEntity.setOrderTimeEntity(new OrderTimeEntity().setDeliverySlot(
                            ZonedDateTime.parse(orderDTO.getOrderTime().getDeliverySlot()))
                    .setRestaurantAccept(ZonedDateTime.parse(orderDTO.getOrderTime().getRestaurantAccept())));

            PriceEntity priceEntity = modelMapper.map(orderDTO.getPrice(), PriceEntity.class);
            orderEntity.setPriceEntity(priceEntity);

        }
        return orderEntity;
    }

}
