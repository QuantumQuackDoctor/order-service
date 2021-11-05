package com.smoothstack.order.api;

import com.database.security.AuthDetails;
import com.smoothstack.order.exception.OrderNotAcceptedException;
import com.smoothstack.order.exception.OrderNotFoundException;
import com.smoothstack.order.model.Order;
import com.smoothstack.order.model.OrderAction;
import com.smoothstack.order.service.DriverService;
import io.swagger.annotations.*;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

@RestController
@RequestMapping("/order")
@RequiredArgsConstructor
public class DriverApiController {
    private final DriverService driverService;

    /**
     * PATCH /order/driver : Driver Update Order
     * Mark order delivered/picked up
     *
     * @param orderAction (optional)
     * @return Order Delivered (status code 200)
     * or Unauthorized (status code 401)
     * or Forbidden (status code 403)
     * or Order Not Found (status code 404)
     */
    @ApiOperation(value = "Driver Update Order", nickname = "driverDeliverOrder", notes = "Mark order delivered/picked up", authorizations = {

            @Authorization(value = "JWT")
    }, tags = {"order",})
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Order Delivered"),
            @ApiResponse(code = 401, message = "Unauthorized"),
            @ApiResponse(code = 403, message = "Forbidden"),
            @ApiResponse(code = 404, message = "Order Not Found"),
            @ApiResponse(code = 410, message = "Order not accepted")})
    @PostMapping(
            value = "/driver/update",
            consumes = {"application/json"}
    )
    @PreAuthorize("hasAuthority('driver')")
    public ResponseEntity<Void> driverDeliverOrder(
            @ApiParam(value = "") @Valid @RequestBody(required = false) OrderAction orderAction,
            Authentication authentication) throws OrderNotFoundException, OrderNotAcceptedException {
        AuthDetails authDetails = (AuthDetails) authentication.getPrincipal();
        switch (orderAction.getAction()) {
            case RETRIEVED:
                driverService.pickUpOrder(orderAction.getOrderId(), authDetails.getId());
                break;
            case DELIVERED:
                driverService.deliverOrder(orderAction.getOrderId(), authDetails.getId());
                break;
            default:
                //these will do something later
        }
        return ResponseEntity.ok(null);
    }

    /**
     * GET /order/driver : Driver Get Accepted Orders
     * Returns all orders assigned to driver
     *
     * @return OK (status code 200)
     * or Access token is missing or invalid (status code 401)
     * or Forbidden (status code 403)
     */
    @ApiOperation(value = "Driver Get Accepted Orders", nickname = "getAcceptedOrders", notes = "Returns all orders assigned to driver", response = Order.class, responseContainer = "List", authorizations = {

            @Authorization(value = "JWT")
    }, tags = {"order",})
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "OK", response = Order.class, responseContainer = "List"),
            @ApiResponse(code = 401, message = "Access token is missing or invalid", response = String.class),
            @ApiResponse(code = 403, message = "Forbidden")})
    @GetMapping(
            value = "/driver/accepted",
            produces = {"application/json", "application/xml"}
    )
    @PreAuthorize("hasAuthority('driver')")
    public ResponseEntity<List<Order>> getAcceptedOrders(Authentication authentication) {
        AuthDetails authDetails = (AuthDetails) authentication.getPrincipal();
        List<Order> orderList = driverService.getAcceptedOrders(authDetails.getId());
        return ResponseEntity.ok(orderList);
    }
}
