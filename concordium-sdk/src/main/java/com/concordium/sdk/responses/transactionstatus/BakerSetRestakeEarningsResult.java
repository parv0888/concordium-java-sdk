package com.concordium.sdk.responses.transactionstatus;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.ToString;

@Getter
@ToString
public final class BakerSetRestakeEarningsResult extends AbstractBakerResult {
    private final boolean restakeEarnings;

    @JsonCreator
    BakerSetRestakeEarningsResult(@JsonProperty("bakerId") String bakerId,
                                  @JsonProperty("account") String account,
                                  @JsonProperty("restakeEarnings") boolean restakeEarnings) {
        super(bakerId, account);
        this.restakeEarnings = restakeEarnings;
    }
}