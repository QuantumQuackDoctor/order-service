package com.smoothstack.order.api;

import com.smoothstack.order.exception.*;
import com.smoothstack.order.model.CreateResponse;
import com.smoothstack.order.model.InlineObject;
import com.smoothstack.order.model.OrderAction;
import com.smoothstack.order.model.Order;
import io.swagger.annotations.*;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.context.request.NativeWebRequest;

import javax.validation.Valid;
import javax.validation.constraints.Min;
import java.util.List;
import java.util.Optional;

@javax.annotation.Generated(value = "org.openapitools.codegen.languages.SpringCodegen", date = "2021-06-30T22:53:09.076567700-06:00[America/Denver]")
@Validated
@Api(value = "order", description = "the order API")
@CrossOrigin
public interface OrderApi {

    default Optional<NativeWebRequest> getRequest() {
        return Optional.empty();
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
    @ApiOperation(value = "Create order", nickname = "createOrder", notes = "Create new order, sends back checkout session data. Payment intent will be canceled in 5 minutes if not paid. (Server note: use stripe webhooks to update payment status)",
            response = CreateResponse.class, tags = {"order",})
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "OK", response = CreateResponse.class),
            @ApiResponse(code = 401, message = "Access token is missing or invalid", response = String.class),
            @ApiResponse(code = 403, message = "Forbidden")})
    @PutMapping(
            path = "/order",
            produces = {"application/json", "application/xml"},
            consumes = {"application/json", "application/xml"}
    )
    default ResponseEntity<CreateResponse> createOrder(@ApiParam(value = "") @Valid @RequestBody(required = false) Order order,
                                                       Authentication authentication) throws EmptyCartException, MissingFieldsException, OrderTimeException, UserNotFoundException {
        getRequest().ifPresent(request -> {
            for (MediaType mediaType : MediaType.parseMediaTypes(request.getHeader("Accept"))) {
                if (mediaType.isCompatibleWith(MediaType.valueOf("application/json"))) {
                    String exampleString = "{ \"id\" : \"id\", \"type\" : \"stripe\" }";
                    ApiUtil.setExampleResponse(request, "application/json", exampleString);
                    break;
                }
                if (mediaType.isCompatibleWith(MediaType.valueOf("application/xml"))) {
                    String exampleString = "<null> <type>aeiou</type> <id>aeiou</id> </null>";
                    ApiUtil.setExampleResponse(request, "application/xml", exampleString);
                    break;
                }
            }
        });
        return new ResponseEntity<>(HttpStatus.NOT_IMPLEMENTED);

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
    @ApiOperation(value = "Delete order", nickname = "deleteOrder", notes = "Delete order by id",
            response = CreateResponse.class, tags = {"order",})
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "OK", response = CreateResponse.class),
            @ApiResponse(code = 401, message = "Access token is missing or invalid", response = String.class),
            @ApiResponse(code = 403, message = "Forbidden")})
    @DeleteMapping(
            path = "/order",
            produces = {"application/json", "application/xml"}
    )
    default ResponseEntity<Void> deleteOrder(@ApiParam(value = "id") @Valid @RequestParam(value = "id", required = true) Long id) throws ValueNotPresentException {
        return new ResponseEntity<>(HttpStatus.NOT_IMPLEMENTED);

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
    @ApiOperation(value = "Delete order", nickname = "deleteOrder", notes = "Delete order by id",
            response = CreateResponse.class, tags = {"order",})
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "OK", response = CreateResponse.class),
            @ApiResponse(code = 401, message = "Access token is missing or invalid", response = String.class),
            @ApiResponse(code = 403, message = "Forbidden")})
    @DeleteMapping(
            path = "/order/user",
            produces = {"application/json", "application/xml"}
    )
    default ResponseEntity<String> cancelOrder(@ApiParam(value = "id") @Valid @RequestParam(value = "id", required = true)
                                                         Long orderId, Authentication authentication) throws ValueNotPresentException {
        return new ResponseEntity<>(HttpStatus.NOT_IMPLEMENTED);
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
    default ResponseEntity<Void> driverAcceptOrders(@ApiParam(value = "Array of order id's to accept") @Valid @RequestBody(required = false) List<String> requestBody) {
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
    default ResponseEntity<Void> driverRemoveOrder(@ApiParam(value = "orderId") @Valid @RequestBody(required = false) String body) {
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
    @ApiOperation(value = "Get an order", nickname = "getOrder", notes = "Returns authenticated users orders, server will check ensure deliveryslot is valid for all chosen restaurants", response = Order.class, responseContainer = "List", tags = {"order",})
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "OK", response = Order.class, responseContainer = "List"),
            @ApiResponse(code = 400, message = "Invalid deliverySlot"),
            @ApiResponse(code = 401, message = "Access token is missing or invalid", response = String.class),
            @ApiResponse(code = 403, message = "Forbidden"),
            @ApiResponse(code = 404, message = "restaurant/item not found")})
    @GetMapping(
            path = "/order",
            produces = {"application/json", "application/xml"}
    )
    default ResponseEntity<Order> getOrder(@ApiParam(value = "") @Valid @RequestParam(value = "id", required = true) Long id) throws ValueNotPresentException, ValueNotPresentException {
        getRequest().ifPresent(request -> {
            for (MediaType mediaType : MediaType.parseMediaTypes(request.getHeader("Accept"))) {
                if (mediaType.isCompatibleWith(MediaType.valueOf("application/json"))) {
                    String exampleString = "{ \"orderType\" : \"delivery\", \"driverNote\" : \"driverNote\", \"address\" : \"address\", \"orderTime\" : { \"driverAccept\" : \"2021-02-10T00:00:00.000Z\", \"orderPlaced\" : \"2021-02-10T00:00:00.000Z\", \"deliverySlot\" : \"2021-02-10T00:00:00.000Z\", \"restaurantStart\" : \"2021-02-10T00:00:00.000Z\", \"delivered\" : \"2021-02-10T00:00:00.000Z\", \"restaurantAccept\" : \"2021-02-10T00:00:00.000Z\", \"restaurantComplete\" : \"2021-02-10T00:00:00.000Z\" }, \"driverId\" : \"driverId\", \"price\" : { \"delivery\" : 6.027456183070403, \"tip\" : 1.4658129805029452, \"food\" : 0.8008281904610115 }, \"refunded\" : true, \"id\" : \"id\", \"restaurantId\" : \"restaurantId\", \"food\" : [ { \"restaurantId\" : \"restaurantId\", \"items\" : [ { \"configurations\" : [ \"configurations\", \"configurations\" ], \"name\" : \"name\" }, { \"configurations\" : [ \"configurations\", \"configurations\" ], \"name\" : \"name\" } ] }, { \"restaurantId\" : \"restaurantId\", \"items\" : [ { \"configurations\" : [ \"configurations\", \"configurations\" ], \"name\" : \"name\" }, { \"configurations\" : [ \"configurations\", \"configurations\" ], \"name\" : \"name\" } ] } ] }";
                    ApiUtil.setExampleResponse(request, "application/json", exampleString);
                    break;
                }
                if (mediaType.isCompatibleWith(MediaType.valueOf("application/xml"))) {
                    String exampleString = "<null> <id>aeiou</id> <orderType>aeiou</orderType> <driverId>aeiou</driverId> <restaurantId>aeiou</restaurantId> <driverNote>aeiou</driverNote> <address>aeiou</address> <refunded>true</refunded> </null>";
                    ApiUtil.setExampleResponse(request, "application/xml", exampleString);
                    break;
                }
            }
        });
        return new ResponseEntity<>(HttpStatus.NOT_IMPLEMENTED);

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
    @ApiOperation(value = "Get actiive orders", nickname = "getActiveOrders", notes = "A list of active orders", response = Order.class, responseContainer = "List", tags = {"order",})
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "OK", response = Order.class, responseContainer = "List"),
            @ApiResponse(code = 400, message = "Invalid deliverySlot"),
            @ApiResponse(code = 401, message = "Access token is missing or invalid", response = String.class),
            @ApiResponse(code = 403, message = "Forbidden"),
            @ApiResponse(code = 404, message = "restaurant/item not found")})
    @GetMapping(
            path = "/orders/driver",
            produces = { "application/json" }
    )
    default ResponseEntity<List<Order>> getActiveOrders(
            @RequestParam(value = "sort_type", required = false) @Valid @ApiParam("type of sort") String sortType,
            @RequestParam(value = "page", required = false) @Valid @ApiParam("page to return indexed by 0") @Min(0) Integer page,
            @RequestParam(value = "size", required = false) @Valid @ApiParam("items in page") @Min(1) Integer size) {
        getRequest().ifPresent(request -> {
            for (MediaType mediaType : MediaType.parseMediaTypes(request.getHeader("Accept"))) {
                if (mediaType.isCompatibleWith(MediaType.valueOf("application/json"))) {
                    String exampleString = "{ \"orderType\" : \"delivery\", \"driverNote\" : \"driverNote\", \"address\" : \"address\", \"orderTime\" : { \"driverAccept\" : \"2021-02-10T00:00:00.000Z\", \"orderPlaced\" : \"2021-02-10T00:00:00.000Z\", \"deliverySlot\" : \"2021-02-10T00:00:00.000Z\", \"restaurantStart\" : \"2021-02-10T00:00:00.000Z\", \"delivered\" : \"2021-02-10T00:00:00.000Z\", \"restaurantAccept\" : \"2021-02-10T00:00:00.000Z\", \"restaurantComplete\" : \"2021-02-10T00:00:00.000Z\" }, \"driverId\" : \"driverId\", \"price\" : { \"delivery\" : 6.027456183070403, \"tip\" : 1.4658129805029452, \"food\" : 0.8008281904610115 }, \"refunded\" : true, \"id\" : \"id\", \"restaurantId\" : \"restaurantId\", \"food\" : [ { \"restaurantId\" : \"restaurantId\", \"items\" : [ { \"configurations\" : [ \"configurations\", \"configurations\" ], \"name\" : \"name\" }, { \"configurations\" : [ \"configurations\", \"configurations\" ], \"name\" : \"name\" } ] }, { \"restaurantId\" : \"restaurantId\", \"items\" : [ { \"configurations\" : [ \"configurations\", \"configurations\" ], \"name\" : \"name\" }, { \"configurations\" : [ \"configurations\", \"configurations\" ], \"name\" : \"name\" } ] } ] }";
                    ApiUtil.setExampleResponse(request, "application/json", exampleString);
                    break;
                }
            }
        });
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
    default ResponseEntity<Void> updateOrderConfigurations(@ApiParam(value = "") @Valid @RequestBody(required = false) InlineObject inlineObject) {
        return new ResponseEntity<>(HttpStatus.NOT_IMPLEMENTED);

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
    @ApiOperation(value = "Admin Update Order", nickname = "patchOrders", notes = "Update order, use this to connect orders with drivers (set driverId)", authorizations = {

            @Authorization(value = "JWT")
    }, tags = {"order",})
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Update successful"),
            @ApiResponse(code = 401, message = "Access token is missing or invalid", response = String.class),
            @ApiResponse(code = 403, message = "Forbidden"),
            @ApiResponse(code = 404, message = "Not Found")})
    @PatchMapping(
            value = "/orders",
            produces = {"application/json"},
            consumes = {"application/json"}
    )
    default ResponseEntity<Void> patchOrders(@ApiParam(value = "Order to update, non null properties will be updated, id necessary")
                                                 @Valid @RequestBody(required = false) Order order) {
        return new ResponseEntity<>(HttpStatus.NOT_IMPLEMENTED);
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
    @ApiOperation(value = "User Update Order", nickname = "patchOrders", notes = "Users use this to update order details", authorizations = {

            @Authorization(value = "JWT")
    }, tags = {"order",})
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Update successful"),
            @ApiResponse(code = 401, message = "Access token is missing or invalid", response = String.class),
            @ApiResponse(code = 403, message = "Forbidden"),
            @ApiResponse(code = 404, message = "Not Found")})
    @PatchMapping(
            value = "/order/details",
            produces = {"application/json"},
            consumes = {"application/json"}
    )
    default ResponseEntity<String> patchOrder(@ApiParam(value = "Order to update, non null properties will be updated, id necessary")
                                             @Valid @RequestBody(required = false) Order order, Authentication authentication) throws ValueNotPresentException {
        return new ResponseEntity<>(HttpStatus.NOT_IMPLEMENTED);
    }

    @ApiOperation(value = "Driver updates order status", nickname = "patchOrderStatus", notes = "driver uses this to change order status", authorizations = {
            @Authorization(value = "JWT")
    }, tags = {"order",})
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Update successful"),
            @ApiResponse(code = 401, message = "Access token is missing or invalid", response = String.class),
            @ApiResponse(code = 403, message = "Forbidden"),
            @ApiResponse(code = 404, message = "Not Found")})
    @PatchMapping(
            value = "/orders/status",
            produces = {"application/json"},
            consumes = {"application/json"}
    )
    @PreAuthorize("hasAuthority('user')")
    default ResponseEntity<Void> patchOrderStatus(@ApiParam(value = "Order to update, non null properties will be updated, id necessary")
                                                  @Valid @RequestBody(required = false) Order order) {
        return new ResponseEntity<>(HttpStatus.NOT_IMPLEMENTED);
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
    @ApiOperation(value = "Driver pick Order", nickname = "patchOrders", notes = "Update order, assign driver to order", authorizations = {

            @Authorization(value = "JWT")
    }, tags={ "order", })
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Update successful"),
            @ApiResponse(code = 401, message = "Access token is missing or invalid", response = String.class),
            @ApiResponse(code = 403, message = "Forbidden"),
            @ApiResponse(code = 404, message = "Not Found") })
    @PatchMapping(
            value = "/orders/drivers",
            produces = { "application/json" },
            consumes = { "application/json" }
    )
    default ResponseEntity<Void> patchDriverOrders(@ApiParam(value = "Update order, assign driver to order"  )  @Valid @RequestParam(required = true) Long order, @Valid @RequestParam(required = true) Long driver, @Valid @RequestParam(required = true) Boolean assign) {
        return new ResponseEntity<>(HttpStatus.NOT_IMPLEMENTED);

    }

}
