package com.smoothstack.order.api;

import com.database.security.AuthDetails;
import com.smoothstack.order.exception.*;
import com.smoothstack.order.model.*;
import com.smoothstack.order.service.OrderService;
import io.swagger.annotations.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.context.request.NativeWebRequest;

import javax.validation.Valid;
import java.util.List;
import java.util.Optional;

@javax.annotation.Generated(value = "org.openapitools.codegen.languages.SpringCodegen", date = "2021-06-30T22:53:09.076567700-06:00[America/Denver]")
@RestController
@RequestMapping(path = "${openapi.orchestrator.base-path:}")
@CrossOrigin
@Slf4j (topic = "OrderAPI Controller: ")
public class OrderApiController {

    private final NativeWebRequest request;
    private final OrderService orderService;

    @org.springframework.beans.factory.annotation.Autowired
    public OrderApiController(NativeWebRequest request, OrderService orderService) {
        this.request = request;
        this.orderService = orderService;
    }

    public Optional<NativeWebRequest> getRequest() {
        return Optional.ofNullable(request);
    }

    /**
         * DELETE /order/user : User cancel order
         * Create new order, sends back checkout session data. Payment intent will be canceled in 5 minutes if not paid. (Server note: use stripe webhooks to update payment status)
         *
         * @param order (optional)
         * @return OK (status code 200)
         * or Access token is missing or invalid (status code 401)
         * or Forbidden (status code 403)
         */
    @DeleteMapping(path = "/order/user", produces = {"application/json", "application/xml"})
    @io.swagger.annotations.ApiResponses({
            @ApiResponse(code = 200, message = "OK", response = CreateResponse.class),
            @ApiResponse(code = 401, message = "Access token is missing or invalid", response = String.class),
            @ApiResponse(code = 403, message = "Forbidden")})
    @io.swagger.annotations.ApiOperation(value = "Delete order", nickname = "deleteOrder", notes = "Delete order by id", response = CreateResponse.class, tags = {"order",})
    @PreAuthorize("hasAuthority ('user')")
    public ResponseEntity<String> cancelOrder (@RequestParam(value = "id", required = true)
                                                   @Valid @ApiParam("id") Long orderId,
                                               Authentication authentication)
            throws ValueNotPresentException {
        log.info ("cancelOrder called");
        AuthDetails authDetails = (AuthDetails) authentication.getPrincipal();
        return orderService.cancelOrder (orderId, authDetails.getId());
    }

    @PatchMapping(value = "/orders/status", produces = {"application/json"}, consumes = {"application/json"})
    @ApiResponses({
            @ApiResponse(code = 200, message = "Update successful"),
            @ApiResponse(code = 401, message = "Access token is missing or invalid", response = String.class),
            @ApiResponse(code = 403, message = "Forbidden"),
            @ApiResponse(code = 404, message = "Not Found")})
    @ApiOperation(value = "Driver updates order status", nickname = "patchOrderStatus", notes = "driver uses this to change order status", authorizations = {
            @Authorization(value = "JWT")
    }, tags = {"order",})
    @PreAuthorize("hasAuthority('user')")
    public ResponseEntity<Void> patchOrderStatus (@RequestBody(required = false)
                                                      @ApiParam("Order to update, non null properties will be updated," +
                                                              " id necessary")
                                                      @Valid Order orderDTO){
        log.info ("patchOrderStatus called.");
        return ResponseEntity.ok (orderService.patchOrderStatus (orderDTO));
    }


