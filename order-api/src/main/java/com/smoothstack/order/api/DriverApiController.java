package com.smoothstack.order.api;

import com.database.security.AuthDetails;
import com.smoothstack.order.exception.OrderNotAcceptedException;
import com.smoothstack.order.exception.OrderNotFoundException;
import com.smoothstack.order.model.OrderAction;
import com.smoothstack.order.service.DriverService;
import io.swagger.annotations.*;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;

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
            @ApiResponse(code = 404, message = "Order Not Found")})
    @PatchMapping(
            value = "/order/driver",
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
            case DELIVERED:
            default:
                //these will do something later
        }
        return ResponseEntity.ok(null);
    }

}
