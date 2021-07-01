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

  @JsonProperty("restaurantStart")
  private String restaurantStart;

  @JsonProperty("restaurantComplete")
  private String restaurantComplete;

  @JsonProperty("driverAccept")
  private String driverAccept;

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

  public OrderOrderTime restaurantStart(String restaurantStart) {
    this.restaurantStart = restaurantStart;
    return this;
  }

  /**
   * Get restaurantStart
   * @return restaurantStart
  */
  @ApiModelProperty(example = "2021-02-10T00:00:00.000Z", value = "")

@Pattern(regexp="\\b\\d{4}-\\d{2}-\\d{2}T\\d{2}:\\d{2}:\\d{2}.\\d{3}Z\\b") 
  public String getRestaurantStart() {
    return restaurantStart;
  }

  public void setRestaurantStart(String restaurantStart) {
    this.restaurantStart = restaurantStart;
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


  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    OrderOrderTime orderOrderTime = (OrderOrderTime) o;
    return Objects.equals(this.orderPlaced, orderOrderTime.orderPlaced) &&
        Objects.equals(this.restaurantAccept, orderOrderTime.restaurantAccept) &&
        Objects.equals(this.restaurantStart, orderOrderTime.restaurantStart) &&
        Objects.equals(this.restaurantComplete, orderOrderTime.restaurantComplete) &&
        Objects.equals(this.driverAccept, orderOrderTime.driverAccept) &&
        Objects.equals(this.delivered, orderOrderTime.delivered) &&
        Objects.equals(this.deliverySlot, orderOrderTime.deliverySlot);
  }

  @Override
  public int hashCode() {
    return Objects.hash(orderPlaced, restaurantAccept, restaurantStart, restaurantComplete, driverAccept, delivered, deliverySlot);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class OrderOrderTime {\n");
    
    sb.append("    orderPlaced: ").append(toIndentedString(orderPlaced)).append("\n");
    sb.append("    restaurantAccept: ").append(toIndentedString(restaurantAccept)).append("\n");
    sb.append("    restaurantStart: ").append(toIndentedString(restaurantStart)).append("\n");
    sb.append("    restaurantComplete: ").append(toIndentedString(restaurantComplete)).append("\n");
    sb.append("    driverAccept: ").append(toIndentedString(driverAccept)).append("\n");
    sb.append("    delivered: ").append(toIndentedString(delivered)).append("\n");
    sb.append("    deliverySlot: ").append(toIndentedString(deliverySlot)).append("\n");
    sb.append("}");
    return sb.toString();
  }

  /**
   * Convert the given object to string with each line indented by 4 spaces
   * (except the first line).
   */
  private String toIndentedString(Object o) {
    if (o == null) {
      return "null";
    }
    return o.toString().replace("\n", "\n    ");
  }
}