    /**
         * PATCH /order/details : User Update Order
         * Update order, use this to connect orders with drivers (set driverId)
         *
         * @param order Order to update, non null properties will be updated, id necessary (optional)
         * @return Update successful (status code 200)
         * or Access token is missing or invalid (status code 401)
         * or Forbidden (status code 403)
         * or Not Found (status code 404)
         */
    @PatchMapping(value = "/order/details", produces = {"application/json"}, consumes = {"application/json"})
    @ApiResponses({
            @ApiResponse(code = 200, message = "Update successful"),
            @ApiResponse(code = 401, message = "Access token is missing or invalid", response = String.class),
            @ApiResponse(code = 403, message = "Forbidden"),
            @ApiResponse(code = 404, message = "Not Found")})
    @ApiOperation(value = "User Update Order", nickname = "patchOrders", notes = "Users use this to update order details", authorizations = {

            @Authorization(value = "JWT")
    }, tags = {"order",})
    public ResponseEntity<String> patchOrder(@RequestBody(required = false) @Valid
                                                 @ApiParam("Order to update, non null properties will be updated, id necessary")
                                                         Order order,
                                             Authentication authentication) throws ValueNotPresentException {
        log.info ("patchOrders called");
        AuthDetails authDetails = (AuthDetails) authentication.getPrincipal();
        return orderService.patchOrder(order, authDetails.getId());
    }

    /**
         * PATCH /orders : Driver pick Order
         * Update order, use this to connect orders with drivers (set driverId)
         *
         * @param Driver id and order id
         * @return Update successful (status code 200)
         *         or Access token is missing or invalid (status code 401)
         *         or Forbidden (status code 403)
         *         or Not Found (status code 404)
         */
    @PatchMapping(value = "/orders/drivers", produces = {"application/json"}, consumes = {"application/json"})
    @ApiResponses({
            @ApiResponse(code = 200, message = "Update successful"),
            @ApiResponse(code = 401, message = "Access token is missing or invalid", response = String.class),
            @ApiResponse(code = 403, message = "Forbidden"),
            @ApiResponse(code = 404, message = "Not Found")})
    @ApiOperation(value = "Driver pick Order", nickname = "patchOrders", notes = "Update order, assign driver to order", authorizations = {

            @Authorization(value = "JWT")
    }, tags = {"order",})
    @PreAuthorize("hasAuthority ('user')")
    public ResponseEntity<Void> patchDriverOrders(@RequestParam(required = true)
                                                      @Valid @ApiParam("Update order, assign driver to order") Long order,
                                                  @RequestParam(required = true) @Valid Long driver,
                                                  @RequestParam(required = true) @Valid Boolean assign) {
        return orderService.patchDriverOrders(order, driver, assign);
    }

    /**
         * GET /order : Get orders
         * Returns orders
         *
         * @return OK (status code 200)
         * or Invalid deliverySlot (status code 400)
         * or Access token is missing or invalid (status code 401)
         * or Forbidden (status code 403)
         * or restaurant/item not found (status code 404)
         */
    @GetMapping(path = "/orders/driver", produces = {"application/json"})
    @ApiResponses({
            @ApiResponse(code = 200, message = "OK", response = Order.class, responseContainer = "List"),
            @ApiResponse(code = 400, message = "Invalid deliverySlot"),
            @ApiResponse(code = 401, message = "Access token is missing or invalid", response = String.class),
            @ApiResponse(code = 403, message = "Forbidden"),
            @ApiResponse(code = 404, message = "restaurant/item not found")})
    @ApiOperation(value = "Get actiive orders", nickname = "getActiveOrders", notes = "A list of active orders", response = Order.class, responseContainer = "List", tags = {"order",})
    @PreAuthorize("hasAuthority('driver')")
    public ResponseEntity<List<Order>> getActiveOrders(@ApiParam("type of sort")
                                                           @Valid @RequestParam(value = "sort_type", required = false) String sortType,
                                                       @javax.validation.constraints.Min(0)
                                                       @ApiParam("page to return indexed by 0")
                                                       @Valid @RequestParam(value = "page", required = false) Integer page,
                                                       @javax.validation.constraints.Min(1) @ApiParam("items in page")
                                                           @Valid @RequestParam(value = "size", required = false) Integer size) {
        log.info ("getActiveOrders called");
        return orderService.getActiveOrders(sortType, page, size);
    }

