package com.smoothstack.order.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.annotations.ApiModelProperty;

import javax.validation.Valid;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * OrderFood
 */
@javax.annotation.Generated(value = "org.openapitools.codegen.languages.SpringCodegen", date = "2021-06-30T22:53:09.076567700-06:00[America/Denver]")
public class OrderFood   {
  @JsonProperty("restaurantId")
  private String restaurantId;

  @JsonProperty("restaurantName")
  private String restaurantName;

  @JsonProperty("items")
  @Valid
  private List<OrderItems> items = null;

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;
    OrderFood orderFood = (OrderFood) o;
    return restaurantId.equals(orderFood.restaurantId) && restaurantName.equals(orderFood.restaurantName);
  }

  @Override
  public int hashCode() {
    return Objects.hash(restaurantId, restaurantName);
  }

  @ApiModelProperty(value = "")

  public String getRestaurantName() {
    return restaurantName;
  }

  public OrderFood setRestaurantName(String restaurantName) {
    this.restaurantName = restaurantName;
    return this;
  }

  /**
   * Get restaurantId
   * @return restaurantId
  */
  @ApiModelProperty(value = "")


  public String getRestaurantId() {
    return restaurantId;
  }

  public void setRestaurantId(String restaurantId) {
    this.restaurantId = restaurantId;
  }

  public OrderFood items(List<OrderItems> items) {
    this.items = items;
    return this;
  }

  public OrderFood addItemsItem(OrderItems itemsItem) {
    if (this.items == null) {
      this.items = new ArrayList<>();
    }
    this.items.add(itemsItem);
    return this;
  }

  /**
   * Get items
   * @return items
  */
  @ApiModelProperty(value = "")

  @Valid

  public List<OrderItems> getItems() {
    return items;
  }

  public void setItems(List<OrderItems> items) {
    this.items = items;
  }


  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class OrderFood {\n");
    
    sb.append("    restaurantId: ").append(toIndentedString(restaurantId)).append("\n");
    sb.append("    items: ").append(toIndentedString(items)).append("\n");
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

