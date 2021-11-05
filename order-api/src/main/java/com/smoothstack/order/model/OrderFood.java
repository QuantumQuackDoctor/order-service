package com.smoothstack.order.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.Valid;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * OrderFood
 */
@javax.annotation.Generated(value = "org.openapitools.codegen.languages.SpringCodegen", date = "2021-06-30T22:53:09.076567700-06:00[America/Denver]")
@Data
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

  public OrderFood addItemsItem(OrderItems itemsItem) {
    if (this.items == null) {
      this.items = new ArrayList<>();
    }
    this.items.add(itemsItem);
    return this;
  }
}

