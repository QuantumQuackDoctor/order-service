package com.smoothstack.order.api;

import com.smoothstack.order.model.Order;
import com.smoothstack.order.service.OrderService;
import io.swagger.annotations.ApiParam;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.context.request.NativeWebRequest;

import javax.validation.Valid;
import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
@javax.annotation.Generated(value = "org.openapitools.codegen.languages.SpringCodegen", date = "2021-06-30T22:53:09.076567700-06:00[America/Denver]")
@Controller
@RequestMapping("${openapi.orchestrator.base-path:}")
public class OrdersApiController implements OrdersApi {

    private final NativeWebRequest request;
    private final OrderService orderService;

    @org.springframework.beans.factory.annotation.Autowired
    public OrdersApiController(NativeWebRequest request, OrderService orderService) {
        this.request = request;
        this.orderService = orderService;
    }

    @Override
    public Optional<NativeWebRequest> getRequest() {
        return Optional.ofNullable(request);
    }

    @Override
    @PreAuthorize("hasAuthority('admin')")
    public ResponseEntity<List<Order>> getOrders(@ApiParam(value = "filters active orders") @Valid @RequestParam(value = "active", required = false) Boolean active,
                                                 @ApiParam(value = "filters by userId") @Valid @RequestParam(value = "userId", required = false) String userId,
                                                 @ApiParam(value = ">= price") @Valid @RequestParam(value = "price", required = false) BigDecimal price,
                                                 @ApiParam(value = "price becomes <=") @Valid @RequestParam(value = "invertPrice", required = false) Boolean invertPrice,
                                                 @ApiParam(value = "matches paymentStatus") @Valid @RequestParam(value = "paymentStatus", required = false) String paymentStatus) {
        return orderService.getOrders(true);
    }

}
