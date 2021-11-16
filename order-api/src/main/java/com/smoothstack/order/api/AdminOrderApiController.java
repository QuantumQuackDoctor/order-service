package com.smoothstack.order.api;

import com.smoothstack.order.exception.ValueNotPresentException;
import com.smoothstack.order.model.Order;
import com.smoothstack.order.service.OrderService;
import io.swagger.annotations.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.context.request.NativeWebRequest;

import javax.validation.Valid;
import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
@javax.annotation.Generated(value = "org.openapitools.codegen.languages.SpringCodegen", date = "2021-06-30T22:53:09.076567700-06:00[America/Denver]")
@Controller
@RequestMapping("${openapi.orchestrator.base-path:}")
@Slf4j(topic = "AdminOrderAPI Controller: ")
public class AdminOrderApiController {

    private final NativeWebRequest request;
    private final OrderService orderService;

    @org.springframework.beans.factory.annotation.Autowired
    public AdminOrderApiController(NativeWebRequest request, OrderService orderService) {
        this.request = request;
        this.orderService = orderService;
    }

    public Optional<NativeWebRequest> getRequest() {
        return Optional.ofNullable(request);
    }

    /**
     * DELETE /orders : Admin Delete Order
     * Delete order
     *
     * @param body Order id to delete (optional)
     * @return OK (status code 200)
     *         or Access token is missing or invalid (status code 401)
     *         or Forbidden (status code 403)
     *         or Not Found (status code 404)
     */
    @ApiOperation(value = "Admin Delete Order", nickname = "deleteOrders", notes = "Delete order", authorizations = {

        @Authorization(value = "JWT")
         }, tags={ "order", })
    @ApiResponses(value = {
        @ApiResponse(code = 200, message = "OK"),
        @ApiResponse(code = 401, message = "Access token is missing or invalid", response = String.class),
        @ApiResponse(code = 403, message = "Forbidden"),
        @ApiResponse(code = 404, message = "Not Found") })
    @DeleteMapping(
        value = "/orders",
        produces = { "application/json" },
        consumes = { "application/json" }
    )
    public ResponseEntity<Void> deleteOrders
    (@ApiParam(value = "Order id to delete") @Valid @RequestBody(required = false) String body) {
        return new ResponseEntity<>(HttpStatus.NOT_IMPLEMENTED);

    }

    /**
     * GET /order : Get orders
     * Returns order by id
     *
     * @param id order id
     * @return OK (status code 200)
     * or Invalid deliverySlot (status code 400)
     * or Access token is missing or invalid (status code 401)
     * or Forbidden (status code 403)
     * or restaurant/item not found (status code 404)
     */
    @GetMapping(path = "/order", produces = {"application/json", "application/xml"})
    @ApiResponses({
            @ApiResponse(code = 200, message = "OK", response = Order.class, responseContainer = "List"),
            @ApiResponse(code = 400, message = "Invalid deliverySlot"),
            @ApiResponse(code = 401, message = "Access token is missing or invalid", response = String.class),
            @ApiResponse(code = 403, message = "Forbidden"),
            @ApiResponse(code = 404, message = "restaurant/item not found")})
    @ApiOperation(value = "Get an order", nickname = "getOrder", notes = "Returns authenticated users orders, server will check ensure deliveryslot is valid for all chosen restaurants", response = Order.class, responseContainer = "List", tags = {"order",})
    @PreAuthorize("hasAuthority('admin')")
    public ResponseEntity<Order> getOrder(@ApiParam(value = "") @Valid @RequestParam(value = "id", required = false)
                                                  Long id) throws ValueNotPresentException {
        log.info ("getOrder called.");
        return orderService.getOrder(id);
    }

    /**
     * PATCH /orders : Admin Update Order
     * Update order, use this to connect orders with drivers (set driverId)
     *
     * @param order Order to update, non null properties will be updated, id necessary (optional)
     * @return Update successful (status code 200)
     * or Access token is missing or invalid (status code 401)
     * or Forbidden (status code 403)
     * or Not Found (status code 404)
     */
    @PatchMapping(value = "/orders", produces = {"application/json"}, consumes = {"application/json"})
    @ApiResponses({
            @ApiResponse(code = 200, message = "Update successful"),
            @ApiResponse(code = 401, message = "Access token is missing or invalid", response = String.class),
            @ApiResponse(code = 403, message = "Forbidden"),
            @ApiResponse(code = 404, message = "Not Found")})
    @ApiOperation(value = "Admin Update Order", nickname = "patchOrders", notes = "Update order, use this to connect orders with drivers (set driverId)", authorizations = {

            @Authorization(value = "JWT")
    }, tags = {"order",})
    public ResponseEntity<Void> patchOrders(@RequestBody(required = false)
                                            @Valid @ApiParam("Order to update, non null properties will be updated" +
            ", id necessary") Order order) {
        log.info ("patchOrders called");
        return orderService.patchOrders(order);
    }

