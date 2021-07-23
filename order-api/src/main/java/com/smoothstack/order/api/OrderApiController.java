package com.smoothstack.order.api;

import com.database.ormlibrary.food.MenuItemEntity;
import com.database.ormlibrary.order.OrderEntity;
import com.database.ormlibrary.order.OrderTimeEntity;
import com.smoothstack.order.model.CreateResponse;
import com.smoothstack.order.model.Order;
import com.smoothstack.order.model.OrderOrderTime;
import com.smoothstack.order.service.OrderService;
import error.EmptyCartException;
import error.MissingFieldsException;
import io.swagger.annotations.ApiParam;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.context.request.NativeWebRequest;

import javax.validation.Valid;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
@javax.annotation.Generated(value = "org.openapitools.codegen.languages.SpringCodegen", date = "2021-06-30T22:53:09.076567700-06:00[America/Denver]")
@Controller
@RequestMapping(path = "${openapi.orchestrator.base-path:}")
//@RequestMapping(path = "/order")
public class OrderApiController implements OrderApi {

    private final NativeWebRequest request;
    private final OrderService orderService;

    @org.springframework.beans.factory.annotation.Autowired
    public OrderApiController(NativeWebRequest request, OrderService orderService) {
        this.request = request;
        this.orderService = orderService;
    }

    @Override
    public Optional<NativeWebRequest> getRequest() {
        return Optional.ofNullable(request);
    }

    @Override
    public ResponseEntity<List<Order>> getOrder(@ApiParam(value = "if true only returns pending orders") @Valid @RequestParam(value = "active", required = false) Boolean active){
        return orderService.getOrder(true);
    }

    @Override
    public ResponseEntity<?> createOrder(Order order) {
       if (!order.checkRequiredFields())
            return ResponseEntity.badRequest().body (new MissingFieldsException("Missing require fields"));
       if (order.getFood().size() == 0)
           return ResponseEntity.badRequest().body(new EmptyCartException("No items in cart."));
        return orderService.createOrder(order);
    }

    @PutMapping(path = "/order/sample")
    public ResponseEntity<?> addSampleOrder (){
        return ResponseEntity.ok(orderService.createSampleOrder());
    }
}
