package com.concordium.sdk.responses.transactionstatus;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.ToString;

@Getter
@ToString
public final class ModuleCreatedResult extends TransactionResultEvent {
    private final String contents;

    @JsonCreator
    public ModuleCreatedResult(@JsonProperty("contents") String contents) {
        this.contents = contents;
    }

    @Override
    public TransactionResultEventType getType() {
        return TransactionResultEventType.MODULE_DEPLOYED;
    }
}
