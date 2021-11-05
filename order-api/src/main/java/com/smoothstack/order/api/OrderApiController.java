package com.smoothstack.order.api;

import com.database.security.AuthDetails;
import com.smoothstack.order.exception.*;
import com.smoothstack.order.model.ChargeRequest;
import com.smoothstack.order.model.ChargeResponse;
import com.smoothstack.order.model.CreateResponse;
import com.smoothstack.order.model.Order;
import com.smoothstack.order.service.OrderService;
import io.swagger.annotations.ApiParam;
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
    @PreAuthorize("hasAuthority ('user')")
    public ResponseEntity<String> cancelOrder (Long orderId, Authentication authentication) throws ValueNotPresentException {
        log.info ("cancelOrder called");
        AuthDetails authDetails = (AuthDetails) authentication.getPrincipal();
        return orderService.cancelOrder (orderId, authDetails.getId());
    }

    @Override
    @PreAuthorize("hasAuthority('user')")
    public ResponseEntity<Void> patchOrderStatus (@Valid Order orderDTO){
        log.info ("patchOrderStatus called.");
        return ResponseEntity.ok (orderService.patchOrderStatus (orderDTO));
    }

    @Override
    @PreAuthorize("hasAuthority('admin')")
    public ResponseEntity<Order> getOrder(@ApiParam(value = "") @Valid @RequestParam(value = "id", required = false) Long id) throws ValueNotPresentException {
        log.info ("getOrder called.");
        return orderService.getOrder(id);
    }

    @Override
    public ResponseEntity<Void> patchOrders(Order order) {
        log.info ("patchOrders called");
        return orderService.patchOrders(order);
    }

    @Override
    public ResponseEntity<String> patchOrder(Order order, Authentication authentication) throws ValueNotPresentException {
        log.info ("patchOrders called");
        AuthDetails authDetails = (AuthDetails) authentication.getPrincipal();
        return orderService.patchOrder(order, authDetails.getId());
    }

    @Override
    @PreAuthorize("hasAuthority ('user')")
    public ResponseEntity<List<Order>> getActiveOrders(String sortType, Integer page, Integer size) {
        log.info ("getActiveOrders called");
        return orderService.getActiveOrders(sortType, page, size);
    }

    @Override
    @PreAuthorize("hasAuthority('user')")
    public ResponseEntity<CreateResponse> createOrder(@Valid Order order, Authentication authentication, String chargeId)
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

    @Override
    public ResponseEntity<Void> deleteOrder(Long id) throws ValueNotPresentException {
        log.info ("deleteOrder called");
        orderService.deleteOrder(id);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @PreAuthorize("hasAuthority ('user')")
    @PostMapping ("/order/charge")
    public ResponseEntity<ChargeResponse> createPaymentIntent (@RequestBody ChargeRequest chargeRequest) {
        log.info ("createStripeCharge called");
        return ResponseEntity.ok().body(orderService.createStripeCharge(chargeRequest));
    }

    @PreAuthorize("hasAuthority(('user'))")
    @PostMapping ("/order/email-order")
    public ResponseEntity<Order> sendOrderConfirmation (@Valid @RequestBody CreateResponse createResponse, Authentication authentication) throws UserNotFoundException {
        AuthDetails authDetails = (AuthDetails) authentication.getPrincipal();
        log.info ("sendOrderConfirmation called");
        return ResponseEntity.ok().body(this.orderService.sendOrderConfirmation(Long.parseLong(createResponse.getId()), authDetails.getId()));
    }
}



