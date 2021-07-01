package com.smoothstack.order.model;

import java.util.Objects;
import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.annotations.ApiModelProperty;
import java.math.BigDecimal;
import javax.validation.Valid;

/**
 * OrderPrice
 */
@javax.annotation.Generated(value = "org.openapitools.codegen.languages.SpringCodegen", date = "2021-06-30T22:53:09.076567700-06:00[America/Denver]")
public class OrderPrice   {
  @JsonProperty("food")
  private BigDecimal food;

  @JsonProperty("delivery")
  private BigDecimal delivery;

  @JsonProperty("tip")
  private BigDecimal tip;

  public OrderPrice food(BigDecimal food) {
    this.food = food;
    return this;
  }

  /**
   * Get food
   * @return food
  */
  @ApiModelProperty(value = "")

  @Valid

  public BigDecimal getFood() {
    return food;
  }

  public void setFood(BigDecimal food) {
    this.food = food;
  }

  public OrderPrice delivery(BigDecimal delivery) {
    this.delivery = delivery;
    return this;
  }

  /**
   * Get delivery
   * @return delivery
  */
  @ApiModelProperty(value = "")

  @Valid

  public BigDecimal getDelivery() {
    return delivery;
  }

  public void setDelivery(BigDecimal delivery) {
    this.delivery = delivery;
  }

  public OrderPrice tip(BigDecimal tip) {
    this.tip = tip;
    return this;
  }

  /**
   * Get tip
   * @return tip
  */
  @ApiModelProperty(value = "")

  @Valid

  public BigDecimal getTip() {
    return tip;
  }

  public void setTip(BigDecimal tip) {
    this.tip = tip;
  }


  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    OrderPrice orderPrice = (OrderPrice) o;
    return Objects.equals(this.food, orderPrice.food) &&
        Objects.equals(this.delivery, orderPrice.delivery) &&
        Objects.equals(this.tip, orderPrice.tip);
  }

  @Override
  public int hashCode() {
    return Objects.hash(food, delivery, tip);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class OrderPrice {\n");
    
    sb.append("    food: ").append(toIndentedString(food)).append("\n");
    sb.append("    delivery: ").append(toIndentedString(delivery)).append("\n");
    sb.append("    tip: ").append(toIndentedString(tip)).append("\n");
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

