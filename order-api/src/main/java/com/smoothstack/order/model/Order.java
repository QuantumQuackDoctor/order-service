package com.smoothstack.order.model;

import java.util.Objects;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import java.util.ArrayList;
import java.util.List;
import javax.validation.Valid;

/**
 * Order DTO
 */
@ApiModel(description = "Order DTO")
@javax.annotation.Generated(value = "org.openapitools.codegen.languages.SpringCodegen", date = "2021-06-30T22:53:09.076567700-06:00[America/Denver]")
public class Order   {
  @JsonProperty("id")
  private String id;

  /**
   * Gets or Sets orderType
   */
  public enum OrderTypeEnum {
    DELIVERY("delivery"),
    
    PICKUP("pickup");

    private String value;

    OrderTypeEnum(String value) {
      this.value = value;
    }

    @JsonValue
    public String getValue() {
      return value;
    }

    @Override
    public String toString() {
      return String.valueOf(value);
    }

    @JsonCreator
    public static OrderTypeEnum fromValue(String value) {
      for (OrderTypeEnum b : OrderTypeEnum.values()) {
        if (b.value.equals(value)) {
          return b;
        }
      }
      throw new IllegalArgumentException("Unexpected value '" + value + "'");
    }
  }

  @JsonProperty("orderType")
  private OrderTypeEnum orderType;

  @JsonProperty("driverId")
  private String driverId;

/*  @JsonProperty("restaurantId")
  private String restaurantId;*/

  @JsonProperty("driverNote")
  private String driverNote;

  @JsonProperty("address")
  private String address;

  @JsonProperty("orderTime")
  private OrderOrderTime orderTime;

  @JsonProperty("refunded")
  private Boolean refunded;

  @JsonProperty("price")
  private OrderPrice price;

  @JsonProperty("food")
  @Valid
  private List<OrderFood> food = null;

  public Order id(String id) {
    this.id = id;
    return this;
  }

  /**
   * Get id
   * @return id
  */
  @ApiModelProperty(value = "")


  public String getId() {
    return id;
  }

  public void setId(String id) {
    this.id = id;
  }

  public Order orderType(OrderTypeEnum orderType) {
    this.orderType = orderType;
    return this;
  }

  /**
   * Get orderType
   * @return orderType
  */
  @ApiModelProperty(value = "")

  public OrderTypeEnum getOrderType() {
    return orderType;
  }

  public void setOrderType(OrderTypeEnum orderType) {
    this.orderType = orderType;
  }

  public Order driverId(String driverId) {
    this.driverId = driverId;
    return this;
  }

  /**
   * Null if order is pickup
   * @return driverId
  */
  @ApiModelProperty(value = "Null if order is pickup")


  public String getDriverId() {
    return driverId;
  }

  public void setDriverId(String driverId) {
    this.driverId = driverId;
  }



  /**
   * Get restaurantId
   * @return restaurantId
  */
/*  @ApiModelProperty(value = "")

  public Order restaurantId(String restaurantId) {
    this.restaurantId = restaurantId;
    return this;
  }

  public String getRestaurantId() {
    return restaurantId;
  }

  public void setRestaurantId(String restaurantId) {
    this.restaurantId = restaurantId;
  }*/

  public Order driverNote(String driverNote) {
    this.driverNote = driverNote;
    return this;
  }

  /**
   * Get driverNote
   * @return driverNote
  */
  @ApiModelProperty(value = "")


  public String getDriverNote() {
    return driverNote;
  }

  public void setDriverNote(String driverNote) {
    this.driverNote = driverNote;
  }

  public Order address(String address) {
    this.address = address;
    return this;
  }

  /**
   * Server: convert this to lat long and store address and geolocation
   * @return address
  */
  @ApiModelProperty(value = "Server: convert this to lat long and store address and geolocation")


  public String getAddress() {
    return address;
  }

  public void setAddress(String address) {
    this.address = address;
  }

  public Order orderTime(OrderOrderTime orderTime) {
    this.orderTime = orderTime;
    return this;
  }

  /**
   * Get orderTime
   * @return orderTime
  */
  @ApiModelProperty(value = "")

  @Valid

  public OrderOrderTime getOrderTime() {
    return orderTime;
  }

  public void setOrderTime(OrderOrderTime orderTime) {
    this.orderTime = orderTime;
  }

  public Order refunded(Boolean refunded) {
    this.refunded = refunded;
    return this;
  }

  /**
   * Get refunded
   * @return refunded
  */
  @ApiModelProperty(value = "")


  public Boolean getRefunded() {
    return refunded;
  }

  public void setRefunded(Boolean refunded) {
    this.refunded = refunded;
  }

  public Order price(OrderPrice price) {
    this.price = price;
    return this;
  }

  /**
   * Get price
   * @return price
  */
  @ApiModelProperty(value = "")

  @Valid

  public OrderPrice getPrice() {
    return price;
  }

  public void setPrice(OrderPrice price) {
    this.price = price;
  }

  public Order food(List<OrderFood> food) {
    this.food = food;
    return this;
  }

  public Order addFoodItem(OrderFood foodItem) {
    if (this.food == null) {
      this.food = new ArrayList<>();
    }
    this.food.add(foodItem);
    return this;
  }

  /**
   * N
   * @return food
  */
  @ApiModelProperty(value = "N")

  @Valid

  public List<OrderFood> getFood() {
    return food;
  }

  public void setFood(List<OrderFood> food) {
    this.food = food;
  }


  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    Order order = (Order) o;
    return Objects.equals(this.id, order.id) &&
        Objects.equals(this.orderType, order.orderType) &&
        Objects.equals(this.driverId, order.driverId) &&
/*        Objects.equals(this.restaurantId, order.restaurantId) &&*/
        Objects.equals(this.driverNote, order.driverNote) &&
        Objects.equals(this.address, order.address) &&
        Objects.equals(this.orderTime, order.orderTime) &&
        Objects.equals(this.refunded, order.refunded) &&
        Objects.equals(this.price, order.price) &&
        Objects.equals(this.food, order.food);
  }

  @Override
  public int hashCode() {
    return Objects.hash(id, orderType, driverId /*restaurantId*/, driverNote, address, orderTime, refunded, price, food);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class Order {\n");
    
    sb.append("    id: ").append(toIndentedString(id)).append("\n");
    sb.append("    orderType: ").append(toIndentedString(orderType)).append("\n");
    sb.append("    driverId: ").append(toIndentedString(driverId)).append("\n");
/*    sb.append("    restaurantId: ").append(toIndentedString(restaurantId)).append("\n");*/
    sb.append("    driverNote: ").append(toIndentedString(driverNote)).append("\n");
    sb.append("    address: ").append(toIndentedString(address)).append("\n");
    sb.append("    orderTime: ").append(toIndentedString(orderTime)).append("\n");
    sb.append("    refunded: ").append(toIndentedString(refunded)).append("\n");
    sb.append("    price: ").append(toIndentedString(price)).append("\n");
    sb.append("    food: ").append(toIndentedString(food)).append("\n");
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

  public boolean checkRequiredFields (){
    if (this.orderType == null) return false;
    if (this.address == null) return false;
    if (this.orderTime == null) return false;
    if (this.refunded == null) return false;
    if (this.price == null) return false;
    if (this.food == null || this.food.size() == 0) return false;
    return true;
  }
}

