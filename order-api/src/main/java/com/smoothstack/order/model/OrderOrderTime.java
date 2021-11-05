package com.smoothstack.order.model;

import java.util.Objects;
import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.*;

/**
 * OrderOrderTime
 */
@javax.annotation.Generated(value = "org.openapitools.codegen.languages.SpringCodegen", date = "2021-06-30T22:53:09.076567700-06:00[America/Denver]")
@Data
public class OrderOrderTime   {
  @JsonProperty("orderPlaced")
  @Pattern(regexp="\\b\\d{4}-\\d{2}-\\d{2}T\\d{2}:\\d{2}:\\d{2}.\\d{3}Z\\b")
  private String orderPlaced;

  @JsonProperty("restaurantAccept")
  @Pattern(regexp="\\b\\d{4}-\\d{2}-\\d{2}T\\d{2}:\\d{2}:\\d{2}.\\d{3}Z\\b")
  private String restaurantAccept;

  @JsonProperty("restaurantComplete")
  @Pattern(regexp="\\b\\d{4}-\\d{2}-\\d{2}T\\d{2}:\\d{2}:\\d{2}.\\d{3}Z\\b")
  private String restaurantComplete;

  @JsonProperty("driverAccept")
  @Pattern(regexp="\\b\\d{4}-\\d{2}-\\d{2}T\\d{2}:\\d{2}:\\d{2}.\\d{3}Z\\b")
  private String driverAccept;

  @JsonProperty("driverPickUp")
  private String driverPickUp;

  @JsonProperty("delivered")
  @Pattern(regexp="\\b\\d{4}-\\d{2}-\\d{2}T\\d{2}:\\d{2}:\\d{2}.\\d{3}Z\\b")
  private String delivered;

  @JsonProperty("deliverySlot")
  @Pattern(regexp="\\b\\d{4}-\\d{2}-\\d{2}T\\d{2}:\\d{2}:\\d{2}.\\d{3}Z\\b")
  private String deliverySlot;
}

