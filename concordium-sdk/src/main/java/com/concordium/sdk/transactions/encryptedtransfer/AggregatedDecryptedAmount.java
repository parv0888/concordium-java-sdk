package com.concordium.sdk.transactions.encryptedtransfer;

import lombok.Builder;
import lombok.Data;
import lombok.extern.jackson.Jacksonized;

@Jacksonized
@Builder
@Data
public class AggregatedDecryptedAmount {
    private final String encryptedChunks;
    private final long index;
}
