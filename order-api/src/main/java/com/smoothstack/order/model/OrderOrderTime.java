package com.smoothstack.order.model;

import java.util.Objects;
import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.annotations.ApiModelProperty;

import javax.validation.constraints.*;

/**
 * OrderOrderTime
 */
@javax.annotation.Generated(value = "org.openapitools.codegen.languages.SpringCodegen", date = "2021-06-30T22:53:09.076567700-06:00[America/Denver]")
public class OrderOrderTime   {
  @JsonProperty("orderPlaced")
  private String orderPlaced;

  @JsonProperty("restaurantAccept")
  private String restaurantAccept;

  @JsonProperty("restaurantComplete")
  private String restaurantComplete;

  @JsonProperty("driverAccept")
  private String driverAccept;

  @JsonProperty("driverPickUp")
  private String driverPickUp;

  @JsonProperty("delivered")
  private String delivered;

  @JsonProperty("deliverySlot")
  private String deliverySlot;

  public OrderOrderTime orderPlaced(String orderPlaced) {
    this.orderPlaced = orderPlaced;
    return this;
  }

  /**
   * Get orderPlaced
   * @return orderPlaced
  */
  @ApiModelProperty(example = "2021-02-10T00:00:00.000Z", value = "")

@Pattern(regexp="\\b\\d{4}-\\d{2}-\\d{2}T\\d{2}:\\d{2}:\\d{2}.\\d{3}Z\\b") 
  public String getOrderPlaced() {
    return orderPlaced;
  }

  public void setOrderPlaced(String orderPlaced) {
    this.orderPlaced = orderPlaced;
  }

  public OrderOrderTime restaurantAccept(String restaurantAccept) {
    this.restaurantAccept = restaurantAccept;
    return this;
  }

  /**
   * Get restaurantAccept
   * @return restaurantAccept
  */
  @ApiModelProperty(example = "2021-02-10T00:00:00.000Z", value = "")

@Pattern(regexp="\\b\\d{4}-\\d{2}-\\d{2}T\\d{2}:\\d{2}:\\d{2}.\\d{3}Z\\b") 
  public String getRestaurantAccept() {
    return restaurantAccept;
  }

  public void setRestaurantAccept(String restaurantAccept) {
    this.restaurantAccept = restaurantAccept;
  }


  public OrderOrderTime restaurantComplete(String restaurantComplete) {
    this.restaurantComplete = restaurantComplete;
    return this;
  }

  /**
   * Get restaurantComplete
   * @return restaurantComplete
  */
  @ApiModelProperty(example = "2021-02-10T00:00:00.000Z", value = "")

@Pattern(regexp="\\b\\d{4}-\\d{2}-\\d{2}T\\d{2}:\\d{2}:\\d{2}.\\d{3}Z\\b") 
  public String getRestaurantComplete() {
    return restaurantComplete;
  }

  public void setRestaurantComplete(String restaurantComplete) {
    this.restaurantComplete = restaurantComplete;
  }

  public OrderOrderTime driverAccept(String driverAccept) {
    this.driverAccept = driverAccept;
    return this;
  }

  /**
   * Get driverAccept
   * @return driverAccept
  */
  @ApiModelProperty(example = "2021-02-10T00:00:00.000Z", value = "")

@Pattern(regexp="\\b\\d{4}-\\d{2}-\\d{2}T\\d{2}:\\d{2}:\\d{2}.\\d{3}Z\\b") 
  public String getDriverAccept() {
    return driverAccept;
  }

  public void setDriverAccept(String driverAccept) {
    this.driverAccept = driverAccept;
  }

  public OrderOrderTime delivered(String delivered) {
    this.delivered = delivered;
    return this;
  }

  /**
   * Get delivered
   * @return delivered
  */
  @ApiModelProperty(example = "2021-02-10T00:00:00.000Z", value = "")

@Pattern(regexp="\\b\\d{4}-\\d{2}-\\d{2}T\\d{2}:\\d{2}:\\d{2}.\\d{3}Z\\b") 
  public String getDelivered() {
    return delivered;
  }

  public void setDelivered(String delivered) {
    this.delivered = delivered;
  }

  public OrderOrderTime deliverySlot(String deliverySlot) {
    this.deliverySlot = deliverySlot;
    return this;
  }

  /**
   * Get deliverySlot
   * @return deliverySlot
  */
  @ApiModelProperty(example = "2021-02-10T00:00:00.000Z", value = "")

@Pattern(regexp="\\b\\d{4}-\\d{2}-\\d{2}T\\d{2}:\\d{2}:\\d{2}.\\d{3}Z\\b") 
  public String getDeliverySlot() {
    return deliverySlot;
  }

  public void setDeliverySlot(String deliverySlot) {
    this.deliverySlot = deliverySlot;
  }

  public String getDriverPickUp() {
    return driverPickUp;
  }

  public OrderOrderTime setDriverPickUp(String driverPickUp) {
    this.driverPickUp = driverPickUp;
    return this;
  }
}

