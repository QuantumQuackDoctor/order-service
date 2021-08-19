package com.smoothstack.order.model;

import java.util.Objects;
import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.annotations.ApiModelProperty;
import java.util.ArrayList;
import java.util.List;
import javax.validation.Valid;

/**
 * OrderItems
 */
@javax.annotation.Generated(value = "org.openapitools.codegen.languages.SpringCodegen", date = "2021-06-30T22:53:09.076567700-06:00[America/Denver]")
public class OrderItems   {

  @JsonProperty("id")
  private String id;

  @JsonProperty("name")
  private String name;

  @JsonProperty("configurations")
  @Valid
  private List<String> configurations = null;

  public OrderItems name(String name) {
    this.name = name;
    return this;
  }

  public String getId() {
    return id;
  }

  public OrderItems setId(String id) {
    this.id = id;
    return this;
  }

  /**
   * Get name
   * @return name
  */
  @ApiModelProperty(value = "")



  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public OrderItems configurations(List<String> configurations) {
    this.configurations = configurations;
    return this;
  }

  public OrderItems addConfigurationsItem(String configurationsItem) {
    if (this.configurations == null) {
      this.configurations = new ArrayList<>();
    }
    this.configurations.add(configurationsItem);
    return this;
  }

  /**
   * all present strings will be applied
   * @return configurations
  */
  @ApiModelProperty(value = "all present strings will be applied")


  public List<String> getConfigurations() {
    return configurations;
  }

  public void setConfigurations(List<String> configurations) {
    this.configurations = configurations;
  }


  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    OrderItems orderItems = (OrderItems) o;
    return Objects.equals(this.name, orderItems.name) &&
        Objects.equals(this.configurations, orderItems.configurations);
  }

  @Override
  public int hashCode() {
    return Objects.hash(name, configurations);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class OrderItems {\n");
    
    sb.append("    name: ").append(toIndentedString(name)).append("\n");
    sb.append("    configurations: ").append(toIndentedString(configurations)).append("\n");
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

