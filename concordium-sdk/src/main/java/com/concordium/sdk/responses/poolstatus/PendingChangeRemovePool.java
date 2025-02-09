package com.concordium.sdk.responses.poolstatus;

import lombok.Builder;
import lombok.Data;
import lombok.extern.jackson.Jacksonized;

import java.util.Date;

@Data
@Jacksonized
@Builder
public class PendingChangeRemovePool extends PendingChange {

    /**
     * Effective time of the change.
     */
    private final Date effectiveTime;
}
