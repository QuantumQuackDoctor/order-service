package com.smoothstack.order.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.annotations.ApiModel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@ApiModel(description = "ChargeRequest")
@Getter
@Setter
@Builder
@AllArgsConstructor
public class ChargeRequest {

    @JsonProperty ("tokenId")
    private String tokenId;

    @JsonProperty ("chargePrice")
    private Long chargePrice;

}