    /**
         * PUT /order : Create order
         * Create new order, sends back checkout session data. Payment intent will be canceled in 5 minutes if not paid. (Server note: use stripe webhooks to update payment status)
         *
         * @param order (optional)
         * @return OK (status code 200)
         * or Access token is missing or invalid (status code 401)
         * or Forbidden (status code 403)
         */
    @PutMapping(path = "/order", produces = {"application/json", "application/xml"}, consumes = {"application/json", "application/xml"})
    @io.swagger.annotations.ApiResponses({
            @ApiResponse(code = 200, message = "OK", response = CreateResponse.class),
            @ApiResponse(code = 401, message = "Access token is missing or invalid", response = String.class),
            @ApiResponse(code = 403, message = "Forbidden")})
    @io.swagger.annotations.ApiOperation(value = "Create order", nickname = "createOrder", notes = "Create new order, sends back checkout session data. Payment intent will be canceled in 5 minutes if not paid. (Server note: use stripe webhooks to update payment status)", response = CreateResponse.class, tags = {"order",})
    @PreAuthorize("hasAuthority('user')")
    public ResponseEntity<CreateResponse> createOrder
    (@RequestBody(required = false) @ApiParam("") @Valid Order order,
     Authentication authentication, @RequestParam String chargeId)
            throws MissingFieldsException, EmptyCartException, OrderTimeException, UserNotFoundException {
        log.info ("createOrder called");
        if (!order.checkRequiredFields())
            throw new MissingFieldsException("Missing require fields");
        if (order.getFood().size() == 0)
            throw new EmptyCartException("No items in cart.");
        AuthDetails autheDetails = (AuthDetails) authentication.getPrincipal();
        return orderService.createOrder(order, autheDetails.getId(), chargeId);
    }

    @PreAuthorize("hasAuthority('user')")
    @GetMapping(path = "/order/user", produces = {"application/json"})
    public ResponseEntity<List<Order>> getUserOrders(Authentication authentication) throws UserNotFoundException {
        AuthDetails authDetails = (AuthDetails) authentication.getPrincipal();
        log.info ("getUserOrders called");
        return ResponseEntity.ok(orderService.getUserOrders(authDetails.getId()));
    }