    /**
     * GET /orders : Admin get orders
     * admin search orders
     *
     * @param active filters active orders (optional)
     * @param userId filters by userId (optional)
     * @param price &gt;&#x3D; price (optional)
     * @param invertPrice price becomes &lt;&#x3D; (optional)
     * @param paymentStatus matches paymentStatus (optional)
     * @return OK (status code 200)
     */
    @ApiOperation(value = "Admin get orders", nickname = "getOrders", notes = "admin search orders", response = Order.class, responseContainer = "List", authorizations = {

        @Authorization(value = "JWT")
         }, tags={ "order", })
    @ApiResponses(value = {
        @ApiResponse(code = 200, message = "OK", response = Order.class, responseContainer = "List") })
    @GetMapping(
        value = "/orders",
        produces = { "application/json", "application/xml" }
    )
    public ResponseEntity<List<Order>> getOrders
    (@ApiParam(value = "filters active orders") @Valid @RequestParam(value = "active", required = false) Boolean active,
     @ApiParam(value = "filters by userId") @Valid @RequestParam(value = "userId", required = false) String userId,
     @ApiParam(value = ">= price") @Valid @RequestParam(value = "price", required = false) BigDecimal price,
     @ApiParam(value = "price becomes <=") @Valid @RequestParam(value = "invertPrice", required = false) Boolean invertPrice,
     @ApiParam(value = "matches paymentStatus") @Valid @RequestParam(value = "paymentStatus", required = false) String paymentStatus) {
        getRequest().ifPresent(request -> {
            for (MediaType mediaType: MediaType.parseMediaTypes(request.getHeader("Accept"))) {
                if (mediaType.isCompatibleWith(MediaType.valueOf("application/json"))) {
                    String exampleString = "{ \"orderType\" : \"delivery\", \"driverNote\" : \"driverNote\", \"address\" : \"address\", \"orderTime\" : { \"driverAccept\" : \"2021-02-10T00:00:00.000Z\", \"orderPlaced\" : \"2021-02-10T00:00:00.000Z\", \"deliverySlot\" : \"2021-02-10T00:00:00.000Z\", \"restaurantStart\" : \"2021-02-10T00:00:00.000Z\", \"delivered\" : \"2021-02-10T00:00:00.000Z\", \"restaurantAccept\" : \"2021-02-10T00:00:00.000Z\", \"restaurantComplete\" : \"2021-02-10T00:00:00.000Z\" }, \"driverId\" : \"driverId\", \"price\" : { \"delivery\" : 6.027456183070403, \"tip\" : 1.4658129805029452, \"food\" : 0.8008281904610115 }, \"refunded\" : true, \"id\" : \"id\", \"restaurantId\" : \"restaurantId\", \"food\" : [ { \"restaurantId\" : \"restaurantId\", \"items\" : [ { \"configurations\" : [ \"configurations\", \"configurations\" ], \"name\" : \"name\" }, { \"configurations\" : [ \"configurations\", \"configurations\" ], \"name\" : \"name\" } ] }, { \"restaurantId\" : \"restaurantId\", \"items\" : [ { \"configurations\" : [ \"configurations\", \"configurations\" ], \"name\" : \"name\" }, { \"configurations\" : [ \"configurations\", \"configurations\" ], \"name\" : \"name\" } ] } ] }";
                    ApiUtil.setExampleResponse(request, "application/json", exampleString);
                    break;
                }
                if (mediaType.isCompatibleWith(MediaType.valueOf("application/xml"))) {
                    String exampleString = "<Order> <id>aeiou</id> <orderType>aeiou</orderType> <driverId>aeiou</driverId> <restaurantId>aeiou</restaurantId> <driverNote>aeiou</driverNote> <address>aeiou</address> <refunded>true</refunded> </Order>";
                    ApiUtil.setExampleResponse(request, "application/xml", exampleString);
                    break;
                }
            }
        });
        return new ResponseEntity<>(HttpStatus.NOT_IMPLEMENTED);

    }
}
