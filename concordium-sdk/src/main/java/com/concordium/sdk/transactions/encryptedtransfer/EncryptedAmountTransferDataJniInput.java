package com.concordium.sdk.transactions.encryptedtransfer;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class EncryptedAmountTransferDataJniInput {
    private final GlobalContext global;
    private final String receiverPublicKey;
    private final String senderSecretKey;
    private final String amountToSend;
    private final AggregatedDecryptedAmount inputAmount;
}