    /**
         * PUT /order : Create order
         * Create new order, sends back checkout session data. Payment intent will be canceled in 5 minutes if not paid. (Server note: use stripe webhooks to update payment status)
         *
         * @param order (optional)
         * @return OK (status code 200)
         * or Access token is missing or invalid (status code 401)
         * or Forbidden (status code 403)
         */
    @DeleteMapping(path = "/order", produces = {"application/json", "application/xml"})
    @io.swagger.annotations.ApiResponses({
            @ApiResponse(code = 200, message = "OK", response = CreateResponse.class),
            @ApiResponse(code = 401, message = "Access token is missing or invalid", response = String.class),
            @ApiResponse(code = 403, message = "Forbidden")})
    @io.swagger.annotations.ApiOperation(value = "Delete order", nickname = "deleteOrder", notes = "Delete order by id", response = CreateResponse.class, tags = {"order",})
    public ResponseEntity<Void> deleteOrder
    (@RequestParam(value = "id", required = true) @Valid @ApiParam("id") Long id) throws ValueNotPresentException {
        log.info ("deleteOrder called");
        orderService.deleteOrder(id);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    /**
         * PATCH /orders : Driver pick Order
         * Update order, use this to connect orders with drivers (set driverId)
         *
         * @param Driver id and order id
         * @return Update successful (status code 200)
         *         or Access token is missing or invalid (status code 401)
         *         or Forbidden (status code 403)
         *         or Not Found (status code 404)
         */
    @ApiResponses({
            @ApiResponse(code = 200, message = "Update successful"),
            @ApiResponse(code = 401, message = "Access token is missing or invalid", response = String.class),
            @ApiResponse(code = 403, message = "Forbidden"),
            @ApiResponse(code = 404, message = "Not Found")})
    @ApiOperation(value = "Driver pick Order", nickname = "patchOrders", notes = "Update order, assign driver to order", authorizations = {

            @Authorization(value = "JWT")
    }, tags = {"order",})
    @PreAuthorize("hasAuthority ('user')")
    @PostMapping ("/order/charge")
    public ResponseEntity<ChargeResponse> createPaymentIntent (@RequestBody ChargeRequest chargeRequest) {
        log.info ("createStripeCharge called");
        return ResponseEntity.ok().body(orderService.createStripeCharge(chargeRequest));
    }

    @PreAuthorize("hasAuthority(('user'))")
    @PostMapping ("/order/email-order")
    public ResponseEntity<Order> sendOrderConfirmation
            (@Valid @RequestBody CreateResponse createResponse,
             Authentication authentication) throws UserNotFoundException {
        AuthDetails authDetails = (AuthDetails) authentication.getPrincipal();
        log.info ("sendOrderConfirmation called");
        return ResponseEntity.ok().body(this.orderService.sendOrderConfirmation(Long.parseLong(createResponse.getId()), authDetails.getId()));
    }

    /**
     * PUT /order/driver : Driver Accept Orders
     * Accepts order
     *
     * @param requestBody Array of order id&#39;s to accept (optional)
     * @return Orders Accepted (status code 200)
     * or Orders can&#39;t be accepted together (status code 400)
     * or Access token is missing or invalid (status code 401)
     * or Forbidden (status code 403)
     * or Order not found (status code 404)
     */
    @ApiOperation(value = "Driver Accept Orders", nickname = "driverAcceptOrders", notes = "Accepts order", authorizations = {

            @Authorization(value = "JWT")
    }, tags = {"order",})
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Orders Accepted"),
            @ApiResponse(code = 400, message = "Orders can't be accepted together", response = String.class, responseContainer = "List"),
            @ApiResponse(code = 401, message = "Access token is missing or invalid", response = String.class),
            @ApiResponse(code = 403, message = "Forbidden"),
            @ApiResponse(code = 404, message = "Order not found", response = String.class, responseContainer = "List")})
    @PutMapping(
            value = "/order/driver",
            produces = {"application/json"},
            consumes = {"application/json"}
    )
    public ResponseEntity<Void> driverAcceptOrders
    (@ApiParam(value = "Array of order id's to accept") @Valid @RequestBody(required = false) List<String> requestBody) {
        return new ResponseEntity<>(HttpStatus.NOT_IMPLEMENTED);
    }

    /**
     * DELETE /order/driver : Driver Cancel Order
     * Removes driver from order, a new driver should be assigned
     *
     * @param body orderId (optional)
     * @return Removed from order (status code 200)
     * or Access token is missing or invalid (status code 401)
     * or Forbidden (status code 403)
     * or Not Found (status code 404)
     */
    @ApiOperation(value = "Driver Cancel Order", nickname = "driverRemoveOrder", notes = "Removes driver from order, a new driver should be assigned", authorizations = {

            @Authorization(value = "JWT")
    }, tags = {"order",})
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Removed from order"),
            @ApiResponse(code = 401, message = "Access token is missing or invalid", response = String.class),
            @ApiResponse(code = 403, message = "Forbidden"),
            @ApiResponse(code = 404, message = "Not Found")})
    @DeleteMapping(
            value = "/order/driver",
            produces = {"application/json"},
            consumes = {"application/json"}
    )
    public ResponseEntity<Void> driverRemoveOrder
    (@ApiParam(value = "orderId") @Valid @RequestBody(required = false) String body) {
        return new ResponseEntity<>(HttpStatus.NOT_IMPLEMENTED);
    }

    /**
     * PATCH /order : Update Order Configurations
     * Updates active order
     *
     * @param inlineObject (optional)
     * @return Update successful (status code 200)
     * or Access token is missing or invalid (status code 401)
     * or Forbidden (status code 403)
     * or Item or order not found (status code 404)
     */
    @ApiOperation(value = "Update Order Configurations", nickname = "updateOrderConfigurations", notes = "Updates active order"/*, authorizations = {

        @Authorization(value = "JWT")
         }*/, tags = {"order",})
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Update successful"),
            @ApiResponse(code = 401, message = "Access token is missing or invalid", response = String.class),
            @ApiResponse(code = 403, message = "Forbidden"),
            @ApiResponse(code = 404, message = "Item or order not found")})
    @PatchMapping(
            value = "/order",
            produces = {"application/json"},
            consumes = {"application/json"}
    )
    public ResponseEntity<Void> updateOrderConfigurations
    (@ApiParam(value = "") @Valid @RequestBody(required = false) InlineObject inlineObject) {
        return new ResponseEntity<>(HttpStatus.NOT_IMPLEMENTED);
    }
}



