package com.smoothstack.order.model;

import java.util.Objects;
import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.annotations.ApiModelProperty;
import java.util.ArrayList;
import java.util.List;
import javax.validation.Valid;

/**
 * InlineObject
 */
@javax.annotation.Generated(value = "org.openapitools.codegen.languages.SpringCodegen", date = "2021-06-30T22:53:09.076567700-06:00[America/Denver]")
public class InlineObject   {
  @JsonProperty("restaurantId")
  private String restaurantId;

  @JsonProperty("itemName")
  private String itemName;

  @JsonProperty("configurations")
  @Valid
  private List<String> configurations = null;

  @JsonProperty("orderId")
  private String orderId;

  public InlineObject restaurantId(String restaurantId) {
    this.restaurantId = restaurantId;
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

  public InlineObject itemName(String itemName) {
    this.itemName = itemName;
    return this;
  }

  /**
   * Get itemName
   * @return itemName
  */
  @ApiModelProperty(value = "")


  public String getItemName() {
    return itemName;
  }

  public void setItemName(String itemName) {
    this.itemName = itemName;
  }

  public InlineObject configurations(List<String> configurations) {
    this.configurations = configurations;
    return this;
  }

  public InlineObject addConfigurationsItem(String configurationsItem) {
    if (this.configurations == null) {
      this.configurations = new ArrayList<>();
    }
    this.configurations.add(configurationsItem);
    return this;
  }

  /**
   * adds configuration if not present, removes if present
   * @return configurations
  */
  @ApiModelProperty(value = "adds configuration if not present, removes if present")


  public List<String> getConfigurations() {
    return configurations;
  }

  public void setConfigurations(List<String> configurations) {
    this.configurations = configurations;
  }

  public InlineObject orderId(String orderId) {
    this.orderId = orderId;
    return this;
  }

  /**
   * order to update
   * @return orderId
  */
  @ApiModelProperty(value = "order to update")


  public String getOrderId() {
    return orderId;
  }

  public void setOrderId(String orderId) {
    this.orderId = orderId;
  }


  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    InlineObject inlineObject = (InlineObject) o;
    return Objects.equals(this.restaurantId, inlineObject.restaurantId) &&
        Objects.equals(this.itemName, inlineObject.itemName) &&
        Objects.equals(this.configurations, inlineObject.configurations) &&
        Objects.equals(this.orderId, inlineObject.orderId);
  }

  @Override
  public int hashCode() {
    return Objects.hash(restaurantId, itemName, configurations, orderId);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class InlineObject {\n");
    
    sb.append("    restaurantId: ").append(toIndentedString(restaurantId)).append("\n");
    sb.append("    itemName: ").append(toIndentedString(itemName)).append("\n");
    sb.append("    configurations: ").append(toIndentedString(configurations)).append("\n");
    sb.append("    orderId: ").append(toIndentedString(orderId)).append("\n");
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

