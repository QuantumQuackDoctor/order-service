package com.smoothstack.order.api;

import com.database.ormlibrary.order.OrderEntity;
import com.smoothstack.order.model.CreateResponse;
import com.smoothstack.order.model.Order;
import com.smoothstack.order.service.OrderService;
import com.smoothstack.order.exception.EmptyCartException;
import com.smoothstack.order.exception.MissingFieldsException;
import com.smoothstack.order.exception.OrderTimeException;
import io.swagger.annotations.ApiParam;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.context.request.NativeWebRequest;

import javax.validation.Valid;
import java.util.List;
import java.util.Optional;
@javax.annotation.Generated(value = "org.openapitools.codegen.languages.SpringCodegen", date = "2021-06-30T22:53:09.076567700-06:00[America/Denver]")
@RestController
@RequestMapping(path = "${openapi.orchestrator.base-path:}")
@CrossOrigin
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
    @PreAuthorize("permitAll()")
    public ResponseEntity<Order> getOrder(@ApiParam(value = "") @Valid @RequestParam(value = "id", required = false) String id){
        return orderService.getOrder(id);
    }

    @Override
    public ResponseEntity<Void> patchOrders(Order order) {
        return orderService.patchOrders(order);
    }

    @Override
    public ResponseEntity<List<Order>> getActiveOrders(String sortType, Integer page, Integer size) {
        return orderService.getActiveOrders(sortType, page, size);
    }

    @Override
    public ResponseEntity<CreateResponse> createOrder(Order order) throws MissingFieldsException, EmptyCartException, OrderTimeException {
       if (!order.checkRequiredFields())
            throw new MissingFieldsException("Missing require fields");
       if (order.getFood().size() == 0)
           throw new EmptyCartException("No items in cart.");
       return orderService.createOrder(order);
    }

    @Override
    public ResponseEntity<Void> deleteOrder(String id) {
        orderService.deleteOrder(id);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @PutMapping(path = "/order/sample")
    public ResponseEntity<OrderEntity> addSampleOrder (){
        return ResponseEntity.ok(orderService.createSampleOrder());
    }
}
