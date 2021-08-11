package com.smoothstack.order.api;

import com.smoothstack.order.model.CreateResponse;
import com.smoothstack.order.model.Order;
import com.smoothstack.order.service.OrderService;
import com.smoothstack.order.exception.EmptyCartException;
import com.smoothstack.order.exception.MissingFieldsException;
import com.smoothstack.order.exception.OrderTimeException;
import io.swagger.annotations.ApiParam;
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
    public ResponseEntity<List<Order>> getOrder(@ApiParam(value = "if true only returns pending orders") @Valid @RequestParam(value = "active", required = false) Boolean active){
        return orderService.getOrder(true);
    }

    @Override
    @PreAuthorize("hasAuthority('user')")
    public ResponseEntity<CreateResponse> createOrder(Order order) throws MissingFieldsException, EmptyCartException, OrderTimeException {
       if (!order.checkRequiredFields())
            throw new MissingFieldsException("Missing require fields");
       if (order.getFood().size() == 0)
           throw new EmptyCartException("No items in cart.");
        return orderService.createOrder(order);
    }

/*    @PutMapping(path = "/order/sample")
    public ResponseEntity<CreateResponse> addSampleOrder (){
        return ResponseEntity.ok(orderService.createSampleOrder());
    }*/
}
