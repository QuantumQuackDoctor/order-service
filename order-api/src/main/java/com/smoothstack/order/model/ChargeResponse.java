package com.smoothstack.order.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.annotations.ApiModel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@Builder
@ApiModel(description = "ChargeResponse")
public class ChargeResponse {

    @JsonProperty ("charge")
    private String charge;

    @JsonProperty ("error")
    private String error;

}
